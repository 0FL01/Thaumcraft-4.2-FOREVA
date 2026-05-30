package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EldritchTesrRoutingContractTest {

    @Test
    public void eldritchTileBackedFamilyShouldUseTesrWorldPathAndBuiltinEntityItems() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockEldritch.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemEldritchRenderer.java");
        String capRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCapRenderer.java");
        String obeliskRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java");
        String lockRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchLockRenderer.java");
        String crabSpawnerRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchCrabSpawnerRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockeldritch_tesr.json");
        String shell4 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_4.json");
        String shell5 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_5.json");
        String shell6 = read("src/main/resources/assets/thaumcraft/models/block/blockeldritch_6.json");

        assertTrue("BlockEldritch should route altar/obelisk/capstone/lock/crab-spawner metas through TESR-first world rendering instead of baked cube placeholders",
                block.contains("return meta == 0 || meta == 1 || meta == 2 || meta == 3 || meta == 8 || meta == 9")
                        && block.contains("? EnumBlockRenderType.INVISIBLE")
                        && block.contains(": EnumBlockRenderType.MODEL;"));
        assertTrue("BlockEldritch should use cutout world-layer rendering with neighbor brightness for its non-full shell family",
                block.contains("this.setLightOpacity(0);")
                        && block.contains("this.useNeighborBrightness = true;")
                        && block.contains("public BlockRenderLayer getRenderLayer()")
                        && block.contains("return BlockRenderLayer.CUTOUT;"));
        assertTrue("BlockEldritch should keep the original creative exposure for the static eldritch shell family instead of surfacing the tile-backed metas directly",
                block.contains("list.add(new ItemStack(this, 1, 4));"));

        assertTrue("ClientProxy should keep ordinary blockstate variants for the full eldritch family, but override TESR-backed metas onto a builtin/entity item model and install ItemEldritchRenderer",
                clientProxy.contains("Item eldritchItem = Item.getItemFromBlock(ConfigBlocks.blockEldritch);")
                        && clientProxy.contains("eldritchItem.setTileEntityItemStackRenderer(new ItemEldritchRenderer());")
                        && clientProxy.contains("for (int meta = 0; meta <= 10; meta++) {")
                        && clientProxy.contains("registerBuiltinItemModel(eldritchItem, 0, \"blockeldritch_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(eldritchItem, 1, \"blockeldritch_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(eldritchItem, 3, \"blockeldritch_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(eldritchItem, 8, \"blockeldritch_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(eldritchItem, 9, \"blockeldritch_tesr\");"));

        assertTrue("ItemEldritchRenderer should delegate the TESR-backed metas to the existing eldritch tile renderers with item-safe fake tiles",
                itemRenderer.contains("new TileEldritchCapRenderer()")
                        && itemRenderer.contains("capRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileEldritchCapRenderer(TileEldritchCapRenderer.altarTexture())")
                        && itemRenderer.contains("altarRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileEldritchObeliskRenderer()")
                        && itemRenderer.contains("obeliskRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileEldritchLockRenderer()")
                        && itemRenderer.contains("lockRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("new TileEldritchCrabSpawnerRenderer()")
                        && itemRenderer.contains("crabSpawnerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);")
                        && itemRenderer.contains("if (meta == 0)")
                        && itemRenderer.contains("if (meta == 1)")
                        && itemRenderer.contains("if (meta == 3)")
                        && itemRenderer.contains("if (meta == 8)")
                        && itemRenderer.contains("if (meta == 9)"));

        assertTrue("TileEldritchCapRenderer should keep altar/cap rendering alive for TEISR by using the default texture unless the tile actually lives in the Outer Lands",
                capRenderer.contains("if (tile == null)")
                        && capRenderer.contains("tile.getWorld() != null && tile.getWorld().provider.getDimension() == Config.dimensionOuterId"));
        assertFalse("TileEldritchCapRenderer should no longer skip all rendering on a worldless item tile",
                capRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileEldritchObeliskRenderer should keep the obelisk shell available for TEISR and only treat distance/outer-dimension logic as world-backed concerns",
                obeliskRenderer.contains("if (tile == null)")
                        && obeliskRenderer.contains("tile.getWorld() != null && tile.getWorld().provider.getDimension() == Config.dimensionOuterId")
                        && obeliskRenderer.contains("boolean inRange = tile.getWorld() != null"));
        assertFalse("TileEldritchObeliskRenderer should no longer drop all rendering on a worldless item tile",
                obeliskRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileEldritchLockRenderer should keep the cube/field shell available for TEISR and only gate parallax distance checks on a real world",
                lockRenderer.contains("if (tile == null)")
                        && lockRenderer.contains("boolean inRange = tile.getWorld() != null"));
        assertFalse("TileEldritchLockRenderer should no longer short-circuit on a worldless item tile",
                lockRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("TileEldritchCrabSpawnerRenderer should keep the crab-vent OBJ shell available for TEISR",
                crabSpawnerRenderer.contains("if (tile == null)")
                        && crabSpawnerRenderer.contains("orient(EnumFacing.byIndex(tile.getFacing()));"));
        assertFalse("TileEldritchCrabSpawnerRenderer should no longer short-circuit on a worldless item tile",
                crabSpawnerRenderer.contains("tile == null || tile.getWorld() == null"));

        assertTrue("The eldritch TEISR model stub must stay builtin/entity so Forge dispatches the custom renderer",
                itemModel.contains("\"parent\": \"builtin/entity\""));
        assertTrue("The static eldritch shell metas 4/5/6 should stay inset on exposed sides but remain floor-anchored instead of floating above the support block",
                shell4.contains("\"from\": [2, 0, 2]") && shell4.contains("\"to\": [14, 14, 14]")
                        && shell5.contains("\"from\": [2, 0, 2]") && shell5.contains("\"to\": [14, 14, 14]")
                        && shell6.contains("\"from\": [2, 0, 2]") && shell6.contains("\"to\": [14, 14, 14]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
