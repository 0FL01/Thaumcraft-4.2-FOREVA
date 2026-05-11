package thaumcraft.common.lib.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionSunScorned extends Potion {

    public static PotionSunScorned instance;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionSunScorned(boolean isBadEffect, int liquidColor) {
        super(isBadEffect, liquidColor);
        setIconIndex(3, 2);
        setPotionName("potion.sunscorned");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (target.world.isDaytime() && !target.world.isRemote) {
            float f = target.getBrightness();
            BlockPos blockpos = target.getRidingEntity() instanceof EntityZombie ? new BlockPos(target.posX, (double) Math.round(target.posY), target.posZ) : new BlockPos(target.posX, (double) Math.round(target.posY + 1.0), target.posZ);
            if (f > 0.5f && target.world.canSeeSky(blockpos) && target.world.getBlockState(blockpos).getBlock() == Blocks.AIR) {
                target.setFire(8);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k > 0 && duration % k == 0;
    }
}
