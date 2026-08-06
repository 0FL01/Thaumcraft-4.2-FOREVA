package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AlchemyClientParityStaticGuardTest {

    @Test
    public void researchRecipesShouldSupportTc4LinksAndDepthAlignedCompounds() throws IOException {
        String source = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(source.contains("private static final LinkedList<Object[]> HISTORY"));
        assertTrue(source.contains("ThaumcraftApi.getCraftingRecipeKey(this.mc.player, stack)"));
        assertTrue(source.contains("HISTORY.push(new Object[]{this.research.key, this.page})"));
        assertTrue(source.contains("Object[] previous = HISTORY.pop()"));
        assertTrue(source.contains("I18n.format(\"recipe.clickthrough\")"));
        assertTrue(source.contains("I18n.format(\"recipe.return\")"));
        assertTrue(source.contains("(-119 + groundTerm + dy * 50) * scale"));
        assertTrue(source.contains("2.0F * scale"));
        assertTrue(lang.contains("recipe.return=Return"));
        assertTrue(lang.contains("recipe.clickthrough=Click for research"));
    }

    @Test
    public void spaShouldTileSixFluidRowsAndMaskTheUnfilledHeight() throws IOException {
        String source = read("src/main/java/thaumcraft/client/gui/GuiSpa.java");

        assertTrue(source.contains("for (int row = 0; row < 6; ++row)"));
        assertTrue(source.contains("this.guiTop + 15 + row * 8, sprite, 8, 8"));
        assertTrue(source.contains("107, 15, 10, 48 - fill"));
    }

    @Test
    public void jarLabelModelShouldTintItsAspectOverlay() throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/item/itemresource_label.json");
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(model.contains("\"layer1\": \"thaumcraft:items/label_over\""));
        assertTrue(proxy.contains("tintIndex != 1"));
        assertTrue(proxy.contains("ItemResource.META_LABEL"));
        assertTrue(proxy.contains("labels[0].getColor()"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
