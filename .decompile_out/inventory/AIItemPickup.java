/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 */
package thaumcraft.common.entities.ai.inventory;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.InventoryUtils;

public class AIItemPickup
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private Entity targetEntity;
    int count = 0;

    public AIItemPickup(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        if (this.theGolem.field_70173_aa % Config.golemDelay > 0) {
            return false;
        }
        return this.findItem();
    }

    private boolean findItem() {
        double range = Double.MAX_VALUE;
        float dmod = this.theGolem.getRange();
        List targets = this.theGolem.field_70170_p.func_72839_b((Entity)this.theGolem, AxisAlignedBB.func_72330_a((double)this.theGolem.func_110172_bL().field_71574_a, (double)this.theGolem.func_110172_bL().field_71572_b, (double)this.theGolem.func_110172_bL().field_71573_c, (double)(this.theGolem.func_110172_bL().field_71574_a + 1), (double)(this.theGolem.func_110172_bL().field_71572_b + 1), (double)(this.theGolem.func_110172_bL().field_71573_c + 1)).func_72314_b((double)dmod, (double)dmod, (double)dmod));
        if (targets.size() == 0) {
            return false;
        }
        for (Entity e : targets) {
            if (!(e instanceof EntityItem) || ((EntityItem)e).field_145804_b >= 5 || !this.theGolem.inventory.allEmpty() && this.theGolem.inventory.getAmountNeededSmart(((EntityItem)e).func_92059_d(), this.theGolem.getUpgradeAmount(5) > 0) <= 0 || this.theGolem.getCarried() != null && (!InventoryUtils.areItemStacksEqualStrict(this.theGolem.getCarried(), ((EntityItem)e).func_92059_d()) || ((EntityItem)e).func_92059_d().field_77994_a > this.theGolem.getCarrySpace())) continue;
            double distance = e.func_70092_e((double)((float)this.theGolem.func_110172_bL().field_71574_a + 0.5f), (double)((float)this.theGolem.func_110172_bL().field_71572_b + 0.5f), (double)((float)this.theGolem.func_110172_bL().field_71573_c + 0.5f));
            double distance2 = e.func_70092_e(this.theGolem.field_70165_t, this.theGolem.field_70163_u, this.theGolem.field_70161_v);
            if (!(distance2 < range) || !(distance <= (double)(dmod * dmod))) continue;
            range = distance2;
            this.targetEntity = e;
        }
        return this.targetEntity != null;
    }

    public boolean func_75253_b() {
        return this.count-- > 0 && !this.theGolem.func_70661_as().func_75500_f() && this.targetEntity.func_70089_S();
    }

    public void func_75251_c() {
        this.count = 0;
        this.targetEntity = null;
        this.theGolem.func_70661_as().func_75499_g();
    }

    public void func_75246_d() {
        this.theGolem.func_70671_ap().func_75651_a(this.targetEntity, 30.0f, 30.0f);
        double dist = this.theGolem.func_70068_e(this.targetEntity);
        if (dist <= 2.0) {
            this.pickUp();
        }
    }

    private void pickUp() {
        int amount = 0;
        if (this.targetEntity instanceof EntityItem) {
            ItemStack stack = ((EntityItem)this.targetEntity).func_92059_d().func_77946_l();
            amount = ((EntityItem)this.targetEntity).func_92059_d().field_77994_a < this.theGolem.getCarrySpace() ? ((EntityItem)this.targetEntity).func_92059_d().field_77994_a : this.theGolem.getCarrySpace();
            stack.field_77994_a = amount;
            ((EntityItem)this.targetEntity).func_92059_d().field_77994_a -= amount;
            if (this.theGolem.getCarried() == null) {
                this.theGolem.setCarried(stack);
            } else {
                this.theGolem.getCarried().field_77994_a += amount;
            }
        }
        if (amount == 0) {
            return;
        }
        this.targetEntity.field_70170_p.func_72956_a(this.targetEntity, "random.pop", 0.2f, ((this.targetEntity.field_70170_p.field_73012_v.nextFloat() - this.targetEntity.field_70170_p.field_73012_v.nextFloat()) * 0.7f + 1.0f) * 2.0f);
    }

    public void func_75249_e() {
        this.count = 200;
        this.theGolem.func_70661_as().func_75497_a(this.targetEntity, (double)this.theGolem.func_70689_ay());
    }
}
