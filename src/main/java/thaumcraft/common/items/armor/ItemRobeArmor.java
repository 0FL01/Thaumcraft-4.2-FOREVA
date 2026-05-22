package thaumcraft.common.items.armor;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemRobeArmor extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear {

    static final int DEFAULT_ROBE_COLOR = 6961280;

    public ItemRobeArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (slot == EntityEquipmentSlot.LEGS) {
            return type == null ? "thaumcraft:textures/models/robes_2.png" : "thaumcraft:textures/models/robes_2_overlay.png";
        }
        return type == null ? "thaumcraft:textures/models/robes_1.png" : "thaumcraft:textures/models/robes_1_overlay.png";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumicCloth = new ItemStack(ConfigItems.itemResource, 1, 7);
        return repair.isItemEqual(thaumicCloth) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return this.armorType == EntityEquipmentSlot.FEET ? 1 : 2;
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        return getRobeColor(stack);
    }

    @Override
    public void removeColor(ItemStack stack) {
        removeRobeColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        setRobeColor(stack, color);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if (!world.isRemote && state.getBlock() == Blocks.CAULDRON) {
            int level = state.getValue(BlockCauldron.LEVEL);
            if (level > 0) {
                this.removeColor(stack);
                world.setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, level - 1), 2);
                world.updateComparatorOutputLevel(pos, state.getBlock());
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    static int getRobeColor(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("display", 10)) {
            return DEFAULT_ROBE_COLOR;
        }
        NBTTagCompound display = tag.getCompoundTag("display");
        return display.hasKey("color", 99) ? display.getInteger("color") : DEFAULT_ROBE_COLOR;
    }

    static void removeRobeColor(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("display", 10)) {
            tag.getCompoundTag("display").removeTag("color");
        }
    }

    static void setRobeColor(ItemStack stack, int color) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound display = tag.getCompoundTag("display");
        if (!tag.hasKey("display", 10)) {
            tag.setTag("display", display);
        }
        display.setInteger("color", color);
    }
}
