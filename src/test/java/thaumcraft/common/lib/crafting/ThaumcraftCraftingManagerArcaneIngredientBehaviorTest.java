package thaumcraft.common.lib.crafting;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ThaumcraftCraftingManagerArcaneIngredientBehaviorTest {
    private static final String TEST_ORE = "arcaneAspectGenerationBehaviorOre";
    private static final Item ORE_ITEM = new Item();

    private List<Object> oldRecipes;
    private Map<List, AspectList> oldObjectTags;
    private Map<List, int[]> oldGroupedObjectTags;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        OreDictionary.registerOre(TEST_ORE, new ItemStack(ORE_ITEM));
    }

    @Before
    public void saveApiState() {
        this.oldRecipes = new ArrayList<>(ThaumcraftApi.getCraftingRecipes());
        this.oldObjectTags = new HashMap<>(ThaumcraftApi.objectTags);
        this.oldGroupedObjectTags = new HashMap<>(ThaumcraftApi.groupedObjectTags);
        ThaumcraftApi.getCraftingRecipes().clear();
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.groupedObjectTags.clear();
        ThaumcraftApi.registerObjectTag(
                new ItemStack(ORE_ITEM, 1, OreDictionary.WILDCARD_VALUE),
                new AspectList().add(Aspect.AIR, 8));
    }

    @After
    public void restoreApiState() {
        ThaumcraftApi.getCraftingRecipes().clear();
        ThaumcraftApi.getCraftingRecipes().addAll(this.oldRecipes);
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.objectTags.putAll(this.oldObjectTags);
        ThaumcraftApi.groupedObjectTags.clear();
        ThaumcraftApi.groupedObjectTags.putAll(this.oldGroupedObjectTags);
    }

    @Test
    public void aspectGenerationShouldReadOreListsFromShapedAndShapelessArcaneRecipes() {
        Item shapedOutput = new Item();
        Item shapelessOutput = new Item();
        ThaumcraftApi.getCraftingRecipes().add(new ShapedArcaneRecipe(
                "", new ItemStack(shapedOutput), new AspectList(),
                "O",
                'O', TEST_ORE));
        ThaumcraftApi.getCraftingRecipes().add(new ShapelessArcaneRecipe(
                "", new ItemStack(shapelessOutput), new AspectList(),
                TEST_ORE));

        AspectList shapedTags = ThaumcraftCraftingManager.generateTags(shapedOutput, 0);
        AspectList shapelessTags = ThaumcraftCraftingManager.generateTags(shapelessOutput, 0);

        assertEquals(6, shapedTags.getAmount(Aspect.AIR));
        assertEquals(6, shapelessTags.getAmount(Aspect.AIR));
    }
}
