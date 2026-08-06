package thaumcraft.common.tiles;

import java.util.Random;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileArcaneFurnaceSmeltingBonusRuntimeTest {
    private static final String TEST_ORE = "tcArcaneFurnaceSmeltingBonus";
    private static Item testOreItem;

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        Item[] candidates = {Items.COMMAND_BLOCK_MINECART, Items.KNOWLEDGE_BOOK, Items.CLOCK};
        for (Item candidate : candidates) {
            if (OreDictionary.getOreIDs(new ItemStack(candidate)).length == 0) {
                testOreItem = candidate;
                break;
            }
        }
        assertNotNull("test needs an item without an existing first ore-dictionary mapping", testOreItem);
        OreDictionary.registerOre(TEST_ORE, new ItemStack(testOreItem));
    }

    @Test
    public void apiStoresNonEmptyItemMetaPrototypesWithoutOutputNbt() {
        ItemStack exactOutput = new ItemStack(Items.DYE, 7, 4);
        exactOutput.setTagCompound(new NBTTagCompound());
        exactOutput.getTagCompound().setBoolean("ignored", true);
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.DIAMOND_HOE), exactOutput);

        ItemStack exact = ThaumcraftApi.getSmeltingBonus(new ItemStack(Items.DIAMOND_HOE));
        assertSame(Items.DYE, exact.getItem());
        assertEquals(4, exact.getMetadata());
        assertEquals(1, exact.getCount());
        assertFalse(exact.hasTagCompound());

        ThaumcraftApi.addSmeltingBonus(TEST_ORE, new ItemStack(Items.DYE, 5, 2));
        ItemStack ore = ThaumcraftApi.getSmeltingBonus(new ItemStack(testOreItem));
        assertSame(Items.DYE, ore.getItem());
        assertEquals(2, ore.getMetadata());
        assertEquals(1, ore.getCount());
    }

    @Test
    public void noBellowsUsesZeroBasedOneInFourCount() {
        ItemStack prototype = new ItemStack(Items.GOLD_NUGGET);

        ItemStack miss = TileArcaneFurnace.createSmeltingBonus(prototype,
                new ScriptedRandom(new int[]{1}, new float[0]), 0);
        ItemStack hit = TileArcaneFurnace.createSmeltingBonus(prototype,
                new ScriptedRandom(new int[]{0}, new float[0]), 0);

        assertTrue(miss.isEmpty());
        assertSame(Items.GOLD_NUGGET, hit.getItem());
        assertEquals(1, hit.getCount());
        assertEquals("the registered prototype must not be used as a guaranteed base item", 1, prototype.getCount());
    }

    @Test
    public void bellowsUseOneIndependentFortyFourPercentRollEach() {
        ItemStack prototype = new ItemStack(Items.DYE, 1, 3);
        ScriptedRandom random = new ScriptedRandom(new int[0], new float[]{0.43F, 0.44F, 0.10F});

        ItemStack result = TileArcaneFurnace.createSmeltingBonus(prototype, random, 3);

        assertSame(Items.DYE, result.getItem());
        assertEquals(3, result.getMetadata());
        assertEquals(2, result.getCount());
        assertEquals(3, random.floatCalls);
        assertEquals(1, prototype.getCount());
    }

    private static final class ScriptedRandom extends Random {
        private final int[] ints;
        private final float[] floats;
        private int intIndex;
        private int floatIndex;
        private int floatCalls;

        private ScriptedRandom(int[] ints, float[] floats) {
            this.ints = ints;
            this.floats = floats;
        }

        @Override
        public int nextInt(int bound) {
            assertEquals(4, bound);
            return this.ints[this.intIndex++];
        }

        @Override
        public float nextFloat() {
            this.floatCalls++;
            return this.floats[this.floatIndex++];
        }
    }
}
