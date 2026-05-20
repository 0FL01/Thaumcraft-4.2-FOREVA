package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityEldritchCrab;

public class ModelEldritchCrab extends ModelBase {

    private final ModelRenderer tailHelm;
    private final ModelRenderer tailBare;
    private final ModelRenderer rightFrontLegTop;
    private final ModelRenderer rightClawMid;
    private final ModelRenderer headTip;
    private final ModelRenderer rightClawBase;
    private final ModelRenderer rightClawEnd;
    private final ModelRenderer leftClawEnd;
    private final ModelRenderer leftClawMid;
    private final ModelRenderer rightArm;
    private final ModelRenderer torso;
    private final ModelRenderer rightRearLegTop;
    private final ModelRenderer headBase;
    private final ModelRenderer leftRearLegTop;
    private final ModelRenderer leftFrontLegTop;
    private final ModelRenderer rightRearLegBase;
    private final ModelRenderer rightFrontLegBase;
    private final ModelRenderer leftFrontLegBase;
    private final ModelRenderer leftRearLegBase;
    private final ModelRenderer leftClawBase;
    private final ModelRenderer leftArm;

    public ModelEldritchCrab() {
        textureWidth = 128;
        textureHeight = 64;

        tailHelm = new ModelRenderer(this, 0, 0);
        tailHelm.addBox(-4.5F, -4.5F, -0.4F, 9, 9, 9);
        tailHelm.setRotationPoint(0.0F, 18.0F, 0.0F);
        setRotation(tailHelm, 0.1047198F, 0.0F, 0.0F);

        tailBare = new ModelRenderer(this, 64, 0);
        tailBare.addBox(-4.0F, -4.0F, -0.4F, 8, 8, 8);
        tailBare.setRotationPoint(0.0F, 18.0F, 0.0F);
        setRotation(tailBare, 0.1047198F, 0.0F, 0.0F);

        rightClawMid = new ModelRenderer(this, 0, 47);
        rightClawMid.addBox(-2.0F, -1.0F, -5.066667F, 4, 3, 5);
        rightClawMid.setRotationPoint(-6.0F, 15.5F, -10.0F);

        headTip = new ModelRenderer(this, 0, 38);
        headTip.addBox(-2.0F, -1.5F, -9.066667F, 4, 4, 1);
        headTip.setRotationPoint(0.0F, 18.0F, 0.0F);

        rightClawBase = new ModelRenderer(this, 0, 55);
        rightClawBase.addBox(-2.0F, -2.5F, -3.066667F, 4, 5, 3);
        rightClawBase.setRotationPoint(-6.0F, 17.0F, -7.0F);

        rightClawEnd = new ModelRenderer(this, 14, 54);
        rightClawEnd.addBox(-1.5F, -1.0F, -4.066667F, 3, 2, 5);
        rightClawEnd.setRotationPoint(-6.0F, 18.5F, -10.0F);
        setRotation(rightClawEnd, 0.3141593F, 0.0F, 0.0F);

        rightArm = new ModelRenderer(this, 44, 4);
        rightArm.addBox(-1.0F, -1.0F, -5.066667F, 2, 2, 6);
        rightArm.setRotationPoint(-3.0F, 17.0F, -4.0F);
        setRotation(rightArm, 0.0F, 0.7504916F, 0.0F);

        leftClawEnd = new ModelRenderer(this, 14, 54);
        leftClawEnd.addBox(-1.5F, -1.0F, -4.066667F, 3, 2, 5);
        leftClawEnd.setRotationPoint(6.0F, 18.5F, -10.0F);
        setRotation(leftClawEnd, 0.3141593F, 0.0F, 0.0F);

        leftClawMid = new ModelRenderer(this, 0, 47);
        leftClawMid.mirror = true;
        leftClawMid.addBox(-2.0F, -1.0F, -5.066667F, 4, 3, 5);
        leftClawMid.setRotationPoint(6.0F, 15.5F, -10.0F);

        leftClawBase = new ModelRenderer(this, 0, 55);
        leftClawBase.mirror = true;
        leftClawBase.addBox(-2.0F, -2.5F, -3.066667F, 4, 5, 3);
        leftClawBase.setRotationPoint(6.0F, 17.0F, -7.0F);

        leftArm = new ModelRenderer(this, 44, 4);
        leftArm.addBox(-1.0F, -1.0F, -4.066667F, 2, 2, 6);
        leftArm.setRotationPoint(4.0F, 17.0F, -5.0F);
        setRotation(leftArm, 0.0F, -0.7504916F, 0.0F);

        torso = new ModelRenderer(this, 0, 18);
        torso.addBox(-3.5F, -3.5F, -6.066667F, 7, 7, 6);
        torso.setRotationPoint(0.0F, 18.0F, 0.0F);
        setRotation(torso, 0.0523599F, 0.0F, 0.0F);

        headBase = new ModelRenderer(this, 0, 31);
        headBase.addBox(-2.5F, -2.0F, -8.066667F, 5, 5, 2);
        headBase.setRotationPoint(0.0F, 18.0F, 0.0F);

        rightRearLegTop = new ModelRenderer(this, 36, 4);
        rightRearLegTop.addBox(-4.5F, 1.0F, -0.9F, 2, 5, 2);
        rightRearLegTop.setRotationPoint(-4.0F, 20.0F, -1.5F);

        rightFrontLegTop = new ModelRenderer(this, 36, 4);
        rightFrontLegTop.addBox(-5.0F, 1.0F, -1.066667F, 2, 5, 2);
        rightFrontLegTop.setRotationPoint(-4.0F, 20.0F, -3.5F);

        leftRearLegTop = new ModelRenderer(this, 36, 4);
        leftRearLegTop.addBox(2.5F, 1.0F, -0.9F, 2, 5, 2);
        leftRearLegTop.setRotationPoint(4.0F, 20.0F, -1.5F);

        leftFrontLegTop = new ModelRenderer(this, 36, 4);
        leftFrontLegTop.addBox(3.0F, 1.0F, -1.066667F, 2, 5, 2);
        leftFrontLegTop.setRotationPoint(4.0F, 20.0F, -3.5F);

        rightRearLegBase = new ModelRenderer(this, 36, 0);
        rightRearLegBase.addBox(-4.5F, -1.0F, -0.9F, 6, 2, 2);
        rightRearLegBase.setRotationPoint(-4.0F, 20.0F, -1.5F);

        rightFrontLegBase = new ModelRenderer(this, 36, 0);
        rightFrontLegBase.addBox(-5.0F, -1.0F, -1.066667F, 6, 2, 2);
        rightFrontLegBase.setRotationPoint(-4.0F, 20.0F, -3.5F);

        leftFrontLegBase = new ModelRenderer(this, 36, 0);
        leftFrontLegBase.addBox(-1.0F, -1.0F, -1.066667F, 6, 2, 2);
        leftFrontLegBase.setRotationPoint(4.0F, 20.0F, -3.5F);

        leftRearLegBase = new ModelRenderer(this, 36, 0);
        leftRearLegBase.addBox(-1.5F, -1.0F, -0.9F, 6, 2, 2);
        leftRearLegBase.setRotationPoint(4.0F, 20.0F, -1.5F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        if (entity instanceof EntityEldritchCrab && ((EntityEldritchCrab) entity).hasHelm()) {
            tailHelm.render(scale);
        } else {
            tailBare.render(scale);
        }
        rightFrontLegTop.render(scale);
        rightClawMid.render(scale);
        headTip.render(scale);
        rightClawBase.render(scale);
        rightClawEnd.render(scale);
        leftClawEnd.render(scale);
        leftClawMid.render(scale);
        rightArm.render(scale);
        torso.render(scale);
        rightRearLegTop.render(scale);
        headBase.render(scale);
        leftRearLegTop.render(scale);
        leftFrontLegTop.render(scale);
        rightRearLegBase.render(scale);
        rightFrontLegBase.render(scale);
        leftFrontLegBase.render(scale);
        leftRearLegBase.render(scale);
        leftClawBase.render(scale);
        leftArm.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        setRotation(rightRearLegTop, 0.0F, 0.2094395F, 0.4363323F);
        setRotation(rightFrontLegTop, 0.0F, -0.2094395F, 0.4363323F);
        setRotation(leftRearLegTop, 0.0F, -0.2094395F, -0.4363323F);
        setRotation(leftFrontLegTop, 0.0F, 0.2094395F, -0.4363323F);
        setRotation(rightRearLegBase, 0.0F, 0.2094395F, 0.4363323F);
        setRotation(rightFrontLegBase, 0.0F, -0.2094395F, 0.4363323F);
        setRotation(leftFrontLegBase, 0.0F, 0.2094395F, -0.4363323F);
        setRotation(leftRearLegBase, 0.0F, -0.2094395F, -0.4363323F);

        float rearSwing = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F) * 0.4F) * limbSwingAmount;
        float frontSwing = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;

        rightRearLegTop.rotateAngleY += rearSwing;
        rightRearLegBase.rotateAngleY += rearSwing;
        leftRearLegTop.rotateAngleY += -rearSwing;
        leftRearLegBase.rotateAngleY += -rearSwing;
        rightFrontLegTop.rotateAngleY += frontSwing;
        rightFrontLegBase.rotateAngleY += frontSwing;
        leftFrontLegTop.rotateAngleY += -frontSwing;
        leftFrontLegBase.rotateAngleY += -frontSwing;

        rightRearLegTop.rotateAngleZ += rearSwing;
        rightRearLegBase.rotateAngleZ += rearSwing;
        leftRearLegTop.rotateAngleZ += -rearSwing;
        leftRearLegBase.rotateAngleZ += -rearSwing;
        rightFrontLegTop.rotateAngleZ += frontSwing;
        rightFrontLegBase.rotateAngleZ += frontSwing;
        leftFrontLegTop.rotateAngleZ += -frontSwing;
        leftFrontLegBase.rotateAngleZ += -frontSwing;

        float tailYaw = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.125F;
        float tailRoll = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.125F;
        tailBare.rotateAngleY = tailHelm.rotateAngleY = tailYaw;
        tailBare.rotateAngleZ = tailHelm.rotateAngleZ = tailRoll;

        rightClawEnd.rotateAngleX = 0.3141593F - MathHelper.sin(entityIn.ticksExisted / 4.0F) * 0.25F;
        leftClawEnd.rotateAngleX = 0.3141593F + MathHelper.sin(entityIn.ticksExisted / 4.1F) * 0.25F;
        rightClawMid.rotateAngleX = MathHelper.sin(entityIn.ticksExisted / 4.0F) * 0.125F;
        leftClawMid.rotateAngleX = -MathHelper.sin(entityIn.ticksExisted / 4.1F) * 0.125F;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
