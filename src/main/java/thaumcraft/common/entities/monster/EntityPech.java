package thaumcraft.common.entities.monster;

public class EntityPech extends net.minecraft.entity.monster.EntityMob implements net.minecraft.entity.IRangedAttackMob {
    public EntityPech(net.minecraft.world.World world) { super(world); }

    @Override public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {}
    @Override public void setSwingingArms(boolean swinging) {}
}
