package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBucketDeath extends Item {

    public ItemBucketDeath() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult mop = this.rayTrace(world, player, false);
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            // Place death water fluid - TBD
            world.playSound(player, mop.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            if (!player.capabilities.isCreativeMode) {
                player.setHeldItem(hand, new ItemStack(Items.BUCKET));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
