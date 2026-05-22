package thaumcraft.common.items.armor;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemFortressArmor extends ItemArmor implements IRepairable, IRunicArmor, ISpecialArmor, IGoggles, IRevealer {

    ModelBiped model1 = null;
    ModelBiped model2 = null;
    ModelBiped model = null;

    public ItemFortressArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/fortress_armor.png";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor,
                                                       net.minecraft.util.DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = this.damageReduceAmount / 25.0;
        if (source.isUnblockable()) {
            priority = 1;
            ratio = this.damageReduceAmount / 35.0;
        } else if (source.isMagicDamage() || source.isDamageAbsolute()) {
            priority = 1;
            ratio = this.damageReduceAmount / 20.0;
        } else if (source.isFireDamage()) {
            priority = 0;
            ratio = 0.0;
        }
        if (player instanceof EntityPlayer) {
            double set = 0.875;
            EntityPlayer entityPlayer = (EntityPlayer) player;
            for (int a = 1; a < 4; a++) {
                ItemStack piece = entityPlayer.inventory.armorInventory.get(a);
                if (piece.isEmpty() || !(piece.getItem() instanceof ItemFortressArmor)) {
                    continue;
                }
                set += 0.125;
                if (!piece.hasTagCompound() || !piece.getTagCompound().hasKey("mask")) {
                    continue;
                }
                set += 0.05;
            }
            ratio *= set;
        }
        return new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }

    @Override
    public int getArmorDisplay(net.minecraft.entity.player.EntityPlayer player, ItemStack armor, int slot) {
        return this.damageReduceAmount;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, net.minecraft.util.DamageSource source, int damage, int slot) {
        if (source != net.minecraft.util.DamageSource.FALL) {
            stack.damageItem(damage, entity);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("goggles")) {
            tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemGoggles.name"));
        }
        if (tag != null && tag.hasKey("mask")) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.HelmetFortress.mask." + tag.getInteger("mask")));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return hasGogglesTag(itemstack);
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return hasGogglesTag(itemstack);
    }

    private boolean hasGogglesTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("goggles");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        int type = this.armorType.ordinal();
        if (this.model1 == null) {
            this.model1 = new ModelFortressArmor(1.0f);
        }
        if (this.model2 == null) {
            this.model2 = new ModelFortressArmor(0.5f);
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
}
