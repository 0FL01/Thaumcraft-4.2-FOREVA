package thaumcraft.client.renderers.item;

import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.wands.ItemFocusBasic;

/** Pure calculation shared by wand rendering and wand-sourced effects. */
@SideOnly(Side.CLIENT)
public final class WandUsePoseSampler {

    private WandUsePoseSampler() {
    }

    public static WandUsePose sample(float elapsedTicks,
                                     ItemFocusBasic.WandFocusAnimation animation,
                                     EnumHandSide side,
                                     boolean firstPerson) {
        float startup = MathHelper.clamp(elapsedTicks / 3.0F, 0.0F, 1.0F);
        float handSign = side == EnumHandSide.LEFT ? -1.0F : 1.0F;
        float contextRotateX = firstPerson ? 10.0F : 0.0F;
        float contextRotateZ = handSign * (firstPerson ? 10.0F : 33.0F);
        float waveRotateX = 0.0F;
        float waveRotateZ = 0.0F;

        if (animation == null || animation == ItemFocusBasic.WandFocusAnimation.WAVE) {
            waveRotateZ = handSign * MathHelper.sin(elapsedTicks / 10.0F) * 10.0F;
            waveRotateX = MathHelper.sin(elapsedTicks / 15.0F) * 10.0F;
        } else if (animation == ItemFocusBasic.WandFocusAnimation.CHARGE) {
            waveRotateZ = handSign * MathHelper.sin(elapsedTicks / 0.8F);
            waveRotateX = MathHelper.sin(elapsedTicks / 0.7F);
        }

        return new WandUsePose(startup, contextRotateX, contextRotateZ,
                -60.0F * startup, waveRotateX, waveRotateZ);
    }
}
