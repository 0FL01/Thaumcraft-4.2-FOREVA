package thaumcraft.common.entities.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintacle extends EntityMob implements ITaintedMob {
    public float flailIntensity = 1.0f;

    public EntityTaintacle(World world) {
        super(world);
        this.setSize(0.66f, 3.0f);
        this.experienceValue = 10;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
    }

    // --- Rooted movement: only vertical sink ---
    @Override
    public void move(MoverType type, double x, double y, double z) {
        x = 0.0; z = 0.0;
        if (y > 0.0) y = 0.0;
        super.move(type, x, y, z);
    }

    // --- Manual targeting + face toward target ---
    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        if (this.getAttackTarget() != null) {
            this.faceEntity(this.getAttackTarget(), 5.0f, 5.0f);
        }
    }

    // --- Per-tick behavior ---
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        // Biome damage
        if (!this.world.isRemote && this.ticksExisted % 20 == 0
                && this.world.getBiome(this.getPosition()) != ThaumcraftWorldGenerator.biomeTaint) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0f);
        }
        // Client flail animation
        if (this.world.isRemote) {
            boolean agitated = this.hurtTime > 0 || this.getAttackTarget() != null
                && this.getDistance(this.getAttackTarget()) < this.height;
            if ((float)this.ticksExisted > this.height * 10.0f && agitated) {
                if (this.flailIntensity < 3.0f) this.flailIntensity += 0.2f;
            } else if (this.flailIntensity > 1.0f) {
                this.flailIntensity -= 0.2f;
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // Client arise FX on spawn
        if (this.world.isRemote && (float)this.ticksExisted < this.height * 10.0f && this.onGround) {
            Thaumcraft.proxy.burst(this.world, this.posX, this.posY, this.posZ, 1.0f);
        }
    }

    // --- Combat ---
    @Override
    public boolean attackEntityAsMob(Entity entity) {
        float dmg = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        if (entity.attackEntityFrom(DamageSourceThaumcraft.causeTentacleDamage(this), dmg)) {
            SoundEvent sound = SoundEvent.REGISTRY.getObject(new ResourceLocation("thaumcraft:tentacle"));
            if (sound != null) this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
            return true;
        }
        return false;
    }

    // --- Spawning ---
    @Override
    public boolean getCanSpawnHere() {
        if (this.world.getBiome(this.getPosition()) != ThaumcraftWorldGenerator.biomeTaint)
            return false;
        return super.getCanSpawnHere();
    }

    // --- Drops ---
    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        ItemStack drop = this.world.rand.nextBoolean()
            ? new ItemStack(ConfigItems.itemResource, 1, 11)
            : new ItemStack(ConfigItems.itemResource, 1, 12);
        this.entityDropItem(drop, this.height / 2.0f);
        super.dropFewItems(wasRecentlyHit, looting);
    }

    @Override
    public int getMaxSpawnedInChunk() { return 200; }

    // --- Sounds ---
    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return null; }
    @Override protected SoundEvent getDeathSound() { return null; }
    @Override protected float getSoundVolume() { return 1.3f - this.height / 10.0f; }
}
