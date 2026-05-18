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
        assertTrue("ConfigAspects should keep Stage 9 ore-dictionary baseline tags for stone/wood and key ore families",
                source.contains("registerObjectTag(\"stone\"")
                        && source.contains("registerObjectTag(\"cobblestone\"")
                        && source.contains("registerObjectTag(\"stairWood\"")
                        && source.contains("registerObjectTag(\"oreLapis\"")
                        && source.contains("registerObjectTag(\"oreDiamond\"")
                        && source.contains("registerObjectTag(\"gemDiamond\"")
                        && source.contains("registerObjectTag(\"oreRedstone\"")
                        && source.contains("registerObjectTag(\"dustRedstone\"")
                        && source.contains("registerObjectTag(\"oreEmerald\"")
                        && source.contains("registerObjectTag(\"gemEmerald\"")
                        && source.contains("registerObjectTag(\"oreQuartz\""));
        assertTrue("ConfigAspects should preserve ore-dictionary dye aspect baseline",
                source.contains("private static final String[] DYES")
                        && source.contains("for (String dye : DYES)")
                        && source.contains("registerObjectTag(dye, new AspectList().add(Aspect.SENSES, 1));"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
