package thaumcraft.common.entities.monster;

public class EntityThaumicSlime extends net.minecraft.entity.monster.EntityMob implements thaumcraft.api.entities.ITaintedMob {
    public EntityThaumicSlime(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: slime split mechanics
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
