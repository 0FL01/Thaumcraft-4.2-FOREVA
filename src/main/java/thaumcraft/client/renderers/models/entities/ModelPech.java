package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.monster.EntityPech;

public class ModelPech extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer head;
    private final ModelRenderer jowls;
    private final ModelRenderer lowerPack;
    private final ModelRenderer upperPack;
    public final ModelRenderer rightArm;
    private final ModelRenderer leftArm;

    public ModelPech() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this, 34, 12);
        this.body.addBox(-3.0F, 0.0F, 0.0F, 6, 10, 6);
        this.body.setRotationPoint(0.0F, 9.0F, -3.0F);
        setRotation(this.body, 0.3129957F, 0.0F, 0.0F);

        this.rightLeg = new ModelRenderer(this, 35, 1);
        this.rightLeg.mirror = true;
        this.rightLeg.addBox(-2.9F, 0.0F, 0.0F, 3, 6, 3);
        this.rightLeg.setRotationPoint(0.0F, 18.0F, 0.0F);
        this.rightLeg.mirror = false;

        this.leftLeg = new ModelRenderer(this, 35, 1);
        this.leftLeg.addBox(-0.1F, 0.0F, 0.0F, 3, 6, 3);
        this.leftLeg.setRotationPoint(0.0F, 18.0F, 0.0F);

        this.head = new ModelRenderer(this, 2, 11);
        this.head.addBox(-3.5F, -5.0F, -5.0F, 7, 5, 5);
        this.head.setRotationPoint(0.0F, 8.0F, 0.0F);

        this.jowls = new ModelRenderer(this, 1, 21);
        this.jowls.addBox(-4.0F, -1.0F, -6.0F, 8, 3, 5);
        this.jowls.setRotationPoint(0.0F, 8.0F, 0.0F);

        this.lowerPack = new ModelRenderer(this, 0, 0);
        this.lowerPack.addBox(-5.0F, 0.0F, 0.0F, 10, 5, 5);
        this.lowerPack.setRotationPoint(0.0F, 10.0F, 3.5F);
        setRotation(this.lowerPack, 0.3013602F, 0.0F, 0.0F);

        this.upperPack = new ModelRenderer(this, 64, 1);
        this.upperPack.addBox(-7.5F, -14.0F, 0.0F, 15, 14, 11);
        this.upperPack.setRotationPoint(0.0F, 10.0F, 3.0F);
        setRotation(this.upperPack, 0.4537856F, 0.0F, 0.0F);

        this.rightArm = new ModelRenderer(this, 52, 2);
        this.rightArm.mirror = true;
        this.rightArm.addBox(-2.0F, 0.0F, -1.0F, 2, 6, 2);
        this.rightArm.setRotationPoint(-3.0F, 10.0F, -1.0F);
        this.rightArm.mirror = false;

        this.leftArm = new ModelRenderer(this, 52, 2);
        this.leftArm.addBox(0.0F, 0.0F, -1.0F, 2, 6, 2);
        this.leftArm.setRotationPoint(3.0F, 10.0F, -1.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.body.render(scale);
        this.rightLeg.render(scale);
        this.leftLeg.render(scale);
        this.head.render(scale);
        this.jowls.render(scale);
        this.lowerPack.render(scale);
        this.upperPack.render(scale);
        this.rightArm.render(scale);
        this.leftArm.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
        float mumble = entity instanceof EntityPech ? ((EntityPech) entity).mumble : 0.0F;
        this.jowls.rotateAngleY = this.head.rotateAngleY;
        this.jowls.rotateAngleX = this.head.rotateAngleX
                + (0.2617994F + MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.25F)
                + 0.34906587F * Math.abs(MathHelper.sin(mumble));
        this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.rotateAngleZ = 0.0F;
        this.leftArm.rotateAngleZ = 0.0F;
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;
        this.lowerPack.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.125F;
        this.lowerPack.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.125F;

        if (this.isRiding) {
            this.rightArm.rotateAngleX += -0.62831855F;
            this.leftArm.rotateAngleX += -0.62831855F;
            this.rightLeg.rotateAngleX = -1.2566371F;
            this.leftLeg.rotateAngleX = -1.2566371F;
            this.rightLeg.rotateAngleY = 0.31415927F;
            this.leftLeg.rotateAngleY = -0.31415927F;
        }

        this.rightArm.rotateAngleY = 0.0F;
        this.leftArm.rotateAngleY = 0.0F;
        if (this.swingProgress > -9990.0F) {
            float swing = this.swingProgress;
            this.rightArm.rotateAngleY += this.body.rotateAngleY;
            this.leftArm.rotateAngleY += this.body.rotateAngleY;
            this.leftArm.rotateAngleX += this.body.rotateAngleY;
            swing = 1.0F - this.swingProgress;
            swing *= swing;
            swing *= swing;
            swing = 1.0F - swing;
            float sinSwing = MathHelper.sin(swing * (float) Math.PI);
            float headOffset = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
            this.rightArm.rotateAngleX = (float) ((double) this.rightArm.rotateAngleX - ((double) sinSwing * 1.2D + (double) headOffset));
            this.rightArm.rotateAngleY += this.body.rotateAngleY * 2.0F;
            this.rightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
        }

        if (entity.isSneaking()) {
            this.rightArm.rotateAngleX += 0.4F;
            this.leftArm.rotateAngleX += 0.4F;
        }

        this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }
}
