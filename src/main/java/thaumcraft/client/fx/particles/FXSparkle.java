package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSparkle extends Particle {
    private final float red;
    private final float green;
    private final float blue;
    private final float spread;
    private final float speed;

    public FXSparkle(World world, double x, double y, double z, float scale, int type, float speed) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spread = Math.max(0.05F, scale * 0.2F);
        this.speed = Math.max(-0.2F, Math.min(0.2F, speed * 0.05F));
        this.particleMaxAge = 6 + this.rand.nextInt(6);
        this.canCollide = false;

        float r = (type & 0xFF) / 255.0F;
        float g = ((type >> 8) & 0xFF) / 255.0F;
        float b = ((type >> 16) & 0xFF) / 255.0F;
        if (r == 0.0F && g == 0.0F && b == 0.0F) {
            r = 0.8F;
            g = 0.8F;
            b = 1.0F;
        }
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

        double px = this.posX + (this.rand.nextFloat() - 0.5F) * this.spread;
        double py = this.posY + (this.rand.nextFloat() - 0.5F) * this.spread;
        double pz = this.posZ + (this.rand.nextFloat() - 0.5F) * this.spread;
        this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
        this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                this.posX, this.posY, this.posZ,
                (this.rand.nextFloat() - 0.5F) * 0.02F,
                this.speed,
                (this.rand.nextFloat() - 0.5F) * 0.02F);

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
