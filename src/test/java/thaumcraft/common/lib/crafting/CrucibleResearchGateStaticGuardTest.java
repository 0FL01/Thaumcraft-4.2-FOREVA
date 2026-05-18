package thaumcraft.common.lib.crafting;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CrucibleResearchGateStaticGuardTest {

    @Test
    public void crucibleAndThaumatoriumResearchGuardsRemainWired() throws IOException {
        String managerSource = readFile("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");
        String containerSource = readFile("src/main/java/thaumcraft/common/container/ContainerThaumatorium.java");

        assertTrue("Crucible matching must enforce research completion",
                managerSource.contains("ResearchManager.isResearchComplete(username, recipe.key)"));
        assertTrue("Crucible matching must keep aspect-count specificity tie-break",
                managerSource.contains("int result = recipe.aspects.size();"));
        assertTrue("Thaumatorium programming must enforce research completion",
                containerSource.contains("ResearchManager.isResearchComplete(this.player.getName(), recipe.key)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
