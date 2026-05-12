package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;

public class EntityCultistLeader extends EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob {
    public static final String[] NAMES = {"Alberic","Baldric","Cedric","Drystan","Edric","Fendrel","Gawain","Hedric","Isembard","Joram","Kendric","Lorcan","Merrick","Percival","Zelipe"};

    public EntityCultistLeader(net.minecraft.world.World world) {
        super(world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 16.0, 1.0, 30, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new AICultistHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        // TODO: equip crimson void sword + cultist plate armor
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        // TODO: fire GolemOrb
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        // TODO: give nearby cultists strength II
    }
}
