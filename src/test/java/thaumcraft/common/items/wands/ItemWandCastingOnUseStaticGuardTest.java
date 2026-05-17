package thaumcraft.common.items.wands;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemWandCastingOnUseStaticGuardTest {

    @Test
    public void onItemUseFirstShouldKeepWardedRemovalAndArcaneDoorContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java");

        assertTrue(source.contains("block == ConfigBlocks.blockWoodenDevice && meta == 2"));
        assertTrue(source.contains("block == ConfigBlocks.blockCosmeticOpaque && meta == 2"));
        assertTrue(source.contains("!Config.wardedStone || (tile instanceof TileOwned && player.getName().equals(((TileOwned) tile).owner))"));
        assertTrue(source.contains("((TileOwned) tile).safeToRemove = true;"));
        assertTrue(source.contains("new ItemStack(block, 1, meta)"));
        assertTrue(source.contains("block == ConfigBlocks.blockArcaneDoor"));
        assertTrue(source.contains("TileEntity upperLower = (meta & 8) == 0 ? world.getTileEntity(pos.up()) : world.getTileEntity(pos.down());"));
        assertTrue(source.contains("if (Config.wardedStone || (meta & 8) == 0)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemArcaneDoor)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
