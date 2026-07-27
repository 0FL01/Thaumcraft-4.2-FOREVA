package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
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

    @Test
    public void excavationBeamShouldRenderFromTheCapturedWandTipToTheCrosshair() throws IOException {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java");
        String beam = read("src/main/java/thaumcraft/client/fx/beams/FXBeam.java");
        String wandBeam = read("src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java");
        String origin = read("src/main/java/thaumcraft/client/renderers/item/FirstPersonWandTipOrigin.java");
        String updateBeam = between(focus, "private void updateBeam(", "@Override\n    public FocusUpgradeType[]");
        String renderFromSource = between(beam, "void renderParticleFromSource(", "protected float getBeamAlpha(");
        String wandRender = between(wandBeam, "public void renderParticle(", "private static Vec3d sourcePos(");
        String resolve = between(origin, "public static Vec3d resolveAndRequest(", "/** Captures the visible cap");

        assertTrue(updateBeam.contains("player.getPositionEyes(1.0F).add(player.getLookVec().scale(10.0D))")
                && updateBeam.contains("tx = mop.hitVec.x;")
                && updateBeam.contains("ty = mop.hitVec.y;")
                && updateBeam.contains("tz = mop.hitVec.z;"));
        assertFalse(updateBeam.contains("player.posY +"));

        assertInOrder(wandRender,
                "boolean localFirstPerson",
                "FirstPersonWandTipOrigin.resolveAndRequest(",
                "if (renderSource == null)",
                "return;",
                "this.renderParticleFromSource(");
        assertTrue(renderFromSource.contains("if (sourceOverride == null)")
                && renderFromSource.contains("double targetX = this.ptX + (this.tX - this.ptX) * partialTicks;")
                && renderFromSource.contains("double targetY = this.ptY + (this.tY - this.ptY) * partialTicks;")
                && renderFromSource.contains("double targetZ = this.ptZ + (this.tZ - this.ptZ) * partialTicks;")
                && renderFromSource.contains("renderLength = MathHelper.sqrt(")
                && renderFromSource.contains("Math.atan2(xd, zd)")
                && renderFromSource.contains("Math.atan2(yd, horizontal)"));
        assertFalse(renderFromSource.contains("this.length =")
                || renderFromSource.contains("this.rotYaw =")
                || renderFromSource.contains("this.rotPitch =")
                || renderFromSource.contains("this.setPosition("));

        assertTrue(resolve.contains("Sample current = sample;"));
        assertFalse(resolve.contains("Sample current = sample;\n        sample = null;"));
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
