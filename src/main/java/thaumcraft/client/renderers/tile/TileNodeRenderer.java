package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileNode;

public class TileNodeRenderer extends TileEntitySpecialRenderer<TileEntity> {

    private static final ResourceLocation NODES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/nodes.png");

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!(tile instanceof INode) || tile.getWorld() == null) {
            return;
        }
        float size = tile instanceof TileJarNode ? 0.7F : 1.0F;
        renderNodeAt((INode) tile, x + 0.5D, y + 0.5D, z + 0.5D, partialTicks, size);
    }

    public static void renderNodeAt(INode node, double x, double y, double z, float partialTicks, float size) {
        Aspect[] aspects = node.getAspects() == null ? null : node.getAspects().getAspects();
        float ticks = (net.minecraft.client.Minecraft.getMinecraft().player == null
                ? 0.0F
                : net.minecraft.client.Minecraft.getMinecraft().player.ticksExisted)
                + partialTicks
                + (node.getId() == null ? 0 : node.getId().hashCode() & 0xFF);

        float modifierScale = 1.0F;
        NodeModifier modifier = node.getNodeModifier();
        if (modifier == NodeModifier.BRIGHT) modifierScale = 1.2F;
        if (modifier == NodeModifier.PALE) modifierScale = 0.8F;
        if (modifier == NodeModifier.FADING) modifierScale = 0.65F;

        int baseColor = colorByType(node.getNodeType());
        float baseAlpha = 0.45F * modifierScale;
        float half = 0.18F * size * modifierScale;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindNodeTexture();
        GlStateManager.pushMatrix();
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(half, ((int) (baseAlpha * 255.0F) << 24) | (baseColor & 0x00FFFFFF),
                0.0F, 0.5F, 0.0F, 0.5F);
        GlStateManager.rotate((ticks * 7.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(half * 1.25F, ((int) (baseAlpha * 180.0F) << 24) | (baseColor & 0x00FFFFFF),
                0.5F, 1.0F, 0.0F, 0.5F);
        GlStateManager.popMatrix();

        if (aspects != null && aspects.length > 0) {
            int count = 0;
            for (Aspect aspect : aspects) {
                if (aspect == null || count >= 6) continue;
                float angle = (ticks * (2.0F + count * 0.25F) + count * 60.0F) % 360.0F;
                float radius = 0.12F * size + count * 0.01F;
                double ox = Math.cos(Math.toRadians(angle)) * radius;
                double oy = Math.sin(Math.toRadians(angle * 1.4F)) * (0.04F * size);
                double oz = Math.sin(Math.toRadians(angle)) * radius;

                net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(aspect.getImage());
                GlStateManager.pushMatrix();
                GlStateManager.translate(ox, oy, oz);
                TileRenderHelper.orientBillboardToPlayer();
                int color = (0xCC << 24) | (aspect.getColor() & 0x00FFFFFF);
                TileRenderHelper.drawTexturedQuad(0.05F * size, color, 0.0F, 1.0F, 0.0F, 1.0F);
                GlStateManager.popMatrix();
                count++;
                bindNodeTexture();
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void bindNodeTexture() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(NODES_TEXTURE);
    }

    private static int colorByType(NodeType type) {
        if (type == null) return 0xFFFFFF;
        switch (type) {
            case DARK:
                return 0x8050A0;
            case PURE:
                return 0xC8FFD8;
            case HUNGRY:
                return 0xB05050;
            case TAINTED:
                return 0xA000A0;
            case UNSTABLE:
                return 0x99CCFF;
            case NORMAL:
            default:
                return 0xFFFFFF;
        }
    }
}
