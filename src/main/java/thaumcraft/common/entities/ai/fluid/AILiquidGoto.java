package thaumcraft.common.entities.ai.fluid;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

import java.util.ArrayList;

public class AILiquidGoto extends EntityAIBase {

    private EntityGolemBase theGolem;
    private double waterX;
    private double waterY;
    private double waterZ;
    private World theWorld;
    int count = 0;
    int prevX = 0;
    int prevY = 0;
    int prevZ = 0;

    public AILiquidGoto(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theGolem.ticksExisted % Config.golemDelay > 0
            || (this.theGolem.fluidCarried != null
                && this.theGolem.fluidCarried.amount > this.theGolem.getFluidCarryLimit() - 1000)) {
            return false;
        }
        ArrayList<FluidStack> fluids = GolemHelper.getMissingLiquids(this.theGolem);
        if (fluids == null) return false;
        for (FluidStack fluid : fluids) {
            Vec3d var1 = GolemHelper.findPossibleLiquid(fluid, this.theGolem);
            if (var1 == null) continue;
            // Use fluid registry name as a sentinel to show what we're looking for
            this.theGolem.itemWatched = new ItemStack(
                Item.getItemFromBlock(net.minecraft.init.Blocks.WATER), 1);
            this.waterX = var1.x;
            this.waterY = var1.y;
            this.waterZ = var1.z;
            double dd = this.theGolem.getDistanceSq(this.waterX, this.waterY, this.waterZ);
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    double dd2 = this.theGolem.getDistanceSq(var1.x + xx, this.waterY, var1.z + zz);
                    BlockPos checkPos = new BlockPos((int) var1.x + xx, (int) this.waterY, (int) var1.z + zz);
                    if (dd2 < dd && this.theGolem.world.isAirBlock(checkPos)) {
                        this.waterX = var1.x + xx;
                        this.waterZ = var1.z + zz;
                        dd = dd2;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.count = 0;
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.count == 0
            && this.prevX == MathHelper.floor(this.theGolem.posX)
            && this.prevY == MathHelper.floor(this.theGolem.posY)
            && this.prevZ == MathHelper.floor(this.theGolem.posZ)) {
            Vec3d var2 = RandomPositionGenerator.findRandomTarget(this.theGolem, 2, 1);
            if (var2 != null) {
                this.count = 20;
                this.theGolem.getNavigator().tryMoveToXYZ(var2.x, var2.y, var2.z, this.theGolem.getAIMoveSpeed());
            }
        }
        super.updateTask();
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        this.prevX = MathHelper.floor(this.theGolem.posX);
        this.prevY = MathHelper.floor(this.theGolem.posY);
        this.prevZ = MathHelper.floor(this.theGolem.posZ);
        this.theGolem.getNavigator().tryMoveToXYZ(this.waterX, this.waterY, this.waterZ, this.theGolem.getAIMoveSpeed());
    }
}
