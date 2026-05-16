package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemPrimalArrowStaticGuardTest {

    @Test
    public void primalArrowKeepsSixSubtypeCreativeVariantContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemPrimalArrow.java");

        assertTrue("ItemPrimalArrow must keep subtype/maxstack setup contracts",
                source.contains("this.setMaxStackSize(64);")
                        && source.contains("this.setHasSubtypes(true);")
                        && source.contains("this.setMaxDamage(0);"));
        assertTrue("ItemPrimalArrow must keep translation-key suffix by metadata",
                source.contains("return super.getTranslationKey() + \".\" + stack.getItemDamage();"));
        assertTrue("ItemPrimalArrow must expose all six primal arrow metadata variants in creative tab",
                source.contains("for (int meta = 0; meta <= 5; meta++)")
                        && source.contains("items.add(new ItemStack(this, 1, meta));"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
