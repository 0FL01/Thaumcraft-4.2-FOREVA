package thaumcraft.common.entities.monster.boss;

public class EntityThaumcraftBoss extends net.minecraft.entity.monster.EntityMob {
    public EntityThaumcraftBoss(net.minecraft.world.World world) { super(world); }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (amount > 35.0f) amount = 35.0f;
        if (amount > 0) {
            // TODO: enrage mechanics
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: boss scaling per player count
    }

    @Override public boolean isNonBoss() { return false; }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
