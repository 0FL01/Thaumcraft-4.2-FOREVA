/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.RandomPositionGenerator
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.oredict.OreDictionary
 */
package thaumcraft.common.entities.ai.inventory;

import java.util.ArrayList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

public class AIFillGoto
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private ChunkCoordinates dest = null;
    int count = 0;
    int prevX = 0;
    int prevY = 0;
    int prevZ = 0;

    public AIFillGoto(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.func_75248_a(3);
    }

    public boolean func_75250_a() {
        if (this.theGolem.getCarried() != null || this.theGolem.field_70173_aa % Config.golemDelay > 0 || !this.theGolem.hasSomething()) {
            return false;
        }
        ArrayList<ItemStack> mi = GolemHelper.getMissingItems(this.theGolem);
        if (mi == null || mi.size() == 0) {
            return false;
        }
        ArrayList<ItemStack> missingItems = new ArrayList<ItemStack>();
        if (this.theGolem.getUpgradeAmount(5) > 0) {
            for (ItemStack stack : mi) {
                int od = OreDictionary.getOreID((ItemStack)stack);
                if (od != -1) {
                    ItemStack[] ores;
                    for (ItemStack ore : ores = OreDictionary.getOres((Integer)od).toArray(new ItemStack[0])) {
                        missingItems.add(ore.func_77946_l());
                    }
                    continue;
                }
                missingItems.add(stack.func_77946_l());
            }
        } else {
            for (ItemStack stack : mi) {
                missingItems.add(stack.func_77946_l());
            }
        }
        ArrayList<Object> results = new ArrayList();
        for (ItemStack stack : missingItems) {
            this.theGolem.itemWatched = stack.func_77946_l();
            ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemWatched);
            for (byte color : matchingColors) {
                results = GolemHelper.getContainersWithGoods(this.theGolem.field_70170_p, this.theGolem, this.theGolem.itemWatched, color);
            }
            if (results.size() <= 0) continue;
            break;
        }
        if (results == null || results.size() == 0) {
            return false;
        }
        ForgeDirection facing = ForgeDirection.getOrientation((int)this.theGolem.homeFacing);
        ChunkCoordinates home = this.theGolem.func_110172_bL();
        int cX = home.field_71574_a - facing.offsetX;
        int cY = home.field_71572_b - facing.offsetY;
        int cZ = home.field_71573_c - facing.offsetZ;
        int tX = 0;
        int tY = 0;
        int tZ = 0;
        double range = Double.MAX_VALUE;
        float dmod = this.theGolem.getRange();
        for (IInventory iInventory : results) {
            TileEntity te = (TileEntity)iInventory;
            double distance = this.theGolem.func_70092_e((double)te.field_145851_c + 0.5, (double)te.field_145848_d + 0.5, (double)te.field_145849_e + 0.5);
            if (!(distance < range) || !(distance <= (double)(dmod * dmod)) || te.field_145851_c == cX && te.field_145848_d == cY && te.field_145849_e == cZ) continue;
            range = distance;
            tX = te.field_145851_c;
            tY = te.field_145848_d;
            tZ = te.field_145849_e;
            this.dest = new ChunkCoordinates(tX, tY, tZ);
        }
        if (this.dest != null) {
            this.movePosX = tX;
            this.movePosY = tY;
            this.movePosZ = tZ;
            return true;
        }
        return false;
    }

    public boolean func_75253_b() {
        return this.count > 0 && !this.theGolem.func_70661_as().func_75500_f();
    }

    public void func_75246_d() {
        Vec3 var2;
        --this.count;
        if (this.count == 0 && this.prevX == MathHelper.func_76128_c((double)this.theGolem.field_70165_t) && this.prevY == MathHelper.func_76128_c((double)this.theGolem.field_70163_u) && this.prevZ == MathHelper.func_76128_c((double)this.theGolem.field_70161_v) && (var2 = RandomPositionGenerator.func_75463_a((EntityCreature)this.theGolem, (int)2, (int)1)) != null) {
            this.count = 20;
            this.theGolem.func_70661_as().func_75492_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, (double)this.theGolem.func_70689_ay());
        }
        super.func_75246_d();
    }

    public void func_75251_c() {
        this.dest = null;
        this.count = 0;
    }

    public void func_75249_e() {
        this.count = 200;
        this.prevX = MathHelper.func_76128_c((double)this.theGolem.field_70165_t);
        this.prevY = MathHelper.func_76128_c((double)this.theGolem.field_70163_u);
        this.prevZ = MathHelper.func_76128_c((double)this.theGolem.field_70161_v);
        this.theGolem.func_70661_as().func_75492_a(this.movePosX, this.movePosY, this.movePosZ, (double)this.theGolem.func_70689_ay());
    }
}
