package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ClientProxyEntityRendererCoverageByConfigTest {

    private static final Pattern CONFIG_ENTITY_PATTERN = Pattern.compile("makeEntry\\((Entity\\w+)\\.class");
    private static final Pattern CLIENT_RENDERER_PATTERN = Pattern.compile("registerEntityRenderer\\((Entity\\w+)\\.class");

    @Test
    public void everyConfiguredEntityHasExplicitRendererRegistration() throws IOException {
        String configSource = readFile("src/main/java/thaumcraft/common/config/ConfigEntities.java");
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        Set<String> configured = collect(CONFIG_ENTITY_PATTERN, configSource);
        Set<String> registered = collect(CLIENT_RENDERER_PATTERN, clientProxySource);

        Set<String> missing = new LinkedHashSet<>(configured);
        missing.removeAll(registered);

        assertTrue("ClientProxy is missing explicit renderer registrations for: " + missing, missing.isEmpty());
    }

    private static Set<String> collect(Pattern pattern, String source) {
        Set<String> values = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
