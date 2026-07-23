package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ResearchManager;

public class TileDeconstructionTable
extends TileThaumcraft
implements ISidedInventory, ITickable {

    public Aspect aspect;
    public int breaktime;
    private ItemStack[] itemStacks = new ItemStack[1];
    private String customName;
    private static final int[] sides = new int[]{0};

    public TileDeconstructionTable() {
        itemStacks[0] = ItemStack.EMPTY;
    }

    @Override
    public int getSizeInventory() { return 1; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? itemStacks[0] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0 && !itemStacks[0].isEmpty()) {
            if (itemStacks[0].getCount() <= count) {
                ItemStack stack = itemStacks[0];
                itemStacks[0] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            }
            ItemStack stack = itemStacks[0].splitStack(count);
            if (itemStacks[0].getCount() == 0) itemStacks[0] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0 && !itemStacks[0].isEmpty()) {
            ItemStack stack = itemStacks[0];
            itemStacks[0] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            itemStacks[0] = stack == null ? ItemStack.EMPTY : stack;
            if (!itemStacks[0].isEmpty() && itemStacks[0].getCount() > this.getInventoryStackLimit()) {
                itemStacks[0].setCount(this.getInventoryStackLimit());
            }
            this.markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D,
                (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index != 0 || stack.isEmpty()) return false;
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
        return tags != null && tags.size() > 0;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? new int[0] : sides;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return true; }

    @Override
    public int getField(int id) { return id == 0 ? this.breaktime : 0; }

    @Override
    public void setField(int id, int value) {
        if (id == 0) {
            this.breaktime = value;
        }
    }

    @Override
    public int getFieldCount() { return 1; }

    @Override
    public void clear() {
        itemStacks[0] = ItemStack.EMPTY;
        this.markDirty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.decontable";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName()
                ? new TextComponentString(this.getName())
                : new TextComponentTranslation(this.getName());
    }

    public void setGuiDisplayName(String customName) {
        this.customName = customName;
    }

    @Override
    public boolean isEmpty() { return itemStacks[0].isEmpty(); }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.aspect = null;
        if (compound.hasKey("Aspect", 8)) {
            this.aspect = Aspect.getAspect(compound.getString("Aspect"));
        } else if (compound.hasKey("aspect", 8)) {
            this.aspect = Aspect.getAspect(compound.getString("aspect"));
        }

        this.itemStacks = new ItemStack[]{ItemStack.EMPTY};
        String inventoryKey = compound.hasKey("Items", 9) ? "Items" : "Inventory";
        NBTTagList list = compound.getTagList(inventoryKey, 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.itemStacks.length) {
                this.itemStacks[slot] = new ItemStack(item);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.removeTag("Inventory");
        compound.removeTag("aspect");
        compound.removeTag("breaktime");

        if (this.aspect != null) {
            compound.setString("Aspect", this.aspect.getTag());
        } else {
            compound.removeTag("Aspect");
        }

        NBTTagList list = new NBTTagList();
        if (!itemStacks[0].isEmpty()) {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) 0);
            itemStacks[0].writeToNBT(item);
            list.appendTag(item);
        }
        compound.setTag("Items", list);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.customName = compound.hasKey("CustomName", 8) ? compound.getString("CustomName") : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound result = super.writeToNBT(compound);
        if (this.hasCustomName()) {
            result.setString("CustomName", this.customName);
        } else {
            result.removeTag("CustomName");
        }
        return result;
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) return;

        boolean changed = false;
        if (this.breaktime == 0 && this.canBreak()) {
            this.breaktime = 40;
            changed = true;
        }

        if (this.breaktime > 0 && this.canBreak()) {
            --this.breaktime;
            if (this.breaktime == 0) {
                this.breakItem();
                changed = true;
            }
        } else {
            this.breaktime = 0;
        }

        if (changed) {
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos,
                    this.world.getBlockState(this.pos),
                    this.world.getBlockState(this.pos), 3);
        }
    }

    private boolean canBreak() {
        if (this.itemStacks[0].isEmpty() || this.aspect != null) return false;
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(this.itemStacks[0]);
        tags = ThaumcraftCraftingManager.getBonusTags(this.itemStacks[0], tags);
        return tags != null && tags.size() > 0;
    }

    public void breakItem() {
        if (!this.canBreak()) return;

        AspectList tags = ThaumcraftCraftingManager.getObjectTags(this.itemStacks[0]);
        tags = ThaumcraftCraftingManager.getBonusTags(this.itemStacks[0], tags);
        AspectList primals = ResearchManager.reduceToPrimals(tags);
        if (this.world.rand.nextInt(80) < primals.visSize()) {
            Aspect[] choices = primals.getAspects();
            this.aspect = choices[this.world.rand.nextInt(choices.length)];
        }

        this.itemStacks[0].shrink(1);
        if (this.itemStacks[0].getCount() <= 0) {
            this.itemStacks[0] = ItemStack.EMPTY;
        }
    }

    public int getBreakTimeScaled(int scale) {
        return this.breaktime * scale / 40;
    }
}
