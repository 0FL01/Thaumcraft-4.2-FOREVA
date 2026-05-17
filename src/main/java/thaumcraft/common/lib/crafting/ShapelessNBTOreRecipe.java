package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessNBTOreRecipe extends ShapelessOreRecipe {
    private final ItemStack output;
    private final ArrayList<Object> input = new ArrayList<>();

    public ShapelessNBTOreRecipe(Block result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapelessNBTOreRecipe(Item result, Object... recipe) {
        this(new ItemStack(result), recipe);
    }

    public ShapelessNBTOreRecipe(ItemStack result, Object... recipe) {
        super((ResourceLocation) null, result, recipe);
        output = result.copy();
        for (Object in : recipe) {
            if (in instanceof ItemStack) {
                input.add(((ItemStack) in).copy());
            } else if (in instanceof Item) {
                input.add(new ItemStack((Item) in));
            } else if (in instanceof Block) {
                input.add(new ItemStack((Block) in));
            } else if (in instanceof String) {
                input.add(OreDictionary.getOres((String) in));
            } else {
                StringBuilder ret = new StringBuilder("Invalid shapeless ore recipe: ");
                for (Object tmp : recipe) {
                    ret.append(tmp).append(", ");
                }
                ret.append(output);
                throw new RuntimeException(ret.toString());
            }
        }
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ArrayList<Object> required = new ArrayList<>(input);
        for (int slotId = 0; slotId < inv.getSizeInventory(); ++slotId) {
            ItemStack slot = inv.getStackInSlot(slotId);
            if (slot.isEmpty()) {
                continue;
            }
            boolean inRecipe = false;
            Iterator<Object> req = required.iterator();
            while (req.hasNext()) {
                boolean match = false;
                Object next = req.next();
                if (next instanceof ItemStack) {
                    match = checkItemEquals((ItemStack) next, slot);
                } else if (next instanceof List) {
                    for (ItemStack item : (List<ItemStack>) next) {
                        match = match || checkItemEquals(item, slot);
                    }
                }
                if (!match) {
                    continue;
                }
                inRecipe = true;
                req.remove();
                break;
            }
            if (!inRecipe) {
                return false;
            }
        }
        return required.isEmpty();
    }

    public ArrayList<Object> getInput() {
        return input;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack stack) {
        return !target.isEmpty()
                && !stack.isEmpty()
                && target.getItem() == stack.getItem()
                && ItemStack.areItemStackTagsEqual(target, stack)
                && (target.getMetadata() == OreDictionary.WILDCARD_VALUE
                || target.getMetadata() == stack.getMetadata());
    }
}
