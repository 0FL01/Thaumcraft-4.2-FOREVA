package thaumcraft.common.config;

import org.junit.Test;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ConfigRecipesDynamicArcaneRegistrationBehaviorTest {

    @Test
    public void dynamicArcaneRecipesShouldFollowJarVoidOnceWithoutRemovingAddonRecipes() {
        Object addonBefore = new Object();
        AddonArcaneWandRecipe addonSubclass = new AddonArcaneWandRecipe();
        Object jarVoid = new Object();
        Object staticRecipeAfterJar = new Object();
        List<Object> recipes = new ArrayList<>();
        recipes.add(addonBefore);
        recipes.add(addonSubclass);
        recipes.add(jarVoid);
        recipes.add(staticRecipeAfterJar);

        ConfigRecipes.insertDynamicArcaneRecipes(recipes, jarVoid);

        assertEquals(6, recipes.size());
        assertSame(addonBefore, recipes.get(0));
        assertSame(addonSubclass, recipes.get(1));
        assertSame(jarVoid, recipes.get(2));
        assertEquals(ArcaneWandRecipe.class, recipes.get(3).getClass());
        assertEquals(ArcaneSceptreRecipe.class, recipes.get(4).getClass());
        assertSame(staticRecipeAfterJar, recipes.get(5));

        Object wand = recipes.get(3);
        Object sceptre = recipes.get(4);
        ConfigRecipes.insertDynamicArcaneRecipes(recipes, jarVoid);

        assertEquals(6, recipes.size());
        assertSame(wand, recipes.get(3));
        assertSame(sceptre, recipes.get(4));
        assertSame(addonSubclass, recipes.get(1));
    }

    private static class AddonArcaneWandRecipe extends ArcaneWandRecipe {}
}
