package thaumcraft.common.lib.events;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class EventHandlerWorld {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
    }

    @SubscribeEvent
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
    }

    @SubscribeEvent
    public void onChunkDataSave(ChunkDataEvent.Save event) {
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
    }

    @SubscribeEvent
    public void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event) {
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
    }

    @SubscribeEvent
    public void onFillBucket(net.minecraftforge.event.entity.player.FillBucketEvent event) {
    }
}
