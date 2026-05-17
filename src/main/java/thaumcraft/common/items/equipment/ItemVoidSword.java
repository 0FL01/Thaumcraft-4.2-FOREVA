package thaumcraft.common.items.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.init.MobEffects;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemVoidSword extends ItemSword implements IRepairable, IWarpingGear {

    public ItemVoidSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return isVoidToolRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        tryApplyVoidWither(target, attacker, 60);
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        repairVoid(stack, world, entityIn);
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("enchantment.special.sapless"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    static boolean isVoidToolRepair(ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == ConfigItems.itemResource && repair.getMetadata() == ItemResource.META_CHARM;
    }

    static boolean canApplyVoidCombatDebuff(EntityLivingBase target, EntityLivingBase hitter) {
        if (target == null || target.world == null || target.world.isRemote) {
            return false;
        }
        if (target instanceof EntityPlayer && hitter instanceof EntityPlayer) {
            net.minecraft.server.MinecraftServer server = target.world.getMinecraftServer();
            if (server != null && !server.isPVPEnabled()) {
                return false;
            }
        }
        return true;
    }

    static void tryApplyVoidWither(EntityLivingBase target, EntityLivingBase hitter, int durationTicks) {
        if (canApplyVoidCombatDebuff(target, hitter)) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, durationTicks));
        }
    }

    static void repairVoid(ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0) {
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
        }
    }
}
