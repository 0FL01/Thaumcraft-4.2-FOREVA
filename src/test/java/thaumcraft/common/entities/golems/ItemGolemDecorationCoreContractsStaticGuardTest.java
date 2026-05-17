package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemGolemDecorationCoreContractsStaticGuardTest {

    @Test
    public void golemDecorationKeepsReferenceDisplayNameAndDecoCharContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemDecoration.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemGolemDecoration must keep localized accessory-prefixed display name contract",
                source.contains("I18n.translateToLocal(\"item.ItemGolemDecoration.name\") + \": \" + super.getItemStackDisplayName(stack);"));
        assertTrue("ItemGolemDecoration must keep reference decoration-char mapping including mace variant",
                source.contains("case 6:")
                        && source.contains("return \"P\";")
                        && source.contains("case 7:")
                        && source.contains("return \"M\";"));
        assertTrue("Golem decoration accessory localization key must exist",
                lang.contains("item.ItemGolemDecoration.name=Accessory"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
