package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.CLIENT)
public final class ParticleEngine {

    public static final ParticleEngine instance = new ParticleEngine();
    public static final ParticleEngine INSTANCE = instance;

    public static final ResourceLocation particleTexture = new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    public static final ResourceLocation particleTexture2 = new ResourceLocation("thaumcraft", "textures/misc/particles2.png");

    private static final int MAX_PENDING_PARTICLES_PER_LAYER = 2000;
    private static final int MAX_TC_PARTICLES_PER_LAYER = 2000;
    private static final int MAX_PARTICLE_ADDITIONS_PER_TICK = 4096;
    private final Queue<QueuedParticle> pendingParticles = new ConcurrentLinkedQueue<>();
    private final Queue<QueuedParticle> pendingTCParticles = new ConcurrentLinkedQueue<>();
    private final Map<ParticleBucketKey, Integer> pendingCountsByBucket = new ConcurrentHashMap<>();
    private final Map<Integer, List<Particle>>[] tcParticles;
    private long lastRenderWorldTime = -1L;
    private float lastRenderPartialTicks = 0.0F;

    @SuppressWarnings("unchecked")
    private ParticleEngine() {
        this.tcParticles = new Map[4];
        for (int i = 0; i < this.tcParticles.length; ++i) {
            this.tcParticles[i] = new HashMap<>();
        }
    }

    public static void addEffect(World world, Particle particle) {
        if (world == null || particle == null || !world.isRemote) {
            return;
        }
        int dimension = world.provider != null ? world.provider.getDimension() : 0;
        if (particle instanceof ITCParticle) {
            int layer = clampLayer(((ITCParticle) particle).getTCParticleLayer());
            INSTANCE.pendingTCParticles.offer(new QueuedParticle(dimension, layer, particle));
            return;
        }

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
        if (event.side != Side.CLIENT) {
            return;
        }
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.effectRenderer == null) {
            clearPendingParticles();
            clearTCParticles();
            return;
        }
        if (mc.isGamePaused()) {
            return;
        }

        int currentDimension = mc.world.provider != null ? mc.world.provider.getDimension() : 0;
        drainPendingTCParticles();
        updateTCParticles(currentDimension);

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
        renderTCParticles(mc, event.getPartialTicks());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() == null || !event.getWorld().isRemote) {
            return;
        }
        clearPendingParticles();
        clearTCParticles();
    }

    public long getLastRenderWorldTime() {
        return lastRenderWorldTime;
    }

    public float getLastRenderPartialTicks() {
        return lastRenderPartialTicks;
    }

    private void clearPendingParticles() {
        pendingParticles.clear();
        pendingTCParticles.clear();
        pendingCountsByBucket.clear();
    }

    private void drainPendingTCParticles() {
        int added = 0;
        while (added < MAX_PARTICLE_ADDITIONS_PER_TICK) {
            QueuedParticle queued = pendingTCParticles.poll();
            if (queued == null) {
                break;
            }
            if (queued.particle == null || !queued.particle.isAlive()) {
                continue;
            }
            List<Particle> particles = this.tcParticles[queued.layer].computeIfAbsent(queued.dimension, ignored -> new ArrayList<>());
            while (particles.size() >= MAX_TC_PARTICLES_PER_LAYER) {
                particles.remove(0);
            }
            particles.add(queued.particle);
            ++added;
        }
    }

    private void updateTCParticles(int currentDimension) {
        for (Map<Integer, List<Particle>> layer : this.tcParticles) {
            List<Particle> particles = layer.get(currentDimension);
            if (particles == null || particles.isEmpty()) {
                continue;
            }
            Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                if (particle == null || !particle.isAlive()) {
                    iterator.remove();
                    continue;
                }
                particle.onUpdate();
                if (!particle.isAlive()) {
                    iterator.remove();
                }
            }
        }
    }

    private void renderTCParticles(Minecraft mc, float partialTicks) {
        Entity camera = mc.getRenderViewEntity();
        if (camera == null || mc.world == null) {
            return;
        }
        int currentDimension = mc.world.provider != null ? mc.world.provider.getDimension() : 0;
        boolean hasParticles = false;
        for (Map<Integer, List<Particle>> layer : this.tcParticles) {
            List<Particle> particles = layer.get(currentDimension);
            if (particles != null && !particles.isEmpty()) {
                hasParticles = true;
                break;
            }
        }
        if (!hasParticles) {
            return;
        }

        float rotationX = ActiveRenderInfo.getRotationX();
        float rotationZ = ActiveRenderInfo.getRotationZ();
        float rotationYZ = ActiveRenderInfo.getRotationYZ();
        float rotationXY = ActiveRenderInfo.getRotationXY();
        float rotationXZ = ActiveRenderInfo.getRotationXZ();
        Particle.interpPosX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * (double) partialTicks;
        Particle.interpPosY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * (double) partialTicks;
        Particle.interpPosZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * (double) partialTicks;
        Particle.cameraViewDir = camera.getLook(partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        boolean texture2Bound = false;
        mc.renderEngine.bindTexture(particleTexture);

        for (int layerIndex = 0; layerIndex < this.tcParticles.length; ++layerIndex) {
            List<Particle> particles = this.tcParticles[layerIndex].get(currentDimension);
            if (particles == null || particles.isEmpty()) {
                continue;
            }
            if (!texture2Bound && layerIndex >= 2) {
                mc.renderEngine.bindTexture(particleTexture2);
                texture2Bound = true;
            }
            if (layerIndex == 0 || layerIndex == 2) {
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            } else {
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (Particle particle : particles) {
                if (particle != null && particle.isAlive()) {
                    particle.renderParticle(buffer, camera, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                }
            }
            tessellator.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void clearTCParticles() {
        pendingTCParticles.clear();
        for (Map<Integer, List<Particle>> layer : this.tcParticles) {
            layer.clear();
        }
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
