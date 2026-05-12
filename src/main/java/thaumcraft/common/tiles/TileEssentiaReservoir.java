package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEssentiaReservoir extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport {
    public AspectList essentia = new AspectList();
    public int maxAmount = 256;
    public EnumFacing facing = EnumFacing.DOWN;
    public float colorR = 1.0f;
    public float colorG = 1.0f;
    public float colorB = 1.0f;
    public Aspect displayAspect = null;

    @Override
    public void update() {}

    // --- NBT ---

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.essentia = new AspectList();
        this.essentia.readFromNBT(nbt);
        this.maxAmount = nbt.getInteger("maxAmount");
        if (nbt.hasKey("facing")) {
            this.facing = EnumFacing.byIndex(nbt.getByte("facing"));
        }
        if (nbt.hasKey("displayAspect")) {
            this.displayAspect = Aspect.getAspect(nbt.getString("displayAspect"));
        } else {
            this.displayAspect = null;
        }
        this.colorR = nbt.getFloat("colorR");
        this.colorG = nbt.getFloat("colorG");
        this.colorB = nbt.getFloat("colorB");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        if (this.essentia != null) {
            this.essentia.writeToNBT(nbt);
        }
        nbt.setInteger("maxAmount", this.maxAmount);
        nbt.setByte("facing", (byte) this.facing.getIndex());
        if (this.displayAspect != null) {
            nbt.setString("displayAspect", this.displayAspect.getTag());
        }
        nbt.setFloat("colorR", this.colorR);
        nbt.setFloat("colorG", this.colorG);
        nbt.setFloat("colorB", this.colorB);
    }

    // --- IAspectContainer ---

    @Override
    public AspectList getAspects() {
        return this.essentia;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.essentia = aspects != null ? aspects : new AspectList();
        this.markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null && this.containerContains(null) < this.maxAmount;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return amount;
        int total = this.containerContains(null);
        int add = Math.min(amount, this.maxAmount - total);
        if (add <= 0) return amount;
        this.essentia.add(tag, add);
        this.markDirty();
        return amount - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return false;
        if (this.containerContains(tag) < amount) return false;
        this.essentia.remove(tag, amount);
        this.markDirty();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList list) {
        if (list == null) return false;
        for (Aspect aspect : list.getAspects()) {
            if (this.essentia.getAmount(aspect) < list.getAmount(aspect)) {
                return false;
            }
        }
        for (Aspect aspect : list.getAspects()) {
            this.essentia.remove(aspect, list.getAmount(aspect));
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.containerContains(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList list) {
        if (list == null) return false;
        for (Aspect aspect : list.getAspects()) {
            if (this.essentia.getAmount(aspect) < list.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        if (tag == null) {
            int total = 0;
            for (Aspect a : this.essentia.getAspects()) {
                if (a != null) total += this.essentia.getAmount(a);
            }
            return total;
        }
        return this.essentia.getAmount(tag);
    }

    // --- IEssentiaTransport ---

    @Override
    public boolean isConnectable(EnumFacing face) {
        return true;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == this.facing || true;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == this.facing || true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (loc != null && loc == this.facing) return 0;
        return this.containerContains(null) < this.maxAmount ? 24 : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        if (loc == this.facing) return this.displayAspect;
        Aspect[] aspects = this.essentia.getAspects();
        if (aspects.length == 1) return aspects[0];
        return this.displayAspect;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.containerContains(null);
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
        if (!this.canOutputTo(face)) return 0;
        int taken = Math.min(amount, this.containerContains(aspect));
        if (taken <= 0) return 0;
        this.takeFromContainer(aspect, taken);
        return taken;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!this.canInputFrom(face)) return 0;
        int leftover = this.addToContainer(aspect, amount);
        return amount - leftover;
    }
}
