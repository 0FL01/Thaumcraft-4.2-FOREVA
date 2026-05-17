package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileInfusionPillarStaticGuardTest {

    @Test
    public void tileInfusionPillarShouldKeepOrientationNbtAndRenderBoundsContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileInfusionPillar.java");

        assertTrue(source.contains("public byte orientation = 0;"));
        assertTrue(source.contains("public AxisAlignedBB getRenderBoundingBox()"));
        assertTrue(source.contains("this.pos.getX() - 1"));
        assertTrue(source.contains("this.pos.getY() + 2"));
        assertTrue(source.contains("this.orientation = nbt.getByte(\"orientation\");"));
        assertTrue(source.contains("nbt.setByte(\"orientation\", this.orientation);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
