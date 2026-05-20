package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;

public class ModelTaintSporeSwarmer extends ModelBase {

    private final ModelRenderer cube;
    private final ModelRenderer outerCube;

    public ModelTaintSporeSwarmer() {
        textureWidth = 64;
        textureHeight = 64;

        cube = new ModelRenderer(this, 0, 0);
        cube.addBox(-8.0F, 0.0F, -8.0F, 16, 16, 16);
        cube.setRotationPoint(0.0F, 0.0F, 0.0F);

        outerCube = new ModelRenderer(this, 0, 32);
        outerCube.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        outerCube.setRotationPoint(0.0F, 16.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        EntityTaintSporeSwarmer spore = (EntityTaintSporeSwarmer) entity;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPushMatrix();
        float display = spore.displaySize;
        float scaleBase = -0.07F;
        float pulse = 0.025F * MathHelper.sin(spore.ticksExisted * 0.075F);
        GL11.glTranslatef(0.0F, 1.6F, 0.0F);
        GL11.glScalef(scaleBase * display - pulse, scaleBase * display + pulse, scaleBase * display - pulse);
        GL11.glTranslatef(0.0F, -(scaleBase * display + pulse) / 2.0F, 0.0F);
        int fullBright = 0xF000F0;
        int lightU = fullBright % 65536;
        int lightV = fullBright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);
        cube.render(scale);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        int packedLight = spore.getBrightnessForRender();
        int packedU = packedLight % 65536;
        int packedV = packedLight / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, packedU, packedV);
        outerCube.render(scale);
        GL11.glPopMatrix();

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
