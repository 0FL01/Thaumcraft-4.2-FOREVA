package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AIAttackOnCollide extends EntityAIBase {

    EntityCreature attacker;
    int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    Path entityPathEntity;
    Class classTarget;
    private int field_75445_i;
    private double field_151497_i;
    private double field_151495_j;
    private double field_151496_k;
    private int failedPathFindingPenalty;

    public AIAttackOnCollide(EntityCreature creature, Class targetClass, double speed, boolean longMemory) {
        this(creature, speed, longMemory);
        this.classTarget = targetClass;
    }

    public AIAttackOnCollide(EntityCreature creature, double speed, boolean longMemory) {
        this.attacker = creature;
        this.speedTowardsTarget = speed;
        this.longMemory = longMemory;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null) return false;
        if (!target.isEntityAlive()) return false;
        if (this.classTarget != null && !this.classTarget.isAssignableFrom(target.getClass())) return false;
        if (--this.field_75445_i <= 0) {
            this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(target);
            this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
            return this.entityPathEntity != null;
        }
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null) return false;
        if (!target.isEntityAlive()) return false;
        if (!this.longMemory) return !this.attacker.getNavigator().noPath();
        return this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(MathHelper.floor(target.posX), MathHelper.floor(target.posY), MathHelper.floor(target.posZ)));
    }

    @Override
    public void startExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget);
        this.field_75445_i = 0;
    }

    @Override
    public void resetTask() {
        this.attacker.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distSq = this.attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        double width = (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + target.width);
        --this.field_75445_i;
        if (this.attackTick > 0) --this.attackTick;
        if ((this.longMemory || this.attacker.getEntitySenses().canSee(target)) && this.field_75445_i <= 0 && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D || target.getDistanceSq(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
            this.field_151497_i = target.posX;
            this.field_151495_j = target.getEntityBoundingBox().minY;
            this.field_151496_k = target.posZ;
            this.field_75445_i = this.failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);
            Path currentPath = this.attacker.getNavigator().getPath();
            if (currentPath != null) {
                PathPoint finalPoint = currentPath.getFinalPathPoint();
                if (finalPoint != null && target.getDistanceSq(finalPoint.x, finalPoint.y, finalPoint.z) < 1.0D) {
                    this.failedPathFindingPenalty = 0;
                } else {
                    this.failedPathFindingPenalty += 10;
                }
            } else {
                this.failedPathFindingPenalty += 10;
            }
            if (distSq > 1024.0D) {
                this.field_75445_i += 10;
            } else if (distSq > 256.0D) {
                this.field_75445_i += 5;
            }
            if (!this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget)) {
                this.field_75445_i += 15;
            }
        }
        if (distSq <= width && this.attackTick <= 0) {
            this.attackTick = 10;
            if (!this.attacker.getHeldItemMainhand().isEmpty()) {
                this.attacker.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
            }
            this.attacker.attackEntityAsMob(target);
        }
    }
}
