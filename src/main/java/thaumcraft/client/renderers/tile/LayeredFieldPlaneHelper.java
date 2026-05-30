package thaumcraft.client.renderers.tile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.EldritchDiagnostics;

final class LayeredFieldPlaneHelper {
    static final ResourceLocation TUNNEL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    static final ResourceLocation FIELD_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    static final ResourceLocation FIELD_TEXTURE_FALLBACK =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");
    static final long FIELD_COLOR_SEED = 31100L;

    private static final FloatBuffer TEX_GEN_S = makeBuffer(1.0F, 0.0F, 0.0F, 0.0F);
    private static final FloatBuffer TEX_GEN_T_UP = makeBuffer(0.0F, 0.0F, 1.0F, 0.0F);
    private static final FloatBuffer TEX_GEN_T_NS = makeBuffer(0.0F, 1.0F, 0.0F, 0.0F);
    private static final FloatBuffer TEX_GEN_R = makeBuffer(0.0F, 0.0F, 0.0F, 1.0F);
    private static final FloatBuffer TEX_GEN_Q_UP = makeBuffer(0.0F, 1.0F, 0.0F, 0.0F);
    private static final FloatBuffer TEX_GEN_Q_NS = makeBuffer(0.0F, 0.0F, 1.0F, 0.0F);

    private LayeredFieldPlaneHelper() {}

    static void renderLayeredFace(
            EnumFacing face,
            double x,
            double y,
            double z,
            float offset,
            boolean inRange,
            float fallbackShade,
            double cameraX,
            double cameraY,
            double cameraZ) {
        renderLayeredFaceRect(face, x, y, z, offset, inRange, fallbackShade, cameraX, cameraY, cameraZ,
                0.0F, 1.0F, 0.0F, 1.0F);
    }

    static void renderLayeredFaceRect(
            EnumFacing face,
            double x,
            double y,
            double z,
            float offset,
            boolean inRange,
            float fallbackShade,
            double cameraX,
            double cameraY,
            double cameraZ,
            float minA,
            float maxA,
            float minB,
            float maxB) {
        // EldritchDiagnostics: disabled for release
        // EldritchDiagnostics.fieldHelperFaces++;
        if (!inRange) {
            Minecraft.getMinecraft().renderEngine.bindTexture(FIELD_TEXTURE_FALLBACK);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            drawFallbackFace(face, x, y, z, offset, minA, maxA, minB, maxB, fallbackShade);
            GlStateManager.disableBlend();
            return;
        }

        Random random = new Random(FIELD_COLOR_SEED);
        for (int i = 0; i < 16; i++) {
            // EldritchDiagnostics.fieldHelperLayers++;
            float layerDepth = 16.0F - i;
            float texScale = 0.0625F;
            float shade = 1.0F / (layerDepth + 1.0F);
            if (i == 0) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TUNNEL_TEXTURE);
                shade = 0.1F;
                layerDepth = 65.0F;
                texScale = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
            } else {
                Minecraft.getMinecraft().renderEngine.bindTexture(FIELD_TEXTURE);
                if (i == 1) {
                    texScale = 0.5F;
                }
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
            }

            float planeDepth = setupTexGen(face, x, y, z, offset, layerDepth, cameraX, cameraY, cameraZ);
            setupTextureMatrix(face, i, texScale, layerDepth, planeDepth, cameraX, cameraY, cameraZ);

            float r = random.nextFloat() * 0.5F + 0.1F;
            float g = random.nextFloat() * 0.5F + 0.4F;
            float b = random.nextFloat() * 0.5F + 0.5F;
            if (i == 0) {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            }
            drawGeneratedFace(face, x, y, z, offset, minA, maxA, minB, maxB, r * shade, g * shade, b * shade);

            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }

        disableTexGen();
        GlStateManager.disableBlend();
    }

    private static float setupTexGen(
            EnumFacing face,
            double x,
            double y,
            double z,
            float offset,
            float layerDepth,
            double cameraX,
            double cameraY,
            double cameraZ) {
        float plane;
        float depth;
        float depthFar;
        float planeShift;
        GlStateManager.pushMatrix();
        switch (face) {
            case UP:
                plane = (float) (y + offset);
                depth = plane - (float) cameraY;
                depthFar = plane + layerDepth - (float) cameraY;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate((float) cameraX, planeShift, (float) cameraZ);
                setTexGenPlanes(face);
                break;
            case DOWN:
                plane = (float) (y + offset);
                depth = -plane + (float) cameraY;
                depthFar = -plane + layerDepth + (float) cameraY;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate((float) cameraX, planeShift, (float) cameraZ);
                setTexGenPlanes(face);
                break;
            case NORTH:
                plane = (float) (z + offset);
                depth = -plane + (float) cameraZ;
                depthFar = -plane + layerDepth + (float) cameraZ;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate((float) cameraX, (float) cameraY, planeShift);
                setTexGenPlanes(face);
                break;
            case SOUTH:
                plane = (float) (z + offset);
                depth = plane - (float) cameraZ;
                depthFar = plane + layerDepth - (float) cameraZ;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate((float) cameraX, (float) cameraY, planeShift);
                setTexGenPlanes(face);
                break;
            case WEST:
                plane = (float) (x + offset);
                depth = -plane + (float) cameraX;
                depthFar = -plane + layerDepth + (float) cameraX;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate(planeShift, (float) cameraY, (float) cameraZ);
                setTexGenPlanes(face);
                break;
            case EAST:
            default:
                plane = (float) (x + offset);
                depth = plane - (float) cameraX;
                depthFar = plane + layerDepth - (float) cameraX;
                planeShift = plane + depth / depthFar;
                GlStateManager.translate(planeShift, (float) cameraY, (float) cameraZ);
                setTexGenPlanes(face);
                break;
        }
        GlStateManager.popMatrix();
        return depth;
    }

    private static void setupTextureMatrix(
            EnumFacing face, int layer, float texScale, float layerDepth, float depth,
            double cameraX, double cameraY, double cameraZ) {
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, (float) (System.currentTimeMillis() % 700000L) / 250000.0F, 0.0F);
        GlStateManager.scale(texScale, texScale, texScale);
        GlStateManager.translate(0.5F, 0.5F, 0.0F);
        GlStateManager.rotate((float) (layer * layer * 4321 + layer * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);

        switch (face) {
            case UP:
            case DOWN:
                GlStateManager.translate((float) -cameraX, (float) -cameraZ, (float) -cameraY);
                GlStateManager.translate(
                        ActiveRenderInfo.getRotationX() * layerDepth / depth,
                        ActiveRenderInfo.getRotationYZ() * layerDepth / depth,
                        (float) -cameraY);
                break;
            case NORTH:
            case SOUTH:
                GlStateManager.translate((float) -cameraX, (float) -cameraY, (float) -cameraZ);
                GlStateManager.translate(
                        ActiveRenderInfo.getRotationX() * layerDepth / depth,
                        ActiveRenderInfo.getRotationXZ() * layerDepth / depth,
                        (float) -cameraZ);
                break;
            case WEST:
            case EAST:
                GlStateManager.translate((float) -cameraZ, (float) -cameraY, (float) -cameraX);
                GlStateManager.translate(
                        ActiveRenderInfo.getRotationYZ() * layerDepth / depth,
                        ActiveRenderInfo.getRotationXZ() * layerDepth / depth,
                        (float) -cameraX);
                break;
            default:
                break;
        }
        GlStateManager.matrixMode(5888);
    }

    private static void setTexGenPlanes(EnumFacing face) {
        GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);

        switch (face) {
            case UP:
            case DOWN:
                GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, TEX_GEN_S);
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, TEX_GEN_T_UP);
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, TEX_GEN_R);
                GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, TEX_GEN_Q_UP);
                break;
            case NORTH:
            case SOUTH:
                GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, TEX_GEN_S);
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, TEX_GEN_T_NS);
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, TEX_GEN_R);
                GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, TEX_GEN_Q_NS);
                break;
            case WEST:
            case EAST:
                GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, TEX_GEN_Q_NS);
                GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, TEX_GEN_T_NS);
                GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, TEX_GEN_R);
                GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, TEX_GEN_S);
                break;
            default:
                break;
        }

        GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
    }

    private static void disableTexGen() {
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
    }

    private static FloatBuffer makeBuffer(float x, float y, float z, float w) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(x).put(y).put(z).put(w);
        buffer.flip();
        return buffer;
    }

    private static void drawGeneratedFace(
            EnumFacing face, double x, double y, double z, float offset,
            float minA, float maxA, float minB, float maxB,
            float r, float g, float b) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addFaceVertices(buf, face, x, y, z, offset, minA, maxA, minB, maxB, r, g, b, 1.0F, false);
        tess.draw();
    }

    private static void drawFallbackFace(
            EnumFacing face, double x, double y, double z, float offset,
            float minA, float maxA, float minB, float maxB, float shade) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        addFaceVertices(buf, face, x, y, z, offset, minA, maxA, minB, maxB, shade, shade, shade, 1.0F, true);
        tess.draw();
    }

    private static void addFaceVertices(
            BufferBuilder buf,
            EnumFacing face,
            double x,
            double y,
            double z,
            float offset,
            float minA,
            float maxA,
            float minB,
            float maxB,
            float r,
            float g,
            float b,
            float a,
            boolean textured) {
        switch (face) {
            case UP:
                vertex(buf, x + minA, y + offset, z + maxB, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + minA, y + offset, z + minB, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + offset, z + minB, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + offset, z + maxB, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            case DOWN:
                vertex(buf, x + minA, y + offset, z + minB, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + minA, y + offset, z + maxB, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + offset, z + maxB, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + offset, z + minB, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            case NORTH:
                vertex(buf, x + minA, y + maxB, z + offset, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + minA, y + minB, z + offset, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + minB, z + offset, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + maxB, z + offset, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            case SOUTH:
                vertex(buf, x + minA, y + minB, z + offset, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + minA, y + maxB, z + offset, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + maxB, z + offset, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + maxA, y + minB, z + offset, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            case WEST:
                vertex(buf, x + offset, y + maxB, z + minA, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + maxB, z + maxA, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + minB, z + maxA, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + minB, z + minA, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            case EAST:
                vertex(buf, x + offset, y + minB, z + minA, 1.0F, 1.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + minB, z + maxA, 1.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + maxB, z + maxA, 0.0F, 0.0F, r, g, b, a, textured);
                vertex(buf, x + offset, y + maxB, z + minA, 0.0F, 1.0F, r, g, b, a, textured);
                break;
            default:
                break;
        }
    }

    private static void vertex(
            BufferBuilder buf, double x, double y, double z, float u, float v,
            float r, float g, float b, float a, boolean textured) {
        if (textured) {
            buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
        } else {
            buf.pos(x, y, z).color(r, g, b, a).endVertex();
        }
    }
}
