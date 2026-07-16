package thaumcraft.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
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
    public void filledNormalAndVoidJarDropsRestoreEssentiaAndFilter() {
        BlockJarItem item = createItem();
        TileJarFillable normal = filledJar(new TileJarFillable());
        ItemStack normalDrop = BlockJar.createJarDrop(new ItemStack(item, 1, 0), normal);
        TileJarFillableVoid voidJar = filledJar(new TileJarFillableVoid());
        ItemStack voidDrop = BlockJar.createJarDrop(new ItemStack(item, 1, 3), voidJar);

        assertEquals(0, normalDrop.getItemDamage());
        assertFillableData(normalDrop, new TileJarFillable());
        assertEquals(3, voidDrop.getItemDamage());
        assertFillableData(voidDrop, new TileJarFillableVoid());
    }

    @Test
    public void filterOnlyJarDropDoesNotInventStoredEssentia() {
        BlockJarItem item = createItem();
        TileJarFillableVoid source = new TileJarFillableVoid();
        source.aspectFilter = Aspect.EARTH;
        ItemStack drop = BlockJar.createJarDrop(new ItemStack(item, 1, 3), source);
        TileJarFillableVoid jar = new TileJarFillableVoid();

        BlockJar.restoreFillableData(drop, jar);

        assertSame(Aspect.EARTH, jar.aspectFilter);
        assertNull(jar.aspect);
        assertEquals(0, jar.amount);
    }

    @Test
    public void nodeDropRestoresTaggedDataButLeavesRawMetaTwoEmpty() {
        BlockJarItem item = createItem();
        TileJarNode rawNode = new TileJarNode();

        BlockJar.restoreNodeData(new ItemStack(item, 1, 2), rawNode);

        assertEquals(0, rawNode.getAspects().size());
        assertSame(NodeType.NORMAL, rawNode.getNodeType());
        assertNull(rawNode.getNodeModifier());
        assertEquals("", rawNode.getId());

        TileJarNode source = new TileJarNode();
        source.setAspects(new AspectList().add(Aspect.MAGIC, 24).add(Aspect.AIR, 16));
        source.setNodeType(NodeType.PURE);
        source.setNodeModifier(NodeModifier.BRIGHT);
        source.setId("placed-node");
        ItemStack tagged = BlockJar.createJarDrop(new ItemStack(item, 1, 2), source);
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

    private static <T extends TileJarFillable> T filledJar(T jar) {
        jar.aspect = Aspect.AIR;
        jar.amount = 37;
        jar.aspectFilter = Aspect.FIRE;
        return jar;
    }

    private static BlockJarItem createItem() {
        return new BlockJarItem(Blocks.GLASS);
    }
}
