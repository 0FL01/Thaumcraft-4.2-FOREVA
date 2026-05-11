package thaumcraft.common.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.common.Thaumcraft;

public class InternalMethodHandler implements IInternalMethodHandler {

    @Override
    public void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
        // Phase 8: particle FX
    }

    @Override
    public boolean isResearchComplete(String username, String researchkey) {
        // Phase 9: research system
        return false;
    }

    @Override
    public boolean hasDiscoveredAspect(String username, Aspect aspect) {
        // Phase 3: aspect discovery
        return false;
    }

    @Override
    public AspectList getDiscoveredAspects(String username) {
        // Phase 3: aspect discovery
        return new AspectList();
    }

    @Override
    public ItemStack getStackInRowAndColumn(Object instance, int row, int column) {
        // Phase 4: tile entity integration
        return ItemStack.EMPTY;
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        // Phase 3: aspect mapping
        return new AspectList();
    }

    @Override
    public AspectList getBonusObjectTags(ItemStack is, AspectList ot) {
        // Phase 3: aspect mapping
        return new AspectList();
    }

    @Override
    public AspectList generateTags(Item item, int meta) {
        // Phase 3: aspect mapping
        return new AspectList();
    }

    @Override
    public boolean consumeVisFromWand(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        // Phase 5: wand system
        return false;
    }

    @Override
    public boolean consumeVisFromWandCrafting(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit) {
        // Phase 5: wand system
        return false;
    }

    @Override
    public boolean consumeVisFromInventory(EntityPlayer player, AspectList cost) {
        // Phase 5: wand system
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
