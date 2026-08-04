package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CrucibleRecipeOreDictionaryRuntimeTest {
    private static final String TEST_ORE = "tcAlchemyCrucibleCatalyst";

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        OreDictionary.registerOre(TEST_ORE, new ItemStack(Items.GLOWSTONE_DUST));
    }

    @Test
    public void oreDictionaryCatalystsUseForgeListAndMatchOnlyRegisteredItems() {
        CrucibleRecipe recipe = new CrucibleRecipe("NITOR", new ItemStack(Items.NETHER_STAR), TEST_ORE,
                new AspectList().add(Aspect.FIRE, 3));
        AspectList available = new AspectList().add(Aspect.FIRE, 3);

        assertTrue(recipe.catalyst instanceof List);
        assertFalse("Forge 1.12 OreDictionary lists are not the TC4 ArrayList implementation",
                recipe.catalyst instanceof ArrayList);
        assertTrue(recipe.matches(available, new ItemStack(Items.GLOWSTONE_DUST)));
        assertTrue(recipe.catalystMatches(new ItemStack(Items.GLOWSTONE_DUST)));
        assertFalse(recipe.matches(available, new ItemStack(Items.DIAMOND)));
        assertFalse(recipe.catalystMatches(new ItemStack(Items.DIAMOND)));
    }
}
