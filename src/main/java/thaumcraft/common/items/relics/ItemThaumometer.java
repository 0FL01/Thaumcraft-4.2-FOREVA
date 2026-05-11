package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumometer extends Item {

    public ItemThaumometer() {
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // Scan target - TBD (uses ScanManager + packets)
        if (!world.isRemote) {
            RayTraceResult mop = this.rayTrace(world, player, true);
            if (mop != null) {
                // Delegate to ScanManager
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
