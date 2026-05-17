package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCentrifuge extends ModelBase {
    private final ModelRenderer crossbar;
    private final ModelRenderer dingus1;
    private final ModelRenderer dingus2;
    private final ModelRenderer core;
    private final ModelRenderer top;
    private final ModelRenderer bottom;

    public ModelCentrifuge() {
        textureWidth = 64;
        textureHeight = 32;

        crossbar = new ModelRenderer(this, 16, 0);
        crossbar.addBox(-4.0F, -1.0F, -1.0F, 8, 2, 2);
        crossbar.setRotationPoint(0.0F, 0.0F, 0.0F);
        crossbar.setTextureSize(64, 32);
        crossbar.mirror = true;

        dingus1 = new ModelRenderer(this, 0, 16);
        dingus1.addBox(4.0F, -3.0F, -2.0F, 4, 6, 4);
        dingus1.setRotationPoint(0.0F, 0.0F, 0.0F);
        dingus1.setTextureSize(64, 32);
        dingus1.mirror = true;

        dingus2 = new ModelRenderer(this, 0, 16);
        dingus2.addBox(-8.0F, -3.0F, -2.0F, 4, 6, 4);
        dingus2.setRotationPoint(0.0F, 0.0F, 0.0F);
        dingus2.setTextureSize(64, 32);
        dingus2.mirror = true;

        core = new ModelRenderer(this, 0, 0);
        core.addBox(-1.5F, -4.0F, -1.5F, 3, 8, 3);
        core.setRotationPoint(0.0F, 0.0F, 0.0F);
        core.setTextureSize(64, 32);
        core.mirror = true;

        top = new ModelRenderer(this, 20, 16);
        top.addBox(-4.0F, -8.0F, -4.0F, 8, 4, 8);
        top.setRotationPoint(0.0F, 0.0F, 0.0F);
        top.setTextureSize(64, 32);
        top.mirror = true;

        bottom = new ModelRenderer(this, 20, 16);
        bottom.addBox(-4.0F, 4.0F, -4.0F, 8, 4, 8);
        bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        bottom.setTextureSize(64, 32);
        bottom.mirror = true;
    }

    public void renderBoxes(float scale) {
        top.render(scale);
        bottom.render(scale);
    }

    public void renderSpinnyBit(float scale) {
        crossbar.render(scale);
        dingus1.render(scale);
        dingus2.render(scale);
        core.render(scale);
    }
}
