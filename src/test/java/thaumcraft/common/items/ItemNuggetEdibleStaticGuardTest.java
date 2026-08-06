package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemNuggetEdibleStaticGuardTest {

    @Test
    public void itemNuggetEdibleKeepsReferenceItemsAndLegacyCarrierContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemNuggetEdible.java");

        assertTrue("ItemNuggetEdible should use reference saturation baseline",
                source.contains("super(1, 0.3f, false)"));
        assertTrue("ItemNuggetEdible should support separate TC4 items and the hidden legacy subtype carrier",
                source.contains("public ItemNuggetEdible(boolean legacySubtypes)")
                        && source.contains("this.setHasSubtypes(legacySubtypes)")
                        && source.contains("if (!legacySubtypes)")
                        && source.contains("this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft)"));
        assertTrue("ItemNuggetEdible should keep 10-tick consume duration",
                source.contains("public int getMaxItemUseDuration(ItemStack stack)")
                        && source.contains("return 10;"));
        assertTrue("ItemNuggetEdible should keep reference nugget subtype names",
                source.contains("\"nuggetchicken\"")
                        && source.contains("\"nuggetbeef\"")
                        && source.contains("\"nuggetpork\"")
                        && source.contains("\"nuggetfish\""));
        assertFalse("ItemNuggetEdible should require normal hunger",
                source.contains("setAlwaysEdible"));
        assertFalse("ItemNuggetEdible should not apply non-reference speed buff on eat",
                source.contains("MobEffects.SPEED"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
