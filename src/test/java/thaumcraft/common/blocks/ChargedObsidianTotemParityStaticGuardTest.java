package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ChargedObsidianTotemParityStaticGuardTest {

    @Test
    public void chargedTotemShouldOwnItsNodeLifecycleAndConnectedRendering() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java");
        String worldgen = read("src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java");
        String model = read("src/main/java/thaumcraft/client/renderers/block/ObsidianTotemBakedModel.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String tileModel = read("src/main/resources/assets/thaumcraft/models/block/blockcosmeticsolid_1.json");

        assertTrue("Charged totems must keep the TileNode in the same BlockCosmeticSolid state",
                block.contains("public static final int TYPE_CHARGED_TOTEM = 8;")
                        && block.contains("return meta == TYPE_WARDING || meta == TYPE_CHARGED_TOTEM;")
                        && block.contains("if (meta == TYPE_CHARGED_TOTEM) return new TileNode();")
                        && worldgen.contains("world.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(8), 3);")
                        && worldgen.contains("createRandomNodeAt(world, pos, rand, false, true, false);")
                        && worldgen.contains("if (alreadyHasTileNode)"));

        assertTrue("Breaking the composite block must use the original tile drop and node essence lifecycle",
                block.contains("return meta == TYPE_CHARGED_TOTEM ? TYPE_OBSIDIAN_TILE : meta;")
                        && block.contains("meta <= 1 || meta == TYPE_CHARGED_TOTEM")
                        && block.contains("tile instanceof INode")
                        && block.contains("aspects.getAmount(aspect) / 10")
                        && block.contains("spawnAsEntity(world, pos, essence);"));

        assertTrue("Totem metas 0 and 8 must share the original neighbor-aware side texture routing",
                block.contains("new IUnlistedProperty[]{TOTEM_STYLE, TOTEM_VARIANT}")
                        && block.contains("if (isTotem(world.getBlockState(pos.up())))")
                        && block.contains("else if (isTotem(world.getBlockState(pos.down())))")
                        && model.contains("obsidiantotembase")
                        && model.contains("obsidiantotembaseshaded")
                        && model.contains("obsidiantotem1")
                        && model.contains("obsidiantotem4")
                        && registry.contains("new int[]{0, 8}")
                        && registry.contains("new ObsidianTotemBakedModel(delegate)")
                        && tileModel.contains("thaumcraft:blocks/obsidiantile"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
