package thaumcraft.common.items.armor;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.gear.ModelRobe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidRobeArmor extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear, IGoggles, IRevealer, ISpecialArmor, IWarpingGear {

    ModelBiped model1 = null;
    ModelBiped model2 = null;
    ModelBiped model = null;

    public ItemVoidRobeArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return type == null ? "thaumcraft:textures/models/void_robe_armor_overlay.png" : "thaumcraft:textures/models/void_robe_armor.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        int type = this.armorType.ordinal();
        if (this.model1 == null) {
            this.model1 = new ModelRobe(1.0f);
        }
        if (this.model2 == null) {
            this.model2 = new ModelRobe(0.5f);
        }
        this.model = type == 1 || type == 3 ? this.model1 : this.model2;
        if (this.model != null) {
            this.model.bipedHead.showModel = armorSlot == EntityEquipmentSlot.HEAD;
            this.model.bipedHeadwear.showModel = armorSlot == EntityEquipmentSlot.HEAD;
            this.model.bipedBody.showModel = armorSlot == EntityEquipmentSlot.CHEST || armorSlot == EntityEquipmentSlot.LEGS;
            this.model.bipedRightArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
            this.model.bipedLeftArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
            this.model.bipedRightLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS;
            this.model.bipedLeftLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS;
            this.model.isSneak = entityLiving.isSneaking();
            this.model.isRiding = entityLiving.isRiding();
            this.model.isChild = entityLiving.isChild();
            this.model.rightArmPose = net.minecraft.client.model.ModelBiped.ArmPose.EMPTY;
            this.model.leftArmPose = net.minecraft.client.model.ModelBiped.ArmPose.EMPTY;
            if (entityLiving instanceof EntityPlayer && ((EntityPlayer)entityLiving).getActiveItemStack().getItem() != null) {
                ItemStack activeStack = ((EntityPlayer)entityLiving).getActiveItemStack();
                EnumAction enumaction = activeStack.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) {
                    this.model.rightArmPose = net.minecraft.client.model.ModelBiped.ArmPose.BLOCK;
                } else if (enumaction == EnumAction.BOW) {
                    this.model.leftArmPose = net.minecraft.client.model.ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }
        return this.model;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tc.visdiscount") + ": " + this.getVisDiscount(stack, null, null) + "%");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ItemVoidArmor.isVoidArmorRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return 5;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        ItemVoidArmor.repairVoidArmor(stack, world, entityIn);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        ItemVoidArmor.repairVoidArmor(stack, world, player);
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return this.armorType == EntityEquipmentSlot.HEAD;
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return this.armorType == EntityEquipmentSlot.HEAD;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor,
                                                       net.minecraft.util.DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = this.damageReduceAmount / 25.0;
        if (source.isUnblockable()) {
            priority = 1;
            ratio = this.damageReduceAmount / 35.0;
        } else if (source.isFireDamage()) {
            priority = 0;
            ratio = 0.0;
        }
        return new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return this.damageReduceAmount;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, net.minecraft.util.DamageSource source, int damage, int slot) {
        if (source != net.minecraft.util.DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 2;
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        return ItemRobeArmor.getRobeColor(stack);
    }

    @Override
    public void removeColor(ItemStack stack) {
        ItemRobeArmor.removeRobeColor(stack);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        ItemRobeArmor.setRobeColor(stack, color);
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
}
