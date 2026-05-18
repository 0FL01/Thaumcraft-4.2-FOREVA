package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTable extends ModelBase {
    private final ModelRenderer top;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer crossbar;

    public ModelTable() {
        textureWidth = 64;
        textureHeight = 32;

        top = new ModelRenderer(this, 0, 0);
        top.addBox(0.0F, 0.0F, 0.0F, 16, 4, 16);
        top.setRotationPoint(-8.0F, 0.0F, -8.0F);
        top.setTextureSize(64, 32);
        top.mirror = true;

        leg1 = new ModelRenderer(this, 0, 20);
        leg1.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4);
        leg1.setRotationPoint(2.0F, 4.0F, -2.0F);
        leg1.setTextureSize(64, 32);
        leg1.mirror = true;

        leg2 = new ModelRenderer(this, 0, 20);
        leg2.addBox(0.0F, 0.0F, 0.0F, 4, 8, 4);
        leg2.setRotationPoint(-6.0F, 4.0F, -2.0F);
        leg2.setTextureSize(64, 32);
        leg2.mirror = true;

        crossbar = new ModelRenderer(this, 16, 20);
        crossbar.addBox(0.0F, 0.0F, 0.0F, 16, 4, 8);
        crossbar.setRotationPoint(-8.0F, 12.0F, -4.0F);
        crossbar.setTextureSize(64, 32);
        crossbar.mirror = true;
    }

    public void renderAll(float scale) {
        top.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        crossbar.render(scale);
    }
}
