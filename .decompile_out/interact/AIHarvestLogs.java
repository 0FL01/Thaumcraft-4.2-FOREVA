/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.common.util.FakePlayerFactory
 */
package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

public class AIHarvestLogs
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
    FakePlayer player;
    private int count = 0;

    public AIHarvestLogs(EntityGolemBase par1EntityCreature) {
        this.theGolem = par1EntityCreature;
        this.theWorld = par1EntityCreature.field_70170_p;
        this.func_75248_a(3);
        this.distance = MathHelper.func_76123_f((float)(this.theGolem.getRange() / 3.0f));
        if (this.theWorld instanceof WorldServer) {
            this.player = FakePlayerFactory.get((WorldServer)((WorldServer)this.theWorld), (GameProfile)new GameProfile((UUID)null, "FakeThaumcraftGolem"));
        }
    }

    public boolean func_75250_a() {
        if (this.delay >= 0 || this.theGolem.field_70173_aa % Config.golemDelay > 0 || !this.theGolem.func_70661_as().func_75500_f()) {
            return false;
        }
        Vec3 var1 = this.findLog();
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
        return this.theWorld.func_147439_a(this.xx, this.yy, this.zz) == this.block && this.theWorld.func_72805_g(this.xx, this.yy, this.zz) == this.blockMd && this.count-- > 0 && (this.delay > 0 || Utils.isWoodLog((IBlockAccess)this.theWorld, this.xx, this.yy, this.zz) || !this.theGolem.func_70661_as().func_75500_f());
    }

    public void func_75246_d() {
        double dist = this.theGolem.func_70092_e((double)this.xx + 0.5, (double)this.yy + 0.5, (double)this.zz + 0.5);
        this.theGolem.func_70671_ap().func_75650_a((double)this.xx + 0.5, (double)this.yy + 0.5, (double)this.zz + 0.5, 30.0f, 30.0f);
        if (dist <= 4.0) {
            if (this.delay < 0) {
                this.maxDelay = this.delay = (int)Math.max(5.0f, (20.0f - (float)this.theGolem.getGolemStrength() * 3.0f) * this.block.func_149712_f(this.theWorld, this.xx, this.yy, this.zz));
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
                    if (Utils.isWoodLog((IBlockAccess)this.theWorld, this.xx, this.yy, this.zz)) {
                        this.delay = -1;
                        this.block = this.theWorld.func_147439_a(this.xx, this.yy, this.zz);
                        this.blockMd = this.theWorld.func_72805_g(this.xx, this.yy, this.zz);
                        this.func_75249_e();
                    } else {
                        this.checkAdjacent();
                    }
                }
            }
        }
    }

    private void checkAdjacent() {
        for (int x2 = -1; x2 <= 1; ++x2) {
            for (int z2 = -1; z2 <= 1; ++z2) {
                for (int y2 = -1; y2 <= 1; ++y2) {
                    Vec3 var1;
                    int x = this.xx + x2;
                    int y = this.yy + y2;
                    int z = this.zz + z2;
                    if ((float)Math.abs(this.theGolem.func_110172_bL().field_71574_a - x) > this.distance || (float)Math.abs(this.theGolem.func_110172_bL().field_71572_b - y) > this.distance || (float)Math.abs(this.theGolem.func_110172_bL().field_71573_c - z) > this.distance || !Utils.isWoodLog((IBlockAccess)this.theWorld, x, y, z) || (var1 = Vec3.func_72443_a((double)x, (double)y, (double)z)) == null) continue;
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

    void harvest() {
        this.count = 200;
        this.theWorld.func_72926_e(2001, this.xx, this.yy, this.zz, Block.func_149682_b((Block)this.block) + (this.blockMd << 12));
        BlockUtils.breakFurthestBlock(this.theWorld, this.xx, this.yy, this.zz, this.block, (EntityPlayer)this.player);
        this.theGolem.startActionTimer();
    }

    private Vec3 findLog() {
        Random rand = this.theGolem.func_70681_au();
        int var2 = 0;
        while ((float)var2 < this.distance * 4.0f) {
            int z;
            int y;
            int x = (int)((float)(this.theGolem.func_110172_bL().field_71574_a + rand.nextInt((int)(1.0f + this.distance * 2.0f))) - this.distance);
            if (Utils.isWoodLog((IBlockAccess)this.theWorld, x, y = (int)((float)(this.theGolem.func_110172_bL().field_71572_b + rand.nextInt((int)(1.0f + this.distance))) - this.distance / 2.0f), z = (int)((float)(this.theGolem.func_110172_bL().field_71573_c + rand.nextInt((int)(1.0f + this.distance * 2.0f))) - this.distance))) {
                Vec3 v = Vec3.func_72443_a((double)x, (double)y, (double)z);
                double dist = this.theGolem.func_70092_e((double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
                int yy = 1;
                while (Utils.isWoodLog((IBlockAccess)this.theWorld, x, y - yy, z) && this.theGolem.func_70092_e((double)x + 0.5, (double)(y - yy) + 0.5, (double)z + 0.5) < dist) {
                    v = Vec3.func_72443_a((double)x, (double)(y - yy), (double)z);
                    dist = this.theGolem.func_70092_e((double)x + 0.5, (double)(y - yy) + 0.5, (double)z + 0.5);
                    ++yy;
                }
                return v;
            }
            ++var2;
        }
        return null;
    }
}
