package thaumcraft.rendering;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GogglesAspectMaskingContractTest {

    @Test
    public void gogglesMaskUnknownAspectsWithoutHidingAmountsOrLeakingGlState() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/lib/RenderEventHandler.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("PlayerKnowledgeProvider.PLAYER_KNOWLEDGE"));
        assertTrue(source.contains("knowledge == null || !knowledge.hasDiscoveredAspect(aspect)"));
        assertTrue(source.contains("textures/aspects/_unknown.png"));
        assertTrue(source.contains("GlStateManager.color(red, green, blue, 0.75F);"));
        assertTrue(source.contains("String amountText = Integer.toString(amount);"));
        assertTrue(source.contains("finally {")
                && source.contains("boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);")
                && source.contains("boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);")
                && source.contains("boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);")
                && source.contains("boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);")
                && source.contains("boolean alphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);")
                && source.contains("GlStateManager.depthMask(depthMask);")
                && source.contains("GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);")
                && source.contains("GlStateManager.alphaFunc(alphaFunction, alphaReference);")
                && source.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);"));
    }
}
