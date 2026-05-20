package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class ModelGolemAccessories extends ModelBase {
    public final ModelRenderer golemHeadFez;
    public final ModelRenderer golemHeadGlasses;
    public final ModelRenderer golemHeadHat;
    public final ModelRenderer golemHeadHatRim;
    public final ModelRenderer golemBowtie;
    public final ModelRenderer golemDartgun;
    public final ModelRenderer golemMace;
    public final ModelRenderer golemVisor;
    public final ModelRenderer golemPlate;
    public final ModelRenderer golemPlateLeft;
    public final ModelRenderer golemPlateRight;
    public final ModelRenderer golemHeadJar;
    public final ModelRenderer golemHeadBrain;
    public final ModelRenderer golemEvilHead;

    public ModelGolemAccessories() {
        this(0.0F);
    }

    public ModelGolemAccessories(float inflate) {
        this(inflate, -7.0F);
    }

    public ModelGolemAccessories(float inflate, float verticalOffset) {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.golemHeadFez = new ModelRenderer(this, 0, 94);
        this.golemHeadFez.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadFez.addBox(-4.5F, -15.0F, -6.0F, 9, 7, 9, inflate);

        this.golemPlate = new ModelRenderer(this, 32, 40);
        this.golemPlate.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemPlate.addBox(-6.5F, -1.0F, -7.0F, 13, 12, 13, inflate);

        this.golemPlateLeft = new ModelRenderer(this, 0, 44);
        this.golemPlateLeft.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemPlateLeft.addBox(-8.5F, -4.0F, -6.5F, 3, 6, 12, inflate);

        this.golemPlateRight = new ModelRenderer(this, 0, 44);
        this.golemPlateRight.mirror = true;
        this.golemPlateRight.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemPlateRight.addBox(5.5F, -4.0F, -6.5F, 3, 6, 12, inflate);

        this.golemHeadHat = new ModelRenderer(this, 0, 110);
        this.golemHeadHat.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadHat.addBox(-4.5F, -17.0F, -6.0F, 9, 9, 9, inflate);

        this.golemHeadGlasses = new ModelRenderer(this, 0, 80);
        this.golemHeadGlasses.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadGlasses.addBox(-4.5F, -8.0F, -6.0F, 9, 4, 9, inflate);

        this.golemVisor = new ModelRenderer(this, 0, 70);
        this.golemVisor.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemVisor.addBox(-5.0F, -8.0F, -6.0F, 10, 5, 5, inflate);

        this.golemHeadHatRim = new ModelRenderer(this, 36, 114);
        this.golemHeadHatRim.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadHatRim.addBox(-6.5F, -9.0F, -8.0F, 13, 1, 13, 0.0F);

        this.golemDartgun = new ModelRenderer(this, 80, 80);
        this.golemDartgun.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemDartgun.addBox(7.9F, 7.5F, -3.5F, 6, 16, 7, inflate);

        this.golemMace = new ModelRenderer(this, 80, 26);
        this.golemMace.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemMace.addBox(-13.0F, 15.0F, -5.0F, 6, 8, 10, inflate);

        this.golemBowtie = new ModelRenderer(this, 0, 0);
        this.golemBowtie.setRotationPoint(0.0F, verticalOffset, 0.0F);
        this.golemBowtie.addBox(-8.5F, -2.0F, -6.5F, 17, 4, 12, inflate);

        this.golemHeadJar = new ModelRenderer(this, 96, 56);
        this.golemHeadJar.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadJar.addBox(-4.0F, -15.0F, -5.5F, 8, 4, 8, inflate);

        this.golemHeadBrain = new ModelRenderer(this, 96, 70);
        this.golemHeadBrain.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemHeadBrain.addBox(-3.5F, -14.0F, -5.0F, 7, 3, 7, inflate);

        this.golemEvilHead = new ModelRenderer(this, 64, 65);
        this.golemEvilHead.setRotationPoint(0.0F, verticalOffset, -2.0F);
        this.golemEvilHead.addBox(-4.0F, -9.0F, -5.5F, 8, 7, 8, inflate);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        if (!(entity instanceof EntityGolemBase)) {
            return;
        }
        EntityGolemBase golem = (EntityGolemBase) entity;
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        String decoration = golem.getGolemDecoration();
        if (decoration != null && decoration.contains("R")) {
            this.golemDartgun.render(scale);
        }
        GlStateManager.pushMatrix();
        if (decoration != null && decoration.contains("F")) {
            if (golem.advanced) {
                GlStateManager.translate(0.0F, -0.01F, 0.0F);
            }
            this.golemHeadFez.render(scale);
        }
        if (decoration != null && decoration.contains("H")) {
            if (golem.advanced) {
                GlStateManager.translate(0.0F, -0.01F, 0.0F);
            }
            this.golemHeadHat.render(scale);
            this.golemHeadHatRim.render(scale);
        }
        GlStateManager.popMatrix();
        if (decoration != null && decoration.contains("B")) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.golemBowtie.render(scale);
            GlStateManager.disableBlend();
        }
        if (decoration != null && decoration.contains("P")) {
            this.golemPlate.render(scale);
            this.golemPlateLeft.render(scale);
            this.golemPlateRight.render(scale);
        }
        if (decoration != null && decoration.contains("G")) {
            this.golemHeadGlasses.render(scale);
        }
        if (decoration != null && decoration.contains("V")) {
            this.golemVisor.render(scale);
        }
        if (decoration != null && decoration.contains("M")) {
            this.golemMace.render(scale);
        }
        if (golem.advanced) {
            this.golemHeadBrain.render(scale);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.golemHeadJar.render(scale);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            if (golem.getCore() >= 0) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.01F, 1.0F, 1.01F);
                this.golemEvilHead.render(scale);
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (!(entity instanceof EntityGolemBase)) {
            return;
        }
        EntityGolemBase golem = (EntityGolemBase) entity;
        if (golem.getCore() == -1 || golem.bootup < 0.0F) {
            this.golemHeadFez.rotateAngleY = 0.0F;
            this.golemHeadFez.rotateAngleX = 0.57595867F;
        } else if (golem.inactive) {
            this.golemHeadFez.rotateAngleY = 0.0F;
            this.golemHeadFez.rotateAngleX = 0.57595867F;
        } else if (golem.bootup > 0.0F) {
            this.golemHeadFez.rotateAngleY = 0.0F;
            this.golemHeadFez.rotateAngleX = golem.bootup / 57.295776F;
        } else {
            this.golemHeadFez.rotateAngleY = netHeadYaw / 57.295776F;
            this.golemHeadFez.rotateAngleX = headPitch / 57.295776F;
        }
        this.golemHeadGlasses.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemHeadGlasses.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemHeadJar.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemHeadJar.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemHeadBrain.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemHeadBrain.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemEvilHead.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemEvilHead.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemVisor.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemVisor.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemHeadHat.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemHeadHat.rotateAngleX = this.golemHeadFez.rotateAngleX;
        this.golemHeadHatRim.rotateAngleY = this.golemHeadFez.rotateAngleY;
        this.golemHeadHatRim.rotateAngleX = this.golemHeadFez.rotateAngleX;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
        if (!(entity instanceof EntityGolemBase)) {
            return;
        }
        EntityGolemBase golem = (EntityGolemBase) entity;
        int actionTimer = golem.getActionTimer();
        if (actionTimer > 0) {
            float angle = -2.0F + 1.5F * triangleWave((float) actionTimer - partialTickTime, 10.0F);
            this.golemDartgun.rotateAngleX = angle;
            this.golemMace.rotateAngleX = angle;
        } else if (golem.leftArm > 0 || golem.rightArm > 0) {
            if (golem.leftArm > 0) {
                this.golemDartgun.rotateAngleX = -2.0F + 1.5F * triangleWave((float) golem.leftArm - partialTickTime, 10.0F);
            }
            if (golem.rightArm > 0) {
                this.golemMace.rotateAngleX = -2.0F + 1.5F * triangleWave((float) golem.rightArm - partialTickTime, 10.0F);
            }
        } else if (!golem.getCarriedForDisplay().isEmpty()) {
            this.golemDartgun.rotateAngleX = -1.0F;
            this.golemMace.rotateAngleX = -1.0F;
        } else {
            this.golemDartgun.rotateAngleX = (-0.2F - 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.golemMace.rotateAngleX = (-0.2F + 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
        }
    }

    private static float triangleWave(float value, float period) {
        return (Math.abs(value % period - period * 0.5F) - period * 0.25F) / (period * 0.25F);
    }
}
