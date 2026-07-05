package thaumcraft.common.blocks.ItemBlocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockEssentiaReservoirItemStaticGuardTest {

    @Test
    public void reservoirItemKeepsOriginalSingleNameItemBlockContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockEssentiaReservoirItem.java");

        assertTrue(source.contains("extends ItemBlock"));
        assertTrue(source.contains("this.setHasSubtypes(true);"));
        assertTrue(source.contains("public int getMetadata(int damage)"));
        assertTrue(source.contains("return damage;"));
        assertFalse(source.contains("extends BlockMetadataItem"));
        assertFalse(source.contains("getTranslationKey(ItemStack stack)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
