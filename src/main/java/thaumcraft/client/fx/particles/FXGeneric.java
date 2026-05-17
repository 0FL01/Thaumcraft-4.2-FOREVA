package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXGeneric extends Particle {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final boolean loop;
    private final int num;
    private final int inc;
    private final int delay;
    private final float scale;

    public FXGeneric(World world, double x, double y, double z,
                     double mx, double my, double mz,
                     float red, float green, float blue, float alpha,
                     boolean loop, int num, int inc, int age, int delay, float scale) {
        super(world, x, y, z, mx, my, mz);
        this.red = MathHelper.clamp(red, 0.0F, 1.0F);
        this.green = MathHelper.clamp(green, 0.0F, 1.0F);
        this.blue = MathHelper.clamp(blue, 0.0F, 1.0F);
        this.alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
        this.loop = loop;
        this.num = Math.max(1, num);
        this.inc = Math.max(1, inc);
        this.delay = Math.max(0, delay);
        this.scale = Math.max(0.05F, scale);
        this.particleMaxAge = Math.max(4, age);
        this.canCollide = false;
        this.particleScale = this.scale;
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

        if (this.particleAge < this.delay) {
            this.particleAge++;
            return;
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.9D;
        this.motionY *= 0.9D;
        this.motionZ *= 0.9D;

        int bursts = Math.max(1, this.num / this.inc);
        for (int i = 0; i < bursts; i++) {
            double px = this.posX + (this.rand.nextFloat() - 0.5F) * this.scale * 0.15F;
            double py = this.posY + (this.rand.nextFloat() - 0.5F) * this.scale * 0.15F;
            double pz = this.posZ + (this.rand.nextFloat() - 0.5F) * this.scale * 0.15F;
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            if (this.alpha >= 0.6F || this.loop || this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, this.motionX * 0.1D, this.motionY * 0.1D, this.motionZ * 0.1D);
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
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
