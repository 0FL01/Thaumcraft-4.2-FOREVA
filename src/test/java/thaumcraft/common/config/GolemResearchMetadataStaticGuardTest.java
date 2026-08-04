package thaumcraft.common.config;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GolemResearchMetadataStaticGuardTest {
    @Test
    public void golemResearchKeepsReviewedMetadata() throws Exception {
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

        int advanced = source.indexOf("ItemStack advancedGolem = new ItemStack(ConfigItems.itemGolemPlacer, 1, Short.MAX_VALUE);");
        int advancedTag = source.indexOf("advancedGolem.setTagInfo(\"advanced\", new NBTTagByte((byte) 1));", advanced);
        int advancedResearch = source.indexOf("\"ADVANCEDGOLEM\"", advancedTag);
        int advancedRegistration = source.indexOf(".registerResearchItem();", advancedResearch);
        int advancedWarp = source.indexOf("ThaumcraftApi.addWarpToResearch(\"ADVANCEDGOLEM\", 5);", advancedRegistration);
        assertTrue(advanced >= 0 && advancedTag > advanced && advancedResearch > advancedTag
                && advancedRegistration > advancedResearch && advancedWarp > advancedRegistration);
    }
}
