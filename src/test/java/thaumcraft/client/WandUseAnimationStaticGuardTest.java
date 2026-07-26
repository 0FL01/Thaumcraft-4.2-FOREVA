package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WandUseAnimationStaticGuardTest {

    @Test
    public void wandUsePoseShouldUseElapsedTimeAndActivePhysicalHand() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWandRenderer.java");
        String sampler = read("src/main/java/thaumcraft/client/renderers/item/WandUsePoseSampler.java");

        assertTrue(renderer.contains("player.getItemInUseMaxCount() + partialTicks")
                && renderer.contains("isRenderedActiveHand(")
                && renderer.contains("getActivePhysicalSide(")
                && renderer.contains("isLeftHandTransform(")
                && renderer.contains("isRightHandTransform(")
                && renderer.contains("player.getActiveItemStack()")
                && renderer.contains("ForgeHooks.canContinueUsing(activeStack, renderedStack)"));
        assertFalse(renderer.contains("getItemInUseCount()")
                || renderer.contains("player.getHeldItem(activeHand)")
                || renderer.contains("ItemStack.areItemStacksEqual(activeStack, renderedStack)"));
        assertTrue(sampler.contains("MathHelper.clamp(elapsedTicks / 3.0F, 0.0F, 1.0F)")
                && sampler.contains("MathHelper.sin(elapsedTicks / 10.0F)")
                && sampler.contains("MathHelper.sin(elapsedTicks / 0.8F)"));
    }

    @Test
    public void modelBasisAndBeamOriginShouldConsumeTheSharedPose() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWandRenderer.java");
        String effectOrigin = read("src/main/java/thaumcraft/client/fx/beams/WandEffectOrigin.java");
        String beam = read("src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java");

        String renderSequence = "applyUseAnimation(wand, stack, player, partialTicks, transformType);\n"
                + "            }\n"
                + "            applyModelBasisCorrection(t);";
        assertTrue(renderer.contains(renderSequence));
        assertTrue(renderer.contains("CORRECTED_USE_PIVOT_Y = -1.0F")
                && renderer.contains("GlStateManager.rotate(-pose.contextRotateZ")
                && renderer.contains("GlStateManager.rotate(-pose.waveRotateZ"));
        assertTrue(effectOrigin.contains("WandUsePoseSampler.sample(")
                && effectOrigin.contains("WandPoseMath.transformPoint(")
                && effectOrigin.contains("forward.crossProduct(WORLD_UP)")
                && effectOrigin.contains("calibration.upOffset - calibration.pivotHeight")
                && effectOrigin.contains("player.height / 2.0D + 0.25D - player.getEyeHeight()"));
        assertTrue(beam.contains("WandEffectOrigin.resolve(player, partialTicks, yOffset)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
