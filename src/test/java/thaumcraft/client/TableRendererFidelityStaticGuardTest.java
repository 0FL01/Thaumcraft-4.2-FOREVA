package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TableRendererFidelityStaticGuardTest {

    @Test
    public void tableFamilyRenderersUseModelDrivenPaths() throws IOException {
        String modelTable = read("src/main/java/thaumcraft/client/renderers/models/ModelTable.java");
        String tableRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTableRenderer.java");
        String deconRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileDeconstructionTableRenderer.java");
        String arcaneWorkbenchRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneWorkbenchRenderer.java");
        String researchRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileResearchTableRenderer.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemTableRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTableRenderer.java");
        String plainItemModel = read("src/main/resources/assets/thaumcraft/models/item/blocktable_0_inventory.json");
        String deconItemModel = read("src/main/resources/assets/thaumcraft/models/item/blocktable_14_inventory.json");
        String itemTableTesrModel = read("src/main/resources/assets/thaumcraft/models/item/blocktable_tesr.json");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/TileRenderHelper.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blocktable.json");
        String plainTableModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_0.json");
        String researchMasterModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_2.json");
        String researchPartnerModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_6.json");
        String deconModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_14.json");
        String arcaneWorkbenchModel = read("src/main/resources/assets/thaumcraft/models/block/blocktable_15.json");

        assertTrue("ModelTable should define the classic top/legs/crossbar geometry",
                modelTable.contains("class ModelTable extends ModelBase")
                        && modelTable.contains("top")
                        && modelTable.contains("leg1")
                        && modelTable.contains("leg2")
                        && modelTable.contains("crossbar")
                        && modelTable.contains("renderAll(float scale)"));

        assertTrue("TileTableRenderer should restore the plain table shell via TESR and skip research partner halves",
                tableRenderer.contains("new ModelTable()")
                        && tableRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && tableRenderer.contains("if (md >= 6) {")
                        && tableRenderer.contains("bindTexture(TABLE_TEXTURE);"));

        assertTrue("TileDeconstructionTableRenderer should restore the deconstruction table shell and keep thaumometer/input/aspect overlays",
                deconRenderer.contains("renderThaumometer")
                        && deconRenderer.contains("new ModelArcaneWorkbench()")
                        && deconRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && deconRenderer.contains("renderItemGround")
                        && deconRenderer.contains("tile.aspect.getImage()")
                        && deconRenderer.contains("TileRenderHelper.renderEntityItem(tile, thaumometer, 0.0F);")
                        && deconRenderer.contains("TileRenderHelper.renderEntityItem(tile, stack, 0.0F);")
                        && !deconRenderer.contains("renderPlate("));

        assertTrue("TileArcaneWorkbenchRenderer should restore the worktable shell and keep the wand overlay",
                arcaneWorkbenchRenderer.contains("wand.getItem() instanceof ItemWandCasting")
                        && arcaneWorkbenchRenderer.contains("new ModelArcaneWorkbench()")
                        && arcaneWorkbenchRenderer.contains("new ModelWand()")
                        && arcaneWorkbenchRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && arcaneWorkbenchRenderer.contains("wandModel.render(wand, partialTicks, player);")
                        && !arcaneWorkbenchRenderer.contains("TileRenderHelper.renderEntityItem(tile, wand")
                        && !arcaneWorkbenchRenderer.contains("renderTableModel"));

        assertTrue("ClientProxy should route Arcane Workbench meta 15 through the existing table TEISR while keeping audited baked routes for metas 0 and 14",
                clientProxy.contains("tableItem.setTileEntityItemStackRenderer(new ItemTableRenderer());")
                        && clientProxy.contains("registerBuiltinItemModel(tableItem, 0, \"blocktable_0_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(tableItem, 14, \"blocktable_14_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(tableItem, 15, \"blocktable_tesr\");")
                        && !clientProxy.contains("blocktable_15_inventory"));

        int restoreOrigin = itemTableRenderer.indexOf("restoreLegacyInventoryOrigin();");
        int legacyRotation = itemTableRenderer.indexOf("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);");
        int legacyTranslation = itemTableRenderer.indexOf("GlStateManager.translate(-0.5F, -0.5F, -0.5F);");
        int researchTranslation = itemTableRenderer.indexOf("GlStateManager.translate(-0.5F, 0.0F, 0.0F);");
        assertTrue("ItemTableRenderer should preserve the Forge-correct TC4 inventory transform chain and render a populated research-table preview for meta 1",
                itemTableRenderer.contains("new TileTableRenderer()")
                        && itemTableRenderer.contains("new TileResearchTableRenderer()")
                        && itemTableRenderer.contains("new TileResearchTable()")
                        && itemTableRenderer.contains("researchTable.setInventorySlotContents(0, new ItemStack(ConfigItems.itemInkwell));")
                        && itemTableRenderer.contains("researchTable.setInventorySlotContents(1, new ItemStack(ConfigItems.itemResearchNotes));")
                        && itemTableRenderer.contains("new TileDeconstructionTableRenderer()")
                        && itemTableRenderer.contains("new TileArcaneWorkbenchRenderer()")
                        && itemTableRenderer.contains("if (meta == 0)")
                        && itemTableRenderer.contains("if (meta == 1)")
                        && itemTableRenderer.contains("if (meta == 14)")
                        && itemTableRenderer.contains("if (meta == 15)")
                        && itemTableRenderer.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);")
                        && restoreOrigin >= 0
                        && restoreOrigin < legacyRotation
                        && legacyRotation < legacyTranslation
                        && legacyTranslation < researchTranslation
                        && itemTableRenderer.contains("finally {")
                        && itemTableRenderer.contains("GlStateManager.enableRescaleNormal();")
                        && itemTableRenderer.contains("GlStateManager.popMatrix();"));

        assertTrue("The research-table preview should reuse the existing builtin TESR model and complete block-style display transforms",
                itemTableTesrModel.contains("\"parent\": \"builtin/entity\"")
                        && itemTableTesrModel.contains("\"gui\"")
                        && itemTableTesrModel.contains("[30, 225, 0]")
                        && itemTableTesrModel.contains("\"ground\"")
                        && itemTableTesrModel.contains("[0.25, 0.25, 0.25]")
                        && itemTableTesrModel.contains("\"fixed\"")
                        && itemTableTesrModel.contains("[0.5, 0.5, 0.5]")
                        && itemTableTesrModel.contains("\"thirdperson_righthand\"")
                        && itemTableTesrModel.contains("\"thirdperson_lefthand\"")
                        && itemTableTesrModel.contains("\"firstperson_righthand\"")
                        && itemTableTesrModel.contains("\"firstperson_lefthand\""));

        assertTrue("The audited plain and deconstruction table item models should retain donor-style transforms and TC4 model-texture UVs",
                plainItemModel.contains("\"rotation\": [30, 225, 0]")
                        && plainItemModel.contains("\"surface\": \"thaumcraft:models/table_inventory\"")
                        && plainItemModel.contains("\"uv\": [8, 0, 12, 8]")
                        && plainItemModel.contains("\"from\": [0, 12, 0]")
                        && plainItemModel.contains("\"to\": [16, 16, 16]")
                        && plainItemModel.contains("\"from\": [10, 4, 6]")
                        && plainItemModel.contains("\"from\": [0, 0, 4]")
                        && deconItemModel.contains("\"surface\": \"thaumcraft:models/decontable_inventory\"")
                        && deconItemModel.contains("\"uv\": [9.5, 1, 10, 2]")
                        && deconItemModel.contains("\"up\": { \"uv\": [2, 0, 4, 4]")
                        && deconItemModel.contains("\"from\": [0, 8, 0]")
                        && deconItemModel.contains("\"from\": [0, 0, 0]")
                        && deconItemModel.contains("\"to\": [16, 16, 16]"));

        assertTrue("TileRenderHelper should keep the shared TESR entity-item path worldless-safe for display-item renderers",
                helper.contains("static void renderEntityItem(TileEntity tile, ItemStack stack, float hoverStart)")
                        && helper.contains("renderEntityItem(tile == null ? null : tile.getWorld(), stack, hoverStart);")
                        && helper.contains("return world != null ? world : Minecraft.getMinecraft().world;")
                        && helper.contains("renderStack.setCount(1);"));

        assertTrue("TileResearchTableRenderer should restore the full research-table shell in the TESR while keeping overlay rendering",
                researchRenderer.contains("md = state.getValue(BlockTable.TYPE);")
                        && researchRenderer.contains("tableModel.renderAll(MODEL_SCALE);")
                        && researchRenderer.contains("tableModel.renderInkwell(MODEL_SCALE);")
                        && researchRenderer.contains("tableModel.renderScroll(MODEL_SCALE, color);"));

        assertTrue("Table blockstate should route plain and research table halves to shaped block models instead of the old shared cube placeholder",
                blockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blocktable_0\", \"y\": 90 }")
                        && blockstate.contains("\"type=2\": { \"model\": \"thaumcraft:blocktable_2\", \"y\": 270 }")
                        && blockstate.contains("\"type=5\": { \"model\": \"thaumcraft:blocktable_2\" }")
                        && blockstate.contains("\"type=6\": { \"model\": \"thaumcraft:blocktable_6\", \"y\": 90 }")
                        && blockstate.contains("\"type=8\": { \"model\": \"thaumcraft:blocktable_6\" }"));

        assertTrue("Table-family block models should stay empty with particle fallback where TESR owns the visuals",
                plainTableModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && plainTableModel.contains("\"elements\": []")
                        && researchMasterModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && researchMasterModel.contains("\"elements\": []")
                        && researchPartnerModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && researchPartnerModel.contains("\"elements\": []")
                        && deconModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && deconModel.contains("\"elements\": []")
                        && arcaneWorkbenchModel.contains("\"particle\": \"thaumcraft:blocks/woodplain\"")
                        && arcaneWorkbenchModel.contains("\"elements\": []"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
