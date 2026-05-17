package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityItemEventStaticGuardTest {

    @Test
    public void itemTossAndBathSaltsExpiryContractsShouldMatchReferenceBaseline() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("event.getEntityItem().getEntityData().setString(\"thrower\", event.getPlayer().getName());"));
        assertTrue(source.contains("expired.getItem() instanceof ItemBathSalts"));
        assertTrue(source.contains("state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0"));
        assertTrue(source.contains("ConfigBlocks.blockFluidPure.getDefaultState()"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
