package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileCentrifugeFidelityStaticGuardTest {

    @Test
    public void tileCentrifugeKeepsRotationSoundAndContainerContracts() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileCentrifuge.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileCentrifugeRenderer.java");

        assertTrue("TileCentrifuge should keep client rotation-crossing pump sound",
                tile.contains("int previous = (int) this.rotation;")
                        && tile.contains("this.rotation % 180.0F <= 20.0F")
                        && tile.contains("previous % 180 >= 160")
                        && tile.contains("TCSounds.PUMP"));

        assertTrue("TileCentrifuge setAspects should remain no-op like reference",
                tile.contains("public void setAspects(AspectList aspects) {")
                        && tile.contains("public AxisAlignedBB getRenderBoundingBox()"));

        assertTrue("TileCentrifuge renderer should rotate by tile rotation state",
                renderer.contains("float spin = tile.rotation;")
                        && renderer.contains("model.renderSpinnyBit(MODEL_SCALE);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
