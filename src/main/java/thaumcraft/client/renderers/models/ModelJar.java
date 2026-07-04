package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelJar extends ModelBase {
    public final ModelRenderer core;
    public final ModelRenderer brine;
    public final ModelRenderer lid;

    public ModelJar() {
        textureWidth = 64;
        textureHeight = 32;

        core = new ModelRenderer(this, 0, 0);
        core.addBox(-5.0F, -12.0F, -5.0F, 10, 12, 10);
        core.setRotationPoint(0.0F, 0.0F, 0.0F);
        core.setTextureSize(64, 32);
        core.mirror = true;

        brine = new ModelRenderer(this, 0, 0);
        brine.addBox(-4.0F, -11.0F, -4.0F, 8, 10, 8);
        brine.setRotationPoint(0.0F, 0.0F, 0.0F);
        brine.setTextureSize(64, 32);
        brine.mirror = true;

        lid = new ModelRenderer(this, 0, 24);
        lid.addBox(-3.0F, 0.0F, -3.0F, 6, 2, 6);
        lid.setRotationPoint(0.0F, -14.0F, 0.0F);
        lid.setTextureSize(64, 32);
        lid.mirror = true;
    }

    public void renderCore(float scale) {
        core.render(scale);
    }

    public void renderBrine(float scale) {
        brine.render(scale);
    }

    public void renderLid(float scale) {
        lid.render(scale);
    }

    public void renderAll(float scale) {
        renderLid(scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        renderCore(scale);
        GlStateManager.disableBlend();
    }
}
