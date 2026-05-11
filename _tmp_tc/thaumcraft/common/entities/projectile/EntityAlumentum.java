/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

public class EntityAlumentum
extends EntityThrowable {
    public EntityAlumentum(World par1World) {
        super(par1World);
    }

    public EntityAlumentum(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);
    }

    public EntityAlumentum(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    protected float func_70182_d() {
        return 0.75f;
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70170_p.field_72995_K) {
            for (int a = 0; a < 3; ++a) {
                Thaumcraft.proxy.wispFX2(this.field_70170_p, this.field_70165_t + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f), this.field_70163_u + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f), this.field_70161_v + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f), 0.3f, 5, true, true, 0.02f);
                double x2 = (this.field_70165_t + this.field_70169_q) / 2.0 + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f);
                double y2 = (this.field_70163_u + this.field_70167_r) / 2.0 + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f);
                double z2 = (this.field_70161_v + this.field_70166_s) / 2.0 + (double)((this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.3f);
                Thaumcraft.proxy.wispFX2(this.field_70170_p, x2, y2, z2, 0.3f, 5, true, true, 0.02f);
                Thaumcraft.proxy.sparkle((float)this.field_70165_t + (this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.1f, (float)this.field_70163_u + (this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.1f, (float)this.field_70161_v + (this.field_70170_p.field_73012_v.nextFloat() - this.field_70170_p.field_73012_v.nextFloat()) * 0.1f, 6);
            }
        }
    }

    protected void func_70184_a(MovingObjectPosition par1MovingObjectPosition) {
        if (!this.field_70170_p.field_72995_K) {
            boolean var2 = this.field_70170_p.func_82736_K().func_82766_b("mobGriefing");
            this.field_70170_p.func_72876_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.66f, var2);
            this.func_70106_y();
        }
    }

    public float func_70053_R() {
        return 0.1f;
    }
}
