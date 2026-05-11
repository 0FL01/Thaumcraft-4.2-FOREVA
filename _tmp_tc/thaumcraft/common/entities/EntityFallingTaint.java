/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.crash.CrashReportCategory
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package thaumcraft.common.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockTaint;
import thaumcraft.common.config.ConfigBlocks;

public class EntityFallingTaint
extends Entity
implements IEntityAdditionalSpawnData {
    public Block block;
    public int metadata;
    public int oldX;
    public int oldY;
    public int oldZ;
    public int fallTime = 0;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0f;

    public EntityFallingTaint(World par1World) {
        super(par1World);
    }

    public EntityFallingTaint(World par1World, double par2, double par4, double par6, Block par8, int par9, int ox, int oy, int oz) {
        super(par1World);
        this.block = par8;
        this.metadata = par9;
        this.field_70156_m = true;
        this.func_70105_a(0.98f, 0.98f);
        this.field_70129_M = this.field_70131_O / 2.0f;
        this.func_70107_b(par2, par4, par6);
        this.field_70159_w = 0.0;
        this.field_70181_x = 0.0;
        this.field_70179_y = 0.0;
        this.field_70169_q = par2;
        this.field_70167_r = par4;
        this.field_70166_s = par6;
        this.oldX = ox;
        this.oldY = oy;
        this.oldZ = oz;
    }

    protected boolean func_70041_e_() {
        return false;
    }

    protected void func_70088_a() {
    }

    public void writeSpawnData(ByteBuf data) {
        data.writeInt(Block.func_149682_b((Block)this.block));
        data.writeByte(this.metadata);
    }

    public void readSpawnData(ByteBuf data) {
        try {
            this.block = Block.func_149729_e((int)data.readInt());
            this.metadata = data.readByte();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean func_70067_L() {
        return !this.field_70128_L;
    }

    public void func_70071_h_() {
        block5: {
            block6: {
                int j;
                block7: {
                    block4: {
                        if (this.block != null && this.block != Blocks.field_150350_a) break block4;
                        this.func_70106_y();
                        break block5;
                    }
                    this.field_70169_q = this.field_70165_t;
                    this.field_70167_r = this.field_70163_u;
                    this.field_70166_s = this.field_70161_v;
                    ++this.fallTime;
                    this.field_70181_x -= (double)0.04f;
                    this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
                    this.field_70159_w *= (double)0.98f;
                    this.field_70181_x *= (double)0.98f;
                    this.field_70179_y *= (double)0.98f;
                    if (this.field_70170_p.field_72995_K) break block6;
                    int i = MathHelper.func_76128_c((double)this.field_70165_t);
                    j = MathHelper.func_76128_c((double)this.field_70163_u);
                    int k = MathHelper.func_76128_c((double)this.field_70161_v);
                    if (this.fallTime == 1) {
                        if (this.field_70170_p.func_147439_a(this.oldX, this.oldY, this.oldZ) != this.block) {
                            this.func_70106_y();
                            return;
                        }
                        this.field_70170_p.func_147468_f(this.oldX, this.oldY, this.oldZ);
                    }
                    if (!this.field_70122_E && (this.field_70170_p.func_147439_a(i, j - 1, k) != ConfigBlocks.blockFluxGoo || this.field_70170_p.func_72805_g(i, j - 1, k) < 4)) break block7;
                    this.field_70159_w *= (double)0.7f;
                    this.field_70179_y *= (double)0.7f;
                    this.field_70181_x *= -0.5;
                    if (this.field_70170_p.func_147439_a(i, j, k) == Blocks.field_150331_J || this.field_70170_p.func_147439_a(i, j, k) == Blocks.field_150326_M || this.field_70170_p.func_147439_a(i, j, k) == Blocks.field_150332_K) break block5;
                    this.field_70170_p.func_72956_a((Entity)this, "thaumcraft:gore", 0.5f, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    this.func_70106_y();
                    if (!this.canPlace(i, j, k) || BlockTaint.canFallBelow(this.field_70170_p, i, j - 1, k) || !this.field_70170_p.func_147465_d(i, j, k, this.block, this.metadata, 3) || !(this.block instanceof BlockTaint)) break block5;
                    ((BlockTaint)this.block).onFinishFalling(this.field_70170_p, i, j, k, this.metadata);
                    break block5;
                }
                if ((this.fallTime <= 100 || this.field_70170_p.field_72995_K || j >= 1 && j <= 256) && this.fallTime <= 600) break block5;
                this.func_70106_y();
                break block5;
            }
            if (this.field_70122_E || this.fallTime == 1) {
                for (int j = 0; j < 10; ++j) {
                    Thaumcraft.proxy.taintLandFX(this);
                }
            }
        }
    }

    private boolean canPlace(int i, int j, int k) {
        return this.field_70170_p.func_147439_a(i, j, k) == ConfigBlocks.blockTaintFibres || this.field_70170_p.func_147439_a(i, j, k) == ConfigBlocks.blockFluxGoo || this.field_70170_p.func_147472_a(this.block, i, j, k, true, 1, (Entity)null, (ItemStack)null);
    }

    protected void func_70069_a(float par1) {
    }

    protected void func_70014_b(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.func_74768_a("TileID", Block.func_149682_b((Block)this.block));
        par1NBTTagCompound.func_74774_a("Data", (byte)this.metadata);
        par1NBTTagCompound.func_74774_a("Time", (byte)this.fallTime);
        par1NBTTagCompound.func_74776_a("FallHurtAmount", this.fallHurtAmount);
        par1NBTTagCompound.func_74768_a("FallHurtMax", this.fallHurtMax);
        par1NBTTagCompound.func_74768_a("OldX", this.oldX);
        par1NBTTagCompound.func_74768_a("OldY", this.oldY);
        par1NBTTagCompound.func_74768_a("OldZ", this.oldZ);
    }

    protected void func_70037_a(NBTTagCompound par1NBTTagCompound) {
        if (par1NBTTagCompound.func_74764_b("TileID")) {
            this.block = Block.func_149729_e((int)par1NBTTagCompound.func_74762_e("TileID"));
        }
        this.metadata = par1NBTTagCompound.func_74771_c("Data") & 0xFF;
        this.fallTime = par1NBTTagCompound.func_74771_c("Time") & 0xFF;
        this.oldX = par1NBTTagCompound.func_74762_e("OldX");
        this.oldY = par1NBTTagCompound.func_74762_e("OldY");
        this.oldZ = par1NBTTagCompound.func_74762_e("OldZ");
        if (par1NBTTagCompound.func_74764_b("HurtEntities")) {
            this.fallHurtAmount = par1NBTTagCompound.func_74760_g("FallHurtAmount");
            this.fallHurtMax = par1NBTTagCompound.func_74762_e("FallHurtMax");
        }
        if (this.block == null) {
            this.block = Blocks.field_150354_m;
        }
    }

    public void func_85029_a(CrashReportCategory par1CrashReportCategory) {
        super.func_85029_a(par1CrashReportCategory);
        par1CrashReportCategory.func_71507_a("Immitating block ID", (Object)Block.func_149682_b((Block)this.block));
        par1CrashReportCategory.func_71507_a("Immitating block data", (Object)this.metadata);
    }

    @SideOnly(value=Side.CLIENT)
    public float func_70053_R() {
        return 0.0f;
    }

    @SideOnly(value=Side.CLIENT)
    public World getWorld() {
        return this.field_70170_p;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_90999_ad() {
        return false;
    }
}
