package thaumcraft.client.lib;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Temporary diagnostic counters for Eldritch dimension FPS investigation.
 * All fields are client-only and reset every 60 frames via {@link #onFrame()}.
 * Logs a summary line to the THAUMCRAFT logger every 60 frames when the player
 * is in the Eldritch dimension (dim 52).
 */
public final class EldritchDiagnostics {
    private static final Logger LOG = LogManager.getLogger("THAUMCRAFT");
    private static final String TAG = "[EldritchPerf]";

    // --- per-frame counters (reset in onFrame) ---
    public static int nothingTESRCalls;
    public static int nothingFacesRendered;
    public static int nothingInRangeFaces;
    public static int nothingFarFaces;
    public static int nothingLayerQuads;
    public static int obeliskTESRCalls;
    public static int lockTESRCalls;
    public static int fieldHelperFaces;
    public static int fieldHelperLayers;

    // --- persistent state ---
    private static int frameCounter;
    private static long lastSummaryNanos;
    private static boolean renderTypeLogged;
    private static long lastEntityCountNanos;

    private EldritchDiagnostics() {}

    /**
     * Called once per frame from RenderEventHandler.renderLast().
     * Logs a summary every 60 frames.
     */
    public static void onFrame() {
        frameCounter++;
        if (frameCounter < 60) {
            return;
        }

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (mc == null || mc.world == null || mc.player == null) {
            reset();
            return;
        }

        int dim = mc.world.provider.getDimension();
        if (dim != -42) { // Config.dimensionOuterId
            reset();
            return;
        }

        long now = System.nanoTime();
        double elapsedMs = (now - lastSummaryNanos) / 1_000_000.0;
        double fps = frameCounter / (elapsedMs / 1000.0);

        // Entity count (throttled to every 3 seconds to avoid spam)
        int entityCount = -1;
        if (now - lastEntityCountNanos > 3_000_000_000L) {
            entityCount = countEntities(mc.world, mc.player);
            lastEntityCountNanos = now;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(TAG)
          .append(String.format(" FPS=%.1f elapsed=%.0fms", fps, elapsedMs))
          .append(String.format(" | nothingTESR=%d faces=%d(inRange=%d far=%d) layers=%d",
                  nothingTESRCalls, nothingFacesRendered, nothingInRangeFaces, nothingFarFaces, nothingLayerQuads))
          .append(String.format(" | fieldHelper faces=%d layers=%d", fieldHelperFaces, fieldHelperLayers))
          .append(String.format(" | obeliskTESR=%d lockTESR=%d", obeliskTESRCalls, lockTESRCalls));
        if (entityCount >= 0) {
            sb.append(String.format(" | entities=%d", entityCount));
        }

        LOG.info(sb.toString());
        reset();
    }

    /**
     * Called once from BlockEldritchNothing.getRenderType() to log the render type.
     */
    public static void logRenderType(String type) {
        if (!renderTypeLogged) {
            renderTypeLogged = true;
            LOG.warn("{} BlockEldritchNothing.getRenderType() -> {} (chunk renderer WILL draw this as a baked model!)",
                    TAG, type);
        }
    }

    private static int countEntities(World world, EntityPlayer player) {
        int count = 0;
        for (Entity e : world.loadedEntityList) {
            if (e instanceof EntityLivingBase && e != player) {
                double distSq = e.getDistanceSq(player);
                if (distSq < 1024.0) { // within 32 blocks
                    count++;
                }
            }
        }
        return count;
    }

    private static void reset() {
        nothingTESRCalls = 0;
        nothingFacesRendered = 0;
        nothingInRangeFaces = 0;
        nothingFarFaces = 0;
        nothingLayerQuads = 0;
        obeliskTESRCalls = 0;
        lockTESRCalls = 0;
        fieldHelperFaces = 0;
        fieldHelperLayers = 0;
        frameCounter = 0;
        lastSummaryNanos = System.nanoTime();
    }
}
