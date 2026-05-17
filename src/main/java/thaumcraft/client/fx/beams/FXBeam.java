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
        double jitter = this.flicker ? 0.085D : 0.045D;

        for (int i = 0; i <= this.density; i++) {
            double t = (double) i / (double) this.density;
            double px = this.sourceX + dx * t + (this.rand.nextFloat() - 0.5F) * jitter;
            double py = this.sourceY + dy * t + (this.rand.nextFloat() - 0.5F) * jitter;
            double pz = this.sourceZ + dz * t + (this.rand.nextFloat() - 0.5F) * jitter;
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            if (this.flicker && this.rand.nextBoolean()) {
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
