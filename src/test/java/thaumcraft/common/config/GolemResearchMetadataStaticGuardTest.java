package thaumcraft.common.config;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GolemResearchMetadataStaticGuardTest {
    @Test
    public void strawSiblingsAndButcherWarpAreRegisteredWithTheirResearch() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/config/research/ConfigResearchGolemancy.java")),
                StandardCharsets.UTF_8);

        int straw = source.indexOf("\"GOLEMSTRAW\"");
        int siblings = source.indexOf(".setSiblings(\"COREGATHER\", \"GOLEMBELL\")", straw);
        int strawRegistration = source.indexOf(".registerResearchItem();", straw);
        assertTrue(straw >= 0 && siblings > straw && siblings < strawRegistration);

        int butcher = source.indexOf("\"COREBUTCHER\"");
        int butcherRegistration = source.indexOf(".registerResearchItem();", butcher);
        int warp = source.indexOf("ThaumcraftApi.addWarpToResearch(\"COREBUTCHER\", 1);", butcher);
        int liquid = source.indexOf("\"CORELIQUID\"", butcher);
        assertTrue(butcher >= 0 && butcherRegistration > butcher
                && warp > butcherRegistration && warp < liquid);
    }
}
