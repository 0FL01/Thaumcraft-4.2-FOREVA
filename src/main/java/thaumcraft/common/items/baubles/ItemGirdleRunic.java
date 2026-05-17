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

public class ItemGirdleRunic extends ItemRunic implements IBauble {

    public static final int META_NORMAL = 0;
    public static final int META_KINETIC = 1;

    public ItemGirdleRunic() {
        super(10);
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
        return EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, META_NORMAL));
            items.add(new ItemStack(this, 1, META_KINETIC));
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return itemstack.getItemDamage() == META_NORMAL ? 10 : 9;
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
