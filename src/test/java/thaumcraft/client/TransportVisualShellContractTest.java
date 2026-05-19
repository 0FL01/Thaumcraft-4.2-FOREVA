package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransportVisualShellContractTest {

    @Test
    public void mirrorAndReservoirShellsStayOnBlockModelsWhileTesrKeepsOnlyDynamicLayers() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String mirrorRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java");
        String reservoirRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaReservoirRenderer.java");
        String mirrorBlockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockmirror.json");
        String mirrorWall = read("src/main/resources/assets/thaumcraft/models/block/blockmirror_0.json");
        String mirrorEssentiaWall = read("src/main/resources/assets/thaumcraft/models/block/blockmirror_6.json");
        String mirrorCeiling = read("src/main/resources/assets/thaumcraft/models/block/blockmirror_down_0.json");
        String mirrorFloor = read("src/main/resources/assets/thaumcraft/models/block/blockmirror_up_0.json");
        String reservoirModel = read("src/main/resources/assets/thaumcraft/models/block/blockessentiareservoir.json");
        String essentiaMirrorItem = read("src/main/resources/assets/thaumcraft/models/item/blockmirror_essentia.json");

        assertTrue("Mirror blockstate must route ceiling/floor variants explicitly and rotate the wall frame for side metas",
                mirrorBlockstate.contains("\"type=0\": { \"model\": \"thaumcraft:blockmirror_down_0\" }")
                        && mirrorBlockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blockmirror_up_0\" }")
                        && mirrorBlockstate.contains("\"type=4\": { \"model\": \"thaumcraft:blockmirror_0\", \"y\": 90 }")
                        && mirrorBlockstate.contains("\"type=5\": { \"model\": \"thaumcraft:blockmirror_0\", \"y\": 270 }")
                        && mirrorBlockstate.contains("\"type=6\": { \"model\": \"thaumcraft:blockmirror_down_6\" }")
                        && mirrorBlockstate.contains("\"type=7\": { \"model\": \"thaumcraft:blockmirror_up_6\" }"));
        assertTrue("Mirror wall model must be a thin frame instead of the old full cube placeholder",
                mirrorWall.contains("\"ambientocclusion\": false")
                        && mirrorWall.contains("\"frame\": \"thaumcraft:blocks/mirrorframe\"")
                        && mirrorWall.contains("\"from\": [15, 1, 0]")
                        && mirrorWall.contains("\"to\": [16, 15, 1]"));
        assertTrue("Essentia mirror wall model must keep the alternate frame texture on the same thin-shell geometry",
                mirrorEssentiaWall.contains("\"frame\": \"thaumcraft:blocks/mirrorframe2\"")
                        && mirrorEssentiaWall.contains("\"from\": [0, 15, 0]")
                        && mirrorEssentiaWall.contains("\"to\": [16, 16, 1]"));
        assertTrue("Mirror ceiling and floor models must use dedicated planar frame shells",
                mirrorCeiling.contains("\"from\": [0, 15, 15]")
                        && mirrorFloor.contains("\"from\": [0, 0, 15]"));
        assertFalse("Mirror TESR render path must not keep drawing a duplicate static frame once the frame moved into block models",
                mirrorRenderer.contains("renderFrame(facing, x, y, z, tile.getBlockMetadata() >= 6, instability);"));
        assertTrue("Mirror TESR must keep the dynamic pane/portal layers after the static shell moved into block models",
                mirrorRenderer.contains("renderPortalLayers(facing, x, y, z, partialTicks);")
                        && mirrorRenderer.contains("renderPane(facing, x, y, z, MIRROR_PANE_TRANS, 0.02F + instability);"));
        assertTrue("Reservoir block model must expose the basin shell geometry instead of the old full cube placeholder",
                reservoirModel.contains("\"ambientocclusion\": false")
                        && reservoirModel.contains("\"from\": [2, 7, 2]")
                        && reservoirModel.contains("\"from\": [13, 2, 3]")
                        && reservoirModel.contains("\"to\": [14, 7, 13]"));
        assertTrue("Reservoir TESR must keep only the dynamic liquid layer after the shell moved into the block model",
                reservoirRenderer.contains("renderLiquid(tile, x, y, z);"));
        assertFalse("Reservoir TESR must not keep rendering the duplicate static shell model",
                reservoirRenderer.contains("model.renderAll(") || reservoirRenderer.contains("RESERVOIR_TEXTURE"));
        assertTrue("ClientProxy must split normal and essentia mirror inventory models by meta range",
                clientProxy.contains("new ResourceLocation(\"thaumcraft\", \"blockmirror_essentia\")")
                        && clientProxy.contains("for (int meta = 6; meta < 12; meta++) {"));
        assertTrue("Essentia mirror item model must point at the alternate block shell",
                essentiaMirrorItem.contains("\"parent\": \"thaumcraft:block/blockmirror_6\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
