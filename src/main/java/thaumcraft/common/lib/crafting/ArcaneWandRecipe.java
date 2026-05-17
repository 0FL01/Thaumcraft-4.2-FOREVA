package thaumcraft.common.lib.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ArcaneWandRecipe implements IArcaneRecipe {
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        if (!hasEmptyPadding(inv) || !checkItemEquals(cap1, cap2) || isEmpty(cap1) || isEmpty(rod)) {
            return ItemStack.EMPTY;
        }

        WandCap capMatch = null;
        for (WandCap cap : WandCap.caps.values()) {
            if (checkItemEquals(cap1, cap.getItem())) {
                capMatch = cap;
                break;
            }
        }

        WandRod rodMatch = null;
        for (WandRod wandRod : WandRod.rods.values()) {
            if (checkItemEquals(rod, wandRod.getItem())) {
                rodMatch = wandRod;
                break;
            }
        }

        if (capMatch == null || rodMatch == null) return ItemStack.EMPTY;
        if ("wood".equals(rodMatch.getTag()) && "iron".equals(capMatch.getTag())) return ItemStack.EMPTY;

        int cost = capMatch.getCraftCost() * rodMatch.getCraftCost();
        ItemStack out = new ItemStack(ConfigItems.itemWandCasting, 1, cost);
        ItemWandCasting.setCap(out, capMatch);
        ItemWandCasting.setRod(out, rodMatch);
        return out;
    }

    @Override
    public AspectList getAspects(IInventory inv) {
        AspectList out = new AspectList();
        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        if (!hasEmptyPadding(inv) || !checkItemEquals(cap1, cap2) || isEmpty(cap1) || isEmpty(rod)) {
            return out;
        }

        int capCost = -1;
        for (WandCap cap : WandCap.caps.values()) {
            if (checkItemEquals(cap1, cap.getItem())) {
                capCost = cap.getCraftCost();
                break;
            }
        }

        int rodCost = -1;
        for (WandRod wandRod : WandRod.rods.values()) {
            if (checkItemEquals(rod, wandRod.getItem())) {
                rodCost = wandRod.getCraftCost();
                break;
            }
        }

        if (capCost >= 0 && rodCost >= 0) {
            int cost = capCost * rodCost;
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                out.add(aspect, cost);
            }
        }
        return out;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(IInventory inv, World world, EntityPlayer player) {
        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        if (!hasEmptyPadding(inv)) return false;
        return checkMatch(cap1, cap2, rod, player);
    }

    private boolean checkMatch(ItemStack cap1, ItemStack cap2, ItemStack rod, EntityPlayer player) {
        if (isEmpty(cap1) || isEmpty(cap2) || isEmpty(rod) || !checkItemEquals(cap1, cap2)) return false;
        boolean capUnlocked = false;
        for (WandCap cap : WandCap.caps.values()) {
            if (checkItemEquals(cap1, cap.getItem())
                    && ThaumcraftApiHelper.isResearchComplete(player.getName(), cap.getResearch())) {
                capUnlocked = true;
                break;
            }
        }
        boolean rodUnlocked = false;
        for (WandRod wandRod : WandRod.rods.values()) {
            if (checkItemEquals(rod, wandRod.getItem())
                    && ThaumcraftApiHelper.isResearchComplete(player.getName(), wandRod.getResearch())) {
                rodUnlocked = true;
                break;
            }
        }
        return capUnlocked && rodUnlocked;
    }

    private boolean hasEmptyPadding(IInventory inv) {
        return isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 0))
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 1))
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 0))
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 2))
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 1))
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 2));
    }

    private static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        boolean targetEmpty = isEmpty(target);
        boolean inputEmpty = isEmpty(input);
        if (targetEmpty && inputEmpty) return true;
        if (targetEmpty || inputEmpty) return false;
        return target.getItem() == input.getItem()
                && (!target.hasTagCompound() || ItemStack.areItemStackTagsEqual(target, input))
                && (target.getMetadata() == Short.MAX_VALUE || target.getMetadata() == input.getMetadata());
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public AspectList getAspects() {
        return null;
    }

    @Override
    public String getResearch() {
        return "";
    }
}
