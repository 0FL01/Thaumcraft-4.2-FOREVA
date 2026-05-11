package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionUnnaturalHunger extends Potion {

    public static PotionUnnaturalHunger instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionUnnaturalHunger(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(0, 2);
        setPotionName("potion.unnaturalhunger");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (target instanceof EntityPlayer) {
            FoodStats food = ((EntityPlayer) target).getFoodStats();
            food.addStats(-1, 0);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k > 0 && duration % k == 0;
    }
}
