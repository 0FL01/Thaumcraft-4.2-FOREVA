package thaumcraft.common.lib.world;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VillageStructureParityStaticGuardTest {

    @Test
    public void wizardTowerKeepsTc4BlocksAndLocalDirections() throws Exception {
        String source = read("src/main/java/thaumcraft/common/lib/world/ComponentWizardTower.java");

        assertTrue(source.contains("0, 0, 0, 6, 12, 6, facing"));
        assertTrue(source.contains("for (int z = 0; z < 6; ++z)"));
        assertTrue(source.contains("for (int x = 0; x < 6; ++x)"));
        assertTrue(source.contains("IBlockState cobblestone = Blocks.COBBLESTONE.getDefaultState()"));
        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState())"));
        assertTrue(source.contains("IBlockState stoneStairs = Blocks.STONE_STAIRS.getDefaultState()"));
        assertTrue(source.contains("fillWithBlocks(world, bb, 2, 0, 2, 4, 0, 4, planks, planks, false)"));
        assertTrue(source.contains("Blocks.GLASS_PANE.getDefaultState()"));
        assertTrue(source.contains("Blocks.GLOWSTONE.getDefaultState()"));
        assertTrue(source.contains("withProperty(BlockLadder.FACING, EnumFacing.WEST)"));
        assertTrue(source.contains("withProperty(BlockTrapDoor.FACING, EnumFacing.WEST)"));
        assertTrue(source.contains("withProperty(BlockStairs.FACING, EnumFacing.NORTH)"));
        assertTrue(source.contains("createVillageDoor(world, bb, random, 3, 1, 1, EnumFacing.NORTH)"));

        assertFalse(source.contains("Blocks.OAK_FENCE"));
        assertFalse(source.contains("Blocks.OAK_FENCE_GATE"));
        assertFalse(source.contains("Blocks.BOOKSHELF"));
        assertFalse(source.contains("Blocks.DOUBLE_STONE_SLAB"));
    }

    @Test
    public void bankerHomeKeepsTc4BlocksAndBiomePalette() throws Exception {
        String source = read("src/main/java/thaumcraft/common/lib/world/ComponentBankerHome.java");

        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState())"));
        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState())"));
        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.LOG.getDefaultState())"));
        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState())"));
        assertTrue(source.contains("getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState()"));
        assertTrue(source.contains("fillWithBlocks(world, bb, 1, 4, 1, 2, 4, 3, log, log, false)"));
        assertTrue(source.contains("setBlockState(world, log, 1, 4, 0, bb)"));
        assertTrue(source.contains("Blocks.IRON_BARS.getDefaultState()"));
        assertTrue(source.contains("withProperty(BlockStairs.FACING, EnumFacing.NORTH)"));
        assertTrue(source.contains("createVillageDoor(world, bb, random, 1, 1, 0, EnumFacing.NORTH)"));

        assertFalse(source.contains("Blocks.OAK_STAIRS"));
        assertFalse(source.contains("Blocks.GLASS_PANE"));
        assertFalse(source.contains("Blocks.DOUBLE_STONE_SLAB"));
    }

    @Test
    public void villageCareersHaveDisplayNames() throws Exception {
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(lang.contains("\nentity.Villager.wizard=Wizard\n"));
        assertTrue(lang.contains("\nentity.Villager.banker=Banker\n"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
