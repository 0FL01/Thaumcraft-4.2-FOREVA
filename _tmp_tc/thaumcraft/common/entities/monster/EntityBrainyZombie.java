/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;

public class EntityBrainyZombie
extends EntityZombie {
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(25.0);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0);
        this.func_110148_a(field_110186_bp).func_111128_a(0.0);
    }

    public EntityBrainyZombie(World world) {
        super(world);
        this.field_70715_bh.func_75776_a(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false));
    }

    public boolean func_70097_a(DamageSource par1DamageSource, float par2) {
        return super.func_70097_a(par1DamageSource, par2);
    }

    public void func_70074_a(EntityLivingBase par1EntityLivingBase) {
        super.func_70074_a(par1EntityLivingBase);
    }

    public int func_70658_aO() {
        int var1 = super.func_70658_aO() + 3;
        if (var1 > 20) {
            var1 = 20;
        }
        return var1;
    }

    protected void func_70628_a(boolean flag, int i) {
        for (int a = 0; a < 3; ++a) {
            if (!this.field_70170_p.field_73012_v.nextBoolean()) continue;
            this.func_145779_a(Items.field_151078_bh, 1);
        }
        if (this.field_70170_p.field_73012_v.nextInt(10) - i <= 4) {
            this.func_70099_a(new ItemStack(ConfigItems.itemZombieBrain), 1.5f);
        }
    }
}
