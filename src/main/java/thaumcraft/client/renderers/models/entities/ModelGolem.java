package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class ModelGolem extends ModelBase {
    public final ModelRenderer golemHead;
    public final ModelRenderer golemBody;
    public final ModelRenderer golemRightArm;
    public final ModelRenderer golemLeftArm;
    public final ModelRenderer golemRightLeg;
    public final ModelRenderer golemLeftLeg;
    public int pass = 0;

    public ModelGolem(boolean itemVariant) {
        float inflate = 0.0F;
        float verticalOffset = itemVariant ? -5.0F : 30.0F;
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.golemHead = new ModelRenderer(this, 0, 0);
        this.golemHead.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHead.addBox(-4.0F, -11.0F, -5.5F, 8, 9, 8, inflate);

        this.golemBody = new ModelRenderer(this, 0, 40);
        this.golemBody.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemBody.addBox(-8.0F, -2.0F, -6.0F, 16, 12, 11, inflate);
        this.golemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, inflate + 0.5F);

        this.golemRightArm = new ModelRenderer(this, 60, 21);
        this.golemRightArm.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemRightArm.addBox(-12.0F, -2.5F, -3.0F, 4, 25, 6, inflate);

        this.golemLeftArm = new ModelRenderer(this, 60, 21);
        this.golemLeftArm.mirror = true;
        this.golemLeftArm.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemLeftArm.addBox(8.0F, -2.5F, -3.0F, 4, 25, 6, inflate);

        this.golemRightLeg = new ModelRenderer(this, 37, 0);
        this.golemRightLeg.setRotationPoint(-4.0F, 18.0F + verticalOffset, 0.0F);
        this.golemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, inflate);

        this.golemLeftLeg = new ModelRenderer(this, 37, 0);
        this.golemLeftLeg.mirror = true;
        this.golemLeftLeg.setRotationPoint(5.0F, 18.0F + verticalOffset, 0.0F);
        this.golemLeftLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, inflate);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GlStateManager.pushMatrix();
        if (this.pass == 2) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
        }
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        this.golemHead.render(scale);
        this.golemBody.render(scale);
        this.golemRightLeg.render(scale);
        this.golemLeftLeg.render(scale);
        this.golemRightArm.render(scale);
        this.golemLeftArm.render(scale);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        if (this.pass == 2) {
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.disableBlend();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float bootup = 0.0F;
        int core = 0;
        boolean inactive = false;
        if (entity instanceof EntityGolemBase) {
            EntityGolemBase golem = (EntityGolemBase) entity;
            core = golem.getCore();
            bootup = golem.bootup;
            inactive = golem.inactive;
            if (this.pass == 0 && golem.healing > 0) {
                float h1 = (float) golem.healing / 10.0F;
                float h2 = (float) golem.healing / 5.0F;
                GlStateManager.color(0.5F + h1, 0.9F + h2, 0.5F + h1, 1.0F);
            }
        }
        if (core == -1 || bootup < 0.0F) {
            this.golemHead.rotateAngleY = 0.0F;
            this.golemHead.rotateAngleX = 0.57595867F;
            this.golemRightLeg.rotateAngleX = 0.0F;
            this.golemLeftLeg.rotateAngleX = 0.0F;
            this.golemRightArm.rotateAngleX = 0.0F;
            this.golemLeftArm.rotateAngleX = 0.0F;
            this.golemRightLeg.rotateAngleY = 0.0F;
            this.golemLeftLeg.rotateAngleY = 0.0F;
            this.golemLeftArm.rotateAngleZ = 0.0F;
            this.golemRightArm.rotateAngleZ = 0.0F;
        } else {
            if (inactive) {
                this.golemHead.rotateAngleY = 0.0F;
                this.golemHead.rotateAngleX = 0.57595867F;
            } else if (bootup > 0.0F) {
                this.golemHead.rotateAngleY = 0.0F;
                this.golemHead.rotateAngleX = bootup / 57.295776F;
            } else {
                this.golemHead.rotateAngleY = netHeadYaw / 57.295776F;
                this.golemHead.rotateAngleX = headPitch / 57.295776F;
            }
            this.golemRightLeg.rotateAngleX = -1.5F * triangleWave(limbSwing, 13.0F) * limbSwingAmount;
            this.golemLeftLeg.rotateAngleX = 1.5F * triangleWave(limbSwing, 13.0F) * limbSwingAmount;
            this.golemRightLeg.rotateAngleY = 0.0F;
            this.golemLeftLeg.rotateAngleY = 0.0F;
            this.golemLeftArm.rotateAngleZ = 0.0F;
            this.golemRightArm.rotateAngleZ = 0.0F;
            if (entity instanceof EntityGolemBase && core == 6) {
                EntityGolemBase golem = (EntityGolemBase) entity;
                float swing = (1.0F - (0.5F + (float) Math.min(64, golem.getCarryLimit()) / 128.0F)) * 25.0F;
                this.golemLeftArm.rotateAngleZ = swing / 57.295776F;
                this.golemRightArm.rotateAngleZ = -swing / 57.295776F;
            }
        }
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        if (!(entity instanceof EntityGolemBase)) {
            return;
        }
        EntityGolemBase golem = (EntityGolemBase) entity;
        int actionTimer = golem.getActionTimer();
        boolean carrying = !golem.getCarriedForDisplay().isEmpty();
        boolean bucket = golem.getCore() == 5;
        int leftArm = golem.leftArm;
        int rightArm = golem.rightArm;

        if (actionTimer > 0) {
            float angle = -2.0F + 1.5F * triangleWave((float) actionTimer - partialTickTime, 5.0F);
            this.golemRightArm.rotateAngleX = angle;
            this.golemLeftArm.rotateAngleX = angle;
        } else if (leftArm > 0 || rightArm > 0) {
            if (leftArm > 0) {
                this.golemLeftArm.rotateAngleX = -2.0F + 1.5F * triangleWave((float) leftArm - partialTickTime, 20.0F);
            }
            if (rightArm > 0) {
                this.golemRightArm.rotateAngleX = -2.0F + 1.5F * triangleWave((float) rightArm - partialTickTime, 20.0F);
            }
        } else if (carrying || bucket) {
            this.golemRightArm.rotateAngleX = -1.0F;
            this.golemLeftArm.rotateAngleX = -1.0F;
        } else {
            this.golemRightArm.rotateAngleX = (-0.2F + 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.golemLeftArm.rotateAngleX = (-0.2F - 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
        }
    }

    private static float triangleWave(float value, float period) {
        return (Math.abs(value % period - period * 0.5F) - period * 0.25F) / (period * 0.25F);
    }
}
