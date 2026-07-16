package thaumcraft.api;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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

        int targetX = MathHelper.floor(v2.x);
        int targetY = MathHelper.floor(v2.y);
        int targetZ = MathHelper.floor(v2.z);
        int currentX = MathHelper.floor(v1.x);
        int currentY = MathHelper.floor(v1.y);
        int currentZ = MathHelper.floor(v1.z);
        RayTraceResult lastMiss = null;
        int steps = 200;

        // This is the vanilla 1.12 DDA traversal without its initial collision
        // check. TC4 relies on that distinction when a ray starts inside a vis
        // relay: the relay itself must not obstruct its link to the next node.
        while (steps-- >= 0) {
            if (Double.isNaN(v1.x) || Double.isNaN(v1.y) || Double.isNaN(v1.z)) {
                return null;
            }
            if (currentX == targetX && currentY == targetY && currentZ == targetZ) {
                return returnLastUncollidableBlock ? lastMiss : null;
            }

            boolean stepX = targetX != currentX;
            boolean stepY = targetY != currentY;
            boolean stepZ = targetZ != currentZ;
            double boundaryX = 999.0D;
            double boundaryY = 999.0D;
            double boundaryZ = 999.0D;
            if (targetX > currentX) {
                boundaryX = currentX + 1.0D;
            } else if (targetX < currentX) {
                boundaryX = currentX;
            }
            if (targetY > currentY) {
                boundaryY = currentY + 1.0D;
            } else if (targetY < currentY) {
                boundaryY = currentY;
            }
            if (targetZ > currentZ) {
                boundaryZ = currentZ + 1.0D;
            } else if (targetZ < currentZ) {
                boundaryZ = currentZ;
            }

            double deltaX = v2.x - v1.x;
            double deltaY = v2.y - v1.y;
            double deltaZ = v2.z - v1.z;
            double fractionX = stepX ? (boundaryX - v1.x) / deltaX : 999.0D;
            double fractionY = stepY ? (boundaryY - v1.y) / deltaY : 999.0D;
            double fractionZ = stepZ ? (boundaryZ - v1.z) / deltaZ : 999.0D;
            if (fractionX == -0.0D) fractionX = -1.0E-4D;
            if (fractionY == -0.0D) fractionY = -1.0E-4D;
            if (fractionZ == -0.0D) fractionZ = -1.0E-4D;

            EnumFacing face;
            if (fractionX < fractionY && fractionX < fractionZ) {
                face = targetX > currentX ? EnumFacing.WEST : EnumFacing.EAST;
                v1 = new Vec3d(boundaryX, v1.y + deltaY * fractionX, v1.z + deltaZ * fractionX);
            } else if (fractionY < fractionZ) {
                face = targetY > currentY ? EnumFacing.DOWN : EnumFacing.UP;
                v1 = new Vec3d(v1.x + deltaX * fractionY, boundaryY, v1.z + deltaZ * fractionY);
            } else {
                face = targetZ > currentZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                v1 = new Vec3d(v1.x + deltaX * fractionZ, v1.y + deltaY * fractionZ, boundaryZ);
            }

            currentX = MathHelper.floor(v1.x) - (face == EnumFacing.EAST ? 1 : 0);
            currentY = MathHelper.floor(v1.y) - (face == EnumFacing.UP ? 1 : 0);
            currentZ = MathHelper.floor(v1.z) - (face == EnumFacing.SOUTH ? 1 : 0);
            BlockPos currentPos = new BlockPos(currentX, currentY, currentZ);
            IBlockState state = world.getBlockState(currentPos);
            Block block = state.getBlock();
            if (!ignoreBlockWithoutBoundingBox || state.getMaterial() == Material.PORTAL
                    || state.getCollisionBoundingBox(world, currentPos) != Block.NULL_AABB) {
                if (block.canCollideCheck(state, stopOnLiquid)) {
                    RayTraceResult hit = state.collisionRayTrace(world, currentPos, v1, v2);
                    if (hit != null) {
                        return hit;
                    }
                } else {
                    lastMiss = new RayTraceResult(RayTraceResult.Type.MISS, v1, face, currentPos);
                }
            }
        }
        return returnLastUncollidableBlock ? lastMiss : null;
    }
}
