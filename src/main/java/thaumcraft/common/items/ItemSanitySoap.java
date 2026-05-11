package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemSanitySoap extends Item {

    public ItemSanitySoap() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            // Remove warp - TBD
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
