package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelAlchemyFurnaceAdvanced extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer tankPanel;
    private final ModelRenderer lavaPanel;

    public ModelAlchemyFurnaceAdvanced() {
        textureWidth = 128;
        textureHeight = 64;

        base = new ModelRenderer(this, 0, 0);
        base.addBox(-8.0F, -1.0F, -8.0F, 16, 14, 16);

        tankPanel = new ModelRenderer(this, 64, 0);
        tankPanel.addBox(-3.5F, -8.0F, 7.3F, 7, 10, 1);

        lavaPanel = new ModelRenderer(this, 64, 12);
        lavaPanel.addBox(-3.5F, -8.0F, 7.31F, 7, 10, 1);
    }

    public void renderBase(float scale) {
        base.render(scale);
    }

    public void renderTankPanel(float scale) {
        tankPanel.render(scale);
    }

    public void renderLavaPanel(float scale) {
        lavaPanel.render(scale);
    }
}
