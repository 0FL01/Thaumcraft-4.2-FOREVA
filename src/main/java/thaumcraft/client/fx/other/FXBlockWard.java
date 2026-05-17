package thaumcraft.client.fx.other;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBlockWard extends Particle {
    private final int blockX;
    private final int blockY;
    private final int blockZ;
    private final float red;
    private final float green;
    private final float blue;
    private final int density;

    public FXBlockWard(World world, int x, int y, int z, int color, int count) {
        super(world, x + 0.5D, y + 0.5D, z + 0.5D, 0.0D, 0.0D, 0.0D);
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.density = Math.max(1, count);
        this.particleMaxAge = 10 + world.rand.nextInt(5);
        this.canCollide = false;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;

        float r = (color & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = ((color >> 16) & 0xFF) / 255.0F;
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

        float progress = this.particleMaxAge <= 0 ? 1.0F : (float) this.particleAge / (float) this.particleMaxAge;
        float fade = progress < 0.5F ? progress * 2.0F : (1.0F - progress) * 2.0F;
        int amount = Math.max(1, Math.round(this.density * (0.6F + fade)));
        for (int i = 0; i < amount; i++) {
            EnumFacing side = EnumFacing.random(this.rand);
            double ux = side.getXOffset();
            double uy = side.getYOffset();
            double uz = side.getZOffset();
            double radial = 0.40D + this.rand.nextFloat() * 0.18D;
            double lateral = 0.42D;
            double px = this.blockX + 0.5D + ux * radial + (this.rand.nextFloat() - 0.5F) * lateral;
            double py = this.blockY + 0.5D + uy * radial + (this.rand.nextFloat() - 0.5F) * lateral;
            double pz = this.blockZ + 0.5D + uz * radial + (this.rand.nextFloat() - 0.5F) * lateral;

            px = MathHelper.clamp(px, this.blockX - 0.12D, this.blockX + 1.12D);
            py = MathHelper.clamp(py, this.blockY - 0.12D, this.blockY + 1.12D);
            pz = MathHelper.clamp(pz, this.blockZ - 0.12D, this.blockZ + 1.12D);

            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                        px + ux * 0.03D, py + uy * 0.03D, pz + uz * 0.03D,
                        0.0D, 0.0D, 0.0D);
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
        // Ward visuals are emitted through spawned particles during update ticks.
    }
}
