package thaumcraft.common.lib.utils;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.entities.InventoryMob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InventoryUtilsRuntimeTest {
    private static Item oreFilter;
    private static Item orePrimary;
    private static Item oreSecondary;
    private static Item oreFirst;
    private static Item oreOther;
    private static Item amountFilter;
    private static Item amountPrimary;
    private static Item amountSecondary;
    private static Item amountFirst;
    private static Item amountOther;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();

        oreFilter = Items.KNOWLEDGE_BOOK;
        orePrimary = Items.TOTEM_OF_UNDYING;
        oreSecondary = Items.SHULKER_SHELL;
        amountFilter = Items.WRITABLE_BOOK;
        amountPrimary = Items.WRITTEN_BOOK;
        amountSecondary = Items.MAP;

        OreDictionary.registerOre("tcR2ComparisonPrimary", new ItemStack(oreFilter));
        OreDictionary.registerOre("tcR2ComparisonPrimary", new ItemStack(orePrimary));
        OreDictionary.registerOre("tcR2ComparisonSecondary", new ItemStack(oreFilter));
        OreDictionary.registerOre("tcR2ComparisonSecondary", new ItemStack(oreSecondary));

        OreDictionary.registerOre("tcR2AmountPrimary", new ItemStack(amountFilter));
        OreDictionary.registerOre("tcR2AmountPrimary", new ItemStack(amountPrimary));
        OreDictionary.registerOre("tcR2AmountSecondary", new ItemStack(amountFilter));
        OreDictionary.registerOre("tcR2AmountSecondary", new ItemStack(amountSecondary));

        boolean comparisonPrimaryFirst = "tcR2ComparisonPrimary".equals(OreDictionary.getOreName(
                OreDictionary.getOreIDs(new ItemStack(oreFilter))[0]));
        oreFirst = comparisonPrimaryFirst ? orePrimary : oreSecondary;
        oreOther = comparisonPrimaryFirst ? oreSecondary : orePrimary;
        boolean amountPrimaryFirst = "tcR2AmountPrimary".equals(OreDictionary.getOreName(
                OreDictionary.getOreIDs(new ItemStack(amountFilter))[0]));
        amountFirst = amountPrimaryFirst ? amountPrimary : amountSecondary;
        amountOther = amountPrimaryFirst ? amountSecondary : amountPrimary;
    }

    @Test
    public void extractionScansPastEmptyAndNonmatchingSlots() {
        InventoryBasic inventory = new InventoryBasic("r2_extract", false, 4);
        inventory.setInventorySlotContents(1, new ItemStack(Items.APPLE));
        inventory.setInventorySlotContents(3, new ItemStack(Items.PAPER, 5));

        ItemStack extracted = InventoryUtils.extractStack(inventory, new ItemStack(Items.PAPER, 3),
                -1, false, false, false, true);

        assertEquals(3, extracted.getCount());
        assertEquals(2, inventory.getStackInSlot(3).getCount());
    }

    @Test
    public void sidedExtractionScansPastEmptyInaccessibleAndNonmatchingSlots() {
        TestSidedInventory inventory = new TestSidedInventory();
        inventory.setInventorySlotContents(1, new ItemStack(Items.PAPER, 5));
        inventory.setInventorySlotContents(2, new ItemStack(Items.APPLE));
        inventory.setInventorySlotContents(3, new ItemStack(Items.PAPER, 5));

        ItemStack extracted = InventoryUtils.extractStack(inventory, new ItemStack(Items.PAPER, 4),
                EnumFacing.NORTH.ordinal(), false, false, false, true);

        assertEquals(4, extracted.getCount());
        assertEquals(5, inventory.getStackInSlot(1).getCount());
        assertEquals(1, inventory.getStackInSlot(3).getCount());
    }

    @Test
    public void smartAmountReturnsRequestedFilterCounts() {
        InventoryMob inventory = new InventoryMob(6);
        ItemStack requested = tagged(new ItemStack(Items.PAPER, 23), 1);
        inventory.setInventorySlotContents(0, requested.copy());
        inventory.setInventorySlotContents(1, tagged(new ItemStack(Items.PAPER, 7), 2));

        assertEquals(23, inventory.getAmountNeededSmart(tagged(new ItemStack(Items.PAPER), 1), false));
        assertEquals(30, inventory.getAmountNeededSmart(tagged(new ItemStack(Items.PAPER), 1), true));
        assertEquals(0, inventory.getAmountNeededSmart(new ItemStack(Items.APPLE), false));
    }

    @Test
    public void smartAmountUsesOnlyTheFiltersFirstOreId() {
        InventoryMob inventory = new InventoryMob(1);
        inventory.setInventorySlotContents(0, new ItemStack(amountFilter, 19));

        assertEquals(19, inventory.getAmountNeededSmart(new ItemStack(amountFirst), true));
        assertEquals(0, inventory.getAmountNeededSmart(new ItemStack(amountOther), true));
    }

    @Test
    public void fuzzyCandidateExpansionUsesOnlyTheFiltersFirstOreId() {
        InventoryMob inventory = new InventoryMob(1);
        inventory.setInventorySlotContents(0, new ItemStack(oreFilter, 19));

        java.util.ArrayList<ItemStack> needed = inventory.getItemsNeeded(true);
        assertTrue(containsItem(needed, oreFirst));
        assertFalse(containsItem(needed, oreOther));
    }

    @Test
    public void comparisonRestoresDamageMetadataWildcardAndNbtToggles() {
        ItemStack damagedA = new ItemStack(Items.IRON_PICKAXE, 1, 3);
        ItemStack damagedB = new ItemStack(Items.IRON_PICKAXE, 1, 9);
        assertFalse(InventoryUtils.areItemStacksEqual(damagedA, damagedB, false, false, false));
        assertTrue(InventoryUtils.areItemStacksEqual(damagedA, damagedB, false, true, false));
        ItemStack unbreakable = damagedA.copy();
        unbreakable.setTagInfo("Unbreakable", new net.minecraft.nbt.NBTTagByte((byte) 1));
        assertFalse(InventoryUtils.areItemStacksEqual(unbreakable, damagedB, false, true, true));

        ItemStack metadataA = new ItemStack(Items.DYE, 1, 1);
        ItemStack metadataB = new ItemStack(Items.DYE, 1, 2);
        assertFalse(InventoryUtils.areItemStacksEqual(metadataA, metadataB, false, true, false));
        assertFalse(InventoryUtils.areItemStacksEqual(
                new ItemStack(Items.DYE, 1, OreDictionary.WILDCARD_VALUE), metadataB,
                false, false, false));
        assertTrue(InventoryUtils.areItemStacksEqual(
                new ItemStack(Items.DYE, 1, OreDictionary.WILDCARD_VALUE), metadataB,
                false, true, false));

        ItemStack taggedA = tagged(new ItemStack(Items.PAPER), 1);
        ItemStack taggedB = tagged(new ItemStack(Items.PAPER), 2);
        assertFalse(InventoryUtils.areItemStacksEqual(taggedA, taggedB, false, false, false));
        assertTrue(InventoryUtils.areItemStacksEqual(taggedA, taggedB, false, false, true));
    }

    @Test
    public void comparisonUsesTheFirstOreIdAndBypassesOtherTogglesOnOreMatch() {
        ItemStack filter = tagged(new ItemStack(oreFilter), 1);
        ItemStack primary = tagged(new ItemStack(oreFirst), 2);

        assertTrue(InventoryUtils.areItemStacksEqual(filter, primary, true, false, false));
        assertFalse(InventoryUtils.areItemStacksEqual(filter, new ItemStack(oreOther),
                true, true, true));
    }

    private static ItemStack tagged(ItemStack stack, int value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("filter", value);
        stack.setTagCompound(tag);
        return stack;
    }

    private static boolean containsItem(java.util.ArrayList<ItemStack> stacks, Item item) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }

    private static final class TestSidedInventory extends InventoryBasic implements ISidedInventory {
        private TestSidedInventory() {
            super("r2_sided_extract", false, 4);
        }

        @Override
        public int[] getSlotsForFace(EnumFacing side) {
            return new int[]{0, 1, 2, 3};
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
            return true;
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
            return index != 1;
        }
    }
}
