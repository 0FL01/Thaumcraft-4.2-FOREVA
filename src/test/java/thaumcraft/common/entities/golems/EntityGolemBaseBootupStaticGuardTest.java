package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityGolemBaseBootupStaticGuardTest {

    @Test
    public void golemBaseKeepsClientBootupSoundContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");

        assertTrue("EntityGolemBase must keep client-side bootup decay and cameraticks sound contract",
                source.contains("else if (this.bootup > 0.0f && this.getCore() > -1)")
                        && source.contains("this.bootup *= this.bootup / 33.1f;")
                        && source.contains("TCSounds.CAMERATICKS")
                        && source.contains("this.bootup * 0.2f")
                        && source.contains("this.bootup,")
                        && source.contains("false);"));
        assertTrue("EntityGolemBase must keep bootup status packet trigger for client animation/sound",
                source.contains("else if (id == 7) this.bootup = 33.0f;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
