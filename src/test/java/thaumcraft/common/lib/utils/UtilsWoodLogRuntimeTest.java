package thaumcraft.common.lib.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldType;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.items.equipment.ItemElementalAxe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsWoodLogRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @After
    public void clearCompatibilityLogs() {
        ItemElementalAxe.oreDictLogs.clear();
    }

    @Test
    public void nonLeafSustainingLogsHonorExactAndWildcardRegistrations() {
        int stoneId = Item.getIdFromItem(Item.getItemFromBlock(Blocks.STONE));
        TestBlockAccess world = new TestBlockAccess(Blocks.STONE.getDefaultState());

        ItemElementalAxe.oreDictLogs.add(Arrays.<Object>asList(stoneId, 1));
        assertFalse(Utils.isWoodLog(world, BlockPos.ORIGIN));

        ItemElementalAxe.oreDictLogs.clear();
        ItemElementalAxe.oreDictLogs.add(Arrays.<Object>asList(stoneId, 0));
        assertTrue(Utils.isWoodLog(world, BlockPos.ORIGIN));

        ItemElementalAxe.oreDictLogs.clear();
        ItemElementalAxe.oreDictLogs.add(Arrays.<Object>asList(stoneId, OreDictionary.WILDCARD_VALUE));
        assertTrue(Utils.isWoodLog(world, BlockPos.ORIGIN));
    }

    @Test
    public void compatibilityScanUsesForgeLogWoodKey() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/config/Config.java")),
                StandardCharsets.UTF_8);
        assertTrue(source.contains("\"logWood\".equals(ore)"));
        assertFalse(source.contains("\"woodLog\".equals(ore)"));
    }

    private static final class TestBlockAccess implements IBlockAccess {
        private final IBlockState state;

        TestBlockAccess(IBlockState state) {
            this.state = state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return this.state;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return this.state.getBlock() == Blocks.AIR;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biome.getBiome(1);
        }

        @Override
        public int getStrongPower(BlockPos pos, net.minecraft.util.EnumFacing direction) {
            return 0;
        }

        @Override
        public WorldType getWorldType() {
            return WorldType.DEFAULT;
        }

        @Override
        public boolean isSideSolid(BlockPos pos, net.minecraft.util.EnumFacing side, boolean defaultValue) {
            return defaultValue;
        }
    }
}
