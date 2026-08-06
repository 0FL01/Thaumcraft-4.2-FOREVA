package thaumcraft.common.config;

import java.io.IOException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigRecipesGolemStoneStaticGuardTest {
    @Test
    public void stoneGolemUsesStoneBrickCatalyst() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();
        int start = source.indexOf("ConfigResearch.recipes.put(\"GolemStone\"");
        int end = source.indexOf("ConfigResearch.recipes.put(\"GolemIron\"", start);
        assertTrue(start >= 0 && end > start);

        String recipe = source.substring(start, end);
        assertTrue(recipe.contains("new ItemStack(Blocks.STONEBRICK)"));
        assertFalse(recipe.contains("new ItemStack(Blocks.STONE)"));
    }
}
