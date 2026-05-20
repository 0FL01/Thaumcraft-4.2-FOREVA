package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
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
import thaumcraft.common.CommonProxy;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.*;

import javax.annotation.Nullable;

public class BlockStoneDevice
extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 14);

    public BlockStoneDevice() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 0);
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
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = getMetaFromState(state);
        return meta == 0 || meta == 1 || meta == 2 || meta == 3 || meta == 5
                || meta == 9 || meta == 10 || meta == 11 || meta == 12 || meta == 13 || meta == 14;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileAlchemyFurnace();
        if (meta == 1) return new TilePedestal();
        if (meta == 2) return new TileInfusionMatrix();
        if (meta == 3) return new TileInfusionPillar();
        if (meta == 5) return new TileWandPedestal();
        if (meta == 9 || meta == 10) return new TileNodeStabilizer();
        if (meta == 11) return new TileNodeConverter();
        if (meta == 12) return new TileSpa();
        if (meta == 13) return new TileFocalManipulator();
        if (meta == 14) return new TileFluxScrubber();
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // alchemy furnace
        list.add(new ItemStack(this, 1, 1)); // pedestal
        list.add(new ItemStack(this, 1, 2)); // infusion matrix
        list.add(new ItemStack(this, 1, 5)); // wand pedestal
        list.add(new ItemStack(this, 1, 8)); // wand pedestal focus
        list.add(new ItemStack(this, 1, 9)); // node stabilizer
        list.add(new ItemStack(this, 1, 10)); // advanced node stabilizer
        list.add(new ItemStack(this, 1, 11)); // node converter
        list.add(new ItemStack(this, 1, 12)); // spa
        list.add(new ItemStack(this, 1, 13)); // focal manipulator
        list.add(new ItemStack(this, 1, 14)); // flux scrubber
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = getMetaFromState(state);
        if (meta == 3) return 7;
        if (meta == 4) return 6;
        return meta;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof IInventory) {
            IInventory inv = (IInventory) te;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                }
            }
        }
        if (te instanceof TileInfusionMatrix && ((TileInfusionMatrix) te).crafting) {
            worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, true);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileAlchemyFurnace) {
            ((TileAlchemyFurnace) te).getBellows();
        } else if (te instanceof TileNodeConverter) {
            ((TileNodeConverter) te).checkStatus();
        }
        int type = state.getValue(TYPE);
        if (type == 1) {
            if (!worldIn.isAirBlock(pos.up())) {
                InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        } else if (type == 5) {
            IBlockState above = worldIn.getBlockState(pos.up());
            if (!worldIn.isAirBlock(pos.up())
                    && (above.getBlock() != this || above.getValue(TYPE) != 8)) {
                InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        } else if (type == 3) {
            IBlockState above = worldIn.getBlockState(pos.up());
            if (above.getBlock() != this || above.getValue(TYPE) != 4) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return;
            }
        } else if (type == 4) {
            IBlockState below = worldIn.getBlockState(pos.down());
            if (below.getBlock() != this || below.getValue(TYPE) != 3) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return;
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileAlchemyFurnace) {
            if (!worldIn.isRemote) {
                playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_ALCHEMY_FURNACE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        if (state.getValue(TYPE) == 1 && te instanceof TilePedestal) {
            return this.handlePedestalActivation(worldIn, pos, playerIn, hand, (TilePedestal) te);
        }
        if (te instanceof TileInfusionMatrix) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileInfusionMatrix) te).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
            return false;
        }
        if (te instanceof TileFocalManipulator) {
            if (!worldIn.isRemote) {
                playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_FOCAL_MANIPULATOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        if (te instanceof TileSpa) {
            if (!worldIn.isRemote) {
                playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_SPA, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    private boolean handlePedestalActivation(World world, BlockPos pos, EntityPlayer player, EnumHand hand, TilePedestal pedestal) {
        if (world.isRemote) return true;

        ItemStack stored = pedestal.getStackInSlot(0);
        if (!stored.isEmpty()) {
            ItemStack remaining = pedestal.removeStackFromSlot(0).copy();
            if (!player.inventory.addItemStackToInventory(remaining) || !remaining.isEmpty()) {
                EntityItem entity = new EntityItem(world, player.posX, player.posY + player.height / 2.0F, player.posZ, remaining.copy());
                entity.setNoPickupDelay();
                world.spawnEntity(entity);
            }
            player.inventoryContainer.detectAndSendChanges();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS,
                    0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) {
            ItemStack placed = held.copy();
            placed.setCount(1);
            pedestal.setInventorySlotContents(0, placed);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
                if (held.isEmpty()) player.setHeldItem(hand, ItemStack.EMPTY);
            }
            player.inventory.markDirty();
            player.inventoryContainer.detectAndSendChanges();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS,
                    0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.6F);
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TilePedestal || te instanceof TileAlchemyFurnace) {
            return Container.calcRedstoneFromInventory((IInventory) te);
        }
        if (te instanceof TileWandPedestal) {
            TileWandPedestal pedestal = (TileWandPedestal) te;
            ItemStack stack = pedestal.getStackInSlot(0);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                float fill = (float) wand.getAllVis(stack).visSize() / (float) ItemWandCasting.getMaxVis(stack) * 6.0F;
                return MathHelper.floor(fill * 14.0F) + 1;
            }
        }
        return 0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = state.getValue(TYPE);
        if (meta == 0) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileAlchemyFurnace && ((TileAlchemyFurnace) te).isBurning()) {
                return 12;
            }
        } else if (meta == 2) {
            return 10;
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 14));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 14));
    }
}
