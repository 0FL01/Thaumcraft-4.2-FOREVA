package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.util.ArrayList;

public class ItemManaBean extends ItemFood implements IEssentiaContainerItem {

    public ItemManaBean() {
        super(1, 0.3f, false);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            Aspect aspect = getBeanAspect(stack, world);
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null && aspect != null && world.rand.nextInt(3) == 0) {
                knowledge.addAspectPool(aspect, 1);
            }
            switch (world.rand.nextInt(5)) {
                case 0: player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0)); break;
                case 1: player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 0)); break;
                case 2: player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 200, 0)); break;
                case 3: player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 0)); break;
                default: player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 200, 0)); break;
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        getBeanAspect(stack, world);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos place = pos.offset(facing);
        if (facing == EnumFacing.DOWN || ConfigBlocks.blockManaPod == null || !world.isAirBlock(place)) return EnumActionResult.PASS;
        if (!world.isRemote) {
            world.setBlockState(place, ConfigBlocks.blockManaPod.getDefaultState(), 3);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        Aspect aspect = getBeanAspect(itemstack, null);
        return aspect == null ? null : new AspectList().add(aspect, 1);
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (aspects == null || aspects.size() == 0) return;
        Aspect aspect = aspects.getAspects()[0];
        if (!itemstack.hasTagCompound()) itemstack.setTagCompound(new NBTTagCompound());
        itemstack.getTagCompound().setString("aspect", aspect.getTag());
    }

    private Aspect getBeanAspect(ItemStack stack, World world) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        String tag = stack.getTagCompound().getString("aspect");
        Aspect aspect = Aspect.getAspect(tag);
        if (aspect == null) {
            ArrayList<Aspect> aspects = new ArrayList<>(Aspect.aspects.values());
            if (aspects.isEmpty()) return null;
            aspect = aspects.get(world == null ? 0 : world.rand.nextInt(aspects.size()));
            stack.getTagCompound().setString("aspect", aspect.getTag());
        }
        return aspect;
    }
}
