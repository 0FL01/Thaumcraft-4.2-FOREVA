package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemResearchNotesCoreContractsStaticGuardTest {

    @Test
    public void researchNotesKeepReferenceConsumptionAndResultContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemResearchNotes.java");

        assertTrue("ItemResearchNotes must keep rarity split and unknown-discovery metas",
                source.contains("META_UNKNOWN_DISCOVERY = 24")
                        && source.contains("META_UNKNOWN_DISCOVERY_ALT = 42")
                        && source.contains("META_DISCOVERY_START = 64")
                        && source.contains("return stack.getItemDamage() >= META_DISCOVERY_START ? EnumRarity.EPIC : EnumRarity.RARE;"));
        assertTrue("ItemResearchNotes must keep PASS result when requisites are missing",
                source.contains("new TextComponentTranslation(\"tc.researcherror\")")
                        && source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue("ItemResearchNotes must consume note stack on successful completion/discovery-fail flows",
                source.contains("stack.shrink(1);")
                        && source.contains("play(world, player, TCSounds.LEARN);")
                        && source.contains("play(world, player, TCSounds.ERASE);"));
        assertTrue("ItemResearchNotes must keep hidden-discovery fallback fragment spawn contract",
                source.contains("ResearchManager.findHiddenResearch(player)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 7 + world.rand.nextInt(3), ItemResource.META_KNOWLEDGE_FRAGMENT)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
