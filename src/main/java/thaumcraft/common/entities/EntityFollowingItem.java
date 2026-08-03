package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.Thaumcraft;

public class EntityFollowingItem extends EntitySpecialItem implements IEntityAdditionalSpawnData {
    private double targetX;
    private double targetY;
    private double targetZ;
    private int type = 3;
    private Entity target;
    private int slowdown = 20;
    public double gravity = 0.04D;

    public EntityFollowingItem(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack) {
        this(world);
        this.setPosition(x, y, z);
        this.setItem(stack);
        this.rotationYaw = (float) (Math.random() * 360.0D);
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack,
                               Entity target, int type) {
        this(world, x, y, z, stack);
        this.target = target;
        this.updateTargetPosition();
        this.type = type;
        this.noClip = true;
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack,
                               double targetX, double targetY, double targetZ) {
        this(world, x, y, z, stack);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    @Override
    public void onUpdate() {
        if (this.target != null) this.updateTargetPosition();
        if (this.targetX != 0.0D || this.targetY != 0.0D || this.targetZ != 0.0D) {
            float dx = (float) (this.targetX - this.posX);
            float dy = (float) (this.targetY - this.posY);
            float dz = (float) (this.targetZ - this.posZ);
            if (this.slowdown > 1) --this.slowdown;
            double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 0.5D) {
                distance *= this.slowdown;
                this.motionX = dx / distance;
                this.motionY = dy / distance;
                this.motionZ = dz / distance;
            } else {
                this.motionX *= 0.1D;
                this.motionY *= 0.1D;
                this.motionZ *= 0.1D;
                this.targetX = 0.0D;
                this.targetY = 0.0D;
                this.targetZ = 0.0D;
                this.target = null;
                this.noClip = false;
            }
            if (this.world.isRemote) {
                float x = (float) this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                float y = (float) this.prevPosY + this.height / 2.0F
                        + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                float z = (float) this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                if (this.type != 10) {
                    Thaumcraft.proxy.sparkle(x, y, z, 1.0F, this.type, 0.0F);
                } else {
                    Thaumcraft.proxy.crucibleBubble(this.world, x, y, z, 0.33F, 0.33F, 1.0F);
                }
            }
        } else {
            this.motionY -= this.gravity;
        }
        super.onUpdate();
    }

    private void updateTargetPosition() {
        this.targetX = this.target.posX;
        this.targetY = this.target.getEntityBoundingBox().minY + this.target.height / 2.0F;
        this.targetZ = this.target.posZ;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setShort("type", (short) this.type);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.type = compound.getShort("type");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(this.target == null ? -1 : this.target.getEntityId());
        data.writeDouble(this.targetX);
        data.writeDouble(this.targetY);
        data.writeDouble(this.targetZ);
        data.writeByte(this.type);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        int entityId = data.readInt();
        if (entityId > -1) this.target = this.world.getEntityByID(entityId);
        this.targetX = data.readDouble();
        this.targetY = data.readDouble();
        this.targetZ = data.readDouble();
        this.type = data.readByte();
    }
}
