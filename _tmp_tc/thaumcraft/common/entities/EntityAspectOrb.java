/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.InventoryUtils;

public class EntityAspectOrb
extends Entity
implements IEntityAdditionalSpawnData {
    public int orbAge = 0;
    public int orbMaxAge = 150;
    public int orbCooldown;
    private int orbHealth = 5;
    private Aspect aspect;
    private int aspectValue;
    private EntityPlayer closestPlayer;

    public boolean func_70112_a(double par1) {
        double d1 = 0.5;
        return par1 < (d1 *= 64.0 * this.field_70155_l) * d1;
    }

    public EntityAspectOrb(World par1World, double par2, double par4, double par6, Aspect aspect, int par8) {
        super(par1World);
        this.func_70105_a(0.125f, 0.125f);
        this.field_70129_M = this.field_70131_O / 2.0f;
        this.func_70107_b(par2, par4, par6);
        this.field_70177_z = (float)(Math.random() * 360.0);
        this.field_70159_w = (float)(Math.random() * (double)0.2f - (double)0.1f) * 2.0f;
        this.field_70181_x = (float)(Math.random() * 0.2) * 2.0f;
        this.field_70179_y = (float)(Math.random() * (double)0.2f - (double)0.1f) * 2.0f;
        this.aspectValue = par8;
        this.setAspect(aspect);
    }

    protected boolean func_70041_e_() {
        return false;
    }

    public EntityAspectOrb(World par1World) {
        super(par1World);
        this.func_70105_a(0.125f, 0.125f);
        this.field_70129_M = this.field_70131_O / 2.0f;
    }

    protected void func_70088_a() {
    }

    @SideOnly(value=Side.CLIENT)
    public int func_70070_b(float par1) {
        float f1 = 0.5f;
        if (f1 < 0.0f) {
            f1 = 0.0f;
        }
        if (f1 > 1.0f) {
            f1 = 1.0f;
        }
        int i = super.func_70070_b(par1);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(f1 * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public void func_70071_h_() {
        double d3;
        double d2;
        double d1;
        double d4;
        double d5;
        List targets;
        super.func_70071_h_();
        if (this.orbCooldown > 0) {
            --this.orbCooldown;
        }
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        this.field_70181_x -= (double)0.03f;
        if (this.field_70170_p.func_147439_a(MathHelper.func_76128_c((double)this.field_70165_t), MathHelper.func_76128_c((double)this.field_70163_u), MathHelper.func_76128_c((double)this.field_70161_v)).func_149688_o() == Material.field_151587_i) {
            this.field_70181_x = 0.2f;
            this.field_70159_w = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
            this.field_70179_y = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
            this.func_85030_a("random.fizz", 0.4f, 2.0f + this.field_70146_Z.nextFloat() * 0.4f);
        }
        this.func_145771_j(this.field_70165_t, (this.field_70121_D.field_72338_b + this.field_70121_D.field_72337_e) / 2.0, this.field_70161_v);
        double d0 = 8.0;
        if (this.field_70173_aa % 5 == 0 && this.closestPlayer == null && (targets = this.field_70170_p.func_72872_a(EntityPlayer.class, AxisAlignedBB.func_72330_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v, (double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v).func_72314_b(d0, d0, d0))).size() > 0) {
            double distance = Double.MAX_VALUE;
            for (Entity t : targets) {
                double d = ((EntityPlayer)t).func_70068_e((Entity)this);
                if (!(d < distance) || InventoryUtils.isWandInHotbarWithRoom(this.getAspect(), this.aspectValue, (EntityPlayer)t) < 0) continue;
                distance = d;
                this.closestPlayer = (EntityPlayer)t;
            }
        }
        if (this.closestPlayer != null && (d5 = 1.0 - (d4 = Math.sqrt((d1 = (this.closestPlayer.field_70165_t - this.field_70165_t) / d0) * d1 + (d2 = (this.closestPlayer.field_70163_u + (double)this.closestPlayer.func_70047_e() - this.field_70163_u) / d0) * d2 + (d3 = (this.closestPlayer.field_70161_v - this.field_70161_v) / d0) * d3))) > 0.0) {
            d5 *= d5;
            this.field_70159_w += d1 / d4 * d5 * 0.1;
            this.field_70181_x += d2 / d4 * d5 * 0.1;
            this.field_70179_y += d3 / d4 * d5 * 0.1;
        }
        this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
        float f = 0.98f;
        if (this.field_70122_E) {
            f = 0.58800006f;
            Block i = this.field_70170_p.func_147439_a(MathHelper.func_76128_c((double)this.field_70165_t), MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b) - 1, MathHelper.func_76128_c((double)this.field_70161_v));
            if (!i.isAir((IBlockAccess)this.field_70170_p, MathHelper.func_76128_c((double)this.field_70165_t), MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b) - 1, MathHelper.func_76128_c((double)this.field_70161_v))) {
                f = i.field_149765_K * 0.98f;
            }
        }
        this.field_70159_w *= (double)f;
        this.field_70181_x *= (double)0.98f;
        this.field_70179_y *= (double)f;
        if (this.field_70122_E) {
            this.field_70181_x *= (double)-0.9f;
        }
        ++this.orbAge;
        if (this.orbAge >= this.orbMaxAge) {
            this.func_70106_y();
        }
    }

    public boolean func_70072_I() {
        return this.field_70170_p.func_72918_a(this.field_70121_D, Material.field_151586_h, (Entity)this);
    }

    protected void func_70081_e(int par1) {
        this.func_70097_a(DamageSource.field_76372_a, par1);
    }

    public boolean func_70097_a(DamageSource par1DamageSource, float par2) {
        if (this.func_85032_ar()) {
            return false;
        }
        this.func_70018_K();
        this.orbHealth = (int)((float)this.orbHealth - par2);
        if (this.orbHealth <= 0) {
            this.func_70106_y();
        }
        return false;
    }

    public void func_70014_b(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.func_74777_a("Health", (short)((byte)this.orbHealth));
        par1NBTTagCompound.func_74777_a("Age", (short)this.orbAge);
        par1NBTTagCompound.func_74777_a("Value", (short)this.aspectValue);
        par1NBTTagCompound.func_74778_a("Aspect", this.getAspect().getTag());
    }

    public void func_70037_a(NBTTagCompound par1NBTTagCompound) {
        this.orbHealth = par1NBTTagCompound.func_74765_d("Health") & 0xFF;
        this.orbAge = par1NBTTagCompound.func_74765_d("Age");
        this.aspectValue = par1NBTTagCompound.func_74765_d("Value");
        this.setAspect(Aspect.getAspect(par1NBTTagCompound.func_74779_i("Aspect")));
    }

    public void func_70100_b_(EntityPlayer par1EntityPlayer) {
        if (!this.field_70170_p.field_72995_K) {
            int slot = InventoryUtils.isWandInHotbarWithRoom(this.getAspect(), this.aspectValue, par1EntityPlayer);
            if (this.orbCooldown == 0 && par1EntityPlayer.field_71090_bL == 0 && this.getAspect().isPrimal() && slot >= 0) {
                ItemWandCasting wand = (ItemWandCasting)par1EntityPlayer.field_71071_by.field_70462_a[slot].func_77973_b();
                wand.addVis(par1EntityPlayer.field_71071_by.field_70462_a[slot], this.getAspect(), this.aspectValue, true);
                par1EntityPlayer.field_71090_bL = 2;
                this.func_85030_a("random.orb", 0.1f, 0.5f * ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.7f + 1.8f));
                this.func_70106_y();
            }
        }
    }

    public void writeSpawnData(ByteBuf data) {
        if (this.getAspect() != null) {
            data.writeShort(this.getAspect().getTag().length());
            for (char c : this.getAspect().getTag().toCharArray()) {
                data.writeChar((int)c);
            }
        }
    }

    public void readSpawnData(ByteBuf data) {
        try {
            int l = data.readShort();
            StringBuilder s = new StringBuilder();
            for (int var4 = 0; var4 < l; ++var4) {
                s.append(data.readChar());
            }
            this.setAspect(Aspect.getAspect(s.toString()));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int getAspectValue() {
        return this.aspectValue;
    }

    public boolean func_70075_an() {
        return false;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }
}
