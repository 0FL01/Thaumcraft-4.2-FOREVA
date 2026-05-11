package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionDeathGaze extends Potion {

    public static PotionDeathGaze instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionDeathGaze(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(2, 2);
        setPotionName("potion.deathgaze");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (target.getHealth() <= 1.0f && target.isEntityUndead()) {
            target.attackEntityFrom(DamageSource.MAGIC, 20.0f);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 60 >> amplifier;
        return k > 0 && duration % k == 0;
    }
}
