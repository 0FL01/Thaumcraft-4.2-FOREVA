package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBlockRunes extends Particle {
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private float gravity;
    private final float red;
    private final float green;
    private final float blue;

    public FXBlockRunes(World world, double x, double y, double z, float red, float green, float blue, int duration) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.gravity = 0.0F;
        this.particleMaxAge = Math.max(8, duration);
        this.canCollide = false;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void onUpdate() {
        if (this.world == null || !this.world.isRemote) {
            setExpired();
            return;
        }
        if (this.particleAge++ >= this.particleMaxAge) {
            setExpired();
            return;
        }

        EnumFacing face = EnumFacing.random(this.rand);
        double px = this.centerX + 0.5D + face.getXOffset() * 0.51D + (this.rand.nextFloat() - 0.5F) * 0.35F;
        double py = this.centerY + 0.5D + face.getYOffset() * 0.51D + (this.rand.nextFloat() - 0.5F) * 0.35F;
        double pz = this.centerZ + 0.5D + face.getZOffset() * 0.51D + (this.rand.nextFloat() - 0.5F) * 0.35F;
        this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py - this.gravity * 0.03D, pz, this.red, this.green, this.blue);
        if (this.rand.nextInt(3) == 0) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, net.minecraft.entity.Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // Emission particle rendered via world.spawnParticle in onUpdate.
    }
}
