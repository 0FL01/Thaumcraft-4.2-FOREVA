package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBrain extends ModelBase {
    private final ModelRenderer shape1;
    private final ModelRenderer shape2;
    private final ModelRenderer shape3;

    public ModelBrain() {
        textureWidth = 128;
        textureHeight = 64;

        shape1 = new ModelRenderer(this, 0, 0);
        shape1.addBox(0.0F, 0.0F, 0.0F, 12, 10, 16);
        shape1.setRotationPoint(-6.0F, 8.0F, -8.0F);
        shape1.setTextureSize(128, 64);
        shape1.mirror = true;
        setRotation(shape1, 0.0F, 0.0F, 0.0F);

        shape2 = new ModelRenderer(this, 64, 0);
        shape2.addBox(0.0F, 0.0F, 0.0F, 8, 3, 7);
        shape2.setRotationPoint(-4.0F, 18.0F, 0.0F);
        shape2.setTextureSize(128, 64);
        shape2.mirror = true;
        setRotation(shape2, 0.0F, 0.0F, 0.0F);

        shape3 = new ModelRenderer(this, 0, 32);
        shape3.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2);
        shape3.setRotationPoint(-1.0F, 18.0F, -2.0F);
        shape3.setTextureSize(128, 64);
        shape3.mirror = true;
        setRotation(shape3, 0.4089647F, 0.0F, 0.0F);
    }

    public void render(float scale) {
        shape1.render(scale);
        shape2.render(scale);
        shape3.render(scale);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
