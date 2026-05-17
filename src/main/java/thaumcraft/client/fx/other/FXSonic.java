package thaumcraft.client.fx.other;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSonic extends Particle {
    private final Entity target;

    public FXSonic(World world, Entity target, int age) {
        super(world, target.posX, target.posY + target.getEyeHeight(), target.posZ, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.particleMaxAge = Math.max(8, age);
        this.canCollide = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.world == null || !this.world.isRemote || this.target == null || !this.target.isEntityAlive()) {
            this.setExpired();
            return;
        }

        this.posX = this.target.posX;
        this.posY = this.target.posY + this.target.getEyeHeight();
        this.posZ = this.target.posZ;

        float ageRatio = (float) this.particleAge / (float) this.particleMaxAge;
        float yaw = (float) Math.toRadians(this.target.rotationYaw);
        float pitch = (float) Math.toRadians(this.target.rotationPitch);
        double forwardX = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
        double forwardY = -MathHelper.sin(pitch);
        double forwardZ = MathHelper.cos(yaw) * MathHelper.cos(pitch);
        double frontDist = 0.6D + ageRatio * (1.8D + this.target.width * 0.5D);
        double centerX = this.posX + forwardX * frontDist;
        double centerY = this.posY + forwardY * frontDist;
        double centerZ = this.posZ + forwardZ * frontDist;

        int points = 10 + this.rand.nextInt(6);
        double radius = 0.22D + ageRatio * (0.45D + this.target.width * 0.35D);
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2.0D * i / points) + this.rand.nextDouble() * 0.2D;
            double ox = Math.cos(angle) * radius;
            double oy = (this.rand.nextFloat() - 0.5F) * 0.1F;
            double oz = Math.sin(angle) * radius;
            this.world.spawnParticle(EnumParticleTypes.REDSTONE,
                    centerX + ox, centerY + oy, centerZ + oz,
                    0.45D, 0.75D, 0.95D);
            if (this.rand.nextBoolean()) {
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                        centerX + ox, centerY + oy, centerZ + oz,
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
        // Sonic visuals are emitted through spawned particles in onUpdate.
    }
}
