package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileNitor;
import thaumcraft.common.tiles.TileWardingStoneFence;

public class BlockAiry extends BlockContainer {

    public static final String[] airyTypes = {"node", "nitor", "leavesFiller1", "leavesFiller2", "wardingFence", "energizedNode", null, null, null, null, "fire", "eerie", "barrier"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 12);

    public BlockAiry() {
        super(Material.AIR);
        this.setHardness(2.0f);
        this.setResistance(200.0f);
        this.setSoundType(SoundType.CLOTH);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    public boolean isAir(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 2 || meta == 3 || meta == 10 || meta == 11;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        int meta = this.getMetaFromState(worldIn.getBlockState(pos));
        return meta == 2 || meta == 3 || meta == 4 || meta == 10 || meta == 11;
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
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 0 || meta == 5 || meta == 10 || meta == 11) return 8;
        if (meta == 1) return 15;
        if (meta == 2 || meta == 3) return 15;
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 12) return -1.0f;
        return 2.0f;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 1 || meta == 4 || meta == 5;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta == 0) return new TileNode();
        if (meta == 1) return new TileNitor();
        if (meta == 4) return new TileWardingStoneFence();
        if (meta == 5) return new TileNodeEnergized();
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, this.getMetaFromState(state));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 12));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ, int meta, net.minecraft.entity.EntityLivingBase placer, net.minecraft.util.EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 12));
    }
}
