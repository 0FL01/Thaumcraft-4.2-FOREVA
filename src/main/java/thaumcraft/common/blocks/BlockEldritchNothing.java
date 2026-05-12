package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEldritchNothing extends Block {
    public BlockEldritchNothing() {
        super(Material.AIR);
        this.setHardness(0.0f);
        this.setDefaultState(this.blockState.getBaseState());
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    
    @Override
    public boolean isFullCube(IBlockState state) { return false; }
    
    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new thaumcraft.common.tiles.TileEldritchNothing();
    }
}
