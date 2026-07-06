package thaumcraft.common;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ThaumcraftAdvertisedVersionStaticGuardTest {

    @Test
    public void advertisedForgeVersionMustSatisfyEnderIoThaumcraftSixDependency() throws IOException {
        String build = readFile("build.gradle");
        Matcher version = Pattern.compile("ext\\.advertisedModVersion\\s*=\\s*\"([^\"]+)\"").matcher(build);

        assertTrue("build.gradle must declare a Forge-advertised Thaumcraft version", version.find());
        assertThaumcraftSixBetaAtLeast13(version.group(1));

        assertTrue("@Mod version replacement must use the advertised Forge mod version",
                build.contains("replace '@VERSION@', project.ext.advertisedModVersion"));
        assertTrue("mcmod.info expansion must use the advertised Forge mod version",
                build.contains("expand version: project.ext.advertisedModVersion"));
        assertTrue("processResources must track the advertised Forge mod version as an input",
                build.contains("inputs.property 'advertisedModVersion', project.ext.advertisedModVersion"));
    }

    private static void assertThaumcraftSixBetaAtLeast13(String version) {
        Matcher beta = Pattern.compile("^6\\.1\\.BETA(\\d+)$").matcher(version);
        assertTrue("advertised version must be compatible with EnderIO's thaumcraft@[6.1.BETA13,) dependency: " + version,
                beta.find() && Integer.parseInt(beta.group(1)) >= 13);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
