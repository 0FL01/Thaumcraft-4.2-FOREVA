package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;

import javax.annotation.Nullable;
import java.awt.Color;

public class RenderWisp extends RenderLiving<EntityWisp> {

    private static final ResourceLocation WISP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    public RenderWisp(RenderManager renderManager) {
        super(renderManager, new ModelBat(), 0.2F);
    }

    @Override
    public void doRender(EntityWisp entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        Aspect aspect = Aspect.getAspect(entity.getWispType());
        if (aspect != null) {
            Color color = new Color(aspect.getColor());
            red = color.getRed() / 255.0F;
            green = color.getGreen() / 255.0F;
            blue = color.getBlue() / 255.0F;
        }
        GlStateManager.color(red, green, blue, 1.0F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWisp entity) {
        return WISP_TEXTURE;
    }
}
