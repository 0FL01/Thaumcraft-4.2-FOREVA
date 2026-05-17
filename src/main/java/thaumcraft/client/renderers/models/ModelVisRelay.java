package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelVisRelay extends ModelBase {
    private final ModelRenderer ringBase;
    private final ModelRenderer ringFloat;
    private final ModelRenderer crystal;

    public ModelVisRelay() {
        textureWidth = 64;
        textureHeight = 32;

        ringBase = new ModelRenderer(this, 0, 0);
        ringBase.addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8);

        ringFloat = new ModelRenderer(this, 0, 10);
        ringFloat.addBox(-3.0F, -0.5F, -3.0F, 6, 1, 6);

        crystal = new ModelRenderer(this, 28, 10);
        crystal.addBox(-1.0F, -3.5F, -1.0F, 2, 5, 2);
    }

    public void renderRingBase(float scale) {
        ringBase.render(scale);
    }

    public void renderRingFloat(float scale) {
        ringFloat.render(scale);
    }

    public void renderCrystal(float scale) {
        crystal.render(scale);
    }
}
