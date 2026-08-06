package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BlockHarvestParityRuntimeTest {

    private static final String[] NO_EXPLICIT_GATE = {
            "BlockCrystal.java", "BlockArcaneFurnace.java", "BlockCosmeticStoneSlab.java",
            "BlockCosmeticWoodSlab.java", "BlockMetalDevice.java", "BlockTable.java",
            "BlockMagicalLog.java", "BlockTaint.java", "BlockStoneDevice.java",
            "BlockWoodenDevice.java", "BlockCosmeticOpaque.java", "BlockEldritch.java",
            "BlockCosmeticSolid.java"
    };

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void customOreKeepsPickaxeTwoOnlyForCinnabarAndAmber() {
        BlockCustomOre ore = new BlockCustomOre();
        assertGate(ore, 0, "pickaxe", 2);
        assertGate(ore, 7, "pickaxe", 2);
        for (int meta = 1; meta <= 6; meta++) {
            IBlockState state = ore.getStateFromMeta(meta);
            assertNull("Unexpected harvest tool for ore meta " + meta, ore.getHarvestTool(state));
            assertEquals("Unexpected harvest level for ore meta " + meta, -1, ore.getHarvestLevel(state));
        }
    }

    @Test
    public void tc4BlocksDoNotKeepPortAddedWholeClassGates() throws IOException {
        for (String file : NO_EXPLICIT_GATE) {
            String source = read(file);
            assertFalse(file + " retains a port-added harvest gate", source.contains("setHarvestLevel("));
        }
    }

    @Test
    public void wardedShellKeepsTc4HarvestPredicate() {
        assertTrue(new BlockWarded().canHarvestBlock(null, null, null));
    }

    private static void assertGate(BlockCustomOre ore, int meta, String tool, int level) {
        IBlockState state = ore.getStateFromMeta(meta);
        assertEquals(tool, ore.getHarvestTool(state));
        assertEquals(level, ore.getHarvestLevel(state));
    }

    private static String read(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/blocks/" + file)), StandardCharsets.UTF_8);
    }
}
