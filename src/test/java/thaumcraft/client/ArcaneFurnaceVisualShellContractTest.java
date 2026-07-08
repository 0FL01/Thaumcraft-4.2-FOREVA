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

        assertTrue("Arcane Furnace item metadata should resolve through the new facing-aware blockstate variant keys",
                clientProxy.contains("registerBlockItemModel(arcaneFurnaceItem, meta, \"type=\" + meta + \",facing=north\");"));

        assertTrue("Arcane Furnace blockstate should provide every facing/type permutation with correct rotations: y:180 for north (flip south-designed model), no rotation for south, y:90 for east, y:270 for west",
                blockstate.contains("\"facing=north,type=0\"")
                        && blockstate.contains("\"facing=east,type=0\"")
                        && blockstate.contains("\"facing=west,type=8\": {\n      \"model\": \"thaumcraft:blockarcanefurnace_7\"")
                        && blockstate.contains("\"facing=north,type=10\"")
                        && blockstate.contains("\"facing=east,type=10\"")
                        && blockstate.contains("\"facing=south,type=10\"")
                        && blockstate.contains("\"facing=west,type=10\"")
                        && blockstate.contains("\"y\": 90")
                        && blockstate.contains("\"y\": 270"));

        assertTrue("Arcane Furnace nozzle model should use south-facing pancake-layer structure: half-block body in north half, inner throat, opening/trim/fire overlays at z=12-15 with south faces",
                nozzleModel.contains("\"ambientocclusion\": false")
                        && nozzleModel.contains("\"front\": \"thaumcraft:blocks/furnace13\"")
                        && nozzleModel.contains("\"trim\": \"thaumcraft:blocks/furnace15\"")
                        && nozzleModel.contains("\"inner\": \"thaumcraft:blocks/furnace9\"")
                        && nozzleModel.contains("\"fire\": \"minecraft:blocks/fire_layer_0\"")
                        // Body half-block in north half (z=0..8), south face = furnace9
                        && nozzleModel.contains("\"from\": [0, 0, 0]")
                        && nozzleModel.contains("\"to\": [16, 16, 8]")
                        // Inner throat cavity (z=8..12)
                        && nozzleModel.contains("\"from\": [4, 4, 8]")
                        && nozzleModel.contains("\"to\": [12, 12, 12]")
                        // Opening face (furnace13) at z=12-13
                        && nozzleModel.contains("\"from\": [0, 0, 12]")
                        && nozzleModel.contains("\"to\": [16, 16, 13]")
                        // Trim face (furnace15) at z=13-14
                        && nozzleModel.contains("\"from\": [0, 0, 13]")
                        && nozzleModel.contains("\"to\": [16, 16, 14]")
                        // Fire face at z=14-15, shade=false
                        && nozzleModel.contains("\"shade\": false")
                        && nozzleModel.contains("\"from\": [0, 0, 14]")
                        && nozzleModel.contains("\"to\": [16, 16, 15]")
                        // South faces on overlays (for south-facing viewer)
                        && nozzleModel.contains("\"south\": { \"texture\": \"#front\" }")
                        && nozzleModel.contains("\"south\": { \"texture\": \"#trim\" }")
                        && nozzleModel.contains("\"south\": { \"texture\": \"#fire\" }")
                        && !nozzleModel.contains("\"parent\": \"block/cube_all\""));

        assertTrue("Arcane Furnace world rendering should be replaced with a baked model that mirrors the 1.7.10 per-face texture resolver instead of cube_all-per-meta mosaics",
                clientModelRegistry.contains("replaceArcaneFurnaceModels(event);")
                        && clientModelRegistry.contains("ARCANE_FURNACE_TEXTURES")
                        && bakedModel.contains("textureForSide(int meta, int level, int nozzleSide, EnumFacing face)")
                        && bakedModel.contains("BlockArcaneFurnace.RENDER_LEVEL")
                        && bakedModel.contains("BlockArcaneFurnace.NOZZLE_SIDE")
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
