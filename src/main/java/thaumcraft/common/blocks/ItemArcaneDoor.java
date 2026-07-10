package thaumcraft.common.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileOwned;

public class ItemArcaneDoor extends Item {

    public ItemArcaneDoor() {
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }

        BlockPos placePos = pos.up();
        BlockArcaneDoor door = ConfigBlocks.blockArcaneDoor;
        if (!player.canPlayerEdit(placePos, facing, stack)
                || !player.canPlayerEdit(placePos.up(), facing, stack)
                || !door.canPlaceBlockAt(worldIn, placePos)) {
            return EnumActionResult.FAIL;
        }

        EnumFacing direction = EnumFacing.fromAngle(player.rotationYaw);
        placeDoor(worldIn, placePos, direction, door, player.getName());
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    public static void placeDoor(World world, BlockPos pos, EnumFacing facing, BlockArcaneDoor door, String owner) {
        EnumFacing clockwise = facing.rotateY();
        EnumFacing counterClockwise = facing.rotateYCCW();
        int clockwiseBlocks = countSolidBlocks(world, pos, clockwise);
        int counterClockwiseBlocks = countSolidBlocks(world, pos, counterClockwise);
        boolean clockwiseDoor = hasDoor(world, pos.offset(clockwise), door);
        boolean counterClockwiseDoor = hasDoor(world, pos.offset(counterClockwise), door);
        boolean hingeRight = counterClockwiseDoor && !clockwiseDoor
                || clockwiseBlocks > counterClockwiseBlocks;
        BlockDoor.EnumHingePosition hinge = hingeRight
                ? BlockDoor.EnumHingePosition.RIGHT
                : BlockDoor.EnumHingePosition.LEFT;

        IBlockState lower = door.getDefaultState()
                .withProperty(BlockArcaneDoor.FACING, facing)
                .withProperty(BlockArcaneDoor.OPEN, false)
                .withProperty(BlockArcaneDoor.HINGE, hinge)
                .withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.LOWER);
        IBlockState upper = door.getDefaultState()
                .withProperty(BlockArcaneDoor.HINGE, hinge)
                .withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);

        world.setBlockState(pos, lower, 2);
        world.setBlockState(pos.up(), upper, 2);
        setOwner(world, pos, owner);
        setOwner(world, pos.up(), owner);
        world.notifyNeighborsOfStateChange(pos, door, false);
        world.notifyNeighborsOfStateChange(pos.up(), door, false);
    }

    private static int countSolidBlocks(World world, BlockPos pos, EnumFacing side) {
        int count = world.getBlockState(pos.offset(side)).isNormalCube() ? 1 : 0;
        return count + (world.getBlockState(pos.up().offset(side)).isNormalCube() ? 1 : 0);
    }

    private static boolean hasDoor(World world, BlockPos pos, BlockArcaneDoor door) {
        return world.getBlockState(pos).getBlock() == door
                || world.getBlockState(pos.up()).getBlock() == door;
    }

    private static void setOwner(World world, BlockPos pos, String owner) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileOwned) {
            ((TileOwned) tile).owner = owner == null ? "" : owner;
            tile.markDirty();
        }
    }
}
