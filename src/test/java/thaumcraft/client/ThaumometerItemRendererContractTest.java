package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumometerItemRendererContractTest {

    @Test
    public void thaumometerShouldUseBuiltinEntityScannerRenderer() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String modelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java");
        String perspectiveModel = read("src/main/java/thaumcraft/client/renderers/item/ThaumometerPerspectiveModel.java");
        String utilsFx = read("src/main/java/thaumcraft/client/lib/UtilsFX.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/itemthaumometer_tesr.json");

        assertTrue("ClientProxy should route itemThaumometer onto a builtin/entity model and install the dedicated scanner renderer",
                clientProxy.contains("if (item == ConfigItems.itemThaumometer) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"itemthaumometer_tesr\");")
                        && clientProxy.contains("ConfigItems.itemThaumometer.setTileEntityItemStackRenderer(new ItemThaumometerRenderer());"));

        assertTrue("ClientModelRegistry should wrap the thaumometer builtin/entity model at bake time so the TEISR can see the active transform type",
                modelRegistry.contains("ModelBakeEvent")
                        && modelRegistry.contains("THAUMOMETER_MODEL")
                        && modelRegistry.contains("new ThaumometerPerspectiveModel(model)"));

        assertTrue("ThaumometerPerspectiveModel should override handlePerspective, push the active camera transform into the scanner renderer, and preserve donor display matrices for every render context",
                perspectiveModel.contains("implements IBakedModel")
                        && perspectiveModel.contains("handlePerspective")
                        && perspectiveModel.contains("ItemThaumometerRenderer.setTransformType(cameraTransformType);")
                        && perspectiveModel.contains("delegate.handlePerspective(cameraTransformType)")
                        && perspectiveModel.contains("delegatePerspective.getRight()"));

        assertTrue("ItemThaumometerRenderer should restore the scanner OBJ render path, keep the TC6 mesh basis adapter, and reattach the HUD to the scanner-screen transform plane",
                renderer.contains("extends TileEntityItemStackRenderer")
                        && renderer.contains("textures/models/scanner.obj")
                        && renderer.contains("textures/models/scanner.png")
                        && renderer.contains("textures/models/scanscreen.png")
                        && renderer.contains("TC4_TO_TC6_VERTICAL_CENTER")
                        && renderer.contains("TC4_TO_TC6_Y_ROTATION")
                        && renderer.contains("CURRENT_TRANSFORM")
                        && renderer.contains("ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND")
                        && renderer.contains("CCModel.parseObjModels")
                        && renderer.contains("ScanManager.hasBeenScanned")
                        && renderer.contains("ItemThaumometer")
                        && renderer.contains("findRawScanTarget(stack, player.world, player)")
                        && renderer.contains("ItemThaumometer.findLookedAtNodeTile(player.world, player, 10.0D)")
                        && renderer.contains("UtilsFX.drawTag(")
                        && renderer.contains("renderScannerDisplay(mc, stack, player, transformType);")
                        && renderer.contains("GlStateManager.translate(0.0F, 0.11F, 0.0F);")
                        && renderer.contains("GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);")
                        && renderer.contains("GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);")
                        && renderer.contains("if (isFirstPerson(transformType) && player != null && mc.gameSettings.thirdPersonView == 0) {")
                        && renderer.contains("renderScanReadout(mc, stack, player);")
                        && renderer.contains("GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);")
                        && renderer.contains("HUD_SCALE_MULTIPLIER")
                        && renderer.contains("1.875F")
                        && renderer.contains("GlStateManager.scale(0.0075F * HUD_SCALE_MULTIPLIER, 0.0075F * HUD_SCALE_MULTIPLIER, 0.0075F * HUD_SCALE_MULTIPLIER);")
                        && renderer.contains("GlStateManager.scale(0.004F * HUD_SCALE_MULTIPLIER, 0.004F * HUD_SCALE_MULTIPLIER, 0.004F * HUD_SCALE_MULTIPLIER);")
                        && renderer.contains("font.drawString(nodeTitle, -nodeTitleWidth / 2, -40, 15642134, false);")
                        && renderer.contains("title = \"\";")
                        && renderer.contains("GlStateManager.translate(0.0F, -0.25F, 0.0F);")
                        && renderer.contains("scale -= 0.000025F * (titleWidth - 90);"));

        assertTrue("Minimal UtilsFX helper surface should exist for thaumometer HUD aspect tags",
                utilsFx.contains("public class UtilsFX")
                        && utilsFx.contains("drawTag(")
                        && utilsFx.contains("drawTexturedQuad(")
                        && utilsFx.contains("bindTexture("));

        assertTrue("The scanner render path should adapt the TC4.2 OBJ basis onto the TC6 scanner basis after the dedicated first-person setup or donor display transforms",
                renderer.contains("GlStateManager.translate(0.0F, TC4_TO_TC6_VERTICAL_CENTER, 0.0F);")
                        && renderer.contains("GlStateManager.rotate(TC4_TO_TC6_Y_ROTATION, 0.0F, 1.0F, 0.0F);"));

        assertTrue("The thaumometer item-model stub must stay builtin/entity so Forge still dispatches the custom baked-model + TEISR route",
                itemModel.contains("\"parent\": \"builtin/entity\""));

        assertTrue("The thaumometer TEISR display transforms should mirror the Thaumcraft 6 scanner donor poses for GUI, ground, fixed, and third-person contexts while retaining the dedicated first-person builtin route",
                itemModel.contains("\"thirdperson_righthand\"")
                        && itemModel.contains("\"rotation\": [90, 90, 0]")
                        && itemModel.contains("\"translation\": [0.0, 0.0, -1.6]")
                        && itemModel.contains("\"translation\": [0.0, -1.6, 0.0]")
                        && itemModel.contains("\"translation\": [2.8, -2.8, 0.0]")
                        && itemModel.contains("\"translation\": [1.6, 1.6, 1.6]")
                        && itemModel.contains("\"translation\": [3.2, 3.2, -2.72]")
                        && itemModel.contains("\"translation\": [-0.96, 0.096, -14.352]")
                        && itemModel.contains("\"translation\": [-16.96, 0.096, -14.352]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
