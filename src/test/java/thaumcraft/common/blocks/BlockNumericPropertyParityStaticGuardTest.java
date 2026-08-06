package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockNumericPropertyParityStaticGuardTest {

    @Test
    public void constructorsKeepTc4HardnessResistanceAndLight() throws IOException {
        String crystal = read("BlockCrystal.java");
        assertTrue(crystal.contains("this.setLightLevel(0.5f);"));
        assertFalse(crystal.contains("public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)"));

        assertConstructor(read("BlockStoneDevice.java"), "this.setHardness(3.0f);", "this.setResistance(25.0f);");
        assertConstructor(read("BlockWoodenDevice.java"), "this.setHardness(2.5f);", "this.setResistance(10.0f);");
        assertConstructor(read("BlockMetalDevice.java"), "this.setHardness(3.0f);", "this.setResistance(17.0f);");
        assertConstructor(read("BlockCustomOre.java"), "this.setHardness(1.5f);", "this.setResistance(5.0f);");
        assertConstructor(read("BlockCosmeticOpaque.java"), "this.setHardness(1.5f);", "this.setResistance(5.0f);");
        assertConstructor(read("BlockTaint.java"), "this.setHardness(2.0f);", "this.setResistance(10.0f);");
        assertConstructor(read("BlockTaintFibres.java"), "this.setHardness(1.0f);", "this.setResistance(5.0f);");
    }

    @Test
    public void stateSpecificPropertiesKeepTc4Matrices() throws IOException {
        String solid = read("BlockCosmeticSolid.java");
        assertTrue(solid.contains("if (meta == 4 || meta == 6 || meta == 7) return 20.0f;"));

        String taint = read("BlockTaint.java");
        assertTrue(taint.contains("if (meta == 0) return 1.75F;")
                && taint.contains("if (meta == 1) return 1.5F;")
                && taint.contains("if (meta == 2) return 0.2F;"));

        String airy = read("BlockAiry.java");
        int constructorStart = airy.indexOf("public BlockAiry()");
        int constructorEnd = airy.indexOf("@Override", constructorStart);
        String constructor = airy.substring(constructorStart, constructorEnd);
        assertFalse(constructor.contains("setHardness") || constructor.contains("setResistance"));
        assertTrue(airy.contains("if (meta == 0 || meta == 5) return 2.0f;")
                && airy.contains("return super.getBlockHardness(state, world, pos);"));
    }

    @Test
    public void customPlantsKeepTc4FireRates() throws IOException {
        String plant = read("BlockCustomPlant.java");
        assertTrue(plant.contains("public int getFlammability(IBlockAccess world, BlockPos pos")
                && plant.contains("return 100;")
                && plant.contains("public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos")
                && plant.contains("return 60;"));
    }

    private static void assertConstructor(String source, String hardness, String resistance) {
        assertTrue(source.contains(hardness));
        assertTrue(source.contains(resistance));
    }

    private static String read(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/blocks/" + file)), StandardCharsets.UTF_8);
    }
}
