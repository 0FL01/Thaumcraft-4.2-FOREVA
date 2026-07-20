package thaumcraft.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockJarItemTooltipTest {
    private CommonProxy oldProxy;

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Before
    public void rememberProxy() {
        this.oldProxy = Thaumcraft.proxy;
    }

    @After
    public void restoreProxy() {
        Thaumcraft.proxy = this.oldProxy;
    }

    @Test
    public void addInformationShowsStoredEssentiaWithoutShiftDependency() {
        Thaumcraft.proxy = null;
        BlockJarItem item = createItem();
        ItemStack stack = new ItemStack(item, 1, 0);
        item.setAspects(stack, new AspectList().add(Aspect.ENERGY, 37));
        List<String> tooltip = new ArrayList<>();

        item.addInformation(stack, null, tooltip, ITooltipFlag.TooltipFlags.NORMAL);

        assertEquals(Collections.singletonList(
                new TextComponentTranslation("tc.aspect.unknown").getFormattedText()), tooltip);
    }

    @Test
    public void knownEssentiaAndFilterUseOriginalTooltipFormat() {
        BlockJarItem item = createItem();
        ItemStack stack = new ItemStack(item, 1, 3);
        item.setAspects(stack, new AspectList().add(Aspect.ENERGY, 37));
        stack.getTagCompound().setString("AspectFilter", Aspect.FIRE.getTag());
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.addDiscoveredAspect(Aspect.ENERGY.getTag());
        knowledge.addDiscoveredAspect(Aspect.FIRE.getTag());
        List<String> tooltip = new ArrayList<>();

        item.addJarInformation(stack, tooltip, knowledge);

        assertEquals(Arrays.asList(
                Aspect.ENERGY.getName() + " x37",
                TextFormatting.DARK_PURPLE + Aspect.FIRE.getName()), tooltip);
    }

    @Test
    public void filterOnlyJarDoesNotInventStoredEssentiaAndEmptyJarAddsNothing() {
        BlockJarItem item = createItem();
        ItemStack filtered = new ItemStack(item, 1, 0);
        filtered.setTagCompound(new NBTTagCompound());
        filtered.getTagCompound().setString("AspectFilter", Aspect.ENERGY.getTag());
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.addDiscoveredAspect(Aspect.ENERGY.getTag());
        List<String> filteredTooltip = new ArrayList<>();

        item.addJarInformation(filtered, filteredTooltip, knowledge);

        assertEquals(Collections.singletonList(TextFormatting.DARK_PURPLE + Aspect.ENERGY.getName()), filteredTooltip);

        List<String> emptyTooltip = new ArrayList<>();
        item.addJarInformation(new ItemStack(item, 1, 0), emptyTooltip, knowledge);
        assertTrue(emptyTooltip.isEmpty());
    }

    @Test
    public void nodeJarShowsLocalizedTypeAndModifierBeforeAspects() {
        BlockJarItem item = createItem();
        ItemStack stack = new ItemStack(item, 1, 2);
        item.setNodeAttributes(stack, NodeType.HUNGRY, NodeModifier.BRIGHT, "tooltip-node");
        item.setAspects(stack, new AspectList().add(Aspect.MAGIC, 24));
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.addDiscoveredAspect(Aspect.MAGIC.getTag());
        List<String> tooltip = new ArrayList<>();

        item.addJarInformation(stack, tooltip, knowledge);

        assertEquals(Arrays.asList(
                TextFormatting.BLUE + I18n.translateToLocal("nodetype.HUNGRY.name") + ", "
                        + I18n.translateToLocal("nodemod.BRIGHT.name"),
                Aspect.MAGIC.getName() + " x24"), tooltip);
    }

    @Test
    public void nodeJarWithoutModifierShowsOnlyItsLocalizedType() {
        BlockJarItem item = createItem();
        ItemStack pureNode = new ItemStack(item, 1, 2);
        item.setNodeAttributes(pureNode, NodeType.PURE, null, "pure-node");
        List<String> pureTooltip = new ArrayList<>();

        item.addJarInformation(pureNode, pureTooltip, null);

        assertEquals(Collections.singletonList(
                TextFormatting.BLUE + I18n.translateToLocal("nodetype.PURE.name")), pureTooltip);
    }

    private static BlockJarItem createItem() {
        return new BlockJarItem(Blocks.GLASS);
    }
}
