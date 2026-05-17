package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBubbleAlt extends Particle {
    private float red = 1.0F;
    private float green = 0.0F;
    private float blue = 0.5F;

    public FXBubbleAlt(World world, double x, double y, double z, double mx, double my, double mz, int age) {
        super(world, x, y, z, mx, my, mz);
        this.particleScale *= this.rand.nextFloat() * 0.3F + 0.2F;
        this.motionX = mx * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.motionY = my * 0.2D + this.rand.nextFloat() * 0.02F;
        this.motionZ = mz * 0.2D + (this.rand.nextFloat() * 2.0F - 1.0F) * 0.02F;
        this.particleMaxAge = Math.max(3, age) + this.rand.nextInt(8);
        this.canCollide = false;
    }

    public void setRGB(float r, float g, float b) {
        this.red = r;
        this.green = g;
        this.blue = b;
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

        this.motionX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.001F;
        this.motionZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.001F;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.85D;
        this.motionY *= 0.85D;
        this.motionZ *= 0.85D;

        float life = this.particleMaxAge <= 0 ? 1.0F : (float) this.particleAge / (float) this.particleMaxAge;
        this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                this.posX, this.posY, this.posZ,
                this.motionX * 0.05D, this.motionY * 0.05D, this.motionZ * 0.05D);
        this.world.spawnParticle(EnumParticleTypes.REDSTONE,
                this.posX, this.posY, this.posZ,
                this.red, this.green, this.blue);

        if (life > 0.75F || this.rand.nextBoolean()) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                    this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
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
