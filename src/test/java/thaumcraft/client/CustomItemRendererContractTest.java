package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomItemRendererContractTest {

    @Test
    public void wandAndTrunkSpawnerShouldUseBuiltinEntityItemRenderers() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String clientModelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String wandRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWandRenderer.java");
        String wandModel = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");
        String wandPerspectiveModel = read("src/main/java/thaumcraft/client/renderers/item/WandPerspectiveModel.java");
        String trunkRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTrunkSpawnerRenderer.java");
        String trunkPerspectiveModel = read("src/main/java/thaumcraft/client/renderers/item/TrunkSpawnerPerspectiveModel.java");
        String wandItemModel = read("src/main/resources/assets/thaumcraft/models/item/wandcasting_tesr.json");
        String trunkItemModel = read("src/main/resources/assets/thaumcraft/models/item/trunkspawner_tesr.json");
        String wandCalibration = read("src/main/java/thaumcraft/client/renderers/item/WandRenderCalibration.java");
        String wandCalibrationAsset = read("src/main/resources/assets/thaumcraft/render_calibration/wand_casting.json");

        assertTrue("ClientProxy should override wandcasting and trunkspawner onto builtin/entity item models and install dedicated item renderers",
                clientProxy.contains("if (item == ConfigItems.itemWandCasting) {")
                        && clientProxy.contains("ModelLoader.setCustomMeshDefinition(item, stack -> wandModel);")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"wandcasting_tesr\");")
                        && clientProxy.contains("if (item == ConfigItems.itemTrunkSpawner) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"trunkspawner_tesr\");")
                        && clientProxy.contains("ConfigItems.itemWandCasting.setTileEntityItemStackRenderer(new ItemWandRenderer());")
                        && clientProxy.contains("ConfigItems.itemTrunkSpawner.setTileEntityItemStackRenderer(new ItemTrunkSpawnerRenderer());"));

        assertTrue("ItemWandRenderer and ModelWand should preserve the reference dynamic wand contract, now driven by an external calibration layer instead of flat hardcoded constants",
                wandRenderer.contains("extends TileEntityItemStackRenderer")
                        && wandRenderer.contains("new ModelWand()")
                        && wandRenderer.contains("CURRENT_TRANSFORM")
                        && wandRenderer.contains("isHandTransform(transformType)")
                        && wandRenderer.contains("WandRenderCalibration.get(")
                        && wandRenderer.contains("resolveKind(wand, stack)")
                        && wandRenderer.contains("applyBasePose(")
                        && wandRenderer.contains("applyUseAnimation(")
                        && wandRenderer.contains("player.isHandActive()")
                        && wandRenderer.contains("wand.getFocus(stack)")
                        && wandRenderer.contains("isFirstPerson(transformType)")
                        && wandRenderer.contains("GlStateManager.rotate(t.finalRotateX")
                        && wandModel.contains("wand.getRod(wandStack).getTexture()")
                        && wandModel.contains("wand.getCap(wandStack).getTexture()")
                        && wandModel.contains("wand.hasRunes(wandStack)")
                        && wandModel.contains("ItemWandCasting.isSceptre(wandStack)")
                        && wandModel.contains("textures/misc/script.png")
                        && wandModel.contains("textures/models/wand.png")
                        && wandModel.contains("drawRune("));

        assertTrue("ModelWand should preserve the caller lightmap for world and item contexts and restore TC4's Halloween focus texture",
                wandModel.contains("OpenGlHelper.lastBrightnessX")
                        && wandModel.contains("OpenGlHelper.lastBrightnessY")
                        && wandModel.contains("textures/models/spec_h.png")
                        && wandModel.contains("Calendar.OCTOBER")
                        && wandModel.contains("Calendar.DAY_OF_MONTH"));
        assertFalse("ModelWand must not restore player lighting while it is rendered at a tile",
                wandModel.contains("getBrightnessForRender()"));

        assertTrue("WandRenderCalibration must keep the wand/staff/sceptre kinds, the TC4 final-rotate basis, and Java defaults that reproduce the prior port constants byte-for-byte",
                wandCalibration.contains("KIND_WAND = \"wand\"")
                        && wandCalibration.contains("KIND_STAFF = \"staff\"")
                        && wandCalibration.contains("KIND_SCEPTRE = \"sceptre\"")
                        && wandCalibration.contains("buildDefaultCalibration()")
                        // prior port constants preserved as Java defaults
                        && wandCalibration.contains("180f, 0f, 0f")
                        && wandCalibration.contains("0.5f,0.5f,0f")
                        && wandCalibration.contains("0.6f,0.6f,0.6f")
                        && wandCalibration.contains("20f,-45f,45f")
                        && wandCalibration.contains("0f,0.6f,0f")
                        && wandCalibration.contains("0f,1f,0f")
                        && wandCalibration.contains("0.5f,1f,0.5f")
                        && wandCalibration.contains("1f,1.1f,1f")
                        && wandCalibration.contains("0.5f,0.5f,0.5f")
                        && wandCalibration.contains("0f,0.5f,0f")
                        && wandCalibration.contains("-0.7f,1.2f,0f")
                        && wandCalibration.contains("0f,1.5f,0f")
                        && wandCalibration.contains("0.9f,0.9f,0.9f")
                        && wandCalibration.contains("thaumcraft.debugWandRender"));

        assertTrue("The bundled wand_casting.json calibration asset must declare the three kinds and the TC4 180-degree X final rotate",
                wandCalibrationAsset.contains("\"finalRotate\": [180.0, 0.0, 0.0]")
                        && wandCalibrationAsset.contains("\"wand\"")
                        && wandCalibrationAsset.contains("\"staff\"")
                        && wandCalibrationAsset.contains("\"sceptre\"")
                        && wandCalibrationAsset.contains("\"FIRST_PERSON_RIGHT_HAND\"")
                        && wandCalibrationAsset.contains("\"THIRD_PERSON_LEFT_HAND\"")
                        && wandCalibrationAsset.contains("\"scaleMultiplier\": [0.8, 0.8, 0.8]")
                        && wandCalibrationAsset.contains("\"postTranslateAdd\": [-0.7, 0.6, 0.0]"));

        assertTrue("ClientModelRegistry should wrap wandcasting into a perspective-aware baked model so the TEISR can distinguish GUI, hand, and ground contexts",
                clientModelRegistry.contains("WANDCASTING_MODEL")
                        && clientModelRegistry.contains("new WandPerspectiveModel(model)"));

        assertTrue("WandPerspectiveModel should keep the baked display matrix and pass TransformType to ItemWandRenderer",
                wandPerspectiveModel.contains("implements IBakedModel")
                        && wandPerspectiveModel.contains("ItemWandRenderer.setTransformType(cameraTransformType)")
                        && wandPerspectiveModel.contains("delegate.handlePerspective(cameraTransformType)")
                        && wandPerspectiveModel.contains("Pair.of(this, delegatePerspective.getRight())"));

        assertTrue("ItemTrunkSpawnerRenderer should render the chest-shell model via TEISR with transforms delegated to JSON display transforms",
                trunkRenderer.contains("extends TileEntityItemStackRenderer")
                        && trunkRenderer.contains("new ModelChest()")
                        && trunkRenderer.contains("textures/models/trunk.png")
                        && trunkRenderer.contains("model.renderAll()")
                        && trunkRenderer.contains("bindTexture")
                        && trunkRenderer.contains("GlStateManager.enableRescaleNormal()")
                        && trunkRenderer.contains("GlStateManager.disableRescaleNormal()")
                        && trunkRenderer.contains("chestLid.rotateAngleX = 0.0F")
                        && trunkRenderer.contains("chestKnob.rotateAngleX = 0.0F"));

        assertTrue("ClientModelRegistry should wrap trunkspawner into a perspective-aware baked model",
                clientModelRegistry.contains("TRUNKSPAWNER_MODEL")
                        && clientModelRegistry.contains("new TrunkSpawnerPerspectiveModel(model)"));

        assertTrue("TrunkSpawnerPerspectiveModel should delegate handlePerspective to the baked model",
                trunkPerspectiveModel.contains("implements IBakedModel")
                        && trunkPerspectiveModel.contains("handlePerspective")
                        && trunkPerspectiveModel.contains("delegate.handlePerspective(cameraTransformType)"));

        assertTrue("The trunkspawner TEISR display transforms should include GUI, ground, fixed, thirdperson, firstperson, and mirrored left-hand entries",
                trunkItemModel.contains("\"gui\"")
                        && trunkItemModel.contains("\"rotation\": [30, 45, 0]")
                        && trunkItemModel.contains("\"scale\": [0.625, 0.625, 0.625]")
                        && trunkItemModel.contains("\"ground\"")
                        && trunkItemModel.contains("\"rotation\": [0, 0, 180]")
                        && trunkItemModel.contains("\"translation\": [0, 3, 0]")
                        && trunkItemModel.contains("\"scale\": [0.25, 0.25, 0.25]")
                        && trunkItemModel.contains("\"head\"")
                        && trunkItemModel.contains("\"fixed\"")
                        && trunkItemModel.contains("\"rotation\": [0, 180, 0]")
                        && trunkItemModel.contains("\"thirdperson_righthand\"")
                        && trunkItemModel.contains("\"rotation\": [75, 315, 180]")
                        && trunkItemModel.contains("\"translation\": [0, 2.5, 0]")
                        && trunkItemModel.contains("\"scale\": [0.375, 0.375, 0.375]")
                        && trunkItemModel.contains("\"thirdperson_lefthand\"")
                        && trunkItemModel.contains("\"rotation\": [75, 135, 180]")
                        && trunkItemModel.contains("\"firstperson_righthand\"")
                        && trunkItemModel.contains("\"rotation\": [0, 315, 180]")
                        && trunkItemModel.contains("\"scale\": [0.4, 0.4, 0.4]")
                        && trunkItemModel.contains("\"firstperson_lefthand\"")
                        && trunkItemModel.contains("\"rotation\": [0, 135, 180]"));

        assertTrue("The wand and trunk item-model stubs must stay builtin/entity so Forge dispatches the dedicated item renderers",
                wandItemModel.contains("\"parent\": \"builtin/entity\"")
                        && trunkItemModel.contains("\"parent\": \"builtin/entity\""));

        assertTrue("The wand TEISR model should keep neutral display matrices because ItemWandRenderer owns the TC4 context-specific pose and lower hand offset",
                wandItemModel.contains("\"thirdperson_righthand\"")
                        && wandItemModel.contains("\"firstperson_righthand\"")
                        && wandItemModel.contains("\"gui\"")
                        && wandItemModel.contains("\"ground\"")
                        && wandItemModel.contains("\"fixed\"")
                        && wandItemModel.contains("\"rotation\": [0, 0, 0]")
                        && wandItemModel.contains("\"translation\": [0, 0, 0]")
                        && wandItemModel.contains("\"scale\": [1.0, 1.0, 1.0]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
