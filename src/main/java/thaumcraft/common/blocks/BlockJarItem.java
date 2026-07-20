package thaumcraft.common.blocks;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;

public class BlockJarItem
extends ItemBlock
implements IEssentiaContainerItem {

    private static final String ASPECT_FILTER_KEY = "AspectFilter";
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(
                Thaumcraft.proxy == null ? null : Thaumcraft.proxy.getClientPlayer());
        this.addJarInformation(stack, tooltip, knowledge);
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    void addJarInformation(ItemStack stack, List<String> tooltip, @Nullable IPlayerKnowledge knowledge) {
        if (stack.getItemDamage() == 2) {
            NodeType type = this.getNodeType(stack);
            if (type != null) {
                String description = I18n.translateToLocal("nodetype." + type.name() + ".name");
                NodeModifier modifier = this.getNodeModifier(stack);
                if (modifier != null) {
                    description += ", " + I18n.translateToLocal("nodemod." + modifier.name() + ".name");
                }
                tooltip.add(TextFormatting.BLUE + description);
            }
        }

        AspectList aspects = this.getAspects(stack);
        if (aspects != null && aspects.size() > 0) {
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (aspect == null) continue;
                if (knowledge != null && knowledge.hasDiscoveredAspect(aspect)) {
                    tooltip.add(aspect.getName() + " x" + aspects.getAmount(aspect));
                } else {
                    tooltip.add(new TextComponentTranslation("tc.aspect.unknown").getFormattedText());
                }
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ASPECT_FILTER_KEY)) {
            Aspect filter = Aspect.getAspect(stack.getTagCompound().getString(ASPECT_FILTER_KEY));
            if (filter != null) {
                String filterName = knowledge != null && knowledge.hasDiscoveredAspect(filter)
                        ? filter.getName()
                        : new TextComponentTranslation("tc.aspect.unknown").getFormattedText();
                tooltip.add(TextFormatting.DARK_PURPLE + filterName);
            }
        }
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
