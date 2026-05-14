package thaumcraft.common.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.IInternalMethodHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileMagicWorkbench;

public class InternalMethodHandler implements IInternalMethodHandler {

    private final PlayerKnowledge playerKnowledge = new PlayerKnowledge();

    @Override
    public void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
        net.minecraft.world.World world = net.minecraftforge.common.DimensionManager.getWorld(dim);
        if (world != null) {
            thaumcraft.common.lib.utils.Utils.generateVisEffect(world,
                new net.minecraft.util.math.BlockPos(x, y, z),
                new net.minecraft.util.math.BlockPos(x2, y2, z2), color);
        }
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
        if (instance instanceof TileMagicWorkbench) {
            return ((TileMagicWorkbench)instance).getStackInRowAndColumn(row, column);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return ThaumcraftCraftingManager.getObjectTags(is);
    }

    @Override
    public AspectList getBonusObjectTags(ItemStack is, AspectList ot) {
        return ThaumcraftCraftingManager.getBonusTags(is, ot);
    }

    @Override
    public AspectList generateTags(Item item, int meta) {
        return ThaumcraftCraftingManager.generateTags(item, meta);
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
        return WandManager.consumeVisFromInventory(player, cost);
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
