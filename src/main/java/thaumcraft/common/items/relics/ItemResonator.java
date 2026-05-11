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

public class ItemResonator extends Item {

    public ItemResonator() {
        this.setMaxStackSize(1);
        this.setMaxDamage(256);
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
        // Scan for aura node with vis - TBD
        if (!world.isRemote) {
            // Damage the resonator
            stack.damageItem(1, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
