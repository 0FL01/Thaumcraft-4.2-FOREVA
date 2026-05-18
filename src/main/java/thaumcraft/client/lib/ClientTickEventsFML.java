package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickEventsFML {
    public static int warpVignette = 0;

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT || event.phase != TickEvent.Phase.START) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || event.player == null || event.player.getEntityId() != player.getEntityId()) {
            return;
        }

        if (warpVignette > 0) {
            warpVignette--;
            RenderEventHandler.targetBrightness = 0.0F;
        } else {
            RenderEventHandler.targetBrightness = 1.0F;
        }

        if (RenderEventHandler.fogFiddled) {
            if (RenderEventHandler.fogDuration < 100) {
                RenderEventHandler.fogTarget = 0.1F * ((float) RenderEventHandler.fogDuration / 100.0F);
            } else if (RenderEventHandler.fogTarget < 0.1F) {
                RenderEventHandler.fogTarget += 0.001F;
            }
            if (--RenderEventHandler.fogDuration < 0) {
                RenderEventHandler.fogDuration = 0;
                RenderEventHandler.fogFiddled = false;
                RenderEventHandler.fogTarget = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public void clientWorldTick(TickEvent.ClientTickEvent event) {
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
    }
}
