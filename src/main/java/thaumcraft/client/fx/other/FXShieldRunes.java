package thaumcraft.client.fx.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityCultist;

@SideOnly(Side.CLIENT)
public class FXShieldRunes extends Particle {
    private static final int LIGHTMAP_FULLBRIGHT = 0x00F000F0;

    private final Entity target;
    private final float yaw;
    private final float pitch;

    public FXShieldRunes(World world, Entity target, int age, float yaw, float pitch) {
        super(world, target.posX, target.posY + target.height * 0.5D, target.posZ, 0.0D, 0.0D, 0.0D);
        this.target = target;
        this.yaw = yaw;
        this.pitch = pitch;
        this.particleMaxAge = Math.max(8, age + this.rand.nextInt(Math.max(1, age / 2)));
        this.particleGravity = 0.0F;
        this.setSize(0.01F, 0.01F);
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

        if (++this.particleAge >= this.particleMaxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        if (this.target == null) {
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float progress = (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        int frame = Math.min(15, (int) (14.0F * progress) + 1);
        boolean useRipple = this.target instanceof EntityMob && !(this.target instanceof EntityCultist);
        ResourceLocation frameTexture = new ResourceLocation(
                "thaumcraft",
                "textures/models/" + (useRipple ? "ripple" : "hemis") + frame + ".png");

        float px = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - Particle.interpPosX);
        float py = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - Particle.interpPosY);
        float pz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - Particle.interpPosZ);
        float tint = useRipple ? 0.5F : 1.0F;
        float alpha = Math.min(1.0F, (1.0F - progress) * 3.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(frameTexture);
        GlStateManager.pushMatrix();
        GlStateManager.translate(px, py, pz);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.rotate(180.0F - this.yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4D * this.target.height, 0.4D * this.target.height, 0.4D * this.target.height);
        GlStateManager.color(tint, tint, tint, alpha);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        addLitVertex(buffer, -1.0D, -1.0D, 0.0D, 0.0D, 1.0D, tint, alpha, LIGHTMAP_FULLBRIGHT);
        addLitVertex(buffer,  1.0D, -1.0D, 0.0D, 1.0D, 1.0D, tint, alpha, LIGHTMAP_FULLBRIGHT);
        addLitVertex(buffer,  1.0D,  1.0D, 0.0D, 1.0D, 0.0D, tint, alpha, LIGHTMAP_FULLBRIGHT);
        addLitVertex(buffer, -1.0D,  1.0D, 0.0D, 0.0D, 0.0D, tint, alpha, LIGHTMAP_FULLBRIGHT);
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void addLitVertex(BufferBuilder buffer, double x, double y, double z,
                              double u, double v, float red, float alpha, int lightmap) {
        int lightU = lightmap & 0xFFFF;
        int lightV = lightmap >> 16 & 0xFFFF;
        buffer.pos(x, y, z)
                .tex(u, v)
                .color(red, red, red, alpha)
                .lightmap(lightU, lightV)
                .endVertex();
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
