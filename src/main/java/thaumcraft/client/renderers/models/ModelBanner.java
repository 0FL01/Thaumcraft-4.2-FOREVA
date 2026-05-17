package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelBanner extends ModelBase {
    private final ModelRenderer tabLeft;
    private final ModelRenderer tabRight;
    private final ModelRenderer beam;
    public final ModelRenderer banner;
    private final ModelRenderer pole;

    public ModelBanner() {
        textureWidth = 128;
        textureHeight = 64;

        tabLeft = new ModelRenderer(this, 0, 29);
        tabLeft.addBox(-5.0F, -7.5F, -1.5F, 2, 3, 3);
        tabLeft.setTextureSize(128, 64);
        tabLeft.mirror = true;

        tabRight = new ModelRenderer(this, 0, 29);
        tabRight.addBox(3.0F, -7.5F, -1.5F, 2, 3, 3);
        tabRight.setTextureSize(128, 64);
        tabRight.mirror = true;

        beam = new ModelRenderer(this, 30, 0);
        beam.addBox(-7.0F, -7.0F, -1.0F, 14, 2, 2);
        beam.setTextureSize(128, 64);
        beam.mirror = true;

        banner = new ModelRenderer(this, 0, 0);
        banner.addBox(-7.0F, 0.0F, -0.5F, 14, 28, 1);
        banner.setRotationPoint(0.0F, -5.0F, 0.0F);
        banner.setTextureSize(128, 64);
        banner.mirror = true;

        pole = new ModelRenderer(this, 62, 0);
        pole.addBox(0.0F, 0.0F, -1.0F, 2, 31, 2);
        pole.setRotationPoint(-1.0F, -7.0F, -2.0F);
        pole.setTextureSize(128, 64);
        pole.mirror = true;
    }

    public void renderPole() {
        pole.render(0.0625F);
    }

    public void renderBeam() {
        beam.render(0.0625F);
    }

    public void renderTabs() {
        tabLeft.render(0.0625F);
        tabRight.render(0.0625F);
    }

    public void renderBanner() {
        banner.render(0.0625F);
    }
}
