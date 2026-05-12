package thaumcraft.common.entities.ai.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.golems.EntityGolemBase;

public abstract class AIDoorInteract extends EntityAIBase {

    protected EntityGolemBase theEntity;
    protected BlockPos doorPosition = BlockPos.ORIGIN;
    protected BlockDoor doorBlock;
    protected boolean hasStoppedDoorInteraction;
    protected float entityPositionX;
    protected float entityPositionZ;
    private int count = 0;

    public AIDoorInteract(EntityGolemBase golem) {
        this.theEntity = golem;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.collidedHorizontally) return false;

        net.minecraft.pathfinding.PathNavigate nav = this.theEntity.getNavigator();
        Path path = nav.getPath();
        if (path != null && !path.isFinished() && nav.noPath()) {
            for (int i = 0; i < Math.min(path.getCurrentPathLength() + 2, path.getCurrentPathIndex()); ++i) {
                PathPoint point = path.getPathPointFromIndex(i);
                this.doorPosition = new BlockPos(point.x, point.y, point.z);
                if (this.theEntity.getDistanceSq(this.doorPosition.getX(), this.theEntity.posY, this.doorPosition.getZ()) > 2.25)
                    continue;
                this.doorBlock = this.getBlockDoor(this.doorPosition);
                if (this.doorBlock == null) {
                    // Also check one block above (doors are 2-tall)
                    this.doorPosition = new BlockPos(point.x, point.y + 1, point.z);
                    this.doorBlock = this.getBlockDoor(this.doorPosition);
                }
                if (this.doorBlock == null) continue;
                this.count = 200;
                return true;
            }
            // Fallback: check door at entity's own position
            this.doorPosition = new BlockPos(
                MathHelper.floor(this.theEntity.posX),
                MathHelper.floor(this.theEntity.posY),
                MathHelper.floor(this.theEntity.posZ));
            this.doorBlock = this.getBlockDoor(this.doorPosition);
            if (this.doorBlock == null) {
                this.doorPosition = new BlockPos(this.doorPosition.getX(), this.doorPosition.getY() + 1, this.doorPosition.getZ());
                this.doorBlock = this.getBlockDoor(this.doorPosition);
            }
            if (this.doorBlock != null) this.count = 200;
            return this.doorBlock != null;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && !this.hasStoppedDoorInteraction;
    }

    @Override
    public void startExecuting() {
        this.count = 100;
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float) (this.doorPosition.getX() + 0.5f - this.theEntity.posX);
        this.entityPositionZ = (float) (this.doorPosition.getZ() + 0.5f - this.theEntity.posZ);
    }

    @Override
    public void updateTask() {
        --this.count;
        float dx = (float) (this.doorPosition.getX() + 0.5f - this.theEntity.posX);
        float dz = (float) (this.doorPosition.getZ() + 0.5f - this.theEntity.posZ);
        float dot = this.entityPositionX * dx + this.entityPositionZ * dz;
        if (dot < 0.0f) this.hasStoppedDoorInteraction = true;
    }

    private BlockDoor getBlockDoor(BlockPos pos) {
        IBlockState state = this.theEntity.world.getBlockState(pos);
        Block block = state.getBlock();
        return block instanceof BlockDoor ? (BlockDoor) block : null;
    }
}
