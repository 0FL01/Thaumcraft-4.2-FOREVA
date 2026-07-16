package thaumcraft.rendering;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MissingTextureParticleAssetContractTest {

    private static final Path ASSET_ROOT = Paths.get("src/main/resources/assets/thaumcraft");

    @Test
    public void reachableBlockModelTexturesMustBeAtlasCompatible() throws IOException {
        List<String> invalid = new ArrayList<>();
        try (Stream<Path> models = Files.list(ASSET_ROOT.resolve("models/block"))) {
            models.filter(path -> path.toString().endsWith(".json")).forEach(model -> {
                try {
                    JsonObject json = parse(model);
                    if (!json.has("textures")) return;
                    for (Map.Entry<String, JsonElement> texture : json.getAsJsonObject("textures").entrySet()) {
                        String reference = texture.getValue().getAsString();
                        if (!reference.startsWith("thaumcraft:")) continue;
                        Path png = ASSET_ROOT.resolve("textures")
                                .resolve(reference.substring("thaumcraft:".length()) + ".png");
                        if (!Files.exists(png)) {
                            invalid.add(model.getFileName() + " -> missing " + reference);
                            continue;
                        }
                        BufferedImage image = ImageIO.read(png.toFile());
                        if (image == null) {
                            invalid.add(model.getFileName() + " -> unreadable " + reference);
                        } else if (image.getWidth() != image.getHeight()
                                && !Files.exists(Paths.get(png.toString() + ".mcmeta"))) {
                            invalid.add(model.getFileName() + " -> non-square static sprite " + reference
                                    + " (" + image.getWidth() + "x" + image.getHeight() + ")");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        assertTrue("Invalid block-atlas texture references: " + invalid, invalid.isEmpty());
    }

    @Test
    public void centrifugeAndFocalManipulatorUseSafeTc4ParticleCarriers() throws IOException {
        JsonObject tube = parse(ASSET_ROOT.resolve("models/block/blocktube_2.json"));
        assertEquals("thaumcraft:blocks/pipe_1",
                tube.getAsJsonObject("textures").get("particle").getAsString());
        assertEquals("thaumcraft:models/centrifuge_inventory",
                tube.getAsJsonObject("textures").get("shell").getAsString());
        assertEquals(2, tube.getAsJsonArray("elements").size());
        tube.getAsJsonArray("elements").forEach(element -> element.getAsJsonObject()
                .getAsJsonObject("faces").entrySet().forEach(face ->
                        assertTrue("Centrifuge shell faces need bounded inventory-atlas UVs",
                                face.getValue().getAsJsonObject().has("uv"))));

        JsonObject focal = parse(ASSET_ROOT.resolve("models/block/blockstonedevice_13.json"));
        assertEquals("thaumcraft:blocks/pedestal_top",
                focal.getAsJsonObject("textures").get("particle").getAsString());
        assertTrue("The invisible focal manipulator carrier must not bake wandtable geometry",
                focal.getAsJsonArray("elements").size() == 0);
    }

    private static JsonObject parse(Path path) throws IOException {
        String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return new JsonParser().parse(json).getAsJsonObject();
    }
}
