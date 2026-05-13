package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fluids.FluidUtil;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.tiles.*;

public class BlockMetalDevice extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 14);

    private int delay = 0;

    public BlockMetalDevice() {
        super(Material.IRON);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileCrucible();
        if (meta == 1) return new TileAlembic();
        if (meta == 5 || meta == 6) return new TileGrate();
        if (meta == 7) return new TileArcaneLamp();
        if (meta == 8) return new TileArcaneLampGrowth();
        if (meta == 10) return new TileThaumatorium();
        if (meta == 11) return new TileThaumatoriumTop();
        return new TilePedestal();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // crucible
        list.add(new ItemStack(this, 1, 1)); // alembic
        list.add(new ItemStack(this, 1, 7)); // lamp
    }

    @Override
    public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote || state.getValue(TYPE) != 0) return;

        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileCrucible)) return;

        TileCrucible tile = (TileCrucible) te;
        if (entityIn instanceof EntityItem && !(entityIn instanceof EntitySpecialItem) && tile.canProcessItems()) {
            tile.attemptSmelt((EntityItem) entityIn);
            return;
        }

        ++this.delay;
        if (this.delay < 10) return;
        this.delay = 0;

        if (entityIn instanceof EntityLivingBase && tile.canProcessItems()) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 0.4F, 2.0F + worldIn.rand.nextFloat() * 0.4F);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(TYPE) == 0
                && FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)) {
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileCrucible) {
            ((TileCrucible) te).spillRemnants();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 14));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 14));
    }
}
