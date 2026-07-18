package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileMirror;
import thaumcraft.common.tiles.TileMirrorEssentia;

public class TileMirrorRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation TUNNEL = new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE = new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation MIRROR_PANE = new ResourceLocation("thaumcraft", "textures/blocks/mirrorpane.png");
    private static final ResourceLocation MIRROR_PANE_TRANS = new ResourceLocation("thaumcraft", "textures/blocks/mirrorpanetrans.png");

    private static final ResourceLocation MIRROR_FRAME = new ResourceLocation("thaumcraft", "blocks/mirrorframe");
    private static final ResourceLocation MIRROR_FRAME_ESS = new ResourceLocation("thaumcraft", "blocks/mirrorframe2");

    private static final float INSET = 0.1875F;
    private static final float FRAME_THICKNESS = 0.0625F;

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) return;

        EnumFacing facing = EnumFacing.byIndex(tile.getBlockMetadata() % 6);
        if (facing == null) facing = EnumFacing.NORTH;

        boolean linked = isLinked(tile);
        float instability = instabilityJitter(tile);
        if (linked && isVisible(tile)) {
            renderPortalLayers(facing, x, y, z, partialTicks);
            renderPane(facing, x, y, z, MIRROR_PANE_TRANS, 0.02F + instability);
        } else {
            renderPane(facing, x, y, z, MIRROR_PANE, 0.02F + instability);
        }
        renderFrame(facing, x, y, z, tile instanceof TileMirrorEssentia);
    }

    private void renderPortalLayers(EnumFacing facing, double x, double y, double z, float partialTicks) {
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        if (view == null) return;

        double viewX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
        double viewY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
        double viewZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
        float near = 0.01F;
        float far = 0.99F;
        float axisOffset = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? near : far;
        LayeredFieldPlaneHelper.renderLayeredFaceRect(
                facing.getOpposite(), x, y, z, axisOffset, true, 1.0F, viewX, viewY, viewZ,
                INSET, 1.0F - INSET, INSET, 1.0F - INSET);
    }

    private void renderPane(EnumFacing facing, double x, double y, double z, ResourceLocation tex, float offset) {
        bindTexture(tex);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableRescaleNormal();
        try {
            transformFromOrientation(x, y, z, facing.getIndex(), offset);
            drawUnitQuad(1.0F, 1.0F, 1.0F, 1.0F);
        } finally {
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void renderFrame(EnumFacing facing, double x, double y, double z, boolean essentiaFrame) {
        TextureAtlasSprite sprite = getFrameSprite(essentiaFrame);
        if (sprite == null) return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        transformFromOrientation(x, y, z, facing.getIndex(), 0.0F);
        ExtrudedSpriteRenderHelper.render(sprite, FRAME_THICKNESS);
        GlStateManager.popMatrix();
    }

    private static TextureAtlasSprite getFrameSprite(boolean essentiaFrame) {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        if (map == null) return null;
        return map.getAtlasSprite((essentiaFrame ? MIRROR_FRAME_ESS : MIRROR_FRAME).toString());
    }

    private static boolean isLinked(TileEntity tile) {
        if (tile instanceof TileMirror) return ((TileMirror) tile).linked;
        if (tile instanceof TileMirrorEssentia) return ((TileMirrorEssentia) tile).linked;
        return false;
    }

    private static float instabilityJitter(TileEntity tile) {
        if (!(tile instanceof TileMirror)) return 0.0F;
        TileMirror mirror = (TileMirror) tile;
        if (mirror.instability <= 0 || tile.getWorld() == null) return 0.0F;
        return tile.getWorld().rand.nextFloat() * (mirror.instability / 10000.0F);
    }

    private static boolean isVisible(TileEntity tile) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.getRenderViewEntity();
        if (player == null) return false;
        double dx = player.posX - (tile.getPos().getX() + 0.5D);
        double dy = player.posY + player.getEyeHeight() - (tile.getPos().getY() + 0.5D);
        double dz = player.posZ - (tile.getPos().getZ() + 0.5D);
        return (dx * dx + dy * dy + dz * dz) <= 64.0D;
    }

    private static void transformFromOrientation(double x, double y, double z, int orientation, float off) {
        if (orientation == 0) {
            GlStateManager.translate(x, y + 1.0D, z + 1.0D);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 2) {
            GlStateManager.translate(x, y, z + 1.0D);
        } else if (orientation == 3) {
            GlStateManager.translate(x + 1.0D, y, z);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.translate(x + 1.0D, y, z + 1.0D);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0F, 0.0F, -off);
    }

    private static void drawUnitQuad(float r, float g, float b, float a) {
        drawUnitQuad(0.0F, 0.0F, 1.0F, 1.0F, r, g, b, a);
    }

    private static void drawUnitQuad(float u0, float v0, float u1, float v1, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        buf.pos(0.0D, 1.0D, 0.0D).tex(u1, v0).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.pos(1.0D, 1.0D, 0.0D).tex(u0, v0).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.pos(1.0D, 0.0D, 0.0D).tex(u0, v1).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.pos(0.0D, 0.0D, 0.0D).tex(u1, v1).color(r, g, b, a).normal(0.0F, 0.0F, -1.0F).endVertex();
        tess.draw();
    }

    private static void v(BufferBuilder buf, double x, double y, double z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
