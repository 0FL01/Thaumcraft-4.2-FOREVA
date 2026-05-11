package thaumcraft.common.entities.monster;

public class EntityCultist extends net.minecraft.entity.monster.EntityMob {
    private int homeX, homeY, homeZ;

    public EntityCultist(net.minecraft.world.World world) { super(world); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(8.0);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.homeX = nbt.getInteger("homeX");
        this.homeY = nbt.getInteger("homeY");
        this.homeZ = nbt.getInteger("homeZ");
    }

    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("homeX", this.homeX);
        nbt.setInteger("homeY", this.homeY);
        nbt.setInteger("homeZ", this.homeZ);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        // drops handled by subclass
    }

    @Override public net.minecraft.util.SoundEvent getAmbientSound() { return null; }
    @Override protected net.minecraft.util.SoundEvent getHurtSound(net.minecraft.util.DamageSource src) { return null; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return null; }
}
