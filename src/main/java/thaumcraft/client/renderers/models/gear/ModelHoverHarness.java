package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelHoverHarness
extends ModelBiped {

    public ModelHoverHarness() {
        this(1.0f);
    }

    public ModelHoverHarness(float scale) {
        super(scale, 0.0f, 128, 64);
        this.textureWidth = 128;
        this.textureHeight = 64;

        // Body harness — replaces the default bipedBody with the hover harness plate
        // Matches original: (-4, 0, -2) dimensions (8, 12, 4) scale factor 0.6
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.6f);

        // Back engine assembly — approximated from original OBJ model
        // Added as children of bipedBody so they inherit all transformations
        // (sneaking rotation, armor Y-offset, etc.)

        // Mounting plate — flat panel on the back
        ModelRenderer backMount = new ModelRenderer(this, 0, 0);
        backMount.addBox(-3.0f, -4.0f, 2.0f, 6, 8, 1);
        this.bipedBody.addChild(backMount);

        // Engine core — central cylinder/tube (main body of the engine)
        ModelRenderer engineCore = new ModelRenderer(this, 0, 9);
        engineCore.addBox(-1.5f, -3.0f, 3.0f, 3, 6, 5);
        this.bipedBody.addChild(engineCore);

        // Engine nozzle — the flared end at the back
        ModelRenderer engineNozzle = new ModelRenderer(this, 0, 20);
        engineNozzle.addBox(-2.5f, -4.0f, 8.0f, 5, 8, 3);
        this.bipedBody.addChild(engineNozzle);

        // Side pipes — tubes connecting the body harness to the back engine
        ModelRenderer pipeL = new ModelRenderer(this, 16, 0);
        pipeL.addBox(-3.5f, -2.0f, 2.5f, 1, 4, 5);
        this.bipedBody.addChild(pipeL);

        ModelRenderer pipeR = new ModelRenderer(this, 28, 0);
        pipeR.addBox(2.5f, -2.0f, 2.5f, 1, 4, 5);
        this.bipedBody.addChild(pipeR);
    }

}
