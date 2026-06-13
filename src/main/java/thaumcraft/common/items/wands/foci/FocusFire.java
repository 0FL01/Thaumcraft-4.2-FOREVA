package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.entities.projectile.EntityEmber;
import thaumcraft.common.entities.projectile.EntityExplosiveOrb;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.TCSounds;

public class FocusFire extends ItemFocusBasic {

    private static final AspectList COST_BASE = new AspectList().add(Aspect.FIRE, 10);
    private static final AspectList COST_BEAM = new AspectList().add(Aspect.FIRE, 10).add(Aspect.ORDER, 3);
    private static final AspectList COST_BALL = new AspectList().add(Aspect.FIRE, 66).add(Aspect.ENTROPY, 33);
    public static final FocusUpgradeType fireball = new FocusUpgradeType(9, new ResourceLocation("thaumcraft", "textures/foci/fireball.png"), "focus.upgrade.fireball.name", "focus.upgrade.fireball.text", new AspectList().add(Aspect.DARKNESS, 1));
    public static final FocusUpgradeType firebeam = new FocusUpgradeType(10, new ResourceLocation("thaumcraft", "textures/foci/firebeam.png"), "focus.upgrade.firebeam.name", "focus.upgrade.firebeam.text", new AspectList().add(Aspect.ENERGY, 1).add(Aspect.AIR, 1));
    private long soundDelay;

    public FocusFire() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF4500;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return this.isUpgradedWith(stack, firebeam) ? COST_BEAM : (this.isUpgradedWith(stack, fireball) ? COST_BALL : COST_BASE);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (this.isUpgradedWith(focusStack, fireball)) {
            if (!world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
                EntityExplosiveOrb orb = new EntityExplosiveOrb(world, (EntityLivingBase) player);
                orb.strength += (float) this.getUpgradeLevel(focusStack, FocusUpgradeType.potency) * 0.4F;
                orb.onFire = this.isUpgradedWith(focusStack, FocusUpgradeType.alchemistsfire);
                world.spawnEntity(orb);
                orb.playSound(TCSounds.FIRELOOP, 0.33F, 2.0F);
            }
            player.swingArm(EnumHand.MAIN_HAND);
        } else {
            player.setActiveHand(EnumHand.MAIN_HAND);
            WandManager.setCooldown(player, -1);
        }
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return this.isUpgradedWith(focusstack, fireball) ? 1000 : 0;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return this.isUpgradedWith(focusstack, fireball) ? ItemFocusBasic.WandFocusAnimation.WAVE : ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (this.isUpgradedWith(focusStack, fireball)) {
            player.resetActiveHand();
            return;
        }
        if (!wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), false, false)) {
            player.resetActiveHand();
            return;
        }
        if (!player.world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
            int potency = this.getUpgradeLevel(focusStack, FocusUpgradeType.potency);
            if (this.soundDelay < System.currentTimeMillis()) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.FIRELOOP, SoundCategory.PLAYERS, 0.33F, 2.0F);
                this.soundDelay = System.currentTimeMillis() + 500L;
            }
            float scatter = this.isUpgradedWith(focusStack, firebeam) ? 0.25F : 15.0F;
            for (int a = 0; a < 2 + potency; ++a) {
                EntityEmber ember = new EntityEmber(player.world, (EntityLivingBase) player, scatter);
                ember.damage = 2.0F + potency;
                if (this.isUpgradedWith(focusStack, firebeam)) {
                    ember.damage += 0.5F;
                    ember.damage *= 1.5F;
                    ember.duration = 30;
                }
                ember.firey = this.getUpgradeLevel(focusStack, FocusUpgradeType.alchemistsfire);
                ember.posX += ember.motionX;
                ember.posY += ember.motionY;
                ember.posZ += ember.motionZ;
                player.world.spawnEntity(ember);
            }
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "AF" + super.getSortingHelper(stack);
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.alchemistsfire};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, fireball, firebeam};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.alchemistsfire};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            default:
                return null;
        }
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return !type.equals(FocusUpgradeType.alchemistsfire)
                || !this.isUpgradedWith(focusstack, fireball)
                || !this.isUpgradedWith(focusstack, FocusUpgradeType.alchemistsfire);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
