package thaumcraft.common.entities.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumParticleTypes;

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
            EntityEldritchCrab crab = new EntityEldritchCrab(this.world);
            crab.setLocationAndAngles(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ, this.rotationYaw, this.rotationPitch);
            crab.setHelm(true);
            this.world.spawnEntity(crab);
            if ((this.recentlyHit > 0 || this.isPlayer()) && this.world.getGameRules().getBoolean("doMobLoot")) {
                int xp = this.getExperiencePoints(this.attackingPlayer);
                while (xp > 0) {
                    int split = EntityXPOrb.getXPSplit(xp);
                    xp -= split;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, split));
                }
            }
        }
        for (int i = 0; i < 20; ++i) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
                    this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.posY + (double) (this.rand.nextFloat() * this.height),
                    this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D,
                    this.rand.nextGaussian() * 0.02D);
        }
        super.onDeathUpdate();
    }

    @Override protected void dropFewItems(boolean wasRecentlyHit, int looting) {}
    @Override public void onKillEntity(net.minecraft.entity.EntityLivingBase entity) {}
}
