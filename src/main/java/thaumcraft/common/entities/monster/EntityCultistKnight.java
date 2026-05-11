package thaumcraft.common.entities.monster;

public class EntityCultistKnight extends thaumcraft.common.entities.monster.EntityCultist {
    public EntityCultistKnight(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ARMOR).setBaseValue(6.0);
    }
}
