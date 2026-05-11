/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySpecialItem
extends EntityItem {
    public EntitySpecialItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        super(par1World);
        this.func_70105_a(0.25f, 0.25f);
        this.field_70129_M = this.field_70131_O / 2.0f;
        this.func_70107_b(par2, par4, par6);
        this.func_92058_a(par8ItemStack);
        this.field_70177_z = (float)(Math.random() * 360.0);
        this.field_70159_w = (float)(Math.random() * (double)0.2f - (double)0.1f);
        this.field_70181_x = 0.2f;
        this.field_70179_y = (float)(Math.random() * (double)0.2f - (double)0.1f);
    }

    public EntitySpecialItem(World par1World) {
        super(par1World);
        this.func_70105_a(0.25f, 0.25f);
        this.field_70129_M = this.field_70131_O / 2.0f;
    }

    public void func_70071_h_() {
        if (this.field_70181_x > 0.0) {
            this.field_70181_x *= (double)0.9f;
        }
        this.field_70181_x += (double)0.04f;
        super.func_70071_h_();
    }

    public boolean func_70097_a(DamageSource p_70097_1_, float p_70097_2_) {
        if (p_70097_1_.func_94541_c()) {
            return false;
        }
        return super.func_70097_a(p_70097_1_, p_70097_2_);
    }
}
