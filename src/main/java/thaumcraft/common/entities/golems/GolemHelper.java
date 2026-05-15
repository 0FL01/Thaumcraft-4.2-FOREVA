package thaumcraft.common.entities.golems;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileEssentiaReservoir;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

public class GolemHelper {

    public static final double ADJACENT_RANGE = 4.0;
    private static ArrayList<SortingItemTimeout> itemTimeout = new ArrayList<>();

    // --- Essentia/Liquid scanning state ---
    public static HashMap<String, TileJarFillable> jarlist = new HashMap<>();
    private static ArrayList<Fluid> reggedLiquids = new ArrayList<>();

    private static void ensureLiquidsRegistered() {
        if (reggedLiquids.isEmpty()) {
            reggedLiquids.addAll(FluidRegistry.getRegisteredFluids().values());
        }
    }

    // --- Marker/Container Discovery ---

    public static ArrayList<IInventory> getMarkedContainers(World world, EntityGolemBase golem) {
        ArrayList<IInventory> results = new ArrayList<>();
        for (Marker marker : golem.getMarkers()) {
            if (marker.dim != world.provider.getDimension()) continue;
            TileEntity te = world.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
            if (te == null || !(te instanceof IInventory)) continue;
            results.add((IInventory) te);
            TileEntity dc = InventoryUtils.getDoubleChest(te);
            if (dc != null) results.add((IInventory) dc);
        }
        return results;
    }

    public static ArrayList<IInventory> getMarkedContainersAdjacentToGolem(World world, EntityGolemBase golem) {
        ArrayList<IInventory> results = new ArrayList<>();
        for (IInventory inv : getMarkedContainers(world, golem)) {
            TileEntity te = (TileEntity) inv;
            double dist = golem.getDistanceSq(te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5);
            if (dist < ADJACENT_RANGE) {
                results.add(inv);
                TileEntity dc = InventoryUtils.getDoubleChest(te);
                if (dc != null) results.add((IInventory) dc);
            }
        }
        return results;
    }

    public static ArrayList<BlockPos> getMarkedBlocksAdjacentToGolem(World world, EntityGolemBase golem, byte color) {
        ArrayList<BlockPos> results = new ArrayList<>();
        for (Marker marker : golem.getMarkers()) {
            if (marker.color != color && color != -1) continue;
            TileEntity te = world.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
            if (te != null && te instanceof IInventory) continue;
            double dist = golem.getDistanceSq(marker.x + 0.5, marker.y + 0.5, marker.z + 0.5);
            if (dist < ADJACENT_RANGE) {
                results.add(new BlockPos(marker.x, marker.y, marker.z));
            }
        }
        return results;
    }

    public static ArrayList<IInventory> getContainersWithRoom(World world, EntityGolemBase golem, byte color) {
        ArrayList<IInventory> results = new ArrayList<>();
        for (IInventory inv : getMarkedContainers(world, golem)) {
            TileEntity te = (TileEntity) inv;
            for (Integer side : getMarkedSides(golem, te, color)) {
                ItemStack result = InventoryUtils.placeItemStackIntoInventory(golem.getCarried(), inv, side, false);
                if (!ItemStack.areItemStacksEqual(result, golem.itemCarried)) {
                    results.add(inv);
                    break;
                }
                TileEntity dc = InventoryUtils.getDoubleChest(te);
                if (dc != null) {
                    result = InventoryUtils.placeItemStackIntoInventory(golem.getCarried(), (IInventory) dc, side, false);
                    if (!ItemStack.areItemStacksEqual(result, golem.itemCarried)) {
                        results.add((IInventory) dc);
                        break;
                    }
                }
            }
        }
        return results;
    }

    public static ArrayList<IInventory> getContainersWithRoom(World world, EntityGolemBase golem, byte color, ItemStack itemToMatch) {
        ArrayList<IInventory> results = new ArrayList<>();
        for (IInventory inv : getMarkedContainers(world, golem)) {
            TileEntity te = (TileEntity) inv;
            for (Integer side : getMarkedSides(golem, te, color)) {
                ItemStack result = InventoryUtils.placeItemStackIntoInventory(itemToMatch, inv, side, false);
                if (!ItemStack.areItemStacksEqual(result, itemToMatch)) {
                    results.add(inv);
                    break;
                }
                TileEntity dc = InventoryUtils.getDoubleChest(te);
                if (dc != null) {
                    result = InventoryUtils.placeItemStackIntoInventory(itemToMatch, (IInventory) dc, side, false);
                    if (!ItemStack.areItemStacksEqual(result, itemToMatch)) {
                        results.add((IInventory) dc);
                        break;
                    }
                }
            }
        }
        return results;
    }

    // --- Side Discovery ---

    public static List<Integer> getMarkedSides(EntityGolemBase golem, TileEntity tile, byte color) {
        return getMarkedSides(golem, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(),
            tile.getWorld().provider.getDimension(), color);
    }

    public static List<Integer> getMarkedSides(EntityGolemBase golem, int x, int y, int z, int dim, byte color) {
        ArrayList<Integer> out = new ArrayList<>();
        ArrayList<Marker> gm = golem.getMarkers();
        if (gm == null || gm.isEmpty()) return out;
        for (int a = 0; a < 6; a++) {
            Marker marker = new Marker(x, y, z, (byte) dim, (byte) a, color);
            if (contained(gm, marker)) out.add(a);
        }
        return out;
    }

    private static boolean contained(ArrayList<Marker> list, Marker m) {
        for (Marker mark : list) {
            if (m.equalsFuzzy(mark)) return true;
        }
        return false;
    }

    // --- Goods Discovery ---

    public static ArrayList<IInventory> getContainersWithGoods(World world, EntityGolemBase golem, ItemStack goods, byte color) {
        ArrayList<IInventory> results = new ArrayList<>();
        for (IInventory inv : getMarkedContainers(world, golem)) {
            try {
                TileEntity te = (TileEntity) inv;
                for (Integer side : getMarkedSides(golem, te, color)) {
                    ItemStack extracted = InventoryUtils.extractStack(inv, goods, side,
                        golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT(), false);
                    if (!extracted.isEmpty()) {
                        results.add(inv);
                        break;
                    }
                    TileEntity dc = InventoryUtils.getDoubleChest(te);
                    if (dc != null) {
                        extracted = InventoryUtils.extractStack((IInventory) dc, goods, side,
                            golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT(), false);
                        if (!extracted.isEmpty()) {
                            results.add((IInventory) dc);
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return results;
    }

    // --- Missing Items ---

    public static ArrayList<ItemStack> getMissingItems(EntityGolemBase golem) {
        EnumFacing facing = EnumFacing.VALUES[golem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = golem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        int slotCount = golem.inventory.slotCount;

        if (golem.getToggles()[0]) {
            ArrayList<ItemStack> qr = new ArrayList<>();
            for (int q = 0; q < slotCount; q++) {
                ItemStack toCheck = golem.inventory.inventory[q];
                if (toCheck == null || toCheck.isEmpty()) continue;
                qr.add(toCheck.copy());
            }
            return qr;
        }

        TileEntity tile = golem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        if (tile == null) return null;

        ArrayList<ItemStack> qr = new ArrayList<>();
        for (int q = 0; q < slotCount; q++) {
            ItemStack toCheck = golem.inventory.inventory[q];
            if (toCheck == null || toCheck.isEmpty()) continue;
            int foundAmount = 0;
            TileEntity currentTile = tile;
            boolean repeat = true;
            boolean didRepeat = false;
            while (repeat) {
                if (didRepeat) repeat = false;
                if (currentTile instanceof ISidedInventory) {
                    ISidedInventory sided = (ISidedInventory) currentTile;
                    int[] slots = sided.getSlotsForFace(facing);
                    for (int slot : slots) {
                        ItemStack slotStack = sided.getStackInSlot(slot);
                        if (slotStack.isEmpty()) continue;
                        if (InventoryUtils.areItemStacksEqual(slotStack, toCheck,
                            golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) {
                            foundAmount += slotStack.getCount();
                            if (foundAmount >= golem.inventory.getAmountNeededSmart(slotStack,
                                golem.checkOreDict())) {
                                break;
                            }
                        }
                    }
                    if (foundAmount >= golem.inventory.getAmountNeededSmart(toCheck, golem.checkOreDict())) {
                        break;
                    }
                } else if (currentTile instanceof IInventory) {
                    IInventory inv = (IInventory) currentTile;
                    int k = inv.getSizeInventory();
                    for (int l = 0; l < k; l++) {
                        ItemStack slotStack = inv.getStackInSlot(l);
                        if (slotStack.isEmpty()) continue;
                        if (InventoryUtils.areItemStacksEqual(slotStack, toCheck,
                            golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) {
                            foundAmount += slotStack.getCount();
                            if (foundAmount >= golem.inventory.getAmountNeededSmart(slotStack,
                                golem.checkOreDict())) {
                                break;
                            }
                        }
                    }
                    if (foundAmount >= golem.inventory.getAmountNeededSmart(toCheck, golem.checkOreDict())) {
                        break;
                    }
                } else {
                    break;
                }
                if (!didRepeat) {
                    TileEntity dc = InventoryUtils.getDoubleChest(currentTile);
                    if (dc != null) {
                        currentTile = dc;
                        didRepeat = true;
                        continue;
                    }
                }
                repeat = false;
            }
            ItemStack ret = toCheck.copy();
            int needed = ret.getCount();
            ret.setCount(Math.max(0, needed - foundAmount));
            if (ret.getCount() > 0) qr.add(ret);
        }
        return qr;
    }

    // --- Items Needed ---

    public static ArrayList<ItemStack> getItemsNeeded(EntityGolemBase golem, boolean fuzzy) {
        ArrayList<ItemStack> needed = null;
        switch (golem.getCore()) {
            case 1:
                needed = golem.inventory.getItemsNeeded(fuzzy);
                if (needed == null || needed.isEmpty()) return null;
                return filterEmptyCore(golem, needed);
            case 8:
                needed = golem.inventory.getItemsNeeded(fuzzy);
                if (needed == null || needed.isEmpty()) return null;
                return filterUseCore(golem, needed);
            case 10:
                needed = getItemsInHomeContainer(golem);
                return filterSortCore(golem, needed);
        }
        return needed;
    }

    private static ArrayList<ItemStack> filterEmptyCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : in) {
            if (stack == null || stack.isEmpty()) continue;
            if (isOnTimeOut(golem, stack)) continue;
            if (findSomethingEmptyCore(golem, stack)) out.add(stack);
        }
        return out;
    }

    private static ArrayList<ItemStack> filterUseCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : in) {
            if (stack == null || stack.isEmpty()) continue;
            if (isOnTimeOut(golem, stack)) continue;
            if (findSomethingUseCore(golem, stack)) out.add(stack);
        }
        return out;
    }

    private static ArrayList<ItemStack> filterSortCore(EntityGolemBase golem, ArrayList<ItemStack> in) {
        ArrayList<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : in) {
            if (stack == null || stack.isEmpty()) continue;
            if (isOnTimeOut(golem, stack)) continue;
            if (findSomethingSortCore(golem, stack)) out.add(stack);
        }
        return out;
    }

    // --- Core behavior finders ---

    public static boolean findSomethingUseCore(EntityGolemBase golem, ItemStack itemToMatch) {
        if (itemToMatch == null || itemToMatch.isEmpty()) return false;
        ArrayList<Byte> matchingColors = golem.getColorsMatching(itemToMatch);
        for (byte col : matchingColors) {
            for (Marker marker : golem.getMarkers()) {
                if (marker.color != col && col != -1) continue;
                boolean isAir = golem.world.isAirBlock(new BlockPos(marker.x, marker.y, marker.z));
                if (golem.getToggles()[0] && !isAir) continue;
                if (!golem.getToggles()[0] && isAir) continue;
                EnumFacing opp = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
                if (!golem.world.isAirBlock(new BlockPos(marker.x + opp.getXOffset(), marker.y + opp.getYOffset(), marker.z + opp.getZOffset()))) continue;
                return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.getEntityId(), itemToMatch.copy(), System.currentTimeMillis() + Config.golemIgnoreDelay));
        return false;
    }

    public static boolean findSomethingEmptyCore(EntityGolemBase golem, ItemStack itemToMatch) {
        if (itemToMatch == null || itemToMatch.isEmpty()) return false;
        ArrayList<Byte> matchingColors = golem.getColorsMatching(itemToMatch);
        for (byte col : matchingColors) {
            ArrayList<IInventory> markers = getContainersWithRoom(golem.world, golem, col, itemToMatch);
            if (markers.isEmpty()) continue;
            EnumFacing facing = EnumFacing.VALUES[golem.homeFacing % EnumFacing.VALUES.length];
            BlockPos home = golem.getHomePosition();
            int cX = home.getX() - facing.getXOffset();
            int cY = home.getY() - facing.getYOffset();
            int cZ = home.getZ() - facing.getZOffset();
            double range = Double.MAX_VALUE;
            float dmod = golem.getRange();
            for (IInventory inv : markers) {
                TileEntity te = (TileEntity) inv;
                double dist = golem.getDistanceSq(te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5);
                if (dist < range && dist <= dmod * dmod && te.getPos().getX() == cX && te.getPos().getY() == cY && te.getPos().getZ() == cZ) continue;
                if (dist < range && dist <= dmod * dmod) {
                    range = dist;
                    if (range <= dmod * dmod) return true;
                }
            }
        }
        for (byte col : matchingColors) {
            for (Marker marker : golem.getMarkers()) {
                if (marker.color != col && col != -1) continue;
                TileEntity te = golem.world.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
                if (te != null && te instanceof IInventory) continue;
                return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.getEntityId(), itemToMatch.copy(), System.currentTimeMillis() + Config.golemIgnoreDelay));
        return false;
    }

    public static boolean findSomethingSortCore(EntityGolemBase golem, ItemStack itemToMatch) {
        if (itemToMatch == null || itemToMatch.isEmpty()) return false;
        ArrayList<IInventory> markers = getContainersWithRoom(golem.world, golem, (byte) -1, itemToMatch);
        if (!markers.isEmpty()) {
            EnumFacing facing = EnumFacing.VALUES[golem.homeFacing % EnumFacing.VALUES.length];
            BlockPos home = golem.getHomePosition();
            int cX = home.getX() - facing.getXOffset();
            int cY = home.getY() - facing.getYOffset();
            int cZ = home.getZ() - facing.getZOffset();
            float dmod = golem.getRange();
            for (IInventory te : markers) {
                TileEntity tile = (TileEntity) te;
                double dist = golem.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5);
                if (dist > dmod * dmod) continue;
                for (int side : getMarkedSides(golem, tile, (byte) -1)) {
                    if (InventoryUtils.inventoryContains(te, itemToMatch, side,
                        golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) return true;
                }
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.getEntityId(), itemToMatch.copy(), System.currentTimeMillis() + Config.golemIgnoreDelay));
        return false;
    }

    // --- Timeout ---

    public static boolean isOnTimeOut(EntityGolemBase golem, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        SortingItemTimeout tos = new SortingItemTimeout(golem.getEntityId(), stack, 0L);
        if (itemTimeout.contains(tos)) {
            int q = itemTimeout.indexOf(tos);
            SortingItemTimeout tos2 = itemTimeout.get(q);
            if (System.currentTimeMillis() < tos2.time) return true;
            itemTimeout.remove(q);
        }
        return false;
    }

    // --- Target Validation ---

    public static boolean validTargetForItem(EntityGolemBase golem, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (isOnTimeOut(golem, stack)) return false;

        switch (golem.getCore()) {
            case 1: return findSomethingEmptyCore(golem, stack);
            case 8: return findSomethingUseCore(golem, stack);
            case 10: return findSomethingSortCore(golem, stack);
        }
        // Default: check home container needs
        ArrayList<ItemStack> neededList = getItemsNeeded(golem, golem.checkOreDict());
        if (neededList != null && !neededList.isEmpty()) {
            for (ItemStack ss : neededList) {
                if (InventoryUtils.areItemStacksEqual(ss, golem.itemCarried,
                    golem.checkOreDict(), golem.ignoreDamage(), golem.ignoreNBT())) return true;
            }
        }
        itemTimeout.add(new SortingItemTimeout(golem.getEntityId(), stack.copy(), System.currentTimeMillis() + Config.golemIgnoreDelay));
        return false;
    }

    // --- First item with timeout ---

    public static ItemStack getFirstItemUsingTimeout(EntityGolemBase golem, IInventory inventory, int side, boolean doit) {
        ItemStack stack1 = null;
        EnumFacing face = (side >= 0 && side < EnumFacing.VALUES.length) ? EnumFacing.VALUES[side] : null;

        if (inventory instanceof ISidedInventory && face != null) {
            ISidedInventory sided = (ISidedInventory) inventory;
            int[] aint = sided.getSlotsForFace(face);
            for (int slot : aint) {
                ItemStack slotStack = inventory.getStackInSlot(slot);
                if (slotStack.isEmpty()) continue;
                if (isOnTimeOut(golem, slotStack)) continue;
                if (stack1 == null) {
                    stack1 = slotStack.copy();
                    stack1.setCount(golem.getCarrySpace());
                }
                if (stack1 != null) {
                    stack1 = InventoryUtils.attemptExtraction(inventory, stack1, slot, face, false, false, false, doit);
                }
                if (stack1 == null) break;
            }
        } else {
            int k = inventory.getSizeInventory();
            for (int l = 0; l < k; l++) {
                ItemStack slotStack = inventory.getStackInSlot(l);
                if (slotStack.isEmpty()) continue;
                if (isOnTimeOut(golem, slotStack)) continue;
                if (stack1 == null) {
                    stack1 = slotStack.copy();
                    stack1.setCount(golem.getCarrySpace());
                }
                if (stack1 != null) {
                    stack1 = InventoryUtils.attemptExtraction(inventory, stack1, l, face, false, false, false, doit);
                }
                if (stack1 == null) break;
            }
        }
        if (stack1 == null || stack1.isEmpty()) {
            if (doit) inventory.markDirty();
            return ItemStack.EMPTY;
        }
        return stack1.copy();
    }

    // --- Home Container ---

    public static ArrayList<ItemStack> getItemsInHomeContainer(EntityGolemBase golem) {
        EnumFacing facing = EnumFacing.VALUES[golem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = golem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = golem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        if (tile == null || !(tile instanceof IInventory)) return null;
        IInventory inv = (IInventory) tile;
        int[] slots;
        if (tile instanceof ISidedInventory && facing.ordinal() >= 0) {
            slots = ((ISidedInventory) inv).getSlotsForFace(facing);
        } else {
            slots = new int[inv.getSizeInventory()];
            for (int a = 0; a < inv.getSizeInventory(); a++) slots[a] = a;
        }
        ArrayList<ItemStack> out = new ArrayList<>();
        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (!s.isEmpty()) out.add(s.copy());
        }
        return out;
    }

    // --- Existing helpers ---
    public static boolean isMarkerWithinDistance(EntityGolemBase golem, Marker marker, double distance) {
        return golem.getDistanceSq(marker.x + 0.5, marker.y + 0.5, marker.z + 0.5) < distance * distance;
    }

    public static boolean hasInventorySpace(EntityGolemBase golem, ItemStack stack) {
        if (golem.inventory == null) return false;
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            ItemStack s = golem.inventory.getStackInSlot(a);
            if (s.isEmpty()) return true;
            if (s.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(s, stack) && s.getCount() < s.getMaxStackSize()) return true;
        }
        return false;
    }

    public static boolean putStackInInventory(EntityGolemBase golem, ItemStack stack) {
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            ItemStack s = golem.inventory.getStackInSlot(a);
            if (s.isEmpty()) {
                golem.inventory.setInventorySlotContents(a, stack.copy());
                return true;
            }
            if (s.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(s, stack) && s.getCount() < s.getMaxStackSize()) {
                s.grow(stack.getCount());
                return true;
            }
        }
        return false;
    }

    public static boolean isStackInInventory(EntityGolemBase golem, ItemStack stack) {
        for (int a = 0; a < golem.inventory.getSizeInventory(); a++) {
            ItemStack s = golem.inventory.getStackInSlot(a);
            if (!s.isEmpty() && s.isItemEqual(stack)) return true;
        }
        return false;
    }

    // --- Connected Jar BFS ---

    public static ArrayList<TileJarFillable> getConnectedJars(TileJarFillable root) {
        ArrayList<TileJarFillable> result = new ArrayList<>();
        if (root == null || root.getWorld() == null || root.getPos() == null) return result;

        Queue<TileJarFillable> queue = new ArrayDeque<>();
        HashSet<String> seen = new HashSet<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TileJarFillable jar = queue.poll();
            if (jar == null || jar.getWorld() == null || jar.getPos() == null) continue;

            BlockPos p = jar.getPos();
            String key = jar.getWorld().provider.getDimension() + ":" + p.getX() + ":" + p.getY() + ":" + p.getZ();
            if (!seen.add(key)) continue;

            result.add(jar);
            jarlist.put(key, jar);

            for (EnumFacing face : EnumFacing.VALUES) {
                BlockPos np = p.offset(face);
                TileEntity te = jar.getWorld().getTileEntity(np);
                if (te instanceof TileJarFillable) {
                    queue.add((TileJarFillable) te);
                }
            }
        }
        return result;
    }

    // --- Find Jar With Room ---

    public static BlockPos findJarWithRoom(EntityGolemBase golem) {
        World world = golem.world;
        float range = golem.getRange();
        float dmod = range * range;

        jarlist.clear();
        if (golem.essentia == null || golem.essentiaAmount <= 0) return null;

        ArrayList<TileJarFillable> markedJars = new ArrayList<>();
        ArrayList<TileEntity> others = new ArrayList<>();

        for (Marker marker : golem.getMarkers()) {
            if (marker.dim != world.provider.getDimension()) continue;
            TileEntity te = world.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
            if (te == null) continue;

            if (te instanceof TileJarFillable) {
                TileJarFillable jar = (TileJarFillable) te;
                if (te.getDistanceSq(golem.getHomePosition().getX(), golem.getHomePosition().getY(), golem.getHomePosition().getZ()) <= dmod) {
                    markedJars.add(jar);
                }
                continue;
            }

            if (te instanceof TileEssentiaReservoir) {
                TileEssentiaReservoir res = (TileEssentiaReservoir) te;
                if (res.getSuctionAmount(res.facing) <= 0) continue;
                Aspect suctionType = res.getSuctionType(res.facing);
                if (suctionType != null && !suctionType.equals(golem.essentia)) continue;
                if (te.getDistanceSq(golem.getHomePosition().getX(), golem.getHomePosition().getY(), golem.getHomePosition().getZ()) > dmod) continue;
                others.add(te);
                continue;
            }

            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport trans = (IEssentiaTransport) te;
                if (golem.essentia == null || golem.essentiaAmount <= 0) continue;
                EnumFacing side = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
                if (!trans.canInputFrom(side)) continue;
                if (trans.getSuctionAmount(side) <= 0) continue;
                Aspect suctType = trans.getSuctionType(side);
                if (suctType != null && !suctType.equals(golem.essentia)) continue;
                if (te.getDistanceSq(golem.getHomePosition().getX(), golem.getHomePosition().getY(), golem.getHomePosition().getZ()) > dmod) continue;
                others.add(te);
            }
        }

        if (!markedJars.isEmpty()) {
            for (TileJarFillable jar : markedJars) {
                getConnectedJars(jar);
            }
        } else if (others.isEmpty()) {
            return null;
        }

        ArrayList<TileEntity> candidates = new ArrayList<>();
        candidates.addAll(others);

        // Phase 1: jars with matching aspect and filter, non-full.
        for (TileJarFillable jar : jarlist.values()) {
            if (jar.aspect != null && jar.amount > 0 && jar.amount < jar.maxAmount
                && jar.aspectFilter != null && golem.essentia != null
                && golem.essentiaAmount > 0 && jar.aspect.equals(golem.essentia)
                && jar.doesContainerAccept(golem.essentia)) {
                candidates.add(jar);
            }
        }

        // Phase 2: empty jars with matching filter.
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if ((jar.aspect == null || jar.amount == 0)
                    && jar.aspectFilter != null
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        // Phase 3: void jars with filter.
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if (jar instanceof TileJarFillableVoid
                    && jar.aspect != null && jar.amount >= jar.maxAmount
                    && jar.aspectFilter != null && golem.essentia != null
                    && golem.essentiaAmount > 0 && jar.aspect.equals(golem.essentia)
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        // Phase 4: jars with matching aspect, no filter.
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if (jar.aspect != null && jar.amount > 0 && jar.amount < jar.maxAmount
                    && jar.aspectFilter == null && golem.essentia != null
                    && golem.essentiaAmount > 0 && jar.aspect.equals(golem.essentia)
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        // Phase 5: empty unlabeled jars (not void).
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if ((jar.aspect == null || jar.amount == 0)
                    && jar.aspectFilter == null
                    && !(jar instanceof TileJarFillableVoid)
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        // Phase 6: void jars with matching aspect, no filter.
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if (jar instanceof TileJarFillableVoid
                    && jar.aspect != null && jar.amount >= jar.maxAmount
                    && jar.aspectFilter == null && golem.essentia != null
                    && golem.essentiaAmount > 0 && jar.aspect.equals(golem.essentia)
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        // Phase 7: empty void jars.
        if (candidates.isEmpty()) {
            for (TileJarFillable jar : jarlist.values()) {
                if ((jar.aspect == null || jar.amount == 0)
                    && jar.aspectFilter == null && (jar instanceof TileJarFillableVoid)
                    && jar.doesContainerAccept(golem.essentia)) {
                    candidates.add(jar);
                }
            }
        }

        BlockPos dest = getNearestEssentiaDestination(golem, candidates, dmod);
        jarlist.clear();
        return dest;
    }

    private static BlockPos getNearestEssentiaDestination(EntityGolemBase golem, List<? extends TileEntity> candidates, float rangeSq) {
        BlockPos dest = null;
        double distance = Double.MAX_VALUE;
        BlockPos home = golem.getHomePosition();
        for (TileEntity candidate : candidates) {
            double candidateDistance = candidate.getDistanceSq(home.getX(), home.getY(), home.getZ());
            if (candidate instanceof TileJarFillableVoid) {
                candidateDistance += rangeSq;
            }
            if (candidateDistance < distance) {
                distance = candidateDistance;
                dest = candidate.getPos();
            }
        }
        return dest;
    }

    // --- Missing Liquids ---

    public static ArrayList<FluidStack> getMissingLiquids(EntityGolemBase golem) {
        ensureLiquidsRegistered();
        ArrayList<FluidStack> out = new ArrayList<>();

        EnumFacing facing = EnumFacing.VALUES[golem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = golem.getHomePosition();
        BlockPos target = home.offset(facing.getOpposite());
        TileEntity tile = golem.world.getTileEntity(target);
        if (tile == null) return out;

        EnumFacing capSide = facing.getOpposite();
        if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, capSide)) return out;
        IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, capSide);
        if (handler == null) return out;

        for (Fluid fluid : reggedLiquids) {
            if (fluid == null) continue;
            if (golem.fluidCarried != null
                    && golem.fluidCarried.amount > 0
                    && !golem.fluidCarried.getFluid().equals(fluid)) {
                continue;
            }

            FluidStack probe = new FluidStack(fluid, golem.getFluidCarryLimit());
            if (handler.fill(probe, false) <= 0) continue;

            if (golem.inventory.hasSomething()) {
                boolean found = false;
                for (int slot = 0; slot < golem.inventory.slotCount; ++slot) {
                    FluidStack contained = FluidUtil.getFluidContained(golem.inventory.getStackInSlot(slot));
                    if (contained != null && contained.isFluidEqual(probe)) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;
            }

            out.add(new FluidStack(fluid, Integer.MAX_VALUE));
        }

        return out;
    }

    // --- Marked Fluid Handlers Adjacent to Golem ---

    public static ArrayList<Marker> getMarkedFluidHandlersAdjacentToGolem(FluidStack ls, World world, EntityGolemBase golem) {
        ArrayList<Marker> results = new ArrayList<>();
        for (Marker marker : golem.getMarkers()) {
            if (marker.dim != world.provider.getDimension()) continue;
            BlockPos pos = new BlockPos(marker.x, marker.y, marker.z);
            TileEntity te = world.getTileEntity(pos);
            if (te == null) continue;

            EnumFacing side = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
            if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) continue;

            // Check proximity
            double dist = golem.getDistanceSq(marker.x + 0.5, marker.y + 0.5, marker.z + 0.5);
            if (dist > ADJACENT_RANGE) continue;

            IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
            if (handler == null) continue;

            if (ls != null) {
                FluidStack drained = handler.drain(ls.copy(), false);
                if (drained == null || drained.amount <= 0) continue;
            }

            results.add(marker);
        }
        return results;
    }

    // --- Drain World Fluid Block ---

    private static FluidStack drainWorldFluidBlock(World world, BlockPos pos, boolean doDrain) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) block;
            if (!fluidBlock.canDrain(world, pos)) return null;
            FluidStack drained = fluidBlock.drain(world, pos, doDrain);
            return (drained != null && drained.amount > 0) ? drained : null;
        }

        Fluid fluid = null;
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
            fluid = FluidRegistry.WATER;
        } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            fluid = FluidRegistry.LAVA;
        }

        if (fluid == null) return null;
        if (!(block instanceof BlockLiquid)) return null;

        Integer level = state.getValue(BlockLiquid.LEVEL);
        if (level == null || level.intValue() != 0) return null;

        if (doDrain) {
            world.setBlockToAir(pos);
        }

        return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    }

    // --- Find Possible Liquid ---

    public static Vec3d findPossibleLiquid(FluidStack ls, EntityGolemBase golem) {
        ensureLiquidsRegistered();

        // 1. Check marked IFluidHandler sources
        for (Marker marker : golem.getMarkers()) {
            if (marker.dim != golem.world.provider.getDimension()) continue;
            BlockPos pos = new BlockPos(marker.x, marker.y, marker.z);
            TileEntity te = golem.world.getTileEntity(pos);
            if (te == null) continue;

            EnumFacing side = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
            if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) continue;

            IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
            if (handler == null) continue;

            int maxDrain = golem.getFluidCarryLimit();
            if (golem.fluidCarried != null) {
                maxDrain -= golem.fluidCarried.amount;
            }

            FluidStack probe;
            if (ls != null) {
                FluidStack simDrain = ls.copy();
                simDrain.amount = Math.min(simDrain.amount, maxDrain);
                probe = handler.drain(simDrain, false);
            } else {
                probe = handler.drain(maxDrain, false);
            }

            if (probe != null && probe.amount > 0) {
                return new Vec3d(marker.x + 0.5, marker.y + 0.5, marker.z + 0.5);
            }
        }

        // 2. Check world fluid source blocks within range
        float range = golem.getRange();
        BlockPos home = golem.getHomePosition();
        int minX = home.getX() - (int) range;
        int maxX = home.getX() + (int) range;
        int minZ = home.getZ() - (int) range;
        int maxZ = home.getZ() + (int) range;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = home.getY() - 3; y <= home.getY() + 3; y++) {
                    BlockPos scanPos = new BlockPos(x, y, z);
                    if (!golem.world.isBlockLoaded(scanPos)) continue;

                    FluidStack drained = drainWorldFluidBlock(golem.world, scanPos, false);
                    if (drained != null && drained.amount > 0) {
                        if (ls == null || drained.isFluidEqual(ls)) {
                            return new Vec3d(x + 0.5, y + 0.5, z + 0.5);
                        }
                    }
                }
            }
        }

        return null;
    }

    // --- Inner class for timeout ---
    public static class SortingItemTimeout implements Comparable<SortingItemTimeout> {
        ItemStack stack = ItemStack.EMPTY;
        int golemId = 0;
        long time = 0L;

        public SortingItemTimeout(int golemId, ItemStack stack, long time) {
            this.stack = stack;
            this.golemId = golemId;
            this.time = time;
        }

        @Override
        public int compareTo(SortingItemTimeout o) {
            return this.equals(o) ? 0 : -1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SortingItemTimeout) {
                SortingItemTimeout t = (SortingItemTimeout) obj;
                if (this.golemId != t.golemId) return false;
                if (this.stack.isEmpty() || t.stack.isEmpty()) return false;
                if (!this.stack.isItemEqual(t.stack)) return false;
                if (!ItemStack.areItemStackTagsEqual(this.stack, t.stack)) return false;
            }
            return true;
        }
    }
}
