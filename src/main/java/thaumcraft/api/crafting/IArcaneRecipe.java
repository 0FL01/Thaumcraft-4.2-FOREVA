package thaumcraft.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;

public interface IArcaneRecipe {
    public boolean matches(IInventory var1, World var2, EntityPlayer var3);

    public ItemStack getCraftingResult(IInventory var1);

    public int getRecipeSize();

    public ItemStack getRecipeOutput();

    public AspectList getAspects();

    public AspectList getAspects(IInventory var1);

    public String getResearch();
}

