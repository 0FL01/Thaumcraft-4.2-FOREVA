package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelEssentiaReservoir extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer wallNorth;
    private final ModelRenderer wallSouth;
    private final ModelRenderer wallWest;
    private final ModelRenderer wallEast;
    private final ModelRenderer ringTop;

    public ModelEssentiaReservoir() {
        textureWidth = 64;
        textureHeight = 32;

        base = new ModelRenderer(this, 0, 0);
        base.addBox(-6.0F, -1.0F, -6.0F, 12, 2, 12);

        wallNorth = new ModelRenderer(this, 0, 14);
        wallNorth.addBox(-6.0F, -6.0F, -6.0F, 12, 5, 1);

        wallSouth = new ModelRenderer(this, 0, 14);
        wallSouth.addBox(-6.0F, -6.0F, 5.0F, 12, 5, 1);

        wallWest = new ModelRenderer(this, 26, 14);
        wallWest.addBox(-6.0F, -6.0F, -5.0F, 1, 5, 10);

        wallEast = new ModelRenderer(this, 26, 14);
        wallEast.addBox(5.0F, -6.0F, -5.0F, 1, 5, 10);

        ringTop = new ModelRenderer(this, 0, 22);
        ringTop.addBox(-6.0F, -7.0F, -6.0F, 12, 1, 12);
    }

    public void renderAll(float scale) {
        base.render(scale);
        wallNorth.render(scale);
        wallSouth.render(scale);
        wallWest.render(scale);
        wallEast.render(scale);
        ringTop.render(scale);
    }
}
