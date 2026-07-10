package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelBellows;
import thaumcraft.common.tiles.TileBellows;

public class TileBellowsRenderer extends TileEntitySpecialRenderer<TileBellows> {
    private static final ResourceLocation BELLOWS =
            new ResourceLocation("thaumcraft", "textures/models/bellows.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBellows model = new ModelBellows();

    @Override
    public void render(TileBellows bellows, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (bellows == null) {
            return;
        }

        float inflate;
        int orientation;
        if (bellows.getWorld() == null) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            float ticks = player == null ? 0.0F : player.ticksExisted + partialTicks;
            inflate = MathHelper.sin(ticks / 8.0F) * 0.3F + 0.7F;
            orientation = 2;
        } else {
            inflate = bellows.inflation;
            orientation = bellows.orientation;
        }

        float tscale = 0.125F + inflate * 0.875F;
        bindTexture(BELLOWS);

        GlStateManager.pushMatrix();
        GlStateManager.enableNormalize();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        try {
            translateFromOrientation(x, y, z, orientation);
            GlStateManager.translate(0.0F, 1.0F, 0.0F);

            GlStateManager.pushMatrix();
            try {
                GlStateManager.scale(0.5F, (inflate + 0.1F) / 2.0F, 0.5F);
                model.bag.setRotationPoint(0.0F, 0.5F, 0.0F);
                model.bag.render(MODEL_SCALE);
            } finally {
                GlStateManager.popMatrix();
            }

            GlStateManager.translate(0.0F, -1.0F, 0.0F);

            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(0.0F, -tscale / 2.0F + 0.5F, 0.0F);
                model.topPlank.render(MODEL_SCALE);
            } finally {
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(0.0F, tscale / 2.0F - 0.5F, 0.0F);
                model.bottomPlank.render(MODEL_SCALE);
            } finally {
                GlStateManager.popMatrix();
            }

            model.render();
        } finally {
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableNormalize();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void translateFromOrientation(double x, double y, double z, int orientation) {
        GlStateManager.translate((float) x + 0.5F, (float) y - 0.5F, (float) z + 0.5F);
        if (orientation == 0) {
            GlStateManager.translate(0.0F, 1.0F, -1.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.translate(0.0F, 1.0F, 1.0F);
            GlStateManager.rotate(270.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 2) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        }
    }
}
