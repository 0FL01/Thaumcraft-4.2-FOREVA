package thaumcraft.common.entities.golems;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.Thaumcraft;

/**
 * Golem fishing bobber — projectile entity cast by EntityGolemBase fishermen.
 * Bobs in water, waits for bites, triggers splash particles.
 */
public class EntityGolemBobber extends Entity implements IEntityAdditionalSpawnData {
    public static final byte STATUS_SPLASH_AMBIENT = 16;
    public static final byte STATUS_SPLASH_NIBBLE = 17;
    public static final byte STATUS_SPLASH_CATCH = 18;

    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private int inData = 0;
    private boolean inGround = false;
    private boolean inBlock = false;
    public EntityGolemBase fisher = null;
    private int serverCatchTimer;
    private int fishTimer;
    private float fishAngle;

    public EntityGolemBobber(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.isImmuneToFire = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    public EntityGolemBobber(World world, EntityGolemBase golem, int x, int y, int z) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.fisher = golem;
        this.isImmuneToFire = true;

        double dx = (double) x + 0.5D - golem.posX;
        double dy = (double) (y + 1) - golem.posY;
        double dz = (double) z + 0.5D - golem.posZ;
        double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = 0.1D;

        this.motionX = dx * speed;
        this.motionY = dy * speed + MathHelper.sqrt(dist) * 0.08D;
        this.motionZ = dz * speed;

        this.setPosition(golem.posX, golem.posY, golem.posZ);
    }

    @Override
    protected void entityInit() {}

    @Override
    public boolean isInRangeToRenderDist(double dist) {
        double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
        if (Double.isNaN(d)) d = 4.0D;
        return dist < (d *= 64.0D) * d;
    }

    @Override
    public float getEyeHeight() { return 0.0F; }

    // ------------------------------------------------------------------
    // Main update — bobber physics + fishing timer
    // ------------------------------------------------------------------

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > 4000) {
            this.setDead();
            return;
        }

        // Server: check fisher validity
        if (!this.world.isRemote) {
            if (this.fisher == null || !this.fisher.isEntityAlive() || this.fisher.world != this.world) {
                this.setDead();
                return;
            }
            // Ambient splash particles (server only)
            if (this.rand.nextFloat() < 0.02F) {
                this.world.setEntityState(this, STATUS_SPLASH_AMBIENT);
            }
        }

        // Block collision reset
        if (this.inBlock) {
            this.inBlock = false;
            this.motionX *= this.rand.nextFloat() * 0.2F;
            this.motionY *= this.rand.nextFloat() * 0.2F;
            this.motionZ *= this.rand.nextFloat() * 0.2F;
        }

        // Raytrace for block hit
        Vec3d start = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d end = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult result = this.world.rayTraceBlocks(start, end);

        start = new Vec3d(this.posX, this.posY, this.posZ);
        end = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (result != null) {
            end = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);
            if (result.entityHit == null) {
                this.inBlock = true;
                if (this.world.getBlockState(result.getBlockPos()).getMaterial() != Material.WATER) {
                    this.setDead();
                }
            }
        }

        if (!this.inBlock) {
            // Move
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            // Update rotation from velocity
            float hSpeed = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.rotationPitch = (float) (MathHelper.atan2(this.motionY, hSpeed) * 180.0D / Math.PI);

            // Smooth rotation
            while (this.rotationPitch - this.prevRotationPitch < -180.0F) this.prevRotationPitch -= 360.0F;
            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) this.prevRotationPitch += 360.0F;
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) this.prevRotationYaw -= 360.0F;
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) this.prevRotationYaw += 360.0F;
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

            // Drag
            float drag = 0.92F;
            if (this.onGround || this.collidedHorizontally) {
                drag = 0.5F;
            }

            // Water detection via volume sampling
            int samples = 5;
            double waterVolume = 0.0D;
            for (int j = 0; j < samples; j++) {
                double y1 = this.getEntityBoundingBox().minY
                    + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY)
                    * (double) (j + 0) / (double) samples - 0.125D + 0.125D;
                double y2 = this.getEntityBoundingBox().minY
                    + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY)
                    * (double) (j + 1) / (double) samples - 0.125D + 0.125D;
                AxisAlignedBB slice = new AxisAlignedBB(
                    this.getEntityBoundingBox().minX, y1, this.getEntityBoundingBox().minZ,
                    this.getEntityBoundingBox().maxX, y2, this.getEntityBoundingBox().maxZ);
                if (this.world.isMaterialInBB(slice, Material.WATER)) {
                    waterVolume += 1.0D / (double) samples;
                }
            }

            // Fishing logic (server only)
            if (!this.world.isRemote && waterVolume > 0.0D) {
                int biteChance = 1;

                if (this.rand.nextFloat() < 0.25F
                    && this.world.canBlockSeeSky(new BlockPos(this).up())) {
                    biteChance = 2;
                }
                if (this.rand.nextFloat() < 0.5F
                    && !this.world.isDaytime()) {
                    biteChance--;
                }
                if (biteChance < 1) biteChance = 1;

                if (this.serverCatchTimer > 0) {
                    this.serverCatchTimer--;
                    if (this.serverCatchTimer <= 0) {
                        this.fishTimer = 0;
                    }
                } else if (this.fishTimer > 0) {
                    this.fishTimer -= biteChance;
                    float nibbleChance = 0.15F;
                    if (this.fishTimer < 20) {
                        nibbleChance = (float) ((double) nibbleChance + (double) (20 - this.fishTimer) * 0.05D);
                    } else if (this.fishTimer < 40) {
                        nibbleChance = (float) ((double) nibbleChance + (double) (40 - this.fishTimer) * 0.02D);
                    } else if (this.fishTimer < 60) {
                        nibbleChance = (float) ((double) nibbleChance + (double) (60 - this.fishTimer) * 0.01D);
                    }
                    if (this.rand.nextFloat() < nibbleChance) {
                        this.world.setEntityState(this, STATUS_SPLASH_NIBBLE);
                    }
                    if (this.fishTimer <= 0) {
                        this.fishAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
                    }
                }
                if (this.serverCatchTimer > 0) {
                    this.motionY -= (double) (this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
                }
            }

            // Buoyancy
            double buoyancy = waterVolume * 2.0D - 1.0D;
            this.motionY += 0.04F * buoyancy;
            if (waterVolume > 0.0D) {
                drag = (float) ((double) drag * 0.9D);
                this.motionY *= 0.8D;
            }

            // Apply drag
            this.motionX *= drag;
            this.motionY *= drag;
            this.motionZ *= drag;

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == STATUS_SPLASH_AMBIENT) {
            Thaumcraft.proxy.golemFishingSplashFX(this, 0);
            return;
        }
        if (id == STATUS_SPLASH_NIBBLE) {
            Thaumcraft.proxy.golemFishingSplashFX(this, 1);
            return;
        }
        if (id == STATUS_SPLASH_CATCH) {
            Thaumcraft.proxy.golemFishingSplashFX(this, 2);
            return;
        }
        super.handleStatusUpdate(id);
    }

    // ------------------------------------------------------------------
    // NBT (minimal — bobber state is transient)
    // ------------------------------------------------------------------

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {}

    // ------------------------------------------------------------------
    // Spawn data
    // ------------------------------------------------------------------

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
        buf.writeInt(this.fisher != null ? this.fisher.getEntityId() : -1);
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
        int fid = buf.readInt();
        if (fid >= 0) {
            Entity entity = this.world.getEntityByID(fid);
            if (entity instanceof EntityGolemBase && entity.world == this.world) {
                this.fisher = (EntityGolemBase) entity;
            }
        }
    }
}
