package thaumcraft.common.lib.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootHandler {
    private static final LootCondition[] NO_CONDITIONS = new LootCondition[0];
    private static final Map<ResourceLocation, List<ChestLootEntry>> CHEST_LOOT = new LinkedHashMap<>();

    public static void addLoot(ResourceLocation table, ItemStack stack, int minCount, int maxCount, int weight) {
        CHEST_LOOT.computeIfAbsent(table, key -> new ArrayList<>())
                .add(new ChestLootEntry(stack.copy(), minCount, maxCount, weight));
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        List<ChestLootEntry> entries = CHEST_LOOT.get(event.getName());
        if (entries == null || entries.isEmpty()) {
            return;
        }
        LootPool pool = event.getTable().getPool("main");
        if (pool == null) {
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            String name = "thaumcraft_tc4_" + i;
            if (pool.getEntry(name) == null) {
                pool.addEntry(entries.get(i).createEntry(name));
            }
        }
    }

    static List<ChestLootEntry> getLoot(ResourceLocation table) {
        List<ChestLootEntry> entries = CHEST_LOOT.get(table);
        return entries == null ? Collections.emptyList() : Collections.unmodifiableList(entries);
    }

    static final class ChestLootEntry {
        final ItemStack stack;
        final int minCount;
        final int maxCount;
        final int weight;

        ChestLootEntry(ItemStack stack, int minCount, int maxCount, int weight) {
            this.stack = stack;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.weight = weight;
        }

        LootEntry createEntry(String name) {
            List<LootFunction> functions = new ArrayList<>();
            if (this.minCount != 1 || this.maxCount != 1) {
                functions.add(new SetCount(NO_CONDITIONS, new RandomValueRange(this.minCount, this.maxCount)));
            }
            if (this.stack.getMetadata() != 0) {
                functions.add(new SetMetadata(NO_CONDITIONS, new RandomValueRange(this.stack.getMetadata())));
            }
            if (this.stack.hasTagCompound()) {
                functions.add(new SetNBT(NO_CONDITIONS, this.stack.getTagCompound().copy()));
            }
            return new LootEntryItem(this.stack.getItem(), this.weight, 0,
                    functions.toArray(new LootFunction[functions.size()]), NO_CONDITIONS, name);
        }
    }
}
