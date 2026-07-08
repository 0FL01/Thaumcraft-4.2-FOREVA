package thaumcraft.common.blocks;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileNitor;
import thaumcraft.common.tiles.TileWardingStoneFence;

public class BlockAiry extends BlockContainer {

    public static final String[] airyTypes = {"node", "nitor", "leavesFiller1", "leavesFiller2", "wardingFence", "energizedNode", null, null, null, null, "fire", "eerie", "barrier"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 12);
    private static final AxisAlignedBB AIRY_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public BlockAiry() {
        super(Config.airyMaterial);
        this.setHardness(2.0f);
        this.setResistance(200.0f);
        this.setSoundType(SoundType.CLOTH);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    public boolean isAir(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 2 || meta == 3 || meta == 10 || meta == 11;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        int meta = this.getMetaFromState(worldIn.getBlockState(pos));
        return meta == 2 || meta == 3 || meta == 4 || meta == 10 || meta == 11;
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
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 4 || meta == 5 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 0 || meta == 5 || meta == 10 || meta == 11) return 8;
        if (meta == 1) return 15;
        if (meta == 2 || meta == 3) return 15;
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 10 || meta == 11) return 100.0f;
        if (meta == 12) return -1.0f;
        return 2.0f;
    }

    public float getExplosionResistance(World world, BlockPos pos, Entity exploder) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 0 || meta == 5) return 200.0f;
        if (meta == 10 || meta == 11) return 50.0f;
        if (meta == 12) return Float.MAX_VALUE;
        return super.getExplosionResistance(exploder);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 1 || meta == 4 || meta == 5;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta == 0) return new TileNode();
        if (meta == 1) return new TileNitor();
        if (meta == 4) return new TileWardingStoneFence();
        if (meta == 5) return new TileNodeEnergized();
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return createNewTileEntity(world, this.getMetaFromState(state));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote && stack.getMetadata() == 0 && placer instanceof EntityPlayer) {
            ThaumcraftWorldGenerator.createRandomNodeAt(worldIn, pos, worldIn.rand, false, false, false);
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        int meta = this.getMetaFromState(state);
        if (meta == 10) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float) (1 + worldIn.rand.nextInt(2)));
            entityIn.motionX *= 0.8D;
            entityIn.motionZ *= 0.8D;
            if (!worldIn.isRemote && worldIn.rand.nextInt(100) == 0) {
                worldIn.setBlockToAir(pos);
            }
        } else if (meta == 11 && !(entityIn instanceof IEldritchMob)) {
            if (worldIn.rand.nextInt(100) == 0) {
                entityIn.attackEntityFrom(DamageSource.MAGIC, 1.0F);
            }
            entityIn.motionX *= 0.66D;
            entityIn.motionZ *= 0.66D;
            if (entityIn instanceof EntityPlayer) {
                ((EntityPlayer) entityIn).addExhaustion(0.05F);
            }
            if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 1, true, false));
            }
        }
    }

    public static void explodify(World world, int x, int y, int z) {
        if (world == null || world.isRemote) {
            return;
        }
        BlockPos origin = new BlockPos(x, y, z);
        world.setBlockToAir(origin);
        world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 3.0F, false);
        for (int a = 0; a < 50; ++a) {
            int xx = x + world.rand.nextInt(8) - world.rand.nextInt(8);
            int yy = y + world.rand.nextInt(8) - world.rand.nextInt(8);
            int zz = z + world.rand.nextInt(8) - world.rand.nextInt(8);
            BlockPos randomPos = new BlockPos(xx, yy, zz);
            if (!world.isAirBlock(randomPos)) {
                continue;
            }
            if (yy < y) {
                world.setBlockState(randomPos, ConfigBlocks.blockFluxGoo.getStateFromMeta(8), 3);
            } else {
                world.setBlockState(randomPos, ConfigBlocks.blockFluxGas.getStateFromMeta(8), 3);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        int meta = this.getMetaFromState(state);
        if (!worldIn.isRemote && (meta == 10 || meta == 11)) {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int meta = this.getMetaFromState(state);
        if (meta != 10 && meta != 11) {
            return;
        }

        float h = rand.nextFloat() * 0.33F;
        float x = (float) pos.getX() + rand.nextFloat();
        float y = (float) pos.getY() + 0.1515F + h / 2.0F;
        float z = (float) pos.getZ() + rand.nextFloat();
        float scale = 0.33F + h;
        if (meta == 10) {
            Thaumcraft.proxy.spark(x, y, z, scale, 0.65F + rand.nextFloat() * 0.1F, 1.0F, 1.0F, 0.8F);
        } else {
            Thaumcraft.proxy.spark(x, y, z, scale, 0.3F - rand.nextFloat() * 0.1F, 0.0F,
                    0.5F + rand.nextFloat() * 0.2F, 1.0F);
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        return meta == 2 || meta == 3 || meta == 4;
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        return meta == 2 || meta == 3;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        return meta == 3 || meta == 4 || meta == 10 || meta == 11 || meta == 12 ? ZERO_AABB : AIRY_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 0 || meta == 2 || meta == 3 || meta == 4 || meta == 5 || meta == 10 || meta == 11 || meta == 12) {
            return ZERO_AABB;
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        int meta = this.getMetaFromState(state);
        if (meta == 4 && entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
            if (isActiveWardingStoneSupport(world, pos)) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
            }
            return;
        }
        if (meta == 12) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
            return;
        }
        super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
    }

    private static boolean isActiveWardingStoneSupport(World world, BlockPos pos) {
        for (int offset = 1; offset <= 2; offset++) {
            BlockPos basePos = pos.down(offset);
            IBlockState base = world.getBlockState(basePos);
            if (base.getBlock() == ConfigBlocks.blockCosmeticSolid
                    && base.getValue(BlockCosmeticSolid.TYPE) == BlockCosmeticSolid.TYPE_WARDING) {
                return !world.isBlockPowered(basePos);
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        return meta == 4 || meta == 12 ? super.getSelectedBoundingBox(state, world, pos) : ZERO_AABB.offset(pos);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing side) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null ? ConfigItems.itemResource : Items.AIR;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null
                ? new ItemStack(ConfigItems.itemResource, 1, 1)
                : ItemStack.EMPTY;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (this.getMetaFromState(state) == 0 && !worldIn.isRemote && te instanceof INode && ConfigItems.itemWispEssence != null) {
            AspectList aspects = ((INode) te).getAspects();
            if (aspects != null && aspects.size() > 0) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect == null || aspects.getAmount(aspect) < 5) {
                        continue;
                    }
                    for (int a = 0; a < aspects.getAmount(aspect) / 10; ++a) {
                        ItemStack itemstack = new ItemStack(ConfigItems.itemWispEssence);
                        ((ItemWispEssence) itemstack.getItem()).setAspects(itemstack, new AspectList().add(aspect, 2));
                        spawnAsEntity(worldIn, pos, itemstack);
                    }
                }
            }
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 12));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ, int meta, net.minecraft.entity.EntityLivingBase placer, net.minecraft.util.EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 12));
    }
}
