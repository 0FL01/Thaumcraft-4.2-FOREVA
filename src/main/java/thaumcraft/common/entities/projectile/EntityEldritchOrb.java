package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEldritchOrb extends EntityThrowable {
    public EntityEldritchOrb(World world) { super(world); }
    public EntityEldritchOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityEldritchOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.0f; }

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
            this.world.playEvent(null, 2001, this.getPosition(), 0);
            this.ticksExisted = 100; // triggers death next tick
        }
        this.setDead();
    }
}
