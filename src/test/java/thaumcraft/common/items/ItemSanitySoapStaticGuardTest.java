package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemSanitySoapStaticGuardTest {

    @Test
    public void sanitySoapKeepsReferenceUseCompletionAndConsumptionContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemSanitySoap.java");

        assertTrue("ItemSanitySoap must keep reference-shaped auto-stop gate at >195 use ticks",
                source.contains("if (used > 195)")
                        && source.contains("entity.resetActiveHand();"));
        assertTrue("ItemSanitySoap must keep reference-shaped sticky/temp warp cleanse path",
                source.contains("float chance = 0.33F;")
                        && source.contains("Config.potionWarpWard")
                        && source.contains("ConfigBlocks.blockFluidPure")
                        && source.contains("Thaumcraft.addStickyWarpToPlayer(player, -1);")
                        && source.contains("Thaumcraft.addWarpToPlayer(player, -knowledge.getWarpTemp(), true);"));
        assertTrue("ItemSanitySoap must consume one item on completed use regardless of creative mode",
                source.contains("stack.shrink(1);")
                        && !source.contains("if (!player.capabilities.isCreativeMode)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
