package thaumcraft.client.lib;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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
    public static int scanEntityId = -1;
    public static BlockPos scanPos = BlockPos.ORIGIN;
    public static long scanExpireAtMs = 0L;
    public static int scanRange = 0;

    public static void startScan(Entity entity, BlockPos pos, long expireAtMs, int range) {
        if (entity == null || pos == null) {
            return;
        }
        scanEntityId = entity.getEntityId();
        scanPos = pos.toImmutable();
        scanExpireAtMs = expireAtMs;
        scanRange = Math.max(0, range);
    }

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
        if (event.getEntityLiving() == null || !event.getEntityLiving().world.isRemote) {
            return;
        }
        if (fogDuration > 0) {
            fogDuration--;
            if (fogDuration <= 0) {
                fogDuration = 0;
                fogFiddled = false;
            }
        }
        if (scanExpireAtMs > 0L && System.currentTimeMillis() >= scanExpireAtMs) {
            scanExpireAtMs = 0L;
            scanEntityId = -1;
            scanRange = 0;
        }
    }
}
