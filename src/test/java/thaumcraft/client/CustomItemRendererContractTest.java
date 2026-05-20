package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CustomItemRendererContractTest {

    @Test
    public void wandAndTrunkSpawnerShouldUseBuiltinEntityItemRenderers() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String wandRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemWandRenderer.java");
        String wandModel = read("src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java");
        String trunkRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemTrunkSpawnerRenderer.java");
        String wandItemModel = read("src/main/resources/assets/thaumcraft/models/item/wandcasting_tesr.json");
        String trunkItemModel = read("src/main/resources/assets/thaumcraft/models/item/trunkspawner_tesr.json");

        assertTrue("ClientProxy should override wandcasting and trunkspawner onto builtin/entity item models and install dedicated item renderers",
                clientProxy.contains("if (item == ConfigItems.itemWandCasting) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"wandcasting_tesr\");")
                        && clientProxy.contains("if (item == ConfigItems.itemTrunkSpawner) {")
                        && clientProxy.contains("registerBuiltinItemModel(item, meta, \"trunkspawner_tesr\");")
                        && clientProxy.contains("ConfigItems.itemWandCasting.setTileEntityItemStackRenderer(new ItemWandRenderer());")
                        && clientProxy.contains("ConfigItems.itemTrunkSpawner.setTileEntityItemStackRenderer(new ItemTrunkSpawnerRenderer());"));

        assertTrue("ItemWandRenderer and ModelWand should preserve the reference dynamic wand contract instead of a flat handheld icon",
                wandRenderer.contains("extends TileEntityItemStackRenderer")
                        && wandRenderer.contains("new ModelWand()")
                        && wandRenderer.contains("player.isHandActive()")
                        && wandRenderer.contains("wand.getFocus(stack)")
                        && wandModel.contains("wand.getRod(wandStack).getTexture()")
                        && wandModel.contains("wand.getCap(wandStack).getTexture()")
                        && wandModel.contains("wand.hasRunes(wandStack)")
                        && wandModel.contains("ItemWandCasting.isSceptre(wandStack)")
                        && wandModel.contains("textures/misc/script.png")
                        && wandModel.contains("textures/models/wand.png")
                        && wandModel.contains("drawRune("));

        assertTrue("ItemTrunkSpawnerRenderer should restore the chest-shell item render path instead of a flat generated sprite",
                trunkRenderer.contains("extends TileEntityItemStackRenderer")
                        && trunkRenderer.contains("new ModelChest()")
                        && trunkRenderer.contains("textures/models/trunk.png")
                        && trunkRenderer.contains("chest.chestBelow.render(0.0625F)")
                        && trunkRenderer.contains("chest.chestLid.render(0.0625F)")
                        && trunkRenderer.contains("chest.chestKnob.render(0.0625F)"));

        assertTrue("The wand and trunk item-model stubs must stay builtin/entity so Forge dispatches the dedicated item renderers",
                wandItemModel.contains("\"parent\": \"builtin/entity\"")
                        && trunkItemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
