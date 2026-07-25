package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class Issues15To19ThaumcraftSixCompatibilityStaticGuardTest {

    private static final Set<String> ISSUE_ADDONS = new HashSet<>(Arrays.asList(
            "balkonsexpansion", "solarflux", "botania", "justenoughmagiculture", "jaopca"));

    @Test
    public void everyIssueDemandMustRemainInTheReviewedSemanticFloor() throws IOException {
        String demand = readFile("docs/compatibility/abi/tc6-addon-demand.txt");
        String target = readFile("docs/compatibility/abi/tc6-target.txt");

        for (String line : demand.split("\\R")) {
            String[] parts = line.split(" ");
            if (parts.length < 3 || !ISSUE_ADDONS.contains(parts[1])) {
                continue;
            }
            if ("A".equals(parts[0])) {
                assertTrue("Issue corpus entries must be promoted only after their runtime smoke passes",
                        "supported".equals(parts[2]));
                continue;
            }
            String symbol = parts[0] + " " + String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
            assertTrue("Missing semantic classification for " + symbol,
                    target.contains(" " + symbol + " | "));
        }
    }

    @Test
    public void compatibilityGateMustTrackTheCompleteDonorGapSet() throws IOException {
        String devScript = readFile("scripts/dev.sh");
        String gaps = readFile("docs/compatibility/abi/tc6-current-gaps.txt");

        assertTrue("compat-validate must compare the donor ABI to the built target",
                devScript.contains("tc6-compat.py\" abi-diff")
                        && devScript.contains("tc6-current-gaps.txt"));
        assertTrue("The reviewed gap snapshot must retain donor provenance and visible deferred gaps",
                gaps.startsWith("FORMAT tc6-abi-gaps-v1\nDONOR_SHA256 ")
                        && gaps.contains("G CLASS_MISSING C thaumcraft/api/"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
