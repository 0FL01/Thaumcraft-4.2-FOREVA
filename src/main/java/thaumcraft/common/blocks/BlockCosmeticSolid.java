package thaumcraft.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileWardingStone;

public class BlockCosmeticSolid extends Block {

    public static final int TYPE_TRAVEL = 2;
    public static final int TYPE_WARDING = 3;
    public static final String[] types = {"obsidianTile", "obsidianTotem", "pavingStone", "wardingStone", "thaumiumBlock", "tallowBlock", "pedestalTop", "arcaneStone", "nodeStone", "golemStone", "golemStoneActive", "eldritchStone", "eldritchPattern", "eldritchStone2", "crust", "eldritchPedestal"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);

    public BlockCosmeticSolid() {
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
        for (int i = 0; i < 16; i++) {
            if (types[i] != null) {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta <= 1) return 30.0f;
        if (meta == 4 || meta == 6 || meta == 7) return 4.0f;
        return 2.0f;
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_TRAVEL) return 9;
        if (meta == 14) return 4;
        return 0;
    }

    public float getExplosionResistance(World world, BlockPos pos, net.minecraft.entity.Entity exploder) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 0 || meta == 1 || meta == 8) return 999.0f;
        return super.getExplosionResistance(exploder);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == TYPE_WARDING || meta == 8;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_WARDING) return new TileWardingStone();
        if (meta == 8) return new TileNode();
        return null;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_TRAVEL || meta == TYPE_WARDING || meta == 13) return false;
        return super.canCreatureSpawn(state, world, pos, type);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this && state.getValue(TYPE) == TYPE_TRAVEL && entity instanceof EntityLivingBase) {
            if (world.isRemote) {
                Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 32768, 5);
            } else {
                EntityLivingBase living = (EntityLivingBase) entity;
                living.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, false, false));
                living.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, false, false));
            }
        }
        super.onEntityWalk(world, pos, entity);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(TYPE) != TYPE_WARDING) return;

        if (world.isBlockPowered(pos)) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(2); i++) {
                Thaumcraft.proxy.blockRunes(world, pos.getX(), pos.getY() + 0.7D, pos.getZ(),
                        0.2F + rand.nextFloat() * 0.4F, rand.nextFloat() * 0.3F,
                        0.8F + rand.nextFloat() * 0.2F, 20, -0.02F);
            }
            return;
        }

        if (!hasWardingAuraSpace(world, pos.up()) || !hasWardingAuraSpace(world, pos.up(2))) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(3); i++) {
                Thaumcraft.proxy.blockRunes(world, pos.getX(), pos.getY() + 0.7D, pos.getZ(),
                        0.9F + rand.nextFloat() * 0.1F, rand.nextFloat() * 0.3F,
                        rand.nextFloat() * 0.3F, 24, -0.02F);
            }
            return;
        }

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(1.0D, 1.0D, 1.0D));
        for (EntityLivingBase entity : entities) {
            if (entity instanceof EntityPlayer) continue;
            Thaumcraft.proxy.blockRunes(world, pos.getX(),
                    pos.getY() + 0.6D + rand.nextFloat() * Math.max(0.8F, entity.getEyeHeight()),
                    pos.getZ(), 0.6F + rand.nextFloat() * 0.4F, 0.0F,
                    0.3F + rand.nextFloat() * 0.7F, 20, 0.0F);
            break;
        }
    }

    private static boolean hasWardingAuraSpace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == ConfigBlocks.blockAiry || state.getBlock().isReplaceable(world, pos);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return true;
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
}
