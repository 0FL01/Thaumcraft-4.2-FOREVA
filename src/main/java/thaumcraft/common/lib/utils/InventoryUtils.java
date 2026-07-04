package thaumcraft.common.lib.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileResearchTable;

import java.util.ArrayList;
import java.util.Random;

public class InventoryUtils {

    public static ItemStack placeItemStackIntoInventory(ItemStack stack, IInventory inv, int side, boolean doit) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack remaining = stack.copy();
        remaining = insertStack(inv, remaining, side, doit);
        if (remaining == null || remaining.isEmpty()) {
            if (doit) inv.markDirty();
            return ItemStack.EMPTY;
        }
        return remaining.copy();
    }

    private static ItemStack insertStack(IInventory inv, ItemStack stack, int side, boolean doit) {
        if (stack == null || stack.isEmpty()) return stack;
        ItemStack working = stack.copy();
        EnumFacing face = (side >= 0 && side < EnumFacing.VALUES.length) ? EnumFacing.VALUES[side] : null;

        if (inv instanceof ISidedInventory && face != null) {
            ISidedInventory sided = (ISidedInventory) inv;
            int[] slots = sided.getSlotsForFace(face);
            // First pass: merge into existing stacks
            if (slots != null) {
                for (int i = 0; i < slots.length && !working.isEmpty(); i++) {
                    int slot = slots[i];
                    ItemStack slotStack = inv.getStackInSlot(slot);
                    if (!slotStack.isEmpty() && slotStack.isItemEqual(working) && ItemStack.areItemStackTagsEqual(slotStack, working)) {
                        working = attemptInsertion(inv, working, slot, face, doit);
                    }
                }
                // Second pass: insert into empty slots
                for (int i = 0; i < slots.length && !working.isEmpty(); i++) {
                    int slot = slots[i];
                    if (inv.getStackInSlot(slot).isEmpty()) {
                        working = attemptInsertion(inv, working, slot, face, doit);
                    }
                }
            }
        } else {
            int size = inv.getSizeInventory();
            // First pass: merge into existing stacks
            for (int i = 0; i < size && !working.isEmpty(); i++) {
                ItemStack slotStack = inv.getStackInSlot(i);
                if (!slotStack.isEmpty() && slotStack.isItemEqual(working) && ItemStack.areItemStackTagsEqual(slotStack, working)) {
                    working = attemptInsertion(inv, working, i, face, doit);
                }
            }
            // Second pass: insert into empty slots
            for (int i = 0; i < size && !working.isEmpty(); i++) {
                if (inv.getStackInSlot(i).isEmpty()) {
                    working = attemptInsertion(inv, working, i, face, doit);
                }
            }

            // Handle double chest fallback
            if (!working.isEmpty()) {
                TileEntityChest dc = getDoubleChest((TileEntity) (inv instanceof TileEntity ? inv : null));
                if (dc != null) {
                    IInventory dcInv = dc;
                    int dcSize = dcInv.getSizeInventory();
                    for (int i = 0; i < dcSize && !working.isEmpty(); i++) {
                        ItemStack slotStack = dcInv.getStackInSlot(i);
                        if (!slotStack.isEmpty() && slotStack.isItemEqual(working) && ItemStack.areItemStackTagsEqual(slotStack, working)) {
                            working = attemptInsertion(dcInv, working, i, face, doit);
                        }
                    }
                    for (int i = 0; i < dcSize && !working.isEmpty(); i++) {
                        if (dcInv.getStackInSlot(i).isEmpty()) {
                            working = attemptInsertion(dcInv, working, i, face, doit);
                        }
                    }
                }
            }
        }
        return working;
    }

    private static ItemStack attemptInsertion(IInventory inv, ItemStack stack, int slot, EnumFacing face, boolean doit) {
        if (!canInsertItemToInventory(inv, stack, slot, face)) return stack;
        ItemStack slotStack = inv.getStackInSlot(slot);
        ItemStack working = stack.copy();
        boolean changed = false;

        if (slotStack.isEmpty()) {
            int maxStack = Math.min(inv.getInventoryStackLimit(), working.getMaxStackSize());
            if (working.getCount() <= maxStack) {
                if (doit) inv.setInventorySlotContents(slot, working.copy());
                working = ItemStack.EMPTY;
            } else {
                ItemStack insert = working.splitStack(maxStack);
                if (doit) inv.setInventorySlotContents(slot, insert);
            }
            changed = true;
        } else if (areItemStacksEqualStrict(slotStack, working)) {
            int maxStack = Math.min(inv.getInventoryStackLimit(), Math.min(slotStack.getMaxStackSize(), working.getMaxStackSize()));
            int room = maxStack - slotStack.getCount();
            int toInsert = Math.min(working.getCount(), room);
            if (toInsert > 0) {
                if (doit) slotStack.grow(toInsert);
                working.shrink(toInsert);
                changed = true;
            }
        }

        if (changed && doit) {
            if (inv instanceof TileEntityHopper) {
                ((TileEntityHopper) inv).setTransferCooldown(8);
            }
            inv.markDirty();
        }
        return working;
    }

    public static ItemStack extractStack(IInventory inv, ItemStack needed, int side, boolean oreDict, boolean ignoreDamage, boolean ignoreNBT, boolean doit) {
        if (needed == null || needed.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = null;
        EnumFacing face = (side >= 0 && side < EnumFacing.VALUES.length) ? EnumFacing.VALUES[side] : null;

        if (inv instanceof ISidedInventory && face != null) {
            ISidedInventory sided = (ISidedInventory) inv;
            int[] slots = sided.getSlotsForFace(face);
            for (int i = 0; i < slots.length && result == null; i++) {
                result = attemptExtraction(inv, needed, slots[i], face, oreDict, ignoreDamage, ignoreNBT, doit);
            }
        } else {
            int size = inv.getSizeInventory();
            for (int i = 0; i < size && result == null; i++) {
                result = attemptExtraction(inv, needed, i, face, oreDict, ignoreDamage, ignoreNBT, doit);
            }
        }
        if (result == null || result.isEmpty()) return ItemStack.EMPTY;
        return result.copy();
    }

    public static ItemStack attemptExtraction(IInventory inv, ItemStack needed, int slot, EnumFacing face, boolean oreDict, boolean ignoreDamage, boolean ignoreNBT, boolean doit) {
        ItemStack slotStack = inv.getStackInSlot(slot);
        if (slotStack.isEmpty()) return ItemStack.EMPTY;
        if (!canExtractItemFromInventory(inv, slotStack, slot, face)) return ItemStack.EMPTY;
        if (!areItemStacksEqual(slotStack, needed, oreDict, ignoreDamage, ignoreNBT)) return ItemStack.EMPTY;

        int amount = Math.min(needed.getCount(), slotStack.getCount());
        ItemStack out = slotStack.copy();
        out.setCount(amount);
        if (doit) {
            if (amount >= slotStack.getCount()) {
                inv.setInventorySlotContents(slot, ItemStack.EMPTY);
            } else {
                slotStack.shrink(amount);
                inv.setInventorySlotContents(slot, slotStack);
            }
            inv.markDirty();
        }
        return out;
    }

    public static boolean inventoryContains(IInventory inv, ItemStack stack, int side, boolean oreDict, boolean ignoreDamage, boolean ignoreNBT) {
        return !extractStack(inv, stack, side, oreDict, ignoreDamage, ignoreNBT, false).isEmpty();
    }

    public static boolean canInsertItemToInventory(IInventory inv, ItemStack stack, int slot, EnumFacing face) {
        if (stack == null || stack.isEmpty()) return false;
        if (!inv.isItemValidForSlot(slot, stack)) return false;
        if (inv instanceof ISidedInventory && face != null) {
            return ((ISidedInventory) inv).canInsertItem(slot, stack, face);
        }
        return true;
    }

    public static boolean canExtractItemFromInventory(IInventory inv, ItemStack stack, int slot, EnumFacing face) {
        if (stack == null || stack.isEmpty()) return false;
        if (inv instanceof ISidedInventory && face != null) {
            return ((ISidedInventory) inv).canExtractItem(slot, stack, face);
        }
        return true;
    }

    // --- ItemStack comparison ---

    public static boolean areItemStacksEqualStrict(ItemStack a, ItemStack b) {
        return areItemStacksEqual(a, b, false, false, false);
    }

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b, boolean oreDict, boolean ignoreDamage, boolean ignoreNBT) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.isEmpty() && b.isEmpty()) return true;
        if (a.isEmpty() || b.isEmpty()) return false;

        // Ore dictionary match
        if (oreDict) {
            int[] idsA = OreDictionary.getOreIDs(a);
            int[] idsB = OreDictionary.getOreIDs(b);
            for (int idA : idsA) {
                for (int idB : idsB) {
                    if (idA == idB) return true;
                }
            }
        }

        // Item type must match
        if (a.getItem() != b.getItem()) return false;

        // Damage/metadata check
        if (!ignoreDamage) {
            boolean dm = a.getItem().isDamageable() && b.getItem().isDamageable();
            if (!dm && a.getMetadata() != b.getMetadata()) return false;
        }

        // NBT check
        if (!ignoreNBT) {
            if (!ItemStack.areItemStackTagsEqual(a, b)) return false;
        }

        return true;
    }

    public static boolean areItemStacksEqualForCrafting(ItemStack a, ItemStack b, boolean oreDict, boolean ignoreDamage, boolean ignoreNBT) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.isEmpty() && b.isEmpty()) return true;
        if (a.isEmpty() || b.isEmpty()) return false;

        if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(a);
            for (int id : ids) {
                String oreName = OreDictionary.getOreName(id);
                if (oreName != null && !oreName.isEmpty()) {
                    net.minecraft.util.NonNullList<ItemStack> ores = OreDictionary.getOres(oreName);
                    if (ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{b}, ores.toArray(new ItemStack[0]))) return true;
                }
            }
        }

        boolean nbtOk = ignoreNBT || ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(a, b);
        if (!nbtOk) return false;

        boolean dmgOk = true;
        if (a.getMetadata() != b.getMetadata()) {
            if (ignoreDamage && a.getItem().isDamageable() && b.getItem().isDamageable()) {
                dmgOk = true;
            } else if (ignoreDamage && (a.getMetadata() == Short.MAX_VALUE || b.getMetadata() == Short.MAX_VALUE)) {
                dmgOk = true;
            } else {
                dmgOk = false;
            }
        }

        if (a.getItem() != b.getItem()) return false;
        if (!dmgOk) return false;
        if (a.getCount() > a.getMaxStackSize()) return false;
        return nbtOk;
    }

    // --- Double chest ---

    public static TileEntityChest getDoubleChest(TileEntity te) {
        if (te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) te;
            if (chest.adjacentChestXNeg != null) return chest.adjacentChestXNeg;
            if (chest.adjacentChestXPos != null) return chest.adjacentChestXPos;
            if (chest.adjacentChestZNeg != null) return chest.adjacentChestZNeg;
            if (chest.adjacentChestZPos != null) return chest.adjacentChestZPos;
        }
        return null;
    }

    public static void openInventoryForGolem(IInventory inv) {
        if (inv instanceof TileEntityChest) {
            updateVanillaChestForGolem((TileEntityChest) inv, true);
        } else {
            inv.openInventory(null);
        }
    }

    public static void closeInventoryForGolem(IInventory inv) {
        if (inv instanceof TileEntityChest) {
            updateVanillaChestForGolem((TileEntityChest) inv, false);
        } else {
            inv.closeInventory(null);
        }
    }

    private static void updateVanillaChestForGolem(TileEntityChest chest, boolean open) {
        if (chest.getWorld() == null || chest.getWorld().isRemote) return;

        if (open) {
            if (chest.numPlayersUsing < 0) chest.numPlayersUsing = 0;
            ++chest.numPlayersUsing;
        } else if (chest.numPlayersUsing > 0) {
            --chest.numPlayersUsing;
        } else {
            chest.numPlayersUsing = 0;
        }

        BlockPos pos = chest.getPos();
        chest.getWorld().addBlockEvent(pos, chest.getBlockType(), 1, chest.numPlayersUsing);
        chest.getWorld().notifyNeighborsOfStateChange(pos, chest.getBlockType(), false);
        if (chest.getBlockType() == Blocks.TRAPPED_CHEST) {
            chest.getWorld().notifyNeighborsOfStateChange(pos.down(), chest.getBlockType(), false);
        }
    }

    // --- Misc utilities ---

    public static boolean compareMultipleItems(ItemStack test, ItemStack[] candidates) {
        if (test == null || test.isEmpty() || candidates == null) return false;
        for (ItemStack c : candidates) {
            if (c != null && test.isItemEqual(c) && ItemStack.areItemStackTagsEqual(test, c)) return true;
        }
        return false;
    }

    public static boolean consumeInventoryItem(EntityPlayer player, Item item, int md) {
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack s = player.inventory.mainInventory.get(i);
            if (!s.isEmpty() && s.getItem() == item && s.getMetadata() == md) {
                s.shrink(1);
                if (s.getCount() <= 0) player.inventory.mainInventory.set(i, ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    public static void dropItems(World world, int x, int y, int z) {
        Random rand = new Random();
        TileEntity te = world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        if (!(te instanceof IInventory)) return;
        IInventory inv = (IInventory) te;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (te instanceof TileResearchTable && i == 9) continue;
            if (te instanceof TileArcaneWorkbench && i == 9) continue;
            ItemStack item = inv.getStackInSlot(i);
            if (item.isEmpty()) continue;
            float rx = rand.nextFloat() * 0.8f + 0.1f;
            float ry = rand.nextFloat() * 0.8f + 0.1f;
            float rz = rand.nextFloat() * 0.8f + 0.1f;
            EntityItem entityItem = new EntityItem(world, (double) x + rx, (double) y + ry, (double) z + rz, item.copy());
            entityItem.motionX = rand.nextGaussian() * 0.05;
            entityItem.motionY = rand.nextGaussian() * 0.05 + 0.2;
            entityItem.motionZ = rand.nextGaussian() * 0.05;
            world.spawnEntity(entityItem);
            inv.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    public static void dropItemsAtEntity(World world, int x, int y, int z, Entity entity) {
        Random rand = new Random();
        TileEntity te = world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        if (!(te instanceof IInventory)) return;
        IInventory inv = (IInventory) te;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (te instanceof TileResearchTable && i == 9) continue;
            if (te instanceof TileArcaneWorkbench && i == 9) continue;
            ItemStack item = inv.getStackInSlot(i);
            if (item.isEmpty()) continue;
            EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + (double) (entity.height / 2.0f), entity.posZ, item.copy());
            world.spawnEntity(entityItem);
            inv.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    public static int isWandInHotbarWithRoom(Aspect aspect, int amount, EntityPlayer player) {
        if (aspect == null || amount <= 0 || player == null) return -1;
        int hotbarSize = Math.min(9, player.inventory.mainInventory.size());
        for (int slot = 0; slot < hotbarSize; slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemWandCasting)) continue;
            // TC4 parity: attract if the wand has room for at least some vis,
            // not necessarily the full amount.
            if (ItemWandCasting.addVis(stack, aspect, amount, false) < amount) {
                return slot;
            }
        }
        return -1;
    }

    public static int isPlayerCarrying(EntityPlayer player, ItemStack stack) {
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack s = player.inventory.mainInventory.get(i);
            if (!s.isEmpty() && s.isItemEqual(stack)) return i;
        }
        return -1;
    }

    public static ItemStack damageItem(int amount, ItemStack stack, World world) {
        if (stack.getItem().isDamageable() && amount > 0) {
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(34), stack);
            int reduced = 0;
            for (int i = 0; unbreaking > 0 && i < amount; i++) {
                if (EnchantmentDurability.negateDamage(stack, unbreaking, world.rand)) reduced++;
            }
            amount -= reduced;
            if (amount <= 0) return stack;
            stack.setItemDamage(stack.getItemDamage() + amount);
            if (stack.getItemDamage() > stack.getMaxDamage()) {
                stack.shrink(1);
                if (stack.getCount() < 0) stack.setCount(0);
                stack.setItemDamage(0);
            }
        }
        return stack;
    }

    public static void dropItemsWithChance(World world, int x, int y, int z, float chance, int fortune, ArrayList<ItemStack> items) {
        for (ItemStack item : items) {
            if (world.rand.nextFloat() > chance || item.isEmpty()) continue;
            if (world.isRemote) continue;
            if (!world.getGameRules().getBoolean("doTileDrops")) continue;
            double vx = world.rand.nextFloat() * 0.7 + (1.0 - 0.7) * 0.5;
            double vy = world.rand.nextFloat() * 0.7 + (1.0 - 0.7) * 0.5;
            double vz = world.rand.nextFloat() * 0.7 + (1.0 - 0.7) * 0.5;
            EntityItem entityItem = new EntityItem(world, (double) x + vx, (double) y + vy, (double) z + vz, item.copy());
            entityItem.setPickupDelay(10);
            world.spawnEntity(entityItem);
        }
    }

    public static boolean isInventoryEmpty(IInventory inv) {
        if (inv == null) return true;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    public static boolean addItemToInventory(IInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).isEmpty()) {
                inv.setInventorySlotContents(i, stack.copy());
                return true;
            }
        }
        return false;
    }

    public static ItemStack cycleItemStack(Object input) {
        ItemStack it = null;
        if (input instanceof ItemStack) {
            it = (ItemStack) input;
            if (it.getMetadata() == Short.MAX_VALUE && it.getItem().getHasSubtypes()) {
                net.minecraft.util.NonNullList<ItemStack> subItems = net.minecraft.util.NonNullList.create();
                it.getItem().getSubItems(it.getItem().getCreativeTab(), subItems);
                if (!subItems.isEmpty()) {
                    int md = (int) (System.currentTimeMillis() / 1000L % subItems.size());
                    ItemStack it2 = new ItemStack(it.getItem(), 1, md);
                    if (it.hasTagCompound()) it2.setTagCompound(it.getTagCompound().copy());
                    it = it2;
                }
            } else if (it.getMetadata() == Short.MAX_VALUE && it.getItem().isDamageable()) {
                int md = (int) (System.currentTimeMillis() / 10L % (it.getMaxDamage() + 1));
                ItemStack it2 = new ItemStack(it.getItem(), 1, md);
                if (it.hasTagCompound()) it2.setTagCompound(it.getTagCompound().copy());
                it = it2;
            } else if (it.getMetadata() == Short.MAX_VALUE) {
                ItemStack it2 = new ItemStack(it.getItem(), 1, 0);
                if (it.hasTagCompound()) it2.setTagCompound(it.getTagCompound().copy());
                it = it2;
            }
        } else if (input instanceof ArrayList) {
            ArrayList<?> list = (ArrayList<?>) input;
            if (!list.isEmpty()) {
                int idx = (int) (System.currentTimeMillis() / 1000L % list.size());
                it = cycleItemStack(list.get(idx));
            }
        } else if (input instanceof String) {
            net.minecraft.util.NonNullList<ItemStack> ores = OreDictionary.getOres((String) input);
            if (ores != null && !ores.isEmpty()) {
                int idx = (int) (System.currentTimeMillis() / 1000L % ores.size());
                it = cycleItemStack(ores.get(idx));
            }
        }
        return it;
    }
}
