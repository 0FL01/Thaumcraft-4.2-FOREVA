package thaumcraft.api.potions;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionVisExhaust
extends Potion {
    public static PotionVisExhaust instance = null;
    private int statusIconIndex = -1;
    static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");

    public PotionVisExhaust(boolean par2, int par3) {
        super(par2, par3);
        this.setIconIndex(0, 0);
    }

    public static void init() {
        instance.setPotionName("potion.visexhaust");
        instance.setIconIndex(5, 1);
        instance.setEffectiveness(0.25);
    }

    @SideOnly(value=Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        return super.getStatusIconIndex();
    }

    public void performEffect(EntityLivingBase target, int par2) {
    }
}
