package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

import javax.annotation.Nullable;

public class RenderTaintCreeper extends RenderLiving<EntityTaintCreeper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/models/creeper.png");
    private static final ResourceLocation ARMOR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/entity/creeper/creeper_armor.png");

    public RenderTaintCreeper(RenderManager renderManager) {
        super(renderManager, new ModelCreeper(), 0.5F);
        this.addLayer(new CreeperArmorLayer(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTaintCreeper entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityTaintCreeper entity, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        float wobble = 1.0F + MathHelper.sin(flash * 100.0F) * flash * 0.01F;
        flash = MathHelper.clamp(flash, 0.0F, 1.0F);
        flash *= flash;
        flash *= flash;
        float scaleXZ = (1.0F + flash * 0.4F) * wobble;
        float scaleY = (1.0F + flash * 0.1F) / wobble;
        GlStateManager.scale(scaleXZ, scaleY, scaleXZ);
    }

    @Override
    protected int getColorMultiplier(EntityTaintCreeper entity, float lightBrightness, float partialTickTime) {
        float flash = entity.getCreeperFlashIntensity(partialTickTime);
        if (((int) (flash * 10.0F)) % 2 == 0) {
            return 0;
        }
        int alpha = (int) (flash * 0.2F * 255.0F);
        alpha = MathHelper.clamp(alpha, 0, 255);
        return alpha << 24 | 0xFFFFFF;
    }

    private static final class CreeperArmorLayer implements LayerRenderer<EntityTaintCreeper> {
        private final RenderTaintCreeper renderer;
        private final ModelCreeper armorModel = new ModelCreeper(2.0F);

        private CreeperArmorLayer(RenderTaintCreeper renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(EntityTaintCreeper entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (!entity.getPowered()) {
                return;
            }
            boolean invisible = entity.isInvisible();
            GlStateManager.depthMask(!invisible);
            renderer.bindTexture(ARMOR_TEXTURE);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float ticks = entity.ticksExisted + partialTicks;
            GlStateManager.translate(ticks * 0.01F, ticks * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            armorModel.setModelAttributes(renderer.getMainModel());
            Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
            armorModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(invisible);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
