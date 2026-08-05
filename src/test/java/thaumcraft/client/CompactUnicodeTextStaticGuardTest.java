package thaumcraft.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CompactUnicodeTextStaticGuardTest {

    @Test
    public void compactAsciiRenderingShouldAlwaysRestoreTheActiveUnicodeMode() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/lib/UtilsFX.java")), StandardCharsets.UTF_8);

        assertTrue("Compact text must only switch an active Unicode renderer for ASCII content",
                source.contains("renderer.getUnicodeFlag()")
                        && source.contains("text.charAt(index) > 0x7f")
                        && source.contains("renderer.setUnicodeFlag(false);"));
        assertTrue("Every compact text draw must restore Unicode mode in a finally block",
                source.contains("finally {\n            endCompactAscii(renderer, compactAscii);\n        }")
                        && source.contains("renderer.setUnicodeFlag(true);"));
        assertTrue("Legacy drawTag amounts and bonus counts must use the compact ASCII scope",
                source.contains("!readableAmount && beginCompactAscii(mc.fontRenderer, am)")
                        && source.contains("boolean compactAscii = beginCompactAscii(mc.fontRenderer, am);"));
    }
}
