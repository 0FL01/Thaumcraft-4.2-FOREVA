package thaumcraft.common.lib.world.dim;

import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

public class TeleporterThaumcraft extends Teleporter {

    private static final int SEARCH_RADIUS = 128;

    public TeleporterThaumcraft(WorldServer ws) {
        super(ws);
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        this.prepareDestination(entity, 1);
        if (!this.placeInExistingPortal(entity, rotationYaw)) {
            this.prepareDestination(entity, 2);
            if (!this.placeInExistingPortal(entity, rotationYaw)) {
                this.placeAtFallback(entity, rotationYaw);
            }
        }
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
        BlockPos portal = this.findPortal(entity);
        if (portal == null) {
            return false;
        }

        return this.placeAtPortalApproach(entity, portal, rotationYaw);
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }

    private BlockPos findPortal(Entity entity) {
        int entityX = MathHelper.floor(entity.posX);
        int entityZ = MathHelper.floor(entity.posZ);
        long cacheKey = ChunkPos.asLong(entityX >> 4, entityZ >> 4);
        PortalPosition cached = this.destinationCoordinateCache.get(cacheKey);
        if (cached != null) {
            cached.lastUpdateTime = this.world.getTotalWorldTime();
            return cached;
        }

        double closestDistance = -1.0D;
        BlockPos closest = null;
        for (int x = entityX - SEARCH_RADIUS; x <= entityX + SEARCH_RADIUS; x++) {
            double dx = x + 0.5D - entity.posX;
            for (int z = entityZ - SEARCH_RADIUS; z <= entityZ + SEARCH_RADIUS; z++) {
                double dz = z + 0.5D - entity.posZ;
                for (int y = this.world.getActualHeight() - 1; y >= 0; y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (this.world.getBlockState(pos).getBlock() != ConfigBlocks.blockEldritchPortal) {
                        continue;
                    }
                    double dy = y + 0.5D - entity.posY;
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (closestDistance < 0.0D || distance < closestDistance) {
                        closestDistance = distance;
                        closest = pos;
                    }
                }
            }
        }

        if (closest != null) {
            this.destinationCoordinateCache.put(cacheKey, new PortalPosition(closest, this.world.getTotalWorldTime()));
        }
        return closest;
    }

    private void prepareDestination(Entity entity, int chunkRadius) {
        int chunkX = MathHelper.floor(entity.posX) >> 4;
        int chunkZ = MathHelper.floor(entity.posZ) >> 4;
        for (int x = chunkX - chunkRadius; x <= chunkX + chunkRadius; x++) {
            for (int z = chunkZ - chunkRadius; z <= chunkZ + chunkRadius; z++) {
                this.world.getChunkProvider().provideChunk(x, z);
            }
        }
    }

    private boolean placeAtPortalApproach(Entity entity, BlockPos portal, float rotationYaw) {
        int[][] offsets = {{-5, 0}, {5, 0}, {0, -5}, {0, 5}};
        for (int[] offset : offsets) {
            BlockPos candidate = portal.add(offset[0], 0, offset[1]);
            if (this.isSafeArrival(candidate)) {
                float yaw = (float) Math.toDegrees(Math.atan2(-offset[0], offset[1]));
                this.setEntityLocation(entity, candidate.getX() + 0.5D, candidate.getY(),
                        candidate.getZ() + 0.5D, yaw);
                return true;
            }
        }

        for (int radius = 2; radius <= 6; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) != radius && Math.abs(z) != radius) continue;
                    BlockPos candidate = portal.add(x, 0, z);
                    if (this.isSafeArrival(candidate)) {
                        this.setEntityLocation(entity, candidate.getX() + 0.5D, candidate.getY(),
                                candidate.getZ() + 0.5D, rotationYaw);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSafeArrival(BlockPos pos) {
        IBlockState floor = this.world.getBlockState(pos.down());
        return floor.getMaterial().blocksMovement()
                && this.world.isAirBlock(pos)
                && this.world.isAirBlock(pos.up());
    }

    private void placeAtFallback(Entity entity, float rotationYaw) {
        if (this.world.provider.getDimension() == Config.dimensionOuterId) {
            BlockPos interior = this.findSafeInterior(entity);
            if (interior != null) {
                this.setEntityLocation(entity, interior.getX() + 0.5D, interior.getY(),
                        interior.getZ() + 0.5D, rotationYaw);
                return;
            }
            int x = MathHelper.floor(entity.posX);
            int z = MathHelper.floor(entity.posZ);
            this.setEntityLocation(entity, x + 0.5D, 53.0D, z + 0.5D, rotationYaw);
            return;
        }

        int x = MathHelper.floor(entity.posX);
        int z = MathHelper.floor(entity.posZ);
        BlockPos top = this.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).up();
        this.setEntityLocation(entity, top.getX() + 0.5D, top.getY(), top.getZ() + 0.5D, rotationYaw);
    }

    private BlockPos findSafeInterior(Entity entity) {
        int originX = MathHelper.floor(entity.posX);
        int originZ = MathHelper.floor(entity.posZ);
        for (int radius = 0; radius <= 32; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (radius > 0 && Math.abs(x) != radius && Math.abs(z) != radius) continue;
                    for (int y = 52; y <= 61; y++) {
                        BlockPos candidate = new BlockPos(originX + x, y, originZ + z);
                        if (this.isSafeArrival(candidate) && hasCeiling(this.world, candidate, 16)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean hasCeiling(World world, BlockPos pos, int distance) {
        for (int y = 2; y <= distance; y++) {
            if (world.getBlockState(pos.up(y)).getMaterial().blocksMovement()) {
                return true;
            }
        }
        return false;
    }

    private void setEntityLocation(Entity entity, double x, double y, double z, float rotationYaw) {
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.setLocationAndAngles(x, y, z, rotationYaw, entity.rotationPitch);
    }
}
