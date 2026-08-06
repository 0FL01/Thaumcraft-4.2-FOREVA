package thaumcraft.common.blocks;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemShard;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockDropXpParityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void stoneDevicePillarsDropCosmeticStoneIdentityAndMetadata() {
        BlockCosmeticSolid old = ConfigBlocks.blockCosmeticSolid;
        try {
            ConfigBlocks.blockCosmeticSolid = new BlockCosmeticSolid();
            BlockStoneDevice device = new BlockStoneDevice();
            assertSame(Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid),
                    device.getItemDropped(device.getStateFromMeta(3), new Random(1L), 0));
            assertEquals(7, device.damageDropped(device.getStateFromMeta(3)));
            assertSame(Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid),
                    device.getItemDropped(device.getStateFromMeta(4), new Random(1L), 0));
            assertEquals(6, device.damageDropped(device.getStateFromMeta(4)));
        } finally {
            ConfigBlocks.blockCosmeticSolid = old;
        }
    }

    @Test
    public void infusedOreDropsSeparateShardStacksWithTc4Range() {
        ItemShard oldShard = ConfigItems.itemShard;
        ItemResource oldResource = ConfigItems.itemResource;
        try {
            ConfigItems.itemShard = new ItemShard();
            ConfigItems.itemResource = new ItemResource();
            BlockCustomOre ore = new BlockCustomOre();
            for (int fortune = 0; fortune <= 3; fortune++) {
                for (int meta = 1; meta <= 6; meta++) {
                    List<ItemStack> drops = ore.getDrops(null, BlockPos.ORIGIN,
                            ore.getStateFromMeta(meta), fortune);
                    assertTrue(drops.size() >= 1 && drops.size() <= 2 + fortune);
                    for (ItemStack drop : drops) {
                        assertSame(ConfigItems.itemShard, drop.getItem());
                        assertEquals(1, drop.getCount());
                        assertEquals(meta - 1, drop.getMetadata());
                    }
                }
            }
        } finally {
            ConfigItems.itemShard = oldShard;
            ConfigItems.itemResource = oldResource;
        }
    }

    @Test
    public void customOreXpUsesTc4RangesAndNoCinnabarXp() throws Exception {
        BlockCustomOre ore = new BlockCustomOre();
        Field field = BlockCustomOre.class.getDeclaredField("random");
        field.setAccessible(true);
        ((Random) field.get(ore)).setSeed(12345L);

        assertEquals(0, ore.getExpDrop(ore.getStateFromMeta(0), null, BlockPos.ORIGIN, 0));
        assertRange(ore, 1, 0, 3);
        assertRange(ore, 7, 1, 4);
    }

    @Test
    public void taintAndFibresKeepTc4DropIdentity() {
        BlockTaint taint = new BlockTaint();
        assertSame(Items.AIR, taint.getItemDropped(taint.getStateFromMeta(0), new Random(), 0));
        assertSame(Item.getItemFromBlock(Blocks.DIRT),
                taint.getItemDropped(taint.getStateFromMeta(1), new Random(), 0));
        assertSame(Items.ROTTEN_FLESH,
                taint.getItemDropped(taint.getStateFromMeta(2), new Random(), 0));
        BlockTaintFibres fibres = new BlockTaintFibres();
        assertSame(Items.AIR, fibres.getItemDropped(fibres.getStateFromMeta(0), new Random(), 0));
    }

    @Test
    public void chargedTotemAndWardedGlassKeepExactSourceContracts() throws IOException {
        String solid = read("BlockCosmeticSolid.java");
        String opaque = read("BlockCosmeticOpaque.java");
        assertTrue(solid.contains("for (int a = 0; a <= aspects.getAmount(aspect) / 10; ++a)"));
        assertTrue(opaque.contains("return super.getItemDropped(state, rand, fortune);"));
    }

    private static void assertRange(BlockCustomOre ore, int meta, int expectedMin, int expectedMax) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 1000; i++) {
            int value = ore.getExpDrop(ore.getStateFromMeta(meta), null, BlockPos.ORIGIN, 0);
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        assertEquals(expectedMin, min);
        assertEquals(expectedMax, max);
    }

    private static String read(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/blocks/" + file)), StandardCharsets.UTF_8);
    }
}
