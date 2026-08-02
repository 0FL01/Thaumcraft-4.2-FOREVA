package thaumcraft.common.entities.ai.interact;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AIFishRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void lootTablesMatchTc4DescriptorsAndWeights() {
        List<AIFish.WeightedLoot> junk = AIFish.lootTable(AIFish.LootTable.JUNK);
        assertEquals(11, junk.size());
        assertLoot(junk.get(0), Items.LEATHER_BOOTS, 1, 0, 10, 0.9F, false);
        assertLoot(junk.get(1), Items.LEATHER, 1, 0, 10, 0.0F, false);
        assertLoot(junk.get(2), Items.BONE, 1, 0, 10, 0.0F, false);
        assertLoot(junk.get(3), Items.POTIONITEM, 1, 0, 10, 0.0F, false);
        assertSame(PotionTypes.WATER, PotionUtils.getPotionFromItem(junk.get(3).stack));
        assertLoot(junk.get(4), Items.STRING, 1, 0, 5, 0.0F, false);
        assertLoot(junk.get(5), Items.FISHING_ROD, 1, 0, 2, 0.9F, false);
        assertLoot(junk.get(6), Items.BOWL, 1, 0, 10, 0.0F, false);
        assertLoot(junk.get(7), Items.STICK, 1, 0, 5, 0.0F, false);
        assertLoot(junk.get(8), Items.DYE, 10, 0, 5, 0.0F, false);
        assertLoot(junk.get(9), Item.getItemFromBlock(Blocks.TRIPWIRE_HOOK), 1, 0, 10, 0.0F, false);
        assertLoot(junk.get(10), Items.ROTTEN_FLESH, 1, 0, 10, 0.0F, false);
        assertEquals(87, totalWeight(junk));

        List<AIFish.WeightedLoot> rare = AIFish.lootTable(AIFish.LootTable.RARE);
        assertEquals(6, rare.size());
        assertLoot(rare.get(0), Item.getItemFromBlock(Blocks.WATERLILY), 1, 0, 1, 0.0F, false);
        assertLoot(rare.get(1), Items.NAME_TAG, 1, 0, 1, 0.0F, false);
        assertLoot(rare.get(2), Items.SADDLE, 1, 0, 1, 0.0F, false);
        assertLoot(rare.get(3), Items.BOW, 1, 0, 1, 0.25F, true);
        assertLoot(rare.get(4), Items.FISHING_ROD, 1, 0, 1, 0.25F, true);
        assertLoot(rare.get(5), Items.BOOK, 1, 0, 1, 0.0F, true);
        assertEquals(6, totalWeight(rare));

        List<AIFish.WeightedLoot> common = AIFish.lootTable(AIFish.LootTable.COMMON);
        assertEquals(4, common.size());
        assertLoot(common.get(0), Items.FISH, 1, ItemFishFood.FishType.COD.getMetadata(), 60, 0.0F, false);
        assertLoot(common.get(1), Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata(), 25, 0.0F, false);
        assertLoot(common.get(2), Items.FISH, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata(), 2, 0.0F, false);
        assertLoot(common.get(3), Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata(), 13, 0.0F, false);
        assertEquals(100, totalWeight(common));
    }

    @Test
    public void damageFractionsDamageItemsAndRareResultsAreAlwaysEnchanted() {
        AIFish.WeightedLoot boots = AIFish.lootTable(AIFish.LootTable.JUNK).get(0);
        Set<Integer> bootDamage = new HashSet<>();
        for (int seed = 0; seed < 64; seed++) {
            ItemStack result = boots.createStack(new Random(seed));
            assertTrue(result.getItemDamage() >= 1);
            assertTrue(result.getItemDamage() <= (int) (result.getMaxDamage() * 0.9F));
            assertFalse(result.isItemEnchanted());
            bootDamage.add(result.getItemDamage());
        }
        assertTrue("damage must be randomized rather than treated as enchant probability", bootDamage.size() > 1);
        ItemStack junkRod = AIFish.lootTable(AIFish.LootTable.JUNK).get(5).createStack(new Random(2L));
        assertSame(Items.FISHING_ROD, junkRod.getItem());
        assertTrue(junkRod.getItemDamage() >= 1);
        assertTrue(junkRod.getItemDamage() <= (int) (junkRod.getMaxDamage() * 0.9F));
        assertFalse(junkRod.isItemEnchanted());

        List<AIFish.WeightedLoot> rare = AIFish.lootTable(AIFish.LootTable.RARE);
        ItemStack bow = rare.get(3).createStack(new Random(3L));
        ItemStack rod = rare.get(4).createStack(new Random(4L));
        ItemStack book = rare.get(5).createStack(new Random(5L));
        assertDamagedAndEnchanted(bow, Items.BOW, 0.25F);
        assertDamagedAndEnchanted(rod, Items.FISHING_ROD, 0.25F);
        assertSame(Items.ENCHANTED_BOOK, book.getItem());
        assertFalse(EnchantmentHelper.getEnchantments(book).isEmpty());
    }

    @Test
    public void upgradeProbabilityBoundariesMatchTc4() {
        for (int roll = 0; roll < 10; roll++) {
            assertEquals(1, AIFish.catchCount(0, roll));
            assertEquals(roll == 0 ? 2 : 1, AIFish.catchCount(1, roll));
            assertEquals(roll < 2 ? 2 : 1, AIFish.catchCount(2, roll));
        }

        assertEquals(0.1F, AIFish.junkChance(0), 0.0F);
        assertEquals(0.075F, AIFish.junkChance(1), 0.0F);
        assertEquals(0.05F, AIFish.junkChance(2), 0.0F);
        assertEquals(0.05F, AIFish.rareChance(0), 0.0F);
        assertEquals(0.0625F, AIFish.rareChance(1), 0.0F);
        assertEquals(0.075F, AIFish.rareChance(2), 0.0F);

        assertSame(AIFish.LootTable.JUNK, AIFish.selectLootTable(0.0499F, 0.05F, 0.075F));
        assertSame(AIFish.LootTable.RARE, AIFish.selectLootTable(0.05F, 0.05F, 0.075F));
        assertSame(AIFish.LootTable.RARE, AIFish.selectLootTable(0.1249F, 0.05F, 0.075F));
        assertSame(AIFish.LootTable.COMMON, AIFish.selectLootTable(0.125F, 0.05F, 0.075F));

        assertEquals(0.0003F, AIFish.catchChance(0.0F, 2), 0.0F);
        assertTrue(AIFish.shouldCatch(0.00029F, 0.0F, 2));
        assertFalse(AIFish.shouldCatch(0.0003F, 0.0F, 2));
        assertEquals(0.00015F, AIFish.catchChance(0.0F, 2) - AIFish.catchChance(0.0F, 1), 0.0F);
    }

    @Test
    public void ignisSmeltsAndCreatesNormallyAgingBurningCatchEntity() {
        ItemStack rawCod = new ItemStack(Items.FISH, 1, ItemFishFood.FishType.COD.getMetadata());
        assertSame(rawCod, AIFish.applyIgnisSmelting(rawCod, false));
        ItemStack cooked = AIFish.applyIgnisSmelting(rawCod, true);
        assertSame(Items.COOKED_FISH, cooked.getItem());
        assertEquals(ItemFishFood.FishType.COD.getMetadata(), cooked.getMetadata());

        EntityItem entity = AIFish.createCatchEntity(new TestWorld(false), new Vec3d(1.0D, 64.0D, 2.0D), cooked, true);
        assertTrue(entity.isBurning());
        assertEquals("Ignis must not use setNoDespawn", 0, entity.getAge());
        assertTrue(entity.cannotPickup());
    }

    private static void assertLoot(AIFish.WeightedLoot loot, Item item, int count, int metadata,
                                   int weight, float damageFraction, boolean enchantable) {
        assertSame(item, loot.stack.getItem());
        assertEquals(count, loot.stack.getCount());
        assertEquals(metadata, loot.stack.getMetadata());
        assertEquals(weight, loot.weight);
        assertEquals(damageFraction, loot.damageFraction, 0.0F);
        assertEquals(enchantable, loot.enchantable);
    }

    private static int totalWeight(List<AIFish.WeightedLoot> loot) {
        int total = 0;
        for (AIFish.WeightedLoot entry : loot) total += entry.weight;
        return total;
    }

    private static void assertDamagedAndEnchanted(ItemStack stack, Item item, float maximumFraction) {
        assertSame(item, stack.getItem());
        assertTrue(stack.getItemDamage() >= 1);
        assertTrue(stack.getItemDamage() <= (int) (stack.getMaxDamage() * maximumFraction));
        assertTrue(stack.isItemEnchanted());
    }

    private static final class TestWorld extends World {
        private TestWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r6_fishing_runtime"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public IBlockState getBlockState(BlockPos pos) { return Blocks.AIR.getDefaultState(); }
        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "r6_fishing_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
