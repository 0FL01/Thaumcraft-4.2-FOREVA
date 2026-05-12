package thaumcraft.common.entities.ai.fluid;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

import java.util.ArrayList;

public class AILiquidEmpty extends EntityAIBase {

    private EntityGolemBase theGolem;
    private int waterX;
    private int waterY;
    private int waterZ;
    private World theWorld;

    public AILiquidEmpty(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = this.theGolem.getHomePosition();
        if (!this.theGolem.getNavigator().noPath()
            || this.theGolem.fluidCarried == null
            || this.theGolem.fluidCarried.amount == 0
            || this.theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) > 5.0) {
            return false;
        }
        ArrayList<FluidStack> fluids = GolemHelper.getMissingLiquids(this.theGolem);
        if (fluids == null) return false;
        for (FluidStack fluid : fluids) {
            if (fluid.isFluidEqual(this.theGolem.fluidCarried)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = this.theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();

        TileEntity tile = this.theWorld.getTileEntity(new BlockPos(cX, cY, cZ));
        if (tile == null) return;

        EnumFacing capSide = facing.getOpposite();
        if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, capSide)) return;
        IFluidHandler fh = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, capSide);
        if (fh == null) return;

        FluidStack copyForFill = this.theGolem.fluidCarried.copy();
        int fillable = fh.fill(copyForFill, false);
        if (fillable > 0) {
            copyForFill.amount = Math.min(copyForFill.amount, fillable);
            int amt = fh.fill(copyForFill, true);
            this.theGolem.fluidCarried.amount -= amt;
            if (this.theGolem.fluidCarried.amount <= 0) {
                this.theGolem.fluidCarried = null;
            }
            if (amt > 200) {
                float vol = Math.min(0.2f, 0.2f * ((float) amt / (float) this.theGolem.getFluidCarryLimit()));
                this.theWorld.playSound(null, this.theGolem.getPosition(),
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                    net.minecraft.util.SoundCategory.NEUTRAL, vol,
                    1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
            }
            this.theGolem.updateCarried();
            this.theWorld.markBlockRangeForRenderUpdate(cX, cY, cZ, cX, cY, cZ);
            this.theGolem.itemWatched = null;
        }
    }

    @Override
    public void resetTask() {}
}
