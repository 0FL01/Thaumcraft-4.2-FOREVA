package thaumcraft.client;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class FxTextureAssetCoverageTest {

    @Test
    public void requiredStage8eFxTexturesExist() {
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particles.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particles.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particles2.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particles2.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particlefield.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particlefield.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/particlefield32.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/beam.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/beam1.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/beam2.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/beam3.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/beamh.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/wisp.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/wisp.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/wispy.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/wispy.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/vortex.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/vortex.png.mcmeta");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/tunnel.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/vignette.png");
        assertAsset("src/main/resources/assets/thaumcraft/textures/misc/vignette.png.mcmeta");
    }

    private static void assertAsset(String path) {
        assertTrue("Missing required FX asset: " + path, Files.exists(Paths.get(path)));
    }
}
