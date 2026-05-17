package thaumcraft.common.items;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;

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
                ItemStack loot = Utils.generateLoot(stack.getItemDamage(), world.rand);
                if (!loot.isEmpty()) {
                    world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, loot.copy()));
                }
            }
            world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.COINS, SoundCategory.PLAYERS, 0.75F, 1.0F);
        }
        stack.shrink(1);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
