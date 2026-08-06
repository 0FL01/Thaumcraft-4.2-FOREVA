package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ThaumcraftCraftingManager {

    public static AspectList getObjectTags(ItemStack is) {
        if (is == null || is.isEmpty()) return new AspectList();
        Item item = is.getItem();
        int meta = is.getMetadata();
        AspectList aspects = findRegisteredObjectTags(item, meta);
        aspects = aspects != null ? aspects.copy() : generateTags(item, meta);
        if (item instanceof ItemWandCasting) {
            if (aspects == null) aspects = new AspectList();
            thaumcraft.api.wands.WandRod rod = ItemWandCasting.getRod(is);
            thaumcraft.api.wands.WandCap cap = ItemWandCasting.getCap(is);
            int rodCost = rod != null ? rod.getCraftCost() : 0;
            int capCost = cap != null ? cap.getCraftCost() : 0;
            aspects.merge(Aspect.MAGIC, (rodCost + capCost) / 2);
            aspects.merge(Aspect.TOOL, (rodCost + capCost) / 3);
        }
        if (isPotionStack(is)) {
            if (aspects == null) aspects = new AspectList();
            addPotionAspects(is, aspects);
        }
        aspects = capAspects(aspects, 64);
        return aspects != null ? aspects : new AspectList();
    }

    public static AspectList getBonusTags(ItemStack is, AspectList ot) {
        AspectList bonus = new AspectList();
        if (is == null || is.isEmpty()) return bonus;
        Item item = is.getItem();
        if (item instanceof IEssentiaContainerItem) {
            AspectList stored = ((IEssentiaContainerItem)item).getAspects(is);
            if (stored != null) {
                for (Aspect aspect : stored.copy().getAspects()) {
                    if (aspect != null && stored.getAmount(aspect) > 0) {
                        bonus.add(aspect, stored.getAmount(aspect));
                    }
                }
            }
        }
        if (ot != null) {
            for (Aspect aspect : ot.getAspects()) {
                if (aspect != null) {
                    bonus.add(aspect, ot.getAmount(aspect));
                }
            }
        }
        if (item instanceof ItemArmor) {
            bonus.merge(Aspect.ARMOR, ((ItemArmor)item).damageReduceAmount);
        } else if (item instanceof ItemSword) {
            int damage = (int)(((ItemSword)item).getAttackDamage() + 1.0F);
            if (damage > 0) bonus.merge(Aspect.WEAPON, damage);
        } else if (item instanceof ItemBow) {
            bonus.merge(Aspect.WEAPON, 3).merge(Aspect.FLIGHT, 1);
        } else if (item instanceof ItemPickaxe) {
            mergeToolMaterialAspect(bonus, ((ItemTool)item).getToolMaterialName(), Aspect.MINE);
        } else if (item instanceof ItemTool) {
            mergeToolMaterialAspect(bonus, ((ItemTool)item).getToolMaterialName(), Aspect.TOOL);
        } else if (item instanceof ItemShears || item instanceof ItemHoe) {
            mergeHarvestAspect(bonus, item.getMaxDamage());
        }
        int totalEnchantments = 0;
        for (Map.Entry<Enchantment, Integer> enchantment : EnchantmentHelper.getEnchantments(is).entrySet()) {
            Enchantment ench = enchantment.getKey();
            int level = enchantment.getValue() == null ? 0 : enchantment.getValue();
            if (ench != null && level > 0) {
                mergeEnchantmentAspect(bonus, ench, level);
                totalEnchantments += level;
            }
        }
        if (totalEnchantments > 0) {
            bonus.merge(Aspect.MAGIC, totalEnchantments);
        }
        return ThaumcraftApiHelper.cullTags(bonus);
    }

    public static AspectList generateTags(Item item, int meta) {
        return generateTags(item, meta, new ArrayList<List>());
    }

    private static AspectList findRegisteredObjectTags(Item item, int meta) {
        AspectList aspects = ThaumcraftApi.objectTags.get(Arrays.asList(item, meta));
        if (aspects != null) return aspects;
        int[] grouped = ThaumcraftApi.groupedObjectTags.get(Arrays.asList(item, meta));
        if (grouped != null && grouped.length > 0) {
            aspects = ThaumcraftApi.objectTags.get(Arrays.asList(item, grouped[0]));
            if (aspects != null) return aspects;
        }
        for (Object keyObject : ThaumcraftApi.objectTags.keySet()) {
            if (!(keyObject instanceof List)) continue;
            List key = (List)keyObject;
            if (key.size() < 2 || key.get(0) != item || !(key.get(1) instanceof int[])) continue;
            int[] metas = ((int[])key.get(1)).clone();
            Arrays.sort(metas);
            if (Arrays.binarySearch(metas, meta) >= 0) {
                return ThaumcraftApi.objectTags.get(keyObject);
            }
        }
        aspects = ThaumcraftApi.objectTags.get(Arrays.asList(item, (int)Short.MAX_VALUE));
        if (aspects != null) return aspects;
        if (meta == Short.MAX_VALUE) {
            for (int i = 0; i < 16; ++i) {
                aspects = ThaumcraftApi.objectTags.get(Arrays.asList(item, i));
                if (aspects != null) return aspects;
                grouped = ThaumcraftApi.groupedObjectTags.get(Arrays.asList(item, i));
                if (grouped != null && grouped.length > 0) {
                    aspects = ThaumcraftApi.objectTags.get(Arrays.asList(item, grouped[0]));
                    if (aspects != null) return aspects;
                }
            }
        }
        return null;
    }

    private static AspectList generateTags(Item item, int meta, ArrayList<List> history) {
        if (item == null) return new AspectList();
        int lookupMeta = normalizeGeneratedMeta(item, meta);
        if (ThaumcraftApi.exists(item, lookupMeta)) {
            return getObjectTags(new ItemStack(item, 1, lookupMeta));
        }
        List key = Arrays.asList(item, lookupMeta);
        if (history.contains(key) || history.size() >= 100) {
            return null;
        }
        history.add(key);
        AspectList generated = generateTagsFromRecipes(item, lookupMeta == Short.MAX_VALUE ? 0 : meta, history);
        generated = capAspects(generated, 64);
        if (generated != null) {
            ThaumcraftApi.registerObjectTag(new ItemStack(item, 1, lookupMeta), generated);
            return generated;
        }
        return new AspectList();
    }

    private static int normalizeGeneratedMeta(Item item, int meta) {
        try {
            ItemStack stack = new ItemStack(item, 1, meta);
            if (!stack.getItem().getHasSubtypes() || stack.getItem().isDamageable()) {
                return Short.MAX_VALUE;
            }
        } catch (Exception ignored) {
        }
        return meta;
    }

    private static AspectList capAspects(AspectList aspects, int cap) {
        if (aspects == null) return null;
        AspectList capped = new AspectList();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) capped.merge(aspect, Math.min(cap, aspects.getAmount(aspect)));
        }
        return capped;
    }

    private static boolean isPotionStack(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.POTIONITEM || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.TIPPED_ARROW;
    }

    private static void addPotionAspects(ItemStack stack, AspectList aspects) {
        aspects.merge(Aspect.WATER, 1);
        if (stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION) {
            aspects.merge(Aspect.ENTROPY, 2);
        }
        for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
            int amount = effect.getAmplifier() + 1;
            aspects.merge(Aspect.MAGIC, amount * 2);
            mergePotionAspect(aspects, effect.getPotion(), amount);
        }
    }

    private static void mergePotionAspect(AspectList aspects, Potion potion, int amount) {
        if (potion == MobEffects.BLINDNESS) aspects.merge(Aspect.DARKNESS, amount * 3);
        else if (potion == MobEffects.NAUSEA) aspects.merge(Aspect.ELDRITCH, amount * 3);
        else if (potion == MobEffects.STRENGTH) aspects.merge(Aspect.WEAPON, amount * 3);
        else if (potion == MobEffects.MINING_FATIGUE || potion == MobEffects.SLOWNESS) aspects.merge(Aspect.TRAP, amount * 3);
        else if (potion == MobEffects.HASTE) aspects.merge(Aspect.TOOL, amount * 3);
        else if (potion == MobEffects.FIRE_RESISTANCE) aspects.merge(Aspect.ARMOR, amount).merge(Aspect.FIRE, amount * 2);
        else if (potion == MobEffects.INSTANT_DAMAGE || potion == MobEffects.HUNGER || potion == MobEffects.WEAKNESS) aspects.merge(Aspect.DEATH, amount * 3);
        else if (potion == MobEffects.INSTANT_HEALTH || potion == MobEffects.REGENERATION) aspects.merge(Aspect.HEAL, amount * 3);
        else if (potion == MobEffects.INVISIBILITY || potion == MobEffects.NIGHT_VISION) aspects.merge(Aspect.SENSES, amount * 3);
        else if (potion == MobEffects.JUMP_BOOST) aspects.merge(Aspect.FLIGHT, amount * 3);
        else if (potion == MobEffects.SPEED) aspects.merge(Aspect.MOTION, amount * 3);
        else if (potion == MobEffects.POISON) aspects.merge(Aspect.POISON, amount * 3);
        else if (potion == MobEffects.RESISTANCE) aspects.merge(Aspect.ARMOR, amount * 3);
        else if (potion == MobEffects.WATER_BREATHING) aspects.merge(Aspect.AIR, amount * 3);
    }

    private static void mergeToolMaterialAspect(AspectList aspects, String materialName, Aspect aspect) {
        for (Item.ToolMaterial material : Item.ToolMaterial.values()) {
            if (material.toString().equals(materialName)) {
                aspects.merge(aspect, material.getHarvestLevel() + 1);
            }
        }
    }

    private static void mergeHarvestAspect(AspectList aspects, int maxDamage) {
        if (maxDamage <= Item.ToolMaterial.WOOD.getMaxUses()) aspects.merge(Aspect.HARVEST, 1);
        else if (maxDamage <= Item.ToolMaterial.STONE.getMaxUses() || maxDamage <= Item.ToolMaterial.GOLD.getMaxUses()) aspects.merge(Aspect.HARVEST, 2);
        else if (maxDamage <= Item.ToolMaterial.IRON.getMaxUses()) aspects.merge(Aspect.HARVEST, 3);
        else aspects.merge(Aspect.HARVEST, 4);
    }

    private static void mergeEnchantmentAspect(AspectList aspects, Enchantment enchantment, int level) {
        if (enchantment == Enchantments.AQUA_AFFINITY) aspects.merge(Aspect.WATER, level);
        else if (enchantment == Enchantments.BANE_OF_ARTHROPODS || enchantment == Enchantments.LURE) aspects.merge(Aspect.BEAST, level);
        else if (enchantment == Enchantments.BLAST_PROTECTION || enchantment == Enchantments.FIRE_PROTECTION || enchantment == Enchantments.PROJECTILE_PROTECTION || enchantment == Enchantments.PROTECTION) aspects.merge(Aspect.ARMOR, level);
        else if (enchantment == Enchantments.EFFICIENCY || enchantment == Config.enchRepair) aspects.merge(Aspect.TOOL, level);
        else if (enchantment == Enchantments.FEATHER_FALLING) aspects.merge(Aspect.FLIGHT, level);
        else if (enchantment == Enchantments.FIRE_ASPECT || enchantment == Enchantments.FLAME) aspects.merge(Aspect.FIRE, level);
        else if (enchantment == Enchantments.FORTUNE || enchantment == Enchantments.LOOTING || enchantment == Enchantments.LUCK_OF_THE_SEA) aspects.merge(Aspect.GREED, level);
        else if (enchantment == Enchantments.INFINITY) aspects.merge(Aspect.CRAFT, level);
        else if (enchantment == Enchantments.KNOCKBACK || enchantment == Enchantments.PUNCH || enchantment == Enchantments.RESPIRATION || enchantment == Enchantments.DEPTH_STRIDER || enchantment == Enchantments.FROST_WALKER) aspects.merge(Aspect.AIR, level);
        else if (enchantment == Enchantments.POWER || enchantment == Enchantments.SHARPNESS || enchantment == Enchantments.SMITE) aspects.merge(Aspect.WEAPON, level);
        else if (enchantment == Enchantments.SILK_TOUCH) aspects.merge(Aspect.EXCHANGE, level);
        else if (enchantment == Enchantments.THORNS) aspects.merge(Aspect.ENTROPY, level);
        else if (enchantment == Enchantments.UNBREAKING) aspects.merge(Aspect.EARTH, level);
        else if (enchantment == Config.enchHaste) aspects.merge(Aspect.MOTION, level);
    }

    private static AspectList generateTagsFromRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList tags = generateTagsFromCrucibleRecipes(item, meta, history);
        if (tags != null) return tags;
        tags = generateTagsFromArcaneRecipes(item, meta, history);
        if (tags != null) return tags;
        tags = generateTagsFromInfusionRecipes(item, meta, history);
        if (tags != null) return tags;
        return generateTagsFromCraftingRecipes(item, meta, history);
    }

    private static AspectList generateTagsFromCrucibleRecipes(Item item, int meta, ArrayList<List> history) {
        CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipe(new ItemStack(item, 1, meta));
        if (recipe == null) return null;
        ItemStack catalyst = firstCatalyst(recipe.catalyst, history);
        if (catalyst == null || catalyst.isEmpty()) return null;
        AspectList result = new AspectList();
        AspectList catalystTags = generateTags(catalyst.getItem(), catalyst.getMetadata(), history);
        if (catalystTags != null) result.add(catalystTags);
        addScaledRecipeAspects(result, recipe.aspects, recipe.getRecipeOutput());
        removeNonPositive(result);
        return result;
    }

    private static AspectList generateTagsFromArcaneRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList best = null;
        for (Object entry : ThaumcraftApi.getCraftingRecipes()) {
            if (!(entry instanceof IArcaneRecipe)) continue;
            IArcaneRecipe recipe = (IArcaneRecipe)entry;
            ItemStack output = recipe.getRecipeOutput();
            if (!matchesOutput(output, item, meta)) continue;
            ArrayList<ItemStack> ingredients = getArcaneIngredients(recipe, history);
            if (ingredients == null) continue;
            AspectList result = getAspectsFromIngredients(ingredients, output, history);
            addScaledRecipeAspects(result, recipe.getAspects(), output);
            removeNonPositive(result);
            if (result.size() > 0) best = result;
        }
        return best;
    }

    private static AspectList generateTagsFromInfusionRecipes(Item item, int meta, ArrayList<List> history) {
        InfusionRecipe recipe = ThaumcraftApi.getInfusionRecipe(new ItemStack(item, 1, meta));
        if (recipe == null || !(recipe.getRecipeOutput() instanceof ItemStack)) return null;
        ItemStack output = (ItemStack)recipe.getRecipeOutput();
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        addSingleStack(ingredients, recipe.getRecipeInput());
        for (ItemStack component : recipe.getComponents()) {
            addSingleStack(ingredients, component);
        }
        AspectList result = getAspectsFromIngredients(ingredients, output, history);
        addScaledRecipeAspects(result, recipe.getAspects(), output);
        removeNonPositive(result);
        return result;
    }

    private static AspectList generateTagsFromCraftingRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList best = null;
        int bestVis = Integer.MAX_VALUE;
        for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection()) {
            ItemStack output = recipe.getRecipeOutput();
            if (!matchesOutput(output, item, meta)) continue;
            ArrayList<ItemStack> ingredients = getRecipeIngredients(recipe.getIngredients(), history);
            if (ingredients == null) continue;
            AspectList result = getAspectsFromIngredients(ingredients, output, history);
            removeNonPositive(result);
            int vis = result.visSize();
            if (vis > 0 && vis < bestVis) {
                best = result;
                bestVis = vis;
            }
        }
        return best;
    }

    private static boolean matchesOutput(ItemStack output, Item item, int meta) {
        if (output == null || output.isEmpty() || output.getItem() != item) return false;
        int outputMeta = output.getMetadata() == Short.MAX_VALUE ? 0 : output.getMetadata();
        int targetMeta = meta == Short.MAX_VALUE ? 0 : meta;
        return outputMeta == targetMeta;
    }

    private static ItemStack firstCatalyst(Object catalyst, ArrayList<List> history) {
        if (catalyst instanceof ItemStack) return singleStack((ItemStack)catalyst);
        if (catalyst instanceof List) return chooseStack((List<ItemStack>)catalyst, history);
        return ItemStack.EMPTY;
    }

    private static ArrayList<ItemStack> getArcaneIngredients(IArcaneRecipe recipe, ArrayList<List> history) {
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        if (recipe instanceof ShapedArcaneRecipe) {
            for (Object input : ((ShapedArcaneRecipe)recipe).getInput()) {
                if (!addRecipeInput(ingredients, input, history)) return null;
            }
        } else if (recipe instanceof ShapelessArcaneRecipe) {
            for (Object input : ((ShapelessArcaneRecipe)recipe).getInput()) {
                if (!addRecipeInput(ingredients, input, history)) return null;
            }
        }
        return ingredients;
    }

    private static ArrayList<ItemStack> getRecipeIngredients(List<Ingredient> recipeIngredients, ArrayList<List> history) {
        ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
        for (Ingredient ingredient : recipeIngredients) {
            if (ingredient == null || ingredient == Ingredient.EMPTY) continue;
            ItemStack chosen = chooseStack(Arrays.asList(ingredient.getMatchingStacks()), history);
            if (chosen != null && !chosen.isEmpty()) ingredients.add(singleStack(chosen));
        }
        return ingredients;
    }

    private static boolean addRecipeInput(ArrayList<ItemStack> ingredients, Object input, ArrayList<List> history) {
        if (input == null) return true;
        if (input instanceof ItemStack) {
            addSingleStack(ingredients, (ItemStack)input);
            return true;
        }
        if (input instanceof List) {
            ItemStack chosen = chooseStack((List<ItemStack>)input, history);
            if (chosen != null && !chosen.isEmpty()) ingredients.add(singleStack(chosen));
            return true;
        }
        return true;
    }

    private static void addSingleStack(ArrayList<ItemStack> ingredients, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) ingredients.add(singleStack(stack));
    }

    private static ItemStack chooseStack(List<ItemStack> choices, ArrayList<List> history) {
        ItemStack first = ItemStack.EMPTY;
        for (ItemStack choice : choices) {
            if (choice == null || choice.isEmpty()) continue;
            if (first.isEmpty()) first = choice;
            AspectList tags = generateTags(choice.getItem(), choice.getMetadata(), history);
            if (tags != null && tags.size() > 0) return choice;
        }
        return first;
    }

    private static ItemStack singleStack(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private static AspectList getAspectsFromIngredients(ArrayList<ItemStack> ingredients, ItemStack output, ArrayList<List> history) {
        AspectList result = new AspectList();
        if (output == null || output.isEmpty()) return result;
        int outputCount = Math.max(1, output.getCount());
        AspectList total = new AspectList();
        for (ItemStack ingredient : ingredients) {
            AspectList tags = generateTags(ingredient.getItem(), ingredient.getMetadata(), history);
            if (tags == null) continue;
            for (Aspect aspect : tags.getAspects()) {
                if (aspect != null) total.add(aspect, tags.getAmount(aspect));
            }
        }
        for (Aspect aspect : total.getAspects()) {
            if (aspect != null) result.add(aspect, (int)((float)total.getAmount(aspect) * 0.75F / (float)outputCount));
        }
        removeNonPositive(result);
        return result;
    }

    private static void addScaledRecipeAspects(AspectList result, AspectList recipeAspects, ItemStack output) {
        if (result == null || recipeAspects == null || output == null || output.isEmpty()) return;
        int outputCount = Math.max(1, output.getCount());
        for (Aspect aspect : recipeAspects.getAspects()) {
            if (aspect != null) result.add(aspect, (int)(Math.sqrt(recipeAspects.getAmount(aspect)) / (double)outputCount));
        }
    }

    private static void removeNonPositive(AspectList aspects) {
        if (aspects == null) return;
        for (Aspect aspect : aspects.copy().getAspects()) {
            if (aspect == null || aspects.getAmount(aspect) <= 0) aspects.remove(aspect);
        }
    }

    public static ItemStack findMatchingArcaneRecipe(IInventory awb, EntityPlayer player) {
        if (awb == null || player == null) return ItemStack.EMPTY;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof IArcaneRecipe)) continue;
            IArcaneRecipe arcaneRecipe = (IArcaneRecipe) recipe;
            if (arcaneRecipe.matches(awb, player.world, player)) {
                ItemStack out = arcaneRecipe.getCraftingResult(awb);
                return out == null ? ItemStack.EMPTY : out;
            }
        }
        return ItemStack.EMPTY;
    }

    public static AspectList findMatchingArcaneRecipeAspects(IInventory awb, EntityPlayer player) {
        if (awb == null || player == null) return new AspectList();
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof IArcaneRecipe)) continue;
            IArcaneRecipe arcaneRecipe = (IArcaneRecipe) recipe;
            if (!arcaneRecipe.matches(awb, player.world, player)) continue;
            return arcaneRecipe.getAspects() != null ? arcaneRecipe.getAspects() : arcaneRecipe.getAspects(awb);
        }
        return new AspectList();
    }

    public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        if (items == null || input == null || input.isEmpty() || player == null) return null;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof InfusionRecipe)) continue;
            InfusionRecipe infusionRecipe = (InfusionRecipe) recipe;
            if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;
        }
        return null;
    }

    public static InfusionEnchantmentRecipe findMatchingInfusionEnchantmentRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        if (items == null || input == null || input.isEmpty() || player == null) return null;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof InfusionEnchantmentRecipe)) continue;
            InfusionEnchantmentRecipe infusionRecipe = (InfusionEnchantmentRecipe) recipe;
            if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;
        }
        return null;
    }

    /**
     * Find the best matching crucible recipe for the given aspects and catalyst item.
     * Matches the original logic: creates a copy of lastDrop with stackSize=1,
     * checks research completion, matches aspects+catalyst, returns recipe with
     * most aspect types.
     */
    public static CrucibleRecipe findMatchingCrucibleRecipe(String username, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        int index = -1;

        for (int a = 0; a < ThaumcraftApi.getCraftingRecipes().size(); ++a) {
            if (!(ThaumcraftApi.getCraftingRecipes().get(a) instanceof CrucibleRecipe)) continue;

            CrucibleRecipe recipe = (CrucibleRecipe) ThaumcraftApi.getCraftingRecipes().get(a);

            // Create a single-item copy of the catalyst stack for matching
            ItemStack temp = lastDrop.copy();
            temp.setCount(1);

            // Check research requirement
            if (!ResearchManager.isResearchComplete(username, recipe.key)) continue;

            // Check recipe match (aspects + catalyst)
            if (!recipe.matches(aspects, temp)) continue;

            // Prefer recipe with more aspect types
            int result = recipe.aspects.size();
            if (result <= highest) continue;

            highest = result;
            index = a;
        }

        if (index < 0) return null;
        return (CrucibleRecipe) ThaumcraftApi.getCraftingRecipes().get(index);
    }
}
