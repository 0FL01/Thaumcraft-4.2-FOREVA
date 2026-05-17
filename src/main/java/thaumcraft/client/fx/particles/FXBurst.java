package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBurst extends Particle {
    private final float scale;
    private final int density;

    public FXBurst(World world, double x, double y, double z, float scale, int density) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.scale = Math.max(0.2F, scale);
        this.density = Math.max(2, density);
        this.particleMaxAge = 3 + this.rand.nextInt(3);
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

        float life = this.particleMaxAge <= 0 ? 1.0F : (float) this.particleAge / (float) this.particleMaxAge;
        float spread = this.scale * (0.2F + life * 0.35F);
        for (int i = 0; i < this.density; i++) {
            double mx = (this.rand.nextFloat() - 0.5F) * spread;
            double my = (this.rand.nextFloat() - 0.5F) * spread;
            double mz = (this.rand.nextFloat() - 0.5F) * spread;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY, this.posZ, mx, my, mz);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, mx * 0.25D, my * 0.25D, mz * 0.25D);
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
