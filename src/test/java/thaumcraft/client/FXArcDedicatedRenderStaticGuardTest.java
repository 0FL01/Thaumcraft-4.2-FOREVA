package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FXArcDedicatedRenderStaticGuardTest {

    @Test
    public void fxArcKeepsDedicatedBeamTextureRenderPath() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/fx/beams/FXArc.java");

        assertTrue("FXArc should keep dedicated beamh texture render path",
                source.contains("textures/misc/beamh.png")
                        && source.contains("renderParticle(")
                        && source.contains("GL11.GL_TRIANGLE_STRIP")
                        && source.contains("DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP"));

        assertTrue("FXArc should keep additive blend and full-bright contracts",
                source.contains("blendmode = GL11.GL_ONE")
                        && source.contains("return 0xF000F0;")
                        && source.contains("return 3;"));

        assertFalse("FXArc should not fall back to spawning vanilla particles in onUpdate",
                source.contains("EnumParticleTypes.REDSTONE")
                        || source.contains("EnumParticleTypes.CRIT_MAGIC"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
