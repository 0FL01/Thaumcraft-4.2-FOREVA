package thaumcraft.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdvancedAlchemyFurnaceCommonBackendStaticGuardTest {

    @Test
    public void blockKeepsTc4LayoutRestorationAndInvisibleStructureContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockAlchemyFurnace.java");

        assertTrue(source.contains("public static final int CENTER = 0;"));
        assertTrue(source.contains("public static final int LOWER_NOZZLE = 1;"));
        assertTrue(source.contains("public static final int UPPER_CORNER = 2;"));
        assertTrue(source.contains("public static final int UPPER_CARDINAL = 3;"));
        assertTrue(source.contains("public static final int LOWER_CORNER = 4;"));
        assertTrue(source.contains("return EnumBlockRenderType.INVISIBLE;"));
        assertTrue(source.contains("public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)"));
        assertTrue(source.contains("return ItemStack.EMPTY;"));
        assertTrue(source.contains("return new TileAlchemyFurnaceAdvanced();"));
        assertTrue(source.contains("return new TileAlchemyFurnaceAdvancedNozzle();"));
        assertTrue(source.contains("ConfigBlocks.blockStoneDevice.getStateFromMeta(0)"));
        assertTrue(source.contains("ConfigBlocks.blockMetalDevice.getStateFromMeta(3)"));
        assertTrue(source.contains("ConfigBlocks.blockMetalDevice.getStateFromMeta(9)"));
        assertTrue(source.contains("ConfigBlocks.blockMetalDevice.getStateFromMeta(1)"));
        assertTrue(source.contains("((TileAlchemyFurnaceAdvanced) tile).destroy = true;"));
        assertTrue(source.contains("this.restoreStructure(worldIn, pos, false);"));
        assertFalse(source.contains("thaumcraft.client"));
    }

    @Test
    public void tilesKeepTc4PowerProcessingOutputAndFullSyncContracts() throws IOException {
        String furnace = read("src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.java");
        String nozzle = read("src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedNozzle.java");

        assertTrue(furnace.contains("public int maxVis = 500;"));
        assertTrue(furnace.contains("public int maxPower = 500;"));
        assertTrue(furnace.contains("Aspect.FIRE, 50"));
        assertTrue(furnace.contains("Aspect.ENTROPY, 50"));
        assertTrue(furnace.contains("Aspect.WATER, 50"));
        assertTrue(furnace.contains("amount * 2 > this.heat"));
        assertTrue(furnace.contains("this.aspects.add(tags);"));
        assertTrue(furnace.contains("this.pos.getX() - 1, this.pos.getY(), this.pos.getZ() - 1"));
        assertTrue(furnace.contains("this.pos.getX() + 2, this.pos.getY() + 2, this.pos.getZ() + 2"));
        assertTrue(furnace.contains("this.aspects.writeToNBT(nbt);"));
        assertTrue(furnace.contains("nbt.setShort(\"power1\""));
        assertTrue(furnace.contains("nbt.setShort(\"power2\""));
        assertTrue(furnace.contains("nbt.setShort(\"processed\""));
        assertTrue(furnace.contains("nbt.setBoolean(\"destroy\""));
        assertTrue(furnace.contains("this.world.updateComparatorOutputLevel(nozzlePos, nozzleState.getBlock())"));
        assertTrue(furnace.contains("BlockAlchemyFurnace.getHeatLight(this.heat, this.maxPower)"));

        assertTrue(nozzle.contains("return face == this.facing;"));
        assertTrue(nozzle.contains("source.aspects.remove(aspect, amount);"));
        assertTrue(nozzle.contains("source.vis = source.aspects.visSize();"));
        assertTrue(nozzle.contains("source.syncContents(false);"));
        assertTrue(nozzle.contains("this.canOutputTo(face) && this.takeFromContainer(aspect, amount)"));
        assertFalse(furnace.contains("thaumcraft.client"));
        assertFalse(nozzle.contains("thaumcraft.client"));
    }

    @Test
    public void dedicatedBlockAndBothTilesAreRegisteredUnderStableLegacyIds() throws IOException {
        String config = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockalchemyfurnace.json");

        assertTrue(config.contains("public static BlockAlchemyFurnace blockAlchemyFurnace;"));
        assertTrue(config.contains("new BlockAlchemyFurnace()"));
        assertTrue(config.contains("legacyPath(\"blockAlchemyFurnace\")"));
        assertTrue(config.contains("new TileRegistration(TileAlchemyFurnaceAdvanced.class, \"TileAlchemyFurnaceAdvanced\")"));
        assertTrue(config.contains("new TileRegistration(TileAlchemyFurnaceAdvancedNozzle.class, \"TileAlchemyFurnaceAdvancedNozzle\")"));
        for (int meta = 0; meta <= 4; ++meta) {
            assertTrue(blockstate.contains("\"type=" + meta + "\""));
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
