package thaumcraft.common.entities.golems;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GolemHelper {

    public static final double ADJACENT_RANGE = 4.0;
    private static ArrayList<SortingItemTimeout> itemTimeout = new ArrayList<>();

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
