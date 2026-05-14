package thaumcraft.common.items.wands;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemFocusPouch extends Item {

    public ItemFocusPouch() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_FOCUS_POUCH, world,
                    MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public ItemStack[] getInventory(ItemStack item) {
        ItemStack[] inventory = new ItemStack[18];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = ItemStack.EMPTY;
        }
        if (item.hasTagCompound()) {
            NBTTagList list = item.getTagCompound().getTagList("Inventory", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound slotTag = list.getCompoundTagAt(i);
                int slot = slotTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = new ItemStack(slotTag);
                }
            }
        }
        return inventory;
    }

    public void setInventory(ItemStack item, ItemStack[] inventory) {
        NBTTagList list = new NBTTagList();
        if (inventory != null) {
            for (int i = 0; i < Math.min(inventory.length, 18); i++) {
                ItemStack stack = inventory[i];
                if (stack == null || stack.isEmpty()) continue;
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                stack.writeToNBT(slotTag);
                list.appendTag(slotTag);
            }
        }
        if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
        }
        item.getTagCompound().setTag("Inventory", list);
    }
}
