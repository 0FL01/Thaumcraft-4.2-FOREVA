package thaumcraft.common.config;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.items.armor.RecipesRobeArmorDyes;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.items.armor.RecipesVoidRobeArmorDyes;

public class ConfigRecipes {
    private static boolean specialRecipesRegistered = false;

    public static void init() {
        boolean hasRunicAugment = false;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof InfusionRunicAugmentRecipe) {
                hasRunicAugment = true;
                break;
            }
        }
        if (!hasRunicAugment) {
            ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());
        }
    }

    public static void oreDictRecipe(Object input, Object[] output) {
        // Stage 9: ore dictionary recipe registration.
    }

    public static void registerSpecialRecipes(IForgeRegistry<IRecipe> registry) {
        if (specialRecipesRegistered) {
            return;
        }
        registry.register(new RecipesRobeArmorDyes().setRegistryName("forge", "robearmordye"));
        registry.register(new RecipesVoidRobeArmorDyes().setRegistryName("forge", "voidrobearmordye"));
        specialRecipesRegistered = true;
    }
}
