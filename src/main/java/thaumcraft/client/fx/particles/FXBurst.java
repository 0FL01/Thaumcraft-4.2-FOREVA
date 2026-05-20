package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.tile.TileNodeRenderer;

@SideOnly(Side.CLIENT)
public class FXBurst extends Particle {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;

    private final float scale;

    public FXBurst(World world, double x, double y, double z, float scale, int density) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.scale = Math.max(0.2F, scale) * (0.9F + Math.min(1.1F, Math.max(1, density) * 0.04F));
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleGravity = 0.0F;
        this.particleMaxAge = 31;
        this.canCollide = false;
        this.setSize(0.01F, 0.01F);
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
        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float u0 = (this.particleAge % 32) / 32.0F;
        float u1 = u0 + 0.03125F;
        float v0 = 0.96875F;
        float v1 = 1.0F;
        float size = this.scale;
        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);

        Minecraft.getMinecraft().renderEngine.bindTexture(TileNodeRenderer.NODES_TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addLitVertex(buffer, px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size, u1, v1);
        addLitVertex(buffer, px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size, u1, v0);
        addLitVertex(buffer, px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size, u0, v0);
        addLitVertex(buffer, px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size, u0, v1);
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void addLitVertex(BufferBuilder buffer, double x, double y, double z, double u, double v) {
        int lightU = LIGHTMAP_FULLBRIGHT & 0xFFFF;
        int lightV = LIGHTMAP_FULLBRIGHT >> 16 & 0xFFFF;
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(1.0F, 1.0F, 1.0F, 0.75F)
                .lightmap(lightU, lightV)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
