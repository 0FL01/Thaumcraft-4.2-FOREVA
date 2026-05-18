package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBellows extends ModelBase {
    public final ModelRenderer bottomPlank;
    public final ModelRenderer middlePlank;
    public final ModelRenderer topPlank;
    public final ModelRenderer bag;
    public final ModelRenderer nozzle;

    public ModelBellows() {
        textureWidth = 128;
        textureHeight = 64;

        bottomPlank = new ModelRenderer(this, 0, 0);
        bottomPlank.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12);
        bottomPlank.setRotationPoint(0.0F, 22.0F, 0.0F);
        bottomPlank.setTextureSize(128, 64);
        bottomPlank.mirror = true;

        middlePlank = new ModelRenderer(this, 0, 0);
        middlePlank.addBox(-6.0F, -1.0F, -6.0F, 12, 2, 12);
        middlePlank.setRotationPoint(0.0F, 16.0F, 0.0F);
        middlePlank.setTextureSize(128, 64);
        middlePlank.mirror = true;

        topPlank = new ModelRenderer(this, 0, 0);
        topPlank.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12);
        topPlank.setRotationPoint(0.0F, 8.0F, 0.0F);
        topPlank.setTextureSize(128, 64);
        topPlank.mirror = true;

        bag = new ModelRenderer(this, 48, 0);
        bag.addBox(-10.0F, -12.03333F, -10.0F, 20, 24, 20);
        bag.setRotationPoint(0.0F, 16.0F, 0.0F);
        bag.setTextureSize(64, 32);
        bag.mirror = true;

        nozzle = new ModelRenderer(this, 0, 36);
        nozzle.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 2);
        nozzle.setRotationPoint(0.0F, 16.0F, 6.0F);
        nozzle.setTextureSize(128, 64);
        nozzle.mirror = true;
    }

    public void renderMid(float scale) {
        middlePlank.render(scale);
        nozzle.render(scale);
    }

    public void render() {
        renderMid(0.0625F);
    }
}
