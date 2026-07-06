package thaumcraft.common.lib.utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class UtilsThaumcraftSixBiomeCompatStaticGuardTest {

    @Test
    public void utilsMustExposeThaumcraftSixBiomeMutationSurfaceForJeidMixin() throws IOException {
        String utils = readFile("src/main/java/thaumcraft/common/lib/utils/Utils.java");

        assertTrue("JEID overwrites the TC6 Utils.setBiomeAt(World, BlockPos, Biome, boolean) descriptor",
                utils.contains("public static void setBiomeAt(World world, BlockPos pos, Biome biome)")
                        && utils.contains("public static void setBiomeAt(World world, BlockPos pos, Biome biome, boolean sync)"));
        assertTrue("Legacy TC4 x/z callers must delegate through the BlockPos overload so JEID owns the shared mutation path",
                utils.contains("public static void setBiomeAt(World world, int x, int z, Biome biome)")
                        && utils.contains("setBiomeAt(world, new BlockPos(x, 0, z), biome, true);"));
        assertTrue("Non-JEID fallback must preserve TC4 biome sync behaviour while respecting the TC6 sync flag",
                utils.contains("byte[] biomes = chunk.getBiomeArray();")
                        && utils.contains("biomes[index] = (byte) (biomeId & 255);")
                        && utils.contains("chunk.setBiomeArray(biomes);")
                        && utils.contains("chunk.markDirty();")
                        && utils.contains("if (sync && !world.isRemote)")
                        && utils.contains("new PacketBiomeChange(x, z, (short) biomeId)"));
    }

    @Test
    public void utilsMustExposeThaumcraftSixBiomeResetSurface() throws IOException {
        String utils = readFile("src/main/java/thaumcraft/common/lib/utils/Utils.java");

        assertTrue("TC6 Utils exposes resetBiomeAt overloads that addons may link against",
                utils.contains("public static boolean resetBiomeAt(World world, BlockPos pos)")
                        && utils.contains("public static boolean resetBiomeAt(World world, BlockPos pos, boolean sync)")
                        && utils.contains("world.getBiomeProvider().getBiomes(null, pos.getX(), pos.getZ(), 1, 1)")
                        && utils.contains("if (biome != world.getBiome(pos))")
                        && utils.contains("setBiomeAt(world, pos, biome, sync);"));
    }

    @Test
    public void jeidSmokeModsetMustStayAvailable() throws IOException {
        String modset = readFile("scripts/smoke-modsets/jeid.txt");
        String script = readFile("scripts/dev.sh");

        assertTrue("JEID smoke must pin the coremod, trigger jars, and checksums from the reproduced crash",
                modset.contains("!mixinbooter-10.7.jar")
                        && modset.contains("JustEnoughIDs-1.0.4-SNAPSHOT-thin.jar")
                        && modset.contains("forestry_1.12.2-5.8.2.424.jar")
                        && modset.contains("MagicBees-1.12.2-3.2.25.jar")
                        && modset.contains("8bb53080aede2d2b1e1c5f713bbf49a38f20add8caa3d52fabcdf8a536b55a18")
                        && modset.contains("b556c5b98ea754a764ba0b0c863878d23fac2f750d0a4b5798dce14afbc7e2b1"));
        assertTrue("Smoke marker filtering must tolerate JEID optional mixin target discovery warnings without hiding fatal CNFE causes",
                script.contains("Error loading class: .*ClassNotFoundException: The specified class .* was not found"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
