package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemBathSaltsStaticGuardTest {

    @Test
    public void bathSaltsShouldKeepReferenceLifespanContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemBathSalts.java");

        assertTrue(source.contains("this.setHasSubtypes(false);"));
        assertTrue(source.contains("public int getEntityLifespan(ItemStack itemStack, World world)"));
        assertTrue(source.contains("return 200;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
