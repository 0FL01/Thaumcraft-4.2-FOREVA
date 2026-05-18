package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXScorch extends Particle {
    public boolean pvp = true;
    public boolean mobs = true;
    public boolean animals = true;
    public boolean lance = false;
    public Entity partDestEnt;

    private double px;
    private double py;
    private double pz;
    private float transferParticleScale;

    public FXScorch(World world, double x, double y, double z, Vec3d v, float spread, boolean lance) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.lance = lance;
        this.px = x + v.x * 100.0;
        this.py = y + v.y * 100.0;
        this.pz = z + v.z * 100.0;

        if (!lance) {
            this.px += (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
            this.py += (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
            this.pz += (this.rand.nextFloat() - this.rand.nextFloat()) * spread;
            this.transferParticleScale = this.particleScale = this.rand.nextFloat() + 3.0f;
        } else {
            this.px += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5;
            this.py += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5;
            this.pz += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5;
            this.transferParticleScale = this.particleScale = this.rand.nextFloat() * 0.5f + 2.0f;
        }

        this.particleMaxAge = 50;
        this.setSize(0.1f, 0.1f);
        this.setParticleTextureIndex(151);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleAlpha = 0.66f;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 210;
    }

    @Override
    public void onUpdate() {
        double dx = this.px - this.posX;
        double dy = this.py - this.posY;
        double dz = this.pz - this.posZ;
        double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= 1.0E-5) {
            this.setExpired();
            return;
        }

        double scale = (double) (1.0f - (float) this.particleAge / (float) this.particleMaxAge) / 1.25;
        this.motionX = dx / distance * scale;
        this.motionY = dy / distance * scale;
        this.motionZ = dz / distance * scale;
        this.motionX += this.rand.nextFloat() * 0.07f - 0.035f;
        this.motionY += this.rand.nextFloat() * 0.07f - 0.035f;
        this.motionZ += this.rand.nextFloat() * 0.07f - 0.035f;

        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
        if (this.particleAge > 1 && this.world.getBlockState(pos).isFullCube()) {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.particleAge += 10;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        float fs = (float) this.particleAge / (float) (this.particleMaxAge - 9);
        if (fs <= 1.0f) {
            this.setParticleTextureIndex((int) (151.0f + fs * 6.0f));
        } else {
            this.setParticleTextureIndex(159 - (this.particleMaxAge - this.particleAge) / 3);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float fs = (float) this.particleAge / (float) this.particleMaxAge;
        this.particleScale = this.transferParticleScale * (fs + 0.25f) * 2.0f;
        float fc = (float) this.particleAge * 9.0f / (float) this.particleMaxAge;
        if (fc > 1.0f) {
            fc = 1.0f;
        }
        this.particleRed = fc;
        this.particleGreen = fc;
        this.particleBlue = 1.0f;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
