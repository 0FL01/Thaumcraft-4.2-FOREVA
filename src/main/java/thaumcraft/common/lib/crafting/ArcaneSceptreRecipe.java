package thaumcraft.common.lib.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ArcaneSceptreRecipe implements IArcaneRecipe {
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 0);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 1);
        ItemStack cap3 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        ItemStack focus = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        if (!hasEmptyPadding(inv)
                || isEmpty(cap1) || isEmpty(cap2) || isEmpty(cap3) || isEmpty(rod) || isEmpty(focus)
                || !checkItemEquals(cap1, cap2) || !checkItemEquals(cap1, cap3)
                || !checkItemEquals(focus, new ItemStack(ConfigItems.itemResource, 1, 15))) {
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

        int cost = (int) ((float) (capMatch.getCraftCost() * rodMatch.getCraftCost()) * 1.5F);
        ItemStack out = new ItemStack(ConfigItems.itemWandCasting, 1, cost);
        ItemWandCasting.setCap(out, capMatch);
        ItemWandCasting.setRod(out, rodMatch);
        out.setTagInfo("sceptre", new NBTTagByte((byte) 1));
        return out;
    }

    @Override
    public AspectList getAspects(IInventory inv) {
        AspectList out = new AspectList();
        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 0);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 1);
        ItemStack cap3 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        ItemStack focus = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        if (!hasEmptyPadding(inv)
                || isEmpty(cap1) || isEmpty(cap2) || isEmpty(cap3) || isEmpty(rod) || isEmpty(focus)
                || !checkItemEquals(cap1, cap2) || !checkItemEquals(cap1, cap3)
                || !checkItemEquals(focus, new ItemStack(ConfigItems.itemResource, 1, 15))) {
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
            int cost = (int) ((float) (capCost * rodCost) * 1.5F);
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
        if (!ThaumcraftApiHelper.isResearchComplete(player.getName(), "SCEPTRE")) return false;

        ItemStack cap1 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 0);
        ItemStack cap2 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 1);
        ItemStack cap3 = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 0, 2);
        ItemStack rod = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 1);
        ItemStack focus = ThaumcraftApiHelper.getStackInRowAndColumn(inv, 2, 0);
        if (!hasEmptyPadding(inv)) return false;
        return checkMatch(cap1, cap2, cap3, rod, focus, player);
    }

    private boolean checkMatch(ItemStack cap1, ItemStack cap2, ItemStack cap3, ItemStack rod, ItemStack focus, EntityPlayer player) {
        if (isEmpty(cap1) || isEmpty(cap2) || isEmpty(cap3) || isEmpty(rod) || isEmpty(focus)) return false;
        if (!checkItemEquals(cap1, cap2) || !checkItemEquals(cap1, cap3)) return false;
        if (!checkItemEquals(focus, new ItemStack(ConfigItems.itemResource, 1, 15))) return false;

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
                && isEmpty(ThaumcraftApiHelper.getStackInRowAndColumn(inv, 1, 2))
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
                && target.getMetadata() == input.getMetadata();
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
