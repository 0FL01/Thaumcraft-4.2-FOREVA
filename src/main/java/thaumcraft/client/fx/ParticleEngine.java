package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.CLIENT)
public final class ParticleEngine {

    public static final ParticleEngine instance = new ParticleEngine();
    public static final ParticleEngine INSTANCE = instance;

    private static final int MAX_PARTICLE_ADDITIONS_PER_TICK = 4096;
    private final Queue<QueuedParticle> pendingParticles = new ConcurrentLinkedQueue<>();
    private long lastRenderWorldTime = -1L;
    private float lastRenderPartialTicks = 0.0F;

    private ParticleEngine() {
    }

    public static void addEffect(World world, Particle particle) {
        if (world == null || particle == null || !world.isRemote) {
            return;
        }
        int dimension = world.provider != null ? world.provider.getDimension() : 0;
        INSTANCE.pendingParticles.offer(new QueuedParticle(dimension, particle));
    }

    @SubscribeEvent
    public void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.effectRenderer == null) {
            pendingParticles.clear();
            return;
        }
        if (mc.isGamePaused()) {
            return;
        }

        int currentDimension = mc.world.provider != null ? mc.world.provider.getDimension() : 0;
        int added = 0;
        while (added < MAX_PARTICLE_ADDITIONS_PER_TICK) {
            QueuedParticle queued = pendingParticles.poll();
            if (queued == null) {
                break;
            }
            if (queued.dimension != currentDimension || queued.particle == null || !queued.particle.isAlive()) {
                continue;
            }
            mc.effectRenderer.addEffect(queued.particle);
            added++;
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) {
            lastRenderWorldTime = -1L;
            lastRenderPartialTicks = 0.0F;
            return;
        }
        lastRenderWorldTime = mc.world.getTotalWorldTime();
        lastRenderPartialTicks = event.getPartialTicks();
    }

    public long getLastRenderWorldTime() {
        return lastRenderWorldTime;
    }

    public float getLastRenderPartialTicks() {
        return lastRenderPartialTicks;
    }

    private static final class QueuedParticle {
        private final int dimension;
        private final Particle particle;

        private QueuedParticle(int dimension, Particle particle) {
            this.dimension = dimension;
            this.particle = particle;
        }
    }
}
