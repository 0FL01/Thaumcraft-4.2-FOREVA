package thaumcraft.common.items.wands.foci;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.events.ServerTickEventsFML;
import thaumcraft.common.lib.utils.BlockUtils;

public class FocusTrade extends ItemFocusBasic implements IArchitect {

    private final ArrayList<BlockCoordinates> checked = new ArrayList<>();

    public FocusTrade() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x00CED1;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        AspectList cost = new AspectList().add(Aspect.ENTROPY, 5).add(Aspect.EARTH, 5).add(Aspect.ORDER, 5);
        if (this.isUpgradedWith(stack, FocusUpgradeType.silktouch)) {
            return new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1).add(Aspect.EARTH, 1)
                    .add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1).add(cost);
        }
        return cost;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        if (movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK) return wandStack;

        BlockPos pos = movingobjectposition.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        if (player.isSneaking()) {
            if (!world.isRemote && world.getTileEntity(pos) == null) {
                ItemStack picked = BlockUtils.createStackedBlock(block, meta);
                if (picked.isEmpty() && block != Blocks.AIR && !block.isAir(state, world, pos)) {
                    picked = new ItemStack(block, 1, meta);
                }
                if (!picked.isEmpty()) {
                    this.storePickedBlock(wandStack, picked);
                }
            }
            player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
            return wandStack;
        }

        ItemStack picked = this.getPickedBlock(wandStack);
        if (picked.isEmpty()) {
            return wandStack;
        }
        if (world.isRemote) {
            player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
            return wandStack;
        }
        if (world.getTileEntity(pos) != null || !this.canSwapBlock(world, pos, player)) return wandStack;

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (this.isUpgradedWith(focusStack, FocusUpgradeType.architect)) {
            for (BlockCoordinates c : this.getArchitectBlocks(wandStack, world, pos.getX(), pos.getY(), pos.getZ(), movingobjectposition.sideHit.getIndex(), player)) {
                BlockPos cpos = new BlockPos(c.x, c.y, c.z);
                IBlockState cstate = world.getBlockState(cpos);
                ServerTickEventsFML.addSwapper(world, c.x, c.y, c.z, cstate.getBlock(), cstate.getBlock().getMetaFromState(cstate), picked.copy(), 0, player, player.inventory.currentItem);
            }
        } else {
            ServerTickEventsFML.addSwapper(world, pos.getX(), pos.getY(), pos.getZ(), block, meta, picked.copy(),
                    3 + this.getUpgradeLevel(focusStack, FocusUpgradeType.enlarge), player, player.inventory.currentItem);
        }
        player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public boolean onFocusBlockStartBreak(ItemStack wandStack, int x, int y, int z, EntityPlayer player) {
        if (!(wandStack.getItem() instanceof ItemWandCasting) || player.world.isRemote) return false;
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack picked = this.getPickedBlock(wandStack);
        if (picked.isEmpty() || player.world.getTileEntity(pos) != null || !this.canSwapBlock(player.world, pos, player)) {
            return false;
        }
        IBlockState state = player.world.getBlockState(pos);
        ServerTickEventsFML.addSwapper(player.world, x, y, z, state.getBlock(), state.getBlock().getMetaFromState(state), picked.copy(), 0, player, player.inventory.currentItem);
        return true;
    }

    public void storePickedBlock(ItemStack stack, ItemStack stackout) {
        if (stack == null || stack.isEmpty() || stackout == null || stackout.isEmpty()) return;
        NBTTagCompound item = new NBTTagCompound();
        stackout.writeToNBT(item);
        ItemWandCasting.ensureTag(stack).setTag("picked", item);
    }

    public ItemStack getPickedBlock(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("picked")) {
            return new ItemStack(stack.getTagCompound().getCompoundTag("picked"));
        }
        return ItemStack.EMPTY;
    }

    private boolean canSwapBlock(World world, BlockPos pos, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block != Blocks.AIR
                && !block.isAir(state, world, pos)
                && state.getMaterial() != Config.taintMaterial
                && block.getBlockHardness(state, world, pos) >= 0.0F
                && world.isBlockModifiable(player, pos);
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
            case 2:
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.enlarge};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.enlarge, FocusUpgradeType.treasure, FocusUpgradeType.architect};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.enlarge, FocusUpgradeType.silktouch};
            default:
                return null;
        }
    }

    @Override
    public int getMaxAreaSize(ItemStack focusstack) {
        return 3 + this.getUpgradeLevel(focusstack, FocusUpgradeType.enlarge) * 2;
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        ItemStack focusStack = wand.getFocusItem(stack);
        int max = this.getMaxAreaSize(focusStack);
        int sizeX = WandManager.getAreaX(stack, max);
        int sizeY = WandManager.getAreaY(stack, max);
        int sizeZ = WandManager.getAreaZ(stack, max);
        IBlockState state = world.getBlockState(new BlockPos(x, y, z));
        ArrayList<BlockCoordinates> out = new ArrayList<>();
        this.checked.clear();
        if (side == EnumFacing.NORTH.getIndex() || side == EnumFacing.SOUTH.getIndex()) {
            this.checkNeighbours(world, x, y, z, state, new BlockCoordinates(x, y, z), side, sizeZ, sizeY, sizeX, out, player);
        } else {
            this.checkNeighbours(world, x, y, z, state, new BlockCoordinates(x, y, z), side, sizeX, sizeY, sizeZ, out, player);
        }
        return out;
    }

    private void checkNeighbours(World world, int x, int y, int z, IBlockState source, BlockCoordinates pos, int side, int sizeX, int sizeY, int sizeZ, ArrayList<BlockCoordinates> list, EntityPlayer player) {
        if (this.checked.contains(pos)) return;
        this.checked.add(pos);
        switch (side) {
            case 0:
            case 1:
                if (Math.abs(pos.x - x) > sizeX || Math.abs(pos.z - z) > sizeZ) return;
                break;
            case 2:
            case 3:
                if (Math.abs(pos.x - x) > sizeX || Math.abs(pos.y - y) > sizeZ) return;
                break;
            case 4:
            case 5:
                if (Math.abs(pos.y - y) > sizeX || Math.abs(pos.z - z) > sizeZ) return;
                break;
            default:
                return;
        }

        BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
        IBlockState state = world.getBlockState(blockPos);
        if (state.getBlock() != source.getBlock()
                || state.getBlock().getMetaFromState(state) != source.getBlock().getMetaFromState(source)
                || world.getTileEntity(blockPos) != null
                || !BlockUtils.isBlockExposed(world, pos.x, pos.y, pos.z)
                || !this.canSwapBlock(world, blockPos, player)) {
            return;
        }

        list.add(pos);
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir.getIndex() == side || dir.getOpposite().getIndex() == side) continue;
            this.checkNeighbours(world, x, y, z, source, new BlockCoordinates(pos.x + dir.getXOffset(), pos.y + dir.getYOffset(), pos.z + dir.getZOffset()), side, sizeX, sizeY, sizeZ, list, player);
        }
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis) {
        int dim = WandManager.getAreaDim(stack);
        switch (side) {
            case 0:
            case 1:
                return axis == IArchitect.EnumAxis.X && (dim == 0 || dim == 1)
                        || axis == IArchitect.EnumAxis.Z && (dim == 0 || dim == 2);
            case 2:
            case 3:
                return axis == IArchitect.EnumAxis.Y && (dim == 0 || dim == 1)
                        || axis == IArchitect.EnumAxis.X && (dim == 0 || dim == 2);
            case 4:
            case 5:
                return axis == IArchitect.EnumAxis.Y && (dim == 0 || dim == 1)
                        || axis == IArchitect.EnumAxis.Z && (dim == 0 || dim == 2);
            default:
                return false;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BT" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
