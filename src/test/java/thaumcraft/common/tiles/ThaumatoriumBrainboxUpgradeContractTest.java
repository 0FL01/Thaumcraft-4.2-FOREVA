package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumatoriumBrainboxUpgradeContractTest {

    @Test
    public void tileThaumatoriumScansBrainboxUpgradesAndPrunesCapacityLists() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileThaumatorium.java");

        assertTrue("TileThaumatorium update must include periodic getUpgrades call",
                source.contains("this.getUpgrades();"));
        assertTrue("TileThaumatorium getUpgrades must filter blockMetalDevice meta 12",
                source.contains("state.getValue(BlockMetalDevice.TYPE) != 12"));
        assertTrue("TileThaumatorium getUpgrades must require brainbox facing toward thaumatorium",
                source.contains("((TileBrainbox) te).facing != dir.getOpposite()"));
        assertTrue("TileThaumatorium getUpgrades must increment recipe capacity per brainbox",
                source.contains("max += 2;"));
        assertTrue("TileThaumatorium getUpgrades must update maxRecipes",
                source.contains("this.maxRecipes = max;"));
        assertTrue("TileThaumatorium getUpgrades must prune recipeHash when capacity shrinks",
                source.contains("this.recipeHash.remove(index);"));
        assertTrue("TileThaumatorium getUpgrades must prune recipeEssentia when capacity shrinks",
                source.contains("this.recipeEssentia.remove(index);"));
        assertTrue("TileThaumatorium getUpgrades must prune recipePlayer when capacity shrinks",
                source.contains("this.recipePlayer.remove(index);"));
        assertTrue("TileThaumatorium completeRecipe must trigger block event for vent FX burst",
                source.contains("this.world.addBlockEvent(this.pos, this.getBlockType(), 0, 0);"));
        assertTrue("TileThaumatorium completeRecipe must keep reference fizz sound behavior",
                source.contains("SoundEvents.BLOCK_FIRE_EXTINGUISH")
                        && source.contains("2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F"));
        assertTrue("TileThaumatorium completeRecipe output spawn must preserve facing vertical offset",
                source.contains("this.facing.getOpposite().getYOffset()"));
        assertTrue("TileThaumatorium client update path must emit vent particles through proxy",
                source.contains("Thaumcraft.proxy.drawVentParticles(")
                        && source.contains("if (this.world.isRemote)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
