package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileResearchTable;
import thaumcraft.common.tiles.TileTable;

import javax.annotation.Nullable;

public class BlockTable
extends BlockContainer
implements IWandable {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);
    private static final AxisAlignedBB RESEARCH_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, -1.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB RESEARCH_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 2.0D);
    private static final AxisAlignedBB RESEARCH_WEST_AABB = new AxisAlignedBB(-1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB RESEARCH_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 2.0D, 1.0D, 1.0D);

    public BlockTable() {
        super(Material.WOOD);
        this.setHardness(2.5f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("axe", 0);
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if ((meta <= 1 || meta >= 6) && meta < 14) {
            return new TileTable();
        }
        if (meta == 14) {
            return new TileDeconstructionTable();
        }
        if (meta == 15) {
            return new TileArcaneWorkbench();
        }
        return new TileResearchTable();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if ((meta <= 1 || meta >= 6) && meta < 14) {
            return new TileTable();
        }
        if (meta == 14) {
            return new TileDeconstructionTable();
        }
        if (meta == 15) {
            return new TileArcaneWorkbench();
        }
        return new TileResearchTable();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 14));
        list.add(new ItemStack(this, 1, 15));
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 14) return 14;
        if (meta == 15) return 15;
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int md = this.getMetaFromState(state);
        if (md < 14) {
            int l = MathHelper.floor((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            int out = (l == 0 || l == 2) ? 0 : 1;
            worldIn.setBlockState(pos, state.withProperty(TYPE, out), 3);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        int md = state.getValue(TYPE);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileResearchTable && md >= 2 && md <= 5) {
            EnumFacing facing = EnumFacing.byIndex(md);
            IBlockState partnerState = worldIn.getBlockState(pos.offset(facing));
            if (partnerState.getBlock() != this || partnerState.getValue(TYPE) < 6) {
                InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());
                worldIn.removeTileEntity(pos);
                worldIn.setBlockState(pos, this.getDefaultState().withProperty(TYPE, 0), 3);
            }
        } else if (md >= 6 && md <= 9) {
            EnumFacing facing = EnumFacing.byIndex(md - 4);
            TileEntity master = worldIn.getTileEntity(pos.offset(facing));
            if (!(master instanceof TileResearchTable)) {
                worldIn.setBlockState(pos, this.getDefaultState().withProperty(TYPE, 0), 3);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        int md = this.getMetaFromState(state);
        if (md <= 1 || tileEntity == null || playerIn.isSneaking()) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        if (tileEntity instanceof TileArcaneWorkbench) {
            playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_ARCANE_WORKBENCH, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        if (tileEntity instanceof TileDeconstructionTable) {
            playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_DECONSTRUCTION_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        if (tileEntity instanceof TileResearchTable) {
            playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_RESEARCH_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        } else {
            // Check adjacent blocks for research table
            for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                TileEntity tile = worldIn.getTileEntity(pos.offset(dir));
                if (tile instanceof TileResearchTable) {
                    BlockPos rp = pos.offset(dir);
                    playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_RESEARCH_TABLE, worldIn, rp.getX(), rp.getY(), rp.getZ());
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (this.getMetaFromState(state)) {
            case 5:
            case 9:
                return RESEARCH_EAST_AABB;
            case 4:
            case 8:
                return RESEARCH_WEST_AABB;
            case 3:
            case 7:
                return RESEARCH_SOUTH_AABB;
            case 2:
            case 6:
                return RESEARCH_NORTH_AABB;
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return this.getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 15));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 15));
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (md <= 1) {
            if (!world.isRemote) {
                ItemWandCasting wand = (ItemWandCasting) wandstack.getItem();
                BlockPos pos = new BlockPos(x, y, z);
                world.removeTileEntity(pos);
                world.setBlockState(pos, ConfigBlocks.blockTable.getDefaultState().withProperty(TYPE, 15), 3);
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileArcaneWorkbench) {
                    TileArcaneWorkbench tawb = (TileArcaneWorkbench) tile;
                    if (!wand.isStaff(wandstack)) {
                        tawb.setInventorySlotContents(10, wandstack.copy());
                        wandstack.setCount(0);
                    }
                    tawb.markDirty();
                }
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                world.playSound(null, x + 0.5D, y + 0.1D, z + 0.5D,
                        net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, net.minecraft.util.SoundCategory.BLOCKS,
                        0.15F, 0.5F);
            }
            return 0;
        }
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }
}
