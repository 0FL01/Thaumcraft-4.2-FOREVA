package thaumcraft.client.renderers.tile;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileBellows;
import thaumcraft.common.tiles.TileTube;

public final class TubeConduitRenderHelper {
    private static final float ARM_MIN = 7.0F / 16.0F;
    private static final float ARM_MAX = 9.0F / 16.0F;
    private static final float EXTENSION = 6.0F / 16.0F;
    private static final float FALLBACK_MIN = 6.0F / 16.0F;
    private static final float FALLBACK_MAX = 10.0F / 16.0F;
    private static final float JOINT_MIN = 6.5F / 16.0F;
    private static final float JOINT_MAX = 9.5F / 16.0F;
    private static final float FILTER_MIN = 5.5F / 16.0F;
    private static final float FILTER_MAX = 10.5F / 16.0F;
    private static final float BUFFER_MIN = 4.0F / 16.0F;
    private static final float BUFFER_MAX = 12.0F / 16.0F;

    enum TubeType {
        ORDINARY("thaumcraft:blocks/pipe_1"),
        VALVE("thaumcraft:blocks/pipe_1"),
        FILTER("thaumcraft:blocks/pipe_1"),
        BUFFER("thaumcraft:blocks/pipe_buffer"),
        RESTRICTED("thaumcraft:blocks/pipe_restrict"),
        DIRECTIONAL("thaumcraft:blocks/pipe_1");

        private final String armTexture;

        TubeType(String armTexture) {
            this.armTexture = armTexture;
        }
    }

    private TubeConduitRenderHelper() {}

    static void renderConduit(TileEntity tile,
                              IEssentiaTransport transport,
                              boolean[] openSides,
                              TubeType type,
                              @Nullable Aspect filterAspect,
                              double x, double y, double z) {
        if (tile == null || tile.getWorld() == null || tile.getPos() == null) {
            return;
        }

        TextureAtlasSprite arm = atlas(type.armTexture);
        if (arm == null) {
            return;
        }

        TileEntity[] neighbours = findNeighbours(tile, transport, openSides, type);
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            int drawnAxes = drawArms(buffer, neighbours, arm);
            drawCenter(buffer, type, filterAspect, drawnAxes, hasExternalNeighbour(neighbours));
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

    private static TileEntity[] findNeighbours(TileEntity tile,
                                                IEssentiaTransport transport,
                                                boolean[] openSides,
                                                TubeType type) {
        TileEntity[] neighbours = new TileEntity[EnumFacing.VALUES.length];
        for (EnumFacing face : EnumFacing.VALUES) {
            int index = face.getIndex();
            if (index >= openSides.length || !openSides[index] || !transport.isConnectable(face)) {
                continue;
            }

            TileEntity neighbour = ThaumcraftApiHelper.getConnectableTile(
                    tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), face);
            if (neighbour == null && type == TubeType.BUFFER) {
                TileEntity adjacent = tile.getWorld().getTileEntity(tile.getPos().offset(face));
                if (adjacent instanceof TileBellows
                        && ((TileBellows) adjacent).orientation == face.getOpposite().getIndex()) {
                    neighbour = adjacent;
                }
            }
            neighbours[index] = neighbour;
        }
        return neighbours;
    }

    private static int drawArms(BufferBuilder buffer, TileEntity[] neighbours, TextureAtlasSprite sprite) {
        int drawnAxes = 0;
        if (connected(neighbours, EnumFacing.WEST) || connected(neighbours, EnumFacing.EAST)) {
            float minX = connected(neighbours, EnumFacing.WEST)
                    ? extendedMinimum(neighbours[EnumFacing.WEST.getIndex()]) : ARM_MIN;
            float maxX = connected(neighbours, EnumFacing.EAST)
                    ? extendedMaximum(neighbours[EnumFacing.EAST.getIndex()]) : ARM_MAX;
            drawTubeCuboid(buffer, minX, ARM_MIN, ARM_MIN, maxX, ARM_MAX, ARM_MAX, sprite, 0xFFFFFFFF);
            ++drawnAxes;
        }
        if (connected(neighbours, EnumFacing.DOWN) || connected(neighbours, EnumFacing.UP)) {
            float minY = connected(neighbours, EnumFacing.DOWN)
                    ? extendedMinimum(neighbours[EnumFacing.DOWN.getIndex()]) : ARM_MIN;
            float maxY = connected(neighbours, EnumFacing.UP)
                    ? extendedMaximum(neighbours[EnumFacing.UP.getIndex()]) : ARM_MAX;
            drawTubeCuboid(buffer, ARM_MIN, minY, ARM_MIN, ARM_MAX, maxY, ARM_MAX, sprite, 0xFFFFFFFF);
            ++drawnAxes;
        }
        if (connected(neighbours, EnumFacing.NORTH) || connected(neighbours, EnumFacing.SOUTH)) {
            float minZ = connected(neighbours, EnumFacing.NORTH)
                    ? extendedMinimum(neighbours[EnumFacing.NORTH.getIndex()]) : ARM_MIN;
            float maxZ = connected(neighbours, EnumFacing.SOUTH)
                    ? extendedMaximum(neighbours[EnumFacing.SOUTH.getIndex()]) : ARM_MAX;
            drawTubeCuboid(buffer, ARM_MIN, ARM_MIN, minZ, ARM_MAX, ARM_MAX, maxZ, sprite, 0xFFFFFFFF);
            ++drawnAxes;
        }
        return drawnAxes;
    }

    private static void drawCenter(BufferBuilder buffer,
                                   TubeType type,
                                   @Nullable Aspect filterAspect,
                                   int drawnAxes,
                                   boolean externalNeighbour) {
        if (type == TubeType.FILTER) {
            drawTubeCuboid(buffer, FILTER_MIN, FILTER_MIN, FILTER_MIN, FILTER_MAX, FILTER_MAX, FILTER_MAX,
                    atlas("thaumcraft:blocks/pipe_filter"), 0xFFFFFFFF);
            int coreColor = filterAspect == null
                    ? 0xFFFFFFFF : 0xFF000000 | (filterAspect.getColor() & 0x00FFFFFF);
            drawTubeCuboid(buffer, FILTER_MIN, FILTER_MIN, FILTER_MIN, FILTER_MAX, FILTER_MAX, FILTER_MAX,
                    atlas("thaumcraft:blocks/pipe_filter_core"), coreColor);
            return;
        }
        if (type == TubeType.BUFFER) {
            drawTubeCuboid(buffer, BUFFER_MIN, BUFFER_MIN, BUFFER_MIN, BUFFER_MAX, BUFFER_MAX, BUFFER_MAX,
                    atlas("thaumcraft:blocks/pipe_buffer"), 0xFFFFFFFF);
            return;
        }
        if (drawnAxes == 0 || externalNeighbour || type == TubeType.VALVE) {
            drawTubeCuboid(buffer, FALLBACK_MIN, FALLBACK_MIN, FALLBACK_MIN,
                    FALLBACK_MAX, FALLBACK_MAX, FALLBACK_MAX,
                    atlas("thaumcraft:blocks/pipe_2"), 0xFFFFFFFF);
            return;
        }

        String jointTexture = type == TubeType.RESTRICTED
                ? "thaumcraft:blocks/pipe_restrict" : "thaumcraft:blocks/pipe_3";
        drawTubeCuboid(buffer, JOINT_MIN, JOINT_MIN, JOINT_MIN, JOINT_MAX, JOINT_MAX, JOINT_MAX,
                atlas(jointTexture), 0xFFFFFFFF);
    }

    private static boolean connected(TileEntity[] neighbours, EnumFacing face) {
        return neighbours[face.getIndex()] != null;
    }

    private static float extendedMinimum(TileEntity neighbour) {
        return shouldExtend(neighbour) ? -EXTENSION : 0.0F;
    }

    private static float extendedMaximum(TileEntity neighbour) {
        return shouldExtend(neighbour) ? 1.0F + EXTENSION : 1.0F;
    }

    private static boolean shouldExtend(TileEntity neighbour) {
        return neighbour instanceof IEssentiaTransport
                && ((IEssentiaTransport) neighbour).renderExtendedTube();
    }

    private static boolean hasExternalNeighbour(TileEntity[] neighbours) {
        for (TileEntity neighbour : neighbours) {
            if (neighbour != null && !(neighbour instanceof TileTube)) {
                return true;
            }
        }
        return false;
    }

    private static void drawTubeCuboid(BufferBuilder buffer,
                                       float minX, float minY, float minZ,
                                       float maxX, float maxY, float maxZ,
                                       TextureAtlasSprite sprite, int argb) {
        if (sprite == null) {
            return;
        }

        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        boolean xOutside = minX < 0.0F || maxX > 1.0F;
        boolean yOutside = minY < 0.0F || maxY > 1.0F;
        boolean zOutside = minZ < 0.0F || maxZ > 1.0F;
        float uMinX = sprite.getInterpolatedU(xOutside ? 0.0F : minX * 16.0F);
        float uMaxX = sprite.getInterpolatedU(xOutside ? 16.0F : maxX * 16.0F);
        float uMinZ = sprite.getInterpolatedU(zOutside ? 0.0F : minZ * 16.0F);
        float uMaxZ = sprite.getInterpolatedU(zOutside ? 16.0F : maxZ * 16.0F);
        float uNorthAtMinX = sprite.getInterpolatedU(xOutside ? 16.0F : 16.0F - minX * 16.0F);
        float uNorthAtMaxX = sprite.getInterpolatedU(xOutside ? 0.0F : 16.0F - maxX * 16.0F);
        float uEastAtMinZ = sprite.getInterpolatedU(zOutside ? 16.0F : 16.0F - minZ * 16.0F);
        float uEastAtMaxZ = sprite.getInterpolatedU(zOutside ? 0.0F : 16.0F - maxZ * 16.0F);
        float vDownAtMaxZ = sprite.getInterpolatedV(zOutside ? 16.0F : maxZ * 16.0F);
        float vDownAtMinZ = sprite.getInterpolatedV(zOutside ? 0.0F : minZ * 16.0F);
        float vUpMin = sprite.getInterpolatedV(zOutside ? 0.0F : minZ * 16.0F);
        float vUpMax = sprite.getInterpolatedV(zOutside ? 16.0F : maxZ * 16.0F);
        float vSideMin = sprite.getInterpolatedV(yOutside ? 0.0F : 16.0F - maxY * 16.0F);
        float vSideMax = sprite.getInterpolatedV(yOutside ? 16.0F : 16.0F - minY * 16.0F);

        vertex(buffer, minX, minY, maxZ, uMinX, vDownAtMaxZ, r, g, b, a, 0.0F, -1.0F, 0.0F);
        vertex(buffer, minX, minY, minZ, uMinX, vDownAtMinZ, r, g, b, a, 0.0F, -1.0F, 0.0F);
        vertex(buffer, maxX, minY, minZ, uMaxX, vDownAtMinZ, r, g, b, a, 0.0F, -1.0F, 0.0F);
        vertex(buffer, maxX, minY, maxZ, uMaxX, vDownAtMaxZ, r, g, b, a, 0.0F, -1.0F, 0.0F);

        vertex(buffer, minX, maxY, minZ, uMinX, vUpMin, r, g, b, a, 0.0F, 1.0F, 0.0F);
        vertex(buffer, minX, maxY, maxZ, uMinX, vUpMax, r, g, b, a, 0.0F, 1.0F, 0.0F);
        vertex(buffer, maxX, maxY, maxZ, uMaxX, vUpMax, r, g, b, a, 0.0F, 1.0F, 0.0F);
        vertex(buffer, maxX, maxY, minZ, uMaxX, vUpMin, r, g, b, a, 0.0F, 1.0F, 0.0F);

        vertex(buffer, minX, maxY, minZ, uNorthAtMinX, vSideMin, r, g, b, a, 0.0F, 0.0F, -1.0F);
        vertex(buffer, maxX, maxY, minZ, uNorthAtMaxX, vSideMin, r, g, b, a, 0.0F, 0.0F, -1.0F);
        vertex(buffer, maxX, minY, minZ, uNorthAtMaxX, vSideMax, r, g, b, a, 0.0F, 0.0F, -1.0F);
        vertex(buffer, minX, minY, minZ, uNorthAtMinX, vSideMax, r, g, b, a, 0.0F, 0.0F, -1.0F);

        vertex(buffer, minX, maxY, maxZ, uMinX, vSideMin, r, g, b, a, 0.0F, 0.0F, 1.0F);
        vertex(buffer, minX, minY, maxZ, uMinX, vSideMax, r, g, b, a, 0.0F, 0.0F, 1.0F);
        vertex(buffer, maxX, minY, maxZ, uMaxX, vSideMax, r, g, b, a, 0.0F, 0.0F, 1.0F);
        vertex(buffer, maxX, maxY, maxZ, uMaxX, vSideMin, r, g, b, a, 0.0F, 0.0F, 1.0F);

        vertex(buffer, minX, maxY, maxZ, uMaxZ, vSideMin, r, g, b, a, -1.0F, 0.0F, 0.0F);
        vertex(buffer, minX, maxY, minZ, uMinZ, vSideMin, r, g, b, a, -1.0F, 0.0F, 0.0F);
        vertex(buffer, minX, minY, minZ, uMinZ, vSideMax, r, g, b, a, -1.0F, 0.0F, 0.0F);
        vertex(buffer, minX, minY, maxZ, uMaxZ, vSideMax, r, g, b, a, -1.0F, 0.0F, 0.0F);

        vertex(buffer, maxX, maxY, minZ, uEastAtMinZ, vSideMin, r, g, b, a, 1.0F, 0.0F, 0.0F);
        vertex(buffer, maxX, maxY, maxZ, uEastAtMaxZ, vSideMin, r, g, b, a, 1.0F, 0.0F, 0.0F);
        vertex(buffer, maxX, minY, maxZ, uEastAtMaxZ, vSideMax, r, g, b, a, 1.0F, 0.0F, 0.0F);
        vertex(buffer, maxX, minY, minZ, uEastAtMinZ, vSideMax, r, g, b, a, 1.0F, 0.0F, 0.0F);
    }

    private static void vertex(BufferBuilder buffer,
                               float x, float y, float z, float u, float v,
                               float r, float g, float b, float a,
                               float normalX, float normalY, float normalZ) {
        buffer.pos(x, y, z).tex(u, v).color(r, g, b, a)
                .normal(normalX, normalY, normalZ).endVertex();
    }

    @Nullable
    private static TextureAtlasSprite atlas(String name) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }
}
