package thaumcraft.common.lib.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.visnet.VisNetHandler;

public class ServerTickEventsFML {

    @SubscribeEvent
    public void serverWorldTick(TickEvent.WorldTickEvent event) {
        // Delegate vis network regeneration to VisNetHandler
        VisNetHandler visNet = new VisNetHandler();
        visNet.onWorldTick(event);
    }
}
