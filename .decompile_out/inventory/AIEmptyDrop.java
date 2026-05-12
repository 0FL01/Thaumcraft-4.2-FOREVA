/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.util.ChunkCoordinates
 */
package thaumcraft.common.entities.ai.inventory;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ChunkCoordinates;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

public class AIEmptyDrop
extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;

    public AIEmptyDrop(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        if (this.theGolem.itemCarried == null || !this.theGolem.func_70661_as().func_75500_f()) {
            return false;
        }
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemCarried);
        for (byte color : matchingColors) {
            ArrayList<ChunkCoordinates> mc = GolemHelper.getMarkedBlocksAdjacentToGolem(this.theGolem.field_70170_p, this.theGolem, color);
            for (ChunkCoordinates cc : mc) {
                if (cc == home) continue;
                return true;
            }
        }
        return false;
    }

    public boolean func_75253_b() {
        return this.count > 0 && this.func_75250_a();
    }

    public void func_75251_c() {
    }

    public void func_75246_d() {
        --this.count;
        super.func_75246_d();
    }

    public void func_75249_e() {
        this.count = 200;
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemCarried);
        block0: for (byte color : matchingColors) {
            ArrayList<ChunkCoordinates> mc = GolemHelper.getMarkedBlocksAdjacentToGolem(this.theGolem.field_70170_p, this.theGolem, color);
            for (ChunkCoordinates cc : mc) {
                EntityItem item;
                if (cc == home || (item = new EntityItem(this.theGolem.field_70170_p, this.theGolem.field_70165_t, this.theGolem.field_70163_u + (double)(this.theGolem.field_70131_O / 2.0f), this.theGolem.field_70161_v, this.theGolem.itemCarried.func_77946_l())) == null) continue;
                double distance = this.theGolem.func_70011_f((double)cc.field_71574_a + 0.5, (double)cc.field_71572_b + 0.5, (double)cc.field_71573_c + 0.5);
                item.field_70159_w = ((double)cc.field_71574_a + 0.5 - this.theGolem.field_70165_t) * (distance / 3.0);
                item.field_70181_x = 0.1 + ((double)cc.field_71572_b + 0.5 - (this.theGolem.field_70163_u + (double)(this.theGolem.field_70131_O / 2.0f))) * (distance / 3.0);
                item.field_70179_y = ((double)cc.field_71573_c + 0.5 - this.theGolem.field_70161_v) * (distance / 3.0);
                item.field_145804_b = 10;
                this.theGolem.field_70170_p.func_72838_d((Entity)item);
                this.theGolem.itemCarried = null;
                this.theGolem.startActionTimer();
                break block0;
            }
        }
        this.theGolem.updateCarried();
    }
}
