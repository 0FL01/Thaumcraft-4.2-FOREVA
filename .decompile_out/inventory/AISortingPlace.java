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

public class AISortingPlace
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    private int xx;
    private int yy;
    private int zz;
    int count = 0;

    public AISortingPlace(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        if (this.theGolem.itemCarried == null || !this.theGolem.func_70661_as().func_75500_f()) {
            return false;
        }
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        ArrayList<IInventory> mc = GolemHelper.getMarkedContainersAdjacentToGolem(this.theGolem.field_70170_p, this.theGolem);
        for (IInventory te : mc) {
            ItemStack is;
            TileEntity tile = (TileEntity)te;
            if (tile == null || tile.field_145851_c == cX && tile.field_145848_d == cY && tile.field_145849_e == cZ) continue;
            for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, (byte)-1)) {
                is = InventoryUtils.placeItemStackIntoInventory(this.theGolem.itemCarried, te, side, false);
                if (ItemStack.func_77989_b((ItemStack)is, (ItemStack)this.theGolem.itemCarried) || !InventoryUtils.inventoryContains(te, this.theGolem.itemCarried, side, this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT())) continue;
                this.xx = tile.field_145851_c;
                this.yy = tile.field_145848_d;
                this.zz = tile.field_145849_e;
                return true;
            }
            if (InventoryUtils.getDoubleChest(tile) == null) continue;
            for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, (byte)-1)) {
                is = InventoryUtils.placeItemStackIntoInventory(this.theGolem.itemCarried, (IInventory)InventoryUtils.getDoubleChest(tile), side, false);
                if (ItemStack.func_77989_b((ItemStack)is, (ItemStack)this.theGolem.itemCarried) || !InventoryUtils.inventoryContains(te, this.theGolem.itemCarried, side, this.theGolem.checkOreDict(), this.theGolem.ignoreDamage(), this.theGolem.ignoreNBT())) continue;
                this.xx = tile.field_145851_c;
                this.yy = tile.field_145848_d;
                this.zz = tile.field_145849_e;
                return true;
            }
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
        TileEntity tile = this.theGolem.field_70170_p.func_147438_o(this.xx, this.yy, this.zz);
        if (tile != null && (tile.field_145851_c != cX || tile.field_145848_d != cY || tile.field_145849_e != cZ)) {
            IInventory te = (IInventory)tile;
            ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemCarried);
            for (byte color : matchingColors) {
                for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, color)) {
                    this.theGolem.itemCarried = InventoryUtils.placeItemStackIntoInventory(this.theGolem.itemCarried, te, side, true);
                    this.countChest = 5;
                    this.inv = (IInventory)tile;
                    if (this.theGolem.itemCarried != null) continue;
                    break;
                }
                if (InventoryUtils.getDoubleChest(tile) != null && this.theGolem.itemCarried != null) {
                    for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, color)) {
                        ItemStack is = InventoryUtils.placeItemStackIntoInventory(this.theGolem.itemCarried, (IInventory)InventoryUtils.getDoubleChest(tile), side, false);
                        if (ItemStack.func_77989_b((ItemStack)is, (ItemStack)this.theGolem.itemCarried)) continue;
                        this.theGolem.itemCarried = InventoryUtils.placeItemStackIntoInventory(this.theGolem.itemCarried, (IInventory)InventoryUtils.getDoubleChest(tile), side, true);
                        this.countChest = 5;
                        this.inv = InventoryUtils.getDoubleChest(tile);
                        if (this.theGolem.itemCarried != null) continue;
                        break;
                    }
                }
                if (this.countChest != 5) continue;
                try {
                    if (!Config.golemChestInteract) break;
                    ((IInventory)tile).func_70295_k_();
                }
                catch (Exception e) {}
                break;
            }
        }
        this.theGolem.updateCarried();
    }
}
