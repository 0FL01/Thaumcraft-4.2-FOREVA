package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.*;

public class BlockWoodenDevice extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 8);

    public BlockWoodenDevice() {
        super(Material.WOOD);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("axe", 0);
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
        list.add(new ItemStack(this, 1, 1)); // sensor
        list.add(new ItemStack(this, 1, 2)); // pressure plate
        list.add(new ItemStack(this, 1, 3)); // pressure plate
        list.add(new ItemStack(this, 1, 4)); // bore base
        list.add(new ItemStack(this, 1, 5)); // bore
        list.add(new ItemStack(this, 1, 8)); // banner
    }

    @Override
    public int damageDropped(IBlockState state) { return getMetaFromState(state); }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileArcaneBore) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileArcaneBore) te).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
            if (!worldIn.isRemote) {
                playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_ARCANE_BORE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        if (te instanceof TileArcaneBoreBase) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileArcaneBoreBase) te).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof IInventory) {
            IInventory inventory = (IInventory) te;
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    worldIn.spawnEntity(new EntityItem(worldIn,
                            (double) pos.getX() + 0.5D,
                            (double) pos.getY() + 0.5D,
                            (double) pos.getZ() + 0.5D,
                            stack.copy()));
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 8));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 8));
    }
}
