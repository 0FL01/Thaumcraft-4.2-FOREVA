package thaumcraft.common.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileMirror;
import thaumcraft.common.tiles.TileMirrorEssentia;

public class BlockMirror extends BlockContainer {
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 11);

    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockMirror() {
        super(Material.GLASS);
        this.setHardness(1.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.GLASS);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta >= 6 ? new TileMirrorEssentia() : new TileMirror();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.createNewTileEntity(world, this.getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 6));
    }

    @Override
    public int damageDropped(IBlockState state) { return this.getMetaFromState(state) >= 6 ? 6 : 0; }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMirror) {
            ((TileMirror) tile).invalidateLink();
        } else if (tile instanceof TileMirrorEssentia) {
            ((TileMirrorEssentia) tile).invalidateLink();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta < 6) {
            TileEntity tile = world.getTileEntity(pos);
            ItemStack drop = new ItemStack(this, 1, 0);
            if (tile instanceof TileMirror) {
                TileMirror mirror = (TileMirror) tile;
                if (mirror.linked) {
                    NBTTagCompound tag = drop.hasTagCompound() ? drop.getTagCompound() : new NBTTagCompound();
                    tag.setInteger("linkX", mirror.linkX);
                    tag.setInteger("linkY", mirror.linkY);
                    tag.setInteger("linkZ", mirror.linkZ);
                    tag.setInteger("linkDim", mirror.linkDim);
                    if (world instanceof World) {
                        tag.setString("dimname", ((World) world).provider.getDimensionType().getName());
                    }
                    drop.setTagCompound(tag);
                    drop.setItemDamage(1);
                    mirror.invalidateLink();
                }
            }
            drops.add(drop);
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        ItemStack drop = new ItemStack(this, 1, 6);
        if (tile instanceof TileMirrorEssentia) {
            TileMirrorEssentia mirror = (TileMirrorEssentia) tile;
            if (mirror.linked) {
                NBTTagCompound tag = drop.hasTagCompound() ? drop.getTagCompound() : new NBTTagCompound();
                tag.setInteger("linkX", mirror.linkX);
                tag.setInteger("linkY", mirror.linkY);
                tag.setInteger("linkZ", mirror.linkZ);
                tag.setInteger("linkDim", mirror.linkDim);
                if (world instanceof World) {
                    tag.setString("dimname", ((World) world).provider.getDimensionType().getName());
                }
                drop.setTagCompound(tag);
                drop.setItemDamage(7);
                mirror.invalidateLink();
            }
        }
        drops.add(drop);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote) {
            return;
        }
        if (state.getValue(TYPE) < 6 && entityIn instanceof EntityItem && !entityIn.isDead && !((EntityItem) entityIn).cannotPickup()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileMirror) {
                ((TileMirror) tile).transport((EntityItem) entityIn);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (worldIn.isRemote) {
            return;
        }
        int meta = state.getValue(TYPE);
        if (!this.isAttachedToSolid(worldIn, pos, meta)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.isSideSolid(pos.offset(side.getOpposite()), side, false);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            if (worldIn.isSideSolid(pos.offset(face.getOpposite()), face, false)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) { return ZERO_AABB; }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        double w = 0.0625D;
        switch (this.getMetaFromState(state) % 6) {
            case 0: return new AxisAlignedBB(0.0D, 1.0D - w, 0.0D, 1.0D, 1.0D, 1.0D);
            case 1: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, w, 1.0D);
            case 2: return new AxisAlignedBB(0.0D, 0.0D, 1.0D - w, 1.0D, 1.0D, 1.0D);
            case 3: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, w);
            case 4: return new AxisAlignedBB(1.0D - w, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            case 5: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, w, 1.0D, 1.0D);
            default: return FULL_BLOCK_AABB;
        }
    }

    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 11));
    }

    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(TYPE); }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        int base = meta >= 6 ? 6 : 0;
        return this.getDefaultState().withProperty(TYPE, base + facing.getIndex());
    }

    private boolean isAttachedToSolid(World world, BlockPos pos, int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta % 6);
        if (facing == null) {
            return false;
        }
        BlockPos support = pos.offset(facing.getOpposite());
        return world.isSideSolid(support, facing, false);
    }
}
