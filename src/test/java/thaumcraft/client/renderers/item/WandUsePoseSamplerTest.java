package thaumcraft.client.renderers.item;

import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import org.junit.Test;
import thaumcraft.api.wands.ItemFocusBasic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WandUsePoseSamplerTest {

    @Test
    public void startupShouldReachFullPoseAfterThreeElapsedTicks() {
        assertEquals(0.0F, sample(0.0F).startup, 0.0001F);
        assertEquals(1.0F / 3.0F, sample(1.0F).startup, 0.0001F);
        assertEquals(2.0F / 3.0F, sample(2.0F).startup, 0.0001F);
        assertEquals(1.0F, sample(3.0F).startup, 0.0001F);
        assertEquals(1.0F, sample(100.0F).startup, 0.0001F);
    }

    @Test
    public void sequentialElapsedTicksShouldAdvanceWaveAndLeftShouldMirrorOnlyZ() {
        WandUsePose tick20 = sample(20.0F);
        WandUsePose tick21 = sample(21.0F);
        assertNotEquals(tick20.waveRotateX, tick21.waveRotateX, 0.0001F);
        assertNotEquals(tick20.waveRotateZ, tick21.waveRotateZ, 0.0001F);

        WandUsePose left = WandUsePoseSampler.sample(20.0F,
                ItemFocusBasic.WandFocusAnimation.WAVE, EnumHandSide.LEFT, true);
        assertEquals(tick20.contextRotateX, left.contextRotateX, 0.0001F);
        assertEquals(tick20.startupRotateX, left.startupRotateX, 0.0001F);
        assertEquals(tick20.waveRotateX, left.waveRotateX, 0.0001F);
        assertEquals(-tick20.contextRotateZ, left.contextRotateZ, 0.0001F);
        assertEquals(-tick20.waveRotateZ, left.waveRotateZ, 0.0001F);
    }

    @Test
    public void correctedBasisShouldKeepTheSustainedWandAtItsHandPivot() {
        WandUsePose sustained = new WandUsePose(
                1.0F, 0.0F, 33.0F, -60.0F, 0.0F, 0.0F);
        Vec3d transformed = WandPoseMath.transformPoint(
                Vec3d.ZERO, new Vec3d(0.0D, -1.0D, 0.0D), sustained);

        assertEquals(0.27232D, transformed.x, 0.00001D);
        assertEquals(-0.58066D, transformed.y, 0.00001D);
        assertEquals(-0.86603D, transformed.z, 0.00001D);
    }

    private static WandUsePose sample(float elapsedTicks) {
        return WandUsePoseSampler.sample(elapsedTicks,
                ItemFocusBasic.WandFocusAnimation.WAVE, EnumHandSide.RIGHT, true);
    }
}
