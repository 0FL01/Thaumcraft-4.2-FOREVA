package thaumcraft.client.fx.beams;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeam extends Particle {
    protected final double sourceX;
    protected final double sourceY;
    protected final double sourceZ;
    protected final double targetX;
    protected final double targetY;
    protected final double targetZ;
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final boolean flicker;
    protected final int density;
    protected int type = 0;
    protected boolean reverse = false;
    protected boolean pulse = false;

    public FXBeam(World world, double x, double y, double z,
                  double tx, double ty, double tz,
                  float red, float green, float blue,
                  int age, boolean flicker, int density) {
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
        this.flicker = flicker;
        this.density = Math.max(4, density);
        this.particleMaxAge = Math.max(4, age);
        this.canCollide = false;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void setPulse(boolean pulse) {
        this.pulse = pulse;
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

        double startX = this.reverse ? this.targetX : this.sourceX;
        double startY = this.reverse ? this.targetY : this.sourceY;
        double startZ = this.reverse ? this.targetZ : this.sourceZ;
        double endX = this.reverse ? this.sourceX : this.targetX;
        double endY = this.reverse ? this.sourceY : this.targetY;
        double endZ = this.reverse ? this.sourceZ : this.targetZ;
        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        float life = this.particleMaxAge <= 0 ? 1.0F : (float) this.particleAge / (float) this.particleMaxAge;
        float pulseScale = !this.pulse ? 1.0F : (life < 0.5F ? life * 2.0F : (1.0F - life) * 2.0F);
        int activeDensity = Math.max(3, Math.round(this.density * (0.35F + pulseScale * 0.65F)));
        double jitter = this.flicker ? 0.085D : 0.045D;
        if (this.type == 2) jitter *= 0.8D;
        if (this.type == 3) jitter *= 1.25D;

        for (int i = 0; i <= activeDensity; i++) {
            double t = (double) i / (double) activeDensity;
            double px = startX + dx * t + (this.rand.nextFloat() - 0.5F) * jitter;
            double py = startY + dy * t + (this.rand.nextFloat() - 0.5F) * jitter;
            double pz = startZ + dz * t + (this.rand.nextFloat() - 0.5F) * jitter;

            if (this.type == 2) {
                this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, px, py, pz, this.red, this.green, this.blue);
            } else if (this.type == 3) {
                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, px, py, pz, 0.0D, 0.0D, 0.0D);
            } else {
                this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            }

            if ((this.flicker || this.type == 1) && this.rand.nextBoolean()) {
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
        // Beam visuals are emitted through spawned particles during update ticks.
    }
}
