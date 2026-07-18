package thaumcraft.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.Block;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Explosion;
import net.minecraft.init.SoundEvents;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockWoodenDevice extends BlockContainer {

    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 8);
    private static final AxisAlignedBB BELLOWS_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 1.0D, 0.9D);
    private static final AxisAlignedBB PRESSURE_PLATE_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);
    private static final AxisAlignedBB PRESSED_PLATE_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.03125D, 0.9375D);
    private static final AxisAlignedBB BANNER_STANDING_AABB = new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.66D, 2.0D, 0.66D);
    private static final AxisAlignedBB BANNER_WALL_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 0.25D);
    private static final AxisAlignedBB BANNER_WALL_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 2.0D, 1.0D);
    private static final AxisAlignedBB BANNER_WALL_WEST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);
    private static final AxisAlignedBB BANNER_WALL_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 2.0D, 1.0D);

    public BlockWoodenDevice() {
        super(Material.WOOD);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("axe", 0);
        this.setTickRandomly(true);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        int meta = state.getValue(TYPE);
        return meta == 0 || meta == 4 || meta == 5 || meta == 8
                ? EnumBlockRenderType.INVISIBLE
                : EnumBlockRenderType.MODEL;
    }
    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = state.getValue(TYPE);
        return meta != 6 && meta != 7;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileBellows();
        if (meta == 1) return new TileSensor();
        if (meta == 2 || meta == 3) return new TileArcanePressurePlate();
        if (meta == 4) return new TileArcaneBoreBase();
        if (meta == 5) return new TileArcaneBore();
        if (meta == 8) return new TileBanner();
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, getMetaFromState(state));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileOwned && placer instanceof EntityPlayer) {
            ((TileOwned) tile).owner = placer.getName();
            tile.markDirty();
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0)); // bellows
        list.add(new ItemStack(this, 1, 1)); // sensor
        list.add(new ItemStack(this, 1, 2)); // pressure plate
        list.add(new ItemStack(this, 1, 3)); // pressure plate
        list.add(new ItemStack(this, 1, 4)); // bore base
        list.add(new ItemStack(this, 1, 5)); // bore
        list.add(new ItemStack(this, 1, 6)); // greatwood plank
        list.add(new ItemStack(this, 1, 7)); // silverwood plank
        list.add(new ItemStack(this, 1, 8)); // banner
        for (int color = 0; color < 16; color++) {
            ItemStack banner = new ItemStack(this, 1, 8);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("color", (byte) color);
            banner.setTagCompound(tag);
            list.add(banner);
        }
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        int meta = state.getValue(TYPE);
        if (meta == 4 || meta == 6 || meta == 7) {
            return true;
        }
        return super.isSideSolid(state, world, pos, side);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int meta = getMetaFromState(state);
        if (meta == 0) {
            return BELLOWS_AABB;
        }
        if (meta == 2) {
            return PRESSURE_PLATE_AABB;
        }
        if (meta == 3) {
            return PRESSED_PLATE_AABB;
        }
        if (meta == 5) {
            TileEntity tile = source.getTileEntity(pos);
            if (tile instanceof TileArcaneBore) {
                return getBoreBounds(((TileArcaneBore) tile).orientation);
            }
        }
        if (meta == 8) {
            TileEntity tile = source.getTileEntity(pos);
            if (tile instanceof TileBanner) {
                return getBannerBounds((TileBanner) tile);
            }
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        int meta = getMetaFromState(state);
        if (meta == 0) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BELLOWS_AABB);
            return;
        }
        if (meta == 2 || meta == 3 || meta == 8) {
            return;
        }
        if (meta == 5) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileArcaneBore) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoreBounds(((TileArcaneBore) tile).orientation));
                return;
            }
        }
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = getMetaFromState(state);
        return meta == 3 ? 2 : meta;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        int meta = state.getValue(TYPE);
        return (meta == 2 || meta == 3) && Config.wardedStone
                ? Items.AIR
                : super.getItemDropped(state, rand, fortune);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = state.getValue(TYPE);
        if (meta == 2 || meta == 3) {
            return Config.wardedStone ? -1.0F : 2.0F;
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = world.getBlockState(pos).getValue(TYPE);
        if (meta == 2 || meta == 3) {
            return 999.0F;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        int meta = state.getValue(TYPE);
        return meta != 2 && meta != 3 && super.canEntityDestroy(state, world, pos, entity);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        int meta = world.getBlockState(pos).getValue(TYPE);
        if (meta != 2 && meta != 3) {
            super.onBlockExploded(world, pos, explosion);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSensor) {
            if (!worldIn.isRemote) {
                TileSensor sensor = (TileSensor) te;
                sensor.changePitch();
                sensor.triggerNote(worldIn, pos.getX(), pos.getY(), pos.getZ(), true);
            }
            return true;
        }
        if (te instanceof TileArcanePressurePlate) {
            if (!worldIn.isRemote && canEditPressurePlate((TileArcanePressurePlate) te, playerIn)) {
                TileArcanePressurePlate plate = (TileArcanePressurePlate) te;
                plate.setting = (byte) ((plate.setting + 1) % 3);
                worldIn.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.1F, 0.9F);
                worldIn.notifyBlockUpdate(pos, state, state, 3);
                plate.markDirty();
            }
            return true;
        }
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
        if (te instanceof TileBanner && state.getValue(TYPE) == 8) {
            TileBanner banner = (TileBanner) te;
            if (banner.getColor() >= 0) {
                ItemStack held = playerIn.getHeldItem(hand);
                boolean sneaking = playerIn.isSneaking();
                boolean holdingEssence = !held.isEmpty() && held.getItem() instanceof ItemEssence
                        && held.getItem() instanceof IEssentiaContainerItem;

                if (sneaking || holdingEssence) {
                    if (!worldIn.isRemote) {
                        if (sneaking) {
                            banner.setAspect(null);
                        } else {
                            AspectList aspects = ((IEssentiaContainerItem) held.getItem()).getAspects(held);
                            if (aspects != null && aspects.getAspects().length > 0) {
                                Aspect aspect = aspects.getAspects()[0];
                                if (aspect != null) {
                                    banner.setAspect(aspect);
                                    held.shrink(1);
                                }
                            }
                        }
                        worldIn.notifyBlockUpdate(pos, state, state, 3);
                        banner.markDirty();
                        worldIn.playSound(null, pos, SoundType.CLOTH.getStepSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (state.getValue(TYPE) == 8) {
            ArrayList<ItemStack> drops = new ArrayList<>();
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileBanner) {
                TileBanner banner = (TileBanner) te;
                ItemStack drop = new ItemStack(this, 1, 8);
                if (banner.getColor() >= 0 || banner.getAspect() != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    if (banner.getAspect() != null) {
                        tag.setString("aspect", banner.getAspect().getTag());
                    }
                    tag.setByte("color", banner.getColor());
                    drop.setTagCompound(tag);
                }
                drops.add(drop);
            }
            return drops;
        }
        return super.getDrops(world, pos, state, fortune);
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileSensor) {
            ((TileSensor) te).updateTone();
            te.markDirty();
        }
        super.onBlockAdded(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        int meta = state.getValue(TYPE);
        if (meta == 1) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileSensor) {
                ((TileSensor) te).updateTone();
            }
        } else if (meta == 5) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileArcaneBore) {
                TileArcaneBore bore = (TileArcaneBore) te;
                EnumFacing base = bore.baseOrientation;
                if (base != null) {
                    BlockPos supportPos = pos.offset(base.getOpposite());
                    IBlockState supportState = worldIn.getBlockState(supportPos);
                    if (supportState.getBlock() != this || !supportState.isSideSolid(worldIn, supportPos, base)) {
                        worldIn.destroyBlock(pos, true);
                    }
                }
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public int tickRate(World worldIn) {
        return 20;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getValue(TYPE) == 3) {
            setStateIfMobInteractsWithPlate(worldIn, pos);
        }
        super.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote && state.getValue(TYPE) == 2) {
            setStateIfMobInteractsWithPlate(worldIn, pos);
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @javax.annotation.Nullable EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 0) {
            return false;
        }
        if (meta == 1 || meta == 2 || meta == 3 || meta == 4 || meta == 5) {
            return true;
        }
        return super.canConnectRedstone(state, world, pos, side);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 1) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileSensor) {
                return ((TileSensor) tile).redstoneSignal > 0 ? 15 : 0;
            }
        } else if (meta == 2) {
            return 0;
        } else if (meta == 3) {
            return side == EnumFacing.UP ? 15 : 0;
        }
        return super.getWeakPower(state, world, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        int meta = getMetaFromState(state);
        if (meta == 1) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileSensor) {
                return ((TileSensor) tile).redstoneSignal > 0 ? 15 : 0;
            }
        } else if (meta == 3) {
            return 15;
        }
        return super.getStrongPower(state, world, pos, side);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        boolean silentSensorEvent = id == -1 || id == 255;
        if (silentSensorEvent || id >= 0 && id <= 4) {
            float pitch = (float) Math.pow(2.0D, (param - 12) / 12.0D);
            if (!silentSensorEvent) {
                switch (id) {
                    case 1:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 2:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_SNARE, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 3:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_HAT, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    case 4:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_BASS, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                    default:
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 3.0F, pitch);
                        break;
                }
            }
            if (worldIn.isRemote && silentSensorEvent) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof TileSensor) {
                    ((TileSensor) tile).redstoneSignal = 10;
                    worldIn.markBlockRangeForRenderUpdate(pos, pos);
                }
            }
            float note = (float) param / 24.0F;
            float red = MathHelper.sin((note + 0.0F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F;
            float green = MathHelper.sin((note + 0.33333334F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F;
            float blue = MathHelper.sin((note + 0.6666667F) * ((float) Math.PI * 2F)) * 0.65F + 0.35F;
            Thaumcraft.proxy.drawGenericParticles(worldIn,
                    pos.getX() + 0.5D,
                    pos.getY() + 1.2D,
                    pos.getZ() + 0.5D,
                    0.0D, 0.2D, 0.0D,
                    red, green, blue, 0.9F,
                    false, 64, 1, 1, 6, 0, 0.75F, 1);
            return true;
        }
        return super.eventReceived(state, worldIn, pos, id, param);
    }

    private boolean canEditPressurePlate(TileArcanePressurePlate plate, EntityPlayer player) {
        if (player == null || plate == null) {
            return false;
        }
        String username = player.getName();
        return username.equals(plate.owner) || plate.accessList.contains("1" + username);
    }

    private void setStateIfMobInteractsWithPlate(World world, BlockPos pos) {
        IBlockState current = world.getBlockState(pos);
        int meta = current.getValue(TYPE);
        boolean pressed = meta == 3;
        boolean shouldPress = false;

        TileEntity tile = world.getTileEntity(pos);
        byte setting = 0;
        String owner = "";
        ArrayList<String> accessList = new ArrayList<>();
        if (tile instanceof TileArcanePressurePlate) {
            TileArcanePressurePlate plate = (TileArcanePressurePlate) tile;
            setting = plate.setting;
            owner = plate.owner;
            accessList = plate.accessList;
        }

        float inset = 0.125F;
        AxisAlignedBB box = new AxisAlignedBB(
                pos.getX() + inset,
                pos.getY(),
                pos.getZ() + inset,
                pos.getX() + 1.0F - inset,
                pos.getY() + 0.25D,
                pos.getZ() + 1.0F - inset);

        List<? extends Entity> entities = null;
        if (setting == 0) {
            entities = world.getEntitiesWithinAABBExcludingEntity(null, box);
        } else if (setting == 1) {
            entities = world.getEntitiesWithinAABB(Entity.class, box);
        } else if (setting == 2) {
            entities = world.getEntitiesWithinAABB(EntityPlayer.class, box);
        }

        if (entities != null && !entities.isEmpty()) {
            for (Entity entity : entities) {
                if (entity.doesEntityNotTriggerPressurePlate()) {
                    continue;
                }
                if (setting == 1 && entity instanceof EntityPlayer) {
                    String name = ((EntityPlayer) entity).getName();
                    if (name.equals(owner) || accessList.contains("0" + name) || accessList.contains("1" + name)) {
                        continue;
                    }
                }
                if (setting == 2 && entity instanceof EntityPlayer) {
                    String name = ((EntityPlayer) entity).getName();
                    if (!name.equals(owner) && !accessList.contains("0" + name) && !accessList.contains("1" + name)) {
                        continue;
                    }
                }
                shouldPress = true;
                break;
            }
        }

        if (shouldPress && !pressed) {
            world.setBlockState(pos, current.withProperty(TYPE, 3), 2);
            world.notifyNeighborsOfStateChange(pos, this, false);
            world.notifyNeighborsOfStateChange(pos.down(), this, false);
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.6F);
        }
        if (!shouldPress && pressed) {
            world.setBlockState(pos, current.withProperty(TYPE, 2), 2);
            world.notifyNeighborsOfStateChange(pos, this, false);
            world.notifyNeighborsOfStateChange(pos.down(), this, false);
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.5F);
        }
        if (shouldPress) {
            world.scheduleUpdate(pos, this, tickRate(world));
        }
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

    private static AxisAlignedBB getBoreBounds(EnumFacing facing) {
        EnumFacing dir = facing == null ? EnumFacing.NORTH : facing;
        return new AxisAlignedBB(
                dir.getXOffset() < 0 ? -1.0D : 0.0D,
                dir.getYOffset() < 0 ? -1.0D : 0.0D,
                dir.getZOffset() < 0 ? -1.0D : 0.0D,
                dir.getXOffset() > 0 ? 2.0D : 1.0D,
                dir.getYOffset() > 0 ? 2.0D : 1.0D,
                dir.getZOffset() > 0 ? 2.0D : 1.0D
        );
    }

    private static AxisAlignedBB getBannerBounds(TileBanner banner) {
        if (!banner.getWall()) {
            return BANNER_STANDING_AABB;
        }
        switch (banner.getFacing()) {
            case 0:
                return BANNER_WALL_NORTH_AABB;
            case 8:
                return BANNER_WALL_SOUTH_AABB;
            case 12:
                return BANNER_WALL_EAST_AABB;
            case 4:
                return BANNER_WALL_WEST_AABB;
            default:
                return FULL_BLOCK_AABB;
        }
    }
}
