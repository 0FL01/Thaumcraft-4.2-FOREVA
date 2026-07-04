package thaumcraft.common.tiles;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.TCSounds;

public class TileJarBrain
extends TileJar implements ITickable {
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
    public float field_40066_q;
    public float rota;
    public float rotb;
    public int xp = 0;
    public int xpMax = 2000;
    public int eatDelay = 0;
    long lastsigh = System.currentTimeMillis() + 1500L;

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.xp = nbttagcompound.getInteger("XP");
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("XP", this.xp);
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World worldIn, net.minecraft.util.math.BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        List<?> ents;
        Entity entity = null;
        if (this.xp > this.xpMax) {
            this.xp = this.xpMax;
        }
        if (this.xp < this.xpMax) {
            entity = this.getClosestXPOrb();
            if (entity != null && this.eatDelay == 0) {
                double dx = ((double) pos.getX() + 0.5D - entity.posX) / 25.0D;
                double dy = ((double) pos.getY() + 0.5D - entity.posY) / 25.0D;
                double dz = ((double) pos.getZ() + 0.5D - entity.posZ) / 25.0D;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double strength = 1.0 - dist;
                if (strength > 0.0D && dist > 0.0D) {
                    strength *= strength;
                    entity.motionX += dx / dist * strength * 0.3;
                    entity.motionY += dy / dist * strength * 0.5;
                    entity.motionZ += dz / dist * strength * 0.3;
                }
            }
        }
        if (this.world.isRemote) {
            this.rotb = this.rota;
            if (entity == null) {
                entity = this.world.getClosestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 6.0D, false);
            }
            if (entity != null && this.lastsigh < System.currentTimeMillis()) {
                this.world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                        TCSounds.BRAIN, SoundCategory.AMBIENT, 0.15F, 0.8F + this.world.rand.nextFloat() * 0.4F, false);
                this.lastsigh = System.currentTimeMillis() + 5000L + this.world.rand.nextInt(25000);
            }
            if (entity != null) {
                double d = entity.posX - ((double) pos.getX() + 0.5D);
                double d1 = entity.posZ - ((double) pos.getZ() + 0.5D);
                this.field_40066_q = (float) Math.atan2(d1, d);
                this.field_40059_f += 0.1F;
                if (this.field_40059_f < 0.5F || this.world.rand.nextInt(40) == 0) {
                    float f3 = this.field_40061_d;
                    do {
                        this.field_40061_d += (float) (this.world.rand.nextInt(4) - this.world.rand.nextInt(4));
                    } while (f3 == this.field_40061_d);
                }
            } else {
                this.field_40066_q += 0.01F;
            }
            while (this.rota >= (float) Math.PI) {
                this.rota -= (float) (Math.PI * 2.0D);
            }
            while (this.rota < (float) -Math.PI) {
                this.rota += (float) (Math.PI * 2.0D);
            }
            while (this.field_40066_q >= (float) Math.PI) {
                this.field_40066_q -= (float) (Math.PI * 2.0D);
            }
            while (this.field_40066_q < (float) -Math.PI) {
                this.field_40066_q += (float) (Math.PI * 2.0D);
            }
            float f = this.field_40066_q - this.rota;
            while (f >= (float) Math.PI) {
                f -= (float) (Math.PI * 2.0D);
            }
            while (f < (float) -Math.PI) {
                f += (float) (Math.PI * 2.0D);
            }
            this.rota += f * 0.04F;
        }
        if (this.eatDelay > 0) {
            --this.eatDelay;
        } else if (this.xp < this.xpMax && (ents = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX() - 0.1, pos.getY() - 0.1, pos.getZ() - 0.1, pos.getX() + 1.1, pos.getY() + 1.1, pos.getZ() + 1.1))).size() > 0) {
            for (Object ent : ents) {
                EntityXPOrb eo = (EntityXPOrb)ent;
                this.xp += eo.getXpValue();
                world.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 0.1f, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f + 1.0f);
                eo.setDead();
            }
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            this.markDirty();
        }
    }

    public EntityXPOrb getClosestXPOrb() {
        double cdist = Double.MAX_VALUE;
        EntityXPOrb orb = null;
        List<EntityXPOrb> ents = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(8.0, 8.0, 8.0));
        if (ents.size() > 0) {
            for (EntityXPOrb eo : ents) {
                double d = this.getDistanceTo(eo.posX, eo.posY, eo.posZ);
                if (!(d < cdist)) continue;
                orb = eo;
                cdist = d;
            }
        }
        return orb;
    }

    public double getDistanceTo(double par1, double par3, double par5) {
        double var7 = pos.getX() + 0.5 - par1;
        double var9 = pos.getY() + 0.5 - par3;
        double var11 = pos.getZ() + 0.5 - par5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }
}
