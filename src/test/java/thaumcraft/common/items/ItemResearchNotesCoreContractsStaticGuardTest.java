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
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemResearchNotes must keep rarity split and unknown-discovery metas",
                source.contains("META_UNKNOWN_DISCOVERY = 24")
                        && source.contains("META_UNKNOWN_DISCOVERY_ALT = 42")
                        && source.contains("META_DISCOVERY_START = 64")
                        && source.contains("return stack.getItemDamage() >= META_DISCOVERY_START ? EnumRarity.EPIC : EnumRarity.RARE;"));
        assertTrue("ItemResearchNotes must keep reference display/glint surface",
                source.contains("public String getItemStackDisplayName(ItemStack stack)")
                        && source.contains("I18n.translateToLocal(\"item.discovery.name\")")
                        && source.contains("I18n.translateToLocal(\"item.researchnotes.name\")")
                        && source.contains("public boolean hasEffect(ItemStack stack)")
                        && source.contains("return false;"));
        assertTrue("ItemResearchNotes must keep overlay tint contract for note/discovery render passes",
                source.contains("public int getColorFromItemStack(ItemStack stack, int tintIndex)")
                        && source.contains("if (tintIndex == 1)")
                        && source.contains("int color = 0x999999;")
                        && source.contains("return -1;"));
        assertTrue("ItemResearchNotes must keep reference tooltip contracts for unknown and forbidden warp clues",
                source.contains("item.researchnotes.unknown.1")
                        && source.contains("item.researchnotes.unknown.2")
                        && source.contains("ThaumcraftApi.getWarp(data.key)")
                        && source.contains("I18n.translateToLocal(\"tc.forbidden\")")
                        && source.contains("I18n.translateToLocal(\"tc.forbidden.level.\" + warp)"));
        assertTrue("ItemResearchNotes must keep PASS result when requisites are missing",
                source.contains("new TextComponentTranslation(\"tc.researcherror\")")
                        && source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue("ItemResearchNotes must keep PASS result on invalid hidden-discovery note fallback",
                source.contains("new TextComponentTranslation(\"tc.researchnotes.invalid\")")
                        && source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue("ItemResearchNotes must consume note stack on successful completion/discovery-fail flows",
                source.contains("stack.shrink(1);")
                        && source.contains("play(world, player, TCSounds.LEARN);")
                        && source.contains("play(world, player, TCSounds.ERASE);"));
        assertTrue("ItemResearchNotes must keep hidden-discovery fallback fragment spawn contract",
                source.contains("ResearchManager.findHiddenResearch(player)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 7 + world.rand.nextInt(3), ItemResource.META_KNOWLEDGE_FRAGMENT)"));
        assertTrue("Research notes localization keys must stay available for display/unknown/discovery labels",
                lang.contains("item.researchnotes.name=Research Notes")
                        && lang.contains("item.researchnotes.unknown.1=Unknown Knowledge")
                        && lang.contains("item.researchnotes.unknown.2=<Right click to reveal>")
                        && lang.contains("item.discovery.name=Discovery"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
