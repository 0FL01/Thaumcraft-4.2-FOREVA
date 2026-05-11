/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package thaumcraft.common.entities.projectile;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class EntityFrostShard
extends EntityThrowable
implements IEntityAdditionalSpawnData {
    public double bounce = 0.5;
    public int bounceLimit = 3;
    public boolean fragile = false;

    public EntityFrostShard(World par1World) {
        super(par1World);
    }

    public EntityFrostShard(World par1World, EntityLivingBase par2EntityLiving, float scatter) {
        super(par1World, par2EntityLiving);
        this.func_70186_c(this.field_70159_w, this.field_70181_x, this.field_70179_y, this.func_70182_d(), scatter);
    }

    protected float func_70185_h() {
        return this.fragile ? 0.015f : 0.05f;
    }

    public void writeSpawnData(ByteBuf data) {
        data.writeDouble(this.bounce);
        data.writeInt(this.bounceLimit);
        data.writeBoolean(this.fragile);
    }

    public void readSpawnData(ByteBuf data) {
        this.bounce = data.readDouble();
        this.bounceLimit = data.readInt();
        this.fragile = data.readBoolean();
    }

    protected void func_70184_a(MovingObjectPosition mop) {
        if (mop.field_72308_g != null) {
            int ox = MathHelper.func_76128_c((double)this.field_70165_t) - MathHelper.func_76128_c((double)mop.field_72308_g.field_70165_t);
            int oy = MathHelper.func_76128_c((double)this.field_70163_u) - MathHelper.func_76128_c((double)mop.field_72308_g.field_70163_u);
            int oz = MathHelper.func_76128_c((double)this.field_70161_v) - MathHelper.func_76128_c((double)mop.field_72308_g.field_70161_v);
            if (oz != 0) {
                this.field_70179_y *= -1.0;
            }
            if (ox != 0) {
                this.field_70159_w *= -1.0;
            }
            if (oy != 0) {
                this.field_70181_x *= -0.9;
            }
            this.field_70159_w *= 0.66;
            this.field_70181_x *= 0.66;
            this.field_70179_y *= 0.66;
            int a = 0;
            while ((float)a < this.getDamage()) {
                this.field_70170_p.func_72869_a("blockcrack_" + Block.func_149682_b((Block)ConfigBlocks.blockCustomOre) + "_15", this.field_70165_t, this.field_70163_u, this.field_70161_v, 4.0 * ((double)this.field_70146_Z.nextFloat() - 0.5), 0.5, ((double)this.field_70146_Z.nextFloat() - 0.5) * 4.0);
                ++a;
            }
        } else if (mop.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            ForgeDirection dir = ForgeDirection.getOrientation((int)mop.field_72310_e);
            if (dir.offsetZ != 0) {
                this.field_70179_y *= -1.0;
            }
            if (dir.offsetX != 0) {
                this.field_70159_w *= -1.0;
            }
            if (dir.offsetY != 0) {
                this.field_70181_x *= -0.9;
            }
            Block bhit = this.field_70170_p.func_147439_a(mop.field_72311_b, mop.field_72312_c, mop.field_72309_d);
            try {
                this.func_85030_a(bhit.field_149762_H.func_150495_a(), 0.3f, 1.2f / (this.field_70146_Z.nextFloat() * 0.2f + 0.9f));
            }
            catch (Exception e) {
                // empty catch block
            }
            int a = 0;
            while ((float)a < this.getDamage()) {
                this.field_70170_p.func_72869_a("blockcrack_" + Block.func_149682_b((Block)bhit) + "_" + this.field_70170_p.func_72805_g(mop.field_72311_b, mop.field_72312_c, mop.field_72309_d), this.field_70165_t, this.field_70163_u, this.field_70161_v, 4.0 * ((double)this.field_70146_Z.nextFloat() - 0.5), 0.5, ((double)this.field_70146_Z.nextFloat() - 0.5) * 4.0);
                ++a;
            }
        }
        this.field_70159_w *= this.bounce;
        this.field_70181_x *= this.bounce;
        this.field_70179_y *= this.bounce;
        float var20 = MathHelper.func_76133_a((double)(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y));
        this.field_70165_t -= this.field_70159_w / (double)var20 * (double)0.05f;
        this.field_70163_u -= this.field_70181_x / (double)var20 * (double)0.05f;
        this.field_70161_v -= this.field_70179_y / (double)var20 * (double)0.05f;
        this.func_70018_K();
        if (!this.field_70170_p.field_72995_K && mop.field_72308_g != null) {
            double mx = mop.field_72308_g.field_70159_w;
            double my = mop.field_72308_g.field_70181_x;
            double mz = mop.field_72308_g.field_70179_y;
            mop.field_72308_g.func_70097_a(DamageSource.func_76356_a((Entity)this, (Entity)this.func_85052_h()), this.getDamage());
            if (mop.field_72308_g instanceof EntityLivingBase && this.getFrosty() > 0) {
                ((EntityLivingBase)mop.field_72308_g).func_70690_d(new PotionEffect(Potion.field_76421_d.field_76415_H, 200, this.getFrosty() - 1));
            }
            if (this.fragile) {
                mop.field_72308_g.field_70172_ad = 0;
                this.func_70106_y();
                this.func_85030_a(Blocks.field_150432_aD.field_149762_H.func_150495_a(), 0.3f, 1.2f / (this.field_70146_Z.nextFloat() * 0.2f + 0.9f));
                mop.field_72308_g.field_70159_w = mx + (mop.field_72308_g.field_70159_w - mx) / 10.0;
                mop.field_72308_g.field_70181_x = my + (mop.field_72308_g.field_70181_x - my) / 10.0;
                mop.field_72308_g.field_70179_y = mz + (mop.field_72308_g.field_70179_y - mz) / 10.0;
            }
        }
        if (this.bounceLimit-- <= 0) {
            this.func_70106_y();
            this.func_85030_a(Blocks.field_150432_aD.field_149762_H.func_150495_a(), 0.3f, 1.2f / (this.field_70146_Z.nextFloat() * 0.2f + 0.9f));
            int a = 0;
            while ((float)a < 8.0f * this.getDamage()) {
                this.field_70170_p.func_72869_a("blockcrack_" + Block.func_149682_b((Block)ConfigBlocks.blockCustomOre) + "_15", this.field_70165_t, this.field_70163_u, this.field_70161_v, 4.0 * ((double)this.field_70146_Z.nextFloat() - 0.5), 0.5, ((double)this.field_70146_Z.nextFloat() - 0.5) * 4.0);
                ++a;
            }
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70170_p.field_72995_K && this.getFrosty() > 0) {
            float s = this.getDamage() / 10.0f;
            for (int a = 0; a < this.getFrosty(); ++a) {
                Thaumcraft.proxy.sparkle((float)this.field_70165_t - s + this.field_70146_Z.nextFloat() * (s * 2.0f), (float)this.field_70163_u - s + this.field_70146_Z.nextFloat() * (s * 2.0f), (float)this.field_70161_v - s + this.field_70146_Z.nextFloat() * (s * 2.0f), 0.4f, 6, 0.005f);
            }
        }
        float var20 = MathHelper.func_76133_a((double)(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y));
        this.field_70177_z = (float)(Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0 / Math.PI);
        this.field_70125_A = (float)(Math.atan2(this.field_70181_x, var20) * 180.0 / Math.PI);
        while (this.field_70125_A - this.field_70127_C < -180.0f) {
            this.field_70127_C -= 360.0f;
        }
        while (this.field_70125_A - this.field_70127_C >= 180.0f) {
            this.field_70127_C += 360.0f;
        }
        while (this.field_70177_z - this.field_70126_B < -180.0f) {
            this.field_70126_B -= 360.0f;
        }
        while (this.field_70177_z - this.field_70126_B >= 180.0f) {
            this.field_70126_B += 360.0f;
        }
        this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2f;
        this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2f;
    }

    public void func_70014_b(NBTTagCompound par1NBTTagCompound) {
        super.func_70014_b(par1NBTTagCompound);
        par1NBTTagCompound.func_74776_a("damage", this.getDamage());
        par1NBTTagCompound.func_74757_a("fragile", this.fragile);
        par1NBTTagCompound.func_74768_a("frost", this.getFrosty());
    }

    public void func_70037_a(NBTTagCompound par1NBTTagCompound) {
        super.func_70037_a(par1NBTTagCompound);
        this.setDamage(par1NBTTagCompound.func_74760_g("damage"));
        this.fragile = par1NBTTagCompound.func_74767_n("fragile");
        this.setFrosty(par1NBTTagCompound.func_74762_e("frost"));
    }

    protected boolean func_70041_e_() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public float func_70053_R() {
        return 0.0f;
    }

    public void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (Object)new Float(0.0f));
        this.field_70180_af.func_75682_a(17, (Object)new Byte(0));
    }

    public void setDamage(float par1) {
        this.field_70180_af.func_75692_b(16, (Object)Float.valueOf(par1));
        this.func_70105_a(0.15f + par1 * 0.15f, 0.15f + par1 * 0.15f);
    }

    public float getDamage() {
        return this.field_70180_af.func_111145_d(16);
    }

    public void setFrosty(int frosty) {
        this.field_70180_af.func_75692_b(17, (Object)((byte)frosty));
    }

    public int getFrosty() {
        return this.field_70180_af.func_75683_a(17);
    }
}
