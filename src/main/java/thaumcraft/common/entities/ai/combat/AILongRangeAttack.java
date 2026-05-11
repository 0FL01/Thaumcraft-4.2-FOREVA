package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;

public class AILongRangeAttack extends EntityAIBase {
    protected final IRangedAttackMob rangedMob;
    protected final double moveSpeed;
    protected final int attackInterval;
    protected final float maxRange;

    public AILongRangeAttack(IRangedAttackMob rangedMob, double moveSpeed, int attackInterval, float maxRange) {
        this.rangedMob = rangedMob;
        this.moveSpeed = moveSpeed;
        this.attackInterval = attackInterval;
        this.maxRange = maxRange;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() { return false; }
    @Override
    public void updateTask() {}
}
