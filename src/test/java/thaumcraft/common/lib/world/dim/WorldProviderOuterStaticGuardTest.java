package thaumcraft.common.lib.world.dim;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WorldProviderOuterStaticGuardTest {

    @Test
    public void worldProviderOuterKeepsReferenceFogAndWaterVaporContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java");

        assertTrue("WorldProviderOuter must keep reference-shaped purple fog baseline",
                source.contains("int color = 0xA080A0;")
                        && source.contains("red *= 0.15F;")
                        && source.contains("green *= 0.15F;")
                        && source.contains("blue *= 0.15F;")
                        && source.contains("return new Vec3d(red, green, blue);"));
        assertTrue("WorldProviderOuter must keep non-hell water-vaporization contract",
                source.contains("public boolean doesWaterVaporize()")
                        && source.contains("return false;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
