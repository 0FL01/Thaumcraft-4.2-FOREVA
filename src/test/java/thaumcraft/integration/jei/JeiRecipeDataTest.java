package thaumcraft.integration.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.client.integration.jei.JeiRecipeData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JeiRecipeDataTest {
    private static Item ITEM_A;
    private static Item ITEM_B;
    private static final Item ITEM_C = new Item();
    private static final Item OUTPUT = new Item();
    private static final Item WILDCARD = new Item().setHasSubtypes(true);
    private static final String FUZZY_ORE = "jeiRecipeDataFuzzyOre";

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        ITEM_A = Items.NETHER_STAR;
        ITEM_B = Items.DRAGON_BREATH;
        OreDictionary.registerOre(FUZZY_ORE, new ItemStack(ITEM_A));
        OreDictionary.registerOre(FUZZY_ORE, new ItemStack(ITEM_B));
    }

    @Test
    public void collectorPreservesOrderDuplicatesTopologyAndShapelessFlag() {
        AspectList sixAspects = new AspectList()
                .add(Aspect.AIR, 1).add(Aspect.EARTH, 2).add(Aspect.FIRE, 3)
                .add(Aspect.WATER, 4).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 6);
        ShapedArcaneRecipe first = new ShapedArcaneRecipe("FIRST", new ItemStack(OUTPUT), sixAspects,
                "A ", " B", 'A', new ItemStack(ITEM_A), 'B', new ItemStack(ITEM_B));
        ShapelessArcaneRecipe second = new ShapelessArcaneRecipe("SECOND", new ItemStack(OUTPUT),
                new AspectList(), new ItemStack(ITEM_A), new ItemStack(ITEM_B));

        JeiRecipeData.Collection data = JeiRecipeData.collect(Arrays.asList(first, second), exactExpander());

        assertEquals(2, data.arcane.size());
        assertEquals("FIRST", data.arcane.get(0).research);
        assertEquals("SECOND", data.arcane.get(1).research);
        assertEquals(4, data.arcane.get(0).inputs.size());
        assertTrue(data.arcane.get(0).inputs.get(1).isEmpty());
        assertFalse(data.arcane.get(0).shapeless);
        assertTrue(data.arcane.get(1).shapeless);
        assertEquals(6, data.arcane.get(0).aspects.size());
    }

    @Test
    public void alternativesFollowEachRecipeMatchersSemantics() {
        ShapedArcaneRecipe arcane = new ShapedArcaneRecipe("", new ItemStack(OUTPUT), new AspectList(),
                "A", 'A', new ItemStack(ITEM_A));
        CrucibleRecipe crucible = new CrucibleRecipe("", new ItemStack(OUTPUT), new ItemStack(ITEM_A),
                new AspectList().add(Aspect.AIR, 1));
        InfusionRecipe infusion = new InfusionRecipe("", new ItemStack(OUTPUT), 0, new AspectList(),
                new ItemStack(ITEM_A), new ItemStack[]{new ItemStack(ITEM_A)});

        JeiRecipeData.Collection data = JeiRecipeData.collect(Arrays.asList(arcane, crucible, infusion), exactExpander());

        assertTrue(OreDictionary.getOreIDs(new ItemStack(ITEM_A)).length > 0);
        assertTrue(InfusionRecipe.areItemStacksEqual(new ItemStack(ITEM_B), new ItemStack(ITEM_A), true));
        assertEquals(1, data.arcane.get(0).inputs.get(0).size());
        assertSame(ITEM_A, data.arcane.get(0).inputs.get(0).get(0).getItem());
        assertEquals(1, data.crucible.get(0).catalyst.size());
        assertSame(ITEM_A, data.crucible.get(0).catalyst.get(0).getItem());
        assertContainsItems(data.infusion.get(0).central, ITEM_A, ITEM_B);
        assertContainsItems(data.infusion.get(0).components.get(0), ITEM_A, ITEM_B);
        assertEquals(1, data.infusion.get(0).outputs.size());
    }

    @Test
    public void wildcardNbtOverlayCreatesEveryOutputWithoutMutatingInputs() {
        ItemStack template = new ItemStack(WILDCARD, 1, OreDictionary.WILDCARD_VALUE);
        template.setTagInfo("required", new NBTTagString("yes"));
        ItemStack subtype0 = taggedSubtype(0, "zero");
        ItemStack subtype1 = taggedSubtype(1, "one");
        JeiRecipeData.StackExpander expander = stack -> Arrays.asList(subtype0, subtype1);
        InfusionRecipe recipe = new InfusionRecipe("NBT", new Object[]{"added", new NBTTagByte((byte) 7)},
                3, new AspectList(), template, new ItemStack[]{new ItemStack(ITEM_C)});

        JeiRecipeData.Collection data = JeiRecipeData.collect(Collections.singletonList(recipe), expander);

        assertEquals(1, data.infusion.size());
        assertEquals(2, data.infusion.get(0).outputs.size());
        for (int i = 0; i < 2; i++) {
            ItemStack output = data.infusion.get(0).outputs.get(i);
            assertEquals(i, output.getMetadata());
            assertEquals(i == 0 ? "zero" : "one", output.getTagCompound().getString("variant"));
            assertEquals("yes", output.getTagCompound().getString("required"));
            assertEquals(7, output.getTagCompound().getByte("added"));
        }
        assertFalse(subtype0.getTagCompound().hasKey("added"));
        assertFalse(subtype1.getTagCompound().hasKey("added"));
        assertFalse(template.getTagCompound().hasKey("added"));
    }

    @Test
    public void unknownRecipesAreReportedInsteadOfInventingAdapters() {
        Object unknown = new Object();
        JeiRecipeData.Collection data = JeiRecipeData.collect(Collections.singletonList(unknown), exactExpander());

        assertEquals(1, data.sourceCount);
        assertEquals(1, data.getSkippedCount());
        assertEquals(Integer.valueOf(1), data.skipped.get(Object.class.getName()));
    }

    private static JeiRecipeData.StackExpander exactExpander() {
        return stack -> Collections.singletonList(stack.copy());
    }

    private static ItemStack taggedSubtype(int metadata, String variant) {
        ItemStack stack = new ItemStack(WILDCARD, 1, metadata);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("variant", variant);
        stack.setTagCompound(tag);
        return stack;
    }

    private static void assertContainsItems(List<ItemStack> stacks, Item... expected) {
        for (Item item : expected) {
            boolean found = false;
            for (ItemStack stack : stacks) {
                if (stack.getItem() == item) {
                    found = true;
                    break;
                }
            }
            assertTrue("Missing alternative for " + item, found);
        }
    }
}
