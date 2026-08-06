package thaumcraft.common.lib.events;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.baubles.ItemAmuletVis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LootHandlerRuntimeTest {
    private static final LootCondition[] NO_CONDITIONS = new LootCondition[0];

    @BeforeClass
    public static void initializeLoot() {
        Bootstrap.register();
        if (ConfigItems.itemResource == null) {
            ConfigItems.init();
        }
        Config.initLoot();
    }

    @Test
    public void lootBagPoolsKeepReferenceTuplesAndPotionVariants() {
        assertEquals(69, WeightedRandomLoot.lootBagCommon.size());
        assertEquals(74, WeightedRandomLoot.lootBagUncommon.size());
        assertEquals(76, WeightedRandomLoot.lootBagRare.size());

        assertLoot(WeightedRandomLoot.lootBagCommon, ConfigItems.itemResource, 18, 1, 2500);
        assertLoot(WeightedRandomLoot.lootBagUncommon, ConfigItems.itemResource, 18, 2, 2250);
        assertLoot(WeightedRandomLoot.lootBagRare, ConfigItems.itemResource, 18, 3, 2000);
        assertLoot(WeightedRandomLoot.lootBagRare, ConfigItems.itemEldritchObject, 3, 1, 1);
        assertLoot(WeightedRandomLoot.lootBagRare, Items.NETHER_STAR, 0, 1, 1);
        assertLoot(WeightedRandomLoot.lootBagCommon, Items.DIAMOND, 0, 1, 10);
        assertLoot(WeightedRandomLoot.lootBagRare, Items.DIAMOND, 0, 1, 50);

        assertPotionPool(WeightedRandomLoot.lootBagCommon);
        assertPotionPool(WeightedRandomLoot.lootBagUncommon);
        assertPotionPool(WeightedRandomLoot.lootBagRare);

        WeightedRandomLoot amuletLoot = findLoot(WeightedRandomLoot.lootBagRare,
                ConfigItems.itemAmuletVis, 0, 1, 6);
        assertNotNull(amuletLoot);
        ItemAmuletVis amulet = (ItemAmuletVis) amuletLoot.item.getItem();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            int vis = amulet.getVis(amuletLoot.item, aspect);
            assertTrue(vis >= 0 && vis <= 400);
            assertEquals(0, vis % 100);
        }

        int common = WeightedRandomLoot.lootBagCommon.size();
        int uncommon = WeightedRandomLoot.lootBagUncommon.size();
        int rare = WeightedRandomLoot.lootBagRare.size();
        Config.initLoot();
        assertEquals(common, WeightedRandomLoot.lootBagCommon.size());
        assertEquals(uncommon, WeightedRandomLoot.lootBagUncommon.size());
        assertEquals(rare, WeightedRandomLoot.lootBagRare.size());
    }

    @Test
    public void chestDescriptorsMatchAllEightReferenceTargets() {
        ResourceLocation[] highWeightTables = {LootTableList.CHESTS_SIMPLE_DUNGEON,
                LootTableList.CHESTS_JUNGLE_TEMPLE, LootTableList.CHESTS_DESERT_PYRAMID};
        ResourceLocation[] lowerWeightTables = {LootTableList.CHESTS_ABANDONED_MINESHAFT,
                LootTableList.CHESTS_STRONGHOLD_CORRIDOR, LootTableList.CHESTS_STRONGHOLD_CROSSING};

        for (ResourceLocation table : highWeightTables) {
            List<LootHandler.ChestLootEntry> entries = LootHandler.getLoot(table);
            assertEquals(22, entries.size());
            assertTupleCount(entries, 1, 3, 5, 3);
            assertTupleCount(entries, 1, 2, 4, 5);
            assertTupleCount(entries, 1, 1, 1, 14);
        }
        for (ResourceLocation table : lowerWeightTables) {
            List<LootHandler.ChestLootEntry> entries = LootHandler.getLoot(table);
            assertEquals(22, entries.size());
            assertTupleCount(entries, 1, 3, 4, 3);
            assertTupleCount(entries, 1, 2, 3, 5);
            assertTupleCount(entries, 1, 1, 1, 14);
        }

        List<LootHandler.ChestLootEntry> library = LootHandler.getLoot(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
        assertEquals(23, library.size());
        assertTupleCount(library, 1, 3, 4, 3);
        assertTupleCount(library, 1, 2, 3, 5);
        assertTupleCount(library, 1, 1, 1, 14);
        assertChestLoot(library, ConfigItems.itemResource, 9, 3, 6, 20);

        List<LootHandler.ChestLootEntry> village = LootHandler.getLoot(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
        assertEquals(1, village.size());
        assertChestLoot(village, ConfigItems.itemResource, 2, 1, 3, 10);
    }

    @Test
    public void loadEventAddsEntriesToExistingMainPoolWithoutDuplicateRolls() {
        LootPool main = new LootPool(new LootEntry[0], NO_CONDITIONS,
                new RandomValueRange(1), new RandomValueRange(0), "main");
        LootTable table = new LootTable(new LootPool[]{main});
        LootTableLoadEvent event = new LootTableLoadEvent(LootTableList.CHESTS_SIMPLE_DUNGEON, table, null);
        LootHandler handler = new LootHandler();

        handler.onLootTableLoad(event);

        for (int i = 0; i < 22; i++) {
            assertNotNull(main.getEntry("thaumcraft_tc4_" + i));
        }
        LootEntry first = main.getEntry("thaumcraft_tc4_0");
        handler.onLootTableLoad(event);
        assertSame(first, main.getEntry("thaumcraft_tc4_0"));
        assertEquals(1.0F, main.getRolls().getMin(), 0.0F);
        assertEquals(1.0F, main.getRolls().getMax(), 0.0F);
    }

    @Test
    public void lifecycleRegistersAdapterAndCallsInitLoot() throws IOException {
        String mod = read("src/main/java/thaumcraft/common/Thaumcraft.java");
        String config = read("src/main/java/thaumcraft/common/config/Config.java");
        assertTrue(mod.contains("MinecraftForge.EVENT_BUS.register(new LootHandler());"));
        assertTrue(mod.contains("Config.initLoot();"));
        assertTrue(config.contains("if (lootInitialized)"));
        assertTrue(!config.contains("WeightedRandomLoot.lootBagCommon.clear()"));
    }

    private static void assertPotionPool(List<WeightedRandomLoot> loot) {
        Map<PotionType, Integer> potionTypes = new HashMap<>();
        int weightOne = 0;
        int weightTwo = 0;
        int weightThree = 0;
        for (WeightedRandomLoot entry : loot) {
            if (entry.item.getItem() != Items.POTIONITEM && entry.item.getItem() != Items.SPLASH_POTION) {
                continue;
            }
            PotionType type = PotionUtils.getPotionFromItem(entry.item);
            potionTypes.put(type, potionTypes.getOrDefault(type, 0) + 1);
            if (entry.itemWeight == 1) weightOne++;
            if (entry.itemWeight == 2) weightTwo++;
            if (entry.itemWeight == 3) weightThree++;
        }
        assertEquals(28, potionTypes.size());
        for (Integer count : potionTypes.values()) {
            assertEquals(2, count.intValue());
        }
        assertEquals(24, weightOne);
        assertEquals(20, weightTwo);
        assertEquals(12, weightThree);
    }

    private static void assertLoot(List<WeightedRandomLoot> loot, Item item, int meta, int count, int weight) {
        assertNotNull(findLoot(loot, item, meta, count, weight));
    }

    private static WeightedRandomLoot findLoot(List<WeightedRandomLoot> loot, Item item,
                                               int meta, int count, int weight) {
        for (WeightedRandomLoot entry : loot) {
            if (entry.item.getItem() == item && entry.item.getMetadata() == meta
                    && entry.item.getCount() == count && entry.itemWeight == weight) {
                return entry;
            }
        }
        return null;
    }

    private static void assertTupleCount(List<LootHandler.ChestLootEntry> entries,
                                         int min, int max, int weight, int expected) {
        int count = 0;
        for (LootHandler.ChestLootEntry entry : entries) {
            if (entry.minCount == min && entry.maxCount == max && entry.weight == weight) {
                count++;
            }
        }
        assertEquals(expected, count);
    }

    private static void assertChestLoot(List<LootHandler.ChestLootEntry> entries, Item item, int meta,
                                        int min, int max, int weight) {
        for (LootHandler.ChestLootEntry entry : entries) {
            if (entry.stack.getItem() == item && entry.stack.getMetadata() == meta
                    && entry.minCount == min && entry.maxCount == max && entry.weight == weight) {
                return;
            }
        }
        throw new AssertionError("Missing chest loot tuple");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
