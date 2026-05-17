package thaumcraft.common.entities.golems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.List;

public class ItemTrunkSpawner extends Item {
    public ItemTrunkSpawner() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey("upgrade")) {
            byte upgrade = tag.getByte("upgrade");
            String text = "\u00a79";
            if (upgrade > -1) {
                text = text + I18n.translateToLocal("item.ItemGolemUpgrade." + upgrade + ".name") + " ";
            }
            tooltip.add(text);
        }
        if (tag.hasKey("inventory")) {
            tooltip.add(I18n.translateToLocal("item.TrunkSpawner.text.1"));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        BlockPos spawnPos = pos.offset(facing);
        double yOffset = getPlacementYOffset(world.getBlockState(pos), facing);
        EntityTravelingTrunk trunk = new EntityTravelingTrunk(world);
        trunk.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY() + yOffset, spawnPos.getZ() + 0.5D,
                MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
        trunk.rotationYawHead = trunk.rotationYaw;
        trunk.renderYawOffset = trunk.rotationYaw;
        trunk.setOwnerId(player.getUniqueID());

        if (stack.hasDisplayName()) {
            trunk.setCustomNameTag(stack.getDisplayName());
        }
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : null;
        if (tag != null && tag.hasKey("upgrade")) {
            trunk.setUpgrade(tag.getByte("upgrade"));
            trunk.setInvSize();
        }
        if (tag != null && tag.hasKey("inventory")) {
            NBTTagList inventoryTag = tag.getTagList("inventory", 10);
            trunk.inventory.readFromNBT(inventoryTag);
        }

        trunk.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(trunk)), (IEntityLivingData) null);
        boolean spawned = world.spawnEntity(trunk);
        if (spawned) {
            trunk.playLivingSound();
        }
        if (spawned && !player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    private static double getPlacementYOffset(IBlockState state, EnumFacing side) {
        if (side != EnumFacing.UP) {
            return 0.0D;
        }
        Block block = state.getBlock();
        return block instanceof BlockFence || block instanceof BlockWall ? 0.5D : 0.0D;
    }
}
