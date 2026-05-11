package thaumcraft.common.entities.monster.boss;

public class EntityCultistLeader extends thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob {
    public static final String[] NAMES = {"Alberic","Baldric","Cedric","Drystan","Edric","Fendrel","Gawain","Hedric","Isembard","Joram","Kendric","Lorcan","Merrick","Percival","Zelipe"};

    public EntityCultistLeader(net.minecraft.world.World world) {
        super(world);
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
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
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
