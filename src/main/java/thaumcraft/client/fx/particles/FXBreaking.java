package thaumcraft.client.fx.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FXBreaking extends Particle {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    private final Item item;
    private final int itemMeta;
    private final TextureAtlasSprite particleSprite;
    private final int textureJitterX;
    private final int textureJitterY;

    public FXBreaking(World world, double x, double y, double z, Item item) {
        this(world, x, y, z, 0.0D, 0.0D, 0.0D, item, 0);
    }

    public FXBreaking(World world, double x, double y, double z, double mx, double my, double mz, Item item) {
        this(world, x, y, z, mx, my, mz, item, 0);
    }

    public FXBreaking(World world, double x, double y, double z, double mx, double my, double mz, Item item, int meta) {
        super(world, x, y, z, mx, my, mz);
        this.item = item;
        this.itemMeta = meta;
        this.particleSprite = item == null ? null : Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, meta);
        this.textureJitterX = this.rand.nextInt(4);
        this.textureJitterY = this.rand.nextInt(4);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.motionX += mx;
        this.motionY += my;
        this.motionZ += mz;
        this.particleScale *= 0.5F;
        this.particleGravity = Blocks.SNOW_LAYER.blockParticleGravity;
        this.particleMaxAge = 12 + this.rand.nextInt(8);
        this.canCollide = false;
    }

    public void setParticleMaxAge(int particleMaxAge) {
        this.particleMaxAge = Math.max(1, particleMaxAge);
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

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }

        this.motionY -= 0.04D * this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        if (this.particleSprite == null) {
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float u0 = this.particleSprite.getInterpolatedU(this.textureJitterX / 4.0F * 16.0F);
        float u1 = this.particleSprite.getInterpolatedU((this.textureJitterX + 1.0F) / 4.0F * 16.0F);
        float v0 = this.particleSprite.getInterpolatedV(this.textureJitterY / 4.0F * 16.0F);
        float v1 = this.particleSprite.getInterpolatedV((this.textureJitterY + 1.0F) / 4.0F * 16.0F);
        float fade = 1.0F - this.particleAge / (float) Math.max(1, this.particleMaxAge);
        float size = 0.1F * this.particleScale * fade;
        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        int brightness = this.getBrightnessForRender(partialTicks);
        int lightU = brightness & 0xFFFF;
        int lightV = brightness >> 16 & 0xFFFF;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.pos(px - rotationX * size - rotationXY * size, py - rotationZ * size, pz - rotationYZ * size - rotationXZ * size)
                .tex(u0, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * fade)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px - rotationX * size + rotationXY * size, py + rotationZ * size, pz - rotationYZ * size + rotationXZ * size)
                .tex(u0, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * fade)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px + rotationX * size + rotationXY * size, py + rotationZ * size, pz + rotationYZ * size + rotationXZ * size)
                .tex(u1, v0)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * fade)
                .lightmap(lightU, lightV)
                .endVertex();
        buffer.pos(px + rotationX * size - rotationXY * size, py - rotationZ * size, pz + rotationYZ * size - rotationXZ * size)
                .tex(u1, v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * fade)
                .lightmap(lightU, lightV)
                .endVertex();
        tessellator.draw();

        Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURE);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
