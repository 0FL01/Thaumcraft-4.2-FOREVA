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
        assertTrue("ItemInkwell must keep reference-shaped master/partner metadata conversion and inkwell slot fill",
                source.contains("world.removeTileEntity(pos);")
                        && source.contains("withProperty(BlockTable.TYPE, facing.getIndex())")
                        && source.contains("withProperty(BlockTable.TYPE, facing.getOpposite().getIndex() + 4)")
                        && source.contains("((TileResearchTable) researchTile).setInventorySlotContents(0, copy);"));
        assertTrue("ItemInkwell must never read BlockTable.TYPE from a non-table neighbor blockstate",
                source.contains("IBlockState partnerState = world.getBlockState(candidate);")
                        && source.contains("if (partnerState.getBlock() != ConfigBlocks.blockTable) continue;")
                        && source.contains("int partnerMeta = partnerState.getValue(BlockTable.TYPE);"));
        assertTrue("ItemInkwell must only convert plain table metadata, not existing research-table halves",
                source.contains("if (!(tile instanceof thaumcraft.common.tiles.TileTable) || !isPlainTable(meta))")
                        && source.contains("if (!(partnerTile instanceof thaumcraft.common.tiles.TileTable) || !isPlainTable(partnerMeta))")
                        && source.contains("private static boolean isPlainTable(int meta)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
