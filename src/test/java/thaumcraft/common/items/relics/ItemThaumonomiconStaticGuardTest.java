package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemThaumonomiconStaticGuardTest {

    @Test
    public void thaumonomiconKeepsCheatSheetAndSiblingUnlockContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/relics/ItemThaumonomicon.java");

        assertTrue("Thaumonomicon must keep optional cheat-sheet sub-item and EPIC rarity for meta 42",
                source.contains("if (Config.allowCheatSheet)")
                        && source.contains("new ItemStack(this, 1, 42)")
                        && source.contains("if (stack.getItemDamage() == 42)")
                        && source.contains("return EnumRarity.EPIC;"));
        assertTrue("Thaumonomicon must keep server unlock routing with cheat-sheet and sibling paths",
                source.contains("if (Config.allowCheatSheet && stack.getItemDamage() == 42)")
                        && source.contains("unlockAllResearch(player);")
                        && source.contains("unlockAllAspects(player);")
                        && source.contains("unlockCompletedResearchSiblings(player);"));
        assertTrue("Thaumonomicon must keep sibling completion and full player-data resync",
                source.contains("research.siblings == null")
                        && source.contains("ResearchCategories.getResearch(sibling) != null")
                        && source.contains("ResearchManager.addResearch(player, sibling)")
                        && source.contains("EventHandlerEntity.syncAllData(player);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
