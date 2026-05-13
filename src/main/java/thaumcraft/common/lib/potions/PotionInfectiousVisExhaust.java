package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.Config;

import java.util.List;

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
        if (target == null || target.world == null || target.world.isRemote) return;

        List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                target.getEntityBoundingBox().grow(4.0D, 4.0D, 4.0D));
        for (EntityLivingBase entity : targets) {
            if (entity == null || entity.isPotionActive(Config.potionInfectiousVisExhaust)) continue;
            if (amplifier > 0) {
                entity.addPotionEffect(new PotionEffect(Config.potionInfectiousVisExhaust, 6000, amplifier - 1, false, true));
            } else if (Config.potionVisExhaust != null) {
                entity.addPotionEffect(new PotionEffect(Config.potionVisExhaust, 6000, 0, false, true));
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}
