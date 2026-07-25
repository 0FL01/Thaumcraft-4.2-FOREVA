package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.common.blocks.BlockWoodenDevice;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileArcanePressurePlate;

public class TileArcanePressurePlateRenderer extends TileEntitySpecialRenderer<TileArcanePressurePlate> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation("thaumcraft", "textures/blocks/applate1.png"),
            new ResourceLocation("thaumcraft", "textures/blocks/applate2.png"),
            new ResourceLocation("thaumcraft", "textures/blocks/applate3.png")
    };
    private static final float MIN = 1.0F / 16.0F;
    private static final float MAX = 15.0F / 16.0F;

    @Override
    public void render(TileArcanePressurePlate tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() != ConfigBlocks.blockWoodenDevice) {
            return;
        }
        int type = state.getValue(BlockWoodenDevice.TYPE);
        if (type != 2 && type != 3) {
            return;
        }

        float height = type == 3 ? 0.5F / 16.0F : 1.0F / 16.0F;
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            bindTexture(TEXTURES[MathHelper.clamp(tile.setting, 0, TEXTURES.length - 1)]);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            drawPlate(buffer, height, alpha);
            tessellator.draw();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (cullEnabled) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void drawPlate(BufferBuilder buffer, float height, float alpha) {
        float sideV = 1.0F - height;
        face(buffer, MIN, height, MIN, MIN, height, MAX, MAX, height, MAX, MAX, height, MIN,
                MIN, MIN, MAX, MAX, alpha);
        face(buffer, MAX, height, MIN, MAX, 0.0F, MIN, MIN, 0.0F, MIN, MIN, height, MIN,
                MIN, sideV, MAX, 1.0F, alpha);
        face(buffer, MIN, height, MAX, MIN, 0.0F, MAX, MAX, 0.0F, MAX, MAX, height, MAX,
                MIN, sideV, MAX, 1.0F, alpha);
        face(buffer, MIN, height, MIN, MIN, 0.0F, MIN, MIN, 0.0F, MAX, MIN, height, MAX,
                MIN, sideV, MAX, 1.0F, alpha);
        face(buffer, MAX, height, MAX, MAX, 0.0F, MAX, MAX, 0.0F, MIN, MAX, height, MIN,
                MIN, sideV, MAX, 1.0F, alpha);
    }

    private static void face(BufferBuilder buffer,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float u0, float v0, float u1, float v1, float alpha) {
        buffer.pos(x1, y1, z1).tex(u0, v0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(x2, y2, z2).tex(u0, v1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(x3, y3, z3).tex(u1, v1).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(x4, y4, z4).tex(u1, v0).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
    }
}
