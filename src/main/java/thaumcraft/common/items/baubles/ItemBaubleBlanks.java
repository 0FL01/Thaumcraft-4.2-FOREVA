package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.CreativeTabThaumcraft;

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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i <= 8; i++) {
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
        return 0;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        int dmg = itemstack.getItemDamage();
        if (dmg == META_RING) return BaubleType.RING;
        if (dmg == META_AMULET) return BaubleType.AMULET;
        if (dmg == META_GIRDLE) return BaubleType.BELT;
        return BaubleType.TRINKET;
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
