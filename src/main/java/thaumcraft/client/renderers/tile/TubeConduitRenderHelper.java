package thaumcraft.client.renderers.tile;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public final class TubeConduitRenderHelper {
    private static final float ARM_MIN = 7.0F / 16.0F;
    private static final float ARM_MAX = 9.0F / 16.0F;
    private static final float EXTENSION = 2.0F / 16.0F;

    private TubeConduitRenderHelper() {}

    static void renderConduit(TileEntity tile,
                              IEssentiaTransport transport,
                              boolean[] openSides,
                              String armTexture,
                              @Nullable Aspect filterAspect,
                              double x, double y, double z) {
        if (tile == null || tile.getWorld() == null || tile.getPos() == null) {
            return;
        }
        TextureAtlasSprite arm = atlas(armTexture);
        if (arm == null) {
            return;
        }
        TextureAtlasSprite filterCore = filterAspect == null ? null : atlas("thaumcraft:blocks/pipe_filter_core");

        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (EnumFacing face : EnumFacing.VALUES) {
            if (!isConnected(tile, transport, openSides, face)) {
                continue;
            }
            drawArm(buf, tile, face, arm);
        }

        if (filterAspect != null && filterCore != null) {
            int color = 0xD0000000 | (filterAspect.getColor() & 0x00FFFFFF);
            TileRenderHelper.drawTexturedCuboid(buf,
                    5.5F / 16.0F, 5.5F / 16.0F, 5.5F / 16.0F,
                    10.5F / 16.0F, 10.5F / 16.0F, 10.5F / 16.0F,
                    filterCore, color);
        }

        tess.draw();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderInventoryShell(int meta) {
        String armTexture = meta == 4 ? "thaumcraft:blocks/pipe_buffer" : "thaumcraft:blocks/pipe_1";
        String centerTexture = null;
        String innerTexture = null;
        boolean wideCenter = false;

        switch (meta) {
            case 0:
            case 1:
                centerTexture = "thaumcraft:blocks/pipe_2";
                break;
            case 3:
                centerTexture = "thaumcraft:blocks/pipe_filter";
                innerTexture = "thaumcraft:blocks/pipe_filter_core";
                break;
            case 4:
                wideCenter = true;
                break;
            case 5:
                centerTexture = "thaumcraft:blocks/pipe_restrict";
                break;
            case 6:
                centerTexture = "thaumcraft:blocks/pipe_2";
                innerTexture = "thaumcraft:blocks/pipe_oneway";
                break;
            default:
                break;
        }

        TextureAtlasSprite arm = atlas(armTexture);
        TextureAtlasSprite center = centerTexture == null ? null : atlas(centerTexture);
        TextureAtlasSprite inner = innerTexture == null ? null : atlas(innerTexture);
        if (arm == null) {
            return;
        }

        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        TileRenderHelper.drawTexturedCuboid(buf, ARM_MIN, 0.0F, ARM_MIN, ARM_MAX, 1.0F, ARM_MAX, arm, 0xFFFFFFFF);

        if (wideCenter) {
            TileRenderHelper.drawTexturedCuboid(buf, 4.0F / 16.0F, 4.0F / 16.0F, 4.0F / 16.0F,
                    12.0F / 16.0F, 12.0F / 16.0F, 12.0F / 16.0F, arm, 0xFFFFFFFF);
        } else if (center != null) {
            TileRenderHelper.drawTexturedCuboid(buf, 6.0F / 16.0F, 6.0F / 16.0F, 6.0F / 16.0F,
                    10.0F / 16.0F, 10.0F / 16.0F, 10.0F / 16.0F, center, 0xFFFFFFFF);
        }
        if (inner != null) {
            float innerMin = meta == 3 ? 5.5F / 16.0F : 6.9F / 16.0F;
            float innerMax = meta == 3 ? 10.5F / 16.0F : 9.1F / 16.0F;
            float innerYMin = meta == 3 ? 5.5F / 16.0F : 2.0F / 16.0F;
            float innerYMax = meta == 3 ? 10.5F / 16.0F : 14.0F / 16.0F;
            TileRenderHelper.drawTexturedCuboid(buf, innerMin, innerYMin, innerMin,
                    innerMax, innerYMax, innerMax, inner, 0xFFFFFFFF);
        }

        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static boolean isConnected(TileEntity tile, IEssentiaTransport transport, boolean[] openSides, EnumFacing face) {
        int index = face.getIndex();
        if (index < 0 || index >= openSides.length || !openSides[index] || !transport.isConnectable(face)) {
            return false;
        }
        return ThaumcraftApiHelper.getConnectableTile(
                tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), face) != null;
    }

    private static void drawArm(BufferBuilder buf, TileEntity tile, EnumFacing face, TextureAtlasSprite sprite) {
        float minX = ARM_MIN;
        float maxX = ARM_MAX;
        float minY = ARM_MIN;
        float maxY = ARM_MAX;
        float minZ = ARM_MIN;
        float maxZ = ARM_MAX;
        float extension = shouldExtend(tile, face) ? EXTENSION : 0.0F;

        switch (face) {
            case DOWN:
                minY = -extension;
                break;
            case UP:
                maxY = 1.0F + extension;
                break;
            case NORTH:
                minZ = -extension;
                break;
            case SOUTH:
                maxZ = 1.0F + extension;
                break;
            case WEST:
                minX = -extension;
                break;
            case EAST:
                maxX = 1.0F + extension;
                break;
            default:
                break;
        }

        TileRenderHelper.drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ, sprite, 0xFFFFFFFF);
    }

    private static boolean shouldExtend(TileEntity tile, EnumFacing face) {
        TileEntity neighbour = ThaumcraftApiHelper.getConnectableTile(
                tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), face);
        return neighbour instanceof IEssentiaTransport
                && ((IEssentiaTransport) neighbour).renderExtendedTube();
    }

    @Nullable
    private static TextureAtlasSprite atlas(String name) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }
}
