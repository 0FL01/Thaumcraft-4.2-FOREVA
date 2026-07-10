package thaumcraft.api.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Bootstrap;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.DummyInternalMethodHandler;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.common.tiles.TileMagicWorkbench;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcaneRecipeMatcherBehaviorTest {
    private static final Item ITEM_A = new Item();
    private static final Item ITEM_B = new Item();
    private static final Item ITEM_C = new Item();
    private static final Item ITEM_EXTRA = new Item();
    private static final Item ITEM_OUTPUT = new Item();
    private static final String TEST_ORE = "arcaneRecipeMatcherBehaviorOre";
    private static IInternalMethodHandler originalInternalMethods;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        OreDictionary.registerOre(TEST_ORE, new ItemStack(ITEM_A));
        originalInternalMethods = ThaumcraftApi.internalMethods;
        ThaumcraftApi.internalMethods = new DummyInternalMethodHandler() {
            @Override
            public ItemStack getStackInRowAndColumn(Object instance, int row, int column) {
                if (instance instanceof TileMagicWorkbench) {
                    return ((TileMagicWorkbench) instance).getStackInRowAndColumn(row, column);
                }
                if (instance instanceof IInventory) {
                    int slot = row + column * 3;
                    return ((IInventory) instance).getStackInSlot(slot);
                }
                return ItemStack.EMPTY;
            }
        };
    }

    @AfterClass
    public static void restoreInternalMethods() {
        ThaumcraftApi.internalMethods = originalInternalMethods;
    }

    @Test
    public void shapedArcaneRecipeShouldMatchBasePatternAndMirroredPatternByDefault() {
        ShapedArcaneRecipe recipe = new ShapedArcaneRecipe(
                "",
                new ItemStack(ITEM_OUTPUT),
                new AspectList(),
                "AB",
                " C",
                'A', new ItemStack(ITEM_A),
                'B', new ItemStack(ITEM_B),
                'C', new ItemStack(ITEM_C));

        TileMagicWorkbench base = new TileMagicWorkbench();
        setGrid(base, 0, 0, new ItemStack(ITEM_A));
        setGrid(base, 1, 0, new ItemStack(ITEM_B));
        setGrid(base, 1, 1, new ItemStack(ITEM_C));
        assertTrue("Base non-mirrored shape should match", recipe.matches(base, null, null));

        TileMagicWorkbench mirrored = new TileMagicWorkbench();
        setGrid(mirrored, 0, 0, new ItemStack(ITEM_B));
        setGrid(mirrored, 1, 0, new ItemStack(ITEM_A));
        setGrid(mirrored, 0, 1, new ItemStack(ITEM_C));
        assertTrue("Mirrored shape should match when mirrored mode is enabled", recipe.matches(mirrored, null, null));
    }

    @Test
    public void shapedArcaneRecipeShouldRejectMirroredPatternWhenMirrorDisabled() {
        ShapedArcaneRecipe recipe = new ShapedArcaneRecipe(
                "",
                new ItemStack(ITEM_OUTPUT),
                new AspectList(),
                "AB",
                " C",
                'A', new ItemStack(ITEM_A),
                'B', new ItemStack(ITEM_B),
                'C', new ItemStack(ITEM_C)).setMirrored(false);

        TileMagicWorkbench mirrored = new TileMagicWorkbench();
        setGrid(mirrored, 0, 0, new ItemStack(ITEM_B));
        setGrid(mirrored, 1, 0, new ItemStack(ITEM_A));
        setGrid(mirrored, 0, 1, new ItemStack(ITEM_C));
        assertFalse("Mirrored shape should fail when mirrored mode is disabled", recipe.matches(mirrored, null, null));
    }

    @Test
    public void shapelessArcaneRecipeShouldMatchOrderIndependentlyAndRejectExtraInputs() {
        ShapelessArcaneRecipe recipe = new ShapelessArcaneRecipe(
                "",
                new ItemStack(ITEM_OUTPUT),
                new AspectList(),
                new ItemStack(ITEM_A),
                new ItemStack(ITEM_B),
                new ItemStack(ITEM_C));

        TileMagicWorkbench valid = new TileMagicWorkbench();
        setGrid(valid, 2, 0, new ItemStack(ITEM_C));
        setGrid(valid, 0, 2, new ItemStack(ITEM_A));
        setGrid(valid, 1, 1, new ItemStack(ITEM_B));
        assertTrue("Shapeless recipe should match regardless of slot order", recipe.matches(valid, null, null));

        TileMagicWorkbench invalid = new TileMagicWorkbench();
        setGrid(invalid, 2, 0, new ItemStack(ITEM_C));
        setGrid(invalid, 0, 2, new ItemStack(ITEM_A));
        setGrid(invalid, 1, 1, new ItemStack(ITEM_B));
        setGrid(invalid, 0, 0, new ItemStack(ITEM_EXTRA));
        assertFalse("Shapeless recipe should reject unmatched extra ingredients", recipe.matches(invalid, null, null));
    }

    @Test
    public void shapedOreRecipeShouldMatchOreEntryAndRejectEmptyOrUnrelatedInputs() {
        ShapedArcaneRecipe recipe = new ShapedArcaneRecipe(
                "", new ItemStack(ITEM_OUTPUT), new AspectList(),
                "O",
                'O', TEST_ORE);

        TileMagicWorkbench valid = new TileMagicWorkbench();
        setGrid(valid, 0, 0, new ItemStack(ITEM_A));
        assertTrue("Shaped ore recipe should accept a registered ore entry", recipe.matches(valid, null, null));

        assertFalse("Shaped ore recipe should not treat an empty slot as an ore entry",
                recipe.matches(new TileMagicWorkbench(), null, null));

        TileMagicWorkbench unrelated = new TileMagicWorkbench();
        setGrid(unrelated, 0, 0, new ItemStack(ITEM_C));
        assertFalse("Shaped ore recipe should not accept an arbitrary item", recipe.matches(unrelated, null, null));
    }

    @Test
    public void shapelessOreRecipeShouldConsumeEachListRequirementExactlyOnce() {
        ShapelessArcaneRecipe recipe = new ShapelessArcaneRecipe(
                "", new ItemStack(ITEM_OUTPUT), new AspectList(),
                TEST_ORE, new ItemStack(ITEM_B));

        TileMagicWorkbench valid = new TileMagicWorkbench();
        setGrid(valid, 0, 0, new ItemStack(ITEM_B));
        setGrid(valid, 2, 2, new ItemStack(ITEM_A));
        assertTrue("Shapeless ore recipe should consume stack and ore-list requirements in slot order",
                recipe.matches(valid, null, null));

        TileMagicWorkbench missingOre = new TileMagicWorkbench();
        setGrid(missingOre, 0, 0, new ItemStack(ITEM_B));
        assertFalse("Shapeless ore recipe should retain an unmatched ore-list requirement",
                recipe.matches(missingOre, null, null));
    }

    private static void setGrid(TileMagicWorkbench table, int x, int y, ItemStack stack) {
        table.setInventorySlotContents(x + y * 3, stack);
    }
}
