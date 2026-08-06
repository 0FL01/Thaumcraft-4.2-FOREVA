package thaumcraft.common.lib.world;

import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

final class WorldgenLootHelper {

    private WorldgenLootHelper() {
    }

    static void fillDungeonChest(World world, Random random, IInventory inventory) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer server = (WorldServer) world;
        LootTable table = server.getLootTableManager()
                .getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
        table.fillInventory(inventory, random, new LootContext.Builder(server).build());
    }
}
