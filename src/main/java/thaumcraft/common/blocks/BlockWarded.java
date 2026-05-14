package thaumcraft.common.blocks;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import thaumcraft.common.tiles.TileWarded;

public class BlockWarded extends BlockContainer {

    public BlockWarded() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
        this.setBlockUnbreakable();
        this.setResistance(999.0F);
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
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWarded();
    }

    public IBlockState getStoredState(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileWarded) {
            return ((TileWarded) tile).getStoredState();
        }
        return Blocks.STONE.getDefaultState();
    }

    @Override
    public Material getMaterial(IBlockState state) {
        return Material.ROCK;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getStoredState(world, pos).isNormalCube();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return this.getStoredState(world, pos).doesSideBlockRendering(world, pos, face);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.getStoredState(source, pos).getBoundingBox(source, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState stored = this.getStoredState(world, pos);
        return stored.getBlock().getCollisionBoundingBox(stored, world, pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState stored = this.getStoredState(world, pos);
        stored.getBlock().addCollisionBoxToList(stored, world, pos, entityBox, collidingBoxes, entity, isActualState);
    }

    @Override
    public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.getStoredState(world, pos).isSideSolid(world, pos, side);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return this.getStoredState(world, pos).getBlock().getSoundType(this.getStoredState(world, pos), world, pos, entity);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileWarded ? ((TileWarded) tile).light & 255 : 0;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getStoredState(world, pos).getLightOpacity(world, pos);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos,
                                    EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction,
                                   IPlantable plantable) {
        return this.getStoredState(world, pos).getBlock().canSustainPlant(this.getStoredState(world, pos), world, pos, direction, plantable);
    }

    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return this.getStoredState(world, pos).getBlock().isFertile(world, pos);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
        return this.getStoredState(world, pos).getBlock().isBeaconBase(world, pos, beacon);
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return this.getStoredState(world, pos).getBlock().getEnchantPowerBonus(world, pos);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getStoredState(world, pos).getBlock().getAiPathNodeType(this.getStoredState(world, pos), world, pos);
    }

    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving entity) {
        return this.getStoredState(world, pos).getBlock().getAiPathNodeType(this.getStoredState(world, pos), world, pos, entity);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        IBlockState stored = this.getStoredState(world, pos);
        return stored.getBlock().getPickBlock(stored, target, world, pos, player);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
    }
}
