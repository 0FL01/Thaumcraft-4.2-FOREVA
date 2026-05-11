package thaumcraft.common.entities.monster.boss;

public class EntityCultistPortal extends net.minecraft.entity.monster.EntityMob {
    public EntityCultistPortal(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: spawn cultists
    }
}
