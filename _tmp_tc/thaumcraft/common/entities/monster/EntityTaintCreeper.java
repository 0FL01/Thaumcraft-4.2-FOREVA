/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIAvoidEntity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.ai.EntityAIWander
 *  net.minecraft.entity.ai.EntityAIWatchClosest
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.passive.EntityOcelot
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities.monster;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AICreeperSwell;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class EntityTaintCreeper
extends EntityMob
implements ITaintedMob {
    private int lastActiveTime;
    private int timeSinceIgnited;
    private int fuseTime = 30;
    private int explosionRadius = 3;

    public EntityTaintCreeper(World par1World) {
        super(par1World);
        this.field_70714_bg.func_75776_a(1, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.field_70714_bg.func_75776_a(2, (EntityAIBase)new AICreeperSwell(this));
        this.field_70714_bg.func_75776_a(3, (EntityAIBase)new EntityAIAvoidEntity((EntityCreature)this, EntityOcelot.class, 6.0f, 1.0, 1.2));
        this.field_70714_bg.func_75776_a(4, (EntityAIBase)new AIAttackOnCollide((EntityCreature)this, 1.0, false));
        this.field_70714_bg.func_75776_a(5, (EntityAIBase)new EntityAIWander((EntityCreature)this, 1.0));
        this.field_70714_bg.func_75776_a(6, (EntityAIBase)new EntityAIWatchClosest((EntityLiving)this, EntityPlayer.class, 8.0f));
        this.field_70714_bg.func_75776_a(6, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.field_70715_bh.func_75776_a(1, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityPlayer.class, 0, true));
        this.field_70715_bh.func_75776_a(2, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25);
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(2.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25);
    }

    public boolean func_70650_aV() {
        return true;
    }

    protected boolean func_70692_ba() {
        return false;
    }

    public int func_82143_as() {
        return this.func_70638_az() == null ? 3 : 3 + (int)(this.func_110143_aJ() - 1.0f);
    }

    protected void func_70069_a(float par1) {
        super.func_70069_a(par1);
        this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + par1 * 1.5f);
        if (this.timeSinceIgnited > this.fuseTime - 5) {
            this.timeSinceIgnited = this.fuseTime - 5;
        }
    }

    public void func_70014_b(NBTTagCompound par1NBTTagCompound) {
        super.func_70014_b(par1NBTTagCompound);
        if (this.field_70180_af.func_75683_a(17) == 1) {
            par1NBTTagCompound.func_74757_a("powered", true);
        }
        par1NBTTagCompound.func_74777_a("Fuse", (short)this.fuseTime);
        par1NBTTagCompound.func_74774_a("ExplosionRadius", (byte)this.explosionRadius);
    }

    public void func_70037_a(NBTTagCompound par1NBTTagCompound) {
        super.func_70037_a(par1NBTTagCompound);
        this.field_70180_af.func_75692_b(17, (Object)((byte)(par1NBTTagCompound.func_74767_n("powered") ? 1 : 0)));
        if (par1NBTTagCompound.func_74764_b("Fuse")) {
            this.fuseTime = par1NBTTagCompound.func_74765_d("Fuse");
        }
        if (par1NBTTagCompound.func_74764_b("ExplosionRadius")) {
            this.explosionRadius = par1NBTTagCompound.func_74771_c("ExplosionRadius");
        }
    }

    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (Object)-1);
        this.field_70180_af.func_75682_a(17, (Object)0);
    }

    protected Item func_146068_u() {
        return ConfigItems.itemResource;
    }

    protected void func_70628_a(boolean flag, int i) {
        if (this.field_70170_p.field_73012_v.nextBoolean()) {
            this.func_70099_a(new ItemStack(ConfigItems.itemResource, 1, 11), this.field_70131_O / 2.0f);
        } else {
            this.func_70099_a(new ItemStack(ConfigItems.itemResource, 1, 12), this.field_70131_O / 2.0f);
        }
    }

    public void func_70071_h_() {
        if (this.func_70089_S()) {
            this.lastActiveTime = this.timeSinceIgnited;
            int var1 = this.getCreeperState();
            if (var1 > 0 && this.timeSinceIgnited == 0) {
                this.field_70170_p.func_72956_a((Entity)this, "random.fuse", 1.0f, 0.5f);
            }
            this.timeSinceIgnited += var1;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
            if (this.timeSinceIgnited >= 30) {
                this.timeSinceIgnited = 30;
                if (!this.field_70170_p.field_72995_K) {
                    this.field_70170_p.func_72876_a((Entity)this, this.field_70165_t, this.field_70163_u + (double)(this.field_70131_O / 2.0f), this.field_70161_v, 1.5f, false);
                    List ents = this.field_70170_p.func_72872_a(EntityLivingBase.class, AxisAlignedBB.func_72330_a((double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v, (double)this.field_70165_t, (double)this.field_70163_u, (double)this.field_70161_v).func_72314_b(6.0, 6.0, 6.0));
                    if (ents.size() > 0) {
                        for (Object ent : ents) {
                            EntityLivingBase el = (EntityLivingBase)ent;
                            if (el instanceof ITaintedMob || el.func_70662_br()) continue;
                            el.func_70690_d(new PotionEffect(Config.potionTaintPoisonID, 100, 0, false));
                        }
                    }
                    int x = (int)this.field_70165_t;
                    int y = (int)this.field_70163_u;
                    int z = (int)this.field_70161_v;
                    for (int a = 0; a < 10; ++a) {
                        int xx = x + (int)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 5.0f);
                        int zz = z + (int)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 5.0f);
                        if (!this.field_70170_p.field_73012_v.nextBoolean() || this.field_70170_p.func_72807_a(xx, zz) == ThaumcraftWorldGenerator.biomeTaint) continue;
                        Utils.setBiomeAt(this.field_70170_p, xx, zz, ThaumcraftWorldGenerator.biomeTaint);
                        if (!this.field_70170_p.func_147445_c(xx, y - 1, zz, false) || !this.field_70170_p.func_147439_a(xx, y, zz).isReplaceable((IBlockAccess)this.field_70170_p, xx, y, zz)) continue;
                        this.field_70170_p.func_147465_d(xx, y, zz, ConfigBlocks.blockTaintFibres, 0, 3);
                    }
                    this.func_70106_y();
                } else {
                    for (int a = 0; a < Thaumcraft.proxy.particleCount(100); ++a) {
                        Thaumcraft.proxy.taintsplosionFX((Entity)this);
                    }
                }
            }
        }
        super.func_70071_h_();
    }

    public void func_70636_d() {
        super.func_70636_d();
        if (this.field_70170_p.field_72995_K && this.field_70173_aa < 5) {
            for (int a = 0; a < Thaumcraft.proxy.particleCount(10); ++a) {
                Thaumcraft.proxy.splooshFX((Entity)this);
            }
        }
    }

    public float getCreeperFlashIntensity(float par1) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * par1) / 28.0f;
    }

    protected String func_70621_aR() {
        return "mob.creeper.say";
    }

    protected String func_70673_aS() {
        return "mob.creeper.death";
    }

    protected float func_70647_i() {
        return 0.7f;
    }

    public boolean func_70652_k(Entity par1Entity) {
        return true;
    }

    public int getCreeperState() {
        return this.field_70180_af.func_75683_a(16);
    }

    public void setCreeperState(int par1) {
        this.field_70180_af.func_75692_b(16, (Object)((byte)par1));
    }
}
