package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTubeValve extends ModelBase {
    private final ModelRenderer valveRod;

    public ModelTubeValve() {
        textureWidth = 64;
        textureHeight = 32;

        valveRod = new ModelRenderer(this, 0, 10);
        valveRod.addBox(-1.0F, 2.0F, -1.0F, 2, 2, 2);
        valveRod.setRotationPoint(0.0F, 0.0F, 0.0F);
        valveRod.setTextureSize(64, 32);
        valveRod.mirror = true;
    }

    public void render(float scale) {
        valveRod.render(scale);
    }
}
