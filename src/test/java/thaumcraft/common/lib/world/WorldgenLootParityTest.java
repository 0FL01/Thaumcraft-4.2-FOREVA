package thaumcraft.common.lib.world;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigItems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorldgenLootParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
        if (ConfigItems.itemResource == null) {
            ConfigItems.init();
        }
    }

    @Test
    public void wizardTowerCoversAllEightyOneWeightedTickets() {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        for (int ticket = 0; ticket < 81; ticket++) {
            ItemStack stack = ComponentWizardTower.getRandomTowerLoot(new TicketRandom(ticket));
            assertFalse(stack.isEmpty());
            String key = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
            counts.put(key, counts.containsKey(key) ? counts.get(key) + 1 : 1);
        }

        assertEquals(Integer.valueOf(3), counts.get(Items.BOOK.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(10), counts.get(Items.PAPER.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(5), counts.get(Items.EMERALD.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(5), counts.get(Items.FILLED_MAP.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(3), counts.get(Items.ENDER_PEARL.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(20), counts.get(ConfigItems.itemResource.getRegistryName() + ":9"));
        assertEquals(Integer.valueOf(5), counts.get(ConfigItems.itemResource.getRegistryName() + ":0"));
        assertEquals(Integer.valueOf(5), counts.get(ConfigItems.itemResource.getRegistryName() + ":1"));
        assertEquals(Integer.valueOf(5), counts.get(ConfigItems.itemResource.getRegistryName() + ":2"));
        assertEquals(Integer.valueOf(20), counts.get(ConfigItems.itemThaumonomicon.getRegistryName() + ":0"));
    }

    @Test
    public void structureFillCountsUseCallerRandomAndNoDeferredSeed() throws IOException {
        String mound = read("src/main/java/thaumcraft/common/lib/world/WorldGenMound.java");
        assertEquals(2, occurrences(mound, "WorldgenLootHelper.fillDungeonChest(world, rand"));
        assertTrue(mound.contains("if (rand.nextInt(5) == 0)"));

        String hilltop = read("src/main/java/thaumcraft/common/lib/world/WorldGenHilltopStones.java");
        assertEquals(2, occurrences(hilltop, "WorldgenLootHelper.fillDungeonChest(world, rand"));

        String greatwood = read("src/main/java/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.java");
        assertEquals(1, occurrences(greatwood, "WorldgenLootHelper.fillDungeonChest(world, rand"));

        String helper = read("src/main/java/thaumcraft/common/lib/world/WorldgenLootHelper.java");
        assertTrue(helper.contains("table.fillInventory(inventory, random"));
        assertFalse(mound.contains("setLootTable("));
        assertFalse(hilltop.contains("setLootTable("));
        assertFalse(greatwood.contains("setLootTable("));
    }

    private static int occurrences(String value, String needle) {
        int count = 0;
        for (int at = 0; (at = value.indexOf(needle, at)) >= 0; at += needle.length()) {
            count++;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static final class TicketRandom extends Random {
        private final int ticket;
        private boolean first = true;

        private TicketRandom(int ticket) {
            this.ticket = ticket;
        }

        @Override
        public int nextInt(int bound) {
            if (first) {
                first = false;
                assertEquals(81, bound);
                return ticket;
            }
            return 0;
        }
    }
}
