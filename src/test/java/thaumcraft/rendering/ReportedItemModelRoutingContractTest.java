package thaumcraft.rendering;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible routing guards for item icons reported during live visual testing. */
public class ReportedItemModelRoutingContractTest {

    @Test
    public void phaseOneStaticItemsShouldAvoidPlacementAndTeisrRoutes() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertFalse("Mirror items must not reuse orientation-bearing placement blockstates",
                clientProxy.contains("registerBlockItemModel(mirrorItem, meta, \"type=\" + meta);"));
        assertTrue("Mirror closed/open variants must use dedicated layered item models",
                clientProxy.contains("meta == 1 ? \"blockmirror_open\" : \"blockmirror\"")
                        && clientProxy.contains("meta == 7 ? \"blockmirror_essentia_open\" : \"blockmirror_essentia\""));

        assertFalse("Static tube and buffer inventory shells must not route through TEISR",
                clientProxy.contains("registerBuiltinItemModel(tubeItem, 0, \"blocktube_tesr\");")
                        || clientProxy.contains("registerBuiltinItemModel(tubeItem, 3, \"blocktube_tesr\");")
                        || clientProxy.contains("registerBuiltinItemModel(tubeItem, 4, \"blocktube_tesr\");")
                        || clientProxy.contains("registerBuiltinItemModel(tubeItem, 5, \"blocktube_tesr\");")
                        || clientProxy.contains("registerBuiltinItemModel(tubeItem, 6, \"blocktube_tesr\");"));
        assertTrue("Grate and hungry chest must route to complete dedicated baked item models",
                clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 5, \"blockmetaldevice_5_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(chestItem, 0, \"blockchesthungry\");"));
        assertFalse("Static hungry chest item must not retain an active TEISR assignment",
                clientProxy.contains("chestItemTeisr.setTileEntityItemStackRenderer"));
    }

    @Test
    public void phaseOneModelsShouldInheritApprovedItemTransforms() throws IOException {
        assertGeneratedLayers("blockmirror.json", "thaumcraft:items/mirrorframe", "thaumcraft:blocks/mirrorpane");
        assertGeneratedLayers("blockmirror_open.json", "thaumcraft:items/mirrorframe", "thaumcraft:blocks/mirrorpaneopen");
        assertGeneratedLayers("blockmirror_essentia.json", "thaumcraft:items/mirrorframe2", "thaumcraft:blocks/mirrorpane");
        assertGeneratedLayers("blockmirror_essentia_open.json", "thaumcraft:items/mirrorframe2", "thaumcraft:blocks/mirrorpaneopen");

        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_0.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_4.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_8.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_5_inventory.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockchesthungry.json");
    }

    @Test
    public void animatedWoodenDevicesShouldUseExplicitLegacyOriginPolicy() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWoodenDeviceRenderer.java");
        String boreRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneBoreRenderer.java");
        String model = read("src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_tesr.json");
        String bannerModel = read("src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_banner_tesr.json");

        assertTrue(renderer.contains("private static void restoreLegacyInventoryOrigin()")
                && renderer.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);"));
        assertFalse("Bore item tiles must retain their TC4 constructor defaults",
                renderer.contains("bore.orientation =") || renderer.contains("bore.baseOrientation ="));
        assertFalse("Banner NBT must not alter the normalized item anchor selected by the shared display model",
                renderer.contains("hasStyledBannerData")
                        || renderer.contains("usesLegacyStyledOffset")
                        || renderer.contains("GlStateManager.translate(1.0F, 1.0F, 1.0F);"));
        assertTrue("The bore base-orientation flip must be scoped to the base model pass",
                boreRenderer.indexOf("GlStateManager.pushMatrix();\n        bindTexture(BORE_TEXTURE);")
                        < boreRenderer.indexOf("if (tile.baseOrientation == EnumFacing.DOWN)")
                        && boreRenderer.indexOf("if (tile.baseOrientation == EnumFacing.DOWN)")
                        < boreRenderer.indexOf("boreModel.renderBase(MODEL_SCALE);"));
        assertComplete3dDisplay(model);
        assertComplete3dDisplay(bannerModel);
    }

    @Test
    public void animatedStoneDevicesShouldUseOneItemOffsetAndOriginalObjGroups() throws IOException {
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemStoneDeviceRenderer.java");
        String nodeModel = read("src/main/java/thaumcraft/client/renderers/models/ModelNodeStabilizer.java");
        String stabilizer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeStabilizerRenderer.java");
        String converter = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeConverterRenderer.java");
        String fluxModel = read("src/main/java/thaumcraft/client/renderers/models/ModelFluxScrubber.java");
        String capModel = read("src/main/java/thaumcraft/client/renderers/models/ModelEldritchCap.java");

        assertFalse("Forge already supplies the TC4 item-space -0.5 translation",
                itemRenderer.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));
        assertTrue("The copied OBJ must use the Wavefront V origin",
                nodeModel.contains(".tex(uv[0], 1.0F - uv[1])"));
        assertTrue("Both node devices must render their base lock before the overlay",
                stabilizer.contains("bindTexture(BASE_TEXTURE);\n            model.renderLock(MODEL_SCALE);")
                        && converter.contains("bindTexture(BASE_TEXTURE);\n            model.renderLock(MODEL_SCALE);"));
        assertTrue("World node overlays must retain TC4's count-driven lightmap glow",
                stabilizer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && converter.contains("OpenGlHelper.setLightmapTextureCoords")
                        && stabilizer.contains("tile.count / 37.0F * pulse")
                        && converter.contains("progress * 2.5F * pulse"));
        assertTrue("The node bubble keeps its fixed TC4 size and pulses through alpha",
                stabilizer.contains("int bubbleAlpha")
                        && stabilizer.contains("drawTexturedQuad(0.9F, bubbleColor"));
        assertFalse("Invented time-based piston rotations must not replace TC4's count translation",
                stabilizer.contains("ticks * (1.5F") || converter.contains("ticks * (1.8F"));
        assertTrue(fluxModel.contains("new ModelEldritchCap()")
                && fluxModel.contains("renderCapGroup()")
                && fluxModel.contains("renderTipGroup()"));
        assertFalse("Flux scrubber must not fall back to substitute ModelRenderer cuboids",
                fluxModel.contains("ModelRenderer") || fluxModel.contains("addBox("));
        assertTrue(capModel.contains("public void renderCapGroup()")
                && capModel.contains("public void renderTipGroup()")
                && capModel.contains(".tex(uv[0], 1.0F - uv[1])"));
    }

    @Test
    public void secondWaveItemsShouldResolveToTheirParityItemRoutes() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("Crucible and levitator should use their complete baked inventory models",
                clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 0, \"blockmetaldevice_0_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(lifterItem, 0, \"blocklifter\");")
                        && clientProxy.contains("new LifterItemColor()"));
        assertTrue("Research Table sentinel meta 1 should reach its populated TEISR preview",
                clientProxy.contains("registerBuiltinItemModel(tableItem, 1, \"blocktable_tesr\");")
                        && clientProxy.contains("tableItem.setTileEntityItemStackRenderer(new ItemTableRenderer());"));
        assertTrue("Only audited metal metas should use the complete dynamic display manifest",
                clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 1, \"blockmetaldevice_dynamic_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 2, \"blockmetaldevice_dynamic_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 14, \"blockmetaldevice_dynamic_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 10, \"blockmetaldevice_tesr\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 11, \"blockmetaldevice_tesr\");"));
        assertTrue("Centrifuge should use its complete static shell while crystallizer stays dynamic",
                clientProxy.contains("registerBuiltinItemModel(tubeItem, 2, \"blocktube_2_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(tubeItem, 7, \"blocktube_tesr\");")
                        && clientProxy.contains("tubeItem.setTileEntityItemStackRenderer(new ItemTubeRenderer());"));
        assertTrue("Lamp previews should use dedicated TC4 inventory shells",
                clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 7, \"blockmetaldevice_7_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 8, \"blockmetaldevice_8_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 13, \"blockmetaldevice_13_inventory\");"));
        assertTrue("Every jar metadata should resolve to the shared NBT-aware built-in renderer",
                clientProxy.contains("for (int meta = 0; meta <= 3; meta++) {\n            registerBuiltinItemModel(jarItem2, meta, \"blockjar\");")
                        && clientProxy.contains("jarItem.setTileEntityItemStackRenderer(renderer);"));
        assertTrue("Reservoir should keep its baked-core plus dynamic-shell item composition",
                clientProxy.contains("registerBuiltinItemModel(Item.getItemFromBlock(ConfigBlocks.blockEssentiaReservoir), 0, \"blockessentiareservoir_tesr\");")
                        && clientProxy.contains("reservoirItem.setTileEntityItemStackRenderer(new ItemEssentiaReservoirRenderer());"));
    }

    @Test
    public void secondWaveModelsShouldExposeCompletePerspectiveAndSourceGeometry() throws IOException {
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_0_inventory.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blocklifter.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blocktube_2_inventory.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_1.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_3.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_5.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/block/blocktube_6.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_7_inventory.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_8_inventory.json");
        assertBlockDisplayParent("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_13_inventory.json");

        assertComplete3dDisplay(read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_dynamic_tesr.json"));
        assertComplete3dDisplay(read("src/main/resources/assets/thaumcraft/models/item/blocktube_tesr.json"));
        assertComplete3dDisplay(read("src/main/resources/assets/thaumcraft/models/item/blockjar.json"));
        assertComplete3dDisplay(read("src/main/resources/assets/thaumcraft/models/item/blockessentiareservoir_tesr.json"));
        assertComplete3dDisplay(read("src/main/resources/assets/thaumcraft/models/item/blocktable_tesr.json"));

        String lifter = read("src/main/resources/assets/thaumcraft/models/item/blocklifter.json");
        assertTrue("Levitator item should retain both independently tinted animated TC4 glow layers",
                lifter.contains("\"glow\": \"thaumcraft:blocks/animatedglow\"")
                        && lifter.contains("\"tintindex\": 0")
                        && lifter.contains("\"tintindex\": 1"));
        String reservoir = read("src/main/resources/assets/thaumcraft/models/block/blockessentiareservoir.json");
        assertTrue("Reservoir baked pass should be the exact TC4 2..14 core, not a substitute shell",
                reservoir.contains("\"from\": [2, 2, 2]")
                        && reservoir.contains("\"to\": [14, 14, 14]")
                        && occurrences(reservoir, "\"from\"") == 1);
    }

    @Test
    public void secondWaveTeisrOriginPolicyShouldKeepExactlyOneForgeOffset() throws IOException {
        String tube = read("src/main/java/thaumcraft/client/renderers/item/ItemTubeRenderer.java");
        String jar = read("src/main/java/thaumcraft/client/renderers/item/ItemJarRenderer.java");
        String reservoir = read("src/main/java/thaumcraft/client/renderers/item/ItemEssentiaReservoirRenderer.java");
        String metal = read("src/main/java/thaumcraft/client/renderers/item/ItemMetalDeviceRenderer.java");
        String table = read("src/main/java/thaumcraft/client/renderers/item/ItemTableRenderer.java");

        assertFalse("Tube renderer must not duplicate Forge's built-in item offset",
                tube.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));
        assertFalse("Jar renderer must preserve direct block-space liquid and label coordinates",
                jar.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);")
                        || jar.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));
        assertFalse("Reservoir core, OBJ and liquid must share Forge's one outer offset",
                reservoir.contains("GlStateManager.translate(0.5F, 0.5F, 0.5F);")
                        || reservoir.contains("GlStateManager.translate(-0.5F, -0.5F, -0.5F);"));
        int reservoirRotation = reservoir.indexOf("GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);");
        int reservoirModel = reservoir.indexOf("renderBlockBrightness(");
        assertTrue("Reservoir must cancel BlockModelRenderer's hidden +90 Y rotation before drawing its core",
                reservoirRotation >= 0 && reservoirModel >= 0 && reservoirRotation < reservoirModel);
        assertTrue("Only metal metas 1, 2 and 14 should restore the legacy TC4 origin",
                occurrences(metal, "restoreLegacyInventoryOrigin();") == 3);
        assertTrue("Research Table must cancel Forge before replaying its rotated TC4 inventory chain",
                table.indexOf("restoreLegacyInventoryOrigin();") >= 0
                        && table.indexOf("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);") >= 0
                        && table.indexOf("restoreLegacyInventoryOrigin();")
                        < table.indexOf("GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);"));
    }

    @Test
    public void nodeJarResearchPreviewShouldKeepTc4StackData() throws IOException {
        String research = read("src/main/java/thaumcraft/common/config/research/ConfigResearchBasics.java");
        int air = research.indexOf(".add(Aspect.AIR, 40)");
        int fire = research.indexOf(".add(Aspect.FIRE, 40)", air);
        int water = research.indexOf(".add(Aspect.WATER, 40)", fire);
        int earth = research.indexOf(".add(Aspect.EARTH, 40)", water);
        assertTrue("NODEJAR preview should retain the ordered TC4 primal aspects",
                air >= 0 && air < fire && fire < water && water < earth);
        assertTrue("NODEJAR preview should use normal type, no modifier and the original empty id",
                research.contains("item.setNodeAttributes(stack, NodeType.NORMAL, null, \"\");"));
    }

    private static void assertGeneratedLayers(String fileName, String frame, String pane) throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/item/" + fileName);
        assertTrue(fileName + " must inherit generated-item transforms", model.contains("\"parent\": \"item/generated\""));
        assertTrue(fileName + " must keep its TC4 frame layer", model.contains("\"layer0\": \"" + frame + "\""));
        assertTrue(fileName + " must keep its TC4 pane layer", model.contains("\"layer1\": \"" + pane + "\""));
    }

    private static void assertBlockDisplayParent(String path) throws IOException {
        assertTrue(path + " must inherit Forge block-item display transforms",
                read(path).contains("\"parent\": \"block/block\""));
    }

    private static void assertComplete3dDisplay(String model) {
        assertTrue(model.contains("\"gui\"")
                && model.contains("\"ground\"")
                && model.contains("\"fixed\"")
                && model.contains("\"thirdperson_righthand\"")
                && model.contains("\"thirdperson_lefthand\"")
                && model.contains("\"firstperson_righthand\"")
                && model.contains("\"firstperson_lefthand\""));
    }

    private static int occurrences(String text, String needle) {
        int count = 0;
        int from = 0;
        while ((from = text.indexOf(needle, from)) >= 0) {
            count++;
            from += needle.length();
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
