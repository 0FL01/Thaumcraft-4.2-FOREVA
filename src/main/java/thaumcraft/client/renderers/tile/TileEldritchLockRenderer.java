package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
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
    private static final float FIELD_MIN = -2.0F;
    private static final float FIELD_MAX = 3.0F;
    private final ModelCube cubeModel = new ModelCube(0);

    @Override
    public void render(TileEldritchLock tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = EnumFacing.byIndex(tile.getFacing());
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        // LOD: full rendering (rings + key + field) within 32 blocks, field-only fallback beyond.
        boolean inRange = tile.getWorld() != null
                && viewer != null
                && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 1024.0D;
        float time = (float) (System.currentTimeMillis() % 700000L) / 250000.0F;
        double viewX = 0.0D;
        double viewY = 0.0D;
        double viewZ = 0.0D;
        if (viewer != null) {
            viewX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
            viewY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
            viewZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        }

        if (inRange) {
            renderLockRings(tile, x, y, z, ticks);
            renderInsertKey(tile, x, y, z, ticks, facing);
        }
        renderLockField(x, y, z, facing, inRange, time, viewX, viewY, viewZ);
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
        TileRenderHelper.renderEntityItem(tile, key, 0.0F);
        GlStateManager.popMatrix();
    }

    private void renderLockField(double x, double y, double z, EnumFacing facing, boolean inRange,
                                 float time, double viewX, double viewY, double viewZ) {
        if (facing == null || facing.getAxis().isVertical()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();

        renderFieldLayers(facing, inRange, time, viewX, viewY, viewZ);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private void renderFieldLayers(
            EnumFacing facing, boolean inRange, float time, double viewX, double viewY, double viewZ) {
        final float offset = 0.5F;

        if (!inRange) {
            bindTexture(PARTICLE_FALLBACK);
            GlStateManager.blendFunc(770, 771);
            drawFieldQuad(facing, offset, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 1.0F);
            return;
        }

        Random random = new Random(31100L);
        for (int i = 0; i < 16; i++) {
            float layerDepth = 16.0F - i;
            float bright = 1.0F / (layerDepth + 1.0F);
            float uvScale = 0.0625F;

            if (i == 0) {
                bindTexture(TUNNEL);
                GlStateManager.blendFunc(770, 771);
                bright = 0.1F;
                layerDepth = 65.0F;
                uvScale = 0.125F;
            } else {
                bindTexture(PARTICLE);
                GlStateManager.blendFunc(1, 1);
                if (i == 1) {
                    uvScale = 0.5F;
                }
            }

            float uvShift = time + (float) (i * i * 4321 + i * 9) * 2.0F;
            float parallaxScale = uvScale * (0.75F + layerDepth * 0.015625F);
            float[] parallax = parallaxOffsets(facing, viewX, viewY, viewZ, parallaxScale);
            float uShift = uvShift * uvScale + parallax[0];
            float vShift = uvShift * uvScale + parallax[1];
            float r = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.1F) * bright;
            float g = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.4F) * bright;
            float b = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.5F) * bright;
            drawFieldQuad(facing, offset, uShift, vShift, uvScale, uvScale, r, g, b, 1.0F);
        }
    }

    private static float[] parallaxOffsets(EnumFacing facing, double viewX, double viewY, double viewZ, float scale) {
        float rotX = ActiveRenderInfo.getRotationX();
        float rotZ = ActiveRenderInfo.getRotationZ();
        float rotYZ = ActiveRenderInfo.getRotationYZ();
        float rotXY = ActiveRenderInfo.getRotationXY();
        float rotXZ = ActiveRenderInfo.getRotationXZ();
        float u;
        float v;
        switch (facing) {
            case NORTH:
            case SOUTH:
                u = (float) (viewX * rotX + viewY * rotXZ);
                v = (float) (viewX * rotZ + viewY * rotXY);
                break;
            case WEST:
            case EAST:
                u = (float) (viewZ * rotYZ + viewY * rotXZ);
                v = (float) (viewZ * rotXY + viewY * rotX);
                break;
            default:
                u = 0.0F;
                v = 0.0F;
        }
        return new float[]{u * scale, v * scale};
    }

    private static void drawFieldQuad(EnumFacing facing, float offset, float uShift, float vShift,
                                      float uScale, float vScale, float r, float g, float b, float a) {
        float u0 = uShift;
        float v0 = vShift;
        float u1 = uShift + uScale;
        float v1 = vShift + vScale;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        switch (facing) {
            case NORTH:
                v(buf, FIELD_MIN, FIELD_MIN, offset, u1, v1, r, g, b, a);
                v(buf, FIELD_MIN, FIELD_MAX, offset, u1, v0, r, g, b, a);
                v(buf, FIELD_MAX, FIELD_MAX, offset, u0, v0, r, g, b, a);
                v(buf, FIELD_MAX, FIELD_MIN, offset, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, FIELD_MIN, FIELD_MAX, offset, u1, v1, r, g, b, a);
                v(buf, FIELD_MIN, FIELD_MIN, offset, u1, v0, r, g, b, a);
                v(buf, FIELD_MAX, FIELD_MIN, offset, u0, v0, r, g, b, a);
                v(buf, FIELD_MAX, FIELD_MAX, offset, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, offset, FIELD_MIN, FIELD_MIN, u1, v1, r, g, b, a);
                v(buf, offset, FIELD_MIN, FIELD_MAX, u1, v0, r, g, b, a);
                v(buf, offset, FIELD_MAX, FIELD_MAX, u0, v0, r, g, b, a);
                v(buf, offset, FIELD_MAX, FIELD_MIN, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, offset, FIELD_MAX, FIELD_MIN, u1, v1, r, g, b, a);
                v(buf, offset, FIELD_MAX, FIELD_MAX, u1, v0, r, g, b, a);
                v(buf, offset, FIELD_MIN, FIELD_MAX, u0, v0, r, g, b, a);
                v(buf, offset, FIELD_MIN, FIELD_MIN, u0, v1, r, g, b, a);
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
