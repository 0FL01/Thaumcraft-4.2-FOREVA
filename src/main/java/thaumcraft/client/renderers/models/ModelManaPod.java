package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelManaPod extends ModelBase {
    public final ModelRenderer pod0;
    public final ModelRenderer pod1;
    public final ModelRenderer pod2;

    public ModelManaPod() {
        textureWidth = 32;
        textureHeight = 32;

        pod0 = new ModelRenderer(this, 0, 0);
        pod0.addBox(-2.0F, 0.0F, -2.0F, 4, 5, 4);
        pod0.setRotationPoint(0.0F, 0.0F, 0.0F);
        pod0.setTextureSize(32, 32);
        pod0.mirror = true;
        setRotation(pod0, 0.0F, 0.0F, 0.0F);

        pod1 = new ModelRenderer(this, 0, 0);
        pod1.addBox(-3.0F, 0.0F, -3.0F, 6, 7, 6);
        pod1.setRotationPoint(0.0F, 0.0F, 0.0F);
        pod1.setTextureSize(32, 32);
        pod1.mirror = true;
        setRotation(pod1, 0.0F, 0.0F, 0.0F);

        pod2 = new ModelRenderer(this, 0, 0);
        pod2.addBox(-3.5F, 0.0F, -3.5F, 7, 9, 7);
        pod2.setRotationPoint(0.0F, 0.0F, 0.0F);
        pod2.setTextureSize(32, 32);
        pod2.mirror = true;
        setRotation(pod2, 0.0F, 0.0F, 0.0F);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
