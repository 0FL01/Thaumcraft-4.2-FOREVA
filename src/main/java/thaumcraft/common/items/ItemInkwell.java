package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != ConfigBlocks.blockTable) return EnumActionResult.PASS;
        TileEntity tile = world.getTileEntity(pos);
        int meta = state.getValue(BlockTable.TYPE);
        if (!(tile instanceof thaumcraft.common.tiles.TileTable) || !isPlainTable(meta)) return EnumActionResult.PASS;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos candidate = pos.offset(facing);
            IBlockState partnerState = world.getBlockState(candidate);
            if (partnerState.getBlock() != ConfigBlocks.blockTable) continue;
            TileEntity partnerTile = world.getTileEntity(candidate);
            int partnerMeta = partnerState.getValue(BlockTable.TYPE);
            if (!(partnerTile instanceof thaumcraft.common.tiles.TileTable) || !isPlainTable(partnerMeta)) continue;
            if (world.isRemote) return EnumActionResult.PASS;

            world.removeTileEntity(pos);
            world.setBlockState(pos,
                    ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, facing.getIndex()), 3);
            world.setBlockState(candidate,
                    ConfigBlocks.blockTable.getDefaultState().withProperty(BlockTable.TYPE, facing.getOpposite().getIndex() + 4), 3);

            TileEntity researchTile = world.getTileEntity(pos);
            if (researchTile instanceof TileResearchTable) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                ((TileResearchTable) researchTile).setInventorySlotContents(0, copy);
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                researchTile.markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private static boolean isPlainTable(int meta) {
        return meta == 0 || meta == 1;
    }
}
