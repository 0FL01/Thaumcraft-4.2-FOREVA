package thaumcraft.common.items.wands.foci;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.entities.projectile.EntityPechBlast;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;

public class FocusPech extends ItemFocusBasic {

    private static final String DEPTH_SPRITE = "thaumcraft:items/focus_pech_depth";

    public static final FocusUpgradeType nightshade = new FocusUpgradeType(15, new ResourceLocation("thaumcraft", "textures/foci/nightshade.png"), "focus.upgrade.nightshade.name", "focus.upgrade.nightshade.text", new AspectList().add(Aspect.LIFE, 1).add(Aspect.POISON, 1).add(Aspect.MAGIC, 1));

    public FocusPech() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x229944;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getFocusDepthLayerIcon(ItemStack stack) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(DEPTH_SPRITE);
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        if (this.isUpgradedWith(stack, nightshade)) {
            return new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 10).add(Aspect.EARTH, 10)
                    .add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10).add(Aspect.WATER, 10);
        }
        return new AspectList().add(Aspect.EARTH, 10).add(Aspect.ENTROPY, 10).add(Aspect.WATER, 10);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (!world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
            EntityPechBlast blast = new EntityPechBlast(world, (EntityLivingBase) player,
                    wand.getFocusPotency(wandStack),
                    wand.getFocusExtend(wandStack),
                    this.isUpgradedWith(focusStack, nightshade));
            blast.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(blast);
            blast.playSound(TCSounds.ICE, 0.4F, 1.0F + world.rand.nextFloat() * 0.1F);
        }
        player.swingArm(EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 250;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.extend};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.extend};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, nightshade};
            default:
                return null;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "PP" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
