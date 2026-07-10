package thaumcraft.common.items.wands;

import org.junit.Test;
import thaumcraft.common.config.ConfigRecipesSourceReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WandManagerTriggerStaticGuardTest {

    @Test
    public void commonProxyAndConfigRecipesShouldKeepWandTriggerRegistrationContract() throws IOException {
        String proxy = readFile("src/main/java/thaumcraft/common/CommonProxy.java");
        String recipes = ConfigRecipesSourceReader.readMergedSource();

        assertTrue(proxy.contains("public final WandManager wandManager = new WandManager();"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 0, Blocks.BOOKSHELF, 0, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 1, Blocks.CAULDRON, -1, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.OBSIDIAN, -1, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.NETHER_BRICK, -1, \"Thaumcraft\");"));
        assertTrue(recipes.contains("WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 2, Blocks.IRON_BARS, -1, \"Thaumcraft\");"));
        assertTrue(recipes.contains("new ItemStack(Blocks.IRON_BARS)"));
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
        assertTrue(source.contains("case 2:"));
        assertTrue(source.contains("case 3:"));
        assertTrue(source.contains("case 4:"));
        assertTrue(source.contains("case 5:"));
        assertTrue(source.contains("case 6:"));
        assertTrue(source.contains("case 7:"));
        assertTrue(source.contains("return createThaumonomicon(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createCrucible(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createArcaneFurnace(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createInfusionAltar(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createNodeJar(wand, player, world, x, y, z);"));
        assertTrue(source.contains("return createThaumatorium(wand, player, world, x, y, z, side);"));
        assertTrue(source.contains("return createOculus(wand, player, world, x, y, z, side);"));
        assertTrue(source.contains("return createAdvancedAlchemicalFurnace(wand, player, world, x, y, z, side);"));
        assertTrue(source.contains("public static boolean createThaumonomicon("));
        assertTrue(source.contains("public static boolean createCrucible("));
        assertTrue(source.contains("public static boolean createThaumatorium("));
        assertTrue(source.contains("private static boolean fitArcaneFurnace("));
        assertTrue(source.contains("private static boolean replaceArcaneFurnace("));
        assertTrue(source.contains("private static boolean fitInfusionAltar("));
        assertTrue(source.contains("private static void replaceInfusionAltar("));
        assertTrue(source.contains("private static boolean fitNodeJar("));
        assertTrue(source.contains("private static void replaceNodeJar("));
        assertTrue(source.contains("private static boolean containsMatch("));
        assertTrue(source.contains("ConfigBlocks.blockJar.getDefaultState().withProperty(thaumcraft.common.blocks.BlockJar.TYPE, 2)"));
        assertTrue(source.contains("jar.setNodeType(NodeType.values()[nodeType]);"));
        assertTrue(source.contains("jar.setId(nodeId);"));
        assertTrue(source.contains("private static boolean createOculus("));
        assertTrue(source.contains("return ((TileEldritchAltar) tile).onWandRightClick(world, wandStack, player, x, y, z, side, meta) == 1;"));
        assertTrue(source.contains("private static boolean createAdvancedAlchemicalFurnace("));
        assertTrue(source.contains("new AspectList().add(Aspect.FIRE, 50).add(Aspect.WATER, 50).add(Aspect.ORDER, 50)"));
        assertTrue(source.contains("matchesMetalDeviceRing(world, center, 0, 3, 3)"));
        assertTrue(source.contains("matchesMetalDeviceRing(world, center, 1, 1, 9)"));
        assertTrue(source.indexOf("if (matchesAdvancedAlchemyInput(world, center))")
                < source.indexOf("int legacyRingOffset = findLegacyAdvancedAlchemyRingOffset(world, center);"));
        assertFalse(source.contains("matchesAdvancedAlchemyInput(world, center, -1)"));
        assertTrue(source.contains("matchesLegacyAdvancedAlchemyLayout(world, center, -1)"));
        assertFalse(source.contains("matchesLegacyAdvancedAlchemyLayout(world, center, 1)"));
        assertTrue(source.indexOf("int legacyRingOffset = findLegacyAdvancedAlchemyRingOffset(world, center);")
                < source.lastIndexOf("wand.consumeAllVisCrafting(wandStack, player,"));
        assertTrue(source.contains("matchesMetalDeviceRing(world, center, ringOffset, 1, 9)"));
        assertTrue(source.contains("hasSafeLegacyShellTileData(world, center, ringOffset)"));
        assertTrue(source.contains("alembic.aspect != null || alembic.aspectFilter != null || alembic.amount != 0"));
        assertTrue(source.contains("!world.isAirBlock(center.add(dx, 1, dz))"));
        assertTrue(source.contains("old upper output is byte-for-byte identical to the canonical TC4 input"));
        assertTrue(source.contains("matchesAdvancedAlchemyFurnace(world, center)"));
        assertTrue(source.contains("ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(0)"));
        assertTrue(source.contains("ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(corner ? 4 : 1)"));
        assertTrue(source.contains("ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(corner ? 2 : 3)"));
        assertTrue(source.contains("world.setBlockToAir(center.add(dx, legacyRingOffset, dz))"));
        assertTrue(source.contains("sourceData.removeTag(\"Items\")"));
        assertTrue(source.contains("transferOrDropInventory(world, center, inventory)"));
        assertTrue(source.contains("new PacketFXBlockSparkle(center.getX(), center.getY(), center.getZ(), -9999)"));
        assertTrue(source.contains("Blocks.NETHER_BRICK"));
        assertTrue(source.contains("Blocks.IRON_BARS"));
        assertTrue(source.contains("Blocks.FLOWING_LAVA"));
        assertTrue(source.contains("ConfigBlocks.blockArcaneFurnace.getDefaultState().withProperty(BlockArcaneFurnace.TYPE, meta), 0)"));
        assertTrue(source.contains("ConfigBlocks.blockStoneDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockStoneDevice.TYPE, 3)"));
        assertTrue(source.contains("((TileInfusionMatrix) tile).active = true;"));
        assertTrue(source.contains("new PacketFXBlockSparkle(center.getX(), center.getY(), center.getZ(), -9999)"));
    }

    @Test
    public void thaumonomiconTriggerShouldWorkWithEquippedFocus() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/wands/WandManager.java");

        assertFalse("Creating the Thaumonomicon from a bookshelf must not be blocked by an equipped wand focus; the block trigger runs before focus casting and should still consume the bookshelf.",
                source.contains("if (wand.getFocus(wandStack) != null) return false;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
