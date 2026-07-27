package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorldgenCascadeDiagnosticsTest {

    @Test
    public void ordinaryPopulationShouldNotBeReportedAsRecursive() {
        StackTraceElement[] stack = {
                frame("net.minecraft.world.chunk.Chunk", "populate"),
                frame("net.minecraft.world.chunk.Chunk", "populate")
        };

        assertFalse(EventHandlerWorld.isRecursiveChunkPopulation(stack));
    }

    @Test
    public void nestedPopulationShouldBeReportedAsRecursive() {
        StackTraceElement[] stack = {
                frame("net.minecraft.world.chunk.Chunk", "populate"),
                frame("net.minecraft.world.chunk.Chunk", "populate"),
                frame("thaumcraft.common.lib.world.TestGenerator", "generate"),
                frame("net.minecraft.world.chunk.Chunk", "populate"),
                frame("net.minecraft.world.chunk.Chunk", "populate")
        };

        assertTrue(EventHandlerWorld.isRecursiveChunkPopulation(stack));
    }

    @Test
    public void diagnosticsShouldRemainOptInAndIncludeTheCallerStack() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java")),
                StandardCharsets.UTF_8);
        int methodStart = source.indexOf("public void onPopulateChunkPre");
        int methodEnd = source.indexOf("static boolean isRecursiveChunkPopulation", methodStart);
        assertTrue(methodStart >= 0 && methodEnd > methodStart);

        String method = source.substring(methodStart, methodEnd);
        int propertyGate = method.indexOf("if (!DEBUG_WORLDGEN_CASCADES) return;");
        int stackCapture = method.indexOf("Thread.currentThread().getStackTrace()");
        int callerLog = method.indexOf("caller.setStackTrace(stack)");

        assertTrue(source.contains("Boolean.getBoolean(WORLDGEN_CASCADE_DEBUG_PROPERTY)"));
        assertTrue(source.contains("thaumcraft.debugWorldgenCascades"));
        assertTrue(propertyGate >= 0 && propertyGate < stackCapture);
        assertTrue(stackCapture < callerLog);
    }

    private static StackTraceElement frame(String className, String methodName) {
        return new StackTraceElement(className, methodName, "Test.java", 1);
    }
}
