package thaumcraft.common.items.equipment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBowBone extends ItemBow implements IRepairable {

    public ItemBowBone() {
        this.setMaxDamage(512);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getItemEnchantability() {
        return 3;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.BONE || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        int ticks = this.getMaxItemUseDuration(stack) - count;
        if (ticks > 18 && entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).stopActiveHand();
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        boolean infinite = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        ItemStack ammo = this.findAmmo(player);
        int charge = this.getMaxItemUseDuration(stack) - timeLeft;
        charge = ForgeEventFactory.onArrowLoose(stack, world, player, charge, !ammo.isEmpty() || infinite);
        if (charge < 0 || ammo.isEmpty() && !infinite) return;
        if (ammo.isEmpty()) ammo = new ItemStack(Items.ARROW);

        float velocity = getBoneArrowVelocity(charge);
        if (velocity < 0.1F) return;
        boolean preserveAmmo = player.capabilities.isCreativeMode
                || ammo.getItem() instanceof ItemArrow
                && ((ItemArrow) ammo.getItem()).isInfinite(ammo, stack, player);

        if (!world.isRemote) {
            ItemArrow arrowItem = ammo.getItem() instanceof ItemArrow
                    ? (ItemArrow) ammo.getItem() : (ItemArrow) Items.ARROW;
            EntityArrow arrow = this.customizeArrow(arrowItem.createArrow(world, ammo, player));
            arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 2.5F, 1.0F);
            arrow.setDamage(arrow.getDamage() + 0.5D);
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            if (power > 0) arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
            if (punch > 0) arrow.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) arrow.setFire(100);
            stack.damageItem(1, player);
            if (preserveAmmo || player.capabilities.isCreativeMode
                    && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            }
            world.spawnEntity(arrow);
        }

        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS, 1.0F,
                1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
        if (!preserveAmmo && !player.capabilities.isCreativeMode) {
            ammo.shrink(1);
            if (ammo.isEmpty()) player.inventory.deleteStack(ammo);
        }
        player.addStat(StatList.getObjectUseStats(this));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean canShoot = player.capabilities.isCreativeMode
                || !this.findAmmo(player).isEmpty()
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        ActionResult<ItemStack> eventResult = ForgeEventFactory.onArrowNock(stack, world, player, hand, canShoot);
        if (eventResult != null) return eventResult;
        if (!canShoot) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    static float getBoneArrowVelocity(int charge) {
        float velocity = (float) charge / 10.0F;
        velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
        return Math.min(velocity, 1.0F);
    }
}
