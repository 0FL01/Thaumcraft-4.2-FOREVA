package thaumcraft.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;
import thaumcraft.common.tiles.TileJarNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class BlockJarPlacementDataTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void filledNormalAndVoidJarsRestoreEssentiaAndFilter() {
        BlockJarItem item = createItem();
        ItemStack stack = new ItemStack(item, 1, 0);
        item.setAspects(stack, new AspectList().add(Aspect.AIR, 37));
        stack.getTagCompound().setString("AspectFilter", Aspect.FIRE.getTag());

        assertFillableData(stack, new TileJarFillable());
        assertFillableData(stack, new TileJarFillableVoid());
    }

    @Test
    public void filterOnlyJarDoesNotInventStoredEssentia() {
        BlockJarItem item = createItem();
        ItemStack stack = new ItemStack(item, 1, 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AspectFilter", Aspect.EARTH.getTag());
        stack.setTagCompound(tag);
        TileJarFillableVoid jar = new TileJarFillableVoid();

        BlockJar.restoreFillableData(stack, jar);

        assertSame(Aspect.EARTH, jar.aspectFilter);
        assertNull(jar.aspect);
        assertEquals(0, jar.amount);
    }

    @Test
    public void nodePlacementRestoresTaggedDataButLeavesRawMetaTwoEmpty() {
        BlockJarItem item = createItem();
        TileJarNode rawNode = new TileJarNode();

        BlockJar.restoreNodeData(new ItemStack(item, 1, 2), rawNode);

        assertEquals(0, rawNode.getAspects().size());
        assertSame(NodeType.NORMAL, rawNode.getNodeType());
        assertNull(rawNode.getNodeModifier());
        assertEquals("", rawNode.getId());

        ItemStack tagged = new ItemStack(item, 1, 2);
        item.setAspects(tagged, new AspectList().add(Aspect.MAGIC, 24).add(Aspect.AIR, 16));
        item.setNodeAttributes(tagged, NodeType.PURE, NodeModifier.BRIGHT, "placed-node");
        TileJarNode restoredNode = new TileJarNode();

        BlockJar.restoreNodeData(tagged, restoredNode);

        assertEquals(24, restoredNode.getAspects().getAmount(Aspect.MAGIC));
        assertEquals(16, restoredNode.getAspects().getAmount(Aspect.AIR));
        assertSame(NodeType.PURE, restoredNode.getNodeType());
        assertSame(NodeModifier.BRIGHT, restoredNode.getNodeModifier());
        assertEquals("placed-node", restoredNode.getId());
    }

    private static void assertFillableData(ItemStack stack, TileJarFillable jar) {
        BlockJar.restoreFillableData(stack, jar);
        assertSame(Aspect.AIR, jar.aspect);
        assertEquals(37, jar.amount);
        assertSame(Aspect.FIRE, jar.aspectFilter);
    }

    private static BlockJarItem createItem() {
        return new BlockJarItem(Blocks.GLASS);
    }
}
