package thaumcraft.common.lib.world;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class WorldGenMoundTemplateParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void compactTemplateMatchesTheTc4AuthoredPlacementCorpus() throws Exception {
        StringBuilder encoded = new StringBuilder();
        int placements = 0;
        assertEquals(16, WorldGenMoundTemplate.LAYERS.length);
        for (String layer : WorldGenMoundTemplate.LAYERS) {
            assertEquals(19 * 19, layer.length());
            encoded.append(layer);
            for (int i = 0; i < layer.length(); i++) {
                if (layer.charAt(i) != '.') {
                    placements++;
                }
            }
        }
        assertEquals(2441, placements);
        assertEquals("ad7549f625376651bf203a480ac9a5339897e3735cbdd11714439a20445c93bc",
                sha256(encoded.toString()));
    }

    @Test
    public void authoredTopologyConsumesNoRandomValuesBeforeLoot() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/world/WorldGenMound.java");
        int generate = source.indexOf("public boolean generate(World world, Random rand, BlockPos pos)");
        int template = source.indexOf("WorldGenMoundTemplate.place(world, pos);", generate);
        int loot = source.indexOf("placeLoot(world, rand, pos);", template);
        assertTrue(generate >= 0 && template > generate && loot > template);
        assertTrue(!source.substring(template, loot).contains("rand."));
    }

    @Test
    public void palettePreservesEveryLegacyBlockAndMetadataState() {
        assertSame(Blocks.AIR, WorldGenMoundTemplate.stateFor('A').getBlock());
        assertSame(Blocks.DIRT, WorldGenMoundTemplate.stateFor('D').getBlock());
        assertSame(Blocks.COBBLESTONE, WorldGenMoundTemplate.stateFor('C').getBlock());
        assertSame(Blocks.GRASS, WorldGenMoundTemplate.stateFor('G').getBlock());
        assertSame(Blocks.MOSSY_COBBLESTONE, WorldGenMoundTemplate.stateFor('M').getBlock());
        assertSame(Blocks.IRON_BARS, WorldGenMoundTemplate.stateFor('I').getBlock());
        assertMeta('B', Blocks.STONEBRICK.getStateFromMeta(3));
        assertMeta('L', Blocks.LADDER.getStateFromMeta(5));
        assertMeta('T', Blocks.TALLGRASS.getStateFromMeta(1));
        for (int meta = 0; meta < 8; meta++) {
            assertMeta((char) ('0' + meta), Blocks.STONE_STAIRS.getStateFromMeta(meta));
        }
    }

    private static void assertMeta(char symbol, IBlockState expected) {
        IBlockState actual = WorldGenMoundTemplate.stateFor(symbol);
        assertSame(expected.getBlock(), actual.getBlock());
        assertEquals(expected.getBlock().getMetaFromState(expected), actual.getBlock().getMetaFromState(actual));
    }

    private static String sha256(String value) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte part : digest) {
            result.append(String.format("%02x", part & 0xff));
        }
        return result.toString();
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
