package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelThaumatorium extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer ring;
    private final ModelRenderer side1;
    private final ModelRenderer side2;
    private final ModelRenderer side3;
    private final ModelRenderer side4;
    private final ModelRenderer spout;

    public ModelThaumatorium() {
        textureWidth = 64;
        textureHeight = 64;

        base = new ModelRenderer(this, 0, 0);
        base.addBox(-6.0F, 9.0F, -6.0F, 12, 5, 12);

        ring = new ModelRenderer(this, 0, 17);
        ring.addBox(-5.5F, 6.5F, -5.5F, 11, 3, 11);

        side1 = new ModelRenderer(this, 0, 31);
        side1.addBox(-6.0F, 6.0F, -6.0F, 12, 3, 2);
        side2 = new ModelRenderer(this, 0, 31);
        side2.addBox(-6.0F, 6.0F, 4.0F, 12, 3, 2);
        side3 = new ModelRenderer(this, 28, 31);
        side3.addBox(-6.0F, 6.0F, -4.0F, 2, 3, 8);
        side4 = new ModelRenderer(this, 28, 31);
        side4.addBox(4.0F, 6.0F, -4.0F, 2, 3, 8);

        spout = new ModelRenderer(this, 0, 37);
        spout.addBox(-1.5F, 8.0F, -8.0F, 3, 3, 2);
    }

    public void renderAll() {
        base.render(0.0625F);
        ring.render(0.0625F);
        side1.render(0.0625F);
        side2.render(0.0625F);
        side3.render(0.0625F);
        side4.render(0.0625F);
        spout.render(0.0625F);
    }
}
