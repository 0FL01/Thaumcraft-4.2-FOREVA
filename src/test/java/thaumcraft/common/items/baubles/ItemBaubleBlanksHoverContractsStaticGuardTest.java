package thaumcraft.common.items.baubles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemBaubleBlanksHoverContractsStaticGuardTest {

    @Test
    public void baubleBlanksAndHoverGirdleKeepReferenceUiAndFallContracts() throws IOException {
        String blanks = readFile("src/main/java/thaumcraft/common/items/baubles/ItemBaubleBlanks.java");
        String hover = readFile("src/main/java/thaumcraft/common/items/baubles/ItemGirdleHover.java");

        assertTrue("ItemBaubleBlanks must keep aspect-ring display name and tooltip localization contracts",
                blanks.contains("I18n.translateToLocal(\"item.ItemBaubleBlanks.3.name\")")
                        && blanks.contains("I18n.translateToLocal(\"tc.discount\")")
                        && blanks.contains("tooltip.add(TextFormatting.DARK_PURPLE + aspect.getName() + \" \" + I18n.translateToLocal(\"tc.discount\") + \": 1%\");"));
        assertTrue("ItemGirdleHover must keep subtype-enabled setup and unclamped fall-distance reduction",
                hover.contains("this.setHasSubtypes(true);")
                        && hover.contains("player.fallDistance -= 0.33F;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
