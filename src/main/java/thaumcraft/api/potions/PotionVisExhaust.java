package thaumcraft.api.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionVisExhaust extends Potion {

    public static PotionVisExhaust instance = null;
    private int statusIconIndex = -1;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionVisExhaust(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        configure(this);
    }

    @Deprecated
    public PotionVisExhaust(int ignoredId, boolean isBadEffect, int liquidColor) {
        this(isBadEffect, liquidColor);
    }

    public static void init() {
        if (instance != null) {
            configure(instance);
        }
    }

    private static void configure(PotionVisExhaust potion) {
        potion.setPotionName("potion.visexhaust");
        potion.setIconIndex(5, 1);
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
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
