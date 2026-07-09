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
import thaumcraft.common.entities.projectile.EntityFrostShard;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;

public class FocusFrost extends ItemFocusBasic {

    private static final AspectList COST_BASE = new AspectList().add(Aspect.WATER, 5).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 2);
    private static final AspectList COST_SCATTER = new AspectList().add(Aspect.WATER, 20).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 2).add(Aspect.AIR, 5);
    private static final AspectList COST_BOULDER = new AspectList().add(Aspect.WATER, 20).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 2).add(Aspect.EARTH, 5);
    public static final FocusUpgradeType scattershot = new FocusUpgradeType(11, new ResourceLocation("thaumcraft", "textures/foci/scattershot.png"), "focus.upgrade.scattershot.name", "focus.upgrade.scattershot.text", new AspectList().add(Aspect.COLD, 1).add(Aspect.WEAPON, 1));
    public static final FocusUpgradeType iceboulder = new FocusUpgradeType(12, new ResourceLocation("thaumcraft", "textures/foci/iceboulder.png"), "focus.upgrade.iceboulder.name", "focus.upgrade.iceboulder.text", new AspectList().add(Aspect.COLD, 1).add(Aspect.CRYSTAL, 1));

    public FocusFrost() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x4F69CC;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return this.isUpgradedWith(stack, scattershot) ? COST_SCATTER : (this.isUpgradedWith(stack, iceboulder) ? COST_BOULDER : COST_BASE);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (!world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
            int potency = this.getUpgradeLevel(focusStack, FocusUpgradeType.potency);
            int frosty = this.getUpgradeLevel(focusStack, FocusUpgradeType.alchemistsfrost);
            EntityFrostShard shard = null;
            if (this.isUpgradedWith(focusStack, scattershot)) {
                for (int a = 0; a < 5 + potency * 2; ++a) {
                    shard = new EntityFrostShard(world, (EntityLivingBase) player, 8.0F);
                    shard.setDamage(1.0F);
                    shard.fragile = true;
                    shard.setFrosty(frosty);
                    world.spawnEntity(shard);
                }
            } else if (this.isUpgradedWith(focusStack, iceboulder)) {
                shard = new EntityFrostShard(world, (EntityLivingBase) player, 1.0F);
                shard.setDamage(4.0F + potency * 2.0F);
                shard.bounce = 0.8D;
                shard.bounceLimit = 6;
                shard.setFrosty(frosty);
                world.spawnEntity(shard);
            } else {
                shard = new EntityFrostShard(world, (EntityLivingBase) player, 1.0F);
                shard.setDamage((float) (3.0D + (double) potency * 1.5D));
                shard.setFrosty(frosty);
                world.spawnEntity(shard);
            }
            if (shard != null) {
                world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.ICE, SoundCategory.PLAYERS, 0.4F, 1.0F + world.rand.nextFloat() * 0.1F);
            }
        }
        player.swingArm(EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BF" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return this.isUpgradedWith(focusstack, scattershot) || this.isUpgradedWith(focusstack, iceboulder) ? 500 : 200;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.alchemistsfrost};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, scattershot, iceboulder, FocusUpgradeType.alchemistsfrost};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.alchemistsfrost};
            default:
                return null;
        }
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
