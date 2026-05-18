package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigBiomeInfoRegistrationStaticGuardTest {

    @Test
    public void registerBiomesKeepsFrozenOrderBiomeEntryFromReference() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/Config.java");

        assertTrue("Config.registerBiomes should derive and register a FROZEN biome type baseline",
                source.contains("BiomeDictionary.Type.getType(\"FROZEN\", net.minecraftforge.common.BiomeDictionary.Type.SNOWY)")
                        && source.contains("net.minecraftforge.common.BiomeDictionary.addTypes(biome, frozenType);"));
        assertTrue("Config.registerBiomes should keep both SNOWY and FROZEN order aspect entries",
                source.contains("net.minecraftforge.common.BiomeDictionary.Type.SNOWY, 80, Aspect.ORDER, false, 0.0f);")
                        && source.contains("frozenType, 100, Aspect.ORDER, false, 0.0f);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
