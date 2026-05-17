package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsCompatOreDictDustNuggetCoverageTest {

    @Test
    public void configAspectsRegistersCompatDustAndNuggetOreTags() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("ConfigAspects should register copper compat ore tags for ingot/nugget/dust",
                source.contains("registerObjectTag(\"ingotCopper\"")
                        && source.contains("registerObjectTag(\"nuggetCopper\"")
                        && source.contains("registerObjectTag(\"dustCopper\""));
        assertTrue("ConfigAspects should register tin compat ore tags for ingot/nugget/dust",
                source.contains("registerObjectTag(\"ingotTin\"")
                        && source.contains("registerObjectTag(\"nuggetTin\"")
                        && source.contains("registerObjectTag(\"dustTin\""));
        assertTrue("ConfigAspects should register silver compat ore tags for ingot/nugget/dust",
                source.contains("registerObjectTag(\"ingotSilver\"")
                        && source.contains("registerObjectTag(\"nuggetSilver\"")
                        && source.contains("registerObjectTag(\"dustSilver\""));
        assertTrue("ConfigAspects should register lead compat ore tags for ingot/nugget/dust",
                source.contains("registerObjectTag(\"ingotLead\"")
                        && source.contains("registerObjectTag(\"nuggetLead\"")
                        && source.contains("registerObjectTag(\"dustLead\""));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
