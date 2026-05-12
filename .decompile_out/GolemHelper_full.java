/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.BlockFluidBase
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidHandler
 */
package thaumcraft.common.entities.golems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.Marker;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileEssentiaReservoir;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;

public class GolemHelper {
    public static final double ADJACENT_RANGE = 4.0;
    static HashMap<String, TileJarFillable> jarlist = new HashMap();
    private static ArrayList<Integer> reggedLiquids = null;
    static ArrayList<SortingItemTimeout> itemTimeout = new ArrayList();

    public static ArrayList<IInventory> getMarkedContainers(World world, EntityGolemBase golem) {
        ArrayList<IInventory> results = new ArrayList<IInventory>();
        for (Marker marker : golem.getMarkers()) {
            TileEntity te = world.func_147438_o(marker.x, marker.y, marker.z);
            if (marker.dim != world.field_73011_w.field_76574_g || te == null || !(te instanceof IInventory)) continue;
            results.add((IInventory)te);
            if (InventoryUtils.getDoubleChest(te) == null) continue;
            results.add((IInventory)InventoryUtils.getDoubleChest(te));
        }
        return results;
    }

    public static ArrayList<IInventory> getMarkedContainersAdjacentToGolem(World world, EntityGolemBase golem) {
        ArrayList<IInventory> results = new ArrayList<IInventory>();
        for (IInventory inventory : GolemHelper.getMarkedContainers(world, golem)) {
            TileEntity te = (TileEntity)inventory;
            if (!(golem.func_70092_e((double)te.field_145851_c + 0.5, (double)te.field_145848_d + 0.5, (double)te.field_145849_e + 0.5) < 4.0)) continue;
            results.add(inventory);
            if (InventoryUtils.getDoubleChest(te) == null) continue;
            results.add((IInventory)InventoryUtils.getDoubleChest(te));
        }
        return results;
    }

    public static ArrayList<ChunkCoordinates> getMarkedBlocksAdjacentToGolem(World world, EntityGolemBase golem, byte color) {
        ArrayList<ChunkCoordinates> results = new ArrayList<ChunkCoordinates>();
        ArrayList<Marker> markers = golem.getMarkers();
        for (Marker marker : markers) {
            if (marker.color != color && color != -1 || golem.field_70170_p.func_147438_o(marker.x, marker.y, marker.z) != null && golem.field_70170_p.func_147438_o(marker.x, marker.y, marker.z) instanceof IInventory || !(golem.func_70092_e((double)marker.x + 0.5, (double)marker.y + 0.5, (double)marker.z + 0.5) < 4.0)) continue;
            results.add(new ChunkCoordinates(marker.x, marker.y, marker.z));
        }
        return results;
    }

    public static ArrayList<IInventory> getContainersWithRoom(World world, EntityGolemBase golem, byte color) {
        ArrayList<IInventory> results = new ArrayList<IInventory>();
        block0: for (IInventory inventory : GolemHelper.getMarkedContainers(world, golem)) {
            boolean hasRoom = false;
            for (Integer side : GolemHelper.getMarkedSides(golem, (TileEntity)inventory, color)) {
                ItemStack result = InventoryUtils.placeItemStackIntoInventory(golem.getCarried(), inventory, side, false);
                if (!ItemStack.func_77989_b((ItemStack)result, (ItemStack)golem.itemCarried)) {
                    results.add(inventory);
                    continue block0;
                }
                if (InventoryUtils.getDoubleChest((TileEntity)inventory) == null || ItemStack.func_77989_b((ItemStack)(result = InventoryUtils.placeItemStackIntoInventory(golem.getCarried(), (IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory), side, false)), (ItemStack)golem.itemCarried)) continue;
                results.add((IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory));
            }
        }
        return results;
    }

    public static ArrayList<IInventory> getContainersWithRoom(World world, EntityGolemBase golem, byte color, ItemStack itemToMatch) {
        ArrayList<IInventory> results = new ArrayList<IInventory>();
        block0: for (IInventory inventory : GolemHelper.getMarkedContainers(world, golem)) {
            boolean hasRoom = false;
            for (Integer side : GolemHelper.getMarkedSides(golem, (TileEntity)inventory, color)) {
                ItemStack result = InventoryUtils.placeItemStackIntoInventory(itemToMatch, inventory, side, false);
                if (!ItemStack.func_77989_b((ItemStack)result, (ItemStack)itemToMatch)) {
                    results.add(inventory);
                    continue block0;
                }
                if (InventoryUtils.getDoubleChest((TileEntity)inventory) == null || ItemStack.func_77989_b((ItemStack)(result = InventoryUtils.placeItemStackIntoInventory(itemToMatch, (IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory), side, false)), (ItemStack)itemToMatch)) continue;
                results.add((IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory));
            }
        }
        return results;
    }

    public static List<Integer> getMarkedSides(EntityGolemBase golem, TileEntity tile, byte color) {
        return GolemHelper.getMarkedSides(golem, tile.field_145851_c, tile.field_145848_d, tile.field_145849_e, tile.func_145831_w().field_73011_w.field_76574_g, color);
    }

    public static List<Integer> getMarkedSides(EntityGolemBase golem, int x, int y, int z, int dim, byte color) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        ArrayList<Marker> gm = golem.getMarkers();
        if (gm == null || gm.size() == 0) {
            return out;
        }
        for (int a = 0; a < 6; ++a) {
            Marker marker = new Marker(x, y, z, dim, (byte)a, color);
            if (!GolemHelper.contained(gm, marker)) continue;
            out.add(a);
        }
        return out;
    }

    public static boolean contained(ArrayList<Marker> l, Marker m) {
        for (Marker mark : l) {
            if (!m.equalsFuzzy(mark)) continue;
            return true;
        }
        return false;
    }

    public static ArrayList<IInventory> getContainersWithGoods(World world, EntityGolemBase golem, ItemStack goods, byte color) {
        ArrayList<IInventory> results = new ArrayList<IInventory>();
        block2: for (IInventory inventory : GolemHelper.getMarkedContainers(world, golem)) {
            try {
                for (Integer side : GolemHelper.getMarkedSides(golem, (TileEntity)inventory, color)) {
                    if (InventoryUtils.extractStack(inventory, goods, side, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT(), false) != null) {
                        results.add(inventory);
                        continue block2;
                    }
                    if (InventoryUtils.getDoubleChest((TileEntity)inventory) == null || InventoryUtils.extractStack((IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory), goods, side, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT(), false) == null) continue;
                    results.add((IInventory)InventoryUtils.getDoubleChest((TileEntity)inventory));
                }
            }
            catch (Exception e) {}
        }
        return results;
    }

    public static ArrayList<ItemStack> getMissingItems(EntityGolemBase golem) {
        ForgeDirection facing = ForgeDirection.getOrientation((int)golem.homeFacing);
        ChunkCoordinates home = golem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        int slotCount = golem.inventory.slotCount;
        if (golem.getToggles()[0]) {
            ArrayList<ItemStack> qr = new ArrayList<ItemStack>();
            for (int q = 0; q < slotCount; ++q) {
                ItemStack toCheck = golem.inventory.inventory[q];
                if (toCheck == null) continue;
                ItemStack ret = toCheck.func_77946_l();
                qr.add(ret);
            }
            return qr;
        }
        TileEntity tile = golem.field_70170_p.func_147438_o(cX, cY, cZ);
        if (tile == null) {
            return null;
        }
        ArrayList<ItemStack> qr = new ArrayList<ItemStack>();
        block1: for (int q = 0; q < slotCount; ++q) {
            ItemStack toCheck = golem.inventory.inventory[q];
            if (toCheck == null) continue;
            int foundAmount = 0;
            boolean repeat = true;
            boolean didRepeat = false;
            while (repeat) {
                if (didRepeat) {
                    repeat = false;
                }
                if (tile instanceof ISidedInventory && facing.ordinal() > -1) {
                    ISidedInventory isidedinventory = (ISidedInventory)tile;
                    int[] aint = isidedinventory.func_94128_d(facing.ordinal());
                    for (int j = 0; j < aint.length; ++j) {
                        if (InventoryUtils.areItemStacksEqual(((ISidedInventory)tile).func_70301_a(aint[j]), toCheck, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT()) && (foundAmount += ((ISidedInventory)tile).func_70301_a((int)aint[j]).field_77994_a) >= golem.inventory.getAmountNeededSmart(((ISidedInventory)tile).func_70301_a(aint[j]), golem.getUpgradeAmount(5) > 0)) continue block1;
                    }
                } else {
                    if (!(tile instanceof IInventory)) break;
                    int k = ((IInventory)tile).func_70302_i_();
                    for (int l = 0; l < k; ++l) {
                        if (InventoryUtils.areItemStacksEqual(((IInventory)tile).func_70301_a(l), toCheck, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT()) && (foundAmount += ((IInventory)tile).func_70301_a((int)l).field_77994_a) >= golem.inventory.getAmountNeededSmart(((IInventory)tile).func_70301_a(l), golem.getUpgradeAmount(5) > 0)) continue block1;
                    }
                }
                if (!didRepeat && InventoryUtils.getDoubleChest(tile) != null) {
                    tile = InventoryUtils.getDoubleChest(tile);
                    didRepeat = true;
                    continue;
                }
                repeat = false;
            }
            ItemStack ret = toCheck.func_77946_l();
            ret.field_77994_a -= foundAmount;
            qr.add(ret);
        }
        return qr;
    }

    public static ChunkCoordinates findJarWithRoom(EntityGolemBase golem) {
        ChunkCoordinates dest = null;
        World world = golem.field_70170_p;
        float dmod = golem.getRange();
        dmod *= dmod;
        ArrayList<TileEntity> jars = new ArrayList<TileEntity>();
        ArrayList<TileEntity> others = new ArrayList<TileEntity>();
        for (Marker marker : golem.getMarkers()) {
            TileEntity te = world.func_147438_o(marker.x, marker.y, marker.z);
            if (marker.dim == world.field_73011_w.field_76574_g && te != null && te instanceof TileJarFillable) {
                if (!(te.func_145835_a((double)golem.func_110172_bL().field_71574_a, (double)golem.func_110172_bL().field_71572_b, (double)golem.func_110172_bL().field_71573_c) <= (double)dmod)) continue;
                jars.add((TileJarFillable)te);
                continue;
            }
            if (marker.dim == world.field_73011_w.field_76574_g && te != null && te instanceof TileEssentiaReservoir) {
                TileEssentiaReservoir res = (TileEssentiaReservoir)te;
                if (res.getSuctionAmount(res.facing) <= 0 || res.getSuctionType(res.facing) != null && res.getSuctionType(res.facing) != golem.essentia || !(te.func_145835_a((double)golem.func_110172_bL().field_71574_a, (double)golem.func_110172_bL().field_71572_b, (double)golem.func_110172_bL().field_71573_c) <= (double)dmod)) continue;
                others.add(te);
                continue;
            }
            if (marker.dim != world.field_73011_w.field_76574_g || te == null || !(te instanceof IEssentiaTransport)) continue;
            IEssentiaTransport trans = (IEssentiaTransport)te;
            if (golem.essentia == null || golem.essentiaAmount <= 0 || !trans.canInputFrom(ForgeDirection.getOrientation((int)marker.side)) || trans.getSuctionAmount(ForgeDirection.getOrientation((int)marker.side)) <= 0 || trans.getSuctionType(ForgeDirection.getOrientation((int)marker.side)) != null && trans.getSuctionType(ForgeDirection.getOrientation((int)marker.side)) != golem.essentia || !(te.func_145835_a((double)golem.func_110172_bL().field_71574_a, (double)golem.func_110172_bL().field_71572_b, (double)golem.func_110172_bL().field_71573_c) <= (double)dmod)) continue;
            others.add(te);
        }
        if (jars.size() > 0) {
            jarlist.clear();
            for (TileEntity tileEntity : jars) {
                jarlist.put(tileEntity.field_145851_c + ":" + tileEntity.field_145848_d + ":" + tileEntity.field_145849_e, (TileJarFillable)tileEntity);
                GolemHelper.getConnectedJars((TileJarFillable)tileEntity);
            }
        } else if (others.size() == 0) {
            return null;
        }
        jars = new ArrayList();
        for (TileEntity tileEntity : others) {
            jars.add(tileEntity);
        }
        for (TileJarFillable tileJarFillable : jarlist.values()) {
            if (tileJarFillable.aspect == null || tileJarFillable.amount <= 0 || tileJarFillable.amount >= tileJarFillable.maxAmount || tileJarFillable.aspectFilter == null || golem.essentia == null || golem.essentiaAmount <= 0 || !tileJarFillable.aspect.equals(golem.essentia) || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
            jars.add(tileJarFillable);
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect != null && tileJarFillable.amount != 0 || tileJarFillable.aspectFilter == null || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect == null || tileJarFillable.amount < tileJarFillable.maxAmount || !(tileJarFillable instanceof TileJarFillableVoid) || tileJarFillable.aspectFilter == null || golem.essentia == null || golem.essentiaAmount <= 0 || !tileJarFillable.aspect.equals(golem.essentia) || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect == null || tileJarFillable.amount <= 0 || tileJarFillable.amount >= tileJarFillable.maxAmount || tileJarFillable.aspectFilter != null || golem.essentia == null || golem.essentiaAmount <= 0 || !tileJarFillable.aspect.equals(golem.essentia) || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect != null && tileJarFillable.amount != 0 || tileJarFillable.aspectFilter != null || tileJarFillable instanceof TileJarFillableVoid || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect == null || !(tileJarFillable instanceof TileJarFillableVoid) || tileJarFillable.aspectFilter != null || golem.essentia == null || golem.essentiaAmount <= 0 || !tileJarFillable.aspect.equals(golem.essentia) || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        if (jars.size() == 0) {
            for (TileJarFillable tileJarFillable : jarlist.values()) {
                if (tileJarFillable.aspect != null && tileJarFillable.amount != 0 || tileJarFillable.aspectFilter != null || !(tileJarFillable instanceof TileJarFillableVoid) || !tileJarFillable.doesContainerAccept(golem.essentia)) continue;
                jars.add(tileJarFillable);
            }
        }
        double dist = Double.MAX_VALUE;
        for (TileEntity jar : jars) {
            double d = jar.func_145835_a((double)golem.func_110172_bL().field_71574_a, (double)golem.func_110172_bL().field_71572_b, (double)golem.func_110172_bL().field_71573_c);
            if (jar instanceof TileJarFillableVoid) {
                d += (double)dmod;
            }
            if (!(d < dist)) continue;
            dist = d;
            dest = new ChunkCoordinates(jar.field_145851_c, jar.field_145848_d, jar.field_145849_e);
        }
        jarlist.clear();
        return dest;
    }

    private static void getConnectedJars(TileJarFillable jar) {
        World world = jar.func_145831_w();
        for (int dir = 0; dir < 6; ++dir) {
            TileEntity te;
            ForgeDirection fd = ForgeDirection.getOrientation((int)dir);
            int xx = jar.field_145851_c + fd.offsetX;
            int yy = jar.field_145848_d + fd.offsetY;
            int zz = jar.field_145849_e + fd.offsetZ;
            if (jarlist.containsKey(xx + ":" + yy + ":" + zz) || (te = world.func_147438_o(xx, yy, zz)) == null || !(te instanceof TileJarFillable)) continue;
            jarlist.put(te.field_145851_c + ":" + te.field_145848_d + ":" + te.field_145849_e, (TileJarFillable)te);
            GolemHelper.getConnectedJars((TileJarFillable)te);
        }
    }

    public static ArrayList<Integer> getReggedLiquids() {
        if (reggedLiquids == null) {
            reggedLiquids = new ArrayList();
            for (Integer f : FluidRegistry.getRegisteredFluidIDs().values()) {
                reggedLiquids.add(f);
            }
        }
        return reggedLiquids;
    }

    public static ArrayList<FluidStack> getMissingLiquids(EntityGolemBase golem) {
        ArrayList<FluidStack> out = new ArrayList<FluidStack>();
        ForgeDirection facing = ForgeDirection.getOrientation((int)golem.homeFacing);
        ChunkCoordinates home = golem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        TileEntity tile = golem.field_70170_p.func_147438_o(cX, cY, cZ);
        if (tile != null && tile instanceof IFluidHandler) {
            IFluidHandler fluidhandler = (IFluidHandler)tile;
            for (Integer id : GolemHelper.getReggedLiquids()) {
                if (golem.fluidCarried != null && golem.fluidCarried.amount > 0 && golem.fluidCarried.fluidID != id || !fluidhandler.canFill(facing, FluidRegistry.getFluid((int)id))) continue;
                FluidStack fs = new FluidStack(FluidRegistry.getFluid((int)id), Integer.MAX_VALUE);
                if (golem.inventory.hasSomething()) {
                    FluidStack fis = null;
                    boolean found = false;
                    for (int a = 0; a < golem.inventory.slotCount; ++a) {
                        fis = FluidContainerRegistry.getFluidForFilledItem((ItemStack)golem.inventory.func_70301_a(a));
                        if (fis == null || !fis.isFluidEqual(fs)) continue;
                        found = true;
                        break;
                    }
                    if (!found) continue;
                }
                out.add(new FluidStack(id.intValue(), Integer.MAX_VALUE));
            }
        }
        return out;
    }

    public static Vec3 findPossibleLiquid(FluidStack ls, EntityGolemBase golem) {
        double d;
        ForgeDirection facing = ForgeDirection.getOrientation((int)golem.homeFacing);
        ChunkCoordinates home = golem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        float dmod = golem.getRange();
        ChunkCoordinates v = null;
        ArrayList<IFluidHandler> fluidhandlers = GolemHelper.getMarkedFluidHandlers(ls, golem.field_70170_p, golem);
        double dd = Double.MAX_VALUE;
        if (fluidhandlers != null) {
            for (IFluidHandler fluidhandler : fluidhandlers) {
                if (fluidhandler == null) continue;
                TileEntity tile = (TileEntity)fluidhandler;
                d = golem.func_70092_e((double)tile.field_145851_c + 0.5, (double)tile.field_145848_d + 0.5, (double)tile.field_145849_e + 0.5);
                if (!(d <= (double)(dmod * dmod)) || !(d < dd)) continue;
                dd = d;
                v = new ChunkCoordinates(tile.field_145851_c, tile.field_145848_d, tile.field_145849_e);
            }
        }
        if (v == null) {
            ArrayList<ChunkCoordinates> inworld = GolemHelper.getMarkedFluidBlocks(ls, golem.field_70170_p, golem);
            dd = Double.MAX_VALUE;
            if (inworld != null) {
                for (ChunkCoordinates coord : inworld) {
                    if (coord == null || !((d = golem.func_70092_e((double)coord.field_71574_a + 0.5, (double)coord.field_71572_b + 0.5, (double)coord.field_71573_c + 0.5)) <= (double)(dmod * dmod)) || !(d < dd)) continue;
                    dd = d;
                    v = new ChunkCoordinates(coord.field_71574_a, coord.field_71572_b, coord.field_71573_c);
                }
            }
        }
        if (v != null) {
            return Vec3.func_72443_a((double)v.field_71574_a, (double)v.field_71572_b, (double)v.field_71573_c);
        }
        return null;
    }

    public static ArrayList<Marker> getMarkedFluidHandlersAdjacentToGolem(FluidStack ls, World world, EntityGolemBase golem) {
        ArrayList<Marker> results = new ArrayList<Marker>();
        for (Marker marker : golem.getMarkers()) {
            FluidStack fs;
            TileEntity te = world.func_147438_o(marker.x, marker.y, marker.z);
            if (marker.dim != world.field_73011_w.field_76574_g || te == null || !(te instanceof IFluidHandler) || !(golem.func_70092_e((double)te.field_145851_c + 0.5, (double)te.field_145848_d + 0.5, (double)te.field_145849_e + 0.5) < 4.0) || (fs = ((IFluidHandler)te).drain(ForgeDirection.getOrientation((int)marker.side), new FluidStack(ls.getFluid(), 1), false)) == null || fs.amount <= 0) continue;
            results.add(marker);
        }
        return results;
    }

    public static ArrayList<IFluidHandler> getMarkedFluidHandlers(FluidStack ls, World world, EntityGolemBase golem) {
        ArrayList<IFluidHandler> results = new ArrayList<IFluidHandler>();
        for (Marker marker : golem.getMarkers()) {
            FluidStack fs;
            TileEntity te = world.func_147438_o(marker.x, marker.y, marker.z);
            if (marker.dim != world.field_73011_w.field_76574_g || te == null || !(te instanceof IFluidHandler) || (fs = ((IFluidHandler)te).drain(ForgeDirection.getOrientation((int)marker.side), new FluidStack(ls.getFluid(), 1), false)) == null || fs.amount <= 0) continue;
            results.add((IFluidHandler)te);
        }
        return results;
    }

    public static ArrayList<ChunkCoordinates> getMarkedFluidBlocks(FluidStack ls, World world, EntityGolemBase golem) {
        ArrayList<ChunkCoordinates> results = new ArrayList<ChunkCoordinates>();
        for (Marker marker : golem.getMarkers()) {
            Block bi = world.func_147439_a(marker.x, marker.y, marker.z);
            if (marker.dim != world.field_73011_w.field_76574_g || FluidRegistry.getFluid((int)ls.fluidID).getBlock() != bi) continue;
            if (bi instanceof BlockFluidBase && ((BlockFluidBase)bi).canDrain(world, marker.x, marker.y, marker.z)) {
                results.add(new ChunkCoordinates(marker.x, marker.y, marker.z));
                continue;
            }
            if (ls.fluidID != FluidRegistry.WATER.getID() && ls.fluidID != FluidRegistry.LAVA.getID()) continue;
            int wmd = world.func_72805_g(marker.x, marker.y, marker.z);
            if ((FluidRegistry.lookupFluidForBlock((Block)bi) != FluidRegistry.WATER || ls.fluidID != FluidRegistry.WATER.getID()) && (FluidRegistry.lookupFluidForBlock((Block)bi) != FluidRegistry.LAVA || ls.fluidID != FluidRegistry.LAVA.getID()) || wmd != 0) continue;
            results.add(new ChunkCoordinates(marker.x, marker.y, marker.z));
        }
        return results;
    }

    public static ArrayList<ItemStack> getItemsNeeded(EntityGolemBase golem, boolean fuzzy) {
        ArrayList<ItemStack> needed = null;
        switch (golem.getCore()) {
            case 1: {
                needed = golem.inventory.getItemsNeeded(golem.getUpgradeAmount(5) > 0);
                if (needed.size() == 0) {
                    return null;
                }
                return GolemHelper.filterEmptyCore(golem, needed);
            }
            case 8: {
                needed = golem.inventory.getItemsNeeded(golem.getUpgradeAmount(5) > 0);
                if (needed.size() == 0) {
                    return null;
                }
                return GolemHelper.filterUseCore(golem, needed);
            }
            case 10: {
                needed = GolemHelper.getItemsInHomeContainer(golem);
                return GolemHelper.filterSortCore(golem, needed);
            }
        }
        return needed;
    }

    private static ArrayList<ItemStack> filterEmptyCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (ItemStack itemToMatch : in) {
            if (GolemHelper.isOnTimeOut(golem, itemToMatch) || !GolemHelper.findSomethingEmptyCore(golem, itemToMatch)) continue;
            out.add(itemToMatch);
        }
        return out;
    }

    private static ArrayList<ItemStack> filterUseCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (ItemStack itemToMatch : in) {
            if (GolemHelper.isOnTimeOut(golem, itemToMatch) || !GolemHelper.findSomethingUseCore(golem, itemToMatch)) continue;
            out.add(itemToMatch);
        }
        return out;
    }

    private static ArrayList<ItemStack> filterSortCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (ItemStack itemToMatch : in) {
            if (GolemHelper.isOnTimeOut(golem, itemToMatch) || !GolemHelper.findSomethingSortCore(golem, itemToMatch)) continue;
            out.add(itemToMatch);
        }
        return out;
    }

    public static boolean findSomethingUseCore(EntityGolemBase golem, ItemStack itemToMatch) {
        ArrayList<Byte> matchingColors = golem.getColorsMatching(itemToMatch);
        for (byte col : matchingColors) {
            ArrayList<Marker> markers = golem.getMarkers();
            for (Marker marker : markers) {
                if (marker.color != col && col != -1 || golem.getToggles()[0] && !golem.field_70170_p.func_147437_c(marker.x, marker.y, marker.z) || !golem.getToggles()[0] && golem.field_70170_p.func_147437_c(marker.x, marker.y, marker.z)) continue;
                ForgeDirection opp = ForgeDirection.getOrientation((int)marker.side);
                if (!golem.field_70170_p.func_147437_c(marker.x + opp.offsetX, marker.y + opp.offsetY, marker.z + opp.offsetZ)) continue;
                return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.func_145782_y(), itemToMatch.func_77946_l(), System.currentTimeMillis() + (long)Config.golemIgnoreDelay));
        return false;
    }

    public static boolean findSomethingEmptyCore(EntityGolemBase golem, ItemStack itemToMatch) {
        ArrayList<Object> markers;
        ArrayList<Byte> matchingColors = golem.getColorsMatching(itemToMatch);
        for (byte color : matchingColors) {
            markers = GolemHelper.getContainersWithRoom(golem.field_70170_p, golem, color, itemToMatch);
            if (markers.size() == 0) continue;
            ForgeDirection i$1 = ForgeDirection.getOrientation((int)golem.homeFacing);
            ChunkCoordinates chunkCoordinates = golem.func_110172_bL();
            int cX = chunkCoordinates.field_71574_a - i$1.offsetX;
            int cY = chunkCoordinates.field_71572_b - i$1.offsetY;
            int cZ = chunkCoordinates.field_71573_c - i$1.offsetZ;
            double range = Double.MAX_VALUE;
            float dmod = golem.getRange();
            for (IInventory iInventory : markers) {
                double distance = golem.func_70092_e((double)((TileEntity)iInventory).field_145851_c + 0.5, (double)((TileEntity)iInventory).field_145848_d + 0.5, (double)((TileEntity)iInventory).field_145849_e + 0.5);
                if (!(distance < range) || !(distance <= (double)(dmod * dmod)) || ((TileEntity)iInventory).field_145851_c == cX && ((TileEntity)iInventory).field_145848_d == cY && ((TileEntity)iInventory).field_145849_e == cZ) continue;
                return true;
            }
        }
        for (byte color : matchingColors) {
            markers = golem.getMarkers();
            for (Marker marker : markers) {
                if (marker.color != color && color != -1 || golem.field_70170_p.func_147438_o(marker.x, marker.y, marker.z) != null && golem.field_70170_p.func_147438_o(marker.x, marker.y, marker.z) instanceof IInventory) continue;
                return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.func_145782_y(), itemToMatch.func_77946_l(), System.currentTimeMillis() + (long)Config.golemIgnoreDelay));
        return false;
    }

    public static boolean findSomethingSortCore(EntityGolemBase golem, ItemStack itemToMatch) {
        ArrayList<IInventory> markers = GolemHelper.getContainersWithRoom(golem.field_70170_p, golem, (byte)-1, itemToMatch);
        if (markers.size() != 0) {
            ForgeDirection i$1 = ForgeDirection.getOrientation((int)golem.homeFacing);
            ChunkCoordinates marker = golem.func_110172_bL();
            int cX = marker.field_71574_a - i$1.offsetX;
            int cY = marker.field_71572_b - i$1.offsetY;
            int cZ = marker.field_71573_c - i$1.offsetZ;
            double range = Double.MAX_VALUE;
            float dmod = golem.getRange();
            for (IInventory te : markers) {
                double distance = golem.func_70092_e((double)((TileEntity)te).field_145851_c + 0.5, (double)((TileEntity)te).field_145848_d + 0.5, (double)((TileEntity)te).field_145849_e + 0.5);
                if (!(distance < range) || !(distance <= (double)(dmod * dmod)) || ((TileEntity)te).field_145851_c == cX && ((TileEntity)te).field_145848_d == cY && ((TileEntity)te).field_145849_e == cZ) continue;
                for (int side : GolemHelper.getMarkedSides(golem, (TileEntity)te, (byte)-1)) {
                    if (!InventoryUtils.inventoryContains(te, itemToMatch, side, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) continue;
                    return true;
                }
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.func_145782_y(), itemToMatch.func_77946_l(), System.currentTimeMillis() + (long)Config.golemIgnoreDelay));
        return false;
    }

    public static boolean isOnTimeOut(EntityGolemBase golem, ItemStack stack) {
        SortingItemTimeout tos = new SortingItemTimeout(golem.func_145782_y(), stack, 0L);
        if (itemTimeout.contains(tos)) {
            int q = itemTimeout.indexOf(tos);
            SortingItemTimeout tos2 = itemTimeout.get(q);
            if (System.currentTimeMillis() < tos2.time) {
                return true;
            }
            itemTimeout.remove(q);
        }
        return false;
    }

    public static boolean validTargetForItem(EntityGolemBase golem, ItemStack stack) {
        if (GolemHelper.isOnTimeOut(golem, stack)) {
            return false;
        }
        ForgeDirection facing = ForgeDirection.getOrientation((int)golem.homeFacing);
        ChunkCoordinates home = golem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        switch (golem.getCore()) {
            case 1: {
                return GolemHelper.findSomethingEmptyCore(golem, stack);
            }
            case 8: {
                return GolemHelper.findSomethingUseCore(golem, stack);
            }
            case 10: {
                return GolemHelper.findSomethingSortCore(golem, stack);
            }
        }
        TileEntity tile = golem.field_70170_p.func_147438_o(cX, cY, cZ);
        ArrayList<ItemStack> neededList = GolemHelper.getItemsNeeded(golem, golem.getUpgradeAmount(5) > 0);
        if (neededList != null && neededList.size() > 0) {
            for (ItemStack ss : neededList) {
                if (!InventoryUtils.areItemStacksEqual(ss, golem.itemCarried, golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) continue;
                return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.func_145782_y(), stack.func_77946_l(), System.currentTimeMillis() + (long)Config.golemIgnoreDelay));
        return false;
    }

    public static ItemStack getFirstItemUsingTimeout(EntityGolemBase golem, IInventory inventory, int side, boolean doit) {
        ItemStack stack1 = null;
        if (inventory instanceof ISidedInventory && side > -1) {
            ISidedInventory isidedinventory = (ISidedInventory)inventory;
            int[] aint = isidedinventory.func_94128_d(side);
            for (int j = 0; j < aint.length; ++j) {
                if (stack1 == null && inventory.func_70301_a(aint[j]) != null) {
                    if (GolemHelper.isOnTimeOut(golem, inventory.func_70301_a(aint[j]))) continue;
                    stack1 = inventory.func_70301_a(aint[j]).func_77946_l();
                    stack1.field_77994_a = golem.getCarrySpace();
                }
                if (stack1 != null) {
                    stack1 = InventoryUtils.attemptExtraction(inventory, stack1, aint[j], side, false, false, false, doit);
                }
                if (stack1 == null) {
                    continue;
                }
                break;
            }
        } else {
            int k = inventory.func_70302_i_();
            for (int l = 0; l < k; ++l) {
                if (stack1 == null && inventory.func_70301_a(l) != null) {
                    if (GolemHelper.isOnTimeOut(golem, inventory.func_70301_a(l))) continue;
                    stack1 = inventory.func_70301_a(l).func_77946_l();
                    stack1.field_77994_a = golem.getCarrySpace();
                }
                if (stack1 != null) {
                    stack1 = InventoryUtils.attemptExtraction(inventory, stack1, l, side, false, false, false, doit);
                }
                if (stack1 == null) {
                    continue;
                }
                break;
            }
        }
        if (stack1 == null || stack1.field_77994_a == 0) {
            if (doit) {
                inventory.func_70296_d();
            }
            return null;
        }
        return stack1.func_77946_l();
    }

    public static ArrayList<ItemStack> getItemsInHomeContainer(EntityGolemBase golem) {
        ForgeDirection facing = ForgeDirection.getOrientation((int)golem.homeFacing);
        ChunkCoordinates home = golem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        TileEntity tile = golem.field_70170_p.func_147438_o(cX, cY, cZ);
        if (tile == null || !(tile instanceof IInventory)) {
            return null;
        }
        int[] aint = null;
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        IInventory inv = (IInventory)tile;
        if (tile instanceof ISidedInventory && facing.ordinal() > -1) {
            aint = ((ISidedInventory)inv).func_94128_d(facing.ordinal());
        } else {
            aint = new int[inv.func_70302_i_()];
            for (int a = 0; a < inv.func_70302_i_(); ++a) {
                aint[a] = a;
            }
        }
        if (aint != null && aint.length > 0) {
            for (int j = 0; j < aint.length; ++j) {
                if (inv.func_70301_a(aint[j]) == null) continue;
                out.add(inv.func_70301_a(aint[j]).func_77946_l());
            }
        }
        return out;
    }

    public static class SortingItemTimeout
    implements Comparable {
        ItemStack stack = null;
        int golemId = 0;
        long time = 0L;

        public SortingItemTimeout(int golemId, ItemStack stack, long time) {
            this.stack = stack;
            this.golemId = golemId;
            this.time = time;
        }

        public int compareTo(Object arg0) {
            return this.equals(arg0) ? 0 : -1;
        }

        public boolean equals(Object obj) {
            if (obj instanceof SortingItemTimeout) {
                SortingItemTimeout t = (SortingItemTimeout)obj;
                if (this.golemId != t.golemId) {
                    return false;
                }
                if (!this.stack.func_77969_a(t.stack)) {
                    return false;
                }
                if (!ItemStack.func_77970_a((ItemStack)this.stack, (ItemStack)t.stack)) {
                    return false;
                }
            }
            return true;
        }
    }
}
