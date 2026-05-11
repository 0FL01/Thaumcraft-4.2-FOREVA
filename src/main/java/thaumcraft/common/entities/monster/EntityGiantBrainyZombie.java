package thaumcraft.common.entities.monster;

public class EntityGiantBrainyZombie extends thaumcraft.common.entities.monster.EntityBrainyZombie {
    public EntityGiantBrainyZombie(net.minecraft.world.World world) {
        super(world);
        this.setSize(1.0f, 2.5f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    }
}
