package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

public class TileEldritchCrystalRenderer extends TileEntitySpecialRenderer<TileEldritchCrystal> {
    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/blocks/crust.png");
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vcrystal.png");
    private static final float MODEL_SCALE = 0.4F;

    private final ModelCrystal model = new ModelCrystal();

    @Override
    public void render(TileEldritchCrystal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int rotationStep = Math.floorMod(tile.hashCode(), 4);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByAttachment(tile.orientation, rotationStep);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(BASE_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.32F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.32F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        float ticks = player == null ? TileRenderHelper.ticks(tile, partialTicks) : player.ticksExisted + partialTicks;
        float glow = MathHelper.sin(ticks / 6.0F) * 0.075F + 0.925F;
        int light = (int) (210.0F * glow);
        int low = light % 65536;
        int high = light / 65536;
        float previousX = OpenGlHelper.lastBrightnessX;
        float previousY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);

        bindTexture(CRYSTAL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
        model.render();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousX, previousY);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void orientByAttachment(short orientation, int quarterTurns) {
        EnumFacing face = EnumFacing.byIndex(orientation);
        if (face != null) {
            switch (face) {
                case DOWN:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case UP:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case NORTH:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case SOUTH:
                    break;
                case WEST:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case EAST:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                default:
                    break;
            }
        }
        GlStateManager.translate(0.0F, 0.0F, -0.5F);
        GlStateManager.rotate(90.0F * quarterTurns, 0.0F, 0.0F, 1.0F);
    }
}
