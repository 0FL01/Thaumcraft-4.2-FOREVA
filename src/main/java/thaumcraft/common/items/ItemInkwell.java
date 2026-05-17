package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.tiles.TileResearchTable;

public class ItemInkwell extends Item implements IScribeTools {

    public ItemInkwell() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(100);
        this.canRepair = true;
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() != ConfigBlocks.blockTable) return EnumActionResult.PASS;
        int meta = world.getBlockState(pos).getValue(BlockTable.TYPE);
        if (meta > 1) return EnumActionResult.PASS;
        BlockPos partner = null;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos candidate = pos.offset(facing);
            if (world.getBlockState(candidate).getBlock() == ConfigBlocks.blockTable
                    && world.getBlockState(candidate).getValue(BlockTable.TYPE) <= 1) {
                partner = candidate;
                break;
            }
        }
        if (partner == null) return EnumActionResult.PASS;
        if (world.isRemote) return EnumActionResult.PASS;
        world.setBlockState(pos, ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, 2), 3);
        world.setBlockState(partner, ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, 3), 3);
        if (world.getTileEntity(pos) instanceof TileResearchTable) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            ((TileResearchTable) world.getTileEntity(pos)).setInventorySlotContents(0, copy);
        }
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
