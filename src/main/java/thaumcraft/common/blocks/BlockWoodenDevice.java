package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.init.SoundEvents;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.*;

public class BlockWoodenDevice extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 8);

    public BlockWoodenDevice() {
        super(Material.WOOD);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("axe", 0);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileBellows();
        if (meta == 1) return new TileSensor();
        if (meta == 2 || meta == 3) return new TileArcanePressurePlate();
        if (meta == 4) return new TileArcaneBoreBase();
        if (meta == 5) return new TileArcaneBore();
        if (meta == 8) return new TileBanner();
        return new TileOwned();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // bellows
        list.add(new ItemStack(this, 1, 1)); // sensor
        list.add(new ItemStack(this, 1, 2)); // pressure plate
        list.add(new ItemStack(this, 1, 3)); // pressure plate
        list.add(new ItemStack(this, 1, 4)); // bore base
        list.add(new ItemStack(this, 1, 5)); // bore
        list.add(new ItemStack(this, 1, 8)); // banner
    }

    @Override
    public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSensor) {
            if (!worldIn.isRemote) {
                TileSensor sensor = (TileSensor) te;
                sensor.changePitch();
                sensor.triggerNote(worldIn, pos.getX(), pos.getY(), pos.getZ(), true);
            }
            return true;
        }
        if (te instanceof TileArcaneBore) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileArcaneBore) te).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
            if (!worldIn.isRemote) {
                playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_ARCANE_BORE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        if (te instanceof TileArcaneBoreBase) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileArcaneBoreBase) te).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof IInventory) {
            IInventory inventory = (IInventory) te;
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    worldIn.spawnEntity(new EntityItem(worldIn,
                            (double) pos.getX() + 0.5D,
                            (double) pos.getY() + 0.5D,
                            (double) pos.getZ() + 0.5D,
                            stack.copy()));
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSensor) {
            ((TileSensor) te).updateTone();
            te.markDirty();
        }
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (state.getValue(TYPE) == 1) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileSensor) {
                ((TileSensor) te).updateTone();
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @javax.annotation.Nullable EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 0) {
            return false;
        }
        if (meta == 1 || meta == 2 || meta == 3 || meta == 4 || meta == 5) {
            return true;
        }
        return super.canConnectRedstone(state, world, pos, side);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 1) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileSensor) {
                return ((TileSensor) tile).redstoneSignal > 0 ? 15 : 0;
            }
        } else if (meta == 2) {
            return 0;
        } else if (meta == 3) {
            return side == EnumFacing.UP ? 15 : 0;
        }
        return super.getWeakPower(state, world, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 1) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileSensor) {
                return ((TileSensor) tile).redstoneSignal > 0 ? 15 : 0;
            }
        } else if (meta == 3) {
            return 15;
        }
        return super.getStrongPower(state, world, pos, side);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (id <= 4) {
            float pitch = (float) Math.pow(2.0D, (param - 12) / 12.0D);
            if (id >= 0) {
                switch (id) {
                    case 1:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 2:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 3:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 4:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BASS, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    default:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                }
            }
            worldIn.spawnParticle(EnumParticleTypes.NOTE,
                    pos.getX() + 0.5D,
                    pos.getY() + 1.2D,
                    pos.getZ() + 0.5D,
                    (double) param / 24.0D,
                    0.0D,
                    0.0D);
            return true;
        }
        return super.eventReceived(state, worldIn, pos, id, param);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 8));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 8));
    }
}
