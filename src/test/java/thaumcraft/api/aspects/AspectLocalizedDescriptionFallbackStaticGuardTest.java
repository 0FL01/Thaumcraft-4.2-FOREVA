package thaumcraft.api.aspects;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AspectLocalizedDescriptionFallbackStaticGuardTest {
    @Test
    public void aspectLocalizedDescriptionShouldFallBackToLegacyHelpKeys() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/api/aspects/Aspect.java")), StandardCharsets.UTF_8);
        String clientProxy = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java")), StandardCharsets.UTF_8);

        assertTrue("Aspect.getLocalizedDescription should resolve tc.aspect.<tag> first and fall back to tc.aspect.help.<tag> so en_us notifications keep their descriptive text",
                source.contains("String description = I18n.translateToLocal((String)(\"tc.aspect.\" + this.tag));")
                        && source.contains("tc.aspect.help.")
                        && source.contains("return description;"));
        assertTrue("Thaumometer discovery errors should route through the shared aspect description formatter instead of emitting raw help keys",
                clientProxy.contains("formatThaumometerAspectDescription(missingAspect)")
                        && clientProxy.contains("return description;"));
    }
}
