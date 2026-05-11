package thaumcraft.common.entities.monster.boss;

public class EntityTaintacleGiant extends thaumcraft.common.entities.monster.EntityTaintacle implements thaumcraft.api.entities.ITaintedMob {
    public EntityTaintacleGiant(net.minecraft.world.World world) {
        super(world);
        this.setSize(1.5f, 5.0f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0);
    }
}
