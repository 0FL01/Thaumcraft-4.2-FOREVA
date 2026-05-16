package thaumcraft.client.lib;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickEventsFML {
    public static int warpVignette = 0;

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
    }

    @SubscribeEvent
    public void clientWorldTick(TickEvent.ClientTickEvent event) {
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
    }
}
