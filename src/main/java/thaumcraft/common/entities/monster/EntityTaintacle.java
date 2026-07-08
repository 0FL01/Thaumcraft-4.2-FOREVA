package thaumcraft.common.entities.monster;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockTaint;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintacle extends EntityMob implements ITaintedMob {
    public float flailIntensity = 1.0f;
    private int attackCooldown = 0;

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

    @Override
    public boolean getCanSpawnHere() {
        BlockPos pos = new BlockPos(
            MathHelper.floor(this.posX),
            MathHelper.floor(this.getEntityBoundingBox().minY),
            MathHelper.floor(this.posZ));
        List<EntityTaintacle> nearby = this.world.getEntitiesWithinAABB(
            EntityTaintacle.class,
            new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).grow(24.0D, 8.0D, 24.0D));
        return nearby.isEmpty()
            && (this.isValidTaintacleSpawnGround(pos) || this.isValidTaintacleSpawnGround(pos.down()))
            && super.getCanSpawnHere();
    }

    private boolean isValidTaintacleSpawnGround(BlockPos pos) {
        if (!this.isTaintBiome(pos)) {
            return false;
        }
        IBlockState state = this.world.getBlockState(pos);
        return state.getBlock() == ConfigBlocks.blockTaintFibres && state.getValue(BlockTaintFibres.TYPE) == 0
            || state.getBlock() == ConfigBlocks.blockTaint && state.getValue(BlockTaint.TYPE) == 1;
    }

    private boolean isTaintBiome(BlockPos pos) {
        Biome biome = this.world.getBiome(pos);
        return Biome.getIdForBiome(biome) == Config.biomeTaintID
            || biome == ThaumcraftWorldGenerator.biomeTaint
            || biome != null && ThaumcraftWorldGenerator.biomeTaint != null
            && Biome.getIdForBiome(biome) == Biome.getIdForBiome(ThaumcraftWorldGenerator.biomeTaint);
    }

    @Override
    public double getYOffset() {
        return 0.25D;
    }

    // --- Rooted movement: only vertical sink ---
    @Override
    public void move(MoverType type, double x, double y, double z) {
        x = 0.0; z = 0.0;
        if (y > 0.0) y = 0.0;
        super.move(type, x, y, z);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    // --- Per-tick behavior ---
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        // Biome damage
        if (!this.world.isRemote && this.ticksExisted % 20 == 0
                && !this.isTaintBiome(this.getPosition())) {
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
            if ((float) this.ticksExisted < this.height * 10.0f && this.onGround) {
                Thaumcraft.proxy.tentacleAriseFX(this);
            }
        }

        if (this.getAttackTarget() == null) {
            this.setAttackTarget(this.findNearestTarget());
        } else if (this.getAttackTarget().isEntityAlive() && this.getAgitationState()) {
            float dist = this.getDistance(this.getAttackTarget());
            if (!this.world.isRemote && this.canEntityBeSeen(this.getAttackTarget())) {
                this.attackTentacle(this.getAttackTarget(), dist);
            }
            this.faceEntity(this.getAttackTarget(), 5.0F);
        } else {
            this.setAttackTarget(null);
        }
    }

    protected void attackTentacle(Entity entity, float distance) {
        if (this.attackCooldown > 0) {
            return;
        }
        if (distance <= this.height
            && entity.getEntityBoundingBox().maxY > this.getEntityBoundingBox().minY
            && entity.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
            this.attackCooldown = 20;
            this.attackEntityAsMob(entity);
            this.playSound(TCSounds.TENTACLE, this.getSoundVolume(), this.getSoundPitch());
        } else if (distance > this.height && entity.onGround && !(this instanceof EntityTaintacleSmall)) {
            this.spawnTentacles(entity);
        }
    }

    // --- Combat ---
    @Override
    public boolean attackEntityAsMob(Entity entity) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int knockback = 0;
        if (entity instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entity).getCreatureAttribute());
            knockback += EnchantmentHelper.getKnockbackModifier(this);
        }
        boolean hit = entity.attackEntityFrom(DamageSourceThaumcraft.causeTentacleDamage(this), damage);
        if (hit) {
            if (knockback > 0) {
                entity.addVelocity(-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float) knockback * 0.5F, 0.1D,
                    MathHelper.cos(this.rotationYaw * 0.017453292F) * (float) knockback * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            int fire = EnchantmentHelper.getFireAspectModifier(this);
            if (fire > 0) {
                entity.setFire(fire * 4);
            }
            if (entity instanceof EntityLivingBase) {
                EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entity, this);
            }
            EnchantmentHelper.applyArthropodEnchantments(this, entity);
        }
        return hit;
    }

    protected void spawnTentacles(Entity entity) {
        int x = MathHelper.floor(entity.posX);
        int y = MathHelper.floor(entity.getEntityBoundingBox().minY);
        int z = MathHelper.floor(entity.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        boolean validSpawnSurface = false;
        if (this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeEldritchLands
            || this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeTaint) {
            if (this.world.getBlockState(pos).getMaterial() == Config.taintMaterial
                || this.world.getBlockState(pos.down()).getMaterial() == Config.taintMaterial) {
                validSpawnSurface = true;
            }
        }
        if (!validSpawnSurface && this.world.getBiome(pos) != ThaumcraftWorldGenerator.biomeEldritchLands) {
            return;
        }

        this.attackCooldown = 40 + this.world.rand.nextInt(20);
        EntityTaintacleSmall small = new EntityTaintacleSmall(this.world);
        small.setLocationAndAngles(
            entity.posX + this.world.rand.nextFloat() - this.world.rand.nextFloat(),
            entity.posY,
            entity.posZ + this.world.rand.nextFloat() - this.world.rand.nextFloat(),
            0.0F, 0.0F);
        this.world.spawnEntity(small);
        this.playSound(TCSounds.TENTACLE, this.getSoundVolume(), this.getSoundPitch());

        if (this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeEldritchLands
            && this.world.isAirBlock(pos)
            && BlockUtils.isAdjacentToSolidBlock(this.world, pos)) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);
            this.world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getDefaultState()
                .withProperty(BlockTaintFibres.TYPE, this.world.rand.nextInt(4) == 0 ? 1 : 0), 3);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!(this instanceof EntityTaintacleSmall)
            && source.getTrueSource() != null
            && this.getDistance(source.getTrueSource()) > 16.0F
            && !this.world.isRemote) {
            this.spawnTentacles(source.getTrueSource());
        }
        return super.attackEntityFrom(source, amount);
    }

    protected EntityLivingBase findNearestTarget() {
        EntityLivingBase nearest = null;
        double nearestDist = Double.MAX_VALUE;
        List<EntityLivingBase> candidates = this.world.getEntitiesWithinAABB(
            EntityLivingBase.class,
            new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)
                .grow(this.height * 6.0F, this.height * 3.0F, this.height * 6.0F));
        for (EntityLivingBase candidate : candidates) {
            if (!(candidate instanceof ITaintedMob)) {
                double d = candidate.getDistanceSq(this);
                if (d < nearestDist) {
                    nearestDist = d;
                    nearest = candidate;
                }
            }
        }
        return nearest;
    }

    public boolean getAgitationState() {
        return this.getAttackTarget() != null
            && this.getAttackTarget().getDistanceSq(this) < (double) (this.height * 7.0F * this.height * 7.0F);
    }

    public void faceEntity(Entity entity, float angle) {
        double dx = entity.posX - this.posX;
        double dz = entity.posZ - this.posZ;
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        this.rotationYaw = this.updateRotation(this.rotationYaw, yaw, angle);
    }

    protected float updateRotation(float current, float intended, float maxChange) {
        float delta = MathHelper.wrapDegrees(intended - current);
        if (delta > maxChange) {
            delta = maxChange;
        }
        if (delta < -maxChange) {
            delta = -maxChange;
        }
        return current + delta;
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
    @Override protected SoundEvent getAmbientSound() { return TCSounds.ROOTS; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return TCSounds.TENTACLE; }
    @Override protected SoundEvent getDeathSound() { return TCSounds.TENTACLE; }
    @Override protected float getSoundPitch() { return 1.3F - this.height / 10.0F; }
    @Override protected float getSoundVolume() { return this.height / 8.0f; }
}
