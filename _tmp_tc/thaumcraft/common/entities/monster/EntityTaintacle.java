/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities.monster;

import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityTaintacleSmall;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintacle
extends EntityMob
implements ITaintedMob {
    public float flailIntensity = 1.0f;

    public EntityTaintacle(World par1World) {
        super(par1World);
        this.func_70105_a(0.66f, 3.0f);
        this.field_70728_aV = 10;
    }

    public boolean func_70601_bi() {
        int var1 = MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b);
        int var2 = MathHelper.func_76128_c((double)this.field_70165_t);
        int var3 = MathHelper.func_76128_c((double)this.field_70161_v);
        int var4 = this.field_70170_p.func_72957_l(var2, var1, var3);
        int var5 = 7;
        List ents = this.field_70170_p.func_72872_a(EntityTaintacle.class, AxisAlignedBB.func_72330_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v, (double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v).func_72314_b(24.0, 8.0, 24.0));
        boolean onTaint = (this.field_70170_p.func_147439_a(var2, var1, var3) == ConfigBlocks.blockTaintFibres && this.field_70170_p.func_72805_g(var2, var1, var3) == 0 || this.field_70170_p.func_147439_a(var2, var1, var3) == ConfigBlocks.blockTaint && this.field_70170_p.func_72805_g(var2, var1, var3) == 1) && this.field_70170_p.func_72807_a((int)var2, (int)var3).field_76756_M == Config.biomeTaintID;
        return ents.size() > 0 || !onTaint ? false : super.func_70601_bi();
    }

    public float func_70053_R() {
        return 0.25f;
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(50.0);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(7.0);
    }

    public boolean func_70067_L() {
        return true;
    }

    public boolean func_70104_M() {
        return true;
    }

    protected Entity func_70782_k() {
        EntityLivingBase entity = null;
        List ents = this.field_70170_p.func_72872_a(EntityLivingBase.class, AxisAlignedBB.func_72330_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v, (double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v).func_72314_b((double)(this.field_70131_O * 6.0f), (double)(this.field_70131_O * 3.0f), (double)(this.field_70131_O * 6.0f)));
        if (ents.size() > 0) {
            double distance = Double.MAX_VALUE;
            for (Object ent : ents) {
                if (ent == null) continue;
                EntityLivingBase el = (EntityLivingBase)ent;
                double d = el.func_70068_e((Entity)this);
                if (el instanceof ITaintedMob || !(d < distance)) continue;
                distance = d;
                entity = el;
            }
        }
        return entity;
    }

    public void func_70091_d(double par1, double par3, double par5) {
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        super.func_70091_d(par1, par3, par5);
    }

    protected void func_70626_be() {
        if (this.field_70789_a != null) {
            this.faceEntity(this.field_70789_a, 5.0f);
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_70173_aa % 20 == 0 && this.field_70170_p.func_72807_a((int)MathHelper.func_76128_c((double)this.field_70165_t), (int)MathHelper.func_76128_c((double)this.field_70161_v)).field_76756_M != Config.biomeTaintID) {
            this.func_70665_d(DamageSource.field_76366_f, 1.0f);
        }
        if (this.field_70170_p.field_72995_K) {
            if ((float)this.field_70173_aa > this.field_70131_O * 10.0f && (this.field_70737_aN > 0 || this.field_70724_aR > 0 || this.field_70789_a != null && this.field_70789_a.func_70032_d((Entity)this) < this.field_70131_O)) {
                if (this.flailIntensity < 3.0f) {
                    this.flailIntensity += 0.2f;
                }
            } else if (this.flailIntensity > 1.0f) {
                this.flailIntensity -= 0.2f;
            }
            if ((float)this.field_70173_aa < this.field_70131_O * 10.0f && this.field_70122_E) {
                Thaumcraft.proxy.tentacleAriseFX((Entity)this);
            }
        }
        if (this.field_70789_a == null) {
            this.field_70789_a = this.func_70782_k();
        } else if (this.field_70789_a.func_70089_S() && this.getAgitationState()) {
            float f1 = this.field_70789_a.func_70032_d((Entity)this);
            if (!this.field_70170_p.field_72995_K && this.func_70685_l(this.field_70789_a)) {
                this.func_70785_a(this.field_70789_a, f1);
            }
        } else {
            this.field_70789_a = null;
        }
    }

    protected void func_70785_a(Entity entity, float par2) {
        if (this.field_70724_aR <= 0) {
            if (par2 <= this.field_70131_O && entity.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b && entity.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
                this.field_70724_aR = 20;
                this.func_70652_k(entity);
                this.func_85030_a("thaumcraft:tentacle", this.func_70599_aP(), this.func_70647_i());
            } else if (par2 > this.field_70131_O && entity.field_70122_E && !(this instanceof EntityTaintacleSmall)) {
                this.spawnTentacles(entity);
            }
        }
    }

    public boolean func_70652_k(Entity par1Entity) {
        boolean flag;
        float i = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
        if (this.func_70644_a(Potion.field_76420_g)) {
            i += (float)(3 << this.func_70660_b(Potion.field_76420_g).func_76458_c());
        }
        if (this.func_70644_a(Potion.field_76437_t)) {
            i -= (float)(2 << this.func_70660_b(Potion.field_76437_t).func_76458_c());
        }
        int j = 0;
        if (par1Entity instanceof EntityLivingBase) {
            i += EnchantmentHelper.func_77512_a((EntityLivingBase)this, (EntityLivingBase)((EntityLivingBase)par1Entity));
            j += EnchantmentHelper.func_77507_b((EntityLivingBase)this, (EntityLivingBase)((EntityLivingBase)par1Entity));
        }
        if (flag = par1Entity.func_70097_a(DamageSourceThaumcraft.causeTentacleDamage((EntityLivingBase)this), i)) {
            int k;
            if (j > 0) {
                par1Entity.func_70024_g((double)(-MathHelper.func_76126_a((float)(this.field_70177_z * (float)Math.PI / 180.0f)) * (float)j * 0.5f), 0.1, (double)(MathHelper.func_76134_b((float)(this.field_70177_z * (float)Math.PI / 180.0f)) * (float)j * 0.5f));
                this.field_70159_w *= 0.6;
                this.field_70179_y *= 0.6;
            }
            if ((k = EnchantmentHelper.func_90036_a((EntityLivingBase)this)) > 0) {
                par1Entity.func_70015_d(k * 4);
            }
            if (par1Entity instanceof EntityLivingBase) {
                EnchantmentHelper.func_151384_a((EntityLivingBase)((EntityLivingBase)par1Entity), (Entity)this);
            }
            EnchantmentHelper.func_151385_b((EntityLivingBase)this, (Entity)par1Entity);
        }
        return flag;
    }

    protected void spawnTentacles(Entity entity) {
        int i = MathHelper.func_76128_c((double)entity.field_70165_t);
        int j = MathHelper.func_76128_c((double)entity.field_70121_D.field_72338_b);
        int k = MathHelper.func_76128_c((double)entity.field_70161_v);
        if (this.field_70170_p.func_72807_a((int)i, (int)k).field_76756_M == Config.biomeEldritchID || this.field_70170_p.func_72807_a((int)i, (int)k).field_76756_M == Config.biomeTaintID && (this.field_70170_p.func_147439_a(i, j, k).func_149688_o() == Config.taintMaterial || this.field_70170_p.func_147439_a(i, j, k).func_149688_o() == Config.taintMaterial || this.field_70170_p.func_147439_a(i, j - 1, k).func_149688_o() == Config.taintMaterial)) {
            this.field_70724_aR = 40 + this.field_70170_p.field_73012_v.nextInt(20);
            EntityTaintacleSmall taintlet = new EntityTaintacleSmall(this.field_70170_p);
            taintlet.func_70012_b(entity.field_70165_t + (double)this.field_70170_p.field_73012_v.nextFloat() - (double)this.field_70170_p.field_73012_v.nextFloat(), entity.field_70163_u, entity.field_70161_v + (double)this.field_70170_p.field_73012_v.nextFloat() - (double)this.field_70170_p.field_73012_v.nextFloat(), 0.0f, 0.0f);
            this.field_70170_p.func_72838_d((Entity)taintlet);
            this.func_85030_a("thaumcraft:tentacle", this.func_70599_aP(), this.func_70647_i());
            if (this.field_70170_p.func_72807_a((int)i, (int)k).field_76756_M == Config.biomeEldritchID && this.field_70170_p.func_147437_c(i, j, k) && BlockUtils.isAdjacentToSolidBlock(this.field_70170_p, i, j, k)) {
                Utils.setBiomeAt(this.field_70170_p, i, k, ThaumcraftWorldGenerator.biomeTaint);
                this.field_70170_p.func_147465_d(i, j, k, ConfigBlocks.blockTaintFibres, this.field_70170_p.field_73012_v.nextInt(4) == 0 ? 1 : 0, 3);
            }
        }
    }

    public boolean func_70097_a(DamageSource ds, float par2) {
        if (!(this instanceof EntityTaintacleSmall) && ds.func_76346_g() != null && this.func_70032_d(ds.func_76346_g()) > 16.0f && !this.field_70170_p.field_72995_K) {
            this.spawnTentacles(ds.func_76346_g());
        }
        return super.func_70097_a(ds, par2);
    }

    public boolean getAgitationState() {
        return this.field_70789_a != null && this.field_70789_a.func_70068_e((Entity)this) < (double)(this.field_70131_O * 7.0f * (this.field_70131_O * 7.0f));
    }

    public void faceEntity(Entity par1Entity, float par2) {
        double d0 = par1Entity.field_70165_t - this.field_70165_t;
        double d1 = par1Entity.field_70161_v - this.field_70161_v;
        float f2 = (float)(Math.atan2(d1, d0) * 180.0 / Math.PI) - 90.0f;
        this.field_70177_z = this.func_70663_b(this.field_70177_z, f2, par2);
    }

    protected float func_70663_b(float par1, float par2, float par3) {
        float f3 = MathHelper.func_76142_g((float)(par2 - par1));
        if (f3 > par3) {
            f3 = par3;
        }
        if (f3 < -par3) {
            f3 = -par3;
        }
        return par1 + f3;
    }

    public int func_70627_aG() {
        return 200;
    }

    protected String func_70639_aQ() {
        return "thaumcraft:roots";
    }

    protected float func_70647_i() {
        return 1.3f - this.field_70131_O / 10.0f;
    }

    protected float func_70599_aP() {
        return this.field_70131_O / 8.0f;
    }

    protected String func_70621_aR() {
        return "thaumcraft:tentacle";
    }

    protected String func_70673_aS() {
        return "thaumcraft:tentacle";
    }

    protected void func_70628_a(boolean flag, int i) {
        if (this.field_70170_p.field_73012_v.nextBoolean()) {
            this.func_70099_a(new ItemStack(ConfigItems.itemResource, 1, 11), this.field_70131_O / 2.0f);
        } else {
            this.func_70099_a(new ItemStack(ConfigItems.itemResource, 1, 12), this.field_70131_O / 2.0f);
        }
        super.func_70628_a(flag, i);
    }
}
