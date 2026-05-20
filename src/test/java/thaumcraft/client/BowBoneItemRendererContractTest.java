package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BowBoneItemRendererContractTest {

    @Test
    public void bowBoneShouldUseDedicatedBuiltinEntityRenderer() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/item/ItemBowBoneRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/itembowbone_tesr.json");

        assertTrue("ClientProxy should route itemBowBone through builtin/entity and install ItemBowBoneRenderer",
                clientProxy.contains("if (item == ConfigItems.itemBowBone) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"itembowbone_tesr\");")
                        && clientProxy.contains("ConfigItems.itemBowBone.setTileEntityItemStackRenderer(new ItemBowBoneRenderer());"));

        assertTrue("ItemBowBoneRenderer should preserve the reference equipped bow surface with pull-state model selection",
                renderer.contains("extends TileEntityItemStackRenderer")
                        && renderer.contains("new ModelResourceLocation(\"thaumcraft:itembowbone\", \"inventory\")")
                        && renderer.contains("new ModelResourceLocation(\"thaumcraft:itembowbone_pulling_0\", \"inventory\")")
                        && renderer.contains("new ModelResourceLocation(\"thaumcraft:itembowbone_pulling_1\", \"inventory\")")
                        && renderer.contains("new ModelResourceLocation(\"thaumcraft:itembowbone_pulling_2\", \"inventory\")")
                        && renderer.contains("getItemModelMesher().getModelManager().getModel(")
                        && renderer.contains("mc.getRenderItem().renderItem(stack, model)")
                        && renderer.contains("player.isHandActive()")
                        && renderer.contains("player.getItemInUseCount()"));

        assertTrue("Bone bow builtin/entity stub must stay in place so Forge dispatches the dedicated item renderer",
                itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
