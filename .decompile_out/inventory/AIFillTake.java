/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraftforge.common.util.ForgeDirection
 */
package thaumcraft.common.entities.ai.inventory;

import java.util.ArrayList;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.lib.utils.InventoryUtils;

public class AIFillTake
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIFillTake(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        if (this.theGolem.getCarried() != null || this.theGolem.itemWatched == null || !this.theGolem.func_70661_as().func_75500_f() || !this.theGolem.hasSomething()) {
            return false;
        }
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        ArrayList<IInventory> mc = GolemHelper.getMarkedContainersAdjacentToGolem(this.theGolem.field_70170_p, this.theGolem);
        for (IInventory te : mc) {
            TileEntity tile = (TileEntity)te;
            if (tile == null || tile.field_145851_c == cX && tile.field_145848_d == cY && tile.field_145849_e == cZ) continue;
            ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemWatched);
            for (byte color : matchingColors) {
                for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, color)) {
                    ItemStack target = this.theGolem.itemWatched.func_77946_l();
                    target.field_77994_a = this.theGolem.getToggles()[0] ? this.theGolem.getCarrySpace() : Math.min(target.field_77994_a, this.theGolem.getCarrySpace());
                    ItemStack result = InventoryUtils.extractStack(te, target, side, this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT(), true);
                    if (result == null && InventoryUtils.getDoubleChest(tile) != null) {
                        result = InventoryUtils.extractStack((IInventory)InventoryUtils.getDoubleChest(tile), target, side, this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT(), true);
                    }
                    if (result == null) continue;
                    this.theGolem.setCarried(result);
                    try {
                        if (Config.golemChestInteract) {
                            this.inv.func_70295_k_();
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                    this.countChest = 5;
                    this.count = 200;
                    this.theGolem.itemWatched = null;
                    this.theGolem.updateCarried();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean func_75253_b() {
        return this.count > 0 && (!this.theGolem.func_70661_as().func_75500_f() || this.countChest > 0);
    }

    public void func_75246_d() {
        --this.count;
        --this.countChest;
        super.func_75246_d();
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

    public void func_75249_e() {
    }
}
