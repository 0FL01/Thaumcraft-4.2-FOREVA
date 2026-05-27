package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.renderers.tile.TileNodeRenderer;
import thaumcraft.common.tiles.TileNode;

public class ItemNodeRenderer extends TileEntityItemStackRenderer {

    private static final AspectList DEFAULT_ASPECTS = new AspectList()
            .add(Aspect.AIR, 40)
            .add(Aspect.FIRE, 40)
            .add(Aspect.EARTH, 40)
            .add(Aspect.WATER, 40);

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        if (meta != 0 && meta != 5) {
            return;
        }

        TileNode node = new TileNode();
        node.setAspects(DEFAULT_ASPECTS.copy());
        node.setNodeType(NodeType.NORMAL);
        node.setId("item");

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5D, 0.5D, 0.5D);
        GlStateManager.scale(2.0D, 2.0D, 2.0D);
        renderItemNode(node, partialTicks);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        renderItemNode(node, partialTicks);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        renderItemNode(node, partialTicks);
        GlStateManager.popMatrix();
    }

    public static void renderItemNode(INode node) {
        renderItemNode(node, 0.0F);
    }

    public static void renderItemNode(INode node, float partialTicks) {
        if (node == null) return;
        AspectList aspects = node.getAspects();
        if (aspects == null || aspects.size() <= 0) return;

        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        float ticks = viewer == null ? partialTicks : viewer.ticksExisted + partialTicks;
        float alpha = 0.5F;
        NodeModifier modifier = node.getNodeModifier();
        if (modifier == NodeModifier.BRIGHT) alpha *= 1.5F;
        else if (modifier == NodeModifier.PALE) alpha *= 0.66F;
        else if (modifier == NodeModifier.FADING) alpha *= MathHelper.sin(ticks / 3.0F) * 0.25F + 0.33F;

        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TileNodeRenderer.NODES_TEXTURE);

        long nano = System.nanoTime();
        int frames = 32;
        int frame = (int) ((nano / 40000000L + 1L) % frames);
        float bscale = 0.25F;
        int count = 0;
        float average = 0.0F;
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null) continue;
            float aspectAlpha = alpha;
            if (aspect.getBlend() == GL11.GL_ONE_MINUS_SRC_ALPHA) {
                aspectAlpha *= 1.5F;
            }
            average += aspects.getAmount(aspect);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, aspect.getBlend());
            float scale = MathHelper.sin(ticks / (14.0F - count)) * bscale + bscale * 2.0F;
            scale = 0.2F + scale * (aspects.getAmount(aspect) / 50.0F);
            drawAnimatedQuadStrip(scale, aspectAlpha / Math.max(1.0F, aspects.size()), frames, 0, frame, aspect.getColor());
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            count++;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int strip = 1;
        float scale = 0.1F + (average / aspects.size()) / 150.0F;
        NodeType type = node.getNodeType();
        if (type != null) {
            switch (type) {
                case NORMAL:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    break;
                case UNSTABLE:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 6;
                    break;
                case DARK:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    strip = 2;
                    break;
                case TAINTED:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    strip = 5;
                    break;
                case PURE:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 4;
                    break;
                case HUNGRY:
                    scale *= 0.75F;
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 3;
                    break;
                default:
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    break;
            }
        } else {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
        drawAnimatedQuadStrip(scale, alpha, frames, strip, frame, 0xFFFFFF);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private static void drawAnimatedQuadStrip(float scale, float alpha, int frames, int strip, int frame, int color) {
        float clampedAlpha = Math.max(0.0F, Math.min(1.0F, alpha));
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float u0 = frame / (float) frames;
        float u1 = (frame + 1) / (float) frames;
        float v0 = strip / (float) frames;
        float v1 = (strip + 1) / (float) frames;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.5D * scale, 0.5D * scale, 0.0D).tex(u0, v1).color(r, g, b, clampedAlpha).endVertex();
        buffer.pos(0.5D * scale, 0.5D * scale, 0.0D).tex(u1, v1).color(r, g, b, clampedAlpha).endVertex();
        buffer.pos(0.5D * scale, -0.5D * scale, 0.0D).tex(u1, v0).color(r, g, b, clampedAlpha).endVertex();
        buffer.pos(-0.5D * scale, -0.5D * scale, 0.0D).tex(u0, v0).color(r, g, b, clampedAlpha).endVertex();
        tessellator.draw();
    }
}
