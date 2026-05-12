/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDirectional
 *  net.minecraft.block.BlockLog
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemSeedFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Direction
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.common.util.FakePlayerFactory
 *  net.minecraftforge.common.util.ForgeDirection
 */
package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.EntityUtils;

public class AIHarvestCrops
extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int xx;
    private int yy;
    private int zz;
    private float movementSpeed;
    private float distance;
    private World theWorld;
    private Block block = Blocks.field_150350_a;
    private int blockMd = 0;
    private int delay = -1;
    private int maxDelay = 1;
    private int mod = 1;
    private int count = 0;
    ArrayList<BlockCoordinates> checklist = new ArrayList();

    public AIHarvestCrops(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.theWorld = par1EntityCreature.field_70170_p;
        this.func_75248_a(3);
        this.distance = MathHelper.func_76123_f((float)(this.theGolem.getRange() / 4.0f));
    }

    public boolean func_75250_a() {
        if (this.delay >= 0 || this.theGolem.field_70173_aa % Config.golemDelay > 0 || !this.theGolem.func_70661_as().func_75500_f()) {
            return false;
        }
        Vec3 var1 = this.findGrownCrop();
        if (var1 == null) {
            return false;
        }
        this.xx = (int)var1.field_72450_a;
        this.yy = (int)var1.field_72448_b;
        this.zz = (int)var1.field_72449_c;
        this.block = this.theWorld.func_147439_a(this.xx, this.yy, this.zz);
        this.blockMd = this.theWorld.func_72805_g(this.xx, this.yy, this.zz);
        return true;
    }

    public boolean func_75253_b() {
        return this.theWorld.func_147439_a(this.xx, this.yy, this.zz) == this.block && this.theWorld.func_72805_g(this.xx, this.yy, this.zz) == this.blockMd && this.count-- > 0 && (this.delay > 0 || !this.theGolem.func_70661_as().func_75500_f());
    }

    public void func_75246_d() {
        double dist = this.theGolem.func_70092_e((double)this.xx + 0.5, (double)this.yy + 0.5, (double)this.zz + 0.5);
        this.theGolem.func_70671_ap().func_75650_a((double)this.xx + 0.5, (double)this.yy + 0.5, (double)this.zz + 0.5, 30.0f, 30.0f);
        if (dist <= 4.0) {
            if (this.delay < 0) {
                this.maxDelay = this.delay = (int)Math.max(10.0f, (20.0f - (float)this.theGolem.getGolemStrength() * 2.0f) * this.block.func_149712_f(this.theWorld, this.xx, this.yy, this.zz));
                this.mod = this.delay / Math.round((float)this.delay / 6.0f);
            }
            if (this.delay > 0) {
                if (--this.delay > 0 && this.delay % this.mod == 0 && this.theGolem.func_70661_as().func_75500_f()) {
                    this.theGolem.startActionTimer();
                    this.theWorld.func_72908_a((double)((float)this.xx + 0.5f), (double)((float)this.yy + 0.5f), (double)((float)this.zz + 0.5f), this.block.field_149762_H.func_150495_a(), (this.block.field_149762_H.func_150497_c() + 0.7f) / 8.0f, this.block.field_149762_H.func_150494_d() * 0.5f);
                    BlockUtils.destroyBlockPartially(this.theWorld, this.theGolem.func_145782_y(), this.xx, this.yy, this.zz, (int)(9.0f * (1.0f - (float)this.delay / (float)this.maxDelay)));
                }
                if (this.delay == 0) {
                    this.harvest();
                    this.checkAdjacent();
                }
            }
        }
    }

    private void checkAdjacent() {
        for (int x2 = -2; x2 <= 2; ++x2) {
            for (int z2 = -2; z2 <= 2; ++z2) {
                for (int y2 = -1; y2 <= 1; ++y2) {
                    Vec3 var1;
                    int x = this.xx + x2;
                    int y = this.yy + y2;
                    int z = this.zz + z2;
                    if ((float)Math.abs(this.theGolem.func_110172_bL().field_71574_a - x) > this.distance || (float)Math.abs(this.theGolem.func_110172_bL().field_71572_b - y) > this.distance || (float)Math.abs(this.theGolem.func_110172_bL().field_71573_c - z) > this.distance || !CropUtils.isGrownCrop(this.theWorld, x, y, z) || (var1 = Vec3.func_72443_a((double)x, (double)y, (double)z)) == null) continue;
                    this.xx = (int)var1.field_72450_a;
                    this.yy = (int)var1.field_72448_b;
                    this.zz = (int)var1.field_72449_c;
                    this.block = this.theWorld.func_147439_a(this.xx, this.yy, this.zz);
                    this.blockMd = this.theWorld.func_72805_g(this.xx, this.yy, this.zz);
                    this.delay = -1;
                    this.func_75249_e();
                    return;
                }
            }
        }
    }

    public void func_75251_c() {
        BlockUtils.destroyBlockPartially(this.theWorld, this.theGolem.func_145782_y(), this.xx, this.yy, this.zz, -1);
        this.delay = -1;
    }

    public void func_75249_e() {
        this.count = 200;
        this.theGolem.func_70661_as().func_75492_a((double)this.xx + 0.5, (double)this.yy + 0.5, (double)this.zz + 0.5, (double)this.theGolem.func_70689_ay());
    }

    private Vec3 findGrownCrop() {
        Random rand = this.theGolem.func_70681_au();
        if (this.checklist.size() == 0) {
            int a = (int)(-this.distance);
            while ((float)a <= this.distance) {
                int b = (int)(-this.distance);
                while ((float)b <= this.distance) {
                    this.checklist.add(new BlockCoordinates(this.theGolem.func_110172_bL().field_71574_a + a, 0, this.theGolem.func_110172_bL().field_71573_c + b));
                    ++b;
                }
                ++a;
            }
            Collections.shuffle(this.checklist, rand);
        }
        int x = this.checklist.get((int)0).x;
        int z = this.checklist.get((int)0).z;
        this.checklist.remove(0);
        for (int y = this.theGolem.func_110172_bL().field_71572_b - 3; y <= this.theGolem.func_110172_bL().field_71572_b + 3; ++y) {
            if (!CropUtils.isGrownCrop(this.theWorld, x, y, z)) continue;
            return Vec3.func_72443_a((double)x, (double)y, (double)z);
        }
        return null;
    }

    void harvest() {
        this.count = 200;
        int md = this.blockMd;
        FakePlayer fp = FakePlayerFactory.get((WorldServer)((WorldServer)this.theWorld), (GameProfile)new GameProfile((UUID)null, "FakeThaumcraftGolem"));
        fp.func_70107_b(this.theGolem.field_70165_t, this.theGolem.field_70163_u, this.theGolem.field_70161_v);
        if (CropUtils.clickableCrops.contains(this.block.func_149739_a() + md)) {
            this.block.func_149727_a(this.theWorld, this.xx, this.yy, this.zz, (EntityPlayer)fp, 0, 0.0f, 0.0f, 0.0f);
        } else {
            this.theWorld.func_147480_a(this.xx, this.yy, this.zz, true);
            if (this.theGolem.getUpgradeAmount(4) > 0) {
                ArrayList items = new ArrayList();
                ArrayList<Entity> drops = EntityUtils.getEntitiesInRange(this.theWorld, this.theGolem.field_70165_t, this.theGolem.field_70163_u, this.theGolem.field_70161_v, (Entity)this.theGolem, EntityItem.class, 6.0);
                if (drops.size() > 0) {
                    for (Entity e : drops) {
                        if (!(e instanceof EntityItem)) continue;
                        if (e.field_70173_aa < 2) {
                            Vec3 v = Vec3.func_72443_a((double)(e.field_70165_t - this.theGolem.field_70165_t), (double)(e.field_70163_u - this.theGolem.field_70163_u), (double)(e.field_70161_v - this.theGolem.field_70161_v));
                            v = v.func_72432_b();
                            e.field_70159_w = -v.field_72450_a / 4.0;
                            e.field_70181_x = 0.075;
                            e.field_70179_y = -v.field_72449_c / 4.0;
                        }
                        boolean done = false;
                        EntityItem item = (EntityItem)e;
                        ItemStack st = item.func_92059_d();
                        if (st.func_77973_b() != null && st.func_77973_b() == Items.field_151100_aR && st.func_77960_j() == 3) {
                            int par4;
                            int var5 = BlockDirectional.func_149895_l((int)this.blockMd);
                            int par2 = this.xx + Direction.field_71583_a[var5];
                            Block var6 = this.theWorld.func_147439_a(par2, this.yy, par4 = this.zz + Direction.field_71581_b[var5]);
                            if (var6 == Blocks.field_150364_r && BlockLog.func_150165_c((int)this.theWorld.func_72805_g(par2, this.yy, par4)) == 3) {
                                --st.field_77994_a;
                                this.theWorld.func_147465_d(this.xx, this.yy, this.zz, Blocks.field_150375_by, BlockDirectional.func_149895_l((int)this.blockMd), 3);
                            }
                            done = true;
                        } else if (st.func_77973_b() != null && st.func_77973_b() == ConfigItems.itemManaBean) {
                            if (this.block.func_149707_d(this.theWorld, this.xx, this.yy, this.zz, 0)) {
                                --st.field_77994_a;
                                if (!st.func_77973_b().func_77648_a(st.func_77946_l(), (EntityPlayer)fp, this.theWorld, this.xx, this.yy + 1, this.zz, 0, 0.5f, 0.5f, 0.5f)) {
                                    this.theWorld.func_147465_d(this.xx, this.yy, this.zz, ConfigBlocks.blockManaPod, 0, 3);
                                }
                            }
                            done = true;
                        } else {
                            int[] xm = new int[]{0, 0, 1, 1, -1, 0, -1, -1, 1};
                            int[] zm = new int[]{0, 1, 0, 1, 0, -1, -1, 1, -1};
                            for (int count = 0; st != null && st.field_77994_a > 0 && count < 9; ++count) {
                                if (st.func_77973_b() == null || !(st.func_77973_b() instanceof IPlantable) && !(st.func_77973_b() instanceof ItemSeedFood) || !st.func_77973_b().func_77648_a(st.func_77946_l(), (EntityPlayer)fp, this.theWorld, this.xx + xm[count], this.yy - 1, this.zz + zm[count], ForgeDirection.UP.ordinal(), 0.5f, 0.5f, 0.5f)) continue;
                                --st.field_77994_a;
                            }
                        }
                        if (st.field_77994_a <= 0) {
                            item.func_70106_y();
                        } else {
                            item.func_92058_a(st);
                        }
                        if (!done) continue;
                        break;
                    }
                }
            }
        }
        fp.func_70106_y();
        this.theGolem.startActionTimer();
    }
}
