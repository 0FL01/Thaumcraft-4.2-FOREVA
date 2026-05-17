package thaumcraft.common.items.wands;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WandManagerTriggerStaticGuardTest {

    @Test
    public void commonProxyAndConfigRecipesShouldKeepWandTriggerRegistrationContract() throws IOException {
        String proxy = readFile("src/main/java/thaumcraft/common/CommonProxy.java");
        String recipes = readFile("src/main/java/thaumcraft/common/config/ConfigRecipes.java");

        assertTrue(proxy.contains("public final WandManager wandManager = new WandManager();"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 0, Blocks.BOOKSHELF, 0, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 1, Blocks.CAULDRON, -1, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 5, ConfigBlocks.blockMetalDevice, 9, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 7, ConfigBlocks.blockMetalDevice, 9, \"Thaumcraft_2\");"));
    }

    @Test
    public void wandManagerShouldKeepTriggerDispatcherAndCoreTransformMethods() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/wands/WandManager.java");

        assertTrue(source.contains("public class WandManager implements IWandTriggerManager"));
        assertTrue(source.contains("switch (event)"));
        assertTrue(source.contains("case 0:"));
        assertTrue(source.contains("case 1:"));
        assertTrue(source.contains("case 5:"));
        assertTrue(source.contains("return createThaumonomicon(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createCrucible(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createThaumatorium(wand, player, world, x, y, z, side);"));
        assertTrue(source.contains("public static boolean createThaumonomicon("));
        assertTrue(source.contains("public static boolean createCrucible("));
        assertTrue(source.contains("public static boolean createThaumatorium("));
        assertTrue(source.contains("new PacketFXBlockSparkle(center.getX(), center.getY(), center.getZ(), -9999)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
