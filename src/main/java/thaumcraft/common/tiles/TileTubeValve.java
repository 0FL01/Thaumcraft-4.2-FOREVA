package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.TCSounds;

public class TileTubeValve extends TileTube {
    public boolean allowFlow = true;
    private boolean wasPoweredLastTick = false;
    public float rotation = 0.0F;

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.count % 5 == 0) {
            boolean powered = this.world.isBlockPowered(this.pos);
            if (this.wasPoweredLastTick && !powered && !this.allowFlow) {
                this.allowFlow = true;
                this.world.playSound(null, this.pos, TCSounds.SQUEEK, SoundCategory.BLOCKS,
                        0.7F, 0.9F + this.world.rand.nextFloat() * 0.2F);
                this.markDirtyAndSync();
            }
            if (!this.wasPoweredLastTick && powered && this.allowFlow) {
                this.allowFlow = false;
                this.world.playSound(null, this.pos, TCSounds.SQUEEK, SoundCategory.BLOCKS,
                        0.7F, 0.9F + this.world.rand.nextFloat() * 0.2F);
                this.markDirtyAndSync();
            }
            this.wasPoweredLastTick = powered;
        }
        if (this.world != null && this.world.isRemote) {
            if (!this.allowFlow && this.rotation < 360.0F) {
                this.rotation += 20.0F;
            } else if (this.allowFlow && this.rotation > 0.0F) {
                this.rotation -= 20.0F;
            }
        }
        super.update();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.allowFlow = !nbt.hasKey("flow") || nbt.getBoolean("flow");
        this.wasPoweredLastTick = nbt.getBoolean("hadpower");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("flow", this.allowFlow);
        nbt.setBoolean("hadpower", this.wasPoweredLastTick);
    }

    @Override
    public boolean isConnectable(net.minecraft.util.EnumFacing face) {
        return face != this.facing && super.isConnectable(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (this.allowFlow) super.setSuction(aspect, amount);
    }

    @Override
    public boolean canInputFrom(net.minecraft.util.EnumFacing face) { return this.allowFlow && super.canInputFrom(face); }

    @Override
    public boolean canOutputTo(net.minecraft.util.EnumFacing face) { return this.allowFlow && super.canOutputTo(face); }
}
