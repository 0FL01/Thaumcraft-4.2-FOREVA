package thaumcraft.common.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileEtherealBloom;

public class BlockCustomPlant extends BlockBush {

    public static final String[] plantTypes = {"greatwoodSapling", "silverwoodSapling", "shimmerleaf", "cinderpearl", "etherealBloom", "manashroom"};

    public BlockCustomPlant() {
        super(Material.PLANTS);
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 6; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 1 || meta == 2 || meta == 3) return 8;
        if (meta == 4) return 15;
        if (meta == 5) return 8;
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return this.getMetaFromState(state) == 4;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (this.getMetaFromState(state) == 4) {
            return new TileEtherealBloom();
        }
        return null;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 2) return EnumPlantType.Plains;
        if (meta == 3) return EnumPlantType.Desert;
        if (meta >= 4) return EnumPlantType.Cave;
        return EnumPlantType.Plains;
    }
}
