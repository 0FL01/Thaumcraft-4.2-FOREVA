package thaumcraft.common.entities.ai.interact;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

public class AIHarvestLogs extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;
    private BlockPos targetPos = null;
    private int logCount = 0;

    public AIHarvestLogs(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        return this.findLog();
    }

    private boolean findLog() {
        BlockPos home = theGolem.getHomePosition();
        float range = theGolem.getRange();
        int r = (int) range;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -1; dy <= 4; dy++) {
                    BlockPos pos = new BlockPos(home.getX() + dx, home.getY() + dy, home.getZ() + dz);
                    if (theGolem.getDistanceSq(pos) > range * range) continue;
                    if (Utils.isWoodLog(theGolem.world, pos)) {
                        this.targetPos = pos;
                        this.logCount = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && this.targetPos != null;
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.targetPos = null;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.targetPos != null) {
            theGolem.getLookHelper().setLookPosition(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 30.0F, 30.0F);
            double dist = theGolem.getDistanceSq(targetPos);
            if (dist <= 3.0) {
                this.chop();
            }
        }
    }

    private void chop() {
        if (theGolem.world.isRemote) return;
        WorldServer world = (WorldServer) theGolem.world;

        // Use a simple loop to break all connected logs upward
        BlockPos current = targetPos;
        int broken = 0;
        while (broken < 20 && current != null && Utils.isWoodLog(world, current)) {
            IBlockState state = world.getBlockState(current);
            state.getBlock().harvestBlock(world, null, current, state, world.getTileEntity(current), ItemStack.EMPTY);
            world.setBlockToAir(current);
            broken++;

            // Move upward to find the next log
            BlockPos next = current.up();
            if (Utils.isWoodLog(world, next)) {
                current = next;
            } else {
                // Check in a 3x3 horizontal area above for branching trees
                boolean found = false;
                for (int dx = -1; dx <= 1 && !found; dx++) {
                    for (int dz = -1; dz <= 1 && !found; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        BlockPos candidate = current.add(dx, 0, dz);
                        if (Utils.isWoodLog(world, candidate)) {
                            current = candidate;
                            found = true;
                        }
                    }
                }
                if (!found) current = null;
            }
        }

        if (broken > 0) {
            theGolem.startActionTimer();
        }
        this.targetPos = null;
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        if (this.targetPos != null) {
            theGolem.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, theGolem.getAIMoveSpeed());
        }
    }
}
