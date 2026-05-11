package thaumcraft.common.entities.monster.boss;

public class EntityEldritchWarden extends thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public static final String[] TITLES = {"Warden of the Outer Spheres","Herald of the Unseen","Voice of the Deep","Keeper of the Final Gate","Scribe of the Endless Tome","Bearer of the Final Truth","Watcher at the Threshold","Guardian of the Forgotten Path","Whisperer in the Void","Seeker of the Hidden Flame"};

    public EntityEldritchWarden(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(180.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }

    @Override
    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target, float distance) {
        // TODO: fire EldritchOrb
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: field frenzy at 0 HP, teleport home, screech
    }
}
