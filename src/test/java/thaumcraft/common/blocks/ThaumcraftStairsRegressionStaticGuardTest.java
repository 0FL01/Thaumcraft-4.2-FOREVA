package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftStairsRegressionStaticGuardTest {

    @Test
    public void stairsUseSafeDisplayTicksAndHaveInventoryModels() throws IOException {
        String base = read("src/main/java/thaumcraft/common/blocks/BlockThaumcraftStairs.java");
        assertTrue(base.contains("void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)")
                && !base.contains("super.randomDisplayTick"));

        for (String name : new String[]{"ArcaneStone", "Greatwood", "Silverwood", "Eldritch"}) {
            String stairs = read("src/main/java/thaumcraft/common/blocks/BlockStairs" + name + ".java");
            assertTrue("TC stairs must use the safe base: " + name,
                    stairs.contains("extends BlockThaumcraftStairs"));
        }

        String eldritch = read("src/main/java/thaumcraft/common/blocks/BlockStairsEldritch.java");
        assertTrue(eldritch.contains("blockCosmeticSolid.getStateFromMeta(11)"));

        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        for (String model : new String[]{"blockstairsarcanestone", "blockstairsgreatwood",
                "blockstairssilverwood", "blockstairseldritch"}) {
            assertTrue("Missing stair inventory model registration: " + model,
                    proxy.contains("\"" + model + "\""));
        }

        String eldritchItem = read("src/main/resources/assets/thaumcraft/models/item/blockstairseldritch.json");
        assertTrue(eldritchItem.contains("thaumcraft:block/blockstairseldritch"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
