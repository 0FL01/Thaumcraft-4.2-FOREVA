package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelHoverHarness
extends ModelBiped {

    private ModelRenderer BackMount;
    private ModelRenderer EngineCore;
    private ModelRenderer EngineNozzle;
    private ModelRenderer PipeL;
    private ModelRenderer PipeR;

    public ModelHoverHarness() {
        super();
        this.textureWidth = 128;
        this.textureHeight = 64;

        // Body harness — replaces the default bipedBody with the hover harness plate
        // Matches original: (-4, 0, -2) dimensions (8, 12, 4) scale 0.6
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, 0.6f);
        // Ensure default bipedBody rotation point
        this.bipedBody.rotationPointX = 0.0f;
        this.bipedBody.rotationPointY = 0.0f;
        this.bipedBody.rotationPointZ = 0.0f;

        // Back engine assembly — approximated from original OBJ model
        // The original model spans x: -2.25..2.25, y: -7.5..2.33, z: 1.05..10.95
        // All coordinates are relative to the model center (player chest)

        // Mounting plate — flat panel on the back
        this.BackMount = new ModelRenderer(this, 0, 0);
        this.BackMount.addBox(-3.0f, -4.0f, 2.0f, 6, 8, 1);
        this.BackMount.rotationPointX = 0.0f;
        this.BackMount.rotationPointY = 0.0f;
        this.BackMount.rotationPointZ = 0.0f;

        // Engine core — central cylinder/tube (main body of the engine)
        this.EngineCore = new ModelRenderer(this, 0, 9);
        this.EngineCore.addBox(-1.5f, -3.0f, 3.0f, 3, 6, 5);
        this.EngineCore.rotationPointX = 0.0f;
        this.EngineCore.rotationPointY = 0.0f;
        this.EngineCore.rotationPointZ = 0.0f;

        // Engine nozzle — the flared end at the back
        this.EngineNozzle = new ModelRenderer(this, 0, 20);
        this.EngineNozzle.addBox(-2.5f, -4.0f, 8.0f, 5, 8, 3);
        this.EngineNozzle.rotationPointX = 0.0f;
        this.EngineNozzle.rotationPointY = 0.0f;
        this.EngineNozzle.rotationPointZ = 0.0f;

        // Side pipes — tubes connecting the body harness to the back engine
        this.PipeL = new ModelRenderer(this, 16, 0);
        this.PipeL.addBox(-3.5f, -2.0f, 2.5f, 1, 4, 5);
        this.PipeL.rotationPointX = 0.0f;
        this.PipeL.rotationPointY = 0.0f;
        this.PipeL.rotationPointZ = 0.0f;

        this.PipeR = new ModelRenderer(this, 28, 0);
        this.PipeR.addBox(2.5f, -2.0f, 2.5f, 1, 4, 5);
        this.PipeR.rotationPointX = 0.0f;
        this.PipeR.rotationPointY = 0.0f;
        this.PipeR.rotationPointZ = 0.0f;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        // Set rotation angles for animation (inherits from ModelBiped)
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        // Sneaking rotation applied to body parts (matching original behavior)
        boolean isSneaking = entity != null && entity.isSneaking();

        // Render standard biped parts based on showModel
        if (this.bipedBody.showModel) {
            if (isSneaking) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(28.64789f, 1.0f, 0.0f, 0.0f);
            }
            this.bipedBody.render(scale);
            if (isSneaking) {
                GlStateManager.popMatrix();
            }
        }

        if (this.bipedHead.showModel) {
            this.bipedHead.render(scale);
        }
        if (this.bipedHeadwear.showModel) {
            this.bipedHeadwear.render(scale);
        }
        if (this.bipedRightArm.showModel) {
            this.bipedRightArm.render(scale);
        }
        if (this.bipedLeftArm.showModel) {
            this.bipedLeftArm.render(scale);
        }
        if (this.bipedRightLeg.showModel) {
            this.bipedRightLeg.render(scale);
        }
        if (this.bipedLeftLeg.showModel) {
            this.bipedLeftLeg.render(scale);
        }

        // Render back engine assembly (only shown when body is visible, i.e. chest/legs slot)
        if (this.bipedBody.showModel) {
            GlStateManager.pushMatrix();
            if (isSneaking) {
                GlStateManager.rotate(28.64789f, 1.0f, 0.0f, 0.0f);
            }
            this.BackMount.render(scale);
            this.EngineCore.render(scale);
            this.EngineNozzle.render(scale);
            this.PipeL.render(scale);
            this.PipeR.render(scale);
            GlStateManager.popMatrix();
        }
    }
}
