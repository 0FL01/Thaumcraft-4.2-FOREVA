package thaumcraft.common.tiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.DimensionManager;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

import java.util.ArrayList;

public class TileMirror extends TileThaumcraft implements ITickable, IInventory {
    private static final String OUTPUT_ORIGIN = "tcMirrorOutput";
    public boolean linked = false;
    public int linkX;
    public int linkY;
    public int linkZ;
    public int linkDim;
    public int instability;
    int count = 0;
    int inc = 40;
    private final ArrayList<ItemStack> outputStacks = new ArrayList<>();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1));
    }

    static void writeOutputOrigin(NBTTagCompound data, int dimension, BlockPos origin) {
        NBTTagCompound outputOrigin = new NBTTagCompound();
        outputOrigin.setInteger("dimension", dimension);
        outputOrigin.setLong("position", origin.toLong());
        data.setTag(OUTPUT_ORIGIN, outputOrigin);
    }

    static boolean isOutputFrom(NBTTagCompound data, int dimension, BlockPos origin) {
        if (!data.hasKey(OUTPUT_ORIGIN, 10)) {
            return false;
        }
        NBTTagCompound outputOrigin = data.getCompoundTag(OUTPUT_ORIGIN);
        return outputOrigin.getInteger("dimension") == dimension
                && outputOrigin.getLong("position") == origin.toLong();
    }

    @Override
    public void update() {
        if (this.world == null) return;
        if (!this.world.isRemote) {
            int tickrate = this.instability / 50;
            if (tickrate == 0 || this.count % (tickrate * tickrate) == 0) {
                this.eject();
            }
            this.checkInstability();
            if (this.count++ % this.inc == 0) {
                if (!this.isLinkValidSimple()) {
                    if (this.inc < 600) {
                        this.inc += 20;
                    }
                    this.restoreLink();
                } else {
                    this.inc = 40;
                }
            }
        }
    }

    public void restoreLink() {
        if (!this.isDestinationValid()) {
            return;
        }
        net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (te instanceof TileMirror) {
            TileMirror tm = (TileMirror) te;
            tm.linked = true;
            tm.linkX = this.pos.getX();
            tm.linkY = this.pos.getY();
            tm.linkZ = this.pos.getZ();
            tm.linkDim = this.world.provider.getDimension();
            targetWorld.notifyBlockUpdate(tm.pos, targetWorld.getBlockState(tm.pos), targetWorld.getBlockState(tm.pos), 3);
            this.linked = true;
            this.markDirty();
            tm.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    public void invalidateLink() {
        net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return;
        }
        BlockPos targetPos = new BlockPos(this.linkX, this.linkY, this.linkZ);
        if (!targetWorld.isBlockLoaded(targetPos)) {
            return;
        }
        TileEntity te = targetWorld.getTileEntity(targetPos);
        if (te instanceof TileMirror) {
            TileMirror tm = (TileMirror) te;
            tm.linked = false;
            this.markDirty();
            tm.markDirty();
            targetWorld.notifyBlockUpdate(targetPos, targetWorld.getBlockState(targetPos), targetWorld.getBlockState(targetPos), 3);
        }
    }

    public boolean isLinkValid() {
        if (!this.linked) {
            return false;
        }
        net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(te instanceof TileMirror)) {
            this.linked = false;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            return false;
        }
        TileMirror tm = (TileMirror) te;
        if (!tm.linked) {
            this.linked = false;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            return false;
        }
        if (tm.linkX != this.pos.getX() || tm.linkY != this.pos.getY() || tm.linkZ != this.pos.getZ() || tm.linkDim != this.world.provider.getDimension()) {
            this.linked = false;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            return false;
        }
        return true;
    }

    public boolean isLinkValidSimple() {
        if (!this.linked) {
            return false;
        }
        net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(te instanceof TileMirror)) {
            return false;
        }
        TileMirror tm = (TileMirror) te;
        if (!tm.linked) {
            return false;
        }
        return tm.linkX == this.pos.getX() && tm.linkY == this.pos.getY() && tm.linkZ == this.pos.getZ() && tm.linkDim == this.world.provider.getDimension();
    }

    public boolean isDestinationValid() {
        net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
        if (targetWorld == null) {
            return false;
        }
        TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (!(te instanceof TileMirror)) {
            this.linked = false;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            return false;
        }
        return !((TileMirror) te).isLinkValid();
    }

    public boolean transport(EntityItem ie) {
        if (this.world != null && isOutputFrom(ie.getEntityData(), this.world.provider.getDimension(), this.pos)) {
            return false;
        }
        ItemStack items = ie.getItem();
        if (!this.linked || !this.isLinkValid()) {
            return false;
        }
        net.minecraft.world.WorldServer linkedWorld = DimensionManager.getWorld(this.linkDim);
        if (linkedWorld == null) {
            return false;
        }
        TileEntity target = linkedWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (target instanceof TileMirror) {
            ((TileMirror) target).addStack(items.copy());
            this.addInstability(null, items.getCount());
            ie.setDead();
            this.markDirty();
            target.markDirty();
            this.world.addBlockEvent(this.pos, ConfigBlocks.blockMirror, 1, 0);
            return true;
        }
        return false;
    }

    public void eject() {
        if (this.outputStacks.size() > 0 && this.count > 20) {
            int i = this.world.rand.nextInt(this.outputStacks.size());
            if (this.outputStacks.get(i) != null) {
                ItemStack outItem = this.outputStacks.get(i).copy();
                outItem.setCount(1);
                if (this.spawnItem(outItem)) {
                    this.outputStacks.get(i).shrink(1);
                    this.addInstability(null, 1);
                    this.world.addBlockEvent(this.pos, ConfigBlocks.blockMirror, 1, 0);
                    if (this.outputStacks.get(i).getCount() <= 0) {
                        this.outputStacks.remove(i);
                    }
                    this.markDirty();
                }
            }
        }
    }

    public boolean spawnItem(ItemStack stack) {
        try {
            EnumFacing face = this.getFacing();
            EntityItem ie2 = new EntityItem(this.world,
                    this.pos.getX() + 0.5D - face.getXOffset() * 0.3D,
                    this.pos.getY() + 0.5D - face.getYOffset() * 0.3D,
                    this.pos.getZ() + 0.5D - face.getZOffset() * 0.3D,
                    stack);
            ie2.motionX = face.getXOffset() * 0.15F;
            ie2.motionY = face.getYOffset() * 0.15F;
            ie2.motionZ = face.getZOffset() * 0.15F;
            ie2.setPickupDelay(20);
            writeOutputOrigin(ie2.getEntityData(), this.world.provider.getDimension(), this.pos);
            this.world.spawnEntity(ie2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void addInstability(net.minecraft.world.World targetWorld, int amt) {
        this.instability += amt;
        if (targetWorld != null) {
            TileEntity te = targetWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
            if (te instanceof TileMirror) {
                ((TileMirror) te).instability += amt;
                if (((TileMirror) te).instability < 0) {
                    ((TileMirror) te).instability = 0;
                }
                te.markDirty();
            }
        }
    }

    public void checkInstability() {
        if (this.instability > 0 && this.count % 20 == 0) {
            --this.instability;
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
        if (this.instability > 0) {
            int amt = VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.ORDER, Math.min(this.instability, 1));
            if (amt > 0) {
                net.minecraft.world.WorldServer targetWorld = DimensionManager.getWorld(this.linkDim);
                this.addInstability(targetWorld, -amt);
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            if (this.world != null && this.world.isRemote) {
                EnumFacing face = this.getFacing();
                for (int q = 0; q < Thaumcraft.proxy.particleCount(1); ++q) {
                    double xx = this.pos.getX() + 0.33D + this.world.rand.nextFloat() * 0.33F - face.getXOffset() / 2.0D;
                    double yy = this.pos.getY() + 0.33D + this.world.rand.nextFloat() * 0.33F - face.getYOffset() / 2.0D;
                    double zz = this.pos.getZ() + 0.33D + this.world.rand.nextFloat() * 0.33F - face.getZOffset() / 2.0D;
                    Thaumcraft.proxy.drawGenericParticles(this.world,
                            xx, yy, zz,
                            0.0D, 0.004D, 0.0D,
                            0.15F + Math.abs(face.getXOffset()) * 0.35F,
                            0.15F + Math.abs(face.getYOffset()) * 0.35F,
                            0.15F + Math.abs(face.getZOffset()) * 0.35F,
                            0.75F,
                            false, 128, 8, -1, 8, 0, 0.5F, 1);
                }
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.linked = nbt.getBoolean("linked");
        this.linkX = nbt.getInteger("linkX");
        this.linkY = nbt.getInteger("linkY");
        this.linkZ = nbt.getInteger("linkZ");
        this.linkDim = nbt.getInteger("linkDim");
        this.instability = nbt.getInteger("instability");

        this.outputStacks.clear();
        NBTTagList list = nbt.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemNbt = list.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(itemNbt);
            if (!stack.isEmpty()) {
                this.outputStacks.add(stack);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("linked", this.linked);
        nbt.setInteger("linkX", this.linkX);
        nbt.setInteger("linkY", this.linkY);
        nbt.setInteger("linkZ", this.linkZ);
        nbt.setInteger("linkDim", this.linkDim);
        nbt.setInteger("instability", this.instability);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.outputStacks.size(); ++i) {
            ItemStack stack = this.outputStacks.get(i);
            if (stack == null || stack.isEmpty()) continue;
            NBTTagCompound itemNbt = new NBTTagCompound();
            stack.writeToNBT(itemNbt);
            list.appendTag(itemNbt);
        }
        nbt.setTag("Items", list);
    }

    public void addStack(ItemStack stack) {
        this.outputStacks.add(stack);
        this.markDirty();
    }

    private EnumFacing getFacing() {
        if (this.world == null || this.pos == null) {
            return EnumFacing.NORTH;
        }
        int meta = this.world.getBlockState(this.pos).getBlock().getMetaFromState(this.world.getBlockState(this.pos));
        EnumFacing face = EnumFacing.byIndex(meta % 6);
        return face == null ? EnumFacing.NORTH : face;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        net.minecraft.world.WorldServer linkedWorld = DimensionManager.getWorld(this.linkDim);
        TileEntity target = linkedWorld == null ? null : linkedWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        if (target instanceof TileMirror) {
            ((TileMirror) target).addStack(stack.copy());
            this.addInstability(null, stack.getCount());
            if (this.world != null) {
                this.world.addBlockEvent(this.pos, ConfigBlocks.blockMirror, 1, 0);
            }
        } else {
            this.spawnItem(stack.copy());
        }
    }

    @Override
    public String getName() {
        return "container.mirror";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        net.minecraft.world.WorldServer linkedWorld = DimensionManager.getWorld(this.linkDim);
        TileEntity target = linkedWorld == null ? null : linkedWorld.getTileEntity(new BlockPos(this.linkX, this.linkY, this.linkZ));
        return target instanceof TileMirror;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.outputStacks.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.outputStacks.isEmpty();
    }
}
