package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.List;

public class ItemBaubleBlanks extends Item implements IBauble, IVisDiscountGear, IRunicArmor {

    public static final int META_VIS_STONE = 0;
    public static final int META_POCKET = 1;
    public static final int META_TABLET = 2;
    public static final int META_RING = 3;
    public static final int META_AMULET = 4;
    public static final int META_GIRDLE = 5;
    public static final int META_RUNE = 6;
    public static final int META_FOCUS_POUCH = 7;
    public static final int META_ICHOR = 8;

    public ItemBaubleBlanks() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta >= META_RING && meta <= META_ICHOR) {
            Aspect aspect = Aspect.getPrimalAspects().get(meta - META_RING);
            return I18n.translateToLocal("item.ItemBaubleBlanks.3.name").replace("%TYPE", aspect.getName());
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int meta = stack.getItemDamage();
        if (meta >= META_RING && meta <= META_ICHOR) {
            Aspect aspect = Aspect.getPrimalAspects().get(meta - META_RING);
            tooltip.add(TextFormatting.DARK_PURPLE + aspect.getName() + " " + I18n.translateToLocal("tc.discount") + ": 1%");
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i <= 2; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        int meta = stack.getItemDamage();
        if (meta >= 3 && meta <= 8 && aspect != null && aspect.isPrimal()) {
            return Aspect.getPrimalAspects().indexOf(aspect) == meta - 3 ? 1 : 0;
        }
        return 0;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        int dmg = itemstack.getItemDamage();
        if (dmg == META_TABLET) return BaubleType.BELT;
        if (dmg == META_VIS_STONE) return BaubleType.AMULET;
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }
}
