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
public class FXSmokeSpiral extends Particle {
    private float radius = 1.0f;
    private int start = 0;
    private int miny = 0;

    public FXSmokeSpiral(World world, double x, double y, double z, float radius, int start, int miny) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleGravity = -0.01f;
        this.particleMaxAge = 20 + world.rand.nextInt(10);
        this.setSize(0.01f, 0.01f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        this.setAlphaF((float) (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge);
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        float r1 = this.start + 720.0f * ((float) this.particleAge / (float) this.particleMaxAge);
        float r2 = 90.0f - 180.0f * ((float) this.particleAge / (float) this.particleMaxAge);
        float mX = -MathHelper.sin(r1 / 180.0f * (float) Math.PI) * MathHelper.cos(r2 / 180.0f * (float) Math.PI);
        float mZ = MathHelper.cos(r1 / 180.0f * (float) Math.PI) * MathHelper.cos(r2 / 180.0f * (float) Math.PI);
        float mY = -MathHelper.sin(r2 / 180.0f * (float) Math.PI);
        mX *= this.radius;
        mY *= this.radius;
        mZ *= this.radius;

        double py = Math.max(this.posY + mY, (float) this.miny + 0.1f);
        this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + mX, py, this.posZ + mZ, 0.0, 0.0, 0.0);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // Emission-style particle: visuals are spawned in onUpdate.
    }
}
