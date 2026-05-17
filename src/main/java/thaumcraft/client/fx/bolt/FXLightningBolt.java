package thaumcraft.client.fx.bolt;

import java.util.Random;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXLightningBolt extends Particle {
    private final double sourceX;
    private final double sourceY;
    private final double sourceZ;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final float red;
    private final float green;
    private final float blue;
    private final int segmentCount;
    private final long seed;

    public FXLightningBolt(World world, double x, double y, double z,
                           double tx, double ty, double tz,
                           float red, float green, float blue,
                           int duration, int segmentCount) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sourceX = x;
        this.sourceY = y;
        this.sourceZ = z;
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.segmentCount = Math.max(6, segmentCount);
        this.seed = world.rand.nextLong();
        this.particleMaxAge = Math.max(3, duration);
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.world == null || !this.world.isRemote) {
            this.setExpired();
            return;
        }

        double dx = this.targetX - this.sourceX;
        double dy = this.targetY - this.sourceY;
        double dz = this.targetZ - this.sourceZ;
        Random jitterRandom = new Random(this.seed + this.particleAge * 31L);

        for (int i = 0; i <= this.segmentCount; i++) {
            double t = (double) i / (double) this.segmentCount;
            double taper = 1.0D - Math.abs(0.5D - t) * 2.0D;
            double jitter = 0.22D * taper;
            double px = this.sourceX + dx * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            double py = this.sourceY + dy * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            double pz = this.sourceZ + dz * t + (jitterRandom.nextFloat() - 0.5F) * jitter;
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            if ((i & 1) == 0 || jitterRandom.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0D, 0.0D, 0.0D);
            }
        }

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Bolt visuals are emitted through spawned particles during update ticks.
    }
}
