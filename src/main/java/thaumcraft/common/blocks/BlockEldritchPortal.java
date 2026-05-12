package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEldritchPortal extends Block {
    public BlockEldritchPortal() {
        super(Material.PORTAL);
        this.setHardness(0.0f);
        this.setLightLevel(1.0f);
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
        return new thaumcraft.common.tiles.TileEldritchPortal();
    }
    
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity.getRidingEntity() == null) {
            // Teleport to Outer Lands dimension
        }
    }
}
