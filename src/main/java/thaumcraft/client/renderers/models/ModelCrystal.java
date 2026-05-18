package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCrystal extends ModelBase {
    private final ModelRenderer crystal;

    public ModelCrystal() {
        textureWidth = 64;
        textureHeight = 32;

        crystal = new ModelRenderer(this, 0, 0);
        crystal.addBox(-16.0F, -16.0F, 0.0F, 16, 16, 16);
        crystal.setRotationPoint(0.0F, 32.0F, 0.0F);
        crystal.setTextureSize(64, 32);
        crystal.mirror = true;
        setRotation(crystal, 0.7071F, 0.0F, 0.7071F);
    }

    public void render() {
        crystal.render(0.0625F);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
