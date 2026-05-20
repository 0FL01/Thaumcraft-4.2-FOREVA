package thaumcraft.common.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.*;

public class BlockTube extends BlockContainer {
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 7);
    private static final AxisAlignedBB TUBE_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);

    public BlockTube() {
        super(Material.CIRCUITS);
        this.setHardness(0.5F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return this.getMetaFromState(state) == 2 ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        switch (meta) {
            case 1: return new TileTubeValve();
            case 2: return new TileCentrifuge();
            case 3: return new TileTubeFilter();
            case 4: return new TileTubeBuffer();
            case 5: return new TileTubeRestrict();
            case 6: return new TileTubeOneway();
            case 7: return new TileEssentiaCrystalizer();
            default: return new TileTube();
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.createNewTileEntity(world, this.getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int meta = 0; meta <= 7; ++meta) {
            list.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    public int damageDropped(IBlockState state) { return this.getMetaFromState(state); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting && tile instanceof IWandable) {
            return ((IWandable) tile).onWandRightClick(world, held, player,
                    pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), this.getMetaFromState(state)) >= 0;
        }
        if (tile instanceof TileTubeValve) {
            if (!world.isRemote) {
                TileTubeValve valve = (TileTubeValve) tile;
                valve.allowFlow = !valve.allowFlow;
                valve.markDirtyAndSync();
            }
            return true;
        }
        if (tile instanceof TileTubeFilter) {
            return this.handleFilterActivation(world, pos, player, hand, (TileTubeFilter) tile);
        }
        return false;
    }

    private boolean handleFilterActivation(World world, BlockPos pos, EntityPlayer player, EnumHand hand, TileTubeFilter filter) {
        ItemStack held = player.getHeldItem(hand);
        if (player.isSneaking() && filter.aspectFilter != null) {
            if (!world.isRemote) {
                filter.aspectFilter = null;
                filter.markDirtyAndSync();
                if (ConfigItems.itemResource != null) {
                    spawnAsEntity(world, pos, new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_LABEL));
                }
            }
            return true;
        }
        if (!held.isEmpty() && held.getItem() instanceof IEssentiaContainerItem && filter.aspectFilter == null) {
            AspectList aspects = ((IEssentiaContainerItem) held.getItem()).getAspects(held);
            if (aspects != null && aspects.getAspects().length > 0) {
                if (!world.isRemote) {
                    filter.aspectFilter = aspects.getAspects()[0];
                    if (!player.capabilities.isCreativeMode) held.shrink(1);
                    filter.markDirtyAndSync();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileTubeBuffer) {
            return MathHelper.floor(((TileTubeBuffer) tile).aspects.visSize() / 8.0F * 14.0F)
                    + (((TileTubeBuffer) tile).aspects.visSize() > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.getMetaFromState(state) == 7 ? FULL_BLOCK_AABB : TUBE_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return this.getMetaFromState(blockState) == 7 ? FULL_BLOCK_AABB : TUBE_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, TYPE); }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) { return state.getValue(TYPE); }
}
