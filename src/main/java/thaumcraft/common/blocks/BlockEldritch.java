package thaumcraft.common.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEldritchObject;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileEldritchAltar;
import thaumcraft.common.tiles.TileEldritchCap;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;
import thaumcraft.common.tiles.TileEldritchLock;
import thaumcraft.common.tiles.TileEldritchObelisk;
import thaumcraft.common.tiles.TileEldritchTrap;

public class BlockEldritch extends Block {
    public static final String[] types = {"altar", "obelisk", "obelisk_middle", "capstone", "glowing_crust",
            "glyphed", "deco", "doorway", "lock", "crab_spawner", "runed"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 10);

    public BlockEldritch() {
        super(Material.ROCK);
        this.setHardness(50.0f);
        this.setResistance(20000.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 2);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
    }
    
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 4));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 10));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 10));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 1 || meta == 3 || meta == 8 || meta == 9 || meta == 10;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (this.getMetaFromState(state)) {
            case 0:
                return new TileEldritchAltar();
            case 1:
                return new TileEldritchObelisk();
            case 3:
                return new TileEldritchCap();
            case 8:
                return new TileEldritchLock();
            case 9:
                return new TileEldritchCrabSpawner();
            case 10:
                return new TileEldritchTrap();
            default:
                return null;
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 4) {
            return Item.getItemFromBlock(this);
        }
        if (meta == 5) {
            return ConfigItems.itemResource;
        }
        return Items.AIR;
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == 2) {
            return 1;
        }
        return meta;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (this.getMetaFromState(state) == 5) {
            drops.add(new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_KNOWLEDGE_FRAGMENT));
            return;
        }
        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        int meta = this.getMetaFromState(state);
        if (meta == 5 || meta == 10) {
            return MathHelper.getInt(RANDOM, 1, 4);
        }
        if (meta == 9) {
            return MathHelper.getInt(RANDOM, 6, 10);
        }
        return super.getExpDrop(state, world, pos, fortune);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 4 || meta == 5 || meta == 7) {
            return 12;
        }
        if (meta == 6 || meta == 8) {
            return 5;
        }
        if (meta == 9) {
            return 4;
        }
        if (meta == 10) {
            return 0;
        }
        return 8;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta == 4 || meta == 5) {
            return 2.0F;
        }
        if (meta == 6) {
            return 4.0F;
        }
        if (meta == 7 || meta == 8) {
            return -1.0F;
        }
        if (meta == 9 || meta == 10) {
            return 15.0F;
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 4 || meta == 5 || meta == 9 || meta == 10) {
            return 30.0F;
        }
        if (meta == 6) {
            return 100.0F;
        }
        if (meta == 7 || meta == 8) {
            return Float.MAX_VALUE;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int meta = this.getMetaFromState(state);
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);

        if (meta == 0
                && !player.isSneaking()
                && !stack.isEmpty()
                && stack.getItem() instanceof ItemEldritchObject
                && stack.getItemDamage() == ItemEldritchObject.META_ELDRITCH_OBJECT
                && tile instanceof TileEldritchAltar
                && ((TileEldritchAltar) tile).getEyes() < 4) {
            if (!world.isRemote) {
                TileEldritchAltar altar = (TileEldritchAltar) tile;
                if (altar.getEyes() >= 2) {
                    altar.setSpawner(true);
                    altar.setSpawnType((byte) 1);
                }
                altar.setEyes((byte) (altar.getEyes() + 1));
                altar.checkForMaze();
                stack.shrink(1);
                tile.markDirty();
                world.notifyBlockUpdate(pos, state, state, 3);
                world.playSound(null, pos, TCSounds.CRYSTAL, SoundCategory.BLOCKS, 0.2F, 1.0F);
            }
            return true;
        }

        if (meta == 8) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof ItemEldritchObject
                    && stack.getItemDamage() == ItemEldritchObject.META_ELDRITCH_OBJECT_2
                    && tile instanceof TileEldritchLock
                    && ((TileEldritchLock) tile).count < 0) {
                if (!world.isRemote) {
                    ((TileEldritchLock) tile).count = 0;
                    tile.markDirty();
                    world.notifyBlockUpdate(pos, state, state, 3);
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    world.playSound(null, pos, TCSounds.RUNICSHIELDCHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
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
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == 0 || meta == 1 || meta == 2 || meta == 3 || meta == 8 || meta == 9
                ? EnumBlockRenderType.INVISIBLE
                : EnumBlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int meta = this.getMetaFromState(state);
        if (meta == 8) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEldritchLock && ((TileEldritchLock) tile).count >= 0) {
                Thaumcraft.proxy.spark(
                        pos.getX() + rand.nextFloat(),
                        pos.getY() + rand.nextFloat(),
                        pos.getZ() + rand.nextFloat(),
                        0.5F,
                        0.65F + rand.nextFloat() * 0.1F,
                        1.0F,
                        1.0F,
                        0.8F);
            }
            return;
        }

        if (meta != 10) {
            return;
        }

        BlockPos sparkPos = pos.add(
                rand.nextInt(2) - rand.nextInt(2),
                rand.nextInt(2) - rand.nextInt(2),
                rand.nextInt(2) - rand.nextInt(2));
        if (!world.isAirBlock(sparkPos)) {
            return;
        }

        Thaumcraft.proxy.blockRunes(
                world,
                sparkPos.getX() + rand.nextFloat(),
                sparkPos.getY() + rand.nextFloat(),
                sparkPos.getZ() + rand.nextFloat(),
                0.5F + rand.nextFloat() * 0.5F,
                rand.nextFloat() * 0.3F,
                0.9F + rand.nextFloat() * 0.1F,
                16 + rand.nextInt(4),
                0.0F);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (!world.isRemote && meta < 4) {
            for (BlockPos target : BlockPos.getAllInBox(pos.add(-3, -2, -3), pos.add(3, 2, 3))) {
                if (target.equals(pos)) {
                    continue;
                }
                IBlockState targetState = world.getBlockState(target);
                if (targetState.getBlock() == this && this.getMetaFromState(targetState) < 4) {
                    world.setBlockToAir(target);
                }
            }
            world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.0F, false);
        }
        super.breakBlock(world, pos, state);
    }
}
