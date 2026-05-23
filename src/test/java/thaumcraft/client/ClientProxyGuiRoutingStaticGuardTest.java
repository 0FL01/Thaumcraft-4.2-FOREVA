package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyGuiRoutingStaticGuardTest {

    @Test
    public void clientProxyShouldKeepArcaneWorkbenchGuiRouting() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep GUI_ARCANE_WORKBENCH id constant",
                commonProxySource.contains("public static final int GUI_ARCANE_WORKBENCH = 13;"));
        assertTrue("ClientProxy should keep Arcane Workbench GUI switch case",
                clientProxySource.contains("case GUI_ARCANE_WORKBENCH:"));
        assertTrue("ClientProxy should route TileArcaneWorkbench to GuiArcaneWorkbench",
                clientProxySource.contains("tile instanceof TileArcaneWorkbench")
                        && clientProxySource.contains("new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)"));
        assertTrue("ClientProxy should keep WorldClient guard around GUI routing",
                clientProxySource.contains("if (!(world instanceof WorldClient)) {")
                        && clientProxySource.contains("return null;"));
    }

    @Test
    public void clientProxyShouldKeepResearchBrowserAndFocalManipulatorGuiRouting() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String browserSource = readFile("src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java");
        String recipeSource = readFile("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue("ClientProxy should keep Thaumonomicon GUI route to GuiResearchBrowser",
                clientProxySource.contains("case GUI_THAUMONOMICON:")
                        && clientProxySource.contains("return new GuiResearchBrowser();"));
        assertTrue("GuiResearchBrowser must keep research cache, purchase packets and recipe-book transition",
                browserSource.contains("public static HashMap<String, ArrayList<String>> completedResearch = new HashMap<>();")
                        && browserSource.contains("player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null)")
                        && browserSource.contains("new PacketPlayerCompleteToServer(")
                        && browserSource.contains("(byte) 0")
                        && browserSource.contains("(byte) 1")
                        && browserSource.contains("this.mc.displayGuiScreen(new GuiResearchRecipe(this.currentHighlight, 0, this.guiMapX, this.guiMapY));"));
        assertTrue("GuiResearchRecipe must preserve browser return path and map-position constructor",
                recipeSource.contains("public GuiResearchRecipe(ResearchItem research, int page, double guiMapX, double guiMapY)")
                        && recipeSource.contains("this.mc.displayGuiScreen(new GuiResearchBrowser(this.guiMapX, this.guiMapY));"));
        assertTrue("ClientProxy should keep focal manipulator GUI route",
                clientProxySource.contains("case GUI_FOCAL_MANIPULATOR:")
                        && clientProxySource.contains("new GuiFocalManipulator(player.inventory, (TileFocalManipulator) tile)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
