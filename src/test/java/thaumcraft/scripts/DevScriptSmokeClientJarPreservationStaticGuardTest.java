package thaumcraft.scripts;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class DevScriptSmokeClientJarPreservationStaticGuardTest {

    @Test
    public void smokeClientMustRestoreProductionJarAfterRunClient() throws IOException {
        String script = readFile("scripts/dev.sh");

        assertTrue("smoke-client must back up the production jar before runClient can overwrite it with deobfuscated classes",
                script.contains("smoke_client()")
                        && script.contains("local prod_jar=\"$ROOT/build/libs/Thaumcraft-1.0.0-universal.jar\"")
                        && script.contains("cp -p \"$prod_jar\" \"$jar_backup\"")
                        && script.contains("runClient -x getAssets --console=plain")
                        && script.contains("cp -p \"$jar_backup\" \"$prod_jar\"")
                        && script.contains("rm -f \"$prod_jar\""));
    }

    @Test
    public void validateMustProduceAndCheckProductionReobfJar() throws IOException {
        String script = readFile("scripts/dev.sh");

        assertTrue("validate must run reobfJar before check-jar and fail on strict MCP leak checks",
                script.contains("validate_step_batch_gradle 'compile+test+reobf' compileJava test jar reobfJar")
                        && script.contains("check_jar > \"$check_file\" 2>&1")
                        && script.contains("printf 'MCP leaks found; log: %s'"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
