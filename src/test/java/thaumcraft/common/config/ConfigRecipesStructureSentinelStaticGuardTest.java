package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigRecipesStructureSentinelStaticGuardTest {

    @Test
    public void compoundStructuresKeepTc4VisibleMarkersAndRealNullCells() throws IOException {
        String source = read("src/main/java/thaumcraft/common/config/ConfigRecipes.java");

        assertTrue(source.contains("ItemStack structureMarker = new ItemStack(ConfigBlocks.blockHole, 1, 15);"));
        assertStructure(source, "InfernalFurnace", "InfusionAltar", 1, 0);
        assertStructure(source, "InfusionAltar", "NodeJar", 4, 13);
        assertStructure(source, "AdvAlchemyFurnace", "if (Thaumcraft.proxy", 1, 0);
    }

    @Test
    public void markerUsesDedicatedGeneratedMetaFifteenModel() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String model = read("src/main/resources/assets/thaumcraft/models/item/blockhole_empty.json");

        assertTrue(proxy.contains("registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockHole), 0, \"blockhole\");"));
        assertTrue(proxy.contains("registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockHole), 15, \"blockhole_empty\");"));
        assertTrue(model.contains("\"parent\": \"item/generated\""));
        assertTrue(model.contains("\"layer0\": \"thaumcraft:blocks/empty\""));
    }

    private static void assertStructure(String source, String key, String next,
            int markers, int nulls) {
        int start = source.indexOf("ConfigResearch.recipes.put(\"" + key + "\"");
        int end = source.indexOf(next, start + 1);
        assertTrue("Missing structure recipe " + key, start >= 0 && end > start);
        String section = source.substring(start, end);
        assertEquals(key + " marker count", markers, occurrences(section, "structureMarker"));
        assertEquals(key + " null-cell count", nulls, occurrences(section, "null"));
        assertFalse(key + " must not collapse visible markers to empty stacks", section.contains("ItemStack.EMPTY"));
    }

    private static int occurrences(String text, String needle) {
        int count = 0;
        for (int at = 0; (at = text.indexOf(needle, at)) >= 0; at += needle.length()) {
            count++;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
