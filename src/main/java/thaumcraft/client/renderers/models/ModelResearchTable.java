package thaumcraft.client.renderers.models;

import java.awt.Color;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelResearchTable extends ModelBase {
    private final ModelRenderer top;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer crossbar;
    private final ModelRenderer inkwell;
    private final ModelRenderer scrollTube;
    private final ModelRenderer scrollRibbon;

    public ModelResearchTable() {
        textureWidth = 128;
        textureHeight = 64;

        top = new ModelRenderer(this, 0, 0);
        top.addBox(0.0F, 0.0F, 0.0F, 32, 4, 16);
        top.setRotationPoint(-8.0F, 0.0F, -8.0F);
        top.setTextureSize(128, 64);
        top.mirror = true;

        leg1 = new ModelRenderer(this, 0, 24);
        leg1.addBox(0.0F, 0.0F, 0.0F, 4, 12, 4);
        leg1.setRotationPoint(-6.0F, 4.0F, -6.0F);
        leg1.setTextureSize(128, 64);
        leg1.mirror = true;

        leg2 = new ModelRenderer(this, 0, 24);
        leg2.addBox(0.0F, 0.0F, 0.0F, 4, 12, 4);
        leg2.setRotationPoint(-6.0F, 4.0F, 2.0F);
        leg2.setTextureSize(128, 64);
        leg2.mirror = true;

        leg3 = new ModelRenderer(this, 0, 24);
        leg3.addBox(0.0F, 0.0F, 0.0F, 4, 12, 4);
        leg3.setRotationPoint(18.0F, 4.0F, -6.0F);
        leg3.setTextureSize(128, 64);
        leg3.mirror = true;

        leg4 = new ModelRenderer(this, 0, 24);
        leg4.addBox(0.0F, 0.0F, 0.0F, 4, 12, 4);
        leg4.setRotationPoint(18.0F, 4.0F, 2.0F);
        leg4.setTextureSize(128, 64);
        leg4.mirror = true;

        crossbar = new ModelRenderer(this, 24, 24);
        crossbar.addBox(0.0F, 0.0F, 0.0F, 24, 4, 4);
        crossbar.setRotationPoint(-4.0F, 10.0F, -2.0F);
        crossbar.setTextureSize(128, 64);
        crossbar.mirror = true;

        inkwell = new ModelRenderer(this, 0, 44);
        inkwell.addBox(0.0F, 0.0F, 0.0F, 3, 2, 3);
        inkwell.setRotationPoint(-6.0F, -2.0F, 3.0F);
        inkwell.setTextureSize(128, 64);
        inkwell.mirror = true;

        scrollTube = new ModelRenderer(this, 0, 0);
        scrollTube.addBox(-21.0F, -0.5F, -8.0F, 8, 2, 2);
        scrollTube.setRotationPoint(-2.0F, -2.0F, 2.0F);
        scrollTube.setTextureSize(128, 64);
        scrollTube.mirror = true;
        setRotation(scrollTube, 0.0F, 10.0F, 0.0F);

        scrollRibbon = new ModelRenderer(this, 0, 4);
        scrollRibbon.addBox(-15.1F, -0.275F, -6.75F, 1, 2, 2);
        scrollRibbon.setRotationPoint(-2.0F, -2.0F, 2.0F);
        scrollRibbon.setTextureSize(128, 64);
        scrollRibbon.mirror = true;
        setRotation(scrollRibbon, 0.0F, 10.0F, 0.0F);
    }

    public void renderAll(float scale) {
        top.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
        crossbar.render(scale);
    }

    public void renderAll() {
        renderAll(0.0625F);
    }

    public void renderInkwell(float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        inkwell.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void renderInkwell() {
        renderInkwell(0.0625F);
    }

    public void renderScroll(float scale, int color) {
        GlStateManager.pushMatrix();
        scrollTube.render(scale);
        Color c = new Color(color);
        GlStateManager.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 1.0F);
        GlStateManager.scale(1.2F, 1.2F, 1.2F);
        scrollRibbon.render(scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public void renderScroll(int color) {
        renderScroll(0.0625F, color);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
