package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ITCParticle;

@SideOnly(Side.CLIENT)
public class FXBlockRunes extends Particle implements ITCParticle {
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;

    private final double ofx;
    private final double ofy;
    private final int rotation;
    private final int runeIndex;
    private float gravity;

    public FXBlockRunes(World world, double x, double y, double z, float red, float green, float blue, int duration) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.particleRed = red == 0.0F ? 1.0F : red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.rotation = this.rand.nextInt(4) * 90;
        this.gravity = 0.0F;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.particleMaxAge = 3 * duration;
        this.canCollide = false;
        this.setSize(0.01F, 0.01F);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.runeIndex = 224 + this.rand.nextInt(16);
        this.ofx = this.rand.nextFloat() * 0.2D;
        this.ofy = -0.3D + this.rand.nextFloat() * 0.6D;
        this.particleScale = (float) (1.0D + this.rand.nextGaussian() * 0.1D);
        this.particleAlpha = 0.0F;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
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
        float threshold = (float) this.particleMaxAge / 5.0F;
        this.particleAlpha = (float) this.particleAge <= threshold
                ? (float) this.particleAge / threshold
                : (float) (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        this.motionY -= 0.04D * (double) this.gravity;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - Particle.interpPosZ);
        float u0 = (float) (this.runeIndex % 16) / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.375F;
        float v1 = v0 + 0.0624375F;
        float size = 0.3F * this.particleScale;

        addLitVertex(buffer, px, py, pz, -0.5D * size, 0.5D * size, u1, v1);
        addLitVertex(buffer, px, py, pz, 0.5D * size, 0.5D * size, u1, v0);
        addLitVertex(buffer, px, py, pz, 0.5D * size, -0.5D * size, u0, v0);
        addLitVertex(buffer, px, py, pz, -0.5D * size, -0.5D * size, u0, v1);
    }

    private void addLitVertex(BufferBuilder buffer, double px, double py, double pz, double x, double y, double u, double v) {
        double tx = x + this.ofx;
        double ty = y + this.ofy;
        double tz = -0.51D;

        double rzX = -ty;
        double rzY = tx;
        double angle = this.rotation * ((float) Math.PI / 180.0F);
        double sin = MathHelper.sin((float) angle);
        double cos = MathHelper.cos((float) angle);
        double rx = rzX * cos + tz * sin;
        double rz = tz * cos - rzX * sin;

        int lightU = LIGHTMAP_FULLBRIGHT & 0xFFFF;
        int lightV = LIGHTMAP_FULLBRIGHT >> 16 & 0xFFFF;
        buffer.pos(px + rx, py + rzY, pz + rz)
                .tex(u, v)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha / 2.0F)
                .lightmap(lightU, lightV)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public int getTCParticleLayer() {
        return 1;
    }
}
