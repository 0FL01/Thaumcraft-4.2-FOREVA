package thaumcraft.api;

import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

public class ThaumcraftApiHelper {
    private static HashMap<Integer, AspectList> allAspects = new HashMap();
    private static HashMap<Integer, AspectList> allCompoundAspects = new HashMap();

    public static AspectList cullTags(AspectList temp) {
        AspectList temp2 = new AspectList();
        for (Aspect tag : temp.getAspects()) {
            if (tag == null) continue;
            temp2.add(tag, temp.getAmount(tag));
        }
        while (temp2 != null && temp2.size() > 6) {
            Aspect lowest = null;
            float low = 32767.0f;
            for (Aspect tag : temp2.getAspects()) {
                if (tag == null) continue;
                float ta = temp2.getAmount(tag);
                if (tag.isPrimal()) {
                    ta *= 0.9f;
                } else {
                    if (!tag.getComponents()[0].isPrimal()) {
                        ta *= 1.1f;
                        if (!tag.getComponents()[0].getComponents()[0].isPrimal()) {
                            ta *= 1.05f;
                        }
                        if (!tag.getComponents()[0].getComponents()[1].isPrimal()) {
                            ta *= 1.05f;
                        }
                    }
                    if (!tag.getComponents()[1].isPrimal()) {
                        ta *= 1.1f;
                        if (!tag.getComponents()[1].getComponents()[0].isPrimal()) {
                            ta *= 1.05f;
                        }
                        if (!tag.getComponents()[1].getComponents()[1].isPrimal()) {
                            ta *= 1.05f;
                        }
                    }
                }
                if (!(ta < low)) continue;
                low = ta;
                lowest = tag;
            }
            temp2.aspects.remove(lowest);
        }
        return temp2;
    }

    public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
        if (s1.isItemStackDamageable() && s2.isItemStackDamageable()) {
            return s1.getItem() == s2.getItem();
        }
        return s1.getItem() == s2.getItem() && s1.getMetadata() == s2.getMetadata();
    }

    public static boolean isResearchComplete(String username, String researchkey) {
        return ThaumcraftApi.internalMethods.isResearchComplete(username, researchkey);
    }

    public static boolean hasDiscoveredAspect(String username, Aspect aspect) {
        return ThaumcraftApi.internalMethods.hasDiscoveredAspect(username, aspect);
    }

    public static AspectList getDiscoveredAspects(String username) {
        return ThaumcraftApi.internalMethods.getDiscoveredAspects(username);
    }

    public static ItemStack getStackInRowAndColumn(Object instance, int row, int column) {
        return ThaumcraftApi.internalMethods.getStackInRowAndColumn(instance, row, column);
    }

    public static AspectList getObjectAspects(ItemStack is) {
        return ThaumcraftApi.internalMethods.getObjectAspects(is);
    }

    public static AspectList getBonusObjectTags(ItemStack is, AspectList ot) {
        return ThaumcraftApi.internalMethods.getBonusObjectTags(is, ot);
    }

    public static AspectList generateTags(Item item, int meta) {
        return ThaumcraftApi.internalMethods.generateTags(item, meta);
    }

    public static boolean containsMatch(boolean strict, ItemStack[] inputs, ItemStack ... targets) {
        for (ItemStack input : inputs) {
            for (ItemStack target : targets) {
                if (!ThaumcraftApiHelper.itemMatches(target, input, strict)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
        if (recipeItem == null || slotItem == null) {
            return false;
        }
        if (recipeItem.getTagCompound() != null && slotItem.getTagCompound() == null) {
            return false;
        }
        if (recipeItem.getTagCompound() == null) {
            return true;
        }
        for (String s : recipeItem.getTagCompound().getKeySet()) {
            if (slotItem.getTagCompound().hasKey(s)) {
                if (slotItem.getTagCompound().getTag(s).toString().equals(recipeItem.getTagCompound().getTag(s).toString())) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict) {
        if (input == null && target != null || input != null && target == null) {
            return false;
        }
        return target.getItem() == input.getItem() && (target.getMetadata() == Short.MAX_VALUE && !strict || target.getMetadata() == input.getMetadata());
    }

    public static TileEntity getConnectableTile(World world, int x, int y, int z, EnumFacing face) {
        TileEntity te = world.getTileEntity(new BlockPos(x + face.getXOffset(), y + face.getYOffset(), z + face.getZOffset()));
        if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) {
            return te;
        }
        return null;
    }

    public static TileEntity getConnectableTile(IBlockAccess world, int x, int y, int z, EnumFacing face) {
        TileEntity te = world.getTileEntity(new BlockPos(x + face.getXOffset(), y + face.getYOffset(), z + face.getZOffset()));
        if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) {
            return te;
        }
        return null;
    }

    public static AspectList getAllAspects(int amount) {
        if (allAspects.get(amount) == null) {
            AspectList al = new AspectList();
            for (Aspect aspect : Aspect.aspects.values()) {
                al.add(aspect, amount);
            }
            allAspects.put(amount, al);
        }
        return allAspects.get(amount);
    }

    public static AspectList getAllCompoundAspects(int amount) {
        if (allCompoundAspects.get(amount) == null) {
            AspectList al = new AspectList();
            for (Aspect aspect : Aspect.getCompoundAspects()) {
                al.add(aspect, amount);
            }
            allCompoundAspects.put(amount, al);
        }
        return allCompoundAspects.get(amount);
    }

    public static boolean consumeVisFromWand(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        return ThaumcraftApi.internalMethods.consumeVisFromWand(wand, player, cost, doit, crafting);
    }

    public static boolean consumeVisFromWandCrafting(ItemStack wand, EntityPlayer player, AspectList cost, boolean doit) {
        return ThaumcraftApi.internalMethods.consumeVisFromWandCrafting(wand, player, cost, doit);
    }

    public static boolean consumeVisFromInventory(EntityPlayer player, AspectList cost) {
        return ThaumcraftApi.internalMethods.consumeVisFromInventory(player, cost);
    }

    public static void addWarpToPlayer(EntityPlayer player, int amount, boolean temporary) {
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, amount, temporary);
    }

    public static void addStickyWarpToPlayer(EntityPlayer player, int amount) {
        ThaumcraftApi.internalMethods.addStickyWarpToPlayer(player, amount);
    }

    public static RayTraceResult rayTraceIgnoringSource(World world, Vec3d v1, Vec3d v2, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (Double.isNaN(v1.x) || Double.isNaN(v1.y) || Double.isNaN(v1.z) ||
            Double.isNaN(v2.x) || Double.isNaN(v2.y) || Double.isNaN(v2.z)) {
            return null;
        }
        return world.rayTraceBlocks(v1, v2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
    }
}

