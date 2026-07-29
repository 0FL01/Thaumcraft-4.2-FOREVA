package thaumcraft.common.blocks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileArcanePressurePlate;
import thaumcraft.common.tiles.TileOwned;

/**
 * The Arcane Door has its own two-block state and ownership mechanics. It must
 * not extend vanilla's BlockDoor: BlockDoor hard-codes vanilla door items and
 * identifies unknown subclasses as an oak door.
 */
public class BlockArcaneDoor extends BlockContainer {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE =
            PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
    public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF =
            PropertyEnum.create("half", BlockDoor.EnumDoorHalf.class);

    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D);

    public BlockArcaneDoor() {
        super(Material.IRON);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(OPEN, false)
                .withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT)
                .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER));
        this.setSoundType(SoundType.METAL);
        this.setResistance(999.0F);
        if (Config.wardedStone) {
            this.setBlockUnbreakable();
        } else {
            this.setHardness(15.0F);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileOwned();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileOwned();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, OPEN, HINGE, HALF);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            IBlockState upper = worldIn.getBlockState(pos.up());
            if (upper.getBlock() == this) {
                state = state.withProperty(HINGE, upper.getValue(HINGE));
            }
        } else {
            IBlockState lower = worldIn.getBlockState(pos.down());
            if (lower.getBlock() == this) {
                state = state.withProperty(FACING, lower.getValue(FACING))
                        .withProperty(OPEN, lower.getValue(OPEN));
            }
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if ((meta & 8) != 0) {
            return this.getDefaultState()
                    .withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER)
                    .withProperty(HINGE, (meta & 1) != 0
                            ? BlockDoor.EnumHingePosition.RIGHT
                            : BlockDoor.EnumHingePosition.LEFT);
        }
        return this.getDefaultState()
                .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3).rotateYCCW())
                .withProperty(OPEN, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            return 8 | (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT ? 1 : 0);
        }
        int meta = state.getValue(FACING).rotateY().getHorizontalIndex();
        return state.getValue(OPEN) ? meta | 4 : meta;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state
                : state.withRotation(mirrorIn.toRotation(state.getValue(FACING))).cycleProperty(HINGE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = this.getActualState(state, source, pos);
        EnumFacing facing = actual.getValue(FACING);
        boolean closed = !actual.getValue(OPEN);
        boolean rightHinge = actual.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT;
        switch (facing) {
            case SOUTH:
                return closed ? SOUTH_AABB : (rightHinge ? EAST_AABB : WEST_AABB);
            case WEST:
                return closed ? WEST_AABB : (rightHinge ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return closed ? NORTH_AABB : (rightHinge ? WEST_AABB : EAST_AABB);
            case EAST:
            default:
                return closed ? EAST_AABB : (rightHinge ? NORTH_AABB : SOUTH_AABB);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        return state.getBlock() == this && this.getActualState(state, worldIn, pos).getValue(OPEN);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        if (pos.getY() >= worldIn.getHeight() - 1) {
            return false;
        }
        BlockPos below = pos.down();
        return worldIn.getBlockState(below).isSideSolid(worldIn, below, EnumFacing.UP)
                && super.canPlaceBlockAt(worldIn, pos)
                && super.canPlaceBlockAt(worldIn, pos.up());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileOwned)) {
            return true;
        }
        TileOwned owned = (TileOwned) tile;
        String playerName = playerIn.getName();
        boolean allowed = playerName.equals(owned.owner)
                || owned.accessList != null && (owned.accessList.contains("0" + playerName)
                || owned.accessList.contains("1" + playerName));
        if (!allowed) {
            playerIn.sendStatusMessage(new TextComponentString("The door refuses to budge."), true);
            worldIn.playSound(null, pos, TCSounds.DOORFAIL, SoundCategory.BLOCKS, 0.66F, 1.0F);
            return true;
        }

        IBlockState actual = this.getActualState(state, worldIn, pos);
        this.setDoorOpen(worldIn, pos, !actual.getValue(OPEN));
        return true;
    }

    public void setDoorOpen(World world, BlockPos pos, boolean open) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }
        BlockPos lowerPos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? pos.down() : pos;
        IBlockState lower = world.getBlockState(lowerPos);
        if (lower.getBlock() != this || lower.getValue(HALF) != BlockDoor.EnumDoorHalf.LOWER
                || lower.getValue(OPEN) == open) {
            return;
        }

        world.setBlockState(lowerPos, lower.withProperty(OPEN, open), 10);
        world.markBlockRangeForRenderUpdate(lowerPos, lowerPos.up());
        world.playSound(null, lowerPos,
                open ? SoundEvents.BLOCK_WOODEN_DOOR_OPEN : SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
                SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
    }

    public void onPoweredBlockChange(World world, BlockPos pos, boolean open) {
        this.setDoorOpen(world, pos, open);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isRemote) {
            return;
        }
        if (blockIn == ConfigBlocks.blockWoodenDevice) {
            this.updateFromArcanePressurePlates(worldIn, pos);
            return;
        }

        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            BlockPos lowerPos = pos.down();
            IBlockState lower = worldIn.getBlockState(lowerPos);
            if (lower.getBlock() != this) {
                worldIn.setBlockToAir(pos);
            } else if (blockIn != this) {
                lower.neighborChanged(worldIn, lowerPos, blockIn, fromPos);
            }
            return;
        }

        if (worldIn.getBlockState(pos.up()).getBlock() != this) {
            worldIn.setBlockToAir(pos);
            if (!Config.wardedStone) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }
        }
    }

    private void updateFromArcanePressurePlates(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() != this) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileOwned)) {
            return;
        }

        Set<String> identities = new HashSet<>();
        TileOwned door = (TileOwned) tile;
        identities.add(door.owner == null ? "" : door.owner);
        if (door.accessList != null) {
            for (String access : door.accessList) {
                if (access != null && access.length() > 1) {
                    identities.add(access.substring(1));
                }
            }
        }

        boolean matchingUnpressedPlate = false;
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            BlockPos platePos = pos.offset(direction);
            IBlockState plateState = world.getBlockState(platePos);
            if (plateState.getBlock() != ConfigBlocks.blockWoodenDevice) {
                continue;
            }
            int type = plateState.getValue(BlockWoodenDevice.TYPE);
            if (type != 2 && type != 3) {
                continue;
            }
            TileEntity plateTile = world.getTileEntity(platePos);
            if (!(plateTile instanceof TileArcanePressurePlate)
                    || !matchesPlate((TileArcanePressurePlate) plateTile, identities)) {
                continue;
            }
            if (type == 3) {
                this.setDoorOpen(world, pos, true);
                return;
            }
            matchingUnpressedPlate = true;
        }
        if (matchingUnpressedPlate) {
            this.setDoorOpen(world, pos, false);
        }
    }

    private static boolean matchesPlate(TileArcanePressurePlate plate, Set<String> identities) {
        for (String identity : identities) {
            if (identity.equals(plate.owner)
                    || plate.accessList != null && plate.accessList.contains(identity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (Config.wardedStone || state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            return Items.AIR;
        }
        return ConfigItems.itemArcaneDoor;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ConfigItems.itemArcaneDoor);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        return new ItemStack(ConfigItems.itemArcaneDoor);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
    }
}
