package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;

public class PotionThaumarhia extends Potion {

    public static PotionThaumarhia instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionThaumarhia(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(6, 1);
        setPotionName("potion.thaumarhia");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (target instanceof EntityPlayer && ((EntityPlayer) target).getMaxHealth() > 1.0f) {
            target.attackEntityFrom(DamageSource.MAGIC, 1.0f);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 60 >> amplifier;
        return k > 0 && duration % k == 0;
    }
}
