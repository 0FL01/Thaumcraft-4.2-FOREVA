package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

import net.minecraft.block.properties.PropertyInteger;

public class BlockCosmeticOpaque extends Block {

    public static final String[] opaqueTypes = {"arcaneStoneBrick", "arcaneStoneTile", "arcaneStonePaver", "stonePaver", "stonePaverTraveller"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 4);

    public BlockCosmeticOpaque() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 3; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (this.getMetaFromState(world.getBlockState(target.getBlockPos())) == 2) {
            BlockPos pos = target.getBlockPos();
            Thaumcraft.proxy.blockWard(world, pos.getX(), pos.getY(), pos.getZ(), target.sideHit,
                    (float) (target.hitVec.x - pos.getX()),
                    (float) (target.hitVec.y - pos.getY()),
                    (float) (target.hitVec.z - pos.getZ()));
            return true;
        }
        return false;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            int meta = state.getValue(TYPE);
            if (meta == 3 || meta == 4) {
                int target = meta == 3 ? BlockCosmeticSolid.TYPE_WARDING : BlockCosmeticSolid.TYPE_TRAVEL;
                worldIn.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getDefaultState()
                        .withProperty(BlockCosmeticSolid.TYPE, target), 3);
                return;
            }
        }
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 4));
    }
}
