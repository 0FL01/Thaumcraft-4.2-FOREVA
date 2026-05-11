package thaumcraft.api.potions;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;

public class PotionFluxTaint
extends Potion {
    public static PotionFluxTaint instance = null;
    private int statusIconIndex = -1;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionFluxTaint(boolean par2, int par3) {
        super(par2, par3);
        this.setIconIndex(0, 0);
    }

    public static void init() {
        instance.setPotionName("potion.fluxtaint");
        instance.setIconIndex(3, 1);
        instance.setEffectiveness(0.25);
    }

    @SideOnly(value=Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    public void performEffect(EntityLivingBase target, int par2) {
        if (target instanceof ITaintedMob) {
            target.heal(1.0f);
        } else if (!target.isEntityUndead() && !(target instanceof EntityPlayer)) {
            target.attackEntityFrom(DamageSourceThaumcraft.taint, 1.0f);
        } else if (!target.isEntityUndead() && (target.getMaxHealth() > 1.0f || target instanceof EntityPlayer)) {
            target.attackEntityFrom(DamageSourceThaumcraft.taint, 1.0f);
        }
    }

    public boolean isReady(int par1, int par2) {
        int k = 40 >> par2;
        return k > 0 ? par1 % k == 0 : true;
    }
}
