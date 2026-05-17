package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBoreBase extends ModelBase {
    private final ModelRenderer base1;
    private final ModelRenderer base2;
    private final ModelRenderer pillarMid;
    private final ModelRenderer pillar2;
    private final ModelRenderer pillar3;
    private final ModelRenderer pillar4;
    private final ModelRenderer pillar1;
    private final ModelRenderer nozzle1;
    private final ModelRenderer nozzle2;

    public ModelBoreBase() {
        textureWidth = 128;
        textureHeight = 64;

        base1 = new ModelRenderer(this, 64, 24);
        base1.addBox(-8.0F, 0.0F, -8.0F, 16, 2, 16);
        base1.setRotationPoint(0.0F, 0.0F, 0.0F);
        base1.setTextureSize(128, 64);
        base1.mirror = true;

        base2 = new ModelRenderer(this, 64, 24);
        base2.addBox(-8.0F, 0.0F, -8.0F, 16, 2, 16);
        base2.setRotationPoint(0.0F, 14.0F, 0.0F);
        base2.setTextureSize(128, 64);
        base2.mirror = true;

        pillarMid = new ModelRenderer(this, 84, 42);
        pillarMid.addBox(-2.5F, 0.0F, -2.5F, 5, 12, 5);
        pillarMid.setRotationPoint(0.0F, 2.0F, 0.0F);
        pillarMid.setTextureSize(128, 64);
        pillarMid.mirror = true;

        pillar2 = new ModelRenderer(this, 64, 42);
        pillar2.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        pillar2.setRotationPoint(-5.0F, 2.0F, -5.0F);
        pillar2.setTextureSize(128, 64);
        pillar2.mirror = true;

        pillar3 = new ModelRenderer(this, 64, 42);
        pillar3.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        pillar3.setRotationPoint(-5.0F, 2.0F, 5.0F);
        pillar3.setTextureSize(128, 64);
        pillar3.mirror = true;

        pillar4 = new ModelRenderer(this, 64, 42);
        pillar4.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        pillar4.setRotationPoint(5.0F, 2.0F, 5.0F);
        pillar4.setTextureSize(128, 64);
        pillar4.mirror = true;

        pillar1 = new ModelRenderer(this, 64, 42);
        pillar1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        pillar1.setRotationPoint(5.0F, 2.0F, -5.0F);
        pillar1.setTextureSize(128, 64);
        pillar1.mirror = true;

        nozzle1 = new ModelRenderer(this, 106, 42);
        nozzle1.addBox(2.5F, -2.0F, -2.0F, 5, 4, 4);
        nozzle1.setRotationPoint(0.0F, 8.0F, 0.0F);
        nozzle1.setTextureSize(128, 64);
        nozzle1.mirror = true;

        nozzle2 = new ModelRenderer(this, 106, 51);
        nozzle2.addBox(7.0F, -2.5F, -2.5F, 1, 5, 5);
        nozzle2.setRotationPoint(0.0F, 8.0F, 0.0F);
        nozzle2.setTextureSize(128, 64);
        nozzle2.mirror = true;
    }

    public void render(float scale) {
        base1.render(scale);
        base2.render(scale);
        pillarMid.render(scale);
        pillar2.render(scale);
        pillar3.render(scale);
        pillar4.render(scale);
        pillar1.render(scale);
    }

    public void renderNozzle(float scale) {
        nozzle1.render(scale);
        nozzle2.render(scale);
    }
}
