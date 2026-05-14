package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.List;

public class ItemLootBag extends Item {

    public static final int META_COMMON = 0;
    public static final int META_UNCOMMON = 1;
    public static final int META_RARE = 2;

    public ItemLootBag() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            ItemStack loot = getLoot(world, stack.getItemDamage());
            if (!loot.isEmpty() && !player.inventory.addItemStackToInventory(loot.copy())) {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY + 0.5D, player.posZ, loot.copy()));
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private ItemStack getLoot(World world, int meta) {
        List<WeightedRandomLoot> table = meta >= META_RARE ? WeightedRandomLoot.lootBagRare
                : meta == META_UNCOMMON ? WeightedRandomLoot.lootBagUncommon : WeightedRandomLoot.lootBagCommon;
        if (!table.isEmpty()) {
            return ((WeightedRandomLoot) WeightedRandom.getRandomItem(world.rand, table)).item.copy();
        }
        if (ConfigItems.itemResource != null) {
            return new ItemStack(ConfigItems.itemResource, 1 + world.rand.nextInt(meta >= META_RARE ? 4 : 2), ItemResource.META_COIN);
        }
        return ItemStack.EMPTY;
    }
}
