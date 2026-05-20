package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
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
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fluids.FluidUtil;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.*;

import javax.annotation.Nullable;

public class BlockMetalDevice extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 14);
    private static final AxisAlignedBB GRATE_AABB = new AxisAlignedBB(0.0D, 0.8125D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB ARCANE_LAMP_AABB = new AxisAlignedBB(0.25D, 0.125D, 0.25D, 0.75D, 0.875D, 0.75D);

    private int delay = 0;

    public BlockMetalDevice() {
        super(Material.IRON);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(TYPE) == 1 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }
    @Override public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileCrucible();
        if (meta == 1) return new TileAlembic();
        if (meta == 2) return new TileMagicWorkbenchCharger();
        if (meta == 5 || meta == 6) return new TileGrate();
        if (meta == 7) return new TileArcaneLamp();
        if (meta == 8) return new TileArcaneLampGrowth();
        if (meta == 10) return new TileThaumatorium();
        if (meta == 11) return new TileThaumatoriumTop();
        if (meta == 12) return new TileBrainbox();
        if (meta == 13) return new TileArcaneLampFertility();
        if (meta == 14) return new TileVisRelay();
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
        list.add(new ItemStack(this, 1, 2)); // magic workbench charger
        list.add(new ItemStack(this, 1, 5)); // grate
        list.add(new ItemStack(this, 1, 7)); // lamp
        list.add(new ItemStack(this, 1, 8)); // growth lamp
        list.add(new ItemStack(this, 1, 10)); // thaumatorium
        list.add(new ItemStack(this, 1, 11)); // thaumatorium top
        list.add(new ItemStack(this, 1, 12)); // brainbox
        list.add(new ItemStack(this, 1, 13)); // fertility lamp
        list.add(new ItemStack(this, 1, 14)); // vis relay
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = getMetaFromState(state);
        return meta == 6 ? 5 : meta;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote || state.getValue(TYPE) != 0) return;

        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileCrucible)) return;

        TileCrucible tile = (TileCrucible) te;
        if (entityIn instanceof EntityItem && !(entityIn instanceof EntitySpecialItem) && tile.canProcessItems()) {
            tile.attemptSmelt((EntityItem) entityIn);
            return;
        }

        ++this.delay;
        if (this.delay < 10) return;
        this.delay = 0;

        if (entityIn instanceof EntityLivingBase && tile.canProcessItems()) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 0.4F, 2.0F + worldIn.rand.nextFloat() * 0.4F);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(TYPE) == 0
                && FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)) {
            return true;
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileAlembic) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                return ((TileAlembic) tileEntity).onWandRightClick(worldIn, held, playerIn,
                        pos.getX(), pos.getY(), pos.getZ(), facing.getIndex(), state.getValue(TYPE)) >= 0;
            }
        }
        if (state.getValue(TYPE) == 5) {
            toggleGrate(worldIn, pos, state, 6, playerIn);
            return true;
        }
        if (state.getValue(TYPE) == 6) {
            toggleGrate(worldIn, pos, state, 5, playerIn);
            return true;
        }
        if (state.getValue(TYPE) == 10) {
            TileEntity te = tileEntity;
            if (te instanceof TileThaumatorium) {
                if (!worldIn.isRemote) {
                    playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_THAUMATORIUM, worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
                return true;
            }
        }
        if (state.getValue(TYPE) == 11) {
            TileEntity te = worldIn.getTileEntity(pos.down());
            if (te instanceof TileThaumatorium) {
                if (!worldIn.isRemote) {
                    playerIn.openGui(Thaumcraft.instance, CommonProxy.GUI_THAUMATORIUM, worldIn, pos.getX(), pos.getY() - 1, pos.getZ());
                }
                return true;
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileCrucible) {
            ((TileCrucible) te).spillRemnants();
        } else if (te instanceof TileArcaneLamp) {
            ((TileArcaneLamp) te).removeLights();
        } else if (te instanceof IInventory) {
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isRemote || state.getValue(TYPE) != 12) return;
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileBrainbox)) return;
        TileBrainbox brainbox = (TileBrainbox) te;
        EnumFacing target = findAdjacentThaumatorium(worldIn, pos);
        if (target == null) {
            target = placer == null ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite();
        }
        brainbox.facing = target;
        brainbox.markDirty();
        worldIn.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (worldIn.isRemote) return;
        boolean powered = worldIn.isBlockPowered(pos);
        onPoweredBlockChange(worldIn, pos, powered);
        if (state.getValue(TYPE) == 7) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileArcaneLamp) {
                TileArcaneLamp lamp = (TileArcaneLamp) te;
                if (worldIn.isAirBlock(pos.offset(lamp.facing))) {
                    worldIn.destroyBlock(pos, true);
                }
            }
            return;
        }
        if (state.getValue(TYPE) == 8) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileArcaneLampGrowth) {
                TileArcaneLampGrowth lamp = (TileArcaneLampGrowth) te;
                if (worldIn.isAirBlock(pos.offset(lamp.facing))) {
                    worldIn.destroyBlock(pos, true);
                }
            }
            return;
        }
        if (state.getValue(TYPE) == 13) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileArcaneLampFertility) {
                TileArcaneLampFertility lamp = (TileArcaneLampFertility) te;
                if (worldIn.isAirBlock(pos.offset(lamp.facing))) {
                    worldIn.destroyBlock(pos, true);
                }
            }
            return;
        }
        if (state.getValue(TYPE) != 12) return;
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileBrainbox)) return;
        EnumFacing target = findAdjacentThaumatorium(worldIn, pos);
        if (target == null || ((TileBrainbox) te).facing == target) return;
        ((TileBrainbox) te).facing = target;
        te.markDirty();
        worldIn.notifyBlockUpdate(pos, state, state, 3);
    }

    private void toggleGrate(World worldIn, BlockPos pos, IBlockState state, int targetMeta, @Nullable EntityPlayer player) {
        if (worldIn.isRemote) {
            return;
        }
        worldIn.setBlockState(pos, state.withProperty(TYPE, targetMeta), 2);
        worldIn.playEvent(player, 1003, pos, 0);
    }

    private void onPoweredBlockChange(World worldIn, BlockPos pos, boolean powered) {
        IBlockState state = worldIn.getBlockState(pos);
        int meta = state.getValue(TYPE);
        if (meta == 5 && powered) {
            worldIn.setBlockState(pos, state.withProperty(TYPE, 6), 2);
            worldIn.playEvent(null, 1003, pos, 0);
        } else if (meta == 6 && !powered) {
            worldIn.setBlockState(pos, state.withProperty(TYPE, 5), 2);
            worldIn.playEvent(null, 1003, pos, 0);
        }
    }

    private EnumFacing findAdjacentThaumatorium(World worldIn, BlockPos pos) {
        for (EnumFacing face : EnumFacing.values()) {
            IBlockState adjacent = worldIn.getBlockState(pos.offset(face));
            if (adjacent.getBlock() == this && (adjacent.getValue(TYPE) == 10 || adjacent.getValue(TYPE) == 11)) {
                return face;
            }
        }
        return null;
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int meta = getMetaFromState(state);
        if (meta == 7 || meta == 8 || meta == 13) {
            return ARCANE_LAMP_AABB;
        }
        return (meta == 5 || meta == 6) ? GRATE_AABB : FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      java.util.List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        int meta = getMetaFromState(state);
        if (meta == 5) {
            if (!(entityIn instanceof EntityItem)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, GRATE_AABB);
            }
            return;
        }
        if (meta == 6) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, GRATE_AABB);
            return;
        }
        if (meta == 7 || meta == 8 || meta == 13) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, ARCANE_LAMP_AABB);
            return;
        }
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = state.getValue(TYPE);
        if (meta == 7) {
            return 15;
        }
        if (meta == 8) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileArcaneLampGrowth) {
                return ((TileArcaneLampGrowth) te).charges > 0 ? 15 : 8;
            }
        }
        if (meta == 13) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileArcaneLampFertility) {
                return ((TileArcaneLampFertility) te).charges > 0 ? 15 : 8;
            }
        }
        if (meta == 14) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileVisRelay) {
                return VisNetHandler.isNodeValid(((TileVisRelay) te).getParent()) ? 10 : 2;
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 14));
    }
}
