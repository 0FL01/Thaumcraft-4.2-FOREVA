package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileMirrorEssentia;

public class BlockMirrorItem extends BlockMetadataItem {
    public BlockMirrorItem(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() == ConfigBlocks.blockMirror && stack.getItemDamage() >= 6) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileMirrorEssentia && !((TileMirrorEssentia) tile).isLinkValid()) {
                if (!world.isRemote) {
                    ItemStack linked = stack.copy();
                    linked.setCount(1);
                    linked.setItemDamage(7);
                    NBTTagCompound tag = linked.getTagCompound();
                    if (tag == null) tag = new NBTTagCompound();
                    tag.setInteger("linkX", pos.getX());
                    tag.setInteger("linkY", pos.getY());
                    tag.setInteger("linkZ", pos.getZ());
                    tag.setInteger("linkDim", world.provider.getDimension());
                    linked.setTagCompound(tag);
                    if (!player.inventory.addItemStackToInventory(linked)) {
                        player.dropItem(linked, false);
                    }
                    if (!player.capabilities.isCreativeMode) stack.shrink(1);
                    player.inventoryContainer.detectAndSendChanges();
                }
                player.swingArm(hand);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (placed && !world.isRemote && stack.hasTagCompound()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileMirrorEssentia) {
                TileMirrorEssentia mirror = (TileMirrorEssentia) tile;
                mirror.linkX = stack.getTagCompound().getInteger("linkX");
                mirror.linkY = stack.getTagCompound().getInteger("linkY");
                mirror.linkZ = stack.getTagCompound().getInteger("linkZ");
                mirror.linkDim = stack.getTagCompound().getInteger("linkDim");
                mirror.restoreLink();
            }
        }
        return placed;
    }
}
