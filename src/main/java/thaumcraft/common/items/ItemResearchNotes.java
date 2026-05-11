package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemResearchNotes extends Item {

    public ItemResearchNotes() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            // Grant research from notes - TBD
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
