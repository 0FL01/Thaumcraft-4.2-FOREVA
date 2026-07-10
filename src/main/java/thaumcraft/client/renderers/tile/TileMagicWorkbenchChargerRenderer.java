package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.client.renderers.models.ModelVisRelay;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;

public class TileMagicWorkbenchChargerRenderer extends TileEntitySpecialRenderer<TileMagicWorkbenchCharger> {
    private static final ResourceLocation RELAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelVisRelay model = new ModelVisRelay();

    @Override
    public void render(TileMagicWorkbenchCharger tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = 0.0F;
        if (Minecraft.getMinecraft().player != null) {
            ticks = Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        }
        float scale = (float) Math.sin(ticks / 2.0F) * 0.05F + 0.95F;
        int light = (VisNetHandler.isNodeValid(tile.getParent()) ? 50 : 0) + (int) (150.0F * scale);
        int low = light % 65536;
        int high = light / 65536;
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(RELAY_TEXTURE);

            model.renderRingFloat(MODEL_SCALE);
            GlStateManager.pushMatrix();
            try {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 0.5F);
                for (int i = 0; i < 4; i++) {
                    model.renderSupport(MODEL_SCALE);
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }
            } finally {
                GlStateManager.popMatrix();
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (tile.color >= 0 && tile.color < ItemShard.colors.length) {
                Color tint = new Color(ItemShard.colors[tile.color]);
                GlStateManager.color(tint.getRed() / 200.0F, tint.getGreen() / 200.0F, tint.getBlue() / 200.0F, 1.0F);
            }
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
            model.renderCrystal(MODEL_SCALE);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            GlStateManager.popMatrix();
        }
    }
}
