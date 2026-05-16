package thaumcraft.common.items.wands.foci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.tiles.TileWarded;

public class FocusWarding extends ItemFocusBasic implements IArchitect {

    private static final Map<String, Long> DELAY = new HashMap<>();
    private final ArrayList<BlockCoordinates> checked = new ArrayList<>();

    public FocusWarding() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFFEFAF;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.EARTH, 25).add(Aspect.ORDER, 25).add(Aspect.WATER, 10);
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        player.swingArm(EnumHand.MAIN_HAND);
        if (world.isRemote || movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK) {
            return wandStack;
        }

        BlockPos pos = movingobjectposition.getBlockPos();
        String key = pos.getX() + ":" + pos.getY() + ":" + pos.getZ() + ":" + world.provider.getDimension();
        long now = System.currentTimeMillis();
        if (DELAY.containsKey(key) && DELAY.get(key) > now) {
            return wandStack;
        }
        DELAY.put(key, now + 500L);

        TileEntity tile = world.getTileEntity(pos);
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        if (tile instanceof TileWarded) {
            TileWarded warded = (TileWarded) tile;
            if (warded.owner == player.getName().hashCode()) {
                for (BlockCoordinates c : this.getArchitectBlocks(wandStack, world, pos.getX(), pos.getY(), pos.getZ(),
                        movingobjectposition.sideHit.getIndex(), player)) {
                    unwardBlock(world, new BlockPos(c.x, c.y, c.z), player);
                    PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockSparkle(c.x, c.y, c.z, 0xFC9A00),
                            new NetworkRegistry.TargetPoint(world.provider.getDimension(), c.x, c.y, c.z, 32.0D));
                }
                world.playSound(null, pos, TCSounds.ZAP, SoundCategory.PLAYERS, 0.25F, 1.0F);
            }
        } else if (isWardable(world, pos, player)) {
            for (BlockCoordinates c : this.getArchitectBlocks(wandStack, world, pos.getX(), pos.getY(), pos.getZ(),
                    movingobjectposition.sideHit.getIndex(), player)) {
                if (!wand.consumeAllVis(wandStack, player, this.getVisCost(wand.getFocusItem(wandStack)), true, false)) {
                    break;
                }
                wardBlock(world, new BlockPos(c.x, c.y, c.z), player);
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockSparkle(c.x, c.y, c.z, 0xFC9A00),
                        new NetworkRegistry.TargetPoint(world.provider.getDimension(), c.x, c.y, c.z, 32.0D));
            }
            world.playSound(null, pos, TCSounds.ZAP, SoundCategory.PLAYERS, 0.25F, 1.0F);
        }
        return wandStack;
    }

    private static boolean isWardable(World world, BlockPos pos, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return world.getTileEntity(pos) == null
                && block != Blocks.AIR
                && block != ConfigBlocks.blockWarded
                && block != ConfigBlocks.blockHole
                && !block.isAir(state, world, pos)
                && state.getMaterial().isSolid()
                && state.isFullCube()
                && block.getBlockHardness(state, world, pos) >= 0.0F
                && world.isBlockModifiable(player, pos);
    }

    private static void wardBlock(World world, BlockPos pos, EntityPlayer player) {
        if (!isWardable(world, pos, player)) return;
        IBlockState state = world.getBlockState(pos);
        int light = state.getLightValue(world, pos);
        world.setBlockState(pos, ConfigBlocks.blockWarded.getDefaultState(), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileWarded) {
            ((TileWarded) tile).setStoredBlock(state, light, player.getName());
            world.notifyBlockUpdate(pos, state, ConfigBlocks.blockWarded.getDefaultState(), 3);
        }
    }

    private static void unwardBlock(World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileWarded && ((TileWarded) tile).owner == player.getName().hashCode()) {
            IBlockState old = world.getBlockState(pos);
            IBlockState stored = ((TileWarded) tile).getStoredState();
            ((TileWarded) tile).restoreBlock(world, pos);
            world.notifyBlockUpdate(pos, old, stored, 3);
        }
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.architect};
            case 3:
            case 4:
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.enlarge};
            default:
                return null;
        }
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return !type.equals(FocusUpgradeType.enlarge) || this.isUpgradedWith(focusstack, FocusUpgradeType.architect);
    }

    @Override
    public int getMaxAreaSize(ItemStack focusstack) {
        return 3 + this.getUpgradeLevel(focusstack, FocusUpgradeType.enlarge);
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        ItemStack focusStack = wand.getFocusItem(stack);
        int sizeX = 0;
        int sizeY = 0;
        int sizeZ = 0;
        if (this.isUpgradedWith(focusStack, FocusUpgradeType.architect)) {
            int max = this.getMaxAreaSize(focusStack);
            sizeX = WandManager.getAreaX(stack, max);
            sizeY = WandManager.getAreaY(stack, max);
            sizeZ = WandManager.getAreaZ(stack, max);
        }

        ArrayList<BlockCoordinates> out = new ArrayList<>();
        this.checked.clear();
        boolean tiles = world.getTileEntity(new BlockPos(x, y, z)) instanceof TileWarded;
        if (side == EnumFacing.NORTH.getIndex() || side == EnumFacing.SOUTH.getIndex()) {
            this.checkNeighbours(world, x, y, z, new BlockCoordinates(x, y, z), side, sizeZ, sizeY, sizeX, out, player, tiles);
        } else {
            this.checkNeighbours(world, x, y, z, new BlockCoordinates(x, y, z), side, sizeX, sizeY, sizeZ, out, player, tiles);
        }
        return out;
    }

    private void checkNeighbours(World world, int x, int y, int z, BlockCoordinates pos, int side, int sizeX,
                                 int sizeY, int sizeZ, ArrayList<BlockCoordinates> list, EntityPlayer player,
                                 boolean tiles) {
        if (this.checked.contains(pos)) return;
        this.checked.add(pos);

        switch (side) {
            case 0:
            case 1:
                if (Math.abs(pos.x - x) > sizeX || Math.abs(pos.z - z) > sizeZ || Math.abs(pos.y - y) > sizeY) return;
                break;
            case 2:
            case 3:
                if (Math.abs(pos.x - x) > sizeX || Math.abs(pos.y - y) > sizeZ || Math.abs(pos.z - z) > sizeY) return;
                break;
            case 4:
            case 5:
                if (Math.abs(pos.y - y) > sizeX || Math.abs(pos.z - z) > sizeZ || Math.abs(pos.x - x) > sizeY) return;
                break;
            default:
                return;
        }

        BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
        TileEntity tile = world.getTileEntity(blockPos);
        if (tiles) {
            if (!(tile instanceof TileWarded) || ((TileWarded) tile).owner != player.getName().hashCode()) return;
        } else if (!isWardable(world, blockPos, player)) {
            return;
        }

        list.add(pos);
        for (EnumFacing dir : EnumFacing.VALUES) {
            this.checkNeighbours(world, x, y, z,
                    new BlockCoordinates(pos.x + dir.getXOffset(), pos.y + dir.getYOffset(), pos.z + dir.getZOffset()),
                    side, sizeX, sizeY, sizeZ, list, player, tiles);
        }
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis) {
        int dim = WandManager.getAreaDim(stack);
        if (dim == 0) {
            return true;
        }
        switch (side) {
            case 0:
            case 1:
                return axis == IArchitect.EnumAxis.X && dim == 1
                        || axis == IArchitect.EnumAxis.Z && dim == 2
                        || axis == IArchitect.EnumAxis.Y && dim == 3;
            case 2:
            case 3:
                return axis == IArchitect.EnumAxis.Y && dim == 1
                        || axis == IArchitect.EnumAxis.X && dim == 2
                        || axis == IArchitect.EnumAxis.Z && dim == 3;
            case 4:
            case 5:
                return axis == IArchitect.EnumAxis.Y && dim == 1
                        || axis == IArchitect.EnumAxis.Z && dim == 2
                        || axis == IArchitect.EnumAxis.X && dim == 3;
            default:
                return false;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BWA" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
