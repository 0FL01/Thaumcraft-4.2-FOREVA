package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelAlembic extends ModelBase {
    private final ModelRenderer pot;
    private final ModelRenderer panel;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;
    private final ModelRenderer tubeMain;
    private final ModelRenderer tubeSmall;

    public ModelAlembic() {
        textureWidth = 64;
        textureHeight = 64;

        pot = new ModelRenderer(this, 0, 0);
        pot.addBox(-4.0F, 2.0F, -4.0F, 8, 8, 8);

        panel = new ModelRenderer(this, 0, 16);
        panel.addBox(-2.0F, 5.0F, -4.8F, 4, 3, 1);

        leg1 = new ModelRenderer(this, 32, 0);
        leg1.addBox(-5.0F, 10.0F, -5.0F, 2, 4, 2);
        leg2 = new ModelRenderer(this, 32, 0);
        leg2.addBox(3.0F, 10.0F, -5.0F, 2, 4, 2);
        leg3 = new ModelRenderer(this, 32, 0);
        leg3.addBox(-5.0F, 10.0F, 3.0F, 2, 4, 2);
        leg4 = new ModelRenderer(this, 32, 0);
        leg4.addBox(3.0F, 10.0F, 3.0F, 2, 4, 2);

        tubeMain = new ModelRenderer(this, 20, 16);
        tubeMain.addBox(-1.0F, -1.0F, 3.0F, 2, 5, 2);

        tubeSmall = new ModelRenderer(this, 28, 16);
        tubeSmall.addBox(-1.0F, -3.0F, 4.5F, 2, 2, 2);
    }

    public void renderLegs() {
        leg1.render(0.0625F);
        leg2.render(0.0625F);
        leg3.render(0.0625F);
        leg4.render(0.0625F);
    }

    public void renderTubeMain() {
        tubeMain.render(0.0625F);
    }

    public void renderTubeSmall() {
        tubeSmall.render(0.0625F);
    }

    public void renderPot() {
        pot.render(0.0625F);
    }

    public void renderPanel() {
        panel.render(0.0625F);
    }
}
