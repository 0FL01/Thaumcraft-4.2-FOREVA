package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BowBoneItemRendererContractTest {

    @Test
    public void bowBoneShouldUseVanillaBowModelPlacement() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/itembowbone.json");
        String pulling0Model = read("src/main/resources/assets/thaumcraft/models/item/itembowbone_pulling_0.json");
        String pulling1Model = read("src/main/resources/assets/thaumcraft/models/item/itembowbone_pulling_1.json");
        String pulling2Model = read("src/main/resources/assets/thaumcraft/models/item/itembowbone_pulling_2.json");

        assertTrue("ClientProxy should let itemBowBone use the normal item model route",
                clientProxy.contains("ModelResourceLocation model = new ModelResourceLocation(registryName, \"inventory\");")
                        && clientProxy.contains("ModelLoader.setCustomModelResourceLocation(item, meta, model);"));
        assertFalse("Bone bow should not use a TEISR placement path when vanilla bow coordinates are used",
                clientProxy.contains("if (item == ConfigItems.itemBowBone) {")
                        || clientProxy.contains("ConfigItems.itemBowBone.setTileEntityItemStackRenderer(new ItemBowBoneRenderer());"));

        assertTrue("Bone bow should use vanilla Minecraft bow display coordinates for held contexts",
                hasVanillaBowDisplay(itemModel)
                        && hasVanillaBowDisplay(pulling0Model)
                        && hasVanillaBowDisplay(pulling1Model)
                        && hasVanillaBowDisplay(pulling2Model));

        assertTrue("Bone bow should keep vanilla pull override thresholds on the normal item model",
                itemModel.contains("\"pulling\": 1")
                        && itemModel.contains("\"pull\": 0.65")
                        && itemModel.contains("\"pull\": 0.9")
                        && itemModel.contains("\"model\": \"thaumcraft:item/itembowbone_pulling_0\"")
                        && itemModel.contains("\"model\": \"thaumcraft:item/itembowbone_pulling_1\"")
                        && itemModel.contains("\"model\": \"thaumcraft:item/itembowbone_pulling_2\""));
    }

    private static boolean hasVanillaBowDisplay(String model) {
        return model.contains("\"rotation\": [-80, 260, -40]")
                && model.contains("\"translation\": [-1, -2, 2.5]")
                && model.contains("\"scale\": [0.9, 0.9, 0.9]")
                && model.contains("\"rotation\": [0, -90, 25]")
                && model.contains("\"translation\": [1.13, 3.2, 1.13]")
                && model.contains("\"scale\": [0.68, 0.68, 0.68]");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
