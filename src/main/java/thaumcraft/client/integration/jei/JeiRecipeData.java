package thaumcraft.client.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;

/** Converts the canonical TC4 recipe list without introducing a second recipe registry. */
public final class JeiRecipeData {
    private JeiRecipeData() {
    }

    public interface StackExpander {
        List<ItemStack> getSubtypes(ItemStack stack);
    }

    public static final class Collection {
        public final List<Arcane> arcane = new ArrayList<Arcane>();
        public final List<Crucible> crucible = new ArrayList<Crucible>();
        public final List<Infusion> infusion = new ArrayList<Infusion>();
        public final Map<String, Integer> skipped = new LinkedHashMap<String, Integer>();
        public int sourceCount;

        private void skip(Object recipe) {
            String name = recipe == null ? "null" : recipe.getClass().getName();
            Integer count = this.skipped.get(name);
            this.skipped.put(name, count == null ? 1 : count + 1);
        }

        public int getSkippedCount() {
            int total = 0;
            for (Integer count : this.skipped.values()) {
                total += count;
            }
            return total;
        }
    }

    public abstract static class Recipe {
        public final AspectList aspects;
        public final String research;

        private Recipe(AspectList aspects, String research) {
            this.aspects = aspects == null ? new AspectList() : aspects.copy();
            this.research = research == null ? "" : research;
        }
    }

    public static final class Arcane extends Recipe {
        public final List<List<ItemStack>> inputs;
        public final int width;
        public final int height;
        public final boolean shapeless;
        public final ItemStack output;

        private Arcane(List<List<ItemStack>> inputs, int width, int height, boolean shapeless,
                       ItemStack output, AspectList aspects, String research) {
            super(aspects, research);
            this.inputs = inputs;
            this.width = width;
            this.height = height;
            this.shapeless = shapeless;
            this.output = output.copy();
        }
    }

    public static final class Crucible extends Recipe {
        public final List<ItemStack> catalyst;
        public final ItemStack output;

        private Crucible(List<ItemStack> catalyst, ItemStack output, AspectList aspects, String research) {
            super(aspects, research);
            this.catalyst = catalyst;
            this.output = output.copy();
        }
    }

    public static final class Infusion extends Recipe {
        public final List<ItemStack> central;
        public final List<List<ItemStack>> components;
        public final List<ItemStack> outputs;
        public final int instability;

        private Infusion(List<ItemStack> central, List<List<ItemStack>> components,
                         List<ItemStack> outputs, AspectList aspects, int instability, String research) {
            super(aspects, research);
            this.central = central;
            this.components = components;
            this.outputs = outputs;
            this.instability = instability;
        }
    }

    public static Collection collect(List<?> recipes, StackExpander expander) {
        Collection result = new Collection();
        if (recipes == null) {
            return result;
        }
        for (Object candidate : new ArrayList<Object>(recipes)) {
            result.sourceCount++;
            if (candidate instanceof ShapedArcaneRecipe) {
                Arcane data = shaped((ShapedArcaneRecipe) candidate, expander);
                if (data != null) {
                    result.arcane.add(data);
                } else {
                    result.skip(candidate);
                }
            } else if (candidate instanceof ShapelessArcaneRecipe) {
                Arcane data = shapeless((ShapelessArcaneRecipe) candidate, expander);
                if (data != null) {
                    result.arcane.add(data);
                } else {
                    result.skip(candidate);
                }
            } else if (candidate instanceof CrucibleRecipe) {
                Crucible data = crucible((CrucibleRecipe) candidate, expander);
                if (data != null) {
                    result.crucible.add(data);
                } else {
                    result.skip(candidate);
                }
            } else if (candidate instanceof InfusionEnchantmentRecipe
                    || candidate instanceof InfusionRunicAugmentRecipe) {
                result.skip(candidate);
            } else if (candidate instanceof InfusionRecipe) {
                Infusion data = infusion((InfusionRecipe) candidate, expander);
                if (data != null) {
                    result.infusion.add(data);
                } else {
                    result.skip(candidate);
                }
            } else {
                // Includes dynamic/non-enumerable IArcaneRecipe implementations.
                result.skip(candidate);
            }
        }
        return result;
    }

    private static Arcane shaped(ShapedArcaneRecipe recipe, StackExpander expander) {
        if (recipe.getRecipeOutput() == null || recipe.getRecipeOutput().isEmpty()
                || recipe.width <= 0 || recipe.height <= 0 || recipe.width > 3 || recipe.height > 3) {
            return null;
        }
        Object[] source = recipe.getInput();
        if (source == null || source.length != recipe.width * recipe.height) {
            return null;
        }
        List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(source.length);
        for (Object ingredient : source) {
            List<ItemStack> alternatives = exactAlternatives(ingredient, expander);
            if (ingredient != null && alternatives.isEmpty()) {
                return null;
            }
            inputs.add(alternatives);
        }
        return new Arcane(inputs, recipe.width, recipe.height, false, recipe.getRecipeOutput(),
                recipe.getAspects(), recipe.getResearch());
    }

    private static Arcane shapeless(ShapelessArcaneRecipe recipe, StackExpander expander) {
        if (recipe.getRecipeOutput() == null || recipe.getRecipeOutput().isEmpty()
                || recipe.getInput() == null || recipe.getInput().isEmpty() || recipe.getInput().size() > 9) {
            return null;
        }
        List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(recipe.getInput().size());
        for (Object ingredient : recipe.getInput()) {
            List<ItemStack> alternatives = exactAlternatives(ingredient, expander);
            if (alternatives.isEmpty()) {
                return null;
            }
            inputs.add(alternatives);
        }
        return new Arcane(inputs, 3, (inputs.size() + 2) / 3, true, recipe.getRecipeOutput(),
                recipe.getAspects(), recipe.getResearch());
    }

    private static Crucible crucible(CrucibleRecipe recipe, StackExpander expander) {
        if (recipe.getRecipeOutput() == null || recipe.getRecipeOutput().isEmpty()) {
            return null;
        }
        List<ItemStack> catalyst = exactAlternatives(recipe.catalyst, expander);
        if (catalyst.isEmpty()) {
            return null;
        }
        for (ItemStack alternative : catalyst) {
            alternative.setTagCompound(null);
        }
        return new Crucible(catalyst, recipe.getRecipeOutput(), recipe.aspects, recipe.key);
    }

    private static Infusion infusion(InfusionRecipe recipe, StackExpander expander) {
        ItemStack input = recipe.getRecipeInput();
        ItemStack[] sourceComponents = recipe.getComponents();
        if (input == null || input.isEmpty() || sourceComponents == null) {
            return null;
        }
        List<ItemStack> central = infusionAlternatives(input, expander);
        if (central.isEmpty()) {
            return null;
        }
        List<List<ItemStack>> components = new ArrayList<List<ItemStack>>(sourceComponents.length);
        for (ItemStack component : sourceComponents) {
            List<ItemStack> alternatives = infusionAlternatives(component, expander);
            if (alternatives.isEmpty()) {
                return null;
            }
            components.add(alternatives);
        }
        AspectList aspects = recipe.getAspects(input);
        List<ItemStack> outputs = outputs(recipe, central);
        if (aspects == null || outputs.isEmpty()) {
            return null;
        }
        return new Infusion(central, components, outputs, aspects, recipe.getInstability(input), recipe.getResearch());
    }

    private static List<ItemStack> outputs(InfusionRecipe recipe, List<ItemStack> central) {
        List<ItemStack> outputs = new ArrayList<ItemStack>();
        Object fixedOutput = recipe.getRecipeOutput(recipe.getRecipeInput());
        if (fixedOutput instanceof ItemStack && !((ItemStack) fixedOutput).isEmpty()) {
            outputs.add(((ItemStack) fixedOutput).copy());
            return outputs;
        }
        for (ItemStack variant : central) {
            Object output = recipe.getRecipeOutput(variant);
            if (output instanceof Object[]) {
                Object[] overlay = (Object[]) output;
                if (overlay.length != 2 || !(overlay[0] instanceof String) || !(overlay[1] instanceof NBTBase)) {
                    return Collections.emptyList();
                }
                ItemStack copy = variant.copy();
                copy.setTagInfo((String) overlay[0], ((NBTBase) overlay[1]).copy());
                outputs.add(copy);
            } else {
                return Collections.emptyList();
            }
        }
        return outputs;
    }

    private static List<ItemStack> exactAlternatives(Object ingredient, StackExpander expander) {
        if (ingredient == null) {
            return Collections.emptyList();
        }
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (ingredient instanceof ItemStack) {
            addConcrete(result, (ItemStack) ingredient, expander);
        } else if (ingredient instanceof Iterable) {
            for (Object alternative : (Iterable<?>) ingredient) {
                if (alternative instanceof ItemStack) {
                    addConcrete(result, (ItemStack) alternative, expander);
                }
            }
        }
        return result;
    }

    private static List<ItemStack> infusionAlternatives(ItemStack template, StackExpander expander) {
        if (template == null || template.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemStack> candidates = new ArrayList<ItemStack>();
        addConcrete(candidates, template, expander);
        for (String oreName : OreDictionary.getOreNames()) {
            List<ItemStack> ores = OreDictionary.getOres(oreName);
            if (!ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{template}, ores.toArray(new ItemStack[0]))) {
                continue;
            }
            for (ItemStack ore : ores) {
                addConcrete(candidates, ore, expander);
            }
        }
        List<ItemStack> accepted = new ArrayList<ItemStack>();
        for (ItemStack candidate : candidates) {
            ItemStack compare = candidate.copy();
            if (template.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                compare.setItemDamage(OreDictionary.WILDCARD_VALUE);
            }
            if (InfusionRecipe.areItemStacksEqual(compare, template, true)) {
                accepted.add(candidate.copy());
            }
        }
        return accepted;
    }

    private static void addConcrete(List<ItemStack> target, ItemStack template, StackExpander expander) {
        if (template == null || template.isEmpty()) {
            return;
        }
        if (template.getMetadata() != OreDictionary.WILDCARD_VALUE) {
            target.add(template.copy());
            return;
        }
        List<ItemStack> expanded = expander == null ? null : expander.getSubtypes(template.copy());
        if (expanded == null || expanded.isEmpty()) {
            target.add(template.copy());
            return;
        }
        for (ItemStack subtype : expanded) {
            if (subtype == null || subtype.isEmpty() || subtype.getItem() != template.getItem()) {
                continue;
            }
            ItemStack copy = subtype.copy();
            copyRequiredTags(template, copy);
            target.add(copy);
        }
    }

    private static void copyRequiredTags(ItemStack template, ItemStack target) {
        NBTTagCompound required = template.getTagCompound();
        if (required == null) {
            return;
        }
        for (String key : required.getKeySet()) {
            target.setTagInfo(key, required.getTag(key).copy());
        }
    }
}
