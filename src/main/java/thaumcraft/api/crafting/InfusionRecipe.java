package thaumcraft.api.crafting;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

public class InfusionRecipe {
    protected AspectList aspects;
    protected String research;
    private ItemStack[] components;
    private ItemStack recipeInput;
    protected Object recipeOutput;
    protected int instability;

    public InfusionRecipe(String research, Object output, int inst, AspectList aspects2, ItemStack input, ItemStack[] recipe) {
        this.research = research;
        this.recipeOutput = output;
        this.recipeInput = input;
        this.aspects = aspects2;
        this.components = recipe;
        this.instability = inst;
    }

    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        if (this.getRecipeInput() == null) {
            return false;
        }
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), this.research)) {
            return false;
        }
        ItemStack i2 = central.copy();
        if (this.getRecipeInput().getMetadata() == Short.MAX_VALUE) {
            i2.setItemDamage(Short.MAX_VALUE);
        }
        if (!InfusionRecipe.areItemStacksEqual(i2, this.getRecipeInput(), true)) {
            return false;
        }
        ArrayList<ItemStack> ii = new ArrayList<ItemStack>();
        for (ItemStack is : input) {
            ii.add(is.copy());
        }
        for (ItemStack comp : this.getComponents()) {
            boolean b = false;
            for (int a = 0; a < ii.size(); ++a) {
                i2 = ((ItemStack)ii.get(a)).copy();
                if (comp.getMetadata() == Short.MAX_VALUE) {
                    i2.setItemDamage(Short.MAX_VALUE);
                }
                if (!InfusionRecipe.areItemStacksEqual(i2, comp, true)) continue;
                ii.remove(a);
                b = true;
                break;
            }
            if (b) continue;
            return false;
        }
        return ii.size() == 0;
    }

    public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, boolean fuzzy) {
        boolean damage;
        ItemStack[] ores;
        if (stack0 == null && stack1 != null) {
            return false;
        }
        if (stack0 != null && stack1 == null) {
            return false;
        }
        if (stack0 == null && stack1 == null) {
            return true;
        }
        boolean t1 = ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(stack0, stack1);
        if (!t1) {
            return false;
        }
        if (fuzzy) {
            int[] oreIDs = OreDictionary.getOreIDs(stack0);
            if (oreIDs.length > 0 && ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{stack1}, ores = OreDictionary.getOres(OreDictionary.getOreName(oreIDs[0])).toArray(new ItemStack[0]))) {
                return true;
            }
        }
        boolean bl = damage = stack0.getMetadata() == stack1.getMetadata() || stack1.getMetadata() == Short.MAX_VALUE;
        return stack0.getItem() != stack1.getItem() ? false : (!damage ? false : stack0.getCount() <= stack0.getMaxStackSize());
    }

    public Object getRecipeOutput() {
        return this.getRecipeOutput(this.getRecipeInput());
    }

    public AspectList getAspects() {
        return this.getAspects(this.getRecipeInput());
    }

    public int getInstability() {
        return this.getInstability(this.getRecipeInput());
    }

    public String getResearch() {
        return this.research;
    }

    public ItemStack getRecipeInput() {
        return this.recipeInput;
    }

    public ItemStack[] getComponents() {
        return this.components;
    }

    public Object getRecipeOutput(ItemStack input) {
        return this.recipeOutput;
    }

    public AspectList getAspects(ItemStack input) {
        return this.aspects;
    }

    public int getInstability(ItemStack input) {
        return this.instability;
    }
}

