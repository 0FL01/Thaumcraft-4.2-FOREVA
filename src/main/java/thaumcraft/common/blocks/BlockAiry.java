package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileNitor;
import thaumcraft.common.tiles.TileWardingStoneFence;

import java.util.Random;

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
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        int meta = this.getMetaFromState(state);
        if (meta == 10) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float) (1 + worldIn.rand.nextInt(2)));
            entityIn.motionX *= 0.8D;
            entityIn.motionZ *= 0.8D;
            if (!worldIn.isRemote && worldIn.rand.nextInt(100) == 0) {
                worldIn.setBlockToAir(pos);
            }
        } else if (meta == 11 && !(entityIn instanceof IEldritchMob)) {
            if (worldIn.rand.nextInt(100) == 0) {
                entityIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
            }
            entityIn.motionX *= 0.66D;
            entityIn.motionZ *= 0.66D;
            if (entityIn instanceof EntityPlayer) {
                ((EntityPlayer) entityIn).addExhaustion(0.05F);
            }
            if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 1, true, false));
            }
        }
    }

    public static void explodify(World world, int x, int y, int z) {
        if (world == null || world.isRemote) {
            return;
        }
        BlockPos origin = new BlockPos(x, y, z);
        world.setBlockToAir(origin);
        world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 3.0F, false);
        for (int a = 0; a < 50; ++a) {
            int xx = x + world.rand.nextInt(8) - world.rand.nextInt(8);
            int yy = y + world.rand.nextInt(8) - world.rand.nextInt(8);
            int zz = z + world.rand.nextInt(8) - world.rand.nextInt(8);
            BlockPos randomPos = new BlockPos(xx, yy, zz);
            if (!world.isAirBlock(randomPos)) {
                continue;
            }
            if (yy < y) {
                world.setBlockState(randomPos, ConfigBlocks.blockFluxGoo.getStateFromMeta(8), 3);
            } else {
                world.setBlockState(randomPos, ConfigBlocks.blockFluxGas.getStateFromMeta(8), 3);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        int meta = this.getMetaFromState(state);
        if (!worldIn.isRemote && (meta == 10 || meta == 11)) {
            worldIn.setBlockToAir(pos);
        }
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
