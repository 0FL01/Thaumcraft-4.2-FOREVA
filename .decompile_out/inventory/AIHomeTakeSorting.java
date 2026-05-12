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

public class AIHomeTakeSorting
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIHomeTakeSorting(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        if (this.theGolem.getCarried() != null || this.theGolem.field_70173_aa % Config.golemDelay > 0 || !this.theGolem.func_70661_as().func_75500_f() || this.theGolem.func_70092_e((float)home.field_71574_a + 0.5f, (float)home.field_71572_b + 0.5f, (float)home.field_71573_c + 0.5f) > 5.0) {
            return false;
        }
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        TileEntity tile = this.theGolem.field_70170_p.func_147438_o(cX, cY, cZ);
        boolean repeat = true;
        boolean didRepeat = false;
        while (repeat) {
            ArrayList<ItemStack> neededList;
            if (didRepeat) {
                repeat = false;
            }
            if (tile != null && tile instanceof IInventory && (neededList = GolemHelper.getItemsNeeded(this.theGolem, this.theGolem.getUpgradeAmount(5) > 0)) != null && neededList.size() > 0) {
                for (ItemStack stack : neededList) {
                    ItemStack needed = stack.func_77946_l();
                    needed.field_77994_a = this.theGolem.getCarrySpace();
                    if (InventoryUtils.extractStack((IInventory)tile, needed, facing.ordinal(), this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT(), false) == null) continue;
                    return true;
                }
            }
            if (!didRepeat && InventoryUtils.getDoubleChest(tile) != null) {
                tile = InventoryUtils.getDoubleChest(tile);
                didRepeat = true;
                continue;
            }
            repeat = false;
        }
        return false;
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
        TileEntity tile = this.theGolem.field_70170_p.func_147438_o(cX, cY, cZ);
        boolean repeat = true;
        boolean didRepeat = false;
        while (repeat) {
            if (didRepeat) {
                repeat = false;
            }
            if (tile != null && tile instanceof IInventory) {
                ArrayList<ItemStack> neededList = GolemHelper.getItemsNeeded(this.theGolem, this.theGolem.getUpgradeAmount(5) > 0);
                if (neededList != null && neededList.size() > 0) {
                    for (ItemStack stack : neededList) {
                        ItemStack needed = stack.func_77946_l();
                        needed.field_77994_a = this.theGolem.getCarrySpace();
                        ItemStack result = InventoryUtils.extractStack((IInventory)tile, needed, facing.ordinal(), this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT(), true);
                        if (result == null) continue;
                        this.theGolem.setCarried(result);
                        try {
                            if (Config.golemChestInteract) {
                                ((IInventory)tile).func_70295_k_();
                            }
                        }
                        catch (Exception e) {
                            // empty catch block
                        }
                        this.countChest = 5;
                        this.inv = (IInventory)tile;
                        break;
                    }
                }
                if (this.theGolem.getCarried() != null) break;
            }
            if (!didRepeat && InventoryUtils.getDoubleChest(tile) != null) {
                tile = InventoryUtils.getDoubleChest(tile);
                didRepeat = true;
                continue;
            }
            repeat = false;
        }
    }
}
