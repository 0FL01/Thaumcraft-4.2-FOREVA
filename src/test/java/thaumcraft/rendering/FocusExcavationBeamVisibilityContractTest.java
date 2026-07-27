package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/** CI-visible guard for the TC4 continuous excavation beam lifecycle. */
public class FocusExcavationBeamVisibilityContractTest {
    @Test
    public void excavationBeamShouldRestoreSoftAlphaInTheCustomParticleLayer() throws IOException {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java");
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String beam = read("src/main/java/thaumcraft/client/fx/beams/FXBeam.java");
        String beamCont = between(proxy, "public Object beamCont(", "public Object beamBore(");
        String render = between(beam, "public void renderParticle(", "protected float getBeamAlpha(");

        assertTrue(focus.contains("Thaumcraft.proxy.beamCont(")
                && focus.contains("player.world, player, tx, ty, tz, 2, 0x00FF66, false,")
                && focus.contains("impact > 0 ? 2.0F : 0.0F, beam.get(key), impact"));
        assertTrue(beamCont.contains("new FXBeamWand(")
                && beamCont.contains("beam.setType(type);")
                && beamCont.contains("beam.setEndMod(endmod);")
                && beamCont.contains("beam.setReverse(reverse);")
                && beamCont.contains("ParticleEngine.addEffect(world, beam);")
                && beamCont.contains("beam.updateBeam(tx, ty, tz);")
                && beamCont.contains("beam.impact = impact;"));
        assertTrue(beam.contains("public int getFXLayer()") && beam.contains("return 3;"));
        assertInOrder(render,
                "GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);",
                "Minecraft.getMinecraft().renderEngine.bindTexture(beamTexture);",
                "buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);",
                "renderImpact(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);",
                "GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String between(String source, String start, String end) {
        int startIndex = source.indexOf(start);
        int endIndex = source.indexOf(end, startIndex);
        assertTrue("Missing source section: " + start + " -> " + end,
                startIndex >= 0 && endIndex > startIndex);
        return source.substring(startIndex, endIndex);
    }

    private static void assertInOrder(String source, String... markers) {
        int index = -1;
        for (String marker : markers) {
            int next = source.indexOf(marker, index + 1);
            assertTrue("Missing or out-of-order source marker: " + marker, next > index);
            index = next;
        }
    }
}
