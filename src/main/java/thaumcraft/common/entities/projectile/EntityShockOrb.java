package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;

public class EntityShockOrb extends EntityThrowable {
    public int area = 4;
    public int damage = 5;

    public EntityShockOrb(World world) { super(world); }
    public EntityShockOrb(World world, EntityLivingBase shooter) { super(world, shooter); }
    public EntityShockOrb(World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.05f; }

    @Override
    public float getCollisionBorderSize() { return 0.1F; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 500) this.setDead();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            // AOE damage within 'area' blocks
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(
                this,
                this.getEntityBoundingBox().grow((double)this.area, (double)this.area, (double)this.area));
            for (Entity entity : list) {
                if (!(entity instanceof EntityLivingBase)) continue;
                double dx = entity.posX - this.posX;
                double dy = entity.getEntityBoundingBox().minY + (double)entity.height * 0.5 - this.posY;
                double dz = entity.posZ - this.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist <= (double)this.area) {
                    // Line of sight check
                    net.minecraft.util.math.RayTraceResult sight = this.world.rayTraceBlocks(
                        new net.minecraft.util.math.Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ),
                        new net.minecraft.util.math.Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ),
                        false, true, false);
                    if (sight == null || sight.typeOfHit != net.minecraft.util.math.RayTraceResult.Type.BLOCK) {
                        entity.attackEntityFrom(
                            DamageSource.causeIndirectDamage(this, this.getThrower()),
                            (float)this.damage);
                    }
                }
            }
            // Place blockAiry (type=10 = fire) in a random pattern within area
            for (int a = 0; a < 20; ++a) {
                int xx = MathHelper.floor(this.posX) + this.rand.nextInt(this.area) - this.rand.nextInt(this.area);
                int zz = MathHelper.floor(this.posZ) + this.rand.nextInt(this.area) - this.rand.nextInt(this.area);
                int yy = MathHelper.floor(this.posY) + this.area;
                while (yy > MathHelper.floor(this.posY) - this.area && this.world.isAirBlock(new BlockPos(xx, yy, zz))) {
                    --yy;
                }
                BlockPos base = new BlockPos(xx, yy, zz);
                BlockPos above = base.up();
                if (!this.world.isAirBlock(above)
                    || this.world.isAirBlock(base)
                    || this.world.getBlockState(above).getBlock() == ConfigBlocks.blockAiry
                    || this.world.rayTraceBlocks(
                        new Vec3d(this.posX, this.posY, this.posZ),
                        new Vec3d((double) xx + 0.5D, (double) yy + 1.5D, (double) zz + 0.5D),
                        false, true, false) != null) {
                    continue;
                }
                this.world.setBlockState(above, ConfigBlocks.blockAiry.getDefaultState()
                    .withProperty(BlockAiry.TYPE, 10), 3);
            }
        }
        this.playSound(TCSounds.SHOCK, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        this.setDead();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) return false;
        this.velocityChanged = true;
        if (source.getTrueSource() != null) {
            Vec3d look = source.getTrueSource().getLookVec();
            if (look != null) {
                this.motionX = look.x * 0.9;
                this.motionY = look.y * 0.9;
                this.motionZ = look.z * 0.9;
                this.playSound(TCSounds.ZAP, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            }
            return true;
        }
        return false;
    }
}
