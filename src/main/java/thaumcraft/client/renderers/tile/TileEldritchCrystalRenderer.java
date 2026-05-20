package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelEldritchCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

public class TileEldritchCrystalRenderer extends TileEntitySpecialRenderer<TileEldritchCrystal> {
    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/blocks/crust.png");
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vcrystal.png");

    private final ModelEldritchCrystal model = new ModelEldritchCrystal();

    @Override
    public void render(TileEldritchCrystal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        int rotationStep = tile.getWorld() == null ? 0 : Math.floorMod(tile.hashCode(), 4);

        GlStateManager.pushMatrix();
        translateFromOrientation(x, y, z, tile.orientation, rotationStep);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(BASE_TEXTURE);
        model.renderBase();

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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
        model.renderCrystal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousX, previousY);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void translateFromOrientation(double x, double y, double z, int orientation, int quarterTurns) {
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (orientation == 0) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 2) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0F, 0.0F, -0.5F);
        GlStateManager.rotate(90.0F * quarterTurns, 0.0F, 0.0F, 1.0F);
    }
}
