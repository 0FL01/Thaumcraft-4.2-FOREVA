package thaumcraft.rendering;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AlchemyFurnaceVisualRestorationContractTest {

    @Test
    public void bakedModelShouldRestoreTc4WorldAndItemFacesWithoutOwningIntegration() throws IOException {
        String model = read("src/main/java/thaumcraft/client/renderers/block/AlchemyFurnaceBakedModel.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String fallback = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_0.json");

        assertTrue("world furnace should be a full cube with the TC4 down, top-state, and four lateral-state textures",
                model.contains("EnumFacing.DOWN, this.sprite(item")
                        && model.contains("thaumcraft:blocks/al_furnace_side")
                        && model.contains("item || !filled")
                        && model.contains("thaumcraft:blocks/al_furnace_top_filled")
                        && model.contains("!item && burning")
                        && model.contains("thaumcraft:blocks/al_furnace_front_on")
                        && model.contains("EnumFacing.NORTH, lateral")
                        && model.contains("EnumFacing.SOUTH, lateral")
                        && model.contains("EnumFacing.WEST, lateral")
                        && model.contains("EnumFacing.EAST, lateral")
                        && model.contains("new Vector3f(0.0F, 0.0F, 0.0F)")
                        && model.contains("new Vector3f(16.0F, 16.0F, 16.0F)"));

        assertTrue("item rendering should use top on both vertical faces, front-off laterally, and delegate standard block transforms",
                model.contains("boolean item = state == null;")
                        && model.contains("? \"thaumcraft:blocks/al_furnace_top\"")
                        && model.contains(": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && model.contains("return this.delegate.getItemCameraTransforms();")
                        && model.contains("this.delegate.handlePerspective(cameraTransformType)"));

        assertTrue("the unwrapped fallback should be the same standard-transform full cube item appearance",
                fallback.contains("\"parent\": \"block/cube\"")
                        && fallback.contains("\"down\": \"thaumcraft:blocks/al_furnace_top\"")
                        && fallback.contains("\"up\": \"thaumcraft:blocks/al_furnace_top\"")
                        && fallback.contains("\"north\": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && fallback.contains("\"south\": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && fallback.contains("\"west\": \"thaumcraft:blocks/al_furnace_front_off\"")
                        && fallback.contains("\"east\": \"thaumcraft:blocks/al_furnace_front_off\""));
        assertTrue("the live stateful model and dynamic sprites must be installed explicitly",
                registry.contains("new ModelResourceLocation(\"thaumcraft:blockstonedevice\", \"type=0\")")
                        && registry.contains("new AlchemyFurnaceBakedModel(delegate)")
                        && registry.contains("blocks/al_furnace_top_filled")
                        && registry.contains("blocks/al_furnace_front_on"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
