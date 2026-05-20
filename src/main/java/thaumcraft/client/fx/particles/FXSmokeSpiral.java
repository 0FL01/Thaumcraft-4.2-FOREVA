package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXSmokeSpiral extends Particle {
    private float radius = 1.0f;
    private int start = 0;
    private int miny = 0;

    public FXSmokeSpiral(World world, double x, double y, double z, float radius, int start, int miny) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.particleGravity = -0.01f;
        this.particleMaxAge = 20 + world.rand.nextInt(10);
        this.setSize(0.01f, 0.01f);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.radius = radius;
        this.start = start;
        this.miny = miny;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.setAlphaF((float) (this.particleMaxAge - this.particleAge) / (float) this.particleMaxAge);
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int particle = 1 + (int) (this.particleAge / (float) this.particleMaxAge * 4.0F);
        float r1 = this.start + 720.0F * ((this.particleAge + partialTicks) / (float) this.particleMaxAge);
        float r2 = 90.0F - 180.0F * ((this.particleAge + partialTicks) / (float) this.particleMaxAge);
        float mX = -MathHelper.sin(r1 / 180.0F * (float) Math.PI) * MathHelper.cos(r2 / 180.0F * (float) Math.PI);
        float mZ = MathHelper.cos(r1 / 180.0F * (float) Math.PI) * MathHelper.cos(r2 / 180.0F * (float) Math.PI);
        float mY = -MathHelper.sin(r2 / 180.0F * (float) Math.PI);
        mX *= this.radius;
        mY *= this.radius;
        mZ *= this.radius;

        float u0 = (particle % 16) / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = (particle / 16) / 16.0F;
        float v1 = v0 + 0.0624375F;
        float size = 0.15F * this.particleScale;
        float px = (float) (this.posX + mX - Particle.interpPosX);
        float py = (float) (Math.max(this.posY + mY, this.miny + 0.1F) - Particle.interpPosY);
        float pz = (float) (this.posZ + mZ - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightU = brightness & 0xFFFF;
        int lightV = brightness >> 16 & 0xFFFF;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.pos(px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size)
                .tex(u1, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.66F * this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size)
                .tex(u1, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.66F * this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size)
                .tex(u0, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.66F * this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size)
                .tex(u0, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, 0.66F * this.particleAlpha)
                .lightmap(lightU, lightV)
                .endVertex();
        tessellator.draw();
    }
}
