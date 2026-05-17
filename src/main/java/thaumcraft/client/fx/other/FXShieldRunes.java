package thaumcraft.client.fx.other;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXShieldRunes extends Particle {
    private final Entity target;
    private final float yaw;
    private final float pitch;

    public FXShieldRunes(World world, Entity target, int age, float yaw, float pitch) {
        super(world, target.posX, target.posY + target.height * 0.5D, target.posZ, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
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
        this.posY = this.target.getEntityBoundingBox().minY + this.target.height * 0.5D;
        this.posZ = this.target.posZ;

        float ageRatio = (float) this.particleAge / (float) this.particleMaxAge;
        int points = 8 + this.rand.nextInt(6);
        double radius = this.target.width * (0.5D + 0.45D * ageRatio);
        double spin = Math.toRadians(this.yaw + this.pitch * 0.2F + this.particleAge * 18.0F);
        for (int i = 0; i < points; i++) {
            double angle = spin + (Math.PI * 2.0D * i / points);
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            double oy = (this.rand.nextFloat() - 0.5F) * this.target.height * 0.35D;

            this.world.spawnParticle(EnumParticleTypes.REDSTONE,
                    this.posX + ox, this.posY + oy, this.posZ + oz,
                    0.35D, 0.65D, 1.0D);
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                    this.posX + ox * 0.85D, this.posY + oy, this.posZ + oz * 0.85D,
                    0.0D, 0.0D, 0.0D);
        }

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        // Shield rune visuals are emitted through spawned particles in onUpdate.
    }
}
