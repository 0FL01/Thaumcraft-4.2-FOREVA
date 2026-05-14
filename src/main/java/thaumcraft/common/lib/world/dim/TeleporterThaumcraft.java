package thaumcraft.common.lib.world.dim;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import thaumcraft.common.config.ConfigBlocks;

public class TeleporterThaumcraft extends Teleporter {

    private static final int SEARCH_RADIUS = 128;

    public TeleporterThaumcraft(WorldServer ws) {
        super(ws);
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        if (!this.placeInExistingPortal(entity, rotationYaw)) {
            this.placeAtSafeTop(entity, rotationYaw);
        }
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
        BlockPos portal = this.findPortal(entity);
        if (portal == null) {
            return false;
        }

        int offsetX = this.world.rand.nextBoolean() ? 1 : -1;
        int offsetZ = this.world.rand.nextBoolean() ? 1 : -1;
        this.setEntityLocation(entity,
                portal.getX() + 0.5D + offsetX,
                portal.getY(),
                portal.getZ() + 0.5D + offsetZ,
                rotationYaw);
        return true;
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

    private void placeAtSafeTop(Entity entity, float rotationYaw) {
        int x = MathHelper.floor(entity.posX);
        int z = MathHelper.floor(entity.posZ);
        BlockPos top = this.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).up();
        this.setEntityLocation(entity, top.getX() + 0.5D, top.getY(), top.getZ() + 0.5D, rotationYaw);
    }

    private void setEntityLocation(Entity entity, double x, double y, double z, float rotationYaw) {
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.setLocationAndAngles(x, y, z, rotationYaw, entity.rotationPitch);
    }
}
