package thaumcraft.client.fx.beams;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXArc extends Particle {
    private final List<Vec3d> points = new ArrayList<>();
    private final float red;
    private final float green;
    private final float blue;

    public FXArc(World world, double x, double y, double z,
                 double tx, double ty, double tz,
                 float red, float green, float blue, double heightGain) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.particleMaxAge = 2;
        this.canCollide = false;
        buildArcPoints(x, y, z, tx, ty, tz, heightGain);
    }

    private void buildArcPoints(double x, double y, double z, double tx, double ty, double tz, double heightGain) {
        Vec3d start = new Vec3d(x, y, z);
        Vec3d end = new Vec3d(tx, ty, tz);
        Vec3d delta = end.subtract(start);
        double length = delta.length();
        int segments = Math.max(8, Math.min(40, (int) (length * 7.0D)));

        points.clear();
        points.add(start);
        for (int i = 1; i < segments; i++) {
            double t = (double) i / (double) segments;
            double px = x + delta.x * t;
            double py = y + delta.y * t;
            double pz = z + delta.z * t;

            double parabola = 4.0D * t * (1.0D - t);
            py += parabola * heightGain;
            double noise = 0.035D + length * 0.02D;
            px += (rand.nextDouble() - rand.nextDouble()) * noise;
            py += (rand.nextDouble() - rand.nextDouble()) * noise;
            pz += (rand.nextDouble() - rand.nextDouble()) * noise;
            points.add(new Vec3d(px, py, pz));
        }
        points.add(end);
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

        for (Vec3d point : points) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, point.x, point.y, point.z, red, green, blue);
            if (rand.nextBoolean()) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, point.x, point.y, point.z, 0.0D, 0.0D, 0.0D);
            }
        }

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, net.minecraft.entity.Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // Arc visual is emitted during update through spawned particles.
    }
}
