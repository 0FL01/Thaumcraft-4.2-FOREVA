/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFishFood$FishType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 *  net.minecraft.util.WeightedRandom
 *  net.minecraft.util.WeightedRandomFishable
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.util.ForgeDirection
 */
package thaumcraft.common.entities.ai.interact;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityGolemBobber;

public class AIFish
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private float quality;
    private float distance;
    private World theWorld;
    private int maxDelay = 1;
    private int mod = 1;
    private int count = 0;
    private Vec3 target = null;
    private EntityGolemBobber bobber = null;
    private static final List LOOTCRAP = Arrays.asList(new WeightedRandomFishable(new ItemStack((Item)Items.field_151021_T), 10).func_150709_a(0.9f), new WeightedRandomFishable(new ItemStack(Items.field_151116_aA), 10), new WeightedRandomFishable(new ItemStack(Items.field_151103_aS), 10), new WeightedRandomFishable(new ItemStack((Item)Items.field_151068_bn), 10), new WeightedRandomFishable(new ItemStack(Items.field_151007_F), 5), new WeightedRandomFishable(new ItemStack((Item)Items.field_151112_aM), 2).func_150709_a(0.9f), new WeightedRandomFishable(new ItemStack(Items.field_151054_z), 10), new WeightedRandomFishable(new ItemStack(Items.field_151055_y), 5), new WeightedRandomFishable(new ItemStack(Items.field_151100_aR, 10, 0), 5), new WeightedRandomFishable(new ItemStack((Block)Blocks.field_150479_bC), 10), new WeightedRandomFishable(new ItemStack(Items.field_151078_bh), 10));
    private static final List LOOTRARE = Arrays.asList(new WeightedRandomFishable(new ItemStack(Blocks.field_150392_bi), 1), new WeightedRandomFishable(new ItemStack(Items.field_151057_cb), 1), new WeightedRandomFishable(new ItemStack(Items.field_151141_av), 1), new WeightedRandomFishable(new ItemStack((Item)Items.field_151031_f), 1).func_150709_a(0.25f).func_150707_a(), new WeightedRandomFishable(new ItemStack((Item)Items.field_151112_aM), 1).func_150709_a(0.25f).func_150707_a(), new WeightedRandomFishable(new ItemStack(Items.field_151122_aG), 1).func_150707_a());
    private static final List LOOTFISH = Arrays.asList(new WeightedRandomFishable(new ItemStack(Items.field_151115_aP, 1, ItemFishFood.FishType.COD.func_150976_a()), 60), new WeightedRandomFishable(new ItemStack(Items.field_151115_aP, 1, ItemFishFood.FishType.SALMON.func_150976_a()), 25), new WeightedRandomFishable(new ItemStack(Items.field_151115_aP, 1, ItemFishFood.FishType.CLOWNFISH.func_150976_a()), 2), new WeightedRandomFishable(new ItemStack(Items.field_151115_aP, 1, ItemFishFood.FishType.PUFFERFISH.func_150976_a()), 13));

    public AIFish(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.theWorld = par1EntityCreature.field_70170_p;
        this.func_75248_a(3);
        this.distance = MathHelper.func_76123_f((float)(this.theGolem.getRange() / 2.0f));
    }

    public boolean func_75250_a() {
        Vec3 vv;
        if (this.target != null || this.count > 0 || this.theGolem.field_70173_aa % Config.golemDelay > 0 || !this.theGolem.func_70661_as().func_75500_f()) {
            return false;
        }
        if (this.bobber != null) {
            this.bobber.func_70106_y();
        }
        if ((vv = this.findWater()) == null) {
            return false;
        }
        this.target = Vec3.func_72443_a((double)vv.field_72450_a, (double)vv.field_72448_b, (double)vv.field_72449_c);
        this.quality = 0.0f;
        int x = (int)this.target.field_72450_a;
        int y = (int)this.target.field_72448_b;
        int z = (int)this.target.field_72449_c;
        for (int a = 2; a <= 5; ++a) {
            ForgeDirection dir = ForgeDirection.getOrientation((int)a);
            if (this.theWorld.func_147439_a(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).func_149688_o() != Material.field_151586_h || !this.theWorld.func_147437_c(x + dir.offsetX, y + 1 + dir.offsetY, z + dir.offsetZ)) continue;
            this.quality += 3.0E-5f;
            if (this.theWorld.func_72937_j(x + dir.offsetX, y + 1 + dir.offsetY, z + dir.offsetZ)) {
                this.quality += 3.0E-5f;
            }
            for (int depth = 1; depth <= 3; ++depth) {
                if (this.theWorld.func_147439_a(x + dir.offsetX, y - depth + dir.offsetY, z + dir.offsetZ).func_149688_o() != Material.field_151586_h) continue;
                this.quality += 1.5E-5f;
            }
        }
        this.theWorld.func_72956_a((Entity)this.theGolem, "random.bow", 0.5f, 0.4f / (this.theWorld.field_73012_v.nextFloat() * 0.4f + 0.8f));
        this.bobber = new EntityGolemBobber(this.theWorld, this.theGolem, x, y, z);
        return this.theWorld.func_72838_d((Entity)this.bobber);
    }

    public boolean func_75253_b() {
        return this.bobber != null && !this.bobber.field_70128_L && this.target != null && this.count-- > 0;
    }

    public void func_75246_d() {
        if (this.target != null) {
            this.theGolem.func_70671_ap().func_75650_a(this.target.field_72450_a + 0.5, this.target.field_72448_b + 1.0, this.target.field_72449_c + 0.5, 30.0f, 30.0f);
            float chance = this.quality + (float)this.theGolem.getGolemStrength() * 1.5E-4f;
            if (this.theWorld.field_73012_v.nextFloat() < chance) {
                this.theGolem.startRightArmTimer();
                int qq = 1;
                if (this.theGolem.getUpgradeAmount(0) > 0 && this.theWorld.field_73012_v.nextInt(10) < this.theGolem.getUpgradeAmount(0)) {
                    ++qq;
                }
                for (int a = 0; a < qq; ++a) {
                    ItemStack sr;
                    ItemStack fs = this.getFishingResult();
                    if (this.theGolem.getUpgradeAmount(2) > 0 && (sr = FurnaceRecipes.func_77602_a().func_151395_a(fs)) != null) {
                        fs = sr.func_77946_l();
                    }
                    EntityItem entityitem = new EntityItem(this.theWorld, this.target.field_72450_a + 0.5, this.target.field_72448_b + 1.0, this.target.field_72449_c + 0.5, fs);
                    if (this.theGolem.getUpgradeAmount(2) > 0) {
                        entityitem.func_70015_d(2);
                    }
                    entityitem.field_145804_b = 20;
                    double d1 = this.theGolem.field_70165_t + (double)this.theWorld.field_73012_v.nextFloat() - (double)this.theWorld.field_73012_v.nextFloat() - this.target.field_72450_a + 0.5;
                    double d3 = this.theGolem.field_70163_u - this.target.field_72448_b + 1.0;
                    double d5 = this.theGolem.field_70161_v + (double)this.theWorld.field_73012_v.nextFloat() - (double)this.theWorld.field_73012_v.nextFloat() - this.target.field_72449_c + 0.5;
                    double d7 = MathHelper.func_76133_a((double)(d1 * d1 + d3 * d3 + d5 * d5));
                    double d9 = 0.1;
                    entityitem.field_70159_w = d1 * d9;
                    entityitem.field_70181_x = d3 * d9 + (double)MathHelper.func_76133_a((double)d7) * 0.08;
                    entityitem.field_70179_y = d5 * d9;
                    this.theWorld.func_72838_d((Entity)entityitem);
                }
                if (this.bobber != null) {
                    this.bobber.func_85030_a("random.splash", 0.15f, 1.0f + (this.theWorld.field_73012_v.nextFloat() - this.theWorld.field_73012_v.nextFloat()) * 0.4f);
                    ((WorldServer)this.theWorld).func_147487_a("splash", this.bobber.field_70165_t, this.bobber.field_70163_u + 0.5, this.bobber.field_70161_v, 20 + this.theWorld.field_73012_v.nextInt(20), (double)0.1f, 0.0, (double)0.1f, 0.0);
                    this.bobber.func_70106_y();
                }
                this.target = null;
            }
        }
    }

    public void func_75251_c() {
        if (this.bobber != null) {
            this.bobber.func_70106_y();
        }
        this.target = null;
        this.count = -1;
    }

    public void func_75249_e() {
        this.count = 300 + this.theWorld.field_73012_v.nextInt(200);
        this.theGolem.startRightArmTimer();
    }

    private Vec3 findWater() {
        Random rand = this.theGolem.func_70681_au();
        int var2 = 0;
        while ((float)var2 < this.distance * 2.0f) {
            int z;
            int y;
            int x = (int)((float)(this.theGolem.func_110172_bL().field_71574_a + rand.nextInt((int)(1.0f + this.distance * 2.0f))) - this.distance);
            if (this.theWorld.func_147439_a(x, y = (int)((float)(this.theGolem.func_110172_bL().field_71572_b + rand.nextInt((int)(1.0f + this.distance))) - this.distance / 2.0f), z = (int)((float)(this.theGolem.func_110172_bL().field_71573_c + rand.nextInt((int)(1.0f + this.distance * 2.0f))) - this.distance)).func_149688_o() == Material.field_151586_h && this.theWorld.func_147437_c(x, y + 1, z)) {
                Vec3 v = Vec3.func_72443_a((double)x, (double)y, (double)z);
                return v;
            }
            ++var2;
        }
        return null;
    }

    private ItemStack getFishingResult() {
        float f = this.theWorld.field_73012_v.nextFloat();
        float f1 = 0.1f - (float)this.theGolem.getUpgradeAmount(5) * 0.025f;
        float f2 = 0.05f + (float)this.theGolem.getUpgradeAmount(4) * 0.0125f;
        int x = (int)this.target.field_72450_a;
        int y = (int)this.target.field_72448_b;
        int z = (int)this.target.field_72449_c;
        for (int a = 2; a <= 5; ++a) {
            ForgeDirection dir = ForgeDirection.getOrientation((int)a);
            if (this.theWorld.func_147439_a(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).func_149688_o() != Material.field_151586_h || !this.theWorld.func_147437_c(x + dir.offsetX, y + 1 + dir.offsetY, z + dir.offsetZ)) continue;
            f1 -= 0.005f;
            f2 += 0.00125f;
            if (this.theWorld.func_72937_j(x + dir.offsetX, y + 1 + dir.offsetY, z + dir.offsetZ)) {
                f1 -= 0.005f;
                f2 += 0.00125f;
            }
            for (int depth = 1; depth <= 3; ++depth) {
                if (this.theWorld.func_147439_a(x + dir.offsetX, y - depth + dir.offsetY, z + dir.offsetZ).func_149688_o() != Material.field_151586_h) continue;
                f2 += 0.001f;
            }
        }
        f1 = MathHelper.func_76131_a((float)f1, (float)0.0f, (float)1.0f);
        f2 = MathHelper.func_76131_a((float)f2, (float)0.0f, (float)1.0f);
        if (f < f1) {
            return ((WeightedRandomFishable)WeightedRandom.func_76271_a((Random)this.theWorld.field_73012_v, (Collection)LOOTCRAP)).func_150708_a(this.theWorld.field_73012_v);
        }
        if ((f -= f1) < f2) {
            return ((WeightedRandomFishable)WeightedRandom.func_76271_a((Random)this.theWorld.field_73012_v, (Collection)LOOTRARE)).func_150708_a(this.theWorld.field_73012_v);
        }
        float f3 = f - f2;
        return ((WeightedRandomFishable)WeightedRandom.func_76271_a((Random)this.theWorld.field_73012_v, (Collection)LOOTFISH)).func_150708_a(this.theWorld.field_73012_v);
    }
}
