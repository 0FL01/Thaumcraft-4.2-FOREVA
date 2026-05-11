package thaumcraft.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCompassStone extends Item {

    public ItemCompassStone() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // Find nearest magical structure - TBD
        if (!world.isRemote) {
            BlockPos pos = player.getPosition();
            // Log the coordinates of nearest interesting location
            player.sendStatusMessage(new net.minecraft.util.text.TextComponentString(
                    "Ex nihilo: " + pos.getX() + ", " + pos.getZ()), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
