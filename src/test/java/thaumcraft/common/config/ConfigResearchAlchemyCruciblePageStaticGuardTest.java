package thaumcraft.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigResearchAlchemyCruciblePageStaticGuardTest {

    @Test
    public void crucibleResearchKeepsOrderedBalancedShardRecipePage() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/config/research/ConfigResearchAlchemy.java")),
                StandardCharsets.UTF_8);
        int textFour = source.indexOf("new ResearchPage(\"tc.research_page.CRUCIBLE.4\")");
        int recipePage = source.indexOf("new ResearchPage(new CrucibleRecipe[]{", textFour);
        int textFive = source.indexOf("new ResearchPage(\"tc.research_page.CRUCIBLE.5\")", recipePage);

        assertTrue("Balanced shard recipe page must remain between CRUCIBLE text pages 4 and 5",
                textFour >= 0 && recipePage > textFour && textFive > recipePage);
        int previous = recipePage;
        for (int metadata = 0; metadata < 6; metadata++) {
            int current = source.indexOf("ConfigResearch.recipeCrucible(\"BalancedShard_" + metadata + "\")",
                    previous);
            assertTrue("Missing or misordered BalancedShard_" + metadata + " page recipe",
                    current > previous && current < textFive);
            previous = current;
        }
    }
}
