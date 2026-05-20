package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

public class EntityEldritchOrb extends EntityThrowable {
    public EntityEldritchOrb(World world) { super(world); }
    public EntityEldritchOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityEldritchOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.0f; }

    @Override
    public float getCollisionBorderSize() { return 0.1F; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 100) this.setDead();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote && this.getThrower() != null) {
            // AOE damage (2-block radius) excluding thrower
            List<Entity> ents = this.world.getEntitiesWithinAABBExcludingEntity(
                this.getThrower(),
                this.getEntityBoundingBox().grow(2.0, 2.0, 2.0));
            float baseDamage = (float)this.getThrower()
                .getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.666f;
            for (Entity entity : ents) {
                if (!(entity instanceof EntityLivingBase)
                    || ((EntityLivingBase)entity).isEntityUndead())
                    continue;
                entity.attackEntityFrom(
                    DamageSource.causeIndirectDamage(this, this.getThrower()),
                    baseDamage);
                ((EntityLivingBase)entity).addPotionEffect(
                    new PotionEffect(MobEffects.WITHER, 160, 0));
            }
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE,
                    0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
            this.ticksExisted = 100; // triggers death next tick
            this.world.setEntityState(this, (byte) 16);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            if (this.world.isRemote) {
                for (int i = 0; i < 30; i++) {
                    float offsetX = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                    float offsetY = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                    float offsetZ = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                    Thaumcraft.proxy.wispFX3(
                            this.world,
                            this.posX + offsetX,
                            this.posY + offsetY,
                            this.posZ + offsetZ,
                            this.posX + offsetX * 8.0F,
                            this.posY + offsetY * 8.0F,
                            this.posZ + offsetZ * 8.0F,
                            0.3F,
                            5,
                            true,
                            0.02F);
                }
            }
            return;
        }
        super.handleStatusUpdate(id);
    }
}
