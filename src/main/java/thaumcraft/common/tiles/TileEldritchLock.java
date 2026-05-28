package thaumcraft.common.tiles;

import net.minecraft.block.Block;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;
import thaumcraft.common.entities.monster.boss.EntityEldritchGolem;
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.lib.world.dim.Cell;
import thaumcraft.common.lib.world.dim.CellLoc;
import thaumcraft.common.lib.world.dim.GenCommon;
import thaumcraft.common.lib.world.dim.MapBossData;
import thaumcraft.common.lib.world.dim.MazeHandler;

public class TileEldritchLock extends TileThaumcraft implements ITickable {
    private static final int BOSS_ROOM_Y = 50;
    private static final int[][] PEDESTAL = new int[][]{{2, 2, 2}, {0, -1, 1}, {3, 3, 3}};
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
        decorateWardenRoom(room);
        BlockPos spawn = room.getOffsetSpawnPos();
        EntityEldritchWarden boss = new EntityEldritchWarden(this.world);
        placeBoss(boss, spawn, room.getCenterHomePos());
        boss.onInitialSpawn(this.world.getDifficultyForLocation(spawn), null);
        this.world.spawnEntity(boss);
    }

    private void spawnEldritchGolem(BossRoom room) {
        notifyNearbyPlayers("tc.boss.golem");
        decorateGolemRoom(room);
        BlockPos spawn = room.getCenterSpawnPos();
        EntityEldritchGolem boss = new EntityEldritchGolem(this.world);
        placeBoss(boss, spawn, null);
        boss.onInitialSpawn(this.world.getDifficultyForLocation(spawn), null);
        this.world.spawnEntity(boss);
    }

    private void spawnCultistPortal(BossRoom room) {
        notifyNearbyPlayers("tc.boss.crimson");
        decorateCultistRoom(room);
        BlockPos spawn = room.getCenterPos(BOSS_ROOM_Y + 2);
        EntityCultistPortal boss = new EntityCultistPortal(this.world);
        boss.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
        this.world.spawnEntity(boss);
    }

    private void spawnTaintBoss(BossRoom room) {
        notifyNearbyPlayers("tc.boss.taint");
        decorateTaintRoom(room);
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

    private void decorateWardenRoom(BossRoom room) {
        int x = room.centerBlockX();
        int z = room.centerBlockZ();
        int y = BOSS_ROOM_Y;
        BlockPos offset = room.getOffsetPos(y + 2);
        int x2 = offset.getX();
        int z2 = offset.getZ();

        GenCommon.genObelisk(this.world, x2, y + 4, z);
        GenCommon.genObelisk(this.world, x, y + 4, z2);
        setBlock(new BlockPos(x2, y + 2, z), ConfigBlocks.blockEldritch, 3);
        setBlock(new BlockPos(x, y + 2, z2), ConfigBlocks.blockEldritch, 3);

        for (int a = -1; a <= 1; ++a) {
            for (int b = -1; b <= 1; ++b) {
                if (a != 0 && b != 0 && this.world.rand.nextFloat() < 0.9F) {
                    setBlock(new BlockPos(x2 + a, y + 2, z + b), ConfigBlocks.blockLootUrn, rollLootMeta(0.1F, 0.3F));
                }
                if (a != 0 && b != 0 && this.world.rand.nextFloat() < 0.9F) {
                    setBlock(new BlockPos(x + a, y + 2, z2 + b), ConfigBlocks.blockLootUrn, rollLootMeta(0.1F, 0.3F));
                }
            }
        }

        placeCornerBlocks(x, y + 3, z, ConfigBlocks.blockEldritch, 10);
        placeCornerBlocks(x, y + 2, z, ConfigBlocks.blockCosmeticSolid, 15);
        placePedestal(x2, y + 2, z2);
    }

    private void decorateGolemRoom(BossRoom room) {
        int x = room.centerBlockX();
        int z = room.centerBlockZ();
        int y = BOSS_ROOM_Y;
        int x2 = room.offsetX();
        int z2 = room.offsetZ();

        GenCommon.genObelisk(this.world, x + x2, y + 4, z + z2);
        GenCommon.genObelisk(this.world, x - x2, y + 4, z + z2);
        GenCommon.genObelisk(this.world, x + x2, y + 4, z - z2);
        setBlock(new BlockPos(x + x2, y + 2, z + z2), ConfigBlocks.blockEldritch, 3);
        setBlock(new BlockPos(x - x2, y + 2, z + z2), ConfigBlocks.blockEldritch, 3);
        setBlock(new BlockPos(x + x2, y + 2, z - z2), ConfigBlocks.blockEldritch, 3);

        placePedestal(x, y + 2, z);

        for (int a = -10; a <= 10; ++a) {
            for (int b = -10; b <= 10; ++b) {
                BlockPos pos = new BlockPos(x + a, y + 2, z + b);
                if (Math.abs(a) <= 2 || Math.abs(b) <= 2 || this.world.rand.nextFloat() >= 0.15F || !this.world.isAirBlock(pos)) {
                    continue;
                }
                int meta = rollLootMeta(0.05F, 0.2F);
                Block block = this.world.rand.nextFloat() < 0.3F ? ConfigBlocks.blockLootCrate : ConfigBlocks.blockLootUrn;
                setBlock(pos, block, meta);
            }
        }
    }

    private void decorateCultistRoom(BossRoom room) {
        int x = room.centerBlockX();
        int z = room.centerBlockZ();
        int y = BOSS_ROOM_Y;

        for (int a = -4; a <= 4; ++a) {
            for (int b = -4; b <= 4; ++b) {
                if ((Math.abs(a) == 2 || Math.abs(b) == 2) && this.world.rand.nextBoolean()
                        || (Math.abs(a) == 3 || Math.abs(b) == 3) && this.world.rand.nextFloat() > 0.33F
                        || (Math.abs(a) == 4 || Math.abs(b) == 4) && this.world.rand.nextFloat() > 0.25F) {
                    continue;
                }
                setBlock(new BlockPos(x + b, y + 1, z + a), ConfigBlocks.blockEldritch, 7);
            }
        }

        for (int a = 0; a < 5; ++a) {
            for (int b = 0; b < 5; ++b) {
                if (a != 0 && a != 4 && b != 0 && b != 4) {
                    continue;
                }
                BlockPos pillar = new BlockPos(x - 8 + b * 4, y, z - 8 + a * 4);
                setBlock(pillar.up(2), ConfigBlocks.blockCosmeticSolid, 11);
                setBlock(pillar.up(3), ConfigBlocks.blockEldritch, 5);
                setBlock(pillar.up(4), ConfigBlocks.blockSlabStone, 1);
                setBlock(pillar.up(10), ConfigBlocks.blockCosmeticSolid, 11);
                setBlock(pillar.up(9), ConfigBlocks.blockEldritch, 5);
                setBlock(pillar.up(8), ConfigBlocks.blockSlabStone, 9);
            }
        }
    }

    private void decorateTaintRoom(BossRoom room) {
        int x = room.centerBlockX();
        int z = room.centerBlockZ();
        int y = BOSS_ROOM_Y;

        for (int a = -12; a <= 12; ++a) {
            for (int b = -12; b <= 12; ++b) {
                Utils.setBiomeAt(this.world, x + b, z + a, ThaumcraftWorldGenerator.biomeTaint);
                for (int c = 0; c < 9; ++c) {
                    BlockPos fibre = new BlockPos(x + b, y + 2 + c, z + a);
                    if (this.world.isAirBlock(fibre)
                            && BlockUtils.isAdjacentToSolidBlock(this.world, fibre)
                            && this.world.rand.nextInt(3) != 0) {
                        setBlock(fibre, ConfigBlocks.blockTaintFibres, this.world.rand.nextInt(4) == 0 ? 1 : 0);
                    }
                }
                if (this.world.rand.nextFloat() < 0.15F) {
                    setBlock(new BlockPos(x + b, y + 2, z + a), ConfigBlocks.blockTaint, 0);
                    if (this.world.rand.nextFloat() < 0.2F) {
                        setBlock(new BlockPos(x + b, y + 3, z + a), ConfigBlocks.blockTaint, 0);
                    }
                }
                if ((Math.abs(a) == 4 || Math.abs(b) == 4) && this.world.rand.nextBoolean()
                        || (Math.abs(a) >= 5 || Math.abs(b) >= 5) && this.world.rand.nextFloat() > 0.33F
                        || (Math.abs(a) >= 7 || Math.abs(b) >= 7) && this.world.rand.nextFloat() > 0.25F) {
                    continue;
                }
                setBlock(new BlockPos(x + b, y + 1, z + a), ConfigBlocks.blockTaint, 1);
            }
        }
    }

    private void placeCornerBlocks(int x, int y, int z, Block block, int meta) {
        setBlock(new BlockPos(x - 2, y, z - 2), block, meta);
        setBlock(new BlockPos(x - 2, y, z + 2), block, meta);
        setBlock(new BlockPos(x + 2, y, z + 2), block, meta);
        setBlock(new BlockPos(x + 2, y, z - 2), block, meta);
    }

    private void placePedestal(int x, int y, int z) {
        for (int a = 0; a < 3; ++a) {
            for (int b = 0; b < 3; ++b) {
                int meta = PEDESTAL[a][b];
                if (meta < 0) {
                    setBlock(new BlockPos(x - 1 + b, y, z - 1 + a), ConfigBlocks.blockEldritch, 4);
                } else {
                    setBlock(new BlockPos(x - 1 + b, y, z - 1 + a), ConfigBlocks.blockStairsEldritch, meta);
                }
            }
        }
    }

    private int rollLootMeta(float rareThreshold, float uncommonThreshold) {
        float roll = this.world.rand.nextFloat();
        return roll < rareThreshold ? 2 : (roll < uncommonThreshold ? 1 : 0);
    }

    private void setBlock(BlockPos pos, Block block, int meta) {
        this.world.setBlockState(pos, block.getStateFromMeta(meta), 3);
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
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXBlockSparkle(target.getX(), target.getY(), target.getZ(), 0x400040),
                        new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), target.getX(), target.getY(), target.getZ(), 32.0));
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
        return 2304.0D;
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
            return new BlockPos(centerBlockX(), y, centerBlockZ());
        }

        BlockPos getCenterSpawnPos() {
            return getCenterPos(BOSS_ROOM_Y + 3);
        }

        BlockPos getCenterHomePos() {
            return getCenterPos(BOSS_ROOM_Y + 2);
        }

        BlockPos getOffsetSpawnPos() {
            return getOffsetPos(BOSS_ROOM_Y + 3);
        }

        BlockPos getOffsetPos(int y) {
            return new BlockPos(centerBlockX() + offsetX(), y, centerBlockZ() + offsetZ());
        }

        int centerBlockX() {
            return this.centerX * 16 + 16;
        }

        int centerBlockZ() {
            return this.centerZ * 16 + 16;
        }

        int offsetX() {
            switch (this.exit) {
                case 2:
                case 4:
                    return 8;
                case 3:
                case 5:
                    return -8;
                default:
                    return 0;
            }
        }

        int offsetZ() {
            switch (this.exit) {
                case 2:
                case 3:
                    return 8;
                case 4:
                case 5:
                    return -8;
                default:
                    return 0;
            }
        }
    }
}
