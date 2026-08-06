package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigBlocksFluidParityStaticGuardTest {

    @Test
    public void pureFluidKeepsTc4LuminosityAndViscosity() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/config/ConfigBlocks.java")), StandardCharsets.UTF_8);
        int start = source.indexOf("FLUIDPURE = new Fluid(\"fluidPure\"");
        int end = source.indexOf("FluidRegistry.registerFluid(FLUIDPURE);", start);

        assertTrue("Missing pure-fluid descriptor", start >= 0 && end > start);
        String descriptor = source.substring(start, end);
        assertTrue(descriptor.contains(".setGaseous(false)")
                && descriptor.contains(".setLuminosity(10)")
                && descriptor.contains(".setViscosity(1000)")
                && descriptor.contains(".setRarity(EnumRarity.RARE)"));
    }
}
