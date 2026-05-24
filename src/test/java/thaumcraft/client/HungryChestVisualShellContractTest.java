package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class HungryChestVisualShellContractTest {

    @Test
    public void hungryChestShellLivesInBlockModelWhileTesrKeepsAnimatedLid() throws IOException {
        String renderer = read("src/main/java/thaumcraft/client/renderers/tile/TileChestHungryRenderer.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockchesthungry.json");
        String blockModel = read("src/main/resources/assets/thaumcraft/models/block/blockchesthungry.json");
        String block = read("src/main/java/thaumcraft/common/blocks/BlockChestHungry.java");

        assertTrue("BlockChestHungry should keep model-backed world rendering with horizontal facing state",
                block.contains("return EnumBlockRenderType.MODEL;")
                        && block.contains("PropertyDirection.create(\"facing\", EnumFacing.Plane.HORIZONTAL)")
                        && blockstate.contains("\"facing=north\": { \"model\": \"thaumcraft:blockchesthungry\", \"y\": 180 }")
                        && blockstate.contains("\"facing=east\": { \"model\": \"thaumcraft:blockchesthungry\", \"y\": 270 }"));

        assertTrue("TileChestHungryRenderer should keep only the animated lid/knob path after the static chest body moved into the block model",
                renderer.contains("this.model.chestLid.rotateAngleX")
                        && renderer.contains("this.model.chestKnob.rotateAngleX = this.model.chestLid.rotateAngleX;")
                        && renderer.contains("this.model.chestLid.render(0.0625F);")
                        && renderer.contains("this.model.chestKnob.render(0.0625F);")
                        && !renderer.contains("this.model.renderAll();"));

        assertTrue("Hungry chest block model should carry the static chest body shell instead of the old full cube placeholder",
                blockModel.contains("\"ambientocclusion\": false")
                        && blockModel.contains("\"shell\": \"thaumcraft:models/chesthungry\"")
                        && blockModel.contains("\"from\": [1, 0, 1]")
                        && blockModel.contains("\"to\": [15, 10, 15]")
                        && blockModel.contains("\"uv\": [3.5, 8.25, 7, 10.75]")
                        && blockModel.contains("\"uv\": [10.5, 8.25, 14, 10.75]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
