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
        assertTrue("PacketScannedToServer must require an authoritative held-thaumometer match before routing scans through ScanManager.completeScan",
                packet.contains("if (player == null || !\"@\".equals(normalizePrefix(prefix)))")
                        && packet.contains("if (player == null || player.getEntityId() != playerid) return false;")
                        && packet.contains("if (player.world.provider.getDimension() != dim) return false;")
                        && packet.contains("getHeldThaumometerScan(player, player.getHeldItemMainhand()")
                        && packet.contains("getHeldThaumometerScan(player, player.getHeldItemOffhand()")
                        && packet.contains("ItemThaumometer thaumometer = (ItemThaumometer) held.getItem();")
                        && packet.contains("ScanResult authoritative = thaumometer.findScanTarget(held, player.world, player);")
                        && packet.contains("return matchesPayload(authoritative, type, id, md, entityid, phenomena) ? authoritative : null;")
                        && packet.contains("if (result == null || !ScanManager.completeScan(player, result, normalizedPrefix))")
                        && packet.contains("if (player instanceof EntityPlayerMP)")
                        && packet.contains("syncKnowledge((EntityPlayerMP) player);"));
    }

    @Test
    public void hiddenDiscoveryFlowShouldKeepClueStateAndNoteGatingContracts() throws IOException {
        String researchManager = readFile("src/main/java/thaumcraft/common/lib/research/ResearchManager.java");
        String notes = readFile("src/main/java/thaumcraft/common/items/ItemResearchNotes.java");
        String completePacket = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java");

        assertTrue("ResearchManager.findHiddenResearch must keep hidden-trigger filtering and prerequisite gating",
                researchManager.contains("if (allHiddenResearch == null)")
                        && researchManager.contains("research.isHidden() && hasUsableResearchTags(research)")
                        && researchManager.contains("if (!doesPlayerHaveRequisites(player, research.key)) continue;")
                        && researchManager.contains("if (research.getItemTriggers() == null && research.getEntityTriggers() == null && research.getAspectTriggers() == null)")
                        && researchManager.contains("if (candidates.isEmpty()) return \"FAIL\";"));
        assertTrue("ResearchManager.createClue must grant only @KEY clue state for hidden/lost research triggers",
                researchManager.contains("if (!research.isHidden() && !research.isLost()) continue;")
                        && researchManager.contains("if (isResearchComplete(player, \"@\" + research.key)) continue;")
                        && researchManager.contains("addResearch(player, \"@\" + key);"));
        assertTrue("ItemResearchNotes discovery reveal must convert clue items into notes and keep FAIL fallback fragments",
                notes.contains("String key = ResearchManager.findHiddenResearch(player);")
                        && notes.contains("if (\"FAIL\".equals(key))")
                        && notes.contains("new ItemStack(ConfigItems.itemResource, 7 + world.rand.nextInt(3), ItemResource.META_KNOWLEDGE_FRAGMENT)")
                        && notes.contains("ItemStack note = ResearchManager.createNote(stack, key, world);")
                        && notes.contains("stack.setItemDamage(0);")
                        && notes.contains("if (!ResearchManager.doesPlayerHaveRequisites(player, data.key))"));
        assertTrue("PacketPlayerCompleteToServer must keep primary note-creation and secondary direct-completion split",
                completePacket.contains("if (type == 0 && research.isSecondary())")
                        && completePacket.contains("return consumeResearchCost(player, research) && completeResearch(player, research);")
                        && completePacket.contains("if (type == 1 && !research.isSecondary())")
                        && completePacket.contains("return !ResearchManager.createResearchNoteForPlayer(player.world, player, key).isEmpty();")
                        && completePacket.contains("if (!ResearchManager.doesPlayerHaveRequisites(player, key))"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
