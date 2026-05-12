package thaumcraft.common.entities.projectile;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityBottleTaint extends EntityThrowable {
    public EntityBottleTaint(World world) { super(world); }
    public EntityBottleTaint(World world, EntityLivingBase shooter) { super(world, shooter); }

    @Override
    protected float getGravityVelocity() { return 0.05f; }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) return;
        if (!this.world.isRemote) {
            // Taint poison to all nearby living entities (5-block radius)
            List<EntityLivingBase> ents = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ)
                    .grow(5.0, 5.0, 5.0));
            for (EntityLivingBase el : ents) {
                if (el instanceof ITaintedMob || el.isEntityUndead()) continue;
                el.addPotionEffect(new PotionEffect(Config.potionFluxTaint, 100, 0, false, true));
            }
            // Biome conversion + taint fibre placement (10 attempts)
            int x = (int)this.posX;
            int y = (int)this.posY;
            int z = (int)this.posZ;
            for (int a = 0; a < 10; ++a) {
                int xx = x + (int)((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0f);
                int zz = z + (int)((this.rand.nextFloat() - this.rand.nextFloat()) * 5.0f);
                if (this.rand.nextBoolean()
                    || this.world.getBiome(new BlockPos(xx, 0, zz)) == ThaumcraftWorldGenerator.biomeTaint)
                    continue;
                Utils.setBiomeAt(this.world, xx, zz, ThaumcraftWorldGenerator.biomeTaint);
                BlockPos bp = new BlockPos(xx, y, zz);
                if (!this.world.getBlockState(bp.down()).isSideSolid(this.world, bp.down(), net.minecraft.util.EnumFacing.UP)
                    || !this.world.getBlockState(bp).getBlock().isReplaceable(this.world, bp))
                    continue;
                this.world.setBlockState(bp, ConfigBlocks.blockTaintFibres.getDefaultState()
                    .withProperty(thaumcraft.common.blocks.BlockTaintFibres.TYPE, 0), 3);
            }
        }
        this.setDead();
    }
}
