package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class BlockJarItem
extends ItemBlock
implements IEssentiaContainerItem {

    private static final String NODE_TYPE_KEY = "nodetype";
    private static final String NODE_MOD_KEY = "nodemod";
    private static final String NODE_ID_KEY = "nodeid";

    public BlockJarItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return aspects;
        }
        return null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }

    public void setNodeAttributes(ItemStack itemstack, NodeType type, NodeModifier mod, String id) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        NodeType safeType = type == null ? NodeType.NORMAL : type;
        itemstack.getTagCompound().setInteger(NODE_TYPE_KEY, safeType.ordinal());
        if (mod != null) {
            itemstack.getTagCompound().setInteger(NODE_MOD_KEY, mod.ordinal());
        } else {
            itemstack.getTagCompound().removeTag(NODE_MOD_KEY);
        }
        itemstack.getTagCompound().setString(NODE_ID_KEY, id == null ? "0" : id);
    }

    public NodeType getNodeType(ItemStack itemstack) {
        if (!itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey(NODE_TYPE_KEY)) {
            return null;
        }
        int ordinal = itemstack.getTagCompound().getInteger(NODE_TYPE_KEY);
        NodeType[] values = NodeType.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return null;
        }
        return values[ordinal];
    }

    public NodeModifier getNodeModifier(ItemStack itemstack) {
        if (!itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey(NODE_MOD_KEY)) {
            return null;
        }
        int ordinal = itemstack.getTagCompound().getInteger(NODE_MOD_KEY);
        NodeModifier[] values = NodeModifier.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return null;
        }
        return values[ordinal];
    }

    public String getNodeId(ItemStack itemstack) {
        if (!itemstack.hasTagCompound() || !itemstack.getTagCompound().hasKey(NODE_ID_KEY)) {
            return "0";
        }
        return itemstack.getTagCompound().getString(NODE_ID_KEY);
    }
}
