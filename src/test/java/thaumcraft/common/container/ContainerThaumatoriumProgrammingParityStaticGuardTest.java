package thaumcraft.common.container;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContainerThaumatoriumProgrammingParityStaticGuardTest {

    @Test
    public void containerThaumatoriumKeepsReferenceProgrammingAndResearchGateFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/container/ContainerThaumatorium.java");

        assertTrue("Thaumatorium recipe list should gate program candidates by research completion and catalyst match",
                source.contains("ResearchManager.isResearchComplete(this.player.getName(), recipe.key)")
                        && source.contains("&& recipe.catalystMatches(this.thaumatorium.inputStack)"));
        assertTrue("Thaumatorium should preserve recipe re-toggle removal path",
                source.contains("this.thaumatorium.recipeHash.remove(i);")
                        && source.contains("this.thaumatorium.recipeEssentia.remove(i);")
                        && source.contains("this.thaumatorium.recipePlayer.remove(i);"));
        assertTrue("Thaumatorium should preserve add-path recipe payload writes",
                source.contains("this.thaumatorium.recipeEssentia.add(recipe.aspects.copy());")
                        && source.contains("this.thaumatorium.recipePlayer.add(playerIn.getName());")
                        && source.contains("this.thaumatorium.recipeHash.add(recipe.hash);"));
        assertFalse("Thaumatorium container should not hard-cap programming by maxRecipes in enchantItem",
                source.contains("this.thaumatorium.recipeHash.size() < this.thaumatorium.maxRecipes"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
