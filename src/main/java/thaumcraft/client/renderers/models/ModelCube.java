package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCube extends ModelBase {
    private final ModelRenderer cube;

    public ModelCube() {
        textureWidth = 64;
        textureHeight = 32;
        cube = new ModelRenderer(this, 0, 0);
        cube.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        cube.setRotationPoint(8.0F, 8.0F, 8.0F);
        cube.setTextureSize(64, 32);
        cube.mirror = true;
    }

    public ModelCube(int shift) {
        textureWidth = 64;
        textureHeight = 64;
        cube = new ModelRenderer(this, 0, shift);
        cube.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        cube.setRotationPoint(0.0F, 0.0F, 0.0F);
        cube.setTextureSize(64, 64);
        cube.mirror = true;
    }

    public void render() {
        cube.render(0.0625F);
    }
}
