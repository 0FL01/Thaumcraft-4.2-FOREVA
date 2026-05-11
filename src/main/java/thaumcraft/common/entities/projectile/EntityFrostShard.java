package thaumcraft.common.entities.projectile;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class EntityFrostShard extends net.minecraft.entity.projectile.EntityThrowable implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
    public double bounce = 0.5;
    public int bounceLimit = 3;
    public boolean fragile = false;

    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityFrostShard.class, DataSerializers.FLOAT);
    private static final DataParameter<Byte> FROSTY = EntityDataManager.createKey(EntityFrostShard.class, DataSerializers.BYTE);

    public EntityFrostShard(net.minecraft.world.World world) { super(world); }
    public EntityFrostShard(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter, float scatter) {
        super(world, shooter);
        this.shoot(this.motionX, this.motionY, this.motionZ, this.getGravityVelocity(), scatter);
    }

    @Override
    protected float getGravityVelocity() { return this.fragile ? 0.015f : 0.05f; }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DAMAGE, 0.0f);
        this.dataManager.register(FROSTY, (byte) 0);
    }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        this.setDead();
    }

    @Override public void readSpawnData(io.netty.buffer.ByteBuf buf) {}
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf buf) {}

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.readEntityFromNBT(nbt); }
    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) { super.writeEntityToNBT(nbt); }
}