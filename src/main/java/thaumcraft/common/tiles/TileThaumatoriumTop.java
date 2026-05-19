package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.BlockMetalDevice;
import thaumcraft.common.config.ConfigBlocks;

public class TileThaumatoriumTop extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport, ISidedInventory {
    private TileThaumatorium bottom;

    @Override
    public void update() {
        this.resolveBottom();
    }

    private TileThaumatorium resolveBottom() {
        if (this.world == null) return null;
        if (this.bottom == null || this.bottom.isInvalid() || this.bottom.getWorld() != this.world
                || !this.bottom.getPos().equals(this.pos.down())) {
            TileEntity tile = this.world.getTileEntity(this.pos.down());
            if (tile instanceof TileThaumatorium) {
                this.bottom = (TileThaumatorium) tile;
                IBlockState state = this.world.getBlockState(this.pos);
                this.world.notifyBlockUpdate(this.pos, state, state, 3);
                this.markDirty();
            } else {
                this.bottom = null;
                if (!this.world.isRemote) {
                    IBlockState state = this.world.getBlockState(this.pos);
                    if (state.getBlock() == ConfigBlocks.blockMetalDevice
                            && state.getValue(BlockMetalDevice.TYPE) == 11) {
                        this.world.setBlockState(this.pos, state.withProperty(BlockMetalDevice.TYPE, 9), 3);
                    }
                }
            }
        }
        return this.bottom;
    }

    @Override
    public AspectList getAspects() { return this.resolveBottom() == null ? new AspectList() : this.resolveBottom().getAspects(); }

    @Override
    public void setAspects(AspectList aspects) { if (this.resolveBottom() != null) this.resolveBottom().setAspects(aspects); }

    @Override
    public boolean doesContainerAccept(Aspect aspect) { return this.resolveBottom() != null && this.resolveBottom().doesContainerAccept(aspect); }

    @Override
    public int addToContainer(Aspect aspect, int amount) { return this.resolveBottom() == null ? amount : this.resolveBottom().addToContainer(aspect, amount); }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) { return this.resolveBottom() != null && this.resolveBottom().takeFromContainer(aspect, amount); }

    @Override
    public boolean takeFromContainer(AspectList aspects) { return this.resolveBottom() != null && this.resolveBottom().takeFromContainer(aspects); }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) { return this.resolveBottom() != null && this.resolveBottom().doesContainerContainAmount(aspect, amount); }

    @Override
    public boolean doesContainerContain(AspectList aspects) { return this.resolveBottom() != null && this.resolveBottom().doesContainerContain(aspects); }

    @Override
    public int containerContains(Aspect aspect) { return this.resolveBottom() == null ? 0 : this.resolveBottom().containerContains(aspect); }

    @Override
    public boolean isConnectable(EnumFacing face) { return this.resolveBottom() != null && this.resolveBottom().isConnectable(face); }

    @Override
    public boolean canInputFrom(EnumFacing face) { return this.resolveBottom() != null && this.resolveBottom().canInputFrom(face); }

    @Override
    public boolean canOutputTo(EnumFacing face) { return false; }

    @Override
    public void setSuction(Aspect aspect, int amount) { if (this.resolveBottom() != null) this.resolveBottom().setSuction(aspect, amount); }

    @Override
    public Aspect getSuctionType(EnumFacing loc) { return this.resolveBottom() == null ? null : this.resolveBottom().getSuctionType(loc); }

    @Override
    public int getSuctionAmount(EnumFacing loc) { return this.resolveBottom() == null ? 0 : this.resolveBottom().getSuctionAmount(loc); }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return this.resolveBottom() == null ? 0 : this.resolveBottom().addEssentia(aspect, amount, face); }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) { return null; }

    @Override
    public int getEssentiaAmount(EnumFacing loc) { return 0; }

    @Override
    public int getMinimumSuction() { return this.resolveBottom() == null ? 0 : this.resolveBottom().getMinimumSuction(); }

    @Override
    public boolean renderExtendedTube() { return false; }

    @Override
    public int getSizeInventory() { return this.resolveBottom() == null ? 0 : this.resolveBottom().getSizeInventory(); }

    @Override
    public boolean isEmpty() { return this.resolveBottom() == null || this.resolveBottom().isEmpty(); }

    @Override
    public ItemStack getStackInSlot(int index) { return this.resolveBottom() == null ? ItemStack.EMPTY : this.resolveBottom().getStackInSlot(index); }

    @Override
    public ItemStack decrStackSize(int index, int count) { return this.resolveBottom() == null ? ItemStack.EMPTY : this.resolveBottom().decrStackSize(index, count); }

    @Override
    public ItemStack removeStackFromSlot(int index) { return this.resolveBottom() == null ? ItemStack.EMPTY : this.resolveBottom().removeStackFromSlot(index); }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) { if (this.resolveBottom() != null) this.resolveBottom().setInventorySlotContents(index, stack); }

    @Override
    public int getInventoryStackLimit() { return this.resolveBottom() == null ? 64 : this.resolveBottom().getInventoryStackLimit(); }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return this.resolveBottom() != null && this.resolveBottom().isUsableByPlayer(player); }

    @Override
    public void openInventory(EntityPlayer player) { if (this.resolveBottom() != null) this.resolveBottom().openInventory(player); }

    @Override
    public void closeInventory(EntityPlayer player) { if (this.resolveBottom() != null) this.resolveBottom().closeInventory(player); }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) { return this.resolveBottom() != null && this.resolveBottom().isItemValidForSlot(index, stack); }

    @Override
    public int getField(int id) { return this.resolveBottom() == null ? 0 : this.resolveBottom().getField(id); }

    @Override
    public void setField(int id, int value) { if (this.resolveBottom() != null) this.resolveBottom().setField(id, value); }

    @Override
    public int getFieldCount() { return this.resolveBottom() == null ? 0 : this.resolveBottom().getFieldCount(); }

    @Override
    public void clear() { if (this.resolveBottom() != null) this.resolveBottom().clear(); }

    @Override
    public String getName() { return this.resolveBottom() == null ? "container.alchemyfurnace" : this.resolveBottom().getName(); }

    @Override
    public boolean hasCustomName() { return this.resolveBottom() != null && this.resolveBottom().hasCustomName(); }

    @Override
    public ITextComponent getDisplayName() { return this.resolveBottom() == null ? null : this.resolveBottom().getDisplayName(); }

    @Override
    public int[] getSlotsForFace(EnumFacing side) { return this.resolveBottom() == null ? new int[0] : this.resolveBottom().getSlotsForFace(side); }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) { return this.resolveBottom() != null && this.resolveBottom().canInsertItem(index, itemStackIn, direction); }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return this.resolveBottom() != null && this.resolveBottom().canExtractItem(index, stack, direction); }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World worldIn, BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
