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

public class ItemKey extends Item {

    public static final int META_IRON = 0;
    public static final int META_GOLD = 1;

    public ItemKey() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int d = stack.getItemDamage();
        return super.getTranslationKey() + "." + (d == 0 ? "iron" : "gold");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, META_IRON));
            items.add(new ItemStack(this, 1, META_GOLD));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // Lock/unlock doors - TBD
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
