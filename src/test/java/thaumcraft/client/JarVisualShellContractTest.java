package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class JarVisualShellContractTest {

    @Test
    public void jarShellLivesInBlockModelsWhileTesrKeepsDynamicContentsAndItemParity() throws IOException {
        String blockJar = read("src/main/java/thaumcraft/common/blocks/BlockJar.java");
        String jarRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileJarRenderer.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemJarRenderer.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockjar.json");
        String normalModel = read("src/main/resources/assets/thaumcraft/models/block/blockjar_0.json");
        String voidModel = read("src/main/resources/assets/thaumcraft/models/block/blockjar_1.json");

        assertTrue("BlockJar should use baked block-model rendering now that the static shell lives in block models",
                blockJar.contains("return EnumBlockRenderType.MODEL;"));

        assertTrue("TileJarRenderer should keep node, liquid, label, brain, and brine overlays while rendering the shell only for TEISR items or node-jar animation pulses",
                jarRenderer.contains("TileNodeRenderer.renderNodeAt((TileJarNode) tile")
                        && jarRenderer.contains("renderBrain((TileJarBrain) tile, x, y, z, partialTicks);")
                        && jarRenderer.contains("renderFillable((TileJarFillable) tile, x, y, z);")
                        && jarRenderer.contains("boolean renderShell = tile.getWorld() == null;")
                        && jarRenderer.contains("float shellScale = 1.0F;")
                        && jarRenderer.contains("if (renderShell) {")
                        && jarRenderer.contains("renderJarShell(tile, x, y, z, shellScale);")
                        && jarRenderer.contains("bindTexture(BRINE_TEXTURE);")
                        && !jarRenderer.contains("renderJarShell(tile, x, y, z);"));

        assertTrue("ItemJarRenderer should keep delegating to TileJarRenderer so filled jars, node jars, and brain jars retain dynamic item visuals",
                itemRenderer.contains("private final TileJarRenderer renderer = new TileJarRenderer();")
                        && itemRenderer.contains("renderer.render(tile, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);"));

        assertTrue("Jar blockstate should route brain and node jars to the normal shell and the void jar to the void shell",
                blockstate.contains("\"type=0\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=2\": { \"model\": \"thaumcraft:blockjar_0\" }")
                        && blockstate.contains("\"type=3\": { \"model\": \"thaumcraft:blockjar_1\" }"));

        assertTrue("Normal and void jar block models should now carry the shaped glass shell instead of the old full-cube placeholder",
                normalModel.contains("\"ambientocclusion\": false")
                        && normalModel.contains("\"from\": [3, 0, 3]")
                        && normalModel.contains("\"to\": [13, 12, 13]")
                        && normalModel.contains("\"from\": [5, 12, 5]")
                        && normalModel.contains("\"to\": [11, 14, 11]")
                        && voidModel.contains("\"side\": \"thaumcraft:blocks/jar_side_void\"")
                        && voidModel.contains("\"top\": \"thaumcraft:blocks/jar_top_void\"")
                        && voidModel.contains("\"from\": [3, 0, 3]")
                        && voidModel.contains("\"to\": [11, 14, 11]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
