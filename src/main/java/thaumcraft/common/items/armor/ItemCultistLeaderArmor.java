package thaumcraft.common.items.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.client.renderers.models.gear.ModelLeaderArmor;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCultistLeaderArmor extends ItemArmor implements IRepairable, IRunicArmor {

    ModelBiped model1 = null;
    ModelBiped model2 = null;
    ModelBiped model = null;

    public ItemCultistLeaderArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.LEATHER || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/cultist_leader_armor.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        if (this.model1 == null) {
            this.model1 = new ModelLeaderArmor(1.0f);
        }
        if (this.model2 == null) {
            this.model2 = new ModelLeaderArmor(0.5f);
        }
        this.model = armorSlot == EntityEquipmentSlot.LEGS ? this.model2 : this.model1;
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
            this.model.rightArmPose = ModelBiped.ArmPose.EMPTY;
            this.model.leftArmPose = ModelBiped.ArmPose.EMPTY;
            if (entityLiving instanceof EntityPlayer && ((EntityPlayer)entityLiving).getActiveItemStack().getItem() != null) {
                ItemStack activeStack = ((EntityPlayer)entityLiving).getActiveItemStack();
                EnumAction enumaction = activeStack.getItemUseAction();
                if (enumaction == EnumAction.BLOCK) {
                    this.model.rightArmPose = ModelBiped.ArmPose.BLOCK;
                } else if (enumaction == EnumAction.BOW) {
                    this.model.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }
        return this.model;
    }
}
