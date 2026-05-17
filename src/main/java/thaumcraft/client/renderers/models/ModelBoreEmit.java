package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBoreEmit extends ModelBase {
    private final ModelRenderer knob;
    private final ModelRenderer cross1;
    private final ModelRenderer cross3;
    private final ModelRenderer cross2;
    private final ModelRenderer rod;

    public ModelBoreEmit() {
        textureWidth = 128;
        textureHeight = 64;

        knob = new ModelRenderer(this, 66, 0);
        knob.addBox(-2.0F, 12.0F, -2.0F, 4, 4, 4);
        knob.setRotationPoint(0.0F, 0.0F, 0.0F);
        knob.setTextureSize(128, 64);
        knob.mirror = true;

        cross1 = new ModelRenderer(this, 56, 16);
        cross1.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 4);
        cross1.setRotationPoint(0.0F, 8.0F, 0.0F);
        cross1.setTextureSize(128, 64);
        cross1.mirror = true;

        cross3 = new ModelRenderer(this, 56, 16);
        cross3.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 4);
        cross3.setRotationPoint(0.0F, 0.0F, 0.0F);
        cross3.setTextureSize(128, 64);
        cross3.mirror = true;

        cross2 = new ModelRenderer(this, 56, 24);
        cross2.addBox(-3.0F, 4.0F, -3.0F, 6, 1, 6);
        cross2.setRotationPoint(0.0F, 0.0F, 0.0F);
        cross2.setTextureSize(128, 64);
        cross2.mirror = true;

        rod = new ModelRenderer(this, 56, 0);
        rod.addBox(-1.0F, 1.0F, -1.0F, 2, 11, 2);
        rod.setRotationPoint(0.0F, 0.0F, 0.0F);
        rod.setTextureSize(128, 64);
        rod.mirror = true;
    }

    public void render(float scale, boolean focus) {
        if (focus) {
            knob.render(scale);
        }
        cross1.render(scale);
        cross3.render(scale);
        cross2.render(scale);
        rod.render(scale);
    }
}
