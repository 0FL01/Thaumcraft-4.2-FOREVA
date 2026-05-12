package thaumcraft.common.entities.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityPrimalOrb extends EntityThrowable implements IEntityAdditionalSpawnData {
    boolean seeker = false;
    int oi = 0; // owner entity ID

    public EntityPrimalOrb(World world) { super(world); }
    public EntityPrimalOrb(World world, EntityLivingBase shooter, boolean seeker) {
        super(world, shooter);
        this.seeker = seeker;
        this.oi = shooter.getEntityId();
    }

    @Override
    protected float getGravityVelocity() { return 0.001f; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 5000) { this.setDead(); return; }
        // Homing
        if (this.seeker && this.getThrower() != null && this.ticksExisted > 20) {
            // Find nearest non-owner living entity within 16 blocks
            EntityLivingBase target = this.world.getClosestPlayerToEntity(this, 16.0);
            if (target != null && target != this.getThrower()) {
                double dx = target.posX - this.posX;
                double dy = target.getEntityBoundingBox().minY + (double)target.height * 0.5 - this.posY;
                double dz = target.posZ - this.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist > 0.01) {
                    this.motionX += (dx / dist - this.motionX) * 0.1;
                    this.motionY += (dy / dist - this.motionY) * 0.1;
                    this.motionZ += (dz / dist - this.motionZ) * 0.1;
                    this.motionX = MathHelper.clamp(this.motionX, -0.2, 0.2);
                    this.motionY = MathHelper.clamp(this.motionY, -0.2, 0.2);
                    this.motionZ = MathHelper.clamp(this.motionZ, -0.2, 0.2);
                }
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            float explosiveRadius = 2.0f;
            float specialChance = 1.0f;
            // Underwater: bigger explosion + higher special chance
            if (this.isInsideOfMaterial(Material.WATER)) {
                explosiveRadius = 4.0f;
                specialChance = 10.0f;
            }
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, explosiveRadius, true);
            // Special effect: taint biome or random node
            if (!this.seeker && (float)this.rand.nextInt(100) < specialChance) {
                if (this.rand.nextBoolean()) {
                    this.taintSplosion();
                } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    ThaumcraftWorldGenerator.createRandomNodeAt(
                        this.world, result.getBlockPos(), this.rand, false, false, true);
                }
            }
        }
        this.setDead();
    }

    private void taintSplosion() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);
        for (int a = 0; a < 10; ++a) {
            int xx = x + (int)(this.rand.nextFloat() - this.rand.nextFloat() * 6.0f);
            int zz = z + (int)(this.rand.nextFloat() - this.rand.nextFloat() * 6.0f);
            if (this.rand.nextBoolean()
                || this.world.getBiome(new BlockPos(xx, 0, zz)) == ThaumcraftWorldGenerator.biomeTaint)
                continue;
            Utils.setBiomeAt(this.world, xx, zz, ThaumcraftWorldGenerator.biomeTaint);
            BlockPos bp = new BlockPos(xx, this.world.getHeight(xx, zz), zz);
            if (this.world.isAirBlock(bp) || this.world.getBlockState(bp).getBlock().isReplaceable(this.world, bp)) {
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
