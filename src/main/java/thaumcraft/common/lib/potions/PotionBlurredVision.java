package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBlurredVision extends Potion {

    public static PotionBlurredVision instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionBlurredVision(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(1, 2);
        setPotionName("potion.blurredvision");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
