package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSmokeTrail extends Particle {
    private final Entity target;
    public int particle = 24;

    public FXSmokeTrail(World world, double x, double y, double z, Entity target, float r, float g, float b) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
        this.particleScale = this.rand.nextFloat() * 0.5f + 0.5f;
        this.target = target;

        double dx = target.posX - this.posX;
        double dy = target.posY - this.posY;
        double dz = target.posZ - this.posZ;
        int base = (int) (MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 3.0f);
        if (base < 1) {
            base = 1;
        }
        this.particleMaxAge = base / 2 + this.rand.nextInt(base);

        float randomSpeed = 0.1f;
        this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * randomSpeed;
        this.motionY = (this.rand.nextFloat() - this.rand.nextFloat()) * randomSpeed;
        this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * randomSpeed;
        this.particleGravity = 0.2f;
        this.setParticleTextureIndex(this.particle);

        EntityLivingBase renderEntity = Minecraft.getMinecraft().player;
        int visibleDistance = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 64 : 32;
        if (renderEntity != null && renderEntity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
    }

    public void setGravity(float value) {
        this.particleGravity = value;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.target == null || !this.target.isEntityAlive() || this.particleAge++ >= this.particleMaxAge || this.target.getDistanceSq(this.posX, this.posY, this.posZ) < 1.0) {
            this.setExpired();
            return;
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.985;
        this.motionY *= 0.985;
        this.motionZ *= 0.985;

        double dx = this.target.posX - this.posX;
        double dy = this.target.posY - this.posY;
        double dz = this.target.posZ - this.posZ;
        double pull = 0.3;
        double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 4.0) {
            this.particleScale *= 0.9f;
            pull = 0.6;
        }
        if (dist > 1.0E-5) {
            this.motionX += dx / dist * pull;
            this.motionY += dy / dist * pull;
            this.motionZ += dz / dist * pull;
        }

        this.motionX = MathHelper.clamp(this.motionX, -0.35, 0.35);
        this.motionY = MathHelper.clamp(this.motionY, -0.35, 0.35);
        this.motionZ = MathHelper.clamp(this.motionZ, -0.35, 0.35);
        this.setParticleTextureIndex(this.particle + (this.particleAge % 16));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }
}
