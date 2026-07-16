package thaumcraft.common.items.wands.foci;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.projectile.EntityShockOrb;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.utils.EntityUtils;

public class FocusShock extends ItemFocusBasic {

    private static final AspectList COST_BASE = new AspectList().add(Aspect.AIR, 25);
    private static final AspectList COST_CHAIN = new AspectList().add(Aspect.AIR, 40).add(Aspect.WATER, 10);
    private static final AspectList COST_GROUND = new AspectList().add(Aspect.AIR, 75).add(Aspect.EARTH, 25);
    public static final FocusUpgradeType chainlightning = new FocusUpgradeType(17, new ResourceLocation("thaumcraft", "textures/foci/chainlightning.png"), "focus.upgrade.chainlightning.name", "focus.upgrade.chainlightning.text", new AspectList().add(Aspect.WEATHER, 1));
    public static final FocusUpgradeType earthshock = new FocusUpgradeType(18, new ResourceLocation("thaumcraft", "textures/foci/earthshock.png"), "focus.upgrade.earthshock.name", "focus.upgrade.earthshock.text", new AspectList().add(Aspect.WEATHER, 1));

    public FocusShock() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFFF7E;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return this.isUpgradedWith(stack, chainlightning) ? COST_CHAIN : (this.isUpgradedWith(stack, earthshock) ? COST_GROUND : COST_BASE);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);
        if (this.isUpgradedWith(focusStack, earthshock)) {
            if (!world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
                EntityShockOrb orb = new EntityShockOrb(world, (EntityLivingBase) player);
                orb.area += this.getUpgradeLevel(focusStack, FocusUpgradeType.enlarge) * 2;
                orb.damage += (int) ((double) this.getUpgradeLevel(focusStack, FocusUpgradeType.potency) * 1.33D);
                world.spawnEntity(orb);
                orb.playSound(TCSounds.ZAP, 1.0F, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
            }
            player.swingArm(hand);
        } else {
            player.setActiveHand(hand);
            WandManager.setCooldown(player, -1);
        }
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return this.isUpgradedWith(focusstack, chainlightning) ? 500 : (this.isUpgradedWith(focusstack, earthshock) ? 1000 : 250);
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return this.isUpgradedWith(focusstack, earthshock) ? ItemFocusBasic.WandFocusAnimation.WAVE : ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (this.isUpgradedWith(focusStack, earthshock)) {
            if (!player.world.isRemote) {
                player.stopActiveHand();
            }
            return;
        }
        // Keep the client active until the authoritative server stop is synchronized. This
        // preserves both the release packet and the renderer's active-use pose.
        if (!player.world.isRemote
                && !wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), false, false)) {
            player.stopActiveHand();
            return;
        }
        Entity target = this.getPointedEntity(player.world, player, 20.0D);
        if (!player.world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
            int potency = this.getUpgradeLevel(focusStack, FocusUpgradeType.potency);
            player.world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.SHOCK, SoundCategory.PLAYERS, 0.25F, 1.0F);
            if (target instanceof EntityLivingBase && this.canDamageTarget(player, target)) {
                int chainUpgrade = this.getUpgradeLevel(focusStack, chainlightning);
                target.attackEntityFrom(DamageSource.causePlayerDamage(player), (chainUpgrade > 0 ? 6.0F : 4.0F) + potency);
                int chains = chainUpgrade * 2 + this.getUpgradeLevel(focusStack, FocusUpgradeType.enlarge) * 2;
                this.chainLightning(player, (EntityLivingBase) target, potency, chains);
            }
            return;
        }
        if (player.world.isRemote) {
            RayTraceResult hit = player.rayTrace(20.0D, 1.0F);
            Vec3d look = player.getLook(1.0F);
            double px = player.posX + look.x * 10.0D;
            double py = player.posY + look.y * 10.0D;
            double pz = player.posZ + look.z * 10.0D;
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && hit.hitVec != null) {
                px = hit.hitVec.x;
                py = hit.hitVec.y;
                pz = hit.hitVec.z;
                for (int i = 0; i < 5; i++) {
                    Thaumcraft.proxy.sparkle(
                            (float) px + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.3F,
                            (float) py + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.3F,
                            (float) pz + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.3F,
                            2.0F + player.world.rand.nextFloat(),
                            2,
                            0.05F + player.world.rand.nextFloat() * 0.05F);
                }
            }
            if (target != null) {
                px = target.posX;
                py = target.getEntityBoundingBox().minY + target.height * 0.5D;
                pz = target.posZ;
                for (int i = 0; i < 5; i++) {
                    Thaumcraft.proxy.sparkle(
                            (float) px + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.6F,
                            (float) py + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.6F,
                            (float) pz + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.6F,
                            2.0F + player.world.rand.nextFloat(),
                            2,
                            0.05F + player.world.rand.nextFloat() * 0.05F);
                }
            }
            shootLightning(player.world, player, px, py, pz);
        }
    }

    public static void shootLightning(World world, EntityLivingBase entityplayer, double tx, double ty, double tz) {
        Thaumcraft.proxy.focusShockBolt(world, entityplayer, tx, ty, tz);
    }

    private boolean canDamageTarget(EntityPlayer player, Entity target) {
        return !(target instanceof EntityPlayer)
            || player.world.getMinecraftServer() == null
            || player.world.getMinecraftServer().isPVPEnabled();
    }

    private void chainLightning(EntityPlayer player, EntityLivingBase firstTarget, int potency, int chains) {
        EntityLivingBase center = firstTarget;
        List<Integer> hit = new ArrayList<>();
        hit.add(firstTarget.getEntityId());
        while (chains-- > 0) {
            EntityLivingBase closest = null;
            double closestDistance = Double.MAX_VALUE;
            for (EntityLivingBase entity : EntityUtils.getEntitiesInRange(player.world, center.posX, center.posY, center.posZ, player, EntityLivingBase.class, 8.0D)) {
                if (hit.contains(entity.getEntityId()) || !this.canDamageTarget(player, entity)) continue;
                double distance = entity.getDistanceSq(center);
                if (distance < closestDistance) {
                    closest = entity;
                    closestDistance = distance;
                }
            }
            if (closest == null) return;
            if (!player.world.isRemote) {
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXZap(center.getEntityId(), closest.getEntityId()),
                        new NetworkRegistry.TargetPoint(
                                player.world.provider.getDimension(),
                                center.posX,
                                center.posY,
                                center.posZ,
                                64.0));
            }
            closest.attackEntityFrom(DamageSource.causePlayerDamage(player), 4.0F + potency);
            hit.add(closest.getEntityId());
            center = closest;
        }
    }

    private Entity getPointedEntity(World world, EntityPlayer player, double range) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = eyes.add(look.x * range, look.y * range, look.z * range);
        Entity pointed = null;
        double closest = range * range;
        for (Entity entity : world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).grow(1.0D), entity -> entity instanceof EntityLivingBase && entity.canBeCollidedWith())) {
            RayTraceResult hit = entity.getEntityBoundingBox().grow(0.3D).calculateIntercept(eyes, end);
            if (hit == null) continue;
            double distance = eyes.squareDistanceTo(hit.hitVec);
            if (distance < closest) {
                pointed = entity;
                closest = distance;
            }
        }
        return pointed;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BL" + super.getSortingHelper(stack);
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return !type.equals(FocusUpgradeType.enlarge)
                || this.isUpgradedWith(focusstack, chainlightning)
                || this.isUpgradedWith(focusstack, earthshock);
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, chainlightning, earthshock};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.enlarge};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.enlarge};
            default:
                return null;
        }
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
