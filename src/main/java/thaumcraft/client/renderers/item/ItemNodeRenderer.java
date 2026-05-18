package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
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
        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        TileNodeRenderer.renderNode(viewer, 64.0D, true, false, 1.0F,
                0.0D, 0.0D, 0.0D, partialTicks, node.getAspects(), node.getNodeType(), node.getNodeModifier());
    }
}
