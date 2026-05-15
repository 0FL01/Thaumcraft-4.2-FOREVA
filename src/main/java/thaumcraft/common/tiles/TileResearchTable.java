package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TileResearchTable
extends TileThaumcraft
implements IInventory, ITickable {

    private ItemStack[] stackList = new ItemStack[2];
    public AspectList bonusAspects = new AspectList();

    public TileResearchTable() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory() { return this.stackList.length; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.stackList.length ? this.stackList[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!this.stackList[index].isEmpty()) {
            if (this.stackList[index].getCount() <= count) {
                ItemStack stack = this.stackList[index];
                this.stackList[index] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            }
            ItemStack stack = this.stackList[index].splitStack(count);
            if (this.stackList[index].getCount() == 0) {
                this.stackList[index] = ItemStack.EMPTY;
            }
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.stackList[index].isEmpty()) {
            ItemStack stack = this.stackList[index];
            this.stackList[index] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stackList[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return true; }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public String getName() { return "Research Table"; }

    @Override
    public ITextComponent getDisplayName() { return new TextComponentString(getName()); }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : stackList) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Inventory", 10);
        this.stackList = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.stackList.length) {
                this.stackList[slot] = new ItemStack(item);
            }
        }

        this.bonusAspects = new AspectList();
        NBTTagList bonus = compound.getTagList("bonusAspects", 10);
        for (int i = 0; i < bonus.tagCount(); i++) {
            NBTTagCompound tag = bonus.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(tag.getString("tag"));
            if (aspect != null) {
                this.bonusAspects.merge(aspect, tag.getInteger("amount"));
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.stackList.length; i++) {
            if (!this.stackList[i].isEmpty()) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                this.stackList[i].writeToNBT(item);
                list.appendTag(item);
            }
        }
        compound.setTag("Inventory", list);

        NBTTagList bonus = new NBTTagList();
        for (Aspect aspect : this.bonusAspects.getAspects()) {
            if (aspect == null) continue;
            int amount = this.bonusAspects.getAmount(aspect);
            if (amount <= 0) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("tag", aspect.getTag());
            tag.setInteger("amount", amount);
            bonus.appendTag(tag);
        }
        compound.setTag("bonusAspects", bonus);
    }

    @Override
    public void update() {
        // Research scanning logic will be added later
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        switch (index) {
            case 0:
                return stack.getItem() instanceof IScribeTools;
            case 1:
                return stack.getItem() instanceof ItemResearchNotes && stack.getMetadata() < 64;
            default:
                return false;
        }
    }

    public void duplicate(EntityPlayer player) {
        if (player == null || this.world == null || this.world.isRemote) return;

        ItemStack notesStack = getStackInSlot(1);
        if (notesStack.isEmpty() || !(notesStack.getItem() instanceof ItemResearchNotes) || notesStack.getMetadata() != 64) {
            return;
        }

        ResearchNoteData data = ResearchManager.getData(notesStack);
        if (data == null || data.key == null || data.key.isEmpty()) return;

        ResearchItem research = ResearchCategories.getResearch(data.key);
        if (research == null || research.tags == null) return;

        if (!playerHasItem(player, Items.FEATHER) || !playerHasItem(player, Items.PAPER)) return;

        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return;

        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int needed = research.tags.getAmount(aspect) + data.copies;
            if (knowledge.getAspectPoolFor(aspect) < needed) {
                return;
            }
        }

        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int cost = research.tags.getAmount(aspect) + data.copies;
            knowledge.addAspectPool(aspect, -cost);
        }

        InventoryUtils.consumeInventoryItem(player, Items.FEATHER, 0);
        InventoryUtils.consumeInventoryItem(player, Items.PAPER, 0);

        data.copies++;
        ResearchManager.updateData(notesStack, data);
        setInventorySlotContents(1, notesStack);

        ItemStack duplicate = notesStack.copy();
        duplicate.setCount(1);
        if (!player.inventory.addItemStackToInventory(duplicate)) {
            player.dropItem(duplicate, false);
        }

        markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }

    private boolean playerHasItem(EntityPlayer player, net.minecraft.item.Item item) {
        NonNullList<ItemStack> main = player.inventory.mainInventory;
        for (ItemStack stack : main) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }
}
