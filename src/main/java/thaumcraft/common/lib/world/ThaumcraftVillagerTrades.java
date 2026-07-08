package thaumcraft.common.lib.world;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import thaumcraft.common.config.ConfigItems;

/**
 * Custom ITradeList implementations for Thaumcraft villager professions.
 * Replaces the removed IVillageTradeHandler from 1.7.10.
 */
public class ThaumcraftVillagerTrades {

    // ---- Custom ITradeList implementations ----

    /**
     * Villager buys an item with metadata for emeralds.
     * ItemStack(meta, count) → 1 Emerald
     */
    public static class BuyForEmeralds implements EntityVillager.ITradeList {
        private final ItemStack buyStack;
        private final int minCount;
        private final int maxCount;

        public BuyForEmeralds(ItemStack buyStack, int minCount, int maxCount) {
            this.buyStack = buyStack;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int count = minCount + (maxCount > minCount ? random.nextInt(maxCount - minCount + 1) : 0);
            ItemStack offer = buyStack.copy();
            offer.setCount(count);
            recipeList.add(new MerchantRecipe(offer, new ItemStack(Items.EMERALD)));
        }
    }

    /**
     * Villager sells an item with metadata for emeralds.
     * 1 Emerald → ItemStack(meta, count)
     */
    public static class SellForEmeralds implements EntityVillager.ITradeList {
        private final ItemStack sellStack;
        private final int minCount;
        private final int maxCount;

        public SellForEmeralds(ItemStack sellStack, int minCount, int maxCount) {
            this.sellStack = sellStack;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int count = minCount + (maxCount > minCount ? random.nextInt(maxCount - minCount + 1) : 0);
            ItemStack result = sellStack.copy();
            result.setCount(count);
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), result));
        }
    }

    /**
     * Villager sells an item for multiple emeralds.
     * Emerald(count) → ItemStack(meta, 1)
     */
    public static class SellForMultiEmeralds implements EntityVillager.ITradeList {
        private final ItemStack sellStack;
        private final int minEmeralds;
        private final int maxEmeralds;

        public SellForMultiEmeralds(ItemStack sellStack, int minEmeralds, int maxEmeralds) {
            this.sellStack = sellStack;
            this.minEmeralds = minEmeralds;
            this.maxEmeralds = maxEmeralds;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int cost = minEmeralds + (maxEmeralds > minEmeralds ? random.nextInt(maxEmeralds - minEmeralds + 1) : 0);
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, cost), sellStack.copy()));
        }
    }

    /**
     * Villager trades items + emeralds for another item.
     * ItemStack(buyItem) + Emerald → ItemStack(sellItem)
     */
    public static class BuyWithEmeraldForItem implements EntityVillager.ITradeList {
        private final ItemStack buyStack;
        private final int minBuyCount;
        private final int maxBuyCount;
        private final ItemStack sellStack;

        public BuyWithEmeraldForItem(ItemStack buyStack, int minBuyCount, int maxBuyCount, ItemStack sellStack) {
            this.buyStack = buyStack;
            this.minBuyCount = minBuyCount;
            this.maxBuyCount = maxBuyCount;
            this.sellStack = sellStack;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int count = minBuyCount + (maxBuyCount > minBuyCount ? random.nextInt(maxBuyCount - minBuyCount + 1) : 0);
            ItemStack buy = buyStack.copy();
            buy.setCount(count);
            recipeList.add(new MerchantRecipe(buy, new ItemStack(Items.EMERALD), sellStack.copy()));
        }
    }

    /**
     * Sells a random aspect shard (meta 0-5) for 1 emerald.
     */
    public static class SellRandomShard implements EntityVillager.ITradeList {
        private final int minCount;
        private final int maxCount;

        public SellRandomShard(int minCount, int maxCount) {
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int count = minCount + (maxCount > minCount ? random.nextInt(maxCount - minCount + 1) : 0);
            int meta = random.nextInt(6);
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(ConfigItems.itemShard, count, meta)));
        }
    }

    /**
     * Sells a random bauble blank (meta 3-8) for emeralds.
     */
    public static class SellRandomBaubleBlank implements EntityVillager.ITradeList {
        private final int minEmeralds;
        private final int maxEmeralds;

        public SellRandomBaubleBlank(int minEmeralds, int maxEmeralds) {
            this.minEmeralds = minEmeralds;
            this.maxEmeralds = maxEmeralds;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int cost = minEmeralds + (maxEmeralds > minEmeralds ? random.nextInt(maxEmeralds - minEmeralds + 1) : 0);
            int meta = 3 + random.nextInt(6); // 3-8
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, cost), new ItemStack(ConfigItems.itemBaubleBlanks, 1, meta)));
        }
    }

    // ---- Wizard trades (replicates original VillageWizardManager.manipulateTradesForVillager) ----

    public static final EntityVillager.ITradeList[][] WIZARD_TRADE_LEVELS = new EntityVillager.ITradeList[][] {
        {
            // Buy TC Resource (meta 18, gold coin) 20-22 → 1 Emerald
            new BuyForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 18), 20, 22),

            // 1 Emerald → Sell TC Resource (meta 9) 1
            new SellForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 9), 1, 1),

            // Buy TC Resource (meta 3) 4-6 → 1 Emerald
            new BuyForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 3), 4, 6),
        },
        {
            // 1 Emerald → Sell TC Resource (meta 0) 1
            new SellForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 0), 1, 1),

            // Buy TC Resource (meta 6) 4-6 → 1 Emerald
            new BuyForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 6), 4, 6),

            // 1 Emerald → Sell TC Resource (meta 1) 1
            new SellForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 1), 1, 1),
        },
        {
            // Buy itemNuggetEdible (meta 0) 24-31 → 1 Emerald (chicken nugget equivalent)
            new BuyForEmeralds(new ItemStack(ConfigItems.itemNugget, 1, 0), 24, 31),

            // Buy Books 4-6 + Emerald → TC Resource (meta 9) 1
            new BuyWithEmeraldForItem(new ItemStack(Items.BOOK), 4, 6, new ItemStack(ConfigItems.itemResource, 1, 9)),

            // Buy itemNugget (meta 1, beef) 24-31 → 1 Emerald
            new BuyForEmeralds(new ItemStack(ConfigItems.itemNugget, 1, 1), 24, 31),
        },
        {
            // 1 Emerald → Sell Random Aspect Shard 2-3
            new SellRandomShard(2, 3),

            // 1 Emerald → Sell Mana Bean 1-2
            new SellForEmeralds(new ItemStack(ConfigItems.itemManaBean, 1, 0), 1, 2),

            // Emerald 5-7 → Sell Bath Salts 1
            new SellForMultiEmeralds(new ItemStack(ConfigItems.itemBathSalts), 5, 7),
        },
        {
            // Emerald 5-7 → Sell Ring of Runic Shielding 1
            new SellForMultiEmeralds(new ItemStack(ConfigItems.itemRingRunic), 5, 7),

            // Emerald 5-7 → Sell Amulet of Vis Storage 1
            new SellForMultiEmeralds(new ItemStack(ConfigItems.itemAmuletVis), 5, 7),

            // Emerald 5-7 → Sell Random Bauble Blank (meta 3-8) 1
            new SellRandomBaubleBlank(5, 7),
        },
    };

    // ---- Banker trades (replicates original VillageBankerManager.manipulateTradesForVillager) ----
    //
    // Banker is a currency exchanger: trades Thaumcraft gold coins (ItemResource meta 18)
    // for various vanilla and TC items.

    public static final EntityVillager.ITradeList[][] BANKER_TRADE_LEVELS = new EntityVillager.ITradeList[][] {
        {
            // Gold Coins 20-22 → Emerald 1
            new BuyForEmeralds(new ItemStack(ConfigItems.itemResource, 1, 18), 20, 22),

            // Gold Coins 2-3 → Arrow 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 2, 3, new ItemStack(Items.ARROW), 1, 1),

            // Gold Coins 3-4 → Paper 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 3, 4, new ItemStack(Items.PAPER), 1, 1),
        },
        {
            // Gold Coins 2-3 → Coal 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 2, 3, new ItemStack(Items.COAL), 1, 1),

            // Gold Coins 6-8 → Lapis Block 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 6, 8, new ItemStack(Items.DYE, 1, 4), 1, 1),

            // Gold Coins 7-9 → Book 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 7, 9, new ItemStack(Items.BOOK), 1, 1),
        },
        {
            // Gold Coins 16-20 → Experience Bottle 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 16, 20, new ItemStack(Items.EXPERIENCE_BOTTLE), 1, 1),

            // Gold Coins 9-12 → Bookshelf 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 9, 12, new ItemStack(Items.BOOK, 3), 1, 1),
        },
        {
            // Gold Coins 6-8 → Iron Ingot 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 6, 8, new ItemStack(Items.IRON_INGOT), 1, 1),

            // Gold Coins 10-12 → TC Resource (meta 2) 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 10, 12, new ItemStack(ConfigItems.itemResource, 1, 2), 1, 1),
        },
        {
            // Gold Coins 22-24 → Diamond 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 22, 24, new ItemStack(Items.DIAMOND), 1, 1),

            // Gold Coins 25-32 → Saddle 1
            new ItemForItem(new ItemStack(ConfigItems.itemResource, 1, 18), 25, 32, new ItemStack(Items.SADDLE), 1, 1),
        },
    };

    /**
     * Generic trade: player gives item A (count range), villager gives item B (count range).
     */
    public static class ItemForItem implements EntityVillager.ITradeList {
        private final ItemStack buyStack;
        private final int minBuyCount;
        private final int maxBuyCount;
        private final ItemStack sellStack;
        private final int minSellCount;
        private final int maxSellCount;

        public ItemForItem(ItemStack buyStack, int minBuyCount, int maxBuyCount, ItemStack sellStack, int minSellCount, int maxSellCount) {
            this.buyStack = buyStack;
            this.minBuyCount = minBuyCount;
            this.maxBuyCount = maxBuyCount;
            this.sellStack = sellStack;
            this.minSellCount = minSellCount;
            this.maxSellCount = maxSellCount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int buyCount = minBuyCount + (maxBuyCount > minBuyCount ? random.nextInt(maxBuyCount - minBuyCount + 1) : 0);
            int sellCount = minSellCount + (maxSellCount > minSellCount ? random.nextInt(maxSellCount - minSellCount + 1) : 0);
            ItemStack buy = buyStack.copy();
            buy.setCount(buyCount);
            ItemStack sell = sellStack.copy();
            sell.setCount(sellCount);
            recipeList.add(new MerchantRecipe(buy, sell));
        }
    }
}
