package thaumcraft.common.entities.ai.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.entities.golems.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AILiquidGather extends EntityAIBase {

    private EntityGolemBase theGolem;
    private int waterX;
    private int waterY;
    private int waterZ;
    private EnumFacing markerOrientation;
    private World theWorld;
    private float pumpDist = 0.0f;
    int count = 0;
    HashMap<BlockPos, ArrayList<SourceBlock>> queue = new HashMap<>();
    ArrayList<BlockPos> cache = new ArrayList<>();
    BlockPos origin = null;

    public AILiquidGather(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        ArrayList<FluidStack> fluids = GolemHelper.getMissingLiquids(this.theGolem);
        if (fluids == null) return false;
        if (this.theGolem.itemWatched == null || fluids.isEmpty()
            || !this.theGolem.getNavigator().noPath()) {
            return false;
        }
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = this.theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        int camt = this.theGolem.fluidCarried != null ? this.theGolem.fluidCarried.amount : 0;
        int max = this.theGolem.getFluidCarryLimit();

        for (FluidStack fluid : fluids) {
            ArrayList<Marker> markers = GolemHelper.getMarkedFluidHandlersAdjacentToGolem(fluid, this.theWorld, this.theGolem);
            for (Marker marker : markers) {
                TileEntity te = this.theWorld.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
                if (te == null) continue;
                EnumFacing side = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
                if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) continue;
                IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                if (handler == null) continue;
                FluidStack sim = handler.drain(new FluidStack(fluid.getFluid(), max - camt), false);
                if (sim != null && sim.amount > 0) return true;
            }

            ArrayList<BlockPos> coords = GolemHelper.getMarkedBlocksAdjacentToGolem(this.theWorld, this.theGolem, (byte) -1);
            for (BlockPos loc : coords) {
                IBlockState state = this.theWorld.getBlockState(loc);
                Block bi = state.getBlock();
                FluidStack drained = drainWorldFluidBlock(this.theWorld, loc, false);
                if (drained != null && drained.getFluid() == fluid.getFluid() && drained.amount <= max - camt) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count < 20 && this.theGolem.itemWatched != null;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        this.count = 0;
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.theGolem.itemWatched = null;
        super.resetTask();
    }

    @Override
    public void updateTask() {
        ++this.count;
        if (this.count < 10) return;

        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = this.theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        int camt = this.theGolem.fluidCarried != null ? this.theGolem.fluidCarried.amount : 0;
        int max = this.theGolem.getFluidCarryLimit();

        ArrayList<FluidStack> fluids = GolemHelper.getMissingLiquids(this.theGolem);
        if (fluids == null) return;

        for (FluidStack fluidstack : fluids) {
            // IFluidHandler sources
            ArrayList<Marker> markers = GolemHelper.getMarkedFluidHandlersAdjacentToGolem(fluidstack, this.theWorld, this.theGolem);
            for (Marker marker : markers) {
                TileEntity te = this.theWorld.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
                if (te == null) continue;
                EnumFacing side = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
                if (!te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) continue;
                IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                if (handler == null) continue;

                FluidStack drained = handler.drain(new FluidStack(fluidstack.getFluid(), max - camt), true);
                if (drained != null && drained.amount > 0) {
                    if (this.theGolem.fluidCarried != null) {
                        this.theGolem.fluidCarried.amount += drained.amount;
                    } else {
                        this.theGolem.fluidCarried = drained.copy();
                    }
                    if (drained.amount > 200) {
                        float vol = 0.2f * ((float) drained.amount / (float) max);
                        this.theWorld.playSound(null, this.theGolem.getPosition(),
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                            net.minecraft.util.SoundCategory.NEUTRAL, vol,
                            1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
                    }
                    this.theGolem.updateCarried();
                    if (this.theGolem.fluidCarried.amount >= this.theGolem.getFluidCarryLimit()) {
                        this.theGolem.itemWatched = null;
                    }
                    this.count = 0;
                }
            }

            // World fluid block sources
            ArrayList<BlockPos> coords = GolemHelper.getMarkedBlocksAdjacentToGolem(this.theWorld, this.theGolem, (byte) -1);
            for (BlockPos loc : coords) {
                int i = loc.getX();
                int j = loc.getY();
                int k = loc.getZ();

                if (this.theGolem.getUpgradeAmount(5) > 0) {
                    if (!this.queue.containsKey(loc) || this.queue.get(loc).isEmpty()) {
                        this.rebuildQueue(loc, fluidstack.getFluid());
                    }
                    if (this.queue.containsKey(loc) && !this.queue.get(loc).isEmpty()) {
                        ArrayList<SourceBlock> t = this.queue.get(loc);
                        do {
                            BlockPos current = t.get(0).loc;
                            i = current.getX();
                            j = current.getY();
                            k = current.getZ();
                            t.remove(0);
                        } while (!t.isEmpty() && !this.validFluidBlock(fluidstack.getFluid(), i, j, k));
                        this.queue.put(loc, t);
                    }
                }

                FluidStack drained = drainWorldFluidBlock(this.theWorld, new BlockPos(i, j, k), false);
                if (drained != null && drained.getFluid() == fluidstack.getFluid()) {
                    int space = max - camt;
                    if (drained.amount > space) continue;

                    drainWorldFluidBlock(this.theWorld, new BlockPos(i, j, k), true);
                    this.theWorld.setBlockToAir(new BlockPos(i, j, k));

                    if (this.theGolem.fluidCarried != null) {
                        this.theGolem.fluidCarried.amount += drained.amount;
                    } else {
                        this.theGolem.fluidCarried = drained.copy();
                    }
                    this.theWorld.playSound(null, this.theGolem.getPosition(),
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.2f,
                        1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
                    this.theGolem.updateCarried();
                    if (this.theGolem.fluidCarried.amount > this.theGolem.getFluidCarryLimit() - 1000) {
                        this.theGolem.itemWatched = null;
                    }
                    this.count = 0;
                }
            }
        }
    }

    private boolean validFluidBlock(Fluid fluid, int i, int j, int k) {
        FluidStack drained = drainWorldFluidBlock(this.theWorld, new BlockPos(i, j, k), false);
        if (drained != null) return true;

        Block bi = this.theWorld.getBlockState(new BlockPos(i, j, k)).getBlock();
        Fluid lookupFluid = FluidRegistry.lookupFluidForBlock(bi);
        if (lookupFluid == fluid) {
            if (bi == Blocks.WATER || bi == Blocks.FLOWING_WATER || bi == Blocks.LAVA || bi == Blocks.FLOWING_LAVA) {
                Integer level = this.theWorld.getBlockState(new BlockPos(i, j, k)).getValue(net.minecraft.block.BlockLiquid.LEVEL);
                return level != null && level == 0;
            }
        }
        return false;
    }

    private void rebuildQueue(BlockPos loc, Fluid fluid) {
        this.pumpDist = this.theGolem.getRange() * this.theGolem.getRange();
        this.cache.clear();
        this.origin = loc;
        ArrayList<SourceBlock> sources = new ArrayList<>();
        this.getConnectedFluidBlocks(this.theWorld, loc.getX(), loc.getY(), loc.getZ(), fluid, sources);
        Collections.sort(sources, Collections.reverseOrder());
        this.queue.put(loc, sources);
    }

    private void getConnectedFluidBlocks(World world, int x, int y, int z, Fluid fluid, ArrayList<SourceBlock> sources) {
        try {
            BlockPos cc = new BlockPos(x, y, z);
            if (this.cache.contains(cc)) return;
            this.cache.add(cc);
            for (int a = -1; a <= 1; ++a) {
                for (int b = -1; b <= 1; ++b) {
                    for (int c = -1; c <= 1; ++c) {
                        if (a == 0 && b == 0 && c == 0) continue;
                        int xx = x + a;
                        int yy = y + b;
                        int zz = z + c;
                        BlockPos np = new BlockPos(xx, yy, zz);
                        float dist = (float) this.origin.distanceSq(np);
                        if (dist > this.pumpDist) continue;

                        Block bi = world.getBlockState(np).getBlock();
                        if (bi == Blocks.WATER) bi = Blocks.FLOWING_WATER;
                        if (bi == Blocks.LAVA) bi = Blocks.FLOWING_LAVA;

                        Fluid fi = FluidRegistry.lookupFluidForBlock(bi);
                        if (fi == null || fi != fluid) continue;
                        if (this.validFluidBlock(fluid, xx, yy, zz)) {
                            sources.add(new SourceBlock(np, dist));
                        }
                        this.getConnectedFluidBlocks(world, xx, yy, zz, fluid, sources);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static FluidStack drainWorldFluidBlock(World world, BlockPos pos, boolean doDrain) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) block;
            if (!fluidBlock.canDrain(world, pos)) return null;
            FluidStack drained = fluidBlock.drain(world, pos, doDrain);
            return (drained != null && drained.amount > 0) ? drained : null;
        }

        Fluid fluid = null;
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) fluid = FluidRegistry.WATER;
        else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) fluid = FluidRegistry.LAVA;

        if (fluid == null) return null;
        if (!(block instanceof net.minecraft.block.BlockLiquid)) return null;
        Integer level = state.getValue(net.minecraft.block.BlockLiquid.LEVEL);
        if (level == null || level != 0) return null;
        if (doDrain) world.setBlockToAir(pos);
        return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    }

    private class SourceBlock implements Comparable<Object> {
        BlockPos loc;
        float dist;

        public SourceBlock(BlockPos loc, float dist) {
            this.loc = loc;
            this.dist = dist;
        }

        @Override
        public int compareTo(Object target) {
            SourceBlock other = (SourceBlock) target;
            return Float.compare(other.dist, this.dist);
        }
    }
}
