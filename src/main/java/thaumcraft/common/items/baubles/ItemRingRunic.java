package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.api.ItemRunic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemRingRunic extends ItemRunic implements IBauble {

    public static final int META_LESSER = 0;
    public static final int META_NORMAL = 1;
    public static final int META_CHARGED = 2;
    public static final int META_REGEN = 3;
    public static final int META_AUTO = 4;

    public ItemRingRunic() {
        super(5);
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
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == META_LESSER ? EnumRarity.UNCOMMON : EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i <= 3; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        if (meta == META_LESSER) return 1;
        if (meta == META_NORMAL) return 5;
        return 4;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) { markRunicDirty(); }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) { markRunicDirty(); }

    private void markRunicDirty() {
        if (Thaumcraft.instance != null && Thaumcraft.instance.runicEventHandler != null) {
            Thaumcraft.instance.runicEventHandler.isDirty = true;
        }
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }
}
