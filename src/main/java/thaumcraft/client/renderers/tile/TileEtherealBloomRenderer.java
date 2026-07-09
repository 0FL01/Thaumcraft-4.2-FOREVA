package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelCube;
import thaumcraft.common.tiles.TileEtherealBloom;

public class TileEtherealBloomRenderer extends TileEntitySpecialRenderer<TileEtherealBloom> {
    private static final ResourceLocation NODES =
            new ResourceLocation("thaumcraft", "textures/misc/nodes.png");
    private static final ResourceLocation CRYSTAL_CAPACITOR =
            new ResourceLocation("thaumcraft", "textures/models/crystalcapacitor.png");

    private static final ResourceLocation LEAF_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/blocks/purifier_leaves.png");
    private static final ResourceLocation STALK_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/blocks/purifier_stalk.png");

    private final ModelCube model = new ModelCube();

    @Override
    public void render(TileEtherealBloom tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) return;

        float rc1 = tile.growthCounter + partialTicks;
        float rc2 = rc1;
        float rc3 = rc1 - 33.0F;
        float rc4 = rc1 - 66.0F;
        if (rc1 > 100.0F) rc1 = 100.0F;
        if (rc2 > 50.0F) rc2 = 50.0F;
        if (rc3 < 0.0F) rc3 = 0.0F;
        if (rc3 > 33.0F) rc3 = 33.0F;
        if (rc4 < 0.0F) rc4 = 0.0F;
        if (rc4 > 33.0F) rc4 = 33.0F;

        float scale1 = rc1 / 100.0F;
        float scale2 = rc2 / 60.0F + 0.1666666F;
        float scale3 = rc3 / 33.0F;
        float scale4 = rc4 / 33.0F * 0.7F;

        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        renderNodePulse(tile, x, y, z, partialTicks, scale1);
        renderCrystalCore(x, y, z, scale1, scale4);
        renderLeafLayers(x, y, z, scale1, scale3, scale4);
        renderStalkLayers(x, y, z, scale1, scale2);

        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void renderNodePulse(TileEtherealBloom tile, double x, double y, double z, float partialTicks, float scale1) {
        int frame = tile.counter % 32;
        float u0 = frame / 32.0F;
        float u1 = u0 + 1.0F / 32.0F;
        float v0 = 6.0F / 32.0F;
        float v1 = 7.0F / 32.0F;

        bindTexture(NODES);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.translate(x + 0.5D, y + scale1, z + 0.5D);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(scale1, 0xFFAADDFF, u0, u1, v0, v1);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderCrystalCore(double x, double y, double z, float scale1, float scale4) {
        bindTexture(CRYSTAL_CAPACITOR);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D - scale4 / 8.0F, y + scale1 - scale4 / 6.0F, z + 0.5D - scale4 / 8.0F);
        GlStateManager.scale(scale4 / 4.0F, scale4 / 3.0F, scale4 / 4.0F);
        this.model.render();
        GlStateManager.popMatrix();
    }

    private void renderLeafLayers(double x, double y, double z, float scale1, float scale3, float scale4) {
        bindTexture(LEAF_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.25D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        for (int a = 0; a < 4; ++a) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale3, scale1, scale3);
            GlStateManager.rotate(90.0F * a, 0.0F, 1.0F, 0.0F);
            drawCenteredTexture();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.6D, z + 0.5D);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        for (int a = 0; a < 4; ++a) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale4, scale1 * 0.7F, scale4);
            GlStateManager.rotate(90.0F * a, 0.0F, 1.0F, 0.0F);
            drawCenteredTexture();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private void renderStalkLayers(double x, double y, double z, float scale1, float scale2) {
        bindTexture(STALK_TEXTURE);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        for (int a = 0; a < 4; ++a) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, (1.0F - scale1) / 2.0F, 0.0F);
            GlStateManager.scale(scale2, scale1, scale2);
            GlStateManager.rotate(90.0F * a, 0.0F, 1.0F, 0.0F);
            drawCenteredTexture();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private static void drawCenteredTexture() {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        float half = 0.5F;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(-half, half, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(half, half, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(half, -half, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(-half, -half, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tess.draw();
    }
}
