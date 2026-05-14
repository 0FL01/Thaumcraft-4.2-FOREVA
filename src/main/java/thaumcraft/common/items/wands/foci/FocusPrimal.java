package thaumcraft.common.items.wands.foci;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.entities.projectile.EntityPrimalOrb;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;

public class FocusPrimal extends ItemFocusBasic {

    public static final FocusUpgradeType seeker = new FocusUpgradeType(16, new ResourceLocation("thaumcraft", "textures/foci/seeker.png"), "focus.upgrade.seeker.name", "focus.upgrade.seeker.text", new AspectList().add(Aspect.SENSES, 1).add(Aspect.MIND, 1));

    public FocusPrimal() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        Random rand = new Random(System.currentTimeMillis() / 200L);
        return new AspectList()
                .add(Aspect.WATER, 50 + rand.nextInt(5) * 50)
                .add(Aspect.AIR, 50 + rand.nextInt(5) * 50)
                .add(Aspect.EARTH, 50 + rand.nextInt(5) * 50)
                .add(Aspect.FIRE, 50 + rand.nextInt(5) * 50)
                .add(Aspect.ORDER, 50 + rand.nextInt(5) * 50)
                .add(Aspect.ENTROPY, 50 + rand.nextInt(5) * 50);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        AspectList cost = this.getVisCost(focusStack);
        if (!world.isRemote && wand.consumeAllVis(wandStack, player, cost, true, false)) {
            EntityPrimalOrb orb = new EntityPrimalOrb(world, (EntityLivingBase) player, this.isUpgradedWith(focusStack, seeker));
            world.spawnEntity(orb);
            orb.playSound(TCSounds.ICE, 0.3F, 0.8F + world.rand.nextFloat() * 0.1F);
        }
        player.swingArm(EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 500;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, seeker};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal};
            default:
                return null;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "FP" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
