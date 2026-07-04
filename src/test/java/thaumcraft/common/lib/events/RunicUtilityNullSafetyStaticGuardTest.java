package thaumcraft.common.lib.events;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RunicUtilityNullSafetyStaticGuardTest {

    @Test
    public void runicStackHelpersMustAcceptNullStacksForDynamicRecipeRendering() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");
        String finalCharge = methodBody(source, "public static int getFinalCharge(ItemStack stack)");
        String hardening = methodBody(source, "public static int getHardening(ItemStack stack)");

        assertTrue("getFinalCharge must be null-safe for Thaumonomicon dynamic infusion pages",
                finalCharge.contains("if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor))"));
        assertTrue("getHardening must be null-safe like getFinalCharge",
                hardening.contains("if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor))"));
    }

    private static String methodBody(String source, String signature) {
        int start = source.indexOf(signature);
        if (start < 0) return "";
        int next = source.indexOf("\n    /**", start + signature.length());
        if (next < 0) next = source.length();
        return source.substring(start, next);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
