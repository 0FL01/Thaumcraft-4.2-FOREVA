package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ArcaneFurnaceVisualShellContractTest {

    @Test
    public void arcaneFurnaceShouldKeepNozzleFacingVariantsAndNonCubeShellModel() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String clientModelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String bakedModel = read("src/main/java/thaumcraft/client/renderers/block/ArcaneFurnaceBakedModel.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockarcanefurnace.json");
        String nozzleModel = read("src/main/resources/assets/thaumcraft/models/block/blockarcanefurnace_10.json");
        String itemBlock = read("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockArcaneFurnaceItem.java");
        int grateLayer = nozzleModel.indexOf("\"comment\": \"Outermost grate");
        int grinLayer = nozzleModel.indexOf("\"comment\": \"Grinning trim");
        int fireLayer = nozzleModel.indexOf("\"comment\": \"Animated fire");

        assertTrue("Arcane Furnace item metadata should resolve through the new facing-aware blockstate variant keys",
                clientProxy.contains("registerBlockItemModel(arcaneFurnaceItem, meta, \"type=\" + meta + \",facing=north\");"));

        assertTrue("Arcane Furnace blockstate should rotate the north-core fallback toward the core while its sheets face the exterior",
                blockstate.contains("\"facing=north,type=0\"")
                        && blockstate.contains("\"facing=east,type=0\"")
                        && blockstate.contains("\"facing=west,type=8\": {\n      \"model\": \"thaumcraft:blockarcanefurnace_7\"")
                        && blockstate.contains("\"facing=north,type=10\": {\n      \"model\": \"thaumcraft:blockarcanefurnace_10\"\n    }")
                        && blockstate.contains("\"facing=east,type=10\"")
                        && blockstate.contains("\"facing=south,type=10\": {\n      \"model\": \"thaumcraft:blockarcanefurnace_10\",\n      \"y\": 180")
                        && blockstate.contains("\"facing=west,type=10\"")
                        && blockstate.contains("\"y\": 90")
                        && blockstate.contains("\"y\": 270"));

        assertTrue("Arcane Furnace fallback should keep only TC4's exterior-facing grate, grin, and tall fire sheets",
                nozzleModel.contains("\"ambientocclusion\": false")
                        && nozzleModel.contains("\"grate\": \"thaumcraft:blocks/furnace13\"")
                        && nozzleModel.contains("\"grin\": \"thaumcraft:blocks/furnace15\"")
                        && nozzleModel.contains("\"fire\": \"minecraft:blocks/fire_layer_0\"")
                        && grateLayer >= 0 && grinLayer > grateLayer && fireLayer > grinLayer
                        && nozzleModel.substring(grateLayer, grinLayer).contains("\"to\": [16, 16, 6]")
                        && nozzleModel.substring(grinLayer, fireLayer).contains("\"to\": [16, 16, 3.2]")
                        && nozzleModel.substring(fireLayer).contains("\"to\": [16, 24, 1.6]")
                        && nozzleModel.contains("FACING points toward the lava core")
                        && nozzleModel.contains("\"south\": { \"texture\": \"#grate\" }")
                        && nozzleModel.contains("\"south\": { \"texture\": \"#grin\" }")
                        && nozzleModel.contains("\"south\": { \"texture\": \"#fire\" }")
                        && !nozzleModel.contains("Inner throat cavity")
                        && !nozzleModel.contains("\"parent\": \"block/cube_all\""));

        assertTrue("Arcane Furnace world rendering should be replaced with a baked model that mirrors the 1.7.10 per-face texture resolver instead of cube_all-per-meta mosaics",
                clientModelRegistry.contains("replaceArcaneFurnaceModels(event);")
                        && clientModelRegistry.contains("ARCANE_FURNACE_TEXTURES")
                        && bakedModel.contains("textureForSide(int meta, int level, int nozzleSide, EnumFacing face)")
                        && bakedModel.contains("BlockArcaneFurnace.RENDER_LEVEL")
                        && bakedModel.contains("BlockArcaneFurnace.NOZZLE_SIDE")
                        && bakedModel.contains("static List<NozzlePlane> nozzleGeometry(EnumFacing coreFacing)")
                        && bakedModel.contains("coreFacing.getOpposite()")
                        && bakedModel.contains("grate -> grin -> fire")
                        && bakedModel.contains("createNozzlePlane(coreFacing, 13, 10.0F, 16.0F)")
                        && bakedModel.contains("createNozzlePlane(coreFacing, 15, 12.8F, 16.0F)")
                        && bakedModel.contains("createNozzlePlane(coreFacing, NOZZLE_FIRE_TEXTURE, 14.4F, 24.0F)")
                        && bakedModel.contains("minecraft:blocks/lava_still")
                        && bakedModel.contains("return 2 + level + nozzleOffset;")
                        && bakedModel.contains("return level != 9 ? 7 : 6;")
                        && bakedModel.contains("case 2:")
                        && bakedModel.contains("return 16;")
                        && bakedModel.contains("case 8:")
                        && bakedModel.contains("return 25;"));

        assertTrue("Arcane Furnace metadata ItemBlock should not append .0..10 to the display key, so Waila/Hwyla shows the localized furnace name",
                itemBlock.contains("class BlockArcaneFurnaceItem extends BlockMetadataItem")
                        && itemBlock.contains("return this.block.getTranslationKey();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
