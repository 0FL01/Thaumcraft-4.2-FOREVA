package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemInkwellCoreContractsStaticGuardTest {

    @Test
    public void itemInkwellKeepsReferenceUseFirstSideResultContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemInkwell.java");

        assertTrue("ItemInkwell must keep client PASS (non-consuming) branch once matching tables are detected",
                source.contains("if (world.isRemote) return EnumActionResult.PASS;"));
        assertTrue("ItemInkwell must keep server SUCCESS terminal result for completed research-table conversion",
                source.contains("return EnumActionResult.SUCCESS;"));
        assertTrue("ItemInkwell must keep reference-shaped paired-table conversion and inkwell slot fill",
                source.contains("world.setBlockState(pos, ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, 2), 3);")
                        && source.contains("world.setBlockState(partner, ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, 3), 3);")
                        && source.contains("((TileResearchTable) world.getTileEntity(pos)).setInventorySlotContents(0, copy);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
