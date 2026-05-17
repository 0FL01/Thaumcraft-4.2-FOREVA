package thaumcraft.common.items;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import thaumcraft.common.blocks.BlockFluidDeath;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBucketDeath extends Item {

    public ItemBucketDeath() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult mop = this.rayTrace(world, player, true);
        if (mop == null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        BlockPos target = mop.getBlockPos().offset(mop.sideHit == null ? EnumFacing.UP : mop.sideHit);
        EnumFacing side = mop.sideHit == null ? EnumFacing.UP : mop.sideHit;
        if (!player.canPlayerEdit(target, side, stack)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (tryPlaceContainedLiquid(world, target)) {
            if (player.capabilities.isCreativeMode) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET));
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    public boolean tryPlaceContainedLiquid(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        boolean replaceable = !material.isSolid();
        if (!world.isAirBlock(pos) && !replaceable) {
            return false;
        }
        if (!world.isRemote && replaceable && !material.isLiquid()) {
            world.destroyBlock(pos, true);
        }
        IBlockState death = ConfigBlocks.blockFluidDeath.getDefaultState()
                .withProperty(BlockFluidBase.LEVEL, BlockFluidDeath.FULL_LEVEL);
        world.setBlockState(pos, death, 3);
        return true;
    }
}
