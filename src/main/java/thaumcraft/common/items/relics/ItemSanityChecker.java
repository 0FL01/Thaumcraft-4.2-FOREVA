package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemSanityChecker extends Item {

    public ItemSanityChecker() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            // Display current warp level - TBD
            player.sendStatusMessage(new net.minecraft.util.text.TextComponentString(
                    "Warp level: TBD"), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
