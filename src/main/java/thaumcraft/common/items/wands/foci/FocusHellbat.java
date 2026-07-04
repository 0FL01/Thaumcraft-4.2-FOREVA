package thaumcraft.common.items.wands.foci;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.EntityUtils;

public class FocusHellbat extends ItemFocusBasic {

    private static final String ORNAMENT_SPRITE = "thaumcraft:items/focus_hellbat_orn";

    public static final FocusUpgradeType batbombs = new FocusUpgradeType(13, new ResourceLocation("thaumcraft", "textures/foci/batbombs.png"), "focus.upgrade.batbombs.name", "focus.upgrade.batbombs.text", new AspectList().add(Aspect.ENERGY, 1).add(Aspect.TRAP, 1));
    public static final FocusUpgradeType devilbats = new FocusUpgradeType(14, new ResourceLocation("thaumcraft", "textures/foci/devilbats.png"), "focus.upgrade.devilbats.name", "focus.upgrade.devilbats.text", new AspectList().add(Aspect.ARMOR, 1));
    public static final FocusUpgradeType vampirebats = new FocusUpgradeType(19, new ResourceLocation("thaumcraft", "textures/foci/vampirebats.png"), "focus.upgrade.vampirebats.name", "focus.upgrade.vampirebats.text", new AspectList().add(Aspect.HUNGER, 1).add(Aspect.LIFE, 1));
    private static final AspectList COST_BASE = new AspectList().add(Aspect.FIRE, 200).add(Aspect.ENTROPY, 100).add(Aspect.AIR, 100);
    private static final AspectList COST_BOMB = new AspectList().add(Aspect.FIRE, 100).add(Aspect.ENTROPY, 200).add(Aspect.AIR, 100);
    private static final AspectList COST_DEVIL = new AspectList().add(Aspect.FIRE, 100).add(Aspect.ENTROPY, 100).add(Aspect.AIR, 100).add(Aspect.EARTH, 100);

    public FocusHellbat() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF0000;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getOrnament(ItemStack stack) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ORNAMENT_SPRITE);
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        if (this.isUpgradedWith(stack, batbombs)) return COST_BOMB;
        if (this.isUpgradedWith(stack, devilbats)) return COST_DEVIL;
        return COST_BASE;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        Entity pointed = EntityUtils.getPointedEntity(world, player, 32.0D, EntityFireBat.class);
        if (!(pointed instanceof EntityLivingBase)) return wandStack;

        if (!world.isRemote) {
            if (pointed instanceof EntityPlayer
                    && world.getMinecraftServer() != null
                    && !world.getMinecraftServer().isPVPEnabled()) {
                return wandStack;
            }

            EntityFireBat firebat = new EntityFireBat(world);
            double px = player.posX;
            double py = player.getEntityBoundingBox().minY + (double) (player.height / 2.0F) + 0.25D;
            double pz = player.posZ;
            px -= (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            py -= 0.05000000014901161D;
            pz -= (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            Vec3d look = player.getLook(1.0F);
            px += look.x * 0.5D;
            py += look.y * 0.5D;
            pz += look.z * 0.5D;

            firebat.setLocationAndAngles(px, py + (double) firebat.height, pz, player.rotationYaw, 0.0F);
            firebat.setAttackTarget((EntityLivingBase) pointed);
            firebat.damBonus = this.getUpgradeLevel(focusStack, FocusUpgradeType.potency);
            firebat.setIsSummoned(true);
            firebat.setIsBatHanging(false);
            if (this.isUpgradedWith(focusStack, devilbats)) {
                firebat.setIsDevil(true);
            }
            if (this.isUpgradedWith(focusStack, batbombs)) {
                firebat.setIsExplosive(true);
            }
            if (this.isUpgradedWith(focusStack, vampirebats)) {
                firebat.owner = player;
                firebat.setIsVampire(true);
            }

            if (wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false) && world.spawnEntity(firebat)) {
                world.playEvent(2004, firebat.getPosition(), 0);
                firebat.playSound(TCSounds.ICE, 0.2F, 0.95F + world.rand.nextFloat() * 0.1F);
            } else {
                player.playSound(TCSounds.WANDFAIL, 0.1F, 0.8F + world.rand.nextFloat() * 0.1F);
            }
        }
        player.swingArm(EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 1000;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
            case 2:
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, batbombs, devilbats};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, vampirebats};
            default:
                return null;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "HH" + super.getSortingHelper(stack);
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return !type.equals(vampirebats) || player != null && ThaumcraftApiHelper.isResearchComplete(player.getName(), "VAMPBAT");
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
