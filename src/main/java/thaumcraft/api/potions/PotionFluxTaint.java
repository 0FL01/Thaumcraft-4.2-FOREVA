package thaumcraft.api.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;

public class PotionFluxTaint extends Potion {

    public static PotionFluxTaint instance = null;
    private int statusIconIndex = -1;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionFluxTaint(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        configure(this);
    }

    @Deprecated
    public PotionFluxTaint(int ignoredId, boolean isBadEffect, int liquidColor) {
        this(isBadEffect, liquidColor);
    }

    public static void init() {
        if (instance != null) {
            configure(instance);
        }
    }

    private static void configure(PotionFluxTaint potion) {
        potion.setPotionName("potion.fluxtaint");
        potion.setIconIndex(3, 1);
        potion.setEffectiveness(0.25D);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int par2) {
        if (target instanceof ITaintedMob) {
            target.heal(1.0f);
        } else if (!target.isEntityUndead() && !(target instanceof EntityPlayer)) {
            target.attackEntityFrom(DamageSourceThaumcraft.taint, 1.0f);
        } else if (!target.isEntityUndead() && (target.getMaxHealth() > 1.0f || target instanceof EntityPlayer)) {
            target.attackEntityFrom(DamageSourceThaumcraft.taint, 1.0f);
        }
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int par1, int par2) {
        int k = 40 >> par2;
        return k > 0 ? par1 % k == 0 : true;
    }
}
