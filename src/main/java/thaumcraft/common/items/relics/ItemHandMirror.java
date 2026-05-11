package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemHandMirror extends Item {

    public ItemHandMirror() {
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
        // Teleport items to linked mirror - TBD
        if (!world.isRemote) {
            RayTraceResult mop = this.rayTrace(world, player, false);
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = mop.getBlockPos();
                // Check for mirror block
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
