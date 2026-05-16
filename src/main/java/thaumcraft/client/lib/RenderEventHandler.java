package thaumcraft.client.lib;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEventHandler {
    public static boolean fogFiddled = false;
    public static int fogDuration = 0;

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
    }

    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event) {
    }

    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
    }

    @SubscribeEvent
    public void fogDensityEvent(EntityViewRenderEvent.RenderFogEvent event) {
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
    }
}
