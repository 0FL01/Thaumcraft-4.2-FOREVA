package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileWardingStone;

public class BlockCosmeticSolid extends Block {

    public static final String[] types = {"obsidianTile", "obsidianTotem", "pavingStone", "wardingStone", "thaumiumBlock", "tallowBlock", "pedestalTop", "arcaneStone", "nodeStone", "golemStone", "golemStoneActive", "eldritchStone", "eldritchPattern", "eldritchStone2", "crust", "eldritchPedestal"};

    public BlockCosmeticSolid() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState());
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
        if (meta == 2) return 9;
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
        return meta == 3 || meta == 8;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 3) return new TileWardingStone();
        if (meta == 8) return new TileNode();
        return null;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return true;
    }
}
