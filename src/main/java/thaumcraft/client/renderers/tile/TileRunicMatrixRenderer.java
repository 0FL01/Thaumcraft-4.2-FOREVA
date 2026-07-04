package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelCube;
import thaumcraft.common.tiles.TileInfusionMatrix;

public class TileRunicMatrixRenderer extends TileEntitySpecialRenderer<TileInfusionMatrix> {

    private static final ResourceLocation INFUSER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/infuser.png");
    private static final Random HALO_RANDOM = new Random(245L);

    private final ModelCube model = new ModelCube(0);
    private final ModelCube modelOver = new ModelCube(32);

    @Override
    public void render(TileInfusionMatrix tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        renderInfusionMatrix(tile, x, y, z, partialTicks);
    }

    private void renderInfusionMatrix(TileInfusionMatrix tile, double x, double y, double z, float partialTicks) {
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float startUp = tile.startUp <= 0.0F ? 1.0F : tile.startUp;
        float craftFactor = (float) Math.min(tile.craftCount, 50) / 50.0F;
        float instability = Math.min(6.0F, 1.0F + tile.instability * 0.66F * craftFactor);

        GlStateManager.pushMatrix();
        bindTexture(INFUSER_TEXTURE);
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (tile.getWorld() != null) {
            GlStateManager.rotate((ticks % 360.0F) * startUp, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(35.0F * startUp, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F * startUp, 0.0F, 0.0F, 1.0F);
        }

        prepareSolidMatrixPass();
        renderCubeCluster(tile, ticks, instability, startUp);
        if (tile.active) {
            renderCubeOverlay(tile, ticks, instability, startUp);
        }
        restoreDefaultMatrixPass();
        GlStateManager.popMatrix();

        if (tile.crafting) {
            drawHalo(x, y, z, tile.craftCount);
        }
    }

    private static void prepareSolidMatrixPass() {
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void restoreDefaultMatrixPass() {
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderCubeCluster(TileInfusionMatrix tile, float ticks, float instability, float startUp) {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    float ox = 0.0F;
                    float oy = 0.0F;
                    float oz = 0.0F;
                    if (tile.active) {
                        ox = MathHelper.sin((ticks + a * 10.0F) / (15.0F - instability / 2.0F)) * 0.01F * startUp * instability;
                        oy = MathHelper.sin((ticks + b * 10.0F) / (14.0F - instability / 2.0F)) * 0.01F * startUp * instability;
                        oz = MathHelper.sin((ticks + c * 10.0F) / (13.0F - instability / 2.0F)) * 0.01F * startUp * instability;
                    }

                    int sx = a == 0 ? -1 : 1;
                    int sy = b == 0 ? -1 : 1;
                    int sz = c == 0 ? -1 : 1;

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(ox + sx * 0.25F, oy + sy * 0.25F, oz + sz * 0.25F);
                    if (a > 0) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    if (b > 0) GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    if (c > 0) GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.scale(0.45F, 0.45F, 0.45F);
                    model.render();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void renderCubeOverlay(TileInfusionMatrix tile, float ticks, float instability, float startUp) {
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.depthMask(false);

        float prevX = OpenGlHelper.lastBrightnessX;
        float prevY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    float ox = MathHelper.sin((ticks + a * 10.0F) / (15.0F - instability / 2.0F)) * 0.01F * startUp * instability;
                    float oy = MathHelper.sin((ticks + b * 10.0F) / (14.0F - instability / 2.0F)) * 0.01F * startUp * instability;
                    float oz = MathHelper.sin((ticks + c * 10.0F) / (13.0F - instability / 2.0F)) * 0.01F * startUp * instability;

                    int sx = a == 0 ? -1 : 1;
                    int sy = b == 0 ? -1 : 1;
                    int sz = c == 0 ? -1 : 1;

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(ox + sx * 0.25F, oy + sy * 0.25F, oz + sz * 0.25F);
                    if (a > 0) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    if (b > 0) GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    if (c > 0) GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.scale(0.45F, 0.45F, 0.45F);

                    float overlayAlpha = (MathHelper.sin((ticks + a * 2.0F + b * 3.0F + c * 4.0F) / 4.0F) * 0.1F + 0.2F) * startUp;
                    GlStateManager.color(0.8F, 0.1F, 1.0F, overlayAlpha);
                    modelOver.render();
                    GlStateManager.popMatrix();
                }
            }
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void drawHalo(double x, double y, double z, int count) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);

        int rays = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 20 : 10;
        float craftScale = (float) Math.min(count, 50) / 50.0F;
        if (craftScale <= 0.0F) craftScale = 0.05F;
        float spin = count / 500.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        HALO_RANDOM.setSeed(245L);
        for (int i = 0; i < rays; i++) {
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(HALO_RANDOM.nextFloat() * 360.0F + spin * 360.0F, 0.0F, 0.0F, 1.0F);

            float length = (HALO_RANDOM.nextFloat() * 20.0F + 5.0F) / (20.0F / craftScale);
            float width = (HALO_RANDOM.nextFloat() * 2.0F + 1.0F) / (20.0F / craftScale);

            buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(0.0D, 0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            buf.pos(-0.866D * width, length, -0.5D * width).color(0.8F, 0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(0.866D * width, length, -0.5D * width).color(0.8F, 0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(0.0D, length, width).color(0.8F, 0.0F, 1.0F, 0.0F).endVertex();
            buf.pos(-0.866D * width, length, -0.5D * width).color(0.8F, 0.0F, 1.0F, 0.0F).endVertex();
            tess.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.popMatrix();
    }
}
