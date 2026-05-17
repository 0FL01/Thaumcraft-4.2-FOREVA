package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelNodeStabilizer extends ModelBase {
    private final ModelRenderer lockFrame;
    private final ModelRenderer lockCore;
    private final ModelRenderer pistonBody;
    private final ModelRenderer pistonTip;

    public ModelNodeStabilizer() {
        textureWidth = 64;
        textureHeight = 32;

        lockFrame = new ModelRenderer(this, 0, 0);
        lockFrame.addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8);

        lockCore = new ModelRenderer(this, 0, 10);
        lockCore.addBox(-1.5F, -2.5F, -1.5F, 3, 5, 3);

        pistonBody = new ModelRenderer(this, 24, 0);
        pistonBody.addBox(-1.0F, -1.0F, -1.0F, 2, 5, 2);

        pistonTip = new ModelRenderer(this, 32, 0);
        pistonTip.addBox(-1.5F, 3.0F, -1.5F, 3, 2, 3);
    }

    public void renderLock(float scale) {
        lockFrame.render(scale);
        lockCore.render(scale);
    }

    public void renderPiston(float scale) {
        pistonBody.render(scale);
        pistonTip.render(scale);
    }
}
