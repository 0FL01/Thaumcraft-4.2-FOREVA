package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelCube;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileEldritchLock;

public class TileEldritchLockRenderer extends TileEntitySpecialRenderer<TileEldritchLock> {
    private static final ResourceLocation CUBE =
            new ResourceLocation("thaumcraft", "textures/models/eldritch_cube.png");
    private static final ResourceLocation TUNNEL =
            new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation PARTICLE_FALLBACK =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");
    private final ModelCube cubeModel = new ModelCube(0);

    @Override
    public void render(TileEldritchLock tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = EnumFacing.byIndex(tile.getFacing());

        renderLockRings(tile, x, y, z, ticks);
        renderInsertKey(tile, x, y, z, ticks, facing);
        renderLockField(tile, x, y, z, ticks, facing);
    }

    private void renderLockRings(TileEldritchLock tile, double x, double y, double z, float ticks) {
        EnumFacing facing = EnumFacing.byIndex(tile.getFacing());
        float axisX = facing != null ? facing.getXOffset() : 0.0F;
        float axisY = facing != null ? facing.getYOffset() : 1.0F;
        float axisZ = facing != null ? facing.getZOffset() : 0.0F;
        if (axisX == 0.0F && axisY == 0.0F && axisZ == 0.0F) {
            axisY = 1.0F;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(CUBE);

        for (int u = 0; u < 4; u++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F * u, axisX, axisY, axisZ);
            int level = 5 - (tile.count + u * 5) / 20;
            for (int a = 1; a < level; a++) {
                GlStateManager.pushMatrix();
                float wobble = (float) Math.sin((ticks + a * 10.0F + u * 20.0F) / 20.0F) * 0.1F;
                if (a == 1 || a == 4) {
                    wobble = wobble * 0.5F + 0.2F;
                }
                GlStateManager.translate(0.0D, 0.25D + 0.5D * a, 0.0D);
                GlStateManager.scale(0.5F + wobble, 0.5F, 0.5F + wobble);
                cubeModel.render();
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderInsertKey(TileEldritchLock tile, double x, double y, double z, float ticks, EnumFacing facing) {
        if (tile.count < 0 || ConfigItems.itemEldritchObject == null || facing == null) {
            return;
        }

        ItemStack key = new ItemStack(ConfigItems.itemEldritchObject, 1, 2);
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                x + 0.5D + facing.getXOffset() * 0.525D,
                y + 0.285D,
                z + 0.5D + facing.getZOffset() * 0.525D);
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        TileRenderHelper.renderFloatingItem(key, ticks, 0.0F, 0.6F);
        GlStateManager.popMatrix();
    }

    private void renderLockField(TileEldritchLock tile, double x, double y, double z, float ticks, EnumFacing facing) {
        if (facing == null || facing.getAxis().isVertical()) {
            return;
        }

        boolean inRange = this.rendererDispatcher != null
                && this.rendererDispatcher.entity != null
                && this.rendererDispatcher.entity.getDistanceSq(tile.getPos()) < 512.0D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();

        renderFieldLayers(facing, inRange, ticks);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderFieldLayers(EnumFacing facing, boolean inRange, float ticks) {
        final float offset = 0.5F;
        final float min = -2.0F;
        final float max = 3.0F;

        if (!inRange) {
            bindTexture(PARTICLE_FALLBACK);
            GlStateManager.blendFunc(770, 771);
            drawFieldQuad(facing, offset, min, max, 0.0F, 1.0F, 0.5F, 0.5F, 0.5F, 1.0F);
            return;
        }

        Random random = new Random(31100L + facing.getIndex() * 37L);
        float time = ticks / 20.0F;
        for (int i = 0; i < 16; i++) {
            float layer = 16.0F - i;
            float bright = 1.0F / (layer + 1.0F);
            float uvScale = i == 0 ? 0.125F : (i == 1 ? 0.5F : 0.0625F);
            float uvShift = time + i * 0.125F;
            float alpha = i == 0 ? 1.0F : 0.9F;

            if (i == 0) {
                bindTexture(TUNNEL);
                GlStateManager.blendFunc(770, 771);
                bright = 0.1F;
            } else {
                bindTexture(PARTICLE);
                GlStateManager.blendFunc(1, 1);
            }

            float r = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.1F) * bright;
            float g = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.4F) * bright;
            float b = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.5F) * bright;
            drawFieldQuad(facing, offset, min, max, uvShift, uvScale, r, g, b, alpha);
        }
    }

    private static void drawFieldQuad(EnumFacing facing, float offset, float min, float max,
                                      float uvShift, float uvScale, float r, float g, float b, float a) {
        float u0 = uvShift;
        float v0 = uvShift;
        float u1 = uvShift + uvScale;
        float v1 = uvShift + uvScale;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        switch (facing) {
            case NORTH:
                v(buf, min, max, offset, u1, v1, r, g, b, a);
                v(buf, min, min, offset, u1, v0, r, g, b, a);
                v(buf, max, min, offset, u0, v0, r, g, b, a);
                v(buf, max, max, offset, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, min, min, offset, u1, v1, r, g, b, a);
                v(buf, min, max, offset, u1, v0, r, g, b, a);
                v(buf, max, max, offset, u0, v0, r, g, b, a);
                v(buf, max, min, offset, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, offset, max, min, u1, v1, r, g, b, a);
                v(buf, offset, max, max, u1, v0, r, g, b, a);
                v(buf, offset, min, max, u0, v0, r, g, b, a);
                v(buf, offset, min, min, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, offset, min, min, u1, v1, r, g, b, a);
                v(buf, offset, min, max, u1, v0, r, g, b, a);
                v(buf, offset, max, max, u0, v0, r, g, b, a);
                v(buf, offset, max, min, u0, v1, r, g, b, a);
                break;
            default:
                break;
        }

        tess.draw();
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
