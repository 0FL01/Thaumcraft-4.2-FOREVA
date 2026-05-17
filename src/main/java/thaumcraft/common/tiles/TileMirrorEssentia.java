package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.lib.events.EssentiaHandler;

public class TileMirrorEssentia extends TileThaumcraft implements ITickable, IAspectSource {
    public boolean linked = false;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public EnumFacing linkedFacing = null;
    private int count = 0;
    private int inc = 40;

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.count++ % this.inc == 0) {
            if (!this.isLinkValidSimple()) {
                if (this.inc < 600) this.inc += 20;
                this.restoreLink();
            } else {
                this.inc = 40;
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.linked = nbt.getBoolean("linked");
        this.linkX = nbt.getInteger("linkX");
        this.linkY = nbt.getInteger("linkY");
        this.linkZ = nbt.getInteger("linkZ");
        this.linkDim = nbt.getInteger("linkDim");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("linked", this.linked);
        nbt.setInteger("linkX", this.linkX);
        nbt.setInteger("linkY", this.linkY);
        nbt.setInteger("linkZ", this.linkZ);
        nbt.setInteger("linkDim", this.linkDim);
    }

    public void restoreLink() {
        if (!this.isDestinationValid()) return;
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return;
        TileEntity tile = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(tile instanceof TileMirrorEssentia)) return;
        TileMirrorEssentia target = (TileMirrorEssentia) tile;
        target.linked = true;
        target.linkX = this.pos.getX();
        target.linkY = this.pos.getY();
        target.linkZ = this.pos.getZ();
        target.linkDim = this.world.provider.getDimension();
        this.linkedFacing = EnumFacing.byIndex(targetWorld.getBlockState(target.getPos()).getBlock().getMetaFromState(targetWorld.getBlockState(target.getPos())) % 6);
        this.linked = true;
        this.markDirtyAndSync();
        target.markDirtyAndSync();
    }

    public void invalidateLink() {
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return;
        BlockPos targetPos = new BlockPos(this.linkX, this.linkY, this.linkZ);
        if (!targetWorld.isBlockLoaded(targetPos)) return;
        TileEntity tile = targetWorld.getTileEntity(targetPos);
        if (tile instanceof TileMirrorEssentia) {
            TileMirrorEssentia target = (TileMirrorEssentia) tile;
            target.linked = false;
            target.linkedFacing = null;
            this.markDirtyAndSync();
            target.markDirtyAndSync();
        }
    }

    public boolean isLinkValid() {
        if (!this.linked) return false;
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return false;
        TileEntity tile = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(tile instanceof TileMirrorEssentia)) return this.invalidateLocalLink();
        TileMirrorEssentia target = (TileMirrorEssentia) tile;
        if (!target.linked) return this.invalidateLocalLink();
        if (target.linkX != this.pos.getX() || target.linkY != this.pos.getY() || target.linkZ != this.pos.getZ()
                || target.linkDim != this.world.provider.getDimension()) return this.invalidateLocalLink();
        return true;
    }

    private boolean invalidateLocalLink() {
        this.linked = false;
        this.linkedFacing = null;
        this.markDirtyAndSync();
        return false;
    }

    public boolean isLinkValidSimple() {
        if (!this.linked) return false;
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return false;
        TileEntity tile = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(tile instanceof TileMirrorEssentia)) return false;
        TileMirrorEssentia target = (TileMirrorEssentia) tile;
        return target.linked && target.linkX == this.pos.getX() && target.linkY == this.pos.getY()
                && target.linkZ == this.pos.getZ() && target.linkDim == this.world.provider.getDimension();
    }

    public boolean isDestinationValid() {
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return false;
        TileEntity tile = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(tile instanceof TileMirrorEssentia)) return this.invalidateLocalLink();
        return !((TileMirrorEssentia) tile).isLinkValid();
    }

    @Override
    public AspectList getAspects() { return null; }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect tag) { return false; }

    @Override
    public int addToContainer(Aspect tag, int amount) { return 0; }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (!this.isLinkValid() || amount > 1) return false;
        WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) return false;
        if (this.linkedFacing == null) {
            BlockPos targetPos = new BlockPos(this.linkX, this.linkY, this.linkZ);
            this.linkedFacing = EnumFacing.byIndex(targetWorld.getBlockState(targetPos).getBlock().getMetaFromState(targetWorld.getBlockState(targetPos)) % 6);
        }
        TileEntity target = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        return target instanceof TileMirrorEssentia && EssentiaHandler.drainEssentia(target, tag, this.linkedFacing, 8, true);
    }

    @Override
    public boolean takeFromContainer(AspectList ot) { return false; }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) { return false; }

    @Override
    public boolean doesContainerContain(AspectList ot) { return false; }

    @Override
    public int containerContains(Aspect tag) { return 0; }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
