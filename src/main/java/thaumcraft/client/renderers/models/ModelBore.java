package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBore extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer side1;
    private final ModelRenderer side2;
    private final ModelRenderer nozzleCrossbar;
    private final ModelRenderer nozzleFront;
    private final ModelRenderer nozzleMid;

    public ModelBore() {
        textureWidth = 128;
        textureHeight = 64;

        base = new ModelRenderer(this, 0, 32);
        base.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12);
        base.setRotationPoint(0.0F, 0.0F, 0.0F);
        base.setTextureSize(64, 32);
        base.mirror = true;

        side1 = new ModelRenderer(this, 0, 0);
        side1.addBox(-2.0F, 2.0F, -5.5F, 4, 8, 1);
        side1.setRotationPoint(0.0F, 0.0F, 0.0F);
        side1.setTextureSize(64, 32);
        side1.mirror = true;

        side2 = new ModelRenderer(this, 0, 0);
        side2.addBox(-2.0F, 2.0F, 4.5F, 4, 8, 1);
        side2.setRotationPoint(0.0F, 0.0F, 0.0F);
        side2.setTextureSize(64, 32);
        side2.mirror = true;

        nozzleCrossbar = new ModelRenderer(this, 0, 48);
        nozzleCrossbar.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12);
        nozzleCrossbar.setRotationPoint(0.0F, 8.0F, 0.0F);
        nozzleCrossbar.setTextureSize(64, 32);
        nozzleCrossbar.mirror = true;

        nozzleFront = new ModelRenderer(this, 30, 14);
        nozzleFront.addBox(4.0F, -2.5F, -2.5F, 4, 5, 5);
        nozzleFront.setRotationPoint(0.0F, 8.0F, 0.0F);
        nozzleFront.setTextureSize(64, 32);
        nozzleFront.mirror = true;

        nozzleMid = new ModelRenderer(this, 0, 14);
        nozzleMid.addBox(-2.0F, -4.0F, -4.0F, 6, 8, 8);
        nozzleMid.setRotationPoint(0.0F, 8.0F, 0.0F);
        nozzleMid.setTextureSize(64, 32);
        nozzleMid.mirror = true;
    }

    public void renderBase(float scale) {
        base.render(scale);
        side1.render(scale);
        side2.render(scale);
        nozzleCrossbar.render(scale);
    }

    public void renderNozzle(float scale) {
        nozzleFront.render(scale);
        nozzleMid.render(scale);
    }
}
