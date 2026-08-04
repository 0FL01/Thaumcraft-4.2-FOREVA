package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPrimalArrow extends EntityArrow implements IProjectile, IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> ARROW_TYPE =
        EntityDataManager.createKey(EntityPrimalArrow.class, DataSerializers.VARINT);
    private int shootingEntityId = -1;
    private int primalKnockbackStrength;

    public EntityPrimalArrow(World world) {
        super(world);
        this.configureArrow(0.5F);
    }

    public EntityPrimalArrow(World world, EntityLivingBase shooter) {
        super(world, shooter);
        this.shootingEntityId = shooter.getEntityId();
        this.configureArrow(0.5F);
    }

    public EntityPrimalArrow(World world, double x, double y, double z) {
        super(world);
        this.configureArrow(0.25F);
        this.setPosition(x, y, z);
    }

    public EntityPrimalArrow(World world, EntityLivingBase shooter, float velocity, int type) {
        super(world);
        this.shootingEntity = shooter;
        this.shootingEntityId = shooter.getEntityId();
        this.setArrowType(type);
        this.configureArrow(0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * 0.16F);
        this.posY -= 0.10000000014901161D;
        this.posZ -= (double) (MathHelper.sin(this.rotationYaw * 0.017453292F) * 0.16F);
        Vec3d look = shooter.getLookVec();
        this.posX += look.x;
        this.posY += look.y;
        this.posZ += look.z;
        this.setPosition(this.posX, this.posY, this.posZ);
        float yaw = shooter.rotationYaw * 0.017453292F;
        float pitch = shooter.rotationPitch * 0.017453292F;
        this.shoot(-MathHelper.sin(yaw) * MathHelper.cos(pitch), -MathHelper.sin(pitch),
                MathHelper.cos(yaw) * MathHelper.cos(pitch), velocity * 1.5F, 1.0F);
    }

    private void configureArrow(float size) {
        this.setSize(size, size);
        this.setDamage(2.1D);
        this.pickupStatus = PickupStatus.DISALLOWED;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ARROW_TYPE, 0);
    }

    public int getArrowType() { return this.dataManager.get(ARROW_TYPE); }
    public void setArrowType(int type) { this.dataManager.set(ARROW_TYPE, type); }

    @Override
    protected ItemStack getArrowStack() { return ItemStack.EMPTY; }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result == null) return;
        if (result.entityHit == null) {
            super.onHit(result);
            return;
        }
        this.inflictPrimalDamage(result.entityHit);
    }

    private void inflictPrimalDamage(Entity target) {
        float speed = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        int damage = MathHelper.ceil((double) speed * this.getDamage());
        if (this.getIsCritical()) damage += this.rand.nextInt(damage / 2 + 2);

        int type = this.getArrowType();
        int fire = this.isBurning() && type != 2 ? 5 : 0;
        Entity owner = this.shootingEntity == null ? this : this.shootingEntity;
        DamageSource source;
        switch (type) {
            case 0:
                source = new EntityDamageSourceIndirect("airarrow", this, owner)
                        .setDamageBypassesArmor().setMagicDamage().setProjectile();
                break;
            case 1:
                fire += 5;
                source = new EntityDamageSourceIndirect("firearrow", this, owner)
                        .setFireDamage().setProjectile();
                break;
            case 4:
                source = new EntityDamageSourceIndirect("orderarrow", this, owner)
                        .setDamageBypassesArmor().setMagicDamage().setProjectile();
                if (target instanceof EntityLivingBase) {
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 4));
                }
                break;
            case 2:
                if (target instanceof EntityLivingBase) {
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
                }
                source = DamageSource.causeArrowDamage(this, owner);
                break;
            case 5:
                if (target instanceof EntityLivingBase) {
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 0));
                }
                source = DamageSource.causeArrowDamage(this, owner);
                break;
            default:
                source = DamageSource.causeArrowDamage(this, owner);
        }

        if (fire > 0 && !(target instanceof EntityEnderman)) target.setFire(fire);
        if (target.attackEntityFrom(source, damage)) {
            if (target instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) target;
                float horizontalSpeed = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (this.primalKnockbackStrength > 0 && horizontalSpeed > 0.0F) {
                    living.addVelocity(this.motionX * this.primalKnockbackStrength * 0.6D / horizontalSpeed,
                            0.1D, this.motionZ * this.primalKnockbackStrength * 0.6D / horizontalSpeed);
                }
                if (this.shootingEntity instanceof EntityLivingBase) {
                    EnchantmentHelper.applyThornEnchantments(living, this.shootingEntity);
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, living);
                }
                if (this.shootingEntity != null && living != this.shootingEntity
                        && living instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                }
            }
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(target instanceof EntityEnderman)) this.setDead();
        } else {
            this.motionX *= -0.1D;
            this.motionY *= -0.1D;
            this.motionZ *= -0.1D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
        }
    }

    @Override
    public double getDamage() {
        double damage = super.getDamage();
        int type = this.getArrowType();
        if (type == 3) return damage * 1.5D;
        if (type == 4 || type == 5) return damage * 0.8D;
        return damage;
    }

    @Override
    public void setKnockbackStrength(int knockbackStrength) {
        super.setKnockbackStrength(knockbackStrength);
        this.primalKnockbackStrength = knockbackStrength;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.inGround && this.timeInGround >= 100) {
            this.setDead();
            return;
        }
        if (!this.isDead && !this.inGround && this.isInWater()) {
            this.motionX *= 4.0D / 3.0D;
            this.motionY = this.motionY * 4.0D / 3.0D + 1.0D / 60.0D;
            this.motionZ *= 4.0D / 3.0D;
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return super.isInRangeToRenderDist(distance / 100.0D);
    }

    public int getPrimalTimeInGround() {
        return this.timeInGround;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("arrowType", this.getArrowType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setArrowType(nbt.getInteger("arrowType"));
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
        buf.writeFloat(this.rotationYaw);
        buf.writeFloat(this.rotationPitch);
        buf.writeByte(this.getArrowType());
        buf.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : this.shootingEntityId);
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
        this.rotationYaw = buf.readFloat();
        this.rotationPitch = buf.readFloat();
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.setArrowType(buf.readByte());
        this.shootingEntityId = buf.readInt();
        try {
            Entity entity = this.world != null ? this.world.getEntityByID(this.shootingEntityId) : null;
            if (entity instanceof EntityLivingBase) {
                this.shootingEntity = (EntityLivingBase) entity;
            }
        } catch (Exception ignored) {
        }
    }
}
