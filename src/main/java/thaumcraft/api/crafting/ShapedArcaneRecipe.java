package thaumcraft.api.crafting;

import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;

public class ShapedArcaneRecipe
implements IArcaneRecipe {
    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;
    public ItemStack output = null;
    public Object[] input = null;
    public AspectList aspects = null;
    public String research;
    public int width = 0;
    public int height = 0;
    private boolean mirrored = true;

    public ShapedArcaneRecipe(String research, Block result, AspectList aspects, Object ... recipe) {
        this(research, new ItemStack(result), aspects, recipe);
    }

    public ShapedArcaneRecipe(String research, Item result, AspectList aspects, Object ... recipe) {
        this(research, new ItemStack(result), aspects, recipe);
    }

    public ShapedArcaneRecipe(String research, ItemStack result, AspectList aspects, Object ... recipe) {
        this.output = result.copy();
        this.research = research;
        this.aspects = aspects;
        String shape = "";
        int idx = 0;
        if (recipe[idx] instanceof Boolean) {
            this.mirrored = (Boolean)recipe[idx];
            if (recipe[idx + 1] instanceof Object[]) {
                recipe = (Object[])recipe[idx + 1];
            } else {
                idx = 1;
            }
        }
        if (recipe[idx] instanceof String[]) {
            String[] parts = (String[])recipe[idx++];
            for (String s : parts) {
                this.width = s.length();
                shape = shape + (String)s;
            }
            this.height = parts.length;
        } else {
            while (recipe[idx] instanceof String) {
                String s = (String)recipe[idx++];
                shape = shape + s;
                this.width = s.length();
                ++this.height;
            }
        }
        if (this.width * this.height != shape.length()) {
            String ret = "Invalid shaped ore recipe: ";
            for (Object tmp : recipe) {
                ret = ret + tmp + ", ";
            }
            ret = ret + this.output;
            throw new RuntimeException(ret);
        }
        HashMap<Character, Object> itemMap = new HashMap<Character, Object>();
        while (idx < recipe.length) {
            Character chr = (Character)recipe[idx];
            Object in = recipe[idx + 1];
            if (in instanceof ItemStack) {
                itemMap.put(chr, ((ItemStack)in).copy());
            } else if (in instanceof Item) {
                itemMap.put(chr, new ItemStack((Item)in));
            } else if (in instanceof Block) {
                itemMap.put(chr, new ItemStack((Block)in, 1, Short.MAX_VALUE));
            } else if (in instanceof String) {
                itemMap.put(chr, OreDictionary.getOres((String)((String)in)));
            } else {
                String ret = "Invalid shaped ore recipe: ";
                for (Object tmp : recipe) {
                    ret = ret + tmp + ", ";
                }
                ret = ret + this.output;
                throw new RuntimeException(ret);
            }
            idx += 2;
        }
        this.input = new Object[this.width * this.height];
        int x = 0;
        for (char chr : shape.toCharArray()) {
            this.input[x++] = itemMap.get(Character.valueOf(chr));
        }
    }

    @Override
    public ItemStack getCraftingResult(IInventory var1) {
        return this.output.copy();
    }

    @Override
    public int getRecipeSize() {
        return this.input.length;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public boolean matches(IInventory inv, World world, EntityPlayer player) {
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), this.research)) {
            return false;
        }
        for (int x = 0; x <= 3 - this.width; ++x) {
            for (int y = 0; y <= 3 - this.height; ++y) {
                if (this.checkMatch(inv, x, y, false)) {
                    return true;
                }
                if (!this.mirrored || !this.checkMatch(inv, x, y, true)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkMatch(IInventory inv, int startX, int startY, boolean mirror) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;
                if (subX >= 0 && subY >= 0 && subX < this.width && subY < this.height) {
                    target = mirror ? this.input[this.width - subX - 1 + subY * this.width] : this.input[subX + subY * this.width];
                }
                ItemStack slot = ThaumcraftApiHelper.getStackInRowAndColumn(inv, x, y);
                if (target instanceof ItemStack) {
                    if (this.checkItemEquals((ItemStack)target, slot)) continue;
                    return false;
                }
                if (target instanceof List) {
                    boolean matched = false;
                    for (ItemStack item : (List<ItemStack>)target) {
                        matched = matched || this.checkItemEquals(item, slot);
                    }
                    if (matched) continue;
                    return false;
                }
                if (target == null && (slot == null || slot.isEmpty())) continue;
                return false;
            }
        }
        return true;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        boolean inputEmpty = input == null || input.isEmpty();
        boolean targetEmpty = target == null || target.isEmpty();
        if (inputEmpty || targetEmpty) {
            return false;
        }
        return !(target.getItem() != input.getItem() || target.hasTagCompound() && !ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(input, target) || target.getMetadata() != Short.MAX_VALUE && target.getMetadata() != input.getMetadata());
    }

    public ShapedArcaneRecipe setMirrored(boolean mirror) {
        this.mirrored = mirror;
        return this;
    }

    public Object[] getInput() {
        return this.input;
    }

    @Override
    public AspectList getAspects() {
        return this.aspects;
    }

    @Override
    public AspectList getAspects(IInventory inv) {
        return this.aspects;
    }

    @Override
    public String getResearch() {
        return this.research;
    }
}
