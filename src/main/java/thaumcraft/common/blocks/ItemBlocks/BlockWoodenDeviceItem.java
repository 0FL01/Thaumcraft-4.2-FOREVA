package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileBellows;
import thaumcraft.common.tiles.TileOwned;

public class BlockWoodenDeviceItem extends BlockMetadataItem {
    public BlockWoodenDeviceItem(Block block) {
        super(block);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("color")) {
            return super.getTranslationKey(stack) + "." + stack.getTagCompound().getByte("color");
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!placed) return false;

        TileEntity tile = world.getTileEntity(pos);
        int metadata = stack.getItemDamage();
        if (tile instanceof TileOwned && player != null && (((TileOwned) tile).owner == null || ((TileOwned) tile).owner.isEmpty())) {
            ((TileOwned) tile).owner = player.getName();
            tile.markDirty();
        }
        if (metadata == 0 && tile instanceof TileBellows) {
            EnumFacing out = side.getOpposite();
            TileBellows bellows = (TileBellows) tile;
            bellows.orientation = (byte) out.getIndex();
            bellows.onVanillaFurnace = world.getBlockState(pos.offset(out)).getBlock() == Blocks.FURNACE
                    || world.getBlockState(pos.offset(out)).getBlock() == Blocks.LIT_FURNACE;
            bellows.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            if (!world.isRemote) {
                world.notifyNeighborsOfStateChange(pos, newState.getBlock(), false);
            }
        } else if (metadata == 4 && tile instanceof TileArcaneBoreBase) {
            ((TileArcaneBoreBase) tile).orientation = player == null ? EnumFacing.NORTH : player.getHorizontalFacing().getOpposite();
            tile.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        } else if (metadata == 5 && tile instanceof TileArcaneBore) {
            TileArcaneBore bore = (TileArcaneBore) tile;
            bore.baseOrientation = side;
            if (player != null) {
                int facing = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                switch (facing) {
                    case 0: bore.orientation = EnumFacing.SOUTH; break;
                    case 1: bore.orientation = EnumFacing.WEST; break;
                    case 2: bore.orientation = EnumFacing.NORTH; break;
                    case 3: bore.orientation = EnumFacing.EAST; break;
                    default: bore.orientation = EnumFacing.UP; break;
                }
            }
            bore.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        } else if (metadata == 8 && tile instanceof TileBanner) {
            TileBanner banner = (TileBanner) tile;
            if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
                int facing = MathHelper.floor(((player == null ? 0.0F : player.rotationYaw) + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
                banner.setFacing((byte) facing);
            } else {
                banner.setWall(true);
                byte facing = 0;
                if (side == EnumFacing.NORTH) facing = 8;
                if (side == EnumFacing.WEST) facing = 4;
                if (side == EnumFacing.EAST) facing = 12;
                banner.setFacing(facing);
            }

            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                if (tag.hasKey("aspect")) {
                    String aspectTag = tag.getString("aspect");
                    if (aspectTag != null && !aspectTag.isEmpty()) {
                        banner.setAspect(Aspect.getAspect(aspectTag));
                    }
                }
                if (tag.hasKey("color")) {
                    banner.setColor(tag.getByte("color"));
                }
            }
            banner.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return true;
    }
}
