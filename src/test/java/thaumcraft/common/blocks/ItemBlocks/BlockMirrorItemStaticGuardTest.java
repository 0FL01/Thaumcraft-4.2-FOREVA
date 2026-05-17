package thaumcraft.common.blocks.ItemBlocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMirrorItemStaticGuardTest {

    @Test
    public void blockMirrorItemShouldKeepLinkedMirrorItemCreationForBothVariants() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockMirrorItem.java");

        assertTrue(source.contains("boolean essentiaVariant = stack.getItemDamage() >= 6;"));
        assertTrue(source.contains("tile instanceof TileMirror && !((TileMirror) tile).isLinkValid()"));
        assertTrue(source.contains("makeLinkedMirrorStack(stack, pos, world, 1)"));
        assertTrue(source.contains("tile instanceof TileMirrorEssentia && !((TileMirrorEssentia) tile).isLinkValid()"));
        assertTrue(source.contains("makeLinkedMirrorStack(stack, pos, world, 7)"));
        assertTrue(source.contains("tag.setInteger(\"linkX\", pos.getX());"));
        assertTrue(source.contains("tag.setInteger(\"linkDim\", world.provider.getDimension());"));
        assertTrue(source.contains("tag.setString(\"dimname\", world.provider.getDimensionType().getName());"));
    }

    @Test
    public void blockMirrorItemShouldRestoreLinkedPlacementForBothTileTypes() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockMirrorItem.java");

        assertTrue(source.contains("if (tile instanceof TileMirror)"));
        assertTrue(source.contains("mirror.linkX = stack.getTagCompound().getInteger(\"linkX\");"));
        assertTrue(source.contains("mirror.restoreLink();"));
        assertTrue(source.contains("else if (tile instanceof TileMirrorEssentia)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
