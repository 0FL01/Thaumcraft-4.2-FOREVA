package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockTaint;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;

/**
 * Falling taint block entity — simulates taint blocks collapsing and falling
 * like sand/gravel, then placing the taint block on landing. Ported from
 * 1.7.10 original (not extending EntityFallingBlock).
 */
public class EntityFallingTaint extends Entity implements IEntityAdditionalSpawnData {

    public Block block;
    public int metadata;
    public int oldX;
    public int oldY;
    public int oldZ;
    public int fallTime = 0;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;

    public EntityFallingTaint(World world) {
        super(world);
    }

    public EntityFallingTaint(World world, double x, double y, double z,
                              Block block, int meta, int ox, int oy, int oz) {
        super(world);
        this.block = block;
        this.metadata = meta;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.oldX = ox;
        this.oldY = oy;
        this.oldZ = oz;
    }

    @Override
    protected boolean canTriggerWalking() { return false; }

    @Override
    protected void entityInit() {}

    @Override
    public boolean canBeCollidedWith() { return !this.isDead; }

    @Override
    public float getEyeHeight() { return this.height / 2.0F; }

    // ------------------------------------------------------------------
    // Spawn data
    // ------------------------------------------------------------------

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(Block.getIdFromBlock(this.block));
        buf.writeByte(this.metadata);
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        try {
            this.block = Block.getBlockById(buf.readInt());
            this.metadata = buf.readByte();
        } catch (Exception e) {
            // ignore
        }
    }

    // ------------------------------------------------------------------
    // Main update — falling physics + block placement
    // ------------------------------------------------------------------

    @Override
    public void onUpdate() {
        if (this.block == null || this.block == Blocks.AIR) {
            this.setDead();
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.fallTime++;
        this.motionY -= 0.04F;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        if (!this.world.isRemote) {
            // Server-side logic
            BlockPos pos = new BlockPos(this);
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();

            // On first tick, clear the source block
            if (this.fallTime == 1) {
                if (this.world.getBlockState(new BlockPos(this.oldX, this.oldY, this.oldZ)).getBlock() != this.block) {
                    this.setDead();
                    return;
                }
                this.world.setBlockToAir(new BlockPos(this.oldX, this.oldY, this.oldZ));
            }

            // Check if landed (on ground OR on flux goo with level >= 4)
            BlockPos below = pos.down();
            IBlockState belowState = this.world.getBlockState(below);
            boolean onFluxGoo = belowState.getBlock() == ConfigBlocks.blockFluxGoo
                && belowState.getBlock().getMetaFromState(belowState) >= 4;

            if (this.onGround || onFluxGoo) {
                BlockPos landPos = pos;
                IBlockState landState = this.world.getBlockState(landPos);
                Block landBlock = landState.getBlock();

                // Don't replace certain blocks
                if (landBlock == Blocks.SNOW_LAYER || landBlock == Blocks.TALLGRASS || landBlock == Blocks.DEADBUSH) {
                    this.setDead();
                    return;
                }

                this.world.playSound(null, this.posX, this.posY, this.posZ,
                    TCSounds.GORE, this.getSoundCategory(), 0.5F,
                    ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);

                // Try to place the taint block
                BlockPos targetPos = new BlockPos(i, j, k);
                if (this.canPlace(targetPos) && !BlockTaint.canFallBelow(this.world, targetPos.down())) {
                    IBlockState newState = this.block.getStateFromMeta(this.metadata);
                    if (this.world.setBlockState(targetPos, newState, 3)) {
                        BlockTaint.onFinishFalling(this.world, targetPos, newState);
                    }
                }
                this.setDead();
            } else {
                // Despawn if taking too long or out of world
                if ((this.fallTime > 100 && (j < 1 || j > 256)) || this.fallTime > 600) {
                    this.setDead();
                }
            }
        } else {
            // Client-side: falling particles
            if (this.onGround || this.fallTime == 1) {
                for (int q = 0; q < 10; ++q) {
                    Thaumcraft.proxy.taintLandFX(this);
                }
            }
        }
    }

    private boolean canPlace(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        Block existing = state.getBlock();
        return existing == ConfigBlocks.blockTaintFibres
            || existing == ConfigBlocks.blockFluxGoo
            || this.block.canPlaceBlockAt(this.world, pos);
    }

    // ------------------------------------------------------------------
    // Overrides
    // ------------------------------------------------------------------

    @Override
    protected void dealFireDamage(int amount) {}

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("TileID")) {
            this.block = Block.getBlockById(compound.getInteger("TileID"));
        }
        this.metadata = compound.getByte("Data") & 0xFF;
        this.fallTime = compound.getInteger("Time");
        this.oldX = compound.getInteger("OldX");
        this.oldY = compound.getInteger("OldY");
        this.oldZ = compound.getInteger("OldZ");
        if (compound.hasKey("HurtEntities")) {
            this.fallHurtAmount = compound.getFloat("FallHurtAmount");
            this.fallHurtMax = compound.getInteger("FallHurtMax");
        }
        if (this.block == null) {
            this.block = Blocks.STONE;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("TileID", Block.getIdFromBlock(this.block));
        compound.setByte("Data", (byte) this.metadata);
        compound.setInteger("Time", this.fallTime);
        compound.setFloat("FallHurtAmount", this.fallHurtAmount);
        compound.setInteger("FallHurtMax", this.fallHurtMax);
        compound.setInteger("OldX", this.oldX);
        compound.setInteger("OldY", this.oldY);
        compound.setInteger("OldZ", this.oldZ);
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category) {
        super.addEntityCrashInfo(category);
        category.addCrashSection("Imitating block ID", Block.getIdFromBlock(this.block));
        category.addCrashSection("Imitating block data", this.metadata);
    }

    @Override
    public float getBrightness() {
        return 0.0F;
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.isInRangeToRender3d(x, y, z);
    }
}
