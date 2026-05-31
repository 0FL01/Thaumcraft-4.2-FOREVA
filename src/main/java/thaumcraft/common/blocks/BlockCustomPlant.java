package thaumcraft.common.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.world.WorldGenGreatwoodTrees;
import thaumcraft.common.lib.world.WorldGenSilverwoodTrees;
import thaumcraft.common.tiles.TileEtherealBloom;

import java.util.Random;

public class BlockCustomPlant extends BlockBush {

    public static final String[] plantTypes = {"greatwoodSapling", "silverwoodSapling", "shimmerleaf", "cinderpearl", "etherealBloom", "manashroom"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 5);
    private static final AxisAlignedBB CUSTOM_PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

    public BlockCustomPlant() {
        super(Material.PLANTS);
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CUSTOM_PLANT_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, Math.min(Math.max(meta, 0), 5));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 6; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = state.getValue(TYPE);
        if (meta == 1 || meta == 2 || meta == 3) return 8;
        if (meta == 4) return 15;
        if (meta == 5) return 8;
        return 0;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (state.getValue(TYPE) == 5 && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase) entityIn).addPotionEffect(
                    new PotionEffect(MobEffects.NAUSEA, 200, 0, true, false)
            );
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int type = state.getValue(TYPE);
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        if (type == 2 && rand.nextInt(3) == 0) {
            float r = 0.3F + rand.nextFloat() * 0.3F;
            float g = 0.7F + rand.nextFloat() * 0.3F;
            float b = 0.3F + rand.nextFloat() * 0.3F;
            Thaumcraft.proxy.drawGenericParticles(world,
                    x + rand.nextGaussian() * 0.3D, y + rand.nextGaussian() * 0.3D, z + rand.nextGaussian() * 0.3D,
                    0.0D, 0.0D, 0.0D,
                    r, g, b, 0.9F,
                    false, 0, 8, -1, 36, 0, 0.2F);
        } else if (type == 3 && rand.nextBoolean()) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    x + rand.nextGaussian() * 0.1D, y + 0.6D + rand.nextGaussian() * 0.1D, z + rand.nextGaussian() * 0.1D,
                    0.0D, 0.0D, 0.0D);
            world.spawnParticle(EnumParticleTypes.FLAME,
                    x + rand.nextGaussian() * 0.1D, y + 0.6D + rand.nextGaussian() * 0.1D, z + rand.nextGaussian() * 0.1D,
                    0.0D, 0.0D, 0.0D);
        } else if (type == 5 && rand.nextInt(3) == 0) {
            Thaumcraft.proxy.drawGenericParticles(world,
                    x + rand.nextGaussian() * 0.4D, y + 0.3D, z + rand.nextGaussian() * 0.4D,
                    0.0D, 0.0D, 0.0D,
                    0.5F, 0.3F, 0.8F, 0.9F,
                    false, 0, 8, -1, 36, 0, 0.1F);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TYPE) == 4;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(TYPE) == 4) {
            return new TileEtherealBloom();
        }
        return null;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) return EnumPlantType.Plains;
        int type = state.getValue(TYPE);
        if (type == 3) return EnumPlantType.Desert;
        if (type == 4) return EnumPlantType.Cave;
        return EnumPlantType.Plains;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        super.updateTick(world, pos, state, rand);
        int type = state.getValue(TYPE);
        if (type == 0 && world.getLight(pos.up()) >= 9 && rand.nextInt(25) == 0) {
            this.growGreatTree(world, pos, rand);
        } else if (type == 1 && world.getLight(pos.up()) >= 9 && rand.nextInt(50) == 0) {
            this.growSilverTree(world, pos, rand);
        }
    }

    public void growGreatTree(World world, BlockPos pos, Random rand) {
        if (world == null || world.provider == null) return;
        if (world.isRemote) return;
        world.setBlockToAir(pos);
        WorldGenGreatwoodTrees obj = new WorldGenGreatwoodTrees(true);
        if (!obj.generate(world, rand, pos)) {
            world.setBlockState(pos, this.getStateFromMeta(0));
        }
    }

    public void growSilverTree(World world, BlockPos pos, Random rand) {
        if (world == null || world.provider == null) return;
        if (world.isRemote) return;
        world.setBlockToAir(pos);
        WorldGenSilverwoodTrees obj = new WorldGenSilverwoodTrees(true, 7, 5);
        if (!obj.generate(world, rand, pos)) {
            world.setBlockState(pos, this.getStateFromMeta(1));
        }
    }
}
