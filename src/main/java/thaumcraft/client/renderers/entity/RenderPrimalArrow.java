package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.blocks.ItemBlocks.BlockCustomOreItem;
import thaumcraft.common.entities.projectile.EntityPrimalArrow;

import javax.annotation.Nullable;

public class RenderPrimalArrow extends RenderArrow<EntityPrimalArrow> {

    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/entity/arrow.png");

    public RenderPrimalArrow(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityPrimalArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        int[] colors = BlockCustomOreItem.colors;
        int colorIndex = MathHelper.clamp(entity.getArrowType() + 1, 0, colors.length - 1);
        int color = colors[colorIndex];
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPrimalArrow entity) {
        return ARROW_TEXTURE;
    }
}
