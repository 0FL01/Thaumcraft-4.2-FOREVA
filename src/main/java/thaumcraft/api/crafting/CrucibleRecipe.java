package thaumcraft.api.crafting;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe {
    private ItemStack recipeOutput;
    public Object catalyst;
    public AspectList aspects;
    public String key;
    public int hash;

    public CrucibleRecipe(String researchKey, ItemStack result, Object cat, AspectList tags) {
        this.recipeOutput = result;
        this.aspects = tags;
        this.key = researchKey;
        this.catalyst = cat;
        if (cat instanceof String) {
            this.catalyst = OreDictionary.getOres((String)((String)cat));
        }
        String hc = researchKey + result.toString();
        for (Aspect tag : tags.getAspects()) {
            hc = hc + tag.getTag() + tags.getAmount(tag);
        }
        if (cat instanceof ItemStack) {
            hc = hc + ((ItemStack)cat).toString();
        } else if (this.catalyst instanceof List && !((List<?>)this.catalyst).isEmpty()) {
            for (ItemStack is : this.getCatalystItems()) {
                hc = hc + is.toString();
            }
        }
        this.hash = hc.hashCode();
    }

    public boolean matches(AspectList itags, ItemStack cat) {
        ItemStack[] ores;
        if (this.catalyst instanceof ItemStack && !ThaumcraftApiHelper.itemMatches((ItemStack)this.catalyst, cat, false)) {
            return false;
        }
        if (this.catalyst instanceof List && !((List<?>)this.catalyst).isEmpty() && !ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, ores = this.getCatalystItems().toArray(new ItemStack[0]))) {
            return false;
        }
        if (itags == null) {
            return false;
        }
        for (Aspect tag : this.aspects.getAspects()) {
            if (itags.getAmount(tag) >= this.aspects.getAmount(tag)) continue;
            return false;
        }
        return true;
    }

    public boolean catalystMatches(ItemStack cat) {
        ItemStack[] ores;
        if (this.catalyst instanceof ItemStack && ThaumcraftApiHelper.itemMatches((ItemStack)this.catalyst, cat, false)) {
            return true;
        }
        return this.catalyst instanceof List && !((List<?>)this.catalyst).isEmpty() && ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{cat}, ores = this.getCatalystItems().toArray(new ItemStack[0]));
    }

    @SuppressWarnings("unchecked")
    private List<ItemStack> getCatalystItems() {
        return (List<ItemStack>)this.catalyst;
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);
        for (Aspect tag : this.aspects.getAspects()) {
            temptags.remove(tag, this.aspects.getAmount(tag));
        }
        itags = temptags;
        return itags;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }
}
