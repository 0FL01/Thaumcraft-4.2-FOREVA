package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;

public class TileBanner extends TileThaumcraft {
    private byte facing = 0;
    private byte color = (byte) -1;
    private Aspect aspect = null;
    private boolean onWall = false;

    public boolean canUpdate() {
        return false;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 2, 1));
    }

    public byte getFacing() {
        return this.facing;
    }

    public void setFacing(byte facing) {
        this.facing = facing;
        this.markDirty();
    }

    public boolean getWall() {
        return this.onWall;
    }

    public void setWall(boolean wall) {
        this.onWall = wall;
        this.markDirty();
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    public byte getColor() {
        return this.color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.facing = compound.getByte("facing");
        this.setColor(compound.getByte("color"));
        String aspectTag = compound.getString("aspect");
        if (aspectTag != null && !aspectTag.isEmpty()) {
            this.setAspect(Aspect.getAspect(aspectTag));
        } else {
            this.aspect = null;
        }
        this.onWall = compound.getBoolean("wall");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("facing", this.facing);
        compound.setByte("color", this.getColor());
        compound.setString("aspect", this.getAspect() == null ? "" : this.getAspect().getTag());
        compound.setBoolean("wall", this.onWall);
    }
}
