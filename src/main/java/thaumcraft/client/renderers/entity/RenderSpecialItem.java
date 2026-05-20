package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Random;

public class RenderSpecialItem extends Render<EntityItem> {

    private static final ResourceLocation DEFAULT_ITEM_TEXTURE = TextureMap.LOCATION_BLOCKS_TEXTURE;

    private final Random random = new Random();
    private final RenderEntityItem itemRenderer;

    public RenderSpecialItem(RenderManager renderManager) {
        super(renderManager);
        this.itemRenderer = new RenderEntityItem(renderManager, Minecraft.getMinecraft().getRenderItem());
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderBurst(entity, x, y, z, partialTicks);
        renderWrappedItem(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderBurst(EntityItem entity, double x, double y, double z, float partialTicks) {
        this.random.setSeed(187L);
        float bob = MathHelper.sin(((float) entity.ticksExisted + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + bob + 0.15F, (float) z);

        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        int rays = settings.fancyGraphics ? 10 : 5;
        float time = (float) entity.ticksExisted / 500.0F;
        float fade = 0.0F;
        Random burstRandom = new Random(245L);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (int i = 0; i < rays; i++) {
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(burstRandom.nextFloat() * 360.0F + time * 360.0F, 0.0F, 0.0F, 1.0F);

            float length = burstRandom.nextFloat() * 20.0F + 5.0F + fade * 10.0F;
            float width = burstRandom.nextFloat() * 2.0F + 1.0F + fade * 2.0F;
            length /= 30.0F / ((float) Math.min(entity.ticksExisted, 10) / 10.0F);
            width /= 30.0F / ((float) Math.min(entity.ticksExisted, 10) / 10.0F);

            buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - fade))).endVertex();
            buffer.pos(-0.866D * width, length, -0.5D * width).color(255, 0, 255, 0).endVertex();
            buffer.pos(0.866D * width, length, -0.5D * width).color(255, 0, 255, 0).endVertex();
            buffer.pos(0.0D, length, 1.0D * width).color(255, 0, 255, 0).endVertex();
            buffer.pos(-0.866D * width, length, -0.5D * width).color(255, 0, 255, 0).endVertex();
            tessellator.draw();
        }
        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    protected void renderWrappedItem(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.getItem().isEmpty()) {
            this.itemRenderer.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return DEFAULT_ITEM_TEXTURE;
    }
}
