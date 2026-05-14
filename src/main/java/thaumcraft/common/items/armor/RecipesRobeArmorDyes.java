package thaumcraft.common.items.armor;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipesRobeArmorDyes extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack armor = ItemStack.EMPTY;
        int dyes = 0;
        for (int slot = 0; slot < inv.getSizeInventory(); ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor) stack.getItem();
                if (!this.isTargetArmor(itemArmor) || !armor.isEmpty()) {
                    return false;
                }
                armor = stack;
            } else if (stack.getItem() == Items.DYE) {
                ++dyes;
            } else {
                return false;
            }
        }
        return !armor.isEmpty() && dyes > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack output = ItemStack.EMPTY;
        ItemArmor armor = null;
        int[] color = new int[3];
        int brightnessTotal = 0;
        int samples = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemArmor) {
                armor = (ItemArmor) stack.getItem();
                if (!this.isTargetArmor(armor) || !output.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                output = stack.copy();
                output.setCount(1);
                if (armor.hasColor(stack)) {
                    int existing = armor.getColor(output);
                    float r = (float) (existing >> 16 & 255) / 255.0F;
                    float g = (float) (existing >> 8 & 255) / 255.0F;
                    float b = (float) (existing & 255) / 255.0F;
                    brightnessTotal = (int) ((float) brightnessTotal + Math.max(r, Math.max(g, b)) * 255.0F);
                    color[0] = (int) ((float) color[0] + r * 255.0F);
                    color[1] = (int) ((float) color[1] + g * 255.0F);
                    color[2] = (int) ((float) color[2] + b * 255.0F);
                    ++samples;
                }
            } else if (stack.getItem() == Items.DYE) {
                float[] dye = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stack.getMetadata()));
                int r = (int) (dye[0] * 255.0F);
                int g = (int) (dye[1] * 255.0F);
                int b = (int) (dye[2] * 255.0F);
                brightnessTotal += Math.max(r, Math.max(g, b));
                color[0] += r;
                color[1] += g;
                color[2] += b;
                ++samples;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (armor == null || samples == 0 || output.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int r = color[0] / samples;
        int g = color[1] / samples;
        int b = color[2] / samples;
        float brightness = (float) brightnessTotal / (float) samples;
        float max = (float) Math.max(r, Math.max(g, b));
        r = (int) ((float) r * brightness / max);
        g = (int) ((float) g * brightness / max);
        b = (int) ((float) b * brightness / max);
        armor.setColor(output, (r << 16) + (g << 8) + b);
        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    protected boolean isTargetArmor(ItemArmor armor) {
        return armor instanceof ItemRobeArmor;
    }
}
