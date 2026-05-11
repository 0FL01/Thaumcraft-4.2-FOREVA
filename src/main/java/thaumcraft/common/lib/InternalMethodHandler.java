package thaumcraft.common.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.lib.research.ResearchManager;

public class InternalMethodHandler implements IInternalMethodHandler {

    private final PlayerKnowledge playerKnowledge = new PlayerKnowledge();

    @Override
    public void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
        // Phase 8: particle FX
    }

    @Override
    public boolean isResearchComplete(String username, String researchkey) {
        return ResearchManager.isResearchComplete(username, researchkey);
    }

    @Override
    public boolean hasDiscoveredAspect(String username, Aspect aspect) {
        return playerKnowledge.hasDiscoveredAspect(username, aspect);
    }

    @Override
    public AspectList getDiscoveredAspects(String username) {
        return playerKnowledge.getAspectsDiscovered(username);
    }

    @Override
    public ItemStack getStackInRowAndColumn(Object instance, int row, int column) {
        // Phase 4: tile entity integration
        return ItemStack.EMPTY;
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        if (is.isEmpty()) return new AspectList();
        // Look up in the object tags registry
        java.util.List<Object> key = java.util.Arrays.asList(is.getItem(), is.getMetadata());
        AspectList aspects = thaumcraft.api.ThaumcraftApi.objectTags.get(key);
        if (aspects == null) {
            key = java.util.Arrays.asList(is.getItem(), Short.MAX_VALUE);
            aspects = thaumcraft.api.ThaumcraftApi.objectTags.get(key);
        }
        return aspects != null ? aspects.copy() : new AspectList();
    }

    @Override
    public AspectList getBonusObjectTags(ItemStack is, AspectList ot) {
        // Phase 3: bonus tags from components
        return new AspectList();
    }

    @Override
    public AspectList generateTags(Item item, int meta) {
        // Phase 3: auto-generate tags from materials
        return new AspectList();
    }

    @Override
    public boolean consumeVisFromWand(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wandItem = (ItemWandCasting) wand.getItem();
        return wandItem.consumeAllVis(wand, player, cost, doit, crafting);
    }

    @Override
    public boolean consumeVisFromWandCrafting(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit) {
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wandItem = (ItemWandCasting) wand.getItem();
        return wandItem.consumeAllVisCrafting(wand, player, cost, doit);
    }

    @Override
    public boolean consumeVisFromInventory(EntityPlayer player, AspectList cost) {
        // Try all inventory slots for wands
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wandItem = (ItemWandCasting) stack.getItem();
                if (wandItem.consumeAllVis(stack, player, cost, true, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addWarpToPlayer(EntityPlayer player, int amount, boolean temporary) {
        Thaumcraft.addWarpToPlayer(player, amount, temporary);
    }

    @Override
    public void addStickyWarpToPlayer(EntityPlayer player, int amount) {
        Thaumcraft.addStickyWarpToPlayer(player, amount);
    }
}
