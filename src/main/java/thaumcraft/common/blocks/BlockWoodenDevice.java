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

public class BlockWoodenDevice extends BlockContainer {

    public BlockWoodenDevice() {
        super(Material.WOOD);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileBellows();
        if (meta == 1) return new TileSensor();
        if (meta == 2 || meta == 3) return new TileArcanePressurePlate();
        if (meta == 4) return new TileArcaneBoreBase();
        if (meta == 5) return new TileArcaneBore();
        if (meta == 8) return new TileBanner();
        return new TileOwned();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // bellows
        list.add(new ItemStack(this, 1, 4)); // bore base
        list.add(new ItemStack(this, 1, 5)); // bore
    }

    @Override
    public int damageDropped(IBlockState state) { return getMetaFromState(state); }
}
