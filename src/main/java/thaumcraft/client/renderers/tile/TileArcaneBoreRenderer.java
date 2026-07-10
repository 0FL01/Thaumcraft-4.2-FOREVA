package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelBore;
import thaumcraft.client.renderers.models.ModelBoreEmit;
import thaumcraft.client.renderers.models.ModelJar;
import thaumcraft.common.tiles.TileArcaneBore;

public class TileArcaneBoreRenderer extends TileEntitySpecialRenderer<TileArcaneBore> {

    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bore.png");
    private static final ResourceLocation JAR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/jar.png");
    private static final ResourceLocation VORTEX_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/vortex.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBore boreModel = new ModelBore();
    private final ModelBoreEmit emitModel = new ModelBoreEmit();
    private final ModelJar jarModel = new ModelJar();

    @Override
    public void render(TileArcaneBore tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = Minecraft.getMinecraft().player == null
                ? TileRenderHelper.ticks(tile, partialTicks)
                : Minecraft.getMinecraft().player.ticksExisted + partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate((float) tile.rotX - tile.vRadX + partialTicks * (float) tile.speedX, 0.0F, 1.0F, 0.0F);

        GlStateManager.pushMatrix();
        bindTexture(BORE_TEXTURE);
        if (tile.baseOrientation == EnumFacing.DOWN) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        boreModel.renderBase(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.rotate((float) tile.rotZ - tile.vRadZ + partialTicks * (float) tile.speedZ, 0.0F, 0.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        boreModel.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(tile.topRotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        emitModel.render(MODEL_SCALE, tile.hasFocus);
        GlStateManager.popMatrix();

        float rotation = ticks % 45.0F;
        renderVortexLayer(-0.17F, -(rotation * 8.0F), 10.0F, 0.40F, 0xFFFFFFFF);
        renderVortexLayer(-0.21F, rotation * 8.0F, 10.0F, 0.30F, 0xCCFFFFFF);
        renderVortexLayer(-0.25F, -(rotation * 8.0F), -10.0F, 0.20F, 0xCCFFFFFF);

        GlStateManager.pushMatrix();
        bindTexture(JAR_TEXTURE);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, 0.3F, 0.0F);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        jarModel.renderCore(MODEL_SCALE);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    private void renderVortexLayer(float yOffset, float rotation, float yaw, float size, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, yOffset, 0.0F);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(VORTEX_TEXTURE);
        TileRenderHelper.drawTexturedQuad(size, color, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

}
