package thaumcraft.common.tiles;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.monster.EntityEldritchCrab;
import thaumcraft.common.lib.TCSounds;

public class TileEldritchCrabSpawner extends TileThaumcraft implements ITickable {
    public int count = 150;
    public int ticks = 0;
    int venting = 0;
    private byte facing = 0;

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.ticks == 0) {
            this.ticks = this.world.rand.nextInt(500);
        }
        ++this.ticks;

        if (this.world.isRemote) {
            if (this.venting > 0) {
                --this.venting;
                for (int i = 0; i < 3; i++) {
                    this.drawVent();
                }
            } else if (this.world.rand.nextInt(20) == 0) {
                this.drawVent();
            }
            return;
        }

        --this.count;
        if (this.count < 0) {
            this.count = 50 + this.world.rand.nextInt(50);
            return;
        }

        if (this.count == 15 && this.isActivated() && !this.maxEntitiesReached()) {
            this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 1, 0);
            this.world.playSound(null, this.pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1.0F);
        }

        if (this.count <= 0 && this.isActivated() && !this.maxEntitiesReached()) {
            this.count = 150 + this.world.rand.nextInt(100);
            this.spawnCrab();
            this.world.playSound(null, this.pos, TCSounds.GORE, SoundCategory.BLOCKS, 0.5F, 1.0F);
        }
    }

    private void drawVent() {
        EnumFacing direction = EnumFacing.byIndex(this.facing);
        float xOffset = 0.15F - this.world.rand.nextFloat() * 0.3F;
        float zOffset = 0.15F - this.world.rand.nextFloat() * 0.3F;
        float yOffset = 0.15F - this.world.rand.nextFloat() * 0.3F;
        float xMotion = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float zMotion = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float yMotion = 0.1F - this.world.rand.nextFloat() * 0.2F;
        Thaumcraft.proxy.drawVentParticles(
                this.world,
                this.pos.getX() + 0.5F + xOffset + direction.getXOffset() / 2.1F,
                this.pos.getY() + 0.5F + yOffset + direction.getYOffset() / 2.1F,
                this.pos.getZ() + 0.5F + zOffset + direction.getZOffset() / 2.1F,
                direction.getXOffset() / 3.0F + xMotion,
                direction.getYOffset() / 3.0F + yMotion,
                direction.getZOffset() / 3.0F + zMotion,
                10061994,
                2.0F);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.venting = 20;
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    private boolean maxEntitiesReached() {
        List<EntityEldritchCrab> crabs = this.world.getEntitiesWithinAABB(
                EntityEldritchCrab.class,
                new AxisAlignedBB(this.pos).grow(32.0D, 32.0D, 32.0D));
        return crabs.size() > 5;
    }

    public boolean isActivated() {
        EntityPlayer player = this.world.getClosestPlayer(
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.5D,
                this.pos.getZ() + 0.5D,
                16.0D,
                false);
        return player != null;
    }

    private void spawnCrab() {
        EnumFacing direction = EnumFacing.byIndex(this.facing);
        BlockPos spawnPos = this.pos.offset(direction);
        EntityEldritchCrab crab = new EntityEldritchCrab(this.world);
        crab.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D);
        crab.onInitialSpawn(this.world.getDifficultyForLocation(spawnPos), null);
        crab.setHelm(false);
        crab.motionX = direction.getXOffset() * 0.2F;
        crab.motionY = direction.getYOffset() * 0.2F;
        crab.motionZ = direction.getZOffset() * 0.2F;
        this.world.spawnEntity(crab);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(2, 2, 2));
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 9216.0D;
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
    public void readCustomNBT(NBTTagCompound compound) {
        this.facing = compound.getByte("facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("facing", this.facing);
    }
}
