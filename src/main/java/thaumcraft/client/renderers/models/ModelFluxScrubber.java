package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelFluxScrubber extends ModelBase {
    private final ModelRenderer cap;
    private final ModelRenderer tip;

    public ModelFluxScrubber() {
        textureWidth = 64;
        textureHeight = 32;

        cap = new ModelRenderer(this, 0, 0);
        cap.addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8);

        tip = new ModelRenderer(this, 0, 10);
        tip.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4);
    }

    public void renderCap(float scale) {
        cap.render(scale);
    }

    public void renderTip(float scale) {
        tip.render(scale);
    }
}
