package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelArcaneWorkbench extends ModelBase {
    private final ModelRenderer top;
    private final ModelRenderer base;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    public ModelArcaneWorkbench() {
        textureWidth = 128;
        textureHeight = 64;

        top = new ModelRenderer(this, 0, 0);
        top.addBox(0.0F, 0.0F, 0.0F, 16, 8, 16);
        top.setRotationPoint(-8.0F, 0.0F, -8.0F);
        top.setTextureSize(128, 64);
        top.mirror = true;

        base = new ModelRenderer(this, 0, 32);
        base.addBox(0.0F, 0.0F, 0.0F, 16, 4, 16);
        base.setRotationPoint(-8.0F, 12.0F, -8.0F);
        base.setTextureSize(128, 64);
        base.mirror = true;

        leg1 = new ModelRenderer(this, 72, 0);
        leg1.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        leg1.setRotationPoint(3.0F, 8.0F, -7.0F);
        leg1.setTextureSize(128, 64);
        leg1.mirror = true;

        leg2 = new ModelRenderer(this, 72, 0);
        leg2.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        leg2.setRotationPoint(-7.0F, 8.0F, 3.0F);
        leg2.setTextureSize(128, 64);
        leg2.mirror = true;

        leg3 = new ModelRenderer(this, 72, 0);
        leg3.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        leg3.setRotationPoint(3.0F, 8.0F, 3.0F);
        leg3.setTextureSize(128, 64);
        leg3.mirror = true;

        leg4 = new ModelRenderer(this, 72, 0);
        leg4.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        leg4.setRotationPoint(-7.0F, 8.0F, -7.0F);
        leg4.setTextureSize(128, 64);
        leg4.mirror = true;
    }

    public void renderAll(float scale) {
        top.render(scale);
        base.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
    }
}
