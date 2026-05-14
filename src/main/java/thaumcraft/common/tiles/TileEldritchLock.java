package thaumcraft.common.tiles;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.world.dim.Cell;
import thaumcraft.common.lib.world.dim.CellLoc;
import thaumcraft.common.lib.world.dim.MapBossData;
import thaumcraft.common.lib.world.dim.MazeHandler;

public class TileEldritchLock extends TileThaumcraft implements ITickable {
    private static final int BOSS_ROOM_Y = 50;
    public int count = -1;
    private byte facing = 0;

    @Override
    public void update() {
        if (this.world == null || this.count == -1) return;
        ++this.count;
        if (this.count % 5 == 0) {
            this.world.playSound(null, this.pos, TCSounds.PUMP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        if (this.count > 100) {
            this.doBossSpawn();
        }
    }

    private void doBossSpawn() {
        this.world.playSound(null, this.pos, TCSounds.ICE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (this.world.isRemote) return;

        BossRoom room = findBossRoom();
        MapBossData data = (MapBossData) this.world.loadData(MapBossData.class, "BossMapData");
        if (data == null) {
            data = new MapBossData("BossMapData");
            data.bossCount = 0;
            data.markDirty();
            this.world.setData("BossMapData", data);
        }
        ++data.bossCount;
        if (this.world.rand.nextFloat() < 0.25F) {
            ++data.bossCount;
        }
        data.markDirty();

        switch (data.bossCount % 4) {
            case 0:
                spawnEldritchGolem(room);
                break;
            case 1:
                spawnEldritchWarden(room);
                break;
            case 2:
                spawnCultistPortal(room);
                break;
            default:
                spawnTaintBoss(room);
                break;
        }

        clearNearbyAiryBlocks();
        this.world.setBlockToAir(this.pos);
    }

    private BossRoom findBossRoom() {
        int cx = this.pos.getX() >> 4;
        int cz = this.pos.getZ() >> 4;
        BossRoom room = new BossRoom(cx, cz, (byte) 0);
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                Cell cell = MazeHandler.getFromHashMap(new CellLoc(cx + x, cz + z));
                if (cell == null) continue;
                if (cell.feature == 2) {
                    room.centerX = cx + x;
                    room.centerZ = cz + z;
                }
                if (cell.feature >= 2 && cell.feature <= 5
                        && (cell.north || cell.south || cell.east || cell.west)) {
                    room.exit = cell.feature;
                }
            }
        }
        return room;
    }

    private void spawnEldritchWarden(BossRoom room) {
        notifyNearbyPlayers("tc.boss.warden");
        BlockPos spawn = room.getOffsetSpawnPos();
        EntityEldritchWarden boss = new EntityEldritchWarden(this.world);
        placeBoss(boss, spawn, room.getCenterHomePos());
        boss.onInitialSpawn(this.world.getDifficultyForLocation(spawn), null);
        this.world.spawnEntity(boss);
    }

    private void spawnEldritchGolem(BossRoom room) {
        notifyNearbyPlayers("tc.boss.golem");
        BlockPos spawn = room.getCenterSpawnPos();
        EntityEldritchGolem boss = new EntityEldritchGolem(this.world);
        placeBoss(boss, spawn, null);
        boss.onInitialSpawn(this.world.getDifficultyForLocation(spawn), null);
        this.world.spawnEntity(boss);
    }

    private void spawnCultistPortal(BossRoom room) {
        notifyNearbyPlayers("tc.boss.crimson");
        BlockPos spawn = room.getCenterPos(BOSS_ROOM_Y + 2);
        EntityCultistPortal boss = new EntityCultistPortal(this.world);
        boss.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
        this.world.spawnEntity(boss);
    }

    private void spawnTaintBoss(BossRoom room) {
        notifyNearbyPlayers("tc.boss.taint");
        BlockPos center = room.getCenterSpawnPos();
        boolean secondGiant = this.world.rand.nextBoolean();
        boolean fourthGiant = this.world.rand.nextBoolean();
        spawnTaintacle(center, this.world.getDifficulty() == EnumDifficulty.HARD);
        spawnTaintacle(center.add(3, 0, 3), secondGiant);
        spawnTaintacle(center.add(-3, 0, 3), !secondGiant);
        spawnTaintacle(center.add(3, 0, -3), fourthGiant);
        spawnTaintacle(center.add(-3, 0, -3), !fourthGiant);
    }

    private void spawnTaintacle(BlockPos spawn, boolean giant) {
        EntityTaintacle boss = giant
                ? new EntityTaintacleGiant(this.world)
                : new EntityTaintacle(this.world);
        boss.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
        EntityUtils.makeChampion(boss, true);
        this.world.spawnEntity(boss);
    }

    private void placeBoss(EntityLivingBase boss, BlockPos spawn, BlockPos home) {
        double dx = this.pos.getX() - (spawn.getX() + 0.5D);
        double dz = this.pos.getZ() - (spawn.getZ() + 0.5D);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        boss.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, 0.0F);
        if (home != null && boss instanceof EntityCreature) {
            ((EntityCreature) boss).setHomePosAndDistance(home, 32);
        }
    }

    private void notifyNearbyPlayers(String key) {
        for (EntityPlayer player : this.world.playerEntities) {
            if (player.getDistanceSq(this.pos) < 300.0D) {
                player.sendMessage(new TextComponentTranslation(key));
            }
        }
    }

    private void clearNearbyAiryBlocks() {
        for (BlockPos target : BlockPos.getAllInBox(this.pos.add(-2, -2, -2), this.pos.add(2, 2, 2))) {
            if (this.world.getBlockState(target).getBlock() == ConfigBlocks.blockAiry) {
                this.world.setBlockToAir(target);
            }
        }
    }

    public void setFacing(byte facing) {
        this.facing = facing;
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
        this.markDirty();
    }

    public byte getFacing() {
        return this.facing;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX() - 2.25D, this.pos.getY() - 2.25D, this.pos.getZ() - 2.25D,
                this.pos.getX() + 3.25D, this.pos.getY() + 3.25D, this.pos.getZ() + 3.25D);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 9216.0D;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.facing = compound.getByte("facing");
        this.count = compound.hasKey("count") ? compound.getShort("count") : -1;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("facing", this.facing);
        compound.setShort("count", (short) this.count);
    }

    private static class BossRoom {
        int centerX;
        int centerZ;
        byte exit;

        BossRoom(int centerX, int centerZ, byte exit) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.exit = exit;
        }

        BlockPos getCenterPos(int y) {
            return new BlockPos(this.centerX * 16 + 16, y, this.centerZ * 16 + 16);
        }

        BlockPos getCenterSpawnPos() {
            return getCenterPos(BOSS_ROOM_Y + 3);
        }

        BlockPos getCenterHomePos() {
            return getCenterPos(BOSS_ROOM_Y + 2);
        }

        BlockPos getOffsetSpawnPos() {
            int x = this.centerX * 16 + 16;
            int z = this.centerZ * 16 + 16;
            switch (this.exit) {
                case 2:
                    x += 8;
                    z += 8;
                    break;
                case 3:
                    x -= 8;
                    z += 8;
                    break;
                case 4:
                    x += 8;
                    z -= 8;
                    break;
                case 5:
                    x -= 8;
                    z -= 8;
                    break;
                default:
                    break;
            }
            return new BlockPos(x, BOSS_ROOM_Y + 3, z);
        }
    }
}
