package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.*;

public class BlockMetalDevice extends BlockContainer {

    public BlockMetalDevice() {
        super(Material.IRON);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileCrucible();
        if (meta == 1) return new TileAlembic();
        if (meta == 5 || meta == 6) return new TileGrate();
        if (meta == 7) return new TileArcaneLamp();
        if (meta == 8) return new TileArcaneLampGrowth();
        if (meta == 10) return new TileThaumatorium();
        if (meta == 11) return new TileThaumatoriumTop();
        return new TilePedestal();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // crucible
        list.add(new ItemStack(this, 1, 1)); // alembic
        list.add(new ItemStack(this, 1, 7)); // lamp
    }

    @Override
    public int damageDropped(IBlockState state) { return getMetaFromState(state); }
}
