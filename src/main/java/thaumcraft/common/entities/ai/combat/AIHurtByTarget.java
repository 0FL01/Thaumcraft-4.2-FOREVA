package thaumcraft.common.entities.ai.combat;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class AIHurtByTarget extends AITarget {

    boolean field_75312_a;
    EntityLivingBase entityPathNavigate;

    public AIHurtByTarget(EntityCreature taskOwner, boolean par2) {
        super(taskOwner, 16.0f, false);
        this.field_75312_a = par2;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return this.isSuitableTarget(this.taskOwner.getRevengeTarget(), false);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.taskOwner.getRevengeTarget() != null && this.taskOwner.getRevengeTarget() != this.entityPathNavigate;
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
        if (this.field_75312_a) {
            double x = this.taskOwner.posX;
            double y = this.taskOwner.posY;
            double z = this.taskOwner.posZ;
            AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).grow(this.targetDistance, 4.0D, this.targetDistance);
            List<EntityLiving> list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), aabb);
            for (EntityLiving entity : list) {
                if (this.taskOwner == entity || entity.getAttackTarget() != null) continue;
                entity.setAttackTarget(this.taskOwner.getRevengeTarget());
            }
        }
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        if (this.taskOwner.getAttackTarget() != null && this.taskOwner.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.taskOwner.getAttackTarget()).capabilities.disableDamage) {
            this.taskOwner.setAttackTarget(null);
            super.resetTask();
        }
    }
}
