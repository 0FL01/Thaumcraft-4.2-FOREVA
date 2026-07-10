package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockTableResearchStructureStaticGuardTest {

    @Test
    public void blockTableAndResearchConversionContractsStayReferenceShaped() throws IOException {
        String blockTable = read("src/main/java/thaumcraft/common/blocks/BlockTable.java");
        String itemInkwell = read("src/main/java/thaumcraft/common/items/ItemInkwell.java");
        String researchTable = read("src/main/java/thaumcraft/common/tiles/TileResearchTable.java");
        String researchRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java");
        String researchMasterModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_2.json");
        String researchPartnerModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_6.json");

        assertTrue("BlockTable should remain wand-convertible and restore the master/partner research-table state machine",
                blockTable.contains("implements IWandable")
                        && blockTable.contains("tile instanceof TileResearchTable && md >= 2 && md <= 5")
                        && blockTable.contains("md >= 6 && md <= 9")
                        && blockTable.contains("ConfigBlocks.blockTable.getDefaultState().withProperty(TYPE, 15)")
                        && blockTable.contains("wandstack.setCount(0);")
                        && blockTable.contains("new AxisAlignedBB(0.0D, 0.0D, -1.0D, 1.0D, 1.0D, 1.0D)")
                        && blockTable.contains("new AxisAlignedBB(0.0D, 0.0D, 0.0D, 2.0D, 1.0D, 1.0D)")
                        && blockTable.contains("return this.getBoundingBox(blockState, worldIn, pos);"));

        assertTrue("ItemInkwell should convert one plain table into a research-table master and the adjacent plain table into the matching partner half",
                itemInkwell.contains("world.removeTileEntity(pos);")
                        && itemInkwell.contains("withProperty(BlockTable.TYPE, facing.getIndex())")
                        && itemInkwell.contains("withProperty(BlockTable.TYPE, facing.getOpposite().getIndex() + 4)")
                        && itemInkwell.contains("researchTile instanceof TileResearchTable"));

        assertTrue("TileResearchTable should keep the expanded render bounds and learn-event client sound contract",
                researchTable.contains("public AxisAlignedBB getRenderBoundingBox()")
                        && researchTable.contains("new AxisAlignedBB(this.pos.add(-1, 0, -1), this.pos.add(2, 2, 2))")
                        && researchTable.contains("public boolean receiveClientEvent(int id, int type)")
                        && researchTable.contains("TCSounds.LEARN"));

        assertTrue("TileResearchTableRenderer should render the full research table model from the TESR using live BlockTable state",
                researchRenderer.contains("state.getBlock() == ConfigBlocks.blockTable")
                        && researchRenderer.contains("md = state.getValue(BlockTable.TYPE);")
                        && researchRenderer.contains("tableModel.renderAll(MODEL_SCALE);"));

        assertTrue("Research table block models should stay empty so the TESR owns the visuals while particles keep a wood fallback",
                researchMasterModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && researchMasterModel.contains("\"elements\": []")
                        && researchPartnerModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && researchPartnerModel.contains("\"elements\": []"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
