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
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileJarBrain;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;
import thaumcraft.common.tiles.TileJarNode;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockJar
extends BlockContainer {

    protected static final AxisAlignedBB JAR_AABB = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
    protected static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 3);

    public BlockJar() {
        super(Material.GLASS);
        this.setHardness(0.3f);
        this.setSoundType(new CustomStepSound("jar", 1.0f, 1.0f));
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setLightLevel(0.66f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 0) {
            return new TileJarFillable();
        }
        if (meta == 1) {
            return new TileJarBrain();
        }
        if (meta == 2) {
            return new TileJarNode();
        }
        if (meta == 3) {
            return new TileJarFillableVoid();
        }
        return new TileJarFillable();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) return new TileJarFillable();
        if (meta == 1) return new TileJarBrain();
        if (meta == 2) return new TileJarNode();
        if (meta == 3) return new TileJarFillableVoid();
        return new TileJarFillable();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 3));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 0 || meta == 3) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileJarFillable) {
                TileJarFillable jar = (TileJarFillable) te;
                ItemStack drop = new ItemStack(this, 1, 0);
                if (te instanceof TileJarFillableVoid) {
                    drop.setItemDamage(3);
                }
                if (jar.amount <= 0 && jar.aspectFilter == null) {
                    drop = new ItemStack(this, 1, meta == 3 ? 3 : 0);
                }
                if (jar.amount > 0) {
                    if (drop.getItem() instanceof IEssentiaContainerItem) {
                        ((IEssentiaContainerItem) drop.getItem()).setAspects(drop, new AspectList().add(jar.aspect, jar.amount));
                    }
                }
                if (jar.aspectFilter != null) {
                    if (!drop.hasTagCompound()) {
                        drop.setTagCompound(new NBTTagCompound());
                    }
                    drop.getTagCompound().setString("AspectFilter", jar.aspectFilter.getTag());
                }
                drops.add(drop);
                return;
            }
        }
        if (meta == 2) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileJarNode && ((TileJarNode) te).drop && ((TileJarNode) te).getAspects() != null) {
                ItemStack drop = new ItemStack(this, 1, 2);
                if (drop.getItem() instanceof IEssentiaContainerItem) {
                    ((IEssentiaContainerItem) drop.getItem()).setAspects(drop, ((TileJarNode) te).getAspects().copy());
                }
                if (drop.getItem() instanceof BlockJarItem) {
                    TileJarNode node = (TileJarNode) te;
                    ((BlockJarItem) drop.getItem()).setNodeAttributes(
                            drop,
                            node.getNodeType(),
                            node.getNodeModifier(),
                            node.getId());
                }
                drops.add(drop);
            }
            return;
        }
        drops.add(new ItemStack(this, 1, meta));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 1 && !worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileJarBrain) {
                TileJarBrain brain = (TileJarBrain) te;
                for (int xp = brain.xp; xp > 0; ) {
                    int split = EntityXPOrb.getXPSplit(xp);
                    xp -= split;
                    worldIn.spawnEntity(new EntityXPOrb(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, split));
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileJarNode && stack != null && !stack.isEmpty()) {
            TileJarNode node = (TileJarNode) tile;
            restoreNodeData(stack, node);
            node.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return;
        }
        if (tile instanceof TileJarFillable) {
            TileJarFillable jar = (TileJarFillable) tile;
            if (l == 0) jar.facing = 2;
            if (l == 1) jar.facing = 5;
            if (l == 2) jar.facing = 3;
            if (l == 3) jar.facing = 4;
            restoreFillableData(stack, jar);
            jar.markDirty();
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    static void restoreFillableData(ItemStack stack, TileJarFillable jar) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof IEssentiaContainerItem) {
            AspectList aspects = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
            if (aspects != null && aspects.size() == 1) {
                Aspect aspect = aspects.getAspects()[0];
                if (aspect != null) {
                    jar.aspect = aspect;
                    jar.amount = aspects.getAmount(aspect);
                }
            }
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter")) {
            Aspect filter = Aspect.getAspect(stack.getTagCompound().getString("AspectFilter"));
            if (filter != null) {
                jar.aspectFilter = filter;
            }
        }
    }

    static void restoreNodeData(ItemStack stack, TileJarNode node) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof IEssentiaContainerItem) {
            AspectList aspects = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
            if (aspects != null && aspects.size() > 0) {
                node.setAspects(aspects.copy());
            }
        }
        if (stack.getItem() instanceof BlockJarItem) {
            BlockJarItem item = (BlockJarItem) stack.getItem();
            NodeType type = item.getNodeType(stack);
            if (type != null) {
                node.setNodeType(type);
            }
            node.setNodeModifier(item.getNodeModifier(stack));
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("nodeid")) {
                node.setId(item.getNodeId(stack));
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (te instanceof TileJarBrain) {
            TileJarBrain brain = (TileJarBrain) te;
            brain.eatDelay = 40;
            if (!worldIn.isRemote) {
                int toDrop = worldIn.rand.nextInt(Math.min(brain.xp + 1, 64));
                if (toDrop > 0) {
                    brain.xp -= toDrop;
                    for (int xp = toDrop; xp > 0; ) {
                        int split = EntityXPOrb.getXPSplit(xp);
                        xp -= split;
                        worldIn.spawnEntity(new EntityXPOrb(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, split));
                    }
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                    brain.markDirty();
                }
            } else {
                worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, TCSounds.JAR, SoundCategory.BLOCKS, 0.2f, 1.0f, false);
            }
            return true;
        }

        if (te instanceof TileJarFillable) {
            TileJarFillable jar = (TileJarFillable) te;

            // Sneak + click on filter side = remove filter
            if (playerIn.isSneaking() && jar.aspectFilter != null && facing.getIndex() == jar.facing) {
                jar.aspectFilter = null;
                if (worldIn.isRemote) {
                    worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                } else {
                    EnumFacing fd = facing;
                    worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5 + fd.getXOffset() / 3.0f, pos.getY() + 0.5, pos.getZ() + 0.5 + fd.getZOffset() / 3.0f, new ItemStack(net.minecraft.init.Items.PAPER)));
                }
                return true;
            }

            // Sneak + empty hand = empty jar
            if (playerIn.isSneaking() && heldItem.isEmpty()) {
                jar.amount = 0;
                if (jar.aspectFilter == null) {
                    jar.aspect = null;
                }
                if (worldIn.isRemote) {
                    worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, TCSounds.JAR, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
                    worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, net.minecraft.init.SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.BLOCKS, 0.5f, 1.0f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.3f, false);
                }
                return true;
            }

            // Apply label (paper with aspect) as filter
            if (!heldItem.isEmpty() && jar.aspectFilter == null && heldItem.getItem() == net.minecraft.init.Items.PAPER) {
                if (heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey("AspectFilter")) {
                    String tag = heldItem.getTagCompound().getString("AspectFilter");
                    Aspect filter = Aspect.getAspect(tag);
                    if (filter != null) {
                        jar.aspectFilter = filter;
                        jar.aspect = filter;
                        heldItem.shrink(1);
                        if (worldIn.isRemote) {
                            worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
                        }
                        return true;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return JAR_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_AABB);
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileJarBrain) {
            return 2.0f;
        }
        return 0.0f;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 2) {
            return 11;
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileJarBrain && ((TileJarBrain) tile).xp >= ((TileJarBrain) tile).xpMax) {
            double xx = pos.getX() + 0.3 + rand.nextFloat() * 0.4;
            double yy = pos.getY() + 0.9;
            double zz = pos.getZ() + 0.3 + rand.nextFloat() * 0.4;
            Thaumcraft.proxy.drawGenericParticles(worldIn,
                    xx, yy, zz,
                    0.0D, 0.004D, 0.0D,
                    0.85F, 0.15F, 0.95F, 0.8F,
                    false, 128, 8, -1, 8, 0, 0.45F, 1);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileJarBrain) {
            TileJarBrain brain = (TileJarBrain) tile;
            float r = (float) brain.xp / (float) brain.xpMax;
            return MathHelper.floor(r * 14.0f) + (brain.xp > 0 ? 1 : 0);
        }
        if (tile instanceof TileJarFillable) {
            TileJarFillable jar = (TileJarFillable) tile;
            float r = (float) jar.amount / (float) jar.maxAmount;
            return MathHelper.floor(r * 14.0f) + (jar.amount > 0 ? 1 : 0);
        }
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 3));
    }
}
