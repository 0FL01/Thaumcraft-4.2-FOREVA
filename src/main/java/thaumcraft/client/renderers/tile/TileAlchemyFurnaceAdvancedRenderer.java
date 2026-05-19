package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.client.renderers.models.ModelAlchemyFurnaceAdvanced;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileAlchemyFurnace;

public class TileAlchemyFurnaceAdvancedRenderer extends TileEntitySpecialRenderer<TileAlchemyFurnace> {
    private static final ResourceLocation FURNACE =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace.png");
    private static final ResourceLocation FURNACE_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_on.png");
    private static final ResourceLocation TANK =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank.png");
    private static final ResourceLocation TANK_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank_on.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelAlchemyFurnaceAdvanced model = new ModelAlchemyFurnaceAdvanced();

    @Override
    public void render(TileAlchemyFurnace tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float content = Math.min(1.0F, tile.vis / 50.0F);
        boolean burning = tile.isBurning();
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = burning ? (0.85F + (float) Math.sin(ticks / 6.0F) * 0.15F) : 0.65F;
        float heatNorm = tile.currentItemBurnTime <= 0 ? 0.0F : Math.min(1.0F, (float) tile.furnaceBurnTime / (float) tile.currentItemBurnTime);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (content > 0.0F) {
            bindTexture(burning ? TANK_ON : TANK);
            int alphaChannel = Math.max(50, (int) (255.0F * content * pulse));
            float alphaFactor = alphaChannel / 255.0F;
            for (int side = 0; side < 4; side++) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(side * 90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.color(0.80F, 0.10F, 0.90F, alphaFactor);
                model.renderLavaPanel(MODEL_SCALE);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
        }

        if (burning && heatNorm > 0.0F) {
            float glow = Math.min(1.0F, 0.45F + heatNorm * 0.55F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.0D, 0.99D);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 0.75F, 0.25F, 0.45F * glow);
            drawFurnaceGlowQuad(0.30F, -0.30F, 0.30F, 0.18F);
            GlStateManager.color(1.0F, 0.45F, 0.15F, 0.35F * glow);
            drawFurnaceGlowQuad(0.24F, -0.24F, 0.24F, 0.24F);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void drawFurnaceGlowQuad(float minX, float maxX, float minY, float maxY) {
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder buf = tess.getBuffer();
        buf.begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR);
        buf.pos(minX, minY, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(maxX, minY, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(minX, maxY, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tess.draw();
    }
}
