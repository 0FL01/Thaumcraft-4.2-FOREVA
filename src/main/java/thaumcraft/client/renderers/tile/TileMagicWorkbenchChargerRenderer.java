package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.client.renderers.models.ModelMagicWorkbenchCharger;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;

public class TileMagicWorkbenchChargerRenderer extends TileEntitySpecialRenderer<TileMagicWorkbenchCharger> {
    private static final ResourceLocation RELAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");
    private static final float MODEL_SCALE = 0.0625F;

    private static final int[] RELAY_COLORS = {
            0xFFFF7E,
            0xFF8844,
            0x99CCFF,
            0x80FF80,
            0xCC99FF,
            0xAAAAAA
    };
    private final ModelMagicWorkbenchCharger model = new ModelMagicWorkbenchCharger();

    @Override
    public void render(TileMagicWorkbenchCharger tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();
        bindTexture(RELAY_TEXTURE);

        if (tile.color >= 0 && tile.color < RELAY_COLORS.length) {
            Color tint = new Color(RELAY_COLORS[tile.color]);
            GlStateManager.color(tint.getRed() / 200.0F, tint.getGreen() / 200.0F, tint.getBlue() / 200.0F, 1.0F);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
        model.renderCrystal(MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
