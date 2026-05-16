package thaumcraft.common.items.wands.foci;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileHole;

public class FocusPortableHole extends ItemFocusBasic {

    public FocusPortableHole() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x091429;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.AIR, 10);
    }

    public static boolean createHole(World world, int x, int y, int z, int side, byte count, int max) {
        if (world == null || world.isRemote || ConfigBlocks.blockHole == null) return false;

        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!canCreateHole(world, pos, state)) return false;

        TileEntity tile = world.getTileEntity(pos);
        TileHole hole = new TileHole(block, block.getMetaFromState(state), (short) max, count, (byte) side, tile);
        world.setBlockToAir(pos);
        if (!world.setBlockState(pos, ConfigBlocks.blockHole.getDefaultState(), 3)) {
            return false;
        }

        TileEntity placed = world.getTileEntity(pos);
        if (placed instanceof TileHole) {
            ((TileHole) placed).setStoredBlock(state, tile, (short) max, count, (byte) side);
        } else {
            world.setTileEntity(pos, hole);
        }
        world.notifyBlockUpdate(pos, Blocks.AIR.getDefaultState(), ConfigBlocks.blockHole.getDefaultState(), 3);
        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockSparkle(x, y, z, 0x400040),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0D));
        return true;
    }

    private static boolean canCreateHole(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        return world.getTileEntity(pos) == null
                && block != Blocks.AIR
                && block != Blocks.BEDROCK
                && block != ConfigBlocks.blockHole
                && block != ConfigBlocks.blockWarded
                && !block.isAir(state, world, pos)
                && !ThaumcraftApi.portableHoleBlackList.contains(block)
                && state.getMaterial().isSolid()
                && block.getBlockHardness(state, world, pos) >= 0.0F;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        if (movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK) return wandStack;

        BlockPos start = movingobjectposition.getBlockPos();
        if (world.provider.getDimension() == Config.dimensionOuterId) {
            if (!world.isRemote) {
                world.playSound(null, start, TCSounds.WANDFAIL, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            player.swingArm(EnumHand.MAIN_HAND);
            return wandStack;
        }

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        int distance = getTunnelDistance(world, start, movingobjectposition.sideHit,
                33 + this.getUpgradeLevel(focusStack, FocusUpgradeType.enlarge) * 8);
        if (!world.isRemote) {
            AspectList cost = scaleCost(this.getVisCost(focusStack), distance);
            if (wand.consumeAllVis(wandStack, player, cost, true, false)) {
                int extend = this.getUpgradeLevel(focusStack, FocusUpgradeType.extend);
                short duration = (short) (120 + 60 * extend);
                createHole(world, start.getX(), start.getY(), start.getZ(), movingobjectposition.sideHit.getIndex(),
                        (byte) (distance + 1), duration);
                world.playSound(null, start, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            } else {
                world.playSound(null, start, TCSounds.WANDFAIL, SoundCategory.PLAYERS, 0.2F, 1.0F);
            }
        }
        player.swingArm(EnumHand.MAIN_HAND);
        return wandStack;
    }

    private static int getTunnelDistance(World world, BlockPos start, EnumFacing side, int maxDistance) {
        BlockPos cursor = start;
        int distance = 0;
        while (distance < maxDistance && canCreateHole(world, cursor, world.getBlockState(cursor))) {
            distance++;
            cursor = cursor.offset(side.getOpposite());
        }
        return distance;
    }

    private static AspectList scaleCost(AspectList base, int multiplier) {
        AspectList out = new AspectList();
        for (Aspect aspect : base.getAspects()) {
            out.add(aspect, base.getAmount(aspect) * multiplier);
        }
        return out;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.enlarge, FocusUpgradeType.extend};
            default:
                return null;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BPH" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}
