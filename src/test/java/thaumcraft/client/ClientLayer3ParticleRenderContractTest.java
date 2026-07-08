package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientLayer3ParticleRenderContractTest {

    @Test
    public void layer3CustomParticlesShouldOwnExactlyOneInternalBeginDrawPass() throws IOException {
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/particles/FXBurst.java");
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/particles/FXBreaking.java");
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/particles/FXSmokeSpiral.java");
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/other/FXSonic.java");
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/other/FXShieldRunes.java");
        assertLayer3SelfContained("src/main/java/thaumcraft/client/fx/other/FXBlockWard.java");
    }

    private static void assertLayer3SelfContained(String path) throws IOException {
        String source = read(path);
        assertTrue(path + " must stay on layer 3 custom rendering", source.contains("return 3;"));
        assertTrue(path + " must not call Particle.setParticleTexture() from a custom layer-3 renderer", !source.contains("setParticleTexture("));
        assertEquals(path + " must open exactly one custom quad pass", 1,
                countOccurrences(source, "buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);"));
        assertEquals(path + " must close exactly one custom quad pass", 1,
                countOccurrences(source, "tessellator.draw();"));
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
