package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXVisSparkle extends Particle {
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final float red;
    private final float green;
    private final float blue;
    private final int density;

    public FXVisSparkle(World world, int x, int y, int z, float red, float green, float blue, int density) {
        super(world, x + 0.5D, y + 0.5D, z + 0.5D, 0.0D, 0.0D, 0.0D);
        this.baseX = x;
        this.baseY = y;
        this.baseZ = z;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.density = Math.max(1, density);
        this.particleMaxAge = 6 + this.rand.nextInt(5);
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

        for (int i = 0; i < this.density; i++) {
            double px = this.baseX + this.rand.nextFloat();
            double py = this.baseY + this.rand.nextFloat();
            double pz = this.baseZ + this.rand.nextFloat();
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, this.red, this.green, this.blue);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, px, py, pz, 0.0D, 0.0D, 0.0D);
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
