package thaumcraft.common.entities.projectile;

import thaumcraft.common.Thaumcraft;

public class EntityAlumentum extends net.minecraft.entity.projectile.EntityThrowable {
    public EntityAlumentum(net.minecraft.world.World world) { super(world); }
    public EntityAlumentum(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter) { super(world, shooter); }
    public EntityAlumentum(net.minecraft.world.World world, double x, double y, double z) { super(world, x, y, z); }

    @Override
    protected float getGravityVelocity() { return 0.03f; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) return;
        for (int i = 0; i < 3; i++) {
            Thaumcraft.proxy.wispFX2(
                    this.world,
                    this.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    this.posY + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    this.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    0.3F,
                    5,
                    true,
                    true,
                    0.02F);
            Thaumcraft.proxy.wispFX2(
                    this.world,
                    (this.posX + this.prevPosX) * 0.5D + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    (this.posY + this.prevPosY) * 0.5D + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    (this.posZ + this.prevPosZ) * 0.5D + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F,
                    0.3F,
                    5,
                    true,
                    true,
                    0.02F);
            Thaumcraft.proxy.sparkle(
                    (float) (this.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.1F),
                    (float) (this.posY + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.1F),
                    (float) (this.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.1F),
                    6);
        }
    }

    @Override
    protected void onImpact(net.minecraft.util.math.RayTraceResult result) {
        if (!this.world.isRemote) {
            boolean griefing = this.world.getGameRules().getBoolean("mobGriefing");
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, 1.66f, griefing);
            this.setDead();
        }
    }

    @Override
    public float getEyeHeight() {
        return 0.1F;
    }
}
