package thaumcraft.common.items.equipment;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.IRepairable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.Set;

public class ItemElementalShovel extends ItemSpade implements IRepairable, IArchitect {

    private static final Block[] EFFECTIVE_BLOCKS = new Block[]{
            Blocks.DIRT, Blocks.GRASS, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW,
            Blocks.SNOW_LAYER, Blocks.CLAY, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MYCELIUM
    };

    private int side = EnumFacing.UP.getIndex();

    public ItemElementalShovel(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("shovel");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState clickedState = world.getBlockState(pos);
        Block clickedBlock = clickedState.getBlock();
        int clickedMeta = clickedBlock.getMetaFromState(clickedState);
        TileEntity te = world.getTileEntity(pos);

        if (te != null) {
            return EnumActionResult.PASS;
        }
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        BlockPos normal = pos.offset(facing);
        boolean did = false;
        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                BlockPos planeOffset = getPlaneOffset(aa, bb, facing.getIndex(), getOrientation(stack), player);
                BlockPos target = normal.add(planeOffset);
                if (!world.isBlockModifiable(player, target)) {
                    continue;
                }

                IBlockState targetState = world.getBlockState(target);
                Block targetBlock = targetState.getBlock();
                if (!canReplaceForPlacement(world, target, targetState, targetBlock)) {
                    continue;
                }

                if (placeCopiedBlock(stack, player, world, target, clickedBlock, clickedMeta)) {
                    did = true;
                    continue;
                }

                if (clickedBlock == Blocks.GRASS && placeCopiedBlock(stack, player, world, target, Blocks.DIRT, 0)) {
                    Thaumcraft.proxy.blockSparkle(world, target.getX(), target.getY(), target.getZ(), 3, 4);
                    did = true;
                }
            }
        }
        return did ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    private static boolean canReplaceForPlacement(World world, BlockPos target, IBlockState targetState, Block targetBlock) {
        if (world.isAirBlock(target)) {
            return true;
        }
        if (targetBlock == Blocks.FIRE || targetBlock == Blocks.VINE || targetBlock == Blocks.WATER || targetBlock == Blocks.FLOWING_WATER) {
            return true;
        }
        return targetState.getMaterial() == Material.WATER || targetBlock.isReplaceable(world, target);
    }

    private static boolean placeCopiedBlock(ItemStack stack, EntityPlayer player, World world, BlockPos target, Block block, int meta) {
        IBlockState placeState;
        try {
            placeState = block.getStateFromMeta(meta);
        } catch (Exception ignored) {
            placeState = block.getDefaultState();
        }

        if (!player.capabilities.isCreativeMode) {
            Item source = Item.getItemFromBlock(block);
            if (source == null || source == Item.getItemFromBlock(Blocks.AIR) || !InventoryUtils.consumeInventoryItem(player, source, meta)) {
                return false;
            }
        }

        SoundType sound = block.getSoundType(placeState, world, target, player);
        world.playSound(null, target, sound.getPlaceSound(), net.minecraft.util.SoundCategory.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        world.setBlockState(target, placeState, 3);
        stack.damageItem(1, player);
        Thaumcraft.proxy.blockSparkle(world, target.getX(), target.getY(), target.getZ(), 8401408, 4);
        player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        RayTraceResult hit = this.rayTrace(player.world, player, true);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && hit.sideHit != null) {
            this.side = hit.sideHit.getIndex();
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity) {
        if (entity.isSneaking()) {
            return super.onBlockDestroyed(stack, world, state, pos, entity);
        }
        if (!world.isRemote && entity instanceof EntityPlayer && (ForgeHooks.isToolEffective(world, pos, stack) || isEffectiveAgainst(state.getBlock()))) {
            for (int aa = -1; aa <= 1; aa++) {
                for (int bb = -1; bb <= 1; bb++) {
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    if (this.side <= 1) {
                        xx = aa;
                        zz = bb;
                    } else if (this.side <= 3) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                    BlockPos target = pos.add(xx, yy, zz);
                    if (target.equals(pos) || !world.isBlockModifiable((EntityPlayer) entity, target)) {
                        continue;
                    }
                    IBlockState targetState = world.getBlockState(target);
                    if (targetState.getBlockHardness(world, target) < 0.0F) {
                        continue;
                    }
                    if (!ForgeHooks.isToolEffective(world, target, stack) && !isEffectiveAgainst(targetState.getBlock())) {
                        continue;
                    }
                    if (world.destroyBlock(target, true)) {
                        stack.damageItem(1, entity);
                    }
                }
            }
        }
        return true;
    }

    private boolean isEffectiveAgainst(Block block) {
        for (Block b : EFFECTIVE_BLOCKS) {
            if (b == block) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        ArrayList<BlockCoordinates> blocks = new ArrayList<>();
        if (!player.isSneaking()) {
            return blocks;
        }
        BlockPos base = new BlockPos(x, y, z);
        BlockPos normal = base.offset(EnumFacing.byIndex(side));
        byte orientation = getOrientation(stack);
        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                BlockPos planeOffset = getPlaneOffset(aa, bb, side, orientation, player);
                BlockPos target = normal.add(planeOffset);
                IBlockState targetState = world.getBlockState(target);
                if (canReplaceForPlacement(world, target, targetState, targetState.getBlock())) {
                    blocks.add(new BlockCoordinates(target.getX(), target.getY(), target.getZ()));
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis) {
        return false;
    }

    private static BlockPos getPlaneOffset(int aa, int bb, int side, byte orientation, EntityPlayer player) {
        int xx = 0;
        int yy = 0;
        int zz = 0;
        if (orientation == 1) {
            yy = bb;
            if (side <= 1) {
                int l = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
                if (l == 0 || l == 2) {
                    xx = aa;
                } else {
                    zz = aa;
                }
            } else if (side <= 3) {
                zz = aa;
            } else {
                xx = aa;
            }
        } else if (orientation == 2) {
            if (side <= 1) {
                int l = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
                yy = bb;
                if (l == 0 || l == 2) {
                    xx = aa;
                } else {
                    zz = aa;
                }
            } else {
                zz = bb;
                xx = aa;
            }
        } else if (side <= 1) {
            xx = aa;
            zz = bb;
        } else if (side <= 3) {
            xx = aa;
            yy = bb;
        } else {
            zz = aa;
            yy = bb;
        }
        return new BlockPos(xx, yy, zz);
    }

    public static byte getOrientation(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("or")) {
            return stack.getTagCompound().getByte("or");
        }
        return 0;
    }

    public static void setOrientation(ItemStack stack, byte orientation) {
        if (stack == null || stack.isEmpty()) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setByte("or", (byte) Math.floorMod(orientation, 3));
    }
}
