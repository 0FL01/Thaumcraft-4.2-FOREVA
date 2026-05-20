package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;

public class ModelTrunk extends ModelBase {
    public final ModelRenderer chestLid;
    public final ModelRenderer chestBelow;
    public final ModelRenderer chestKnob;

    public ModelTrunk() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.chestLid = new ModelRenderer(this, 0, 0);
        this.chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7.0F;
        this.chestLid.rotationPointZ = 15.0F;

        this.chestKnob = new ModelRenderer(this, 0, 0);
        this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1);
        this.chestKnob.rotationPointX = 8.0F;
        this.chestKnob.rotationPointY = 7.0F;
        this.chestKnob.rotationPointZ = 15.0F;

        this.chestBelow = new ModelRenderer(this, 0, 19);
        this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14);
        this.chestBelow.rotationPointX = 1.0F;
        this.chestBelow.rotationPointY = 6.0F;
        this.chestBelow.rotationPointZ = 1.0F;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
        this.chestLid.render(scale);
        this.chestBelow.render(scale);
        this.chestKnob.render(scale);
        if (entity instanceof EntityTravelingTrunk) {
            this.renderUpgradeIcon((EntityTravelingTrunk) entity, scale);
        }
    }

    private void renderUpgradeIcon(EntityTravelingTrunk trunk, float scale) {
        int upgrade = trunk.getUpgrade();
        if (upgrade < 0) {
            return;
        }
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .getParticleIcon(ConfigItems.itemGolemUpgrade, upgrade);
        if (sprite == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.chestKnob.rotationPointX * scale, this.chestKnob.rotationPointY * scale,
                this.chestKnob.rotationPointZ * scale);
        if (this.chestKnob.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(this.chestKnob.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
        }
        if (this.chestKnob.rotateAngleY != 0.0F) {
            GlStateManager.rotate(this.chestKnob.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
        }
        if (this.chestKnob.rotateAngleX != 0.0F) {
            GlStateManager.rotate(this.chestKnob.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.translate(-0.075F, -0.115F, -0.94301F);
        GlStateManager.scale(0.15F, 0.15F, 0.15F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        buffer.pos(0.0D, 0.0D, 0.0D).tex(minU, maxV).endVertex();
        buffer.pos(1.0D, 0.0D, 0.0D).tex(maxU, maxV).endVertex();
        buffer.pos(1.0D, 1.0D, 0.0D).tex(maxU, minV).endVertex();
        buffer.pos(0.0D, 1.0D, 0.0D).tex(minU, minV).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
