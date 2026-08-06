package thaumcraft.common.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigGlobalGateParityTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void cheatSheetDefaultsFalseAndPreservesExplicitTrue() throws Exception {
        String source = read("src/main/java/thaumcraft/common/config/Config.java");
        assertTrue(source.contains("config.get(\"general\", \"allow_cheat_sheet\", false).getBoolean(false)"));

        File file = temporaryFolder.newFile("thaumcraft.cfg");
        Map<String, Object> oldBlackboard = Launch.blackboard;
        Field minecraftHome = FMLInjectionData.class.getDeclaredField("minecraftHome");
        minecraftHome.setAccessible(true);
        Object oldMinecraftHome = minecraftHome.get(null);
        Launch.blackboard = new HashMap<String, Object>();
        Launch.blackboard.put("fml.deobfuscatedEnvironment", Boolean.TRUE);
        minecraftHome.set(null, temporaryFolder.getRoot());
        try {
            Configuration fresh = new Configuration(file);
            fresh.load();
            assertFalse(fresh.get("general", "allow_cheat_sheet", false).getBoolean(false));
            fresh.get("general", "allow_cheat_sheet", false).set(true);
            fresh.save();

            Configuration existing = new Configuration(file);
            existing.load();
            assertTrue(existing.get("general", "allow_cheat_sheet", false).getBoolean(false));
        } finally {
            minecraftHome.set(null, oldMinecraftHome);
            Launch.blackboard = oldBlackboard;
        }
    }

    @Test
    public void halloweenFirebatRegistrationStaysInsideSpawnGate() throws IOException {
        String source = read("src/main/java/thaumcraft/common/config/ConfigEntities.java");
        int start = source.indexOf("if (Config.spawnFireBat) {");
        int end = source.indexOf("if (Config.spawnWisp) {", start);
        assertTrue(start >= 0 && end > start);

        String section = source.substring(start, end);
        assertTrue(section.contains("if (isHalloween()) {\n"
                + "                addSpawn(EntityFireBat.class, 5, 1, 2"));
        assertFalse(source.substring(end).contains("if (isHalloween())"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
