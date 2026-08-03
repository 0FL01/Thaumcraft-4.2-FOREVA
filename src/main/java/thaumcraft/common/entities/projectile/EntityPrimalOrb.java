package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityPrimalOrb extends EntityThrowable implements IEntityAdditionalSpawnData {
    private int count = 0;
    boolean seeker = false;
    int oi = 0; // owner entity ID

    public EntityPrimalOrb(World world) { super(world); }
    public EntityPrimalOrb(World world, EntityLivingBase shooter, boolean seeker) {
        super(world, shooter);
        this.seeker = seeker;
        this.oi = shooter.getEntityId();
        this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 0.5F, 1.0F);
    }

    @Override
    protected float getGravityVelocity() { return 0.001f; }

    @Override
    public float getCollisionBorderSize() { return 0.1F; }

    @Override
    public void onUpdate() {
        ++this.count;
        if (this.isInsideOfMaterial(Material.WATER)) {
            this.onImpact(new RayTraceResult(this));
            if (this.isDead) {
                return;
            }
        }
        if (this.world.isRemote) {
            for (int a = 0; a < 6; ++a) {
                Thaumcraft.proxy.wispFX4(
                        this.world,
                        (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                        (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                        (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                        this, a, true, 0.0F);
            }
            Thaumcraft.proxy.wispFX2(
                    this.world,
                    this.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                    this.posY + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                    this.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F,
                    0.1F, this.rand.nextInt(6), true, true, 0.0F);
        }
        if (this.ticksExisted > 20) {
            Random seeded = new Random(this.getEntityId() + this.count);
            if (!this.seeker) {
                this.motionX += (double) ((seeded.nextFloat() - seeded.nextFloat()) * 0.01F);
                this.motionY += (double) ((seeded.nextFloat() - seeded.nextFloat()) * 0.01F);
                this.motionZ += (double) ((seeded.nextFloat() - seeded.nextFloat()) * 0.01F);
            } else {
                EntityLivingBase target = this.findSeekerTarget();
                if (target != null) {
                    double d = this.getDistanceSq(target);
                    if (d > 0.01D) {
                        double dx = target.posX - this.posX;
                        double dy = target.getEntityBoundingBox().minY + (double) target.height * 0.9D - this.posY;
                        double dz = target.posZ - this.posZ;
                        double accel = 0.2D;
                        this.motionX += dx / d * accel;
                        this.motionY += dy / d * accel;
                        this.motionZ += dz / d * accel;
                        this.motionX = MathHelper.clamp(this.motionX, -0.2D, 0.2D);
                        this.motionY = MathHelper.clamp(this.motionY, -0.2D, 0.2D);
                        this.motionZ = MathHelper.clamp(this.motionZ, -0.2D, 0.2D);
                    }
                }
            }
        }
        super.onUpdate();
        if (this.ticksExisted > 5000) { this.setDead(); }
    }

    private EntityLivingBase findSeekerTarget() {
        EntityLivingBase target = null;
        double closest = Double.MAX_VALUE;
        AxisAlignedBB search = this.getEntityBoundingBox().grow(16.0D);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, search);
        for (EntityLivingBase entity : entities) {
            if (entity.getEntityId() == this.oi || entity.isDead) {
                continue;
            }
            double distance = this.getDistanceSq(entity);
            if (distance < closest) {
                closest = distance;
                target = entity;
            }
        }
        return target;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (this.world.isRemote) {
            for (int a = 0; a < 6; ++a) {
                for (int type = 0; type < 6; ++type) {
                    float offsetX = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    float offsetY = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    float offsetZ = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.5F;
                    Thaumcraft.proxy.wispFX3(
                            this.world,
                            this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ,
                            this.posX + offsetX * 10.0F, this.posY + offsetY * 10.0F, this.posZ + offsetZ * 10.0F,
                            0.4F, type, true, 0.05F);
                }
            }
        } else {
            float explosiveRadius = 2.0f;
            float specialChance = 1.0f;
            if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.isInsideOfMaterial(Material.WATER)) {
                explosiveRadius = 4.0f;
                specialChance = 10.0f;
            }
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, explosiveRadius, true);
            if (!this.seeker && (float)this.rand.nextInt(100) <= specialChance) {
                if (this.rand.nextBoolean()) {
                    this.taintSplosion();
                } else {
                    BlockPos impactPos = result.getBlockPos();
                    if (impactPos == null && result.hitVec != null) {
                        impactPos = new BlockPos(result.hitVec);
                    }
                    if (impactPos != null) {
                        ThaumcraftWorldGenerator.createRandomNodeAt(
                                this.world, impactPos, this.rand, false, false, true);
                    }
                }
            }
            this.setDead();
        }
    }

    private void taintSplosion() {
        int x = (int) this.posX;
        int y = (int) this.posY;
        int z = (int) this.posZ;
        for (int a = 0; a < 10; ++a) {
            int xx = x + (int)(this.rand.nextFloat() - this.rand.nextFloat() * 6.0f);
            int zz = z + (int)(this.rand.nextFloat() - this.rand.nextFloat() * 6.0f);
            if (!this.rand.nextBoolean()
                || this.world.getBiome(new BlockPos(xx, 0, zz)) == ThaumcraftWorldGenerator.biomeTaint)
                continue;
            Utils.setBiomeAt(this.world, xx, zz, ThaumcraftWorldGenerator.biomeTaint);
            BlockPos bp = new BlockPos(xx, this.world.getHeight(xx, zz), zz);
            if (!this.world.isAirBlock(bp.down())) {
                this.world.setBlockState(bp, ConfigBlocks.blockTaintFibres.getDefaultState()
                    .withProperty(BlockTaintFibres.TYPE, 0), 3);
            }
        }
    }

    @Override public void writeSpawnData(ByteBuf buf) {
        buf.writeBoolean(this.seeker);
        buf.writeInt(this.oi);
    }
    @Override public void readSpawnData(ByteBuf buf) {
        this.seeker = buf.readBoolean();
        this.oi = buf.readInt();
    }
}
