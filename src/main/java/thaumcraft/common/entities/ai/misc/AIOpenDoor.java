package thaumcraft.common.entities.ai.misc;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIOpenDoor extends AIDoorInteract {
    private int closeDoorTemporisation;

    public AIOpenDoor(EntityGolemBase golem) {
        super(golem);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.closeDoorTemporisation > 0 && super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.closeDoorTemporisation = 20;
        this.setOpen(true);
    }

    @Override
    public void resetTask() {
        this.setOpen(false);
    }

    @Override
    public void updateTask() {
        --this.closeDoorTemporisation;
        super.updateTask();
    }

    private void setOpen(boolean open) {
        IBlockState state = this.theEntity.world.getBlockState(this.doorPosition);
        if (this.doorBlock instanceof BlockDoor) {
            ((BlockDoor) this.doorBlock).toggleDoor(this.theEntity.world, this.doorPosition, open);
        } else if (this.doorBlock instanceof BlockFenceGate
                && state.getBlock() == this.doorBlock
                && state.getValue(BlockFenceGate.OPEN) != open) {
            this.theEntity.world.setBlockState(this.doorPosition,
                    state.withProperty(BlockFenceGate.OPEN, open), 10);
            this.theEntity.world.playEvent(null, open ? 1008 : 1014, this.doorPosition, 0);
        }
    }
}
