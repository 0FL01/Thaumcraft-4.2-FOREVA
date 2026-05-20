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
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/itemthaumometer_tesr.json");

        assertTrue("ClientProxy should route itemThaumometer onto a builtin/entity model and install the dedicated scanner renderer",
                clientProxy.contains("if (item == ConfigItems.itemThaumometer) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"itemthaumometer_tesr\");")
                        && clientProxy.contains("ConfigItems.itemThaumometer.setTileEntityItemStackRenderer(new ItemThaumometerRenderer());"));

        assertTrue("ClientModelRegistry should wrap the thaumometer builtin/entity model at bake time so the TEISR can see the active transform type",
                modelRegistry.contains("ModelBakeEvent")
                        && modelRegistry.contains("THAUMOMETER_MODEL")
                        && modelRegistry.contains("new ThaumometerPerspectiveModel(model)"));

        assertTrue("ThaumometerPerspectiveModel should override handlePerspective and push the active camera transform into the scanner renderer before returning an identity matrix",
                perspectiveModel.contains("implements IBakedModel")
                        && perspectiveModel.contains("handlePerspective")
                        && perspectiveModel.contains("ItemThaumometerRenderer.setTransformType(cameraTransformType);")
                        && perspectiveModel.contains("TRSRTransformation.identity().getMatrix()"));

        assertTrue("ItemThaumometerRenderer should restore the scanner OBJ render path with transform-aware GUI/ground/third-person/first-person branches",
                renderer.contains("extends TileEntityItemStackRenderer")
                        && renderer.contains("textures/models/scanner.obj")
                        && renderer.contains("textures/models/scanner.png")
                        && renderer.contains("textures/models/scanscreen.png")
                        && renderer.contains("CURRENT_TRANSFORM")
                        && renderer.contains("applyContextTransform")
                        && renderer.contains("renderFirstPersonSetup")
                        && renderer.contains("renderFirstPersonHands")
                        && renderer.contains("ItemCameraTransforms.TransformType.GUI")
                        && renderer.contains("ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND")
                        && renderer.contains("ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND")
                        && renderer.contains("CCModel.parseObjModels")
                        && renderer.contains("player.isHandActive()")
                        && renderer.contains("ScanManager.hasBeenScanned")
                        && renderer.contains("EntityUtils.getPointedEntity")
                        && renderer.contains("player.rayTrace(10.0D, 1.0F)")
                        && renderer.contains("ThaumcraftApi.scanEventhandlers"));

        assertTrue("The thaumometer item-model stub must stay builtin/entity so Forge still dispatches the custom baked-model + TEISR route",
                itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
