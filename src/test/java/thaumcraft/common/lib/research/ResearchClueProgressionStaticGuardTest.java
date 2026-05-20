package thaumcraft.common.lib.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ResearchClueProgressionStaticGuardTest {

    @Test
    public void scanPipelineShouldKeepHiddenClueCreationBoundary() throws IOException {
        String scanManager = readFile("src/main/java/thaumcraft/common/lib/research/ScanManager.java");
        String packet = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketScannedToServer.java");

        assertTrue("ScanManager.completeScan must keep awarded-aspect + clue handoff into ResearchManager.createClue",
                scanManager.contains("AspectList awardedAspects = new AspectList();")
                        && scanManager.contains("Object clue = createScanClue(scan);")
                        && scanManager.contains("ResearchManager.createClue(player.world, player, clue, awardedAspects);")
                        && scanManager.contains("ResearchManager.updateCache(player.getName(), knowledge);"));
        assertTrue("PacketScannedToServer must rebuild ScanResult server-side and route scans through ScanManager.completeScan",
                packet.contains("if (this.type == 1)")
                        && packet.contains("else if (this.type == 2)")
                        && packet.contains("else if (this.type == 3)")
                        && packet.contains("if (result != null && ScanManager.completeScan(player, result, this.prefix))")
                        && packet.contains("syncKnowledge(player);"));
    }

    @Test
    public void hiddenDiscoveryFlowShouldKeepClueStateAndNoteGatingContracts() throws IOException {
        String researchManager = readFile("src/main/java/thaumcraft/common/lib/research/ResearchManager.java");
        String notes = readFile("src/main/java/thaumcraft/common/items/ItemResearchNotes.java");
        String completePacket = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java");

        assertTrue("ResearchManager.findHiddenResearch must keep hidden-trigger filtering and prerequisite gating",
                researchManager.contains("if (allHiddenResearch == null)")
                        && researchManager.contains("research.isHidden() && hasUsableResearchTags(research)")
                        && researchManager.contains("if (!doesPlayerHaveRequisites(player.getName(), research.key)) continue;")
                        && researchManager.contains("if (research.getItemTriggers() == null && research.getEntityTriggers() == null && research.getAspectTriggers() == null)")
                        && researchManager.contains("if (candidates.isEmpty()) return \"FAIL\";"));
        assertTrue("ResearchManager.createClue must grant only @KEY clue state for hidden/lost research triggers",
                researchManager.contains("if (!research.isHidden() && !research.isLost()) continue;")
                        && researchManager.contains("if (isResearchComplete(player.getName(), \"@\" + research.key)) continue;")
                        && researchManager.contains("addResearch(player, \"@\" + key);"));
        assertTrue("ItemResearchNotes discovery reveal must convert clue items into notes and keep FAIL fallback fragments",
                notes.contains("String key = ResearchManager.findHiddenResearch(player);")
                        && notes.contains("if (\"FAIL\".equals(key))")
                        && notes.contains("new ItemStack(ConfigItems.itemResource, 7 + world.rand.nextInt(3), ItemResource.META_KNOWLEDGE_FRAGMENT)")
                        && notes.contains("ItemStack note = ResearchManager.createNote(stack, key, world);")
                        && notes.contains("stack.setItemDamage(0);"));
        assertTrue("PacketPlayerCompleteToServer must keep primary note-creation and secondary direct-completion split",
                completePacket.contains("if (this.type == 0 && research.isSecondary())")
                        && completePacket.contains("completed = consumeResearchCost(player, research) && completeResearch(player, research);")
                        && completePacket.contains("} else if (this.type == 1 && !research.isSecondary())")
                        && completePacket.contains("completed = !ResearchManager.createResearchNoteForPlayer(player.world, player, this.key).isEmpty();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
