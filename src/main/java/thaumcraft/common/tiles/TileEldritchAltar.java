package thaumcraft.common.tiles;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.lib.world.dim.MazeThread;

public class TileEldritchAltar extends TileThaumcraft implements ITickable, IWandable {
    private boolean spawner = false;
    private boolean open = false;
    private boolean spawnedClerics = false;
    private byte spawnType = 0;
    private byte eyes = 0;
    private int counter = 0;

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote || !this.spawner) return;
        if (this.counter++ < 80 || this.counter % 40 != 0) return;

        if (this.spawnType == 0) {
            if (!this.spawnedClerics) {
                spawnClerics();
            } else {
                spawnGuards();
            }
        } else if (this.spawnType == 1) {
            spawnGuardian();
        }
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 9216.0;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.eyes = compound.getByte("eyes");
        this.open = compound.getBoolean("open");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("eyes", this.eyes);
        compound.setBoolean("open", this.open);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.spawnedClerics = compound.getBoolean("spawnedClerics");
        this.spawner = compound.getBoolean("spawner");
        this.spawnType = compound.getByte("spawntype");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound ret = super.writeToNBT(compound);
        ret.setBoolean("spawnedClerics", this.spawnedClerics);
        ret.setBoolean("spawner", this.spawner);
        ret.setByte("spawntype", this.spawnType);
        return ret;
    }

    public boolean isSpawner() {
        return this.spawner;
    }

    public void setSpawner(boolean spawner) {
        this.spawner = spawner;
        this.markDirty();
    }

    public byte getSpawnType() {
        return this.spawnType;
    }

    public void setSpawnType(byte spawnType) {
        this.spawnType = spawnType;
        this.markDirty();
    }

    public byte getEyes() {
        return this.eyes;
    }

    public void setEyes(byte eyes) {
        this.eyes = eyes;
        this.markDirty();
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        this.markDirty();
    }

    public boolean checkForMaze() {
        if (this.world == null) return true;
        int width = 15 + this.world.rand.nextInt(8) * 2;
        int height = 15 + this.world.rand.nextInt(8) * 2;
        int chunkX = this.pos.getX() >> 4;
        int chunkZ = this.pos.getZ() >> 4;
        if (!MazeHandler.mazesInRange(chunkX, chunkZ, width, height)) {
            Thread mazeThread = new Thread(new MazeThread(chunkX, chunkZ, width, height, this.world.rand.nextLong()));
            mazeThread.start();
            return false;
        }
        return true;
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (world.isRemote) return 0;
        if (player == null
                || wandstack.isEmpty()
                || !(wandstack.getItem() instanceof ItemWandCasting)
                || !ResearchManager.isResearchComplete(player.getName(), "OCULUS")
                || this.eyes != 4
                || this.open) {
            return -1;
        }

        net.minecraft.tileentity.TileEntity node = world.getTileEntity(this.pos.up());
        if (!(node instanceof TileNode) || ((TileNode) node).getNodeType() != NodeType.DARK || !this.checkForMaze()) {
            return -1;
        }

        ItemWandCasting wand = (ItemWandCasting) wandstack.getItem();
        AspectList cost = new AspectList()
                .add(Aspect.AIR, 100)
                .add(Aspect.FIRE, 100)
                .add(Aspect.EARTH, 100)
                .add(Aspect.WATER, 100)
                .add(Aspect.ORDER, 100)
                .add(Aspect.ENTROPY, 100);
        if (!wand.consumeAllVisCrafting(wandstack, player, cost, true)) {
            return -1;
        }

        world.playSound(null, this.pos, TCSounds.WAND, SoundCategory.BLOCKS, 1.0F, 1.0F);
        this.setOpen(true);
        world.setBlockToAir(this.pos.up());
        world.setBlockState(this.pos.up(), ConfigBlocks.blockEldritchPortal.getDefaultState(), 3);
        this.markDirty();
        world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        return 1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    private void spawnGuards() {
        AxisAlignedBB area = new AxisAlignedBB(this.pos).grow(24.0, 16.0, 24.0);
        List<EntityCultistCleric> clerics = this.world.getEntitiesWithinAABB(EntityCultistCleric.class, area);
        if (clerics.isEmpty()) {
            setSpawner(false);
            return;
        }

        List<EntityCultist> cultists = this.world.getEntitiesWithinAABB(EntityCultist.class, area);
        if (cultists.size() >= 8) return;

        EntityCultistKnight knight = new EntityCultistKnight(this.world);
        BlockPos spawnPos = randomSpawnPos(4, 10, 3);
        if (canSpawnAt(spawnPos, knight)) {
            spawnLiving(knight, spawnPos, 16);
        }
    }

    private void spawnGuardian() {
        EntityEldritchGuardian guardian = new EntityEldritchGuardian(this.world);
        BlockPos spawnPos = randomSpawnPos(4, 10, 3);
        if (canSpawnAt(spawnPos, guardian) && guardian.getCanSpawnHere()) {
            spawnLiving(guardian, spawnPos, 16);
        }
    }

    private void spawnClerics() {
        int success = 0;
        int[][] offsets = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (int[] offset : offsets) {
            BlockPos spawnPos = this.pos.add(offset[0], 0, offset[1]);
            EntityCultistCleric cleric = new EntityCultistCleric(this.world);
            if (!canSpawnAt(spawnPos, cleric)) continue;
            if (spawnLiving(cleric, spawnPos, 8)) {
                success++;
                cleric.setIsRitualist(true);
            }
        }
        if (success > 2) {
            this.spawnedClerics = true;
            this.markDirty();
        }
    }

    private BlockPos randomSpawnPos(int minHorizontal, int maxHorizontal, int maxVertical) {
        int xOffset = randomSignedRange(minHorizontal, maxHorizontal);
        int yOffset = randomSignedRange(0, maxVertical);
        int zOffset = randomSignedRange(minHorizontal, maxHorizontal);
        return this.pos.add(xOffset, yOffset, zOffset);
    }

    private int randomSignedRange(int min, int max) {
        int value = min + this.world.rand.nextInt(max - min + 1);
        return value * (this.world.rand.nextBoolean() ? 1 : -1);
    }

    private boolean canSpawnAt(BlockPos spawnPos, EntityCreature entity) {
        BlockPos floor = spawnPos.down();
        if (!this.world.getBlockState(floor).isSideSolid(this.world, floor, EnumFacing.UP)) return false;
        entity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        return this.world.checkNoEntityCollision(entity.getEntityBoundingBox())
                && this.world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()
                && !this.world.containsAnyLiquid(entity.getEntityBoundingBox());
    }

    private boolean spawnLiving(EntityCreature entity, BlockPos spawnPos, int homeDistance) {
        entity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        entity.onInitialSpawn(this.world.getDifficultyForLocation(spawnPos), null);
        entity.enablePersistence();
        entity.setHomePosAndDistance(this.pos, homeDistance);
        return this.world.spawnEntity(entity);
    }
}
