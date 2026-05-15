package thaumcraft.common.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;

public class ContainerPech extends Container {

    private EntityPech pech;
    private InventoryPech pechInv;
    private World world;
    private int playerInventoryStart;

    public ContainerPech() {}

    public ContainerPech(InventoryPlayer playerInv, World world, EntityPech pech) {
        this.pech = pech;
        this.world = world;
        this.pechInv = new InventoryPech();
        this.pechInv.setPlayer(playerInv.player);
        this.pechInv.setMerchant(pech);
        this.pechInv.setContainer(this);
        if (this.pech != null) {
            this.pech.trading = true;
        }

        this.addSlotToContainer(new Slot(this.pechInv, 0, 36, 29));
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlotToContainer(new SlotOutput(this.pechInv, 1 + col + row * 2, 106 + col * 18, 20 + row * 18));
            }
        }

        this.playerInventoryStart = this.inventorySlots.size();
        this.bindPlayerInventory(playerInv);
    }

    private void bindPlayerInventory(InventoryPlayer playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.pech != null && !this.pech.isDead && this.pech.isTamed() && player.getDistanceSq(this.pech) <= 64.0D;
    }

    public InventoryPech getMerchantInventory() {
        return this.pechInv;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            generateContents();
            return true;
        }
        return super.enchantItem(player, id);
    }

    private void generateContents() {
        if (this.world == null || this.world.isRemote || this.pech == null || this.pechInv == null) return;
        ItemStack input = this.pechInv.getStackInSlot(0);
        if (input.isEmpty() || !outputsEmpty() || !this.pech.isValued(input)) return;

        int value = this.pech.getValue(input);
        if (this.world.rand.nextInt(100) <= value / 2) {
            this.pech.setTamed(false);
            this.pech.setCombatTask();
            this.pech.playSound(TCSounds.PECH_TRADE, 0.4F, 1.0F);
        }

        if (this.world.rand.nextInt(5) == 0) {
            value += this.world.rand.nextInt(3);
        } else if (this.world.rand.nextBoolean()) {
            value -= this.world.rand.nextInt(3);
        }

        List<TradeEntry> trades = getTradeInventory(this.pech.getPechType());
        while (value > 0 && !trades.isEmpty()) {
            int amount = Math.min(5, Math.max((value + 1) / 2, this.world.rand.nextInt(value) + 1));
            value -= amount;

            if (amount == 1 && this.world.rand.nextBoolean() && hasStuffInPack()) {
                addLootFromPack();
                continue;
            }

            if (amount >= 4 && this.world.rand.nextBoolean()) {
                ItemStack loot = Utils.generateLoot(1, this.world.rand);
                if (!loot.isEmpty()) {
                    addTradeOutput(loot);
                    continue;
                }
            }

            addTradeOutput(pickTrade(trades, amount));
        }

        this.pechInv.decrStackSize(0, 1);
    }

    private boolean outputsEmpty() {
        for (int i = 1; i < 5; ++i) {
            if (!this.pechInv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    private boolean hasStuffInPack() {
        for (ItemStack stack : this.pech.loot) {
            if (stack != null && !stack.isEmpty()) return true;
        }
        return false;
    }

    private void addLootFromPack() {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < this.pech.loot.length; ++i) {
            ItemStack stack = this.pech.loot[i];
            if (stack != null && !stack.isEmpty()) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) return;

        int slot = slots.get(this.world.rand.nextInt(slots.size()));
        ItemStack stack = this.pech.loot[slot].copy();
        stack.setCount(1);
        addTradeOutput(stack);
        this.pech.loot[slot].shrink(1);
        if (this.pech.loot[slot].isEmpty()) {
            this.pech.loot[slot] = ItemStack.EMPTY;
        }
    }

    private ItemStack pickTrade(List<TradeEntry> trades, int value) {
        List<TradeEntry> matches = new ArrayList<>();
        for (TradeEntry trade : trades) {
            if (trade.value == value) {
                matches.add(trade);
            }
        }
        if (matches.isEmpty()) return ItemStack.EMPTY;
        return matches.get(this.world.rand.nextInt(matches.size())).stack.copy();
    }

    private boolean addTradeOutput(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ItemStack remaining = stack.copy();

        for (int i = 1; i < 5; ++i) {
            ItemStack slot = this.pechInv.getStackInSlot(i);
            if (slot.isEmpty() || !ItemHandlerHelper.canItemStacksStack(slot, remaining)) continue;
            int move = Math.min(remaining.getCount(), slot.getMaxStackSize() - slot.getCount());
            if (move <= 0) continue;
            slot.grow(move);
            remaining.shrink(move);
            this.pechInv.setInventorySlotContents(i, slot);
            if (remaining.isEmpty()) return true;
        }

        for (int i = 1; i < 5; ++i) {
            if (!this.pechInv.getStackInSlot(i).isEmpty()) continue;
            this.pechInv.setInventorySlotContents(i, remaining.copy());
            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < this.playerInventoryStart) {
                if (!this.mergeItemStack(stack, this.playerInventoryStart, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return result;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.pech != null) {
            this.pech.trading = false;
        }
        if (this.pechInv != null && !player.world.isRemote) {
            for (int i = 0; i < this.pechInv.getSizeInventory(); i++) {
                ItemStack stack = this.pechInv.removeStackFromSlot(i);
                if (!stack.isEmpty()) {
                    net.minecraft.entity.item.EntityItem dropped = player.dropItem(stack, false);
                    if (dropped != null) {
                        dropped.setOwner("PechDrop");
                    }
                }
            }
        }
    }

    private static class SlotOutput extends Slot {
        SlotOutput(InventoryPech inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }

    private static List<TradeEntry> getTradeInventory(int type) {
        switch (type) {
            case 1:
                return getMageTrades();
            case 2:
                return getArcherTrades();
            default:
                return getForagerTrades();
        }
    }

    private static List<TradeEntry> getForagerTrades() {
        List<TradeEntry> trades = new ArrayList<>();
        addTrade(trades, 1, new ItemStack(ConfigItems.itemManaBean));
        addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_IRON));
        addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_GOLD));
        addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_CINNABAR));
        if (Config.foundCopperIngot) addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_COPPER));
        if (Config.foundTinIngot) addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_TIN));
        if (Config.foundSilverIngot) addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_SILVER));
        if (Config.foundLeadIngot) addTrade(trades, 1, new ItemStack(ConfigItems.itemNugget, 1, ItemNugget.META_CLUSTER_LEAD));
        addTrade(trades, 2, new ItemStack(Items.BLAZE_ROD));
        addTrade(trades, 2, new ItemStack(ConfigBlocks.blockCustomPlant, 1, 0));
        addTrade(trades, 3, new ItemStack(Items.EXPERIENCE_BOTTLE));
        addTrade(trades, 3, new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT));
        addTrade(trades, 3, new ItemStack(Items.GOLDEN_APPLE));
        addTrade(trades, 4, new ItemStack(ConfigItems.itemPickThaumium));
        addTrade(trades, 5, new ItemStack(Items.GOLDEN_APPLE, 1, 1));
        addTrade(trades, 5, new ItemStack(ConfigBlocks.blockCustomPlant, 1, 1));
        return trades;
    }

    private static List<TradeEntry> getMageTrades() {
        List<TradeEntry> trades = new ArrayList<>();
        addTrade(trades, 1, new ItemStack(ConfigItems.itemManaBean));
        for (int i = 0; i < 6; ++i) {
            addTrade(trades, 1, new ItemStack(ConfigItems.itemShard, 1, i));
        }
        addTrade(trades, 1, new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT));
        addTrade(trades, 2, new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT));
        addTrade(trades, 3, enchantedBook(Config.enchHaste, 1));
        addTrade(trades, 3, new ItemStack(Items.GOLDEN_APPLE));
        for (int i = 0; i < 7; ++i) {
            addTrade(trades, 4, new ItemStack(ConfigBlocks.blockCrystal, 1, i));
        }
        addTrade(trades, 5, new ItemStack(Items.GOLDEN_APPLE, 1, 1));
        addTrade(trades, 5, enchantedBook(Config.enchRepair, 1));
        addTrade(trades, 5, new ItemStack(ConfigItems.itemFocusPouch));
        addTrade(trades, 5, new ItemStack(ConfigItems.focusPech));
        addTrade(trades, 5, new ItemStack(ConfigItems.itemAmuletVis));
        return trades;
    }

    private static List<TradeEntry> getArcherTrades() {
        List<TradeEntry> trades = new ArrayList<>();
        addTrade(trades, 1, new ItemStack(ConfigItems.itemManaBean));
        addTrade(trades, 1, new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2));
        addTrade(trades, 2, new ItemStack(Items.GHAST_TEAR));
        addTrade(trades, 2, enchantedBook(Enchantments.POWER, 1));
        addTrade(trades, 3, new ItemStack(Items.EXPERIENCE_BOTTLE));
        addTrade(trades, 3, new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT));
        addTrade(trades, 3, new ItemStack(Items.GOLDEN_APPLE));
        addTrade(trades, 4, new ItemStack(ConfigItems.itemBootsThaumium));
        addTrade(trades, 5, new ItemStack(Items.GOLDEN_APPLE, 1, 1));
        addTrade(trades, 5, new ItemStack(ConfigItems.itemRingRunic));
        addTrade(trades, 5, enchantedBook(Enchantments.PROTECTION, 1));
        addTrade(trades, 5, enchantedBook(Enchantments.PROJECTILE_PROTECTION, 1));
        return trades;
    }

    private static void addTrade(List<TradeEntry> trades, int value, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            trades.add(new TradeEntry(value, stack));
        }
    }

    private static ItemStack enchantedBook(Enchantment enchantment, int level) {
        if (enchantment == null) return ItemStack.EMPTY;
        return ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(enchantment, level));
    }

    private static class TradeEntry {
        private final int value;
        private final ItemStack stack;

        private TradeEntry(int value, ItemStack stack) {
            this.value = value;
            this.stack = stack;
        }
    }
}
