package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

    public EntityPrimalArrow(World world) { super(world); }
    public EntityPrimalArrow(World world, EntityLivingBase shooter) {
        super(world, shooter);
        this.shootingEntityId = shooter.getEntityId();
    }
    public EntityPrimalArrow(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
    }
    public EntityPrimalArrow(World world, EntityLivingBase shooter, float velocity, int type) {
        super(world);
        this.shootingEntity = shooter;
        this.shootingEntityId = shooter.getEntityId();
        this.setArrowType(type);
        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double) (MathHelper.cos(this.rotationYaw * 0.017453292F) * 0.16F);
        this.posY -= 0.10000000014901161D;
        this.posZ -= (double) (MathHelper.sin(this.rotationYaw * 0.017453292F) * 0.16F);
        Vec3d look = shooter.getLookVec();
        this.posX += look.x;
        this.posY += look.y;
        this.posZ += look.z;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, velocity * 1.5F, 1.0F);
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
        // Let EntityArrow handle physical damage + sticking first
        super.onHit(result);

        // Apply primal aspect effects if we hit a living entity
        if (!this.world.isRemote && result.typeOfHit == RayTraceResult.Type.ENTITY
                && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            int type = this.getArrowType();
            switch (type) {
                case 0: // Air — unblockable magic damage
                    target.attackEntityFrom(
                        new EntityDamageSourceIndirect("indirectMagic", this, this.shootingEntity)
                            .setDamageBypassesArmor().setMagicDamage().setProjectile(), 1.0f);
                    break;
                case 1: // Fire — fire damage + ignite
                    target.setFire(5);
                    target.attackEntityFrom(
                        new EntityDamageSourceIndirect("firearrow", this, this.shootingEntity)
                            .setFireDamage().setProjectile(), 1.0f);
                    break;
                case 2: // Water — slowness
                    target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
                    break;
                case 3: // Earth — bonus damage already handled by super.onHit with 1.5x speed
                    break;
                case 4: // Order — weakness
                    target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 4));
                    break;
                case 5: // Entropy — wither
                    target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 0));
                    break;
            }
        }
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
