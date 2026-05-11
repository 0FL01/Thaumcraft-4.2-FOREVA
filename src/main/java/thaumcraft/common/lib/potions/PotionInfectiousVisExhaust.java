package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionInfectiousVisExhaust extends Potion {

    public static PotionInfectiousVisExhaust instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionInfectiousVisExhaust(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(5, 1);
        setPotionName("potion.infvisexhaust");
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
