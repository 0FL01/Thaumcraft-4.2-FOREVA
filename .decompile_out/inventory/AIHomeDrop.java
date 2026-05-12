/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraftforge.common.util.ForgeDirection
 */
package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIHomeDrop
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIHomeDrop(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        if (this.theGolem.getCarried() == null || !this.theGolem.func_70661_as().func_75500_f() || this.theGolem.func_70092_e((float)home.field_71574_a + 0.5f, (float)home.field_71572_b + 0.5f, (float)home.field_71573_c + 0.5f) > 5.0) {
            return false;
        }
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        TileEntity tile = this.theGolem.field_70170_p.func_147438_o(cX, cY, cZ);
        return tile == null || !(tile instanceof IInventory);
    }

    public boolean func_75253_b() {
        return this.count > 0 && (this.func_75250_a() || this.countChest > 0);
    }

    public void func_75251_c() {
        try {
            if (this.inv != null && Config.golemChestInteract) {
                this.inv.func_70305_f();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void func_75246_d() {
        --this.countChest;
        --this.count;
        super.func_75246_d();
    }

    public void func_75249_e() {
        this.count = 200;
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        EntityItem item = new EntityItem(this.theGolem.field_70170_p, this.theGolem.field_70165_t, this.theGolem.field_70163_u + (double)(this.theGolem.field_70131_O / 2.0f), this.theGolem.field_70161_v, this.theGolem.itemCarried.func_77946_l());
        if (item != null) {
            double distance = this.theGolem.func_70011_f((double)cX + 0.5, (double)cY + 0.5, (double)cZ + 0.5);
            item.field_70159_w = ((double)cX + 0.5 - this.theGolem.field_70165_t) * (distance / 3.0);
            item.field_70181_x = 0.1 + ((double)cY + 0.5 - (this.theGolem.field_70163_u + (double)(this.theGolem.field_70131_O / 2.0f))) * (distance / 3.0);
            item.field_70179_y = ((double)cZ + 0.5 - this.theGolem.field_70161_v) * (distance / 3.0);
            item.field_145804_b = 10;
            this.theGolem.field_70170_p.func_72838_d((Entity)item);
            this.theGolem.itemCarried = null;
            this.theGolem.startActionTimer();
            this.theGolem.updateCarried();
        }
    }
}
