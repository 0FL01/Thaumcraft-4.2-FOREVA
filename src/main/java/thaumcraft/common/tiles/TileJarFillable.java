package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import net.minecraft.util.ITickable;
import thaumcraft.common.tiles.TileJar;

public class TileJarFillable
extends TileJar
implements ITickable, IAspectContainer, IAspectSource,
IEssentiaTransport {
    public Aspect aspect = null;
    public Aspect aspectFilter = null;
    public int amount = 0;
    public int maxAmount = 64;
    public int facing = 2;
    public boolean forgeLiquid = false;
    public int lid = 0;
    int count = 0;

    @Override
    public boolean shouldRefresh(World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspect = Aspect.getAspect(nbttagcompound.getString("Aspect"));
        this.aspectFilter = Aspect.getAspect(nbttagcompound.getString("AspectFilter"));
        this.amount = nbttagcompound.getShort("Amount");
        this.facing = nbttagcompound.getByte("facing");
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        if (this.aspect != null) {
            nbttagcompound.setString("Aspect", this.aspect.getTag());
        }
        if (this.aspectFilter != null) {
            nbttagcompound.setString("AspectFilter", this.aspectFilter.getTag());
        }
        nbttagcompound.setShort("Amount", (short)this.amount);
        nbttagcompound.setByte("facing", (byte)this.facing);
    }

    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (this.aspect != null && this.amount > 0) {
            al.add(this.aspect, this.amount);
        }
        return al;
    }

    public void setAspects(AspectList aspects) {
    }

    public int addToContainer(Aspect tt, int am) {
        if (am == 0) {
            return am;
        }
        if (this.amount < this.maxAmount && tt == this.aspect || this.amount == 0) {
            this.aspect = tt;
            int added = Math.min(am, this.maxAmount - this.amount);
            this.amount += added;
            am -= added;
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        this.markDirty();
        return am;
    }

    public boolean takeFromContainer(Aspect tt, int am) {
        if (this.amount >= am && tt == this.aspect) {
            this.amount -= am;
            if (this.amount <= 0) {
                this.aspect = null;
                this.amount = 0;
            }
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            this.markDirty();
            return true;
        }
        return false;
    }

    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return this.amount >= amt && tag == this.aspect;
    }

    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (this.amount <= 0 || tt != this.aspect) continue;
            return true;
        }
        return false;
    }

    public int containerContains(Aspect tag) {
        return 0;
    }

    public boolean doesContainerAccept(Aspect tag) {
        return this.aspectFilter != null ? tag.equals(this.aspectFilter) : true;
    }

    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public boolean canInputFrom(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    public void setSuction(Aspect aspect, int amount) {
    }

    public boolean renderExtendedTube() {
        return true;
    }

    public int getMinimumSuction() {
        return this.aspectFilter != null ? 64 : 32;
    }

    public Aspect getSuctionType(EnumFacing loc) {
        return this.aspectFilter != null ? this.aspectFilter : this.aspect;
    }

    public int getSuctionAmount(EnumFacing loc) {
        if (this.amount < this.maxAmount) {
            if (this.aspectFilter != null) {
                return 64;
            }
            return 32;
        }
        return 0;
    }

    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspect;
    }

    public int getEssentiaAmount(EnumFacing loc) {
        return this.amount;
    }

    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public void update() {
        if (!world.isRemote && ++this.count % 5 == 0 && this.amount < this.maxAmount) {
            this.fillJar();
        }
    }

    void fillJar() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos.getX(), pos.getY(), pos.getZ(), EnumFacing.UP);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(EnumFacing.DOWN)) {
                return;
            }
            Aspect ta = null;
            if (this.aspectFilter != null) {
                ta = this.aspectFilter;
            } else if (this.aspect != null && this.amount > 0) {
                ta = this.aspect;
            } else if (ic.getEssentiaAmount(EnumFacing.DOWN) > 0 && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP) && this.getSuctionAmount(EnumFacing.UP) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(EnumFacing.DOWN);
            }
            if (ta != null && ic.getSuctionAmount(EnumFacing.DOWN) < this.getSuctionAmount(EnumFacing.UP)) {
                this.addToContainer(ta, ic.takeEssentia(ta, 1, EnumFacing.DOWN));
            }
        }
    }
}
