package thaumcraft.client.renderers.tile;

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
            new ResourceLocation("thaumcraft", "textures/models/Bore.png");
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
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = tile.orientation == null ? EnumFacing.NORTH : tile.orientation;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        applyFacingRotation(facing);
        if (tile.baseOrientation == EnumFacing.DOWN) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.pushMatrix();
        bindTexture(BORE_TEXTURE);
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        boreModel.renderBase(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        boreModel.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        float spinSpeed = tile.hasFocus && tile.hasPickaxe ? 4.0F : 1.0F;
        float spin = (tile.topRotation + partialTicks * spinSpeed) % 360.0F;
        GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        emitModel.render(MODEL_SCALE, tile.hasFocus);
        GlStateManager.popMatrix();

        float rotation = (ticks % 45.0F);
        renderVortexLayer(-0.17F, -(rotation * 8.0F), 10.0F, 0.40F, 0xFFFFFFFF);
        renderVortexLayer(-0.21F, rotation * 8.0F, 10.0F, 0.30F, 0xCCFFFFFF);
        renderVortexLayer(-0.25F, -(rotation * 8.0F), -10.0F, 0.20F, 0xCCFFFFFF);

        GlStateManager.pushMatrix();
        bindTexture(JAR_TEXTURE);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, 0.3F, 0.0F);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        jarModel.renderCore(MODEL_SCALE);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
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

    private static void applyFacingRotation(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case DOWN:
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
            default:
                break;
        }
    }
}
