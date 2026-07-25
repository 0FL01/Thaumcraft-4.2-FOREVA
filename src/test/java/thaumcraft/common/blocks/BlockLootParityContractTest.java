package thaumcraft.common.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BlockLootParityContractTest {
    private static final Path ASSET_ROOT = Paths.get("src/main/resources/assets/thaumcraft");
    private static final String[] SIDE_FACES = {"north", "south", "west", "east"};

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void worldModelsUseOriginalCrateAndUrnBounds() throws IOException {
        String[][] crateBounds = {{"[1,0,1]", "[15,14,15]"}};
        String[][] urnBounds = {
                {"[3,0,3]", "[13,1,13]"},
                {"[2,1,2]", "[14,13,14]"},
                {"[4,13,4]", "[12,16,12]"}
        };
        for (int meta = 0; meta <= 2; meta++) {
            assertWorldModel("crate", meta, crateBounds);
            assertWorldModel("urn", meta, urnBounds);
        }
    }

    @Test
    public void everyWorldVariantAndFlatItemSpriteIsReachable() throws IOException {
        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        for (String type : new String[]{"crate", "urn"}) {
            JsonObject variants = parse(ASSET_ROOT.resolve("blockstates/blockloot" + type + ".json"))
                    .getAsJsonObject("variants");
            for (int meta = 0; meta <= 2; meta++) {
                assertEquals("thaumcraft:blockloot" + type + "_" + meta,
                        variants.getAsJsonObject("type=" + meta).get("model").getAsString());

                String itemName = "blockloot" + type + (meta == 0 ? "" : "_" + meta);
                JsonObject itemModel = parse(ASSET_ROOT.resolve("models/item/" + itemName + ".json"));
                assertEquals("item/generated", itemModel.get("parent").getAsString());
                assertEquals("thaumcraft:blocks/" + type + "_side_" + meta,
                        itemModel.getAsJsonObject("textures").get("layer0").getAsString());
                assertFalse(itemModel.has("elements"));
            }
            assertEquals(1, occurrences(proxy, "String[] loot" + capitalize(type) + "ItemModels"));
            assertEquals(1, occurrences(proxy, "registerBuiltinItemModel(loot" + capitalize(type)
                    + "Item, meta, loot" + capitalize(type) + "ItemModels[meta]);"));
        }
    }

    @Test
    public void lootItemsKeepOriginalNamesRaritiesAndTooltipLine() throws IOException {
        String lang = read(ASSET_ROOT.resolve("lang/en_us.lang"));
        assertEquals(1, occurrences(lang, "tile.thaumcraft.loot_urn.name=Old Urn"));
        assertEquals(1, occurrences(lang, "tile.thaumcraft.loot_crate.name=Abandoned Crate"));

        BlockLootItem item = new BlockLootItem(new BlockLoot(Material.ROCK, 1));
        EnumRarity[] expected = {EnumRarity.COMMON, EnumRarity.UNCOMMON, EnumRarity.RARE};
        for (int meta = 0; meta <= 2; meta++) {
            ItemStack stack = new ItemStack(item, 1, meta);
            assertEquals(expected[meta], item.getRarity(stack));
            List<String> tooltip = new ArrayList<>();
            item.addInformation(stack, null, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
            assertEquals(Collections.singletonList(expected[meta].rarityName), tooltip);
        }
    }

    private static void assertWorldModel(String type, int meta, String[][] expectedBounds) throws IOException {
        JsonObject model = parse(ASSET_ROOT.resolve("models/block/blockloot" + type + "_" + meta + ".json"));
        JsonObject textures = model.getAsJsonObject("textures");
        assertEquals("block/block", model.get("parent").getAsString());
        assertEquals("thaumcraft:blocks/" + type + "_top", textures.get("top").getAsString());
        assertEquals("thaumcraft:blocks/" + type + "_side_" + meta, textures.get("side").getAsString());
        assertEquals("thaumcraft:blocks/" + type + "_side_" + meta, textures.get("particle").getAsString());

        JsonArray elements = model.getAsJsonArray("elements");
        assertEquals(expectedBounds.length, elements.size());
        for (int i = 0; i < expectedBounds.length; i++) {
            JsonObject element = elements.get(i).getAsJsonObject();
            assertEquals(expectedBounds[i][0], element.getAsJsonArray("from").toString());
            assertEquals(expectedBounds[i][1], element.getAsJsonArray("to").toString());
            JsonObject faces = element.getAsJsonObject("faces");
            assertEquals(6, faces.entrySet().size());
            assertFace(faces.getAsJsonObject("down"), "#top");
            assertFace(faces.getAsJsonObject("up"), "#top");
            for (String face : SIDE_FACES) {
                assertFace(faces.getAsJsonObject(face), "#side");
            }
        }
    }

    private static void assertFace(JsonObject face, String texture) {
        assertEquals(texture, face.get("texture").getAsString());
        assertFalse(face.has("uv"));
        assertFalse(face.has("cullface"));
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static int occurrences(String value, String needle) {
        int count = 0;
        for (int index = 0; (index = value.indexOf(needle, index)) >= 0; index += needle.length()) {
            count++;
        }
        return count;
    }

    private static JsonObject parse(Path path) throws IOException {
        return new JsonParser().parse(read(path)).getAsJsonObject();
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
