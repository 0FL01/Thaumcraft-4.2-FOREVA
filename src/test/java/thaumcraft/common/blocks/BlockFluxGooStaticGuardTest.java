package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockFluxGooStaticGuardTest {

    @Test
    public void fluxGooShouldUseVanillaLiquidLevelPropertyForWaterMaterial() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockFluxGoo.java");

        assertTrue("Material.WATER blocks must expose BlockLiquid.LEVEL or vanilla water movement crashes on getValue(BlockLiquid.LEVEL)",
                source.contains("super(Material.WATER);")
                        && source.contains("public static final PropertyInteger LEVEL = BlockLiquid.LEVEL;")
                        && source.contains("return MathHelper.clamp(state.getValue(LEVEL), 0, 7);")
                        && !source.contains("PropertyInteger.create(\"level\", 0, 7)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
