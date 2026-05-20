package thaumcraft.common.lib.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ResearchManagerFindMatchingResearchStaticGuardTest {

    @Test
    public void findMatchingResearchKeepsReferenceSelectionGuards() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/research/ResearchManager.java");
        assertTrue("ResearchManager should expose findMatchingResearch(EntityPlayer, Aspect) helper",
                source.contains("public static String findMatchingResearch(EntityPlayer player, Aspect aspect)"));
        assertTrue("findMatchingResearch should keep reference candidate filters for secondary/hidden/lost/autounlock/virtual/stub research",
                source.contains("research.isSecondary() && (Config.researchDifficulty == 0 || Config.researchDifficulty == -1)")
                        && source.contains("research.isHidden()")
                        && source.contains("research.isLost()")
                        && source.contains("research.isAutoUnlock()")
                        && source.contains("research.isVirtual()")
                        && source.contains("research.isStub()"));
        assertTrue("findMatchingResearch should keep prerequisite and aspect-tag gating before random key pick",
                source.contains("doesPlayerHaveRequisites(player, research.key)")
                        && source.contains("research.tags.getAmount(aspect) <= 0")
                        && source.contains("return keys.get(player.world.rand.nextInt(keys.size()));"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
