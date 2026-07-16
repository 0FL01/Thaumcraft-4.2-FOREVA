package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.lib.TCSounds;

public class TileFocalManipulator extends TileThaumcraft implements ITickable, IInventory {
    public AspectList aspects = new AspectList();
    public int size = 0;
    public int upgrade = -1;
    public int rank = -1;
    public boolean reset = false;
    public static final int XP_MULT = 8;
    public static final int VIS_MULT = 200;

    private int ticks = 0;
    private ItemStack[] itemStacks = new ItemStack[]{ItemStack.EMPTY};

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) return;

        boolean complete = false;
        boolean upgraded = false;
        if (this.rank < 0) this.rank = 0;
        ++this.ticks;

        if (this.ticks % 5 == 0) {
            if (this.size > 0 && (this.aspects.visSize() <= 0 || this.getStackInSlot(0).isEmpty())) {
                complete = true;
            }
            if (this.size > 0) {
                for (Aspect aspect : this.aspects.getAspectsSortedAmount()) {
                    int drained = VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                            aspect, Math.min(100, this.aspects.getAmount(aspect)));
                    if (drained <= 0) continue;
                    this.aspects.reduce(aspect, drained);
                    this.markDirtyAndSync();
                }
                if (this.aspects.visSize() <= 0 && !this.getStackInSlot(0).isEmpty()
                        && this.getStackInSlot(0).getItem() instanceof ItemFocusBasic) {
                    complete = true;
                    ItemFocusBasic focus = (ItemFocusBasic) this.getStackInSlot(0).getItem();
                    if (this.upgrade >= 0 && this.upgrade < FocusUpgradeType.types.length
                            && FocusUpgradeType.types[this.upgrade] != null) {
                        focus.applyUpgrade(this.getStackInSlot(0), FocusUpgradeType.types[this.upgrade], this.rank);
                        upgraded = true;
                    }
                }
            }
        }

        if (complete) {
            if (upgraded) {
                this.world.playSound(null, this.pos, TCSounds.WAND, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else {
                this.world.playSound(null, this.pos, TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 0.33F, 1.0F);
            }
            this.size = 0;
            this.rank = -1;
            this.upgrade = -1;
            this.aspects = new AspectList();
            this.markDirtyAndSync();
        }
    }

    public boolean startCraft(int id, EntityPlayer player) {
        ItemStack focusStack = this.getStackInSlot(0);
        if (this.size > 0 || focusStack.isEmpty() || !(focusStack.getItem() instanceof ItemFocusBasic)) return false;

        ItemFocusBasic focus = (ItemFocusBasic) focusStack.getItem();
        short[] applied = focus.getAppliedUpgrades(focusStack);
        this.rank = 1;
        while (this.rank <= 5 && applied[this.rank - 1] != -1) {
            ++this.rank;
        }
        if (this.rank > 5) return false;

        int xp = this.rank * XP_MULT;
        if (player.experienceLevel < xp && !player.capabilities.isCreativeMode) return false;

        FocusUpgradeType[] allowed = focus.getPossibleUpgradesByRank(focusStack, this.rank);
        if (allowed == null) return false;

        boolean found = false;
        for (FocusUpgradeType type : allowed) {
            if (type != null && type.id == id) {
                found = true;
                break;
            }
        }
        if (!found || id < 0 || id >= FocusUpgradeType.types.length || FocusUpgradeType.types[id] == null) return false;
        if (!focus.canApplyUpgrade(focusStack, player, FocusUpgradeType.types[id], this.rank)) return false;

        int amount = VIS_MULT;
        for (int i = 1; i < this.rank; ++i) {
            amount *= 2;
        }

        AspectList cost = new AspectList();
        for (Aspect aspect : FocusUpgradeType.types[id].aspects.getAspects()) {
            cost.add(aspect, amount);
        }
        this.aspects = reduceToPrimals(cost);
        this.size = this.aspects.visSize();
        this.upgrade = id;

        if (!player.capabilities.isCreativeMode) {
            player.addExperienceLevel(-xp);
        }
        this.markDirtyAndSync();
        this.world.playSound(null, this.pos, TCSounds.CRAFTSTART, SoundCategory.BLOCKS, 0.25F, 1.0F);
        return true;
    }

    private static AspectList reduceToPrimals(AspectList source) {
        AspectList out = new AspectList();
        for (Aspect aspect : source.getAspects()) {
            addPrimal(out, aspect, source.getAmount(aspect));
        }
        return out;
    }

    private static void addPrimal(AspectList out, Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return;
        if (aspect.isPrimal() || aspect.getComponents() == null) {
            out.add(aspect, amount);
            return;
        }
        for (Aspect component : aspect.getComponents()) {
            addPrimal(out, component, amount);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.itemStacks = new ItemStack[]{ItemStack.EMPTY};
        NBTTagList list = nbt.getTagList("Inventory", 10);
        if (list.tagCount() > 0) {
            NBTTagCompound itemTag = list.getCompoundTagAt(0);
            this.itemStacks[0] = new ItemStack(itemTag);
        }
        this.aspects.readFromNBT(nbt);
        this.size = nbt.getInteger("size");
        this.upgrade = nbt.getInteger("upgrade");
        this.rank = nbt.getInteger("rank");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        if (!this.itemStacks[0].isEmpty()) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) 0);
            this.itemStacks[0].writeToNBT(itemTag);
            list.appendTag(itemTag);
        }
        nbt.setTag("Inventory", list);
        this.aspects.writeToNBT(nbt);
        nbt.setInteger("size", this.size);
        nbt.setInteger("upgrade", this.upgrade);
        nbt.setInteger("rank", this.rank);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.itemStacks[0] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0 || this.itemStacks[0].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack;
        if (this.itemStacks[0].getCount() <= count) {
            stack = this.itemStacks[0];
            this.itemStacks[0] = ItemStack.EMPTY;
        } else {
            stack = this.itemStacks[0].splitStack(count);
            if (this.itemStacks[0].getCount() <= 0) this.itemStacks[0] = ItemStack.EMPTY;
        }
        this.aspects = new AspectList();
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index != 0 || this.itemStacks[0].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.itemStacks[0];
        this.itemStacks[0] = ItemStack.EMPTY;
        this.aspects = new AspectList();
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index != 0) return;
        this.itemStacks[0] = stack;
        this.aspects = new AspectList();
        this.reset = this.world != null && this.world.isRemote;
        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.focalmanipulator";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && !stack.isEmpty() && stack.getItem() instanceof ItemFocusBasic;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return this.size;
            case 1: return this.upgrade;
            case 2: return this.rank;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: this.size = value; break;
            case 1: this.upgrade = value; break;
            case 2: this.rank = value; break;
            default: break;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
    }

    @Override
    public void clear() {
        this.itemStacks[0] = ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return this.itemStacks[0].isEmpty();
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
