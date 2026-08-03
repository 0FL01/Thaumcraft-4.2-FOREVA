package thaumcraft.common.clientguard;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ResearchCompletionFeedbackStaticGuardTest {

    @Test
    public void incrementalResearchPacketRestoresReferenceFeedbackAndBrowserState() throws IOException {
        String packet = read("src/main/java/thaumcraft/common/lib/network/playerdata/PacketResearchComplete.java");
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String popup = read("src/main/java/thaumcraft/client/gui/GuiResearchPopup.java");
        String ticks = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(packet.contains("knowledge.addResearch(researchKey);")
                && packet.contains("Thaumcraft.proxy.notifyResearchComplete(researchKey);"));
        assertTrue(proxy.contains("if (researchKey.startsWith(\"@\"))")
                && proxy.contains("PlayerNotifications.addNotification")
                && proxy.contains("\"tc.addclue\"")
                && proxy.contains("player.playSound(TCSounds.LEARN")
                && proxy.contains("!research.isVirtual()")
                && proxy.contains("queueResearchInformation(research)")
                && proxy.contains("GuiResearchBrowser.highlightedItem.add(researchKey)")
                && proxy.contains("GuiResearchBrowser.highlightedItem.add(research.category)")
                && proxy.contains("GuiResearchBrowser.syncClientKnowledgeCache(player)")
                && proxy.contains("((GuiResearchBrowser) mc.currentScreen).updateResearch();"));
        assertTrue(popup.contains("class GuiResearchPopup extends Gui")
                && popup.contains("DISPLAY_TIME_MS = 3000L")
                && popup.contains("Research Completed!")
                && popup.contains("renderItemAndEffectIntoGUI")
                && popup.contains("UtilsFX.drawTexturedQuadFull"));
        assertTrue(ticks.contains("public static GuiResearchPopup researchPopup;")
                && ticks.contains("researchPopup.updateResearchWindow();"));
        assertTrue(lang.contains("tc.addclue=You have discovered a clue to new research!"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
