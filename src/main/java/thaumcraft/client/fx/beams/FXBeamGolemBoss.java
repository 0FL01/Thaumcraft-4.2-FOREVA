package thaumcraft.client.fx.beams;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamGolemBoss extends FXBeam {
    private final EntityLivingBase boss;
    private final Entity target;

    public FXBeamGolemBoss(World world, EntityLivingBase boss, Entity target,
                           float red, float green, float blue, int age) {
        super(world,
                boss.posX, boss.posY + boss.getEyeHeight(), boss.posZ,
                target.posX, target.getEntityBoundingBox().minY + target.height * 0.5D, target.posZ,
                red, green, blue, age, true, 20);
        this.boss = boss;
        this.target = target;
        updateEndpointsFromEntities();
    }

    private void updateEndpointsFromEntities() {
        float f1 = MathHelper.cos((float) (-this.boss.renderYawOffset * ((float) Math.PI / 180.0F) - Math.PI));
        float f2 = MathHelper.sin((float) (-this.boss.renderYawOffset * ((float) Math.PI / 180.0F) - Math.PI));
        float f3 = -MathHelper.cos((float) (-this.boss.rotationPitch * ((float) Math.PI / 180.0F)));
        float f4 = MathHelper.sin((float) (-this.boss.rotationPitch * ((float) Math.PI / 180.0F)));
        Vec3d look = new Vec3d(f2 * f3, f4, f1 * f3);
        double sx = this.boss.posX + look.x * 0.5D;
        double sy = this.boss.posY + this.boss.getEyeHeight();
        double sz = this.boss.posZ + look.z * 0.5D;
        double tx = this.target.prevPosX;
        double ty = this.target.getEntityBoundingBox().minY + this.target.height * 0.5D;
        double tz = this.target.prevPosZ;
        updateBeam(sx, sy, sz, tx, ty, tz);
    }

    @Override
    public void onUpdate() {
        if (this.boss == null || this.target == null || !this.boss.isEntityAlive() || !this.target.isEntityAlive()) {
            this.setExpired();
            return;
        }
        updateEndpointsFromEntities();
        super.onUpdate();
    }
}
