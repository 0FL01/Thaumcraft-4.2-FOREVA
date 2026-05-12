package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileAlembic extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport {
    public Aspect aspect = null;
    public int amount = 0;
    public int maxAmount = 32;
    public int facing = 2;

    @Override
    public void update() {}

    // --- NBT ---

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        if (nbt.hasKey("aspect")) {
            this.aspect = Aspect.getAspect(nbt.getString("aspect"));
        } else {
            this.aspect = null;
        }
        this.amount = nbt.getInteger("amount");
        this.maxAmount = nbt.getInteger("maxAmount");
        this.facing = nbt.getByte("facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        if (this.aspect != null) {
            nbt.setString("aspect", this.aspect.getTag());
        }
        nbt.setInteger("amount", this.amount);
        nbt.setInteger("maxAmount", this.maxAmount);
        nbt.setByte("facing", (byte) this.facing);
    }

    // --- IAspectContainer ---

    @Override
    public AspectList getAspects() {
        AspectList al = new AspectList();
        if (this.aspect != null && this.amount > 0) {
            al.add(this.aspect, this.amount);
        }
        return al;
    }

    @Override
    public void setAspects(AspectList list) {
        if (list == null || list.getAspects().length == 0) {
            this.aspect = null;
            this.amount = 0;
        } else {
            this.aspect = list.getAspects()[0];
            this.amount = Math.min(list.getAmount(this.aspect), this.maxAmount);
        }
        this.markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null && this.amount < this.maxAmount && (this.aspect == null || this.aspect.equals(tag));
    }

    @Override
    public int addToContainer(Aspect tag, int requested) {
        if (!this.doesContainerAccept(tag)) return requested;
        if (this.aspect == null) {
            this.aspect = tag;
        }
        int add = Math.min(requested, this.maxAmount - this.amount);
        this.amount += add;
        this.markDirty();
        return requested - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int requested) {
        if (tag == null || !tag.equals(this.aspect) || this.amount < requested) return false;
        this.amount -= requested;
        if (this.amount <= 0) {
            this.aspect = null;
            this.amount = 0;
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList list) {
        if (list == null || list.getAspects().length != 1) return false;
        Aspect tag = list.getAspects()[0];
        int req = list.getAmount(tag);
        return this.takeFromContainer(tag, req);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return tag != null && tag.equals(this.aspect) && this.amount >= amt;
    }

    @Override
    public boolean doesContainerContain(AspectList list) {
        if (list == null || list.getAspects().length != 1) return false;
        Aspect tag = list.getAspects()[0];
        int req = list.getAmount(tag);
        return this.doesContainerContainAmount(tag, req);
    }

    @Override
    public int containerContains(Aspect tag) {
        if (tag != null && tag.equals(this.aspect)) return this.amount;
        return 0;
    }

    // --- IEssentiaTransport ---

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return this.amount > 0;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspect;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.amount;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!this.canOutputTo(face) || !aspect.equals(this.aspect)) return 0;
        int taken = Math.min(amount, this.amount);
        this.takeFromContainer(aspect, taken);
        return taken;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }
}
