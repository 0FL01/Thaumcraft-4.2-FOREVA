package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXVent extends Particle {
    private final float red;
    private final float green;
    private final float blue;

    public FXVent(World world, double x, double y, double z, double mx, double my, double mz, float red, float green, float blue) {
        super(world, x, y, z, mx, my, mz);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.particleMaxAge = 8 + this.rand.nextInt(6);
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

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.motionX *= 0.92D;
        this.motionY *= 0.92D;
        this.motionZ *= 0.92D;

        this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, this.red, this.green, this.blue);
        this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                this.posX, this.posY, this.posZ,
                this.motionX * 0.2D, this.motionY * 0.2D, this.motionZ * 0.2D);

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
