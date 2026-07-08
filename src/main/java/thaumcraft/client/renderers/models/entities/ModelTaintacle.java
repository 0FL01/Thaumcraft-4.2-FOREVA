package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.EntityTaintacle;

public class ModelTaintacle extends ModelBase {

    private final ModelRendererTaintacle tentacle;
    private final ModelRendererTaintacle[] tents;
    private final int length;

    public ModelTaintacle(int length) {
        this.length = length;
        textureWidth = 64;
        textureHeight = 64;

        tentacle = new ModelRendererTaintacle(this, 0, 0);
        tentacle.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        tentacle.rotationPointY = 12.0F;

        tents = new ModelRendererTaintacle[length];
        for (int i = 0; i < length - 1; i++) {
            tents[i] = new ModelRendererTaintacle(this, 0, 16);
            tents[i].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
            tents[i].rotationPointY = -8.0F;
            if (i == 0) {
                tentacle.addChild(tents[i]);
            } else {
                tents[i - 1].addChild(tents[i]);
            }
        }

        ModelRendererTaintacle orb = new ModelRendererTaintacle(this, 0, 56);
        orb.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4);
        orb.rotationPointY = -8.0F;
        tents[length - 2].addChild(orb);

        tents[length - 1] = new ModelRendererTaintacle(this, 0, 32);
        tents[length - 1].addBox(-6.0F, -6.0F, -6.0F, 12, 12, 12);
        tents[length - 1].rotationPointY = -8.0F;
        tents[length - 2].addChild(tents[length - 1]);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        boolean agitated = false;
        float flail = 0.0F;
        int hurtTime = 0;
        int attackTime = 0;
        if (entityIn instanceof EntityTaintacle) {
            EntityTaintacle tentacleEntity = (EntityTaintacle) entityIn;
            agitated = tentacleEntity.getAgitationState();
            flail = tentacleEntity.flailIntensity;
            hurtTime = tentacleEntity.hurtTime;
            attackTime = tentacleEntity.maxHurtTime;
        }

        float mod = scaleFactor * 0.2F;
        float flailSpeed = agitated ? 3.0F : 1.0F + (agitated ? mod : -mod);
        float flailIntensity = flail + (hurtTime > 0 || attackTime > 0 ? mod : -mod);
        tentacle.rotateAngleX = 0.0F;
        for (int i = 0; i < length - 1; i++) {
            tents[i].rotateAngleX = 0.15F * flailIntensity * MathHelper.sin(ageInTicks * 0.1F * flailSpeed - i / 2.0F);
            tents[i].rotateAngleZ = 0.1F / flailIntensity * MathHelper.sin(ageInTicks * 0.15F - i / 2.0F);
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float heightOffset = 0.0F;
        float growTicks = entity.height * 10.0F;
        if (entity.ticksExisted < growTicks) {
            heightOffset = (growTicks - entity.ticksExisted) / growTicks * entity.height;
        }
        GL11.glTranslatef(0.0F, (entity.height == 3.0F ? 0.6F : 1.2F) + heightOffset, 0.0F);
        GL11.glScalef(entity.height / 3.0F, entity.height / 3.0F, entity.height / 3.0F);
        tentacle.render(scale, 0.88F);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
