package thaumcraft.common.entities.monster;

public class EntityInhabitedZombie extends net.minecraft.entity.monster.EntityZombie {
    public EntityInhabitedZombie(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    @Override
    public void onDeathUpdate() {
        if (!this.world.isRemote && this.deathTime == 0) {
            // TODO: spawn EntityEldritchCrab on death
        }
        super.onDeathUpdate();
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {}
    @Override public void onKillEntity(net.minecraft.entity.EntityLivingBase entity) {}
}
