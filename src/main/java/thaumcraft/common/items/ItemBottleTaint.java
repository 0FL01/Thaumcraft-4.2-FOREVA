package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBottleTaint extends Item implements IScribeTools {

    public ItemBottleTaint() {
        this.setMaxStackSize(8);
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            EntityBottleTaint bottle = new EntityBottleTaint(world, player);
            float yaw = player.rotationYaw * 0.017453292F;
            bottle.setPosition(
                player.posX - MathHelper.sin(yaw) * 0.8D,
                player.posY + player.getEyeHeight() - 0.1D,
                player.posZ + MathHelper.cos(yaw) * 0.8D);
            bottle.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);
            world.spawnEntity(bottle);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
