/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities.monster;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityTaintSpider;

public class EntityTaintSpore
extends EntityMob
implements ITaintedMob,
IEntityAdditionalSpawnData {
    public ArrayList swarm = new ArrayList();
    protected int growth = 0;
    public float displaySize = 0.0f;

    public EntityTaintSpore(World par1World) {
        super(par1World);
        this.setSporeSize(2);
    }

    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, (Object)new Byte(1));
    }

    public void func_70014_b(NBTTagCompound par1NBTTagCompound) {
        super.func_70014_b(par1NBTTagCompound);
        par1NBTTagCompound.func_74768_a("Size", this.getSporeSize() - 1);
    }

    public void func_70037_a(NBTTagCompound par1NBTTagCompound) {
        super.func_70037_a(par1NBTTagCompound);
        this.setSporeSize(par1NBTTagCompound.func_74762_e("Size") + 1);
    }

    public void setSporeSize(int par1) {
        this.field_70180_af.func_75692_b(16, (Object)new Byte((byte)par1));
        float size = Math.max(0.15f * (float)par1, 0.5f);
        this.func_70105_a(size, size);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70728_aV = par1;
    }

    public int getSporeSize() {
        return this.field_70180_af.func_75683_a(16);
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(1.0);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
    }

    public float func_70053_R() {
        return 0.0f;
    }

    public boolean func_70067_L() {
        return true;
    }

    public boolean func_70104_M() {
        return false;
    }

    public void func_70091_d(double par1, double par3, double par5) {
        int z;
        int y;
        int x;
        par1 = 0.0;
        par5 = 0.0;
        if (par3 > 0.0) {
            par3 = 0.0;
        }
        if (this.field_70170_p.func_147439_a(x = MathHelper.func_76128_c((double)this.field_70165_t), y = MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b) - 1, z = MathHelper.func_76128_c((double)this.field_70161_v)) == ConfigBlocks.blockTaintFibres && this.field_70170_p.func_72805_g(x, y, z) == 4) {
            return;
        }
        super.func_70091_d(par1, par3, par5);
    }

    protected void func_70626_be() {
    }

    public boolean func_70112_a(double par1) {
        return par1 < 4096.0;
    }

    @SideOnly(value=Side.CLIENT)
    public int func_70070_b(float par1) {
        return 0xF000F0;
    }

    public float func_70013_c(float par1) {
        return 1.0f;
    }

    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_70173_aa % 20 == 0 && this.field_70170_p.func_72807_a((int)MathHelper.func_76128_c((double)this.field_70165_t), (int)MathHelper.func_76128_c((double)this.field_70161_v)).field_76756_M != Config.biomeTaintID) {
            this.func_70665_d(DamageSource.field_76366_f, 1.0f);
        }
        this.sporeOnUpdate();
    }

    protected void sporeOnUpdate() {
        int z;
        int y;
        int x;
        if (this.getSporeSize() < 10 && this.growth++ == 1200) {
            this.setSporeSize(this.getSporeSize() + 1);
            this.growth = 0;
        }
        if (this.field_70170_p.field_72995_K) {
            if (this.displaySize < (float)this.getSporeSize()) {
                this.displaySize += 0.02f;
            }
            for (int a = 0; a < this.swarm.size(); ++a) {
                if (this.swarm.get(a) != null && !((Entity)this.swarm.get((int)a)).field_70128_L) continue;
                this.swarm.remove(a);
                break;
            }
            if (this.swarm.size() < this.getSporeSize() / 3) {
                this.swarm.add(Thaumcraft.proxy.swarmParticleFX(this.field_70170_p, (Entity)this, 0.1f, 10.0f, 0.0f));
            }
        }
        if (this.field_70170_p.func_147439_a(x = MathHelper.func_76128_c((double)this.field_70165_t), y = MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b) - 1, z = MathHelper.func_76128_c((double)this.field_70161_v)) != ConfigBlocks.blockTaintFibres || this.field_70170_p.func_72805_g(x, y, z) != 4) {
            this.spiderBurst();
        } else if (this.field_70725_aQ > 0) {
            this.spiderBurst();
        }
    }

    public void func_70100_b_(EntityPlayer par1EntityPlayer) {
        this.spiderBurst();
    }

    protected void spiderBurst() {
        if (!this.field_70170_p.field_72995_K) {
            int z;
            int y;
            this.field_70170_p.func_72956_a((Entity)this, "thaumcraft:gore", 1.0f, 0.9f + this.field_70170_p.field_73012_v.nextFloat() * 0.1f);
            int q = this.getSporeSize() / 3 + this.field_70170_p.field_73012_v.nextInt(this.getSporeSize() / 2 + 1);
            for (int a = 0; a < q; ++a) {
                EntityTaintSpider spiderling = new EntityTaintSpider(this.field_70170_p);
                spiderling.func_70012_b(this.field_70165_t + (double)this.field_70170_p.field_73012_v.nextFloat() - (double)this.field_70170_p.field_73012_v.nextFloat(), this.field_70163_u + (double)this.field_70170_p.field_73012_v.nextFloat(), this.field_70161_v + (double)this.field_70170_p.field_73012_v.nextFloat() - (double)this.field_70170_p.field_73012_v.nextFloat(), this.field_70170_p.field_73012_v.nextFloat() * 360.0f, 0.0f);
                this.field_70170_p.func_72838_d((Entity)spiderling);
            }
            int x = MathHelper.func_76128_c((double)this.field_70165_t);
            if (this.field_70170_p.func_147439_a(x, y = MathHelper.func_76128_c((double)this.field_70121_D.field_72338_b) - 1, z = MathHelper.func_76128_c((double)this.field_70161_v)) == ConfigBlocks.blockTaintFibres && this.field_70170_p.func_72805_g(x, y, z) == 4) {
                this.field_70170_p.func_72921_c(x, y, z, 3, 3);
            }
            this.func_70106_y();
        } else {
            this.sploosh(50);
        }
    }

    protected void sploosh(int amt) {
        for (int a = 0; a < amt; ++a) {
            Thaumcraft.proxy.splooshFX((Entity)this);
        }
    }

    public void writeSpawnData(ByteBuf data) {
        data.writeFloat((float)this.getSporeSize());
    }

    public void readSpawnData(ByteBuf data) {
        try {
            this.displaySize = data.readFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected float func_70599_aP() {
        return 0.1f;
    }

    public int func_70627_aG() {
        return 200;
    }

    protected String func_70639_aQ() {
        return "thaumcraft:swarm";
    }

    protected String func_70621_aR() {
        return "thaumcraft:gore";
    }

    protected String func_70673_aS() {
        return "thaumcraft:gore";
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
}
