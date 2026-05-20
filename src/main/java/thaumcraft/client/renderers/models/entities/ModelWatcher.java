package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.entities.monster.EntityWatcher;

public class ModelWatcher extends ModelBase {

    private final ModelRenderer watcherBody;
    private final ModelRenderer watcherEye;
    private final ModelRenderer[] watcherSpines;
    private final ModelRenderer[] watcherTail;

    public ModelWatcher() {
        textureWidth = 64;
        textureHeight = 64;
        watcherSpines = new ModelRenderer[12];
        watcherBody = new ModelRenderer(this);
        watcherBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
        watcherBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
        watcherBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
        watcherBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
        watcherBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

        for (int i = 0; i < watcherSpines.length; ++i) {
            watcherSpines[i] = new ModelRenderer(this, 0, 0);
            watcherSpines[i].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
            watcherBody.addChild(watcherSpines[i]);
        }

        watcherEye = new ModelRenderer(this, 8, 0);
        watcherEye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
        watcherBody.addChild(watcherEye);

        watcherTail = new ModelRenderer[3];
        watcherTail[0] = new ModelRenderer(this, 40, 0);
        watcherTail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
        watcherTail[1] = new ModelRenderer(this, 0, 54);
        watcherTail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
        watcherTail[2] = new ModelRenderer(this);
        watcherTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
        watcherTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
        watcherBody.addChild(watcherTail[0]);
        watcherTail[0].addChild(watcherTail[1]);
        watcherTail[1].addChild(watcherTail[2]);
    }

    public int getEyeTextureOffset() {
        return 54;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        watcherBody.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        EntityWatcher watcher = (EntityWatcher) entityIn;
        float partialTicks = ageInTicks - watcher.ticksExisted;
        watcherBody.rotateAngleY = netHeadYaw * 0.017453292F;
        watcherBody.rotateAngleX = headPitch * 0.017453292F;

        float[] spinePitch = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
        float[] spineYaw = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
        float[] spineRoll = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
        float[] spineX = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
        float[] spineY = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
        float[] spineZ = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
        float spineOffset = (1.0F - watcher.getFinAngle(partialTicks)) * 0.55F;

        for (int i = 0; i < 12; ++i) {
            watcherSpines[i].rotateAngleX = (float) Math.PI * spinePitch[i];
            watcherSpines[i].rotateAngleY = (float) Math.PI * spineYaw[i];
            watcherSpines[i].rotateAngleZ = (float) Math.PI * spineRoll[i];
            watcherSpines[i].rotationPointX = spineX[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + i) * 0.01F - spineOffset);
            watcherSpines[i].rotationPointY = 16.0F + spineY[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + i) * 0.01F - spineOffset);
            watcherSpines[i].rotationPointZ = spineZ[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + i) * 0.01F - spineOffset);
        }

        watcherEye.rotationPointZ = -8.25F;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        if (watcher.hasTargetedEntity()) {
            viewer = watcher.getTargetedEntity();
        }

        if (viewer != null) {
            Vec3d viewerEyes = viewer.getPositionEyes(0.0F);
            Vec3d watcherEyes = entityIn.getPositionEyes(0.0F);
            watcherEye.rotationPointY = viewerEyes.y > watcherEyes.y ? 0.0F : 1.0F;
            Vec3d look = entityIn.getLook(0.0F);
            look = new Vec3d(look.x, 0.0D, look.z);
            Vec3d lateral = new Vec3d(watcherEyes.x - viewerEyes.x, 0.0D, watcherEyes.z - viewerEyes.z).normalize().rotateYaw((float) Math.PI / 2F);
            double dot = look.dotProduct(lateral);
            watcherEye.rotationPointX = MathHelper.sqrt((float) Math.abs(dot)) * 2.0F * (float) Math.signum(dot);
        }

        watcherEye.showModel = true;
        float tail = watcher.getTailAngle(partialTicks);
        watcherTail[0].rotateAngleY = MathHelper.sin(tail) * (float) Math.PI * 0.05F;
        watcherTail[1].rotateAngleY = MathHelper.sin(tail) * (float) Math.PI * 0.1F;
        watcherTail[1].rotationPointX = -1.5F;
        watcherTail[1].rotationPointY = 0.5F;
        watcherTail[1].rotationPointZ = 14.0F;
        watcherTail[2].rotateAngleY = MathHelper.sin(tail) * (float) Math.PI * 0.15F;
        watcherTail[2].rotationPointX = 0.5F;
        watcherTail[2].rotationPointY = 0.5F;
        watcherTail[2].rotationPointZ = 6.0F;
    }
}
