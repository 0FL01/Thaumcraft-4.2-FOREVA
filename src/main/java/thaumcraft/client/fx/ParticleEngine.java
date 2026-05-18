package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.CLIENT)
public final class ParticleEngine {

    public static final ParticleEngine instance = new ParticleEngine();
    public static final ParticleEngine INSTANCE = instance;

    private static final int MAX_PENDING_PARTICLES_PER_LAYER = 2000;
    private static final int MAX_PARTICLE_ADDITIONS_PER_TICK = 4096;
    private final Queue<QueuedParticle> pendingParticles = new ConcurrentLinkedQueue<>();
    private final Map<ParticleBucketKey, Integer> pendingCountsByBucket = new ConcurrentHashMap<>();
    private long lastRenderWorldTime = -1L;
    private float lastRenderPartialTicks = 0.0F;

    private ParticleEngine() {
    }

    public static void addEffect(World world, Particle particle) {
        if (world == null || particle == null || !world.isRemote) {
            return;
        }
        int dimension = world.provider != null ? world.provider.getDimension() : 0;
        int layer = clampLayer(particle.getFXLayer());
        ParticleBucketKey bucket = new ParticleBucketKey(dimension, layer);
        if (INSTANCE.getPendingCount(bucket) >= MAX_PENDING_PARTICLES_PER_LAYER && !INSTANCE.dropOldestPendingFromBucket(bucket)) {
            return;
        }
        INSTANCE.pendingParticles.offer(new QueuedParticle(dimension, layer, particle));
        INSTANCE.incrementPendingCount(bucket);
    }

    @SubscribeEvent
    public void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.effectRenderer == null) {
            clearPendingParticles();
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
            decrementPendingCount(new ParticleBucketKey(queued.dimension, queued.layer));
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

    private void clearPendingParticles() {
        pendingParticles.clear();
        pendingCountsByBucket.clear();
    }

    private int getPendingCount(ParticleBucketKey bucket) {
        Integer count = pendingCountsByBucket.get(bucket);
        return count == null ? 0 : count;
    }

    private void incrementPendingCount(ParticleBucketKey bucket) {
        pendingCountsByBucket.merge(bucket, 1, Integer::sum);
    }

    private void decrementPendingCount(ParticleBucketKey bucket) {
        pendingCountsByBucket.computeIfPresent(bucket, (ignored, count) -> count <= 1 ? null : count - 1);
    }

    private boolean dropOldestPendingFromBucket(ParticleBucketKey bucket) {
        for (QueuedParticle queued : pendingParticles) {
            if (queued.dimension != bucket.dimension || queued.layer != bucket.layer) {
                continue;
            }
            if (pendingParticles.remove(queued)) {
                decrementPendingCount(bucket);
                return true;
            }
        }
        return false;
    }

    private static int clampLayer(int layer) {
        if (layer < 0) {
            return 0;
        }
        if (layer > 3) {
            return 3;
        }
        return layer;
    }

    private static final class ParticleBucketKey {
        private final int dimension;
        private final int layer;

        private ParticleBucketKey(int dimension, int layer) {
            this.dimension = dimension;
            this.layer = layer;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ParticleBucketKey)) {
                return false;
            }
            ParticleBucketKey other = (ParticleBucketKey) obj;
            return this.dimension == other.dimension && this.layer == other.layer;
        }

        @Override
        public int hashCode() {
            return 31 * dimension + layer;
        }
    }

    private static final class QueuedParticle {
        private final int dimension;
        private final int layer;
        private final Particle particle;

        private QueuedParticle(int dimension, int layer, Particle particle) {
            this.dimension = dimension;
            this.layer = layer;
            this.particle = particle;
        }
    }
}
