package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelMagicWorkbenchCharger extends ModelBase {
    private final ModelRenderer ringFloat;
    private final ModelRenderer support;
    private final ModelRenderer crystal;

    public ModelMagicWorkbenchCharger() {
        textureWidth = 64;
        textureHeight = 32;

        ringFloat = new ModelRenderer(this, 0, 10);
        ringFloat.addBox(-3.0F, -0.5F, -3.0F, 6, 1, 6);

        support = new ModelRenderer(this, 28, 0);
        support.addBox(-0.5F, -0.5F, -3.5F, 1, 1, 7);

        crystal = new ModelRenderer(this, 28, 10);
        crystal.addBox(-1.0F, -3.5F, -1.0F, 2, 5, 2);
    }

    public void renderRingFloat(float scale) {
        ringFloat.render(scale);
    }

    public void renderSupport(float scale) {
        support.render(scale);
    }

    public void renderCrystal(float scale) {
        crystal.render(scale);
    }
}
