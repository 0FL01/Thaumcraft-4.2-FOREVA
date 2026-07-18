package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;

import java.util.Random;

public class BlockTaint extends Block {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);

    public BlockTaint() {
        super(Config.taintMaterial);
        this.setHardness(1.5f);
        this.setResistance(3.0f);
        this.setSoundType(SoundType.GROUND);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("shovel", 0);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        int meta = this.getMetaFromState(state);
        if (meta == 2) return;

        BlockTaintFibres.taintBiomeSpread(world, pos, rand, this);
        if (meta == 0) {
            if (this.tryToFall(world, pos, pos)) {
                return;
            }
            if (world.isAirBlock(pos.up())) {
                EnumFacing direction = EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)];
                boolean canCascade = true;
                for (int depth = 0; depth < 4; depth++) {
                    if (!world.isAirBlock(pos.offset(direction).down(depth))
                            || world.getBlockState(pos.down(depth)).getBlock() != this) {
                        canCascade = false;
                        break;
                    }
                }
                if (canCascade && this.tryToFall(world, pos, pos.offset(direction))) {
                    return;
                }
            }
        }

        BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
        if (BlockTaintFibres.isTaintBiome(world, target)) {
            if (!BlockTaintFibres.spreadFibres(world, target)) {
                int adjacent = BlockTaintFibres.getAdjacentTaint(world, target);
                IBlockState targetState = world.getBlockState(target);
                Block targetBlock = targetState.getBlock();
                if (adjacent >= 2 && (Utils.isWoodLog(world, target) || targetBlock.isLeaves(targetState, world, target))) {
                    world.setBlockState(target, ConfigBlocks.blockTaint.getStateFromMeta(0), 3);
                    world.addBlockEvent(target, ConfigBlocks.blockTaint, 1, 0);
                } else if (adjacent >= 3 && isTaintSoilTarget(targetBlock)) {
                    world.setBlockState(target, ConfigBlocks.blockTaint.getStateFromMeta(1), 3);
                    world.addBlockEvent(target, ConfigBlocks.blockTaint, 1, 0);
                }
            }
            if (meta == 0 && !handleTaintSporeSwarmerSpawn(world, pos, rand)) {
                convertSurroundedTaintToFlux(world, pos);
            }
        } else if (meta == 0 && ConfigBlocks.blockFluxGoo != null && rand.nextInt(20) == 0) {
            world.setBlockState(pos, ConfigBlocks.blockFluxGoo.getStateFromMeta(ConfigBlocks.blockFluxGoo.getQuanta()), 3);
        } else if (meta == 1 && rand.nextInt(10) == 0) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
        }
    }

    private boolean handleTaintSporeSwarmerSpawn(World world, BlockPos pos, Random rand) {
        if (!Config.spawnTaintSpore || !world.isAirBlock(pos.up()) || rand.nextInt(200) != 0) {
            return false;
        }
        if (!world.getEntitiesWithinAABB(EntityTaintSporeSwarmer.class, new AxisAlignedBB(pos).grow(16.0D)).isEmpty()) {
            return true;
        }
        world.setBlockToAir(pos);
        EntityTaintSporeSwarmer swarmer = new EntityTaintSporeSwarmer(world);
        swarmer.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        world.spawnEntity(swarmer);
        world.playSound(null, pos, TCSounds.ROOTS, SoundCategory.BLOCKS,
                0.1F, 0.9F + world.rand.nextFloat() * 0.2F);
        return true;
    }

    private boolean convertSurroundedTaintToFlux(World world, BlockPos pos) {
        if (ConfigBlocks.blockFluxGoo == null || world.getBlockState(pos.up()).getBlock() != this) {
            return false;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(facing)).getBlock() != this) {
                return false;
            }
        }
        world.setBlockState(pos, ConfigBlocks.blockFluxGoo.getStateFromMeta(ConfigBlocks.blockFluxGoo.getQuanta()), 3);
        return true;
    }

    private static boolean isTaintSoilTarget(Block block) {
        return block == Blocks.GRASS
                || block == Blocks.DIRT
                || block == Blocks.STONE
                || block == Blocks.SAND
                || block == Blocks.GRAVEL
                || block == Blocks.CLAY;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 1) return 0;
        return meta;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 2) return Items.ROTTEN_FLESH;
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random) {
        return 9;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return this.getMetaFromState(state) == 2;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        if (id == 1) {
            if (world.isRemote) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), TCSounds.ROOTS,
                        SoundCategory.BLOCKS, 0.1F, 0.9F + world.rand.nextFloat() * 0.2F, false);
            }
            return true;
        }
        return super.eventReceived(state, world, pos, id, param);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 2));
    }

    /**
     * Checks if taint can fall below the given position.
     * Returns false if any adjacent block is a wood log (supports the taint).
     */
    public static boolean canFallBelow(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    if (Utils.isWoodLog(world, pos.add(xx, yy, zz))) {
                        return false;
                    }
                }
            }
        }
        Block block = state.getBlock();
        if (block.isAir(state, world, pos)) {
            return true;
        }
        if (block == ConfigBlocks.blockFluxGoo && block.getMetaFromState(state) >= 4) {
            return false;
        }
        if (block == Blocks.FIRE || block == ConfigBlocks.blockTaintFibres || block.isReplaceable(world, pos)) {
            return true;
        }
        Material material = state.getMaterial();
        return material == Material.WATER || material == Material.LAVA;
    }

    private boolean tryToFall(World world, BlockPos source, BlockPos destination) {
        if (destination.getY() < 0 || !canFallBelow(world, destination.down())) {
            return false;
        }

        int radius = 32;
        if (world.isAreaLoaded(destination.add(-radius, -radius, -radius),
                destination.add(radius, radius, radius), false)) {
            int meta = this.getMetaFromState(world.getBlockState(source));
            EntityFallingTaint fallingTaint = new EntityFallingTaint(world,
                    destination.getX() + 0.5D, destination.getY() + 0.5D, destination.getZ() + 0.5D,
                    this, meta, source.getX(), source.getY(), source.getZ());
            this.onStartFalling(fallingTaint);
            world.spawnEntity(fallingTaint);
            return true;
        }

        IBlockState sourceState = world.getBlockState(source);
        world.setBlockToAir(source);
        BlockPos landing = destination;
        while (landing.getY() > 0 && canFallBelow(world, landing.down())) {
            landing = landing.down();
        }
        if (landing.getY() > 0) {
            world.setBlockState(source, sourceState, 3);
        }
        return false;
    }

    protected void onStartFalling(EntityFallingTaint fallingTaint) {
    }

    /**
     * Called after a falling taint block lands and places itself.
     */
    public static void onFinishFalling(World world, BlockPos pos, IBlockState state) {
        // No-op hook for subclasses or future use
    }
}
