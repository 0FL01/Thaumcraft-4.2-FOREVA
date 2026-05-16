package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.projectile.EntityFrostShard;

import javax.annotation.Nullable;
import java.util.Random;

public class RenderFrostShard extends Render<EntityFrostShard> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/blocks/frostshard.png");

    public RenderFrostShard(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFrostShard entity, double x, double y, double z, float entityYaw, float partialTicks) {
        bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.translate((float) x, (float) y, (float) z);

        Random random = new Random(entity.getEntityId());
        float baseScale = entity.getDamage() * 0.1F;
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(
                baseScale + random.nextFloat() * 0.1F,
                baseScale + random.nextFloat() * 0.1F,
                baseScale + random.nextFloat() * 0.1F
        );

        renderCrossQuads();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderCrossQuads() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float half = 0.5F;
        for (int i = 0; i < 2; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-half, -half, 0.0D).tex(0.0D, 1.0D).endVertex();
            buffer.pos(half, -half, 0.0D).tex(1.0D, 1.0D).endVertex();
            buffer.pos(half, half, 0.0D).tex(1.0D, 0.0D).endVertex();
            buffer.pos(-half, half, 0.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFrostShard entity) {
        return TEXTURE;
    }
}
