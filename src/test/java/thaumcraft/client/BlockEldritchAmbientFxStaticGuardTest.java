package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockEldritchAmbientFxStaticGuardTest {

    @Test
    public void eldritchLockAndTrapShouldKeepReferenceAmbientFxAndTrapTextureVariation() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockEldritch.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockeldritch.json");
        String trap0 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_10.json");
        String trap1 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_10_1.json");
        String trap2 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_10_2.json");
        String trap3 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_10_3.json");

        assertTrue("BlockEldritch should restore the reference client ambient path for lock sparkles and trap rune drift",
                block.contains("public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)")
                        && block.contains("if (meta == 8) {")
                        && block.contains("tile instanceof TileEldritchLock && ((TileEldritchLock) tile).count >= 0")
                        && block.contains("Thaumcraft.proxy.spark(")
                        && block.contains("0.65F + rand.nextFloat() * 0.1F")
                        && block.contains("if (meta != 10) {")
                        && block.contains("world.isAirBlock(sparkPos)")
                        && block.contains("Thaumcraft.proxy.blockRunes(")
                        && block.contains("0.5F + rand.nextFloat() * 0.5F")
                        && block.contains("rand.nextFloat() * 0.3F")
                        && block.contains("0.9F + rand.nextFloat() * 0.1F")
                        && block.contains("16 + rand.nextInt(4)"));

        assertTrue("Trap blockstate should keep four randomized shell variants instead of a single fixed placeholder texture",
                blockstate.contains("\"type=10\": [")
                        && blockstate.contains("\"model\": \"thaumcraft:blockeldritch_10\"")
                        && blockstate.contains("\"model\": \"thaumcraft:blockeldritch_10_1\"")
                        && blockstate.contains("\"model\": \"thaumcraft:blockeldritch_10_2\"")
                        && blockstate.contains("\"model\": \"thaumcraft:blockeldritch_10_3\""));

        assertTrue("Trap shell variants should cover the original es_5..es_8 texture family",
                trap0.contains("\"all\": \"thaumcraft:blocks/es_5\"")
                        && trap1.contains("\"all\": \"thaumcraft:blocks/es_6\"")
                        && trap2.contains("\"all\": \"thaumcraft:blocks/es_7\"")
                        && trap3.contains("\"all\": \"thaumcraft:blocks/es_8\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
