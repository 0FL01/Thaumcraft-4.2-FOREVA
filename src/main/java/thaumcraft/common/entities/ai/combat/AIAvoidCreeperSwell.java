package thaumcraft.common.entities.ai.combat;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIAvoidCreeperSwell extends EntityAIBase {

    private EntityGolemBase theGolem;
    private float farSpeed;
    private float nearSpeed;
    private Entity closestLivingEntity;
    private float distanceFromEntity;
    private Path entityPathEntity;
    private PathNavigate entityPathNavigate;
    Vec3d targetBlock;

    public AIAvoidCreeperSwell(EntityGolemBase golem) {
        this.theGolem = golem;
        this.distanceFromEntity = 5.0F;
        this.entityPathNavigate = golem.getNavigator();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.farSpeed == 0.0F) {
            this.farSpeed = this.theGolem.getAIMoveSpeed() * 1.125F;
            this.nearSpeed = this.theGolem.getAIMoveSpeed() * 1.25F;
        }
        List<EntityCreeper> list = this.theGolem.world.getEntitiesWithinAABB(EntityCreeper.class,
            this.theGolem.getEntityBoundingBox().grow((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity));
        if (list.isEmpty()) return false;
        if (list.get(0).getCreeperState() != 1) return false;
        this.closestLivingEntity = list.get(0);
        if (!this.theGolem.getEntitySenses().canSee(this.closestLivingEntity)) return false;
        Vec3d fleePos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
            (net.minecraft.entity.EntityCreature)this.theGolem, 16, 7,
            new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));
        if (fleePos == null) return false;
        if (this.closestLivingEntity.getDistanceSq(fleePos.x, fleePos.y, fleePos.z) < this.closestLivingEntity.getDistanceSq(this.theGolem)) return false;

        int tx = MathHelper.floor(fleePos.x);
        int ty = MathHelper.floor(fleePos.y);
        int tz = MathHelper.floor(fleePos.z);

        this.entityPathEntity = this.entityPathNavigate.getPathToPos(new BlockPos(tx, ty, tz));
        this.targetBlock = fleePos;

        if (this.entityPathEntity == null) return false;

        PathPoint end = this.entityPathEntity.getFinalPathPoint();
        if (end == null) return false;

        int dx = end.x - tx;
        int dy = end.y - ty;
        int dz = end.z - tz;

        return dx * dx + dy * dy + dz * dz <= 1;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.entityPathNavigate.noPath();
    }

    @Override
    public void startExecuting() {
        double dx = this.targetBlock.x + 0.5D - this.theGolem.posX;
        double dz = this.targetBlock.z + 0.5D - this.theGolem.posZ;
        float dist = MathHelper.sqrt(dx * dx + dz * dz);
        this.theGolem.motionX += dx / (double)dist * 1.0D * 0.8D + this.theGolem.motionX * 0.2D;
        this.theGolem.motionZ += dz / (double)dist * 1.0D * 0.8D + this.theGolem.motionZ * 0.2D;
        this.theGolem.motionY = 0.3D;
        this.entityPathNavigate.setPath(this.entityPathEntity, (double)this.nearSpeed);
    }

    @Override
    public void resetTask() {
        this.closestLivingEntity = null;
    }

    @Override
    public void updateTask() {
        this.entityPathNavigate.setSpeed(
            this.theGolem.getDistanceSq(this.closestLivingEntity) < 49.0D
                ? this.nearSpeed
                : this.farSpeed
        );
    }
}
