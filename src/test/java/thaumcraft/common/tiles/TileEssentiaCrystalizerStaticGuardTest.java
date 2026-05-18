package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileEssentiaCrystalizerStaticGuardTest {

    @Test
    public void tubeVariantAndTileRegistrationShouldKeepEssentiaCrystalizerContracts() throws IOException {
        String blockTube = read("src/main/java/thaumcraft/common/blocks/BlockTube.java");
        String blockTubeItem = read("src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockTubeItem.java");
        String configBlocks = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String recipeSlice = read("src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java");
        String researchAlchemy = read("src/main/java/thaumcraft/common/config/research/ConfigResearchAlchemy.java");

        assertTrue(blockTube.contains("PropertyInteger.create(\"type\", 0, 7)"));
        assertTrue(blockTube.contains("case 7: return new TileEssentiaCrystalizer();"));
        assertTrue(blockTube.contains("for (int meta = 0; meta <= 7; ++meta)"));
        assertTrue(blockTube.contains("MathHelper.clamp(meta, 0, 7)"));
        assertTrue(blockTube.contains("this.getMetaFromState(state) == 7 ? FULL_BLOCK_AABB : TUBE_AABB"));

        assertTrue(blockTubeItem.contains("tile instanceof TileEssentiaCrystalizer"));
        assertTrue(blockTubeItem.contains("((TileEssentiaCrystalizer) tile).facing = side;"));

        assertTrue(configBlocks.contains("new TileRegistration(TileEssentiaCrystalizer.class, \"TileEssentiaCrystalizer\")"));
        assertTrue(recipeSlice.contains("new ItemStack(ConfigBlocks.blockTube, 1, 7)"));
        assertTrue(researchAlchemy.contains("new ItemStack(ConfigBlocks.blockTube, 1, 7)"));
    }

    @Test
    public void tileEssentiaCrystalizerShouldKeepCoreFlowContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileEssentiaCrystalizer.java");

        assertTrue(source.contains("public EnumFacing facing = EnumFacing.DOWN;"));
        assertTrue(source.contains("private static final int PROGRESS_MAX = 200;"));
        assertTrue(source.contains("VisNetHandler.drainVis("));
        assertTrue(source.contains("Aspect.EARTH"));
        assertTrue(source.contains("if (this.aspect != null && this.progress >= PROGRESS_MAX)"));
        assertTrue(source.contains("new ItemStack(ConfigItems.itemCrystalEssence, 1, 0)"));
        assertTrue(source.contains("InventoryUtils.placeItemStackIntoInventory"));
        assertTrue(source.contains("Thaumcraft.proxy.drawVentParticles("));
        assertTrue(source.contains("nbt.setString(\"Aspect\", this.aspect.getTag())"));
        assertTrue(source.contains("nbt.setByte(\"face\", (byte) this.facing.getIndex())"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
