package thaumcraft.common.items.wands;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FocusPouchRegistrationParityStaticGuardTest {

    @Test
    public void configRegistersOneCanonicalBeltPouch() throws IOException {
        String config = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String bauble = read("src/main/java/thaumcraft/common/items/wands/ItemFocusPouchBauble.java");

        assertTrue("The canonical field must keep the shared pouch base type",
                config.contains("public static ItemFocusPouch itemFocusPouch;"));
        assertTrue("The canonical FocusPouch registry entry must instantiate the Baubles subclass",
                config.contains("itemFocusPouch = (ItemFocusPouch) new ItemFocusPouchBauble()")
                        && config.contains("ConfigBlocks.legacyPath(\"FocusPouch\")")
                        && config.contains("allItems.add(itemFocusPouch);"));
        assertFalse("The port must not register a second FocusPouchBauble item",
                config.contains("itemFocusPouchBauble")
                        || config.contains("ConfigBlocks.legacyPath(\"FocusPouchBauble\")"));
        assertTrue("The registered pouch must inherit pouch storage and use the belt slot",
                bauble.contains("class ItemFocusPouchBauble extends ItemFocusPouch implements IBauble")
                        && bauble.contains("return BaubleType.BELT;"));
    }

    @Test
    public void canonicalOutputsAndResourcesUseTheVisiblePouch() throws IOException {
        String recipes = read("src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java");
        String research = read("src/main/java/thaumcraft/common/config/research/ConfigResearchThaumaturgy.java");
        String pech = read("src/main/java/thaumcraft/common/entities/ContainerPech.java");
        String model = read("src/main/resources/assets/thaumcraft/models/item/focuspouch.json");
        String language = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("The arcane recipe must output the canonical pouch",
                recipes.contains("new ItemStack(ConfigItems.itemFocusPouch)"));
        assertTrue("The research icon must use the canonical pouch",
                research.contains("new ItemStack(ConfigItems.itemFocusPouch)"));
        assertTrue("Pech trades must use the canonical pouch",
                pech.contains("new ItemStack(ConfigItems.itemFocusPouch)"));
        assertTrue("The canonical item model must use the visible original pouch texture",
                model.contains("\"layer0\": \"thaumcraft:items/focuspouch\""));
        assertFalse("The removed second item must not keep a model route",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/item/focuspouchbauble.json")));
        assertFalse("The removed second item must not keep a localization route",
                language.contains("item.thaumcraft.focus_pouch_bauble.name"));

        Path originalTexture = Paths.get("thaumcraft_src/assets/thaumcraft/textures/items/focuspouch.png");
        Path portTexture = Paths.get("src/main/resources/assets/thaumcraft/textures/items/focuspouch.png");
        assertArrayEquals("The canonical texture must match the original TC4 asset",
                Files.readAllBytes(originalTexture), Files.readAllBytes(portTexture));
        BufferedImage image = ImageIO.read(portTexture.toFile());
        assertNotNull("The canonical pouch texture must be a readable image", image);
        assertTrue("The canonical pouch texture must contain visible pixels", hasVisiblePixel(image));
    }

    private static boolean hasVisiblePixel(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) != 0) return true;
            }
        }
        return false;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
