package thaumcraft.common.items;

import java.util.List;
import java.util.Random;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

public class ItemLootBag extends Item {

    public static final int META_COMMON = 0;
    public static final int META_UNCOMMON = 1;
    public static final int META_RARE = 2;

    public ItemLootBag() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
        this.addPropertyOverride(new ResourceLocation("thaumcraft", "bag_type"),
                (stack, world, entity) -> stack.getItemDamage());
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, META_COMMON));
            items.add(new ItemStack(this, 1, META_UNCOMMON));
            items.add(new ItemStack(this, 1, META_RARE));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case META_UNCOMMON:
                return EnumRarity.UNCOMMON;
            case META_RARE:
                return EnumRarity.RARE;
            default:
                return EnumRarity.COMMON;
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TextComponentTranslation("tc.lootbag").getFormattedText());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            int rolls = 8 + world.rand.nextInt(5);
            for (int i = 0; i < rolls; i++) {
                ItemStack loot = generateLoot(stack.getItemDamage(), world.rand);
                if (!loot.isEmpty()) {
                    world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, loot.copy()));
                }
            }
            world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.COINS, SoundCategory.PLAYERS, 0.75F, 1.0F);
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private ItemStack generateLoot(int rarity, Random rand) {
        ItemStack loot = ItemStack.EMPTY;
        if (rarity > 0 && rand.nextFloat() < 0.025F * (float) rarity) {
            loot = genGear(rarity, rand);
            if (loot.isEmpty()) {
                loot = generateLoot(rarity, rand);
            }
        } else {
            List<WeightedRandomLoot> table = rarity >= META_RARE ? WeightedRandomLoot.lootBagRare
                    : rarity == META_UNCOMMON ? WeightedRandomLoot.lootBagUncommon : WeightedRandomLoot.lootBagCommon;
            if (!table.isEmpty()) {
                loot = ((WeightedRandomLoot) WeightedRandom.getRandomItem(rand, table)).item.copy();
            }
        }
        if (loot.isEmpty()) return fallbackCoins(rand, rarity);
        if (loot.getItem() == Items.BOOK) {
            loot = EnchantmentHelper.addRandomEnchantment(rand, loot, (int) (5.0F + (float) rarity * 0.75F * (float) rand.nextInt(18)), false);
        }
        return loot.copy();
    }

    private ItemStack fallbackCoins(Random rand, int rarity) {
        if (ConfigItems.itemResource != null) {
            return new ItemStack(ConfigItems.itemResource, 1 + rand.nextInt(rarity >= META_RARE ? 4 : 2), ItemResource.META_COIN);
        }
        return ItemStack.EMPTY;
    }

    private ItemStack genGear(int rarity, Random rand) {
        int quality = rand.nextInt(2);
        if (rand.nextFloat() < 0.2F) quality++;
        if (rand.nextFloat() < 0.15F) quality++;
        if (rand.nextFloat() < 0.1F) quality++;
        if (rand.nextFloat() < 0.095F) quality++;
        if (rand.nextFloat() < 0.095F) quality++;
        Item item = getGearItemForSlot(rand.nextInt(5), quality);
        if (item == null) return ItemStack.EMPTY;
        int maxDamage = item.getMaxDamage();
        ItemStack stack = new ItemStack(item, 1, maxDamage > 0 ? rand.nextInt(1 + maxDamage / 6) : 0);
        if (rand.nextInt(4) < rarity) {
            stack = EnchantmentHelper.addRandomEnchantment(rand, stack, (int) (5.0F + (float) rarity * 0.75F * (float) rand.nextInt(18)), false);
        }
        return stack.copy();
    }

    private Item getGearItemForSlot(int slot, int quality) {
        switch (slot) {
            case 4:
                if (quality == 0) return Items.LEATHER_HELMET;
                if (quality == 1) return Items.CHAINMAIL_HELMET;
                if (quality == 2) return Items.IRON_HELMET;
                if (quality == 3) return Items.GOLDEN_HELMET;
                if (quality == 4) return ConfigItems.itemHelmThaumium;
                if (quality == 5) return Items.DIAMOND_HELMET;
                if (quality == 6) return ConfigItems.itemHelmVoid;
                break;
            case 3:
                if (quality == 0) return Items.LEATHER_CHESTPLATE;
                if (quality == 1) return Items.CHAINMAIL_CHESTPLATE;
                if (quality == 2) return Items.IRON_CHESTPLATE;
                if (quality == 3) return Items.GOLDEN_CHESTPLATE;
                if (quality == 4) return ConfigItems.itemChestThaumium;
                if (quality == 5) return Items.DIAMOND_CHESTPLATE;
                if (quality == 6) return ConfigItems.itemChestVoid;
                break;
            case 2:
                if (quality == 0) return Items.LEATHER_LEGGINGS;
                if (quality == 1) return Items.CHAINMAIL_LEGGINGS;
                if (quality == 2) return Items.IRON_LEGGINGS;
                if (quality == 3) return Items.GOLDEN_LEGGINGS;
                if (quality == 4) return ConfigItems.itemLegsThaumium;
                if (quality == 5) return Items.DIAMOND_LEGGINGS;
                if (quality == 6) return ConfigItems.itemLegsVoid;
                break;
            case 1:
                if (quality == 0) return Items.LEATHER_BOOTS;
                if (quality == 1) return Items.CHAINMAIL_BOOTS;
                if (quality == 2) return Items.IRON_BOOTS;
                if (quality == 3) return Items.GOLDEN_BOOTS;
                if (quality == 4) return ConfigItems.itemBootsThaumium;
                if (quality == 5) return Items.DIAMOND_BOOTS;
                if (quality == 6) return ConfigItems.itemBootsVoid;
                break;
            case 0:
                if (quality == 0) return Items.WOODEN_SWORD;
                if (quality == 1) return Items.STONE_SWORD;
                if (quality == 2) return Items.IRON_SWORD;
                if (quality == 3) return Items.GOLDEN_SWORD;
                if (quality == 4) return ConfigItems.itemSwordThaumium;
                if (quality == 5) return Items.DIAMOND_SWORD;
                if (quality == 6) return ConfigItems.itemSwordVoid;
                break;
            default:
                break;
        }
        return null;
    }
}
