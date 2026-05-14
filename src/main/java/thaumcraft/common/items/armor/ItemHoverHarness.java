package thaumcraft.common.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemHoverHarness extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear {

    public ItemHoverHarness(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setMaxDamage(400);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.IRON_INGOT || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            Hover.handleHoverArmor(player, stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_HOVER_HARNESS, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return aspect == Aspect.AIR ? 5 : 2;
    }
}
