package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.renderers.tile.TileJarRenderer;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarBrain;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;
import thaumcraft.common.tiles.TileJarNode;

public class ItemJarRenderer extends TileEntityItemStackRenderer {

    private final TileJarRenderer renderer = new TileJarRenderer();

    public ItemJarRenderer() {
        renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        TileJar tile = createTile(stack);
        if (tile == null) {
            return;
        }
        GlStateManager.pushMatrix();
        try {
            // Forge supplies the outer -0.5 TEISR translation; the tile renderer keeps its normal block origin.
            renderer.render(tile, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static TileJar createTile(ItemStack stack) {
        int meta = stack.getMetadata();
        if (meta == 1) {
            return new TileJarBrain();
        }
        if (meta == 2) {
            TileJarNode jarNode = new TileJarNode();
            applyNodeData(stack, jarNode);
            return jarNode;
        }
        if (meta == 3) {
            TileJarFillableVoid jarVoid = new TileJarFillableVoid();
            applyFillableData(stack, jarVoid);
            return jarVoid;
        }
        TileJarFillable jar = new TileJarFillable();
        applyFillableData(stack, jar);
        return jar;
    }

    private static void applyFillableData(ItemStack stack, TileJarFillable jar) {
        jar.facing = 5;
        if (stack.getItem() instanceof IEssentiaContainerItem) {
            AspectList aspects = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
            if (aspects != null && aspects.size() == 1) {
                Aspect aspect = aspects.getAspects()[0];
                if (aspect != null) {
                    jar.aspect = aspect;
                    jar.amount = aspects.getAmount(aspect);
                }
            }
        }
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("AspectFilter")) {
            return;
        }
        Aspect filter = Aspect.getAspect(nbt.getString("AspectFilter"));
        if (filter != null) {
            jar.aspectFilter = filter;
        }
    }

    private static void applyNodeData(ItemStack stack, TileJarNode jarNode) {
        AspectList aspects = null;
        if (stack.getItem() instanceof IEssentiaContainerItem) {
            aspects = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
        }
        if (aspects != null && aspects.size() > 0) {
            jarNode.setAspects(aspects);
        }
        if (stack.getItem() instanceof BlockJarItem) {
            BlockJarItem item = (BlockJarItem) stack.getItem();
            NodeType type = item.getNodeType(stack);
            if (type != null) {
                jarNode.setNodeType(type);
            }
            NodeModifier modifier = item.getNodeModifier(stack);
            jarNode.setNodeModifier(modifier);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("nodeid")) {
                String id = item.getNodeId(stack);
                if (id != null && !id.isEmpty()) {
                    jarNode.setId(id);
                }
            }
        } else if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null) {
                if (nbt.hasKey("nodetype")) {
                    int ordinal = nbt.getInteger("nodetype");
                    NodeType[] values = NodeType.values();
                    if (ordinal >= 0 && ordinal < values.length) {
                        jarNode.setNodeType(values[ordinal]);
                    }
                }
                if (nbt.hasKey("nodemod")) {
                    int ordinal = nbt.getInteger("nodemod");
                    NodeModifier[] values = NodeModifier.values();
                    if (ordinal >= 0 && ordinal < values.length) {
                        jarNode.setNodeModifier(values[ordinal]);
                    }
                }
                if (nbt.hasKey("nodeid")) {
                    String id = nbt.getString("nodeid");
                    if (id != null && !id.isEmpty()) {
                        jarNode.setId(id);
                    }
                }
            }
        }
    }
}
