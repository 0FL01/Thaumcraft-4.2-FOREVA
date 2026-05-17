package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyResearchNotesModelStaticGuardTest {

    @Test
    public void clientProxyKeepsResearchNotesDiscoveryModelSplitAndColorRegistration() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String notesModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemresearchnotes.json");
        String discoveryModel = readFile("src/main/resources/assets/thaumcraft/models/item/discovery.json");

        assertTrue("ClientProxy must map itemResearchNotes metas 0..63 to note model and 64..127 to discovery model",
                source.contains("if (item == ConfigItems.itemResearchNotes)")
                        && source.contains("new ModelResourceLocation(new ResourceLocation(\"thaumcraft\", \"discovery\"), \"inventory\")")
                        && source.contains("for (int meta = 0; meta < 64; meta++)")
                        && source.contains("for (int meta = 64; meta < 128; meta++)"));
        assertTrue("ClientProxy must register itemResearchNotes tint handler",
                source.contains("ConfigItems.itemResearchNotes.getColorFromItemStack(stack, tintIndex)"));
        assertTrue("itemresearchnotes model must include overlay layer",
                notesModel.contains("\"layer0\": \"thaumcraft:items/researchnotes\"")
                        && notesModel.contains("\"layer1\": \"thaumcraft:items/researchnotesoverlay\""));
        assertTrue("discovery model must include overlay layer",
                discoveryModel.contains("\"layer0\": \"thaumcraft:items/discovery\"")
                        && discoveryModel.contains("\"layer1\": \"thaumcraft:items/discoveryoverlay\""));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
