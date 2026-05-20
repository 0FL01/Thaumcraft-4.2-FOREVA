package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelTaintSpore extends ModelBase {

    private final ModelRenderer cube;

    public ModelTaintSpore() {
        textureWidth = 64;
        textureHeight = 64;
        cube = new ModelRenderer(this, 0, 0);
        cube.addBox(-6.0F, 2.0F, -6.0F, 12, 12, 12);
        cube.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16);
        cube.setRotationPoint(0.0F, 24.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        cube.render(scale);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float intensity = 0.02F;
        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).hurtTime > 0) {
            intensity = 0.04F;
        }
        cube.rotateAngleX = intensity * MathHelper.sin(ageInTicks * 0.05F);
        cube.rotateAngleZ = intensity * MathHelper.sin(ageInTicks * 0.1F);
    }
}
