package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class TileNode
extends TileThaumcraft
implements ITickable, INode, IAspectContainer {

    public String id = "";
    private AspectList aspects = new AspectList();
    private AspectList aspectsBase = new AspectList();
    private NodeType nodeType = NodeType.NORMAL;
    private NodeModifier nodeModifier = null;
    private boolean dirty = false;
    private long lastActive = 0L;
    private int count = 0;
    private int regeneration = -1;
    private boolean catchUp = false;
    public long fuel = 0;
    public boolean balanced = false;

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.readFromNBT(nbttagcompound);
        this.id = nbttagcompound.getString("nodeId");
        this.lastActive = nbttagcompound.getLong("lastActive");
        AspectList al = new AspectList();
        NBTTagList tlist = nbttagcompound.getTagList("AspectsBase", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
        short oldBase = nbttagcompound.getShort("nodeVisBase");
        if (oldBase > 0 && al.size() == 0) {
            this.aspectsBase = new AspectList();
            for (Aspect aspect : this.aspects.getAspects()) {
                this.aspectsBase.merge(aspect, oldBase);
            }
        } else if (al.size() > 0) {
            this.aspectsBase = al.copy();
        } else {
            this.aspectsBase = this.aspects.copy();
        }
        byte type = nbttagcompound.getByte("type");
        byte mod = nbttagcompound.getByte("modifier");
        this.nodeType = NodeType.values()[type];
        this.nodeModifier = mod >= 0 ? NodeModifier.values()[mod] : null;
        this.fuel = nbttagcompound.getLong("fuel");
        this.regeneration = getRegenerationInterval();
        this.catchUp = this.regeneration > 0 && this.lastActive > 0L
                && System.currentTimeMillis() > this.lastActive + (long) this.regeneration * 75L;
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.writeToNBT(nbttagcompound);
        nbttagcompound.setString("nodeId", this.id);
        nbttagcompound.setLong("lastActive", this.lastActive);
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("AspectsBase", tlist);
        for (Aspect aspect : this.aspectsBase.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("key", aspect.getTag());
            tag.setInteger("amount", this.aspectsBase.getAmount(aspect));
            tlist.appendTag(tag);
        }
        nbttagcompound.setByte("type", (byte)this.nodeType.ordinal());
        nbttagcompound.setByte("modifier", this.nodeModifier != null ? (byte)this.nodeModifier.ordinal() : -1);
        nbttagcompound.setLong("fuel", this.fuel);
    }

    // IAspectContainer
    public AspectList getAspects() { return this.aspects; }
    public AspectList getAspectsBase() { return this.aspectsBase; }
    public void setAspects(AspectList aspects) {
        this.aspects = aspects.copy();
        this.aspectsBase = aspects.copy();
    }
    public boolean doesContainerAccept(Aspect tag) { return true; }
    public int addToContainer(Aspect tt, int am) {
        int out = 0;
        if (this.aspects.getAmount(tt) + am > this.aspectsBase.getAmount(tt)) {
            out = this.aspects.getAmount(tt) + am - this.aspectsBase.getAmount(tt);
        }
        this.aspects.add(tt, am - out);
        this.markDirty();
        return out;
    }
    public boolean takeFromContainer(Aspect tt, int am) {
        if (this.aspects.getAmount(tt) >= am) {
            this.aspects.remove(tt, am);
            this.markDirty();
            return true;
        }
        return false;
    }
    public boolean takeFromContainer(AspectList ot) {
        if (ot == null || !doesContainerContain(ot)) return false;
        for (Aspect aspect : ot.getAspects()) {
            if (aspect == null) continue;
            this.aspects.remove(aspect, ot.getAmount(aspect));
        }
        this.markDirty();
        return true;
    }
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return this.aspects.getAmount(tag) >= amt;
    }
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (this.aspects.getAmount(tt) < ot.getAmount(tt)) return false;
        }
        return true;
    }
    public int containerContains(Aspect tag) { return this.aspects.getAmount(tag); }

    // INode
    public NodeType getNodeType() { return this.nodeType; }
    public void setNodeType(NodeType type) { this.nodeType = type; }
    public NodeModifier getNodeModifier() { return this.nodeModifier; }
    public void setNodeModifier(NodeModifier mod) { this.nodeModifier = mod; this.regeneration = -1; }
    public int getNodeVisBase(Aspect aspect) { return this.aspectsBase.getAmount(aspect); }
    public void setNodeVisBase(Aspect aspect, short visBase) {
        if (this.aspectsBase.getAmount(aspect) < visBase) {
            this.aspectsBase.merge(aspect, visBase);
        } else {
            this.aspectsBase.reduce(aspect, this.aspectsBase.getAmount(aspect) - visBase);
        }
    }
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateId();
        }
        if (this.world.isRemote) return;

        this.count++;
        if (this.regeneration < 0) {
            this.regeneration = getRegenerationInterval();
        }

        boolean changed = false;
        if (this.catchUp) {
            changed = handleCatchUpRecharge();
        }
        if (this.regeneration > 0 && this.count % this.regeneration == 0) {
            this.lastActive = System.currentTimeMillis();
            changed |= rechargeOneMissingAspect();
        }

        if (changed) {
            nodeChange();
        }
    }

    private String generateId() {
        return this.world.provider.getDimension() + ":" + this.pos.getX() + ":" + this.pos.getY() + ":" + this.pos.getZ();
    }

    private int getRegenerationInterval() {
        if (this.nodeModifier == NodeModifier.BRIGHT) return 400;
        if (this.nodeModifier == NodeModifier.PALE) return 900;
        if (this.nodeModifier == NodeModifier.FADING) return 0;
        return 600;
    }

    private boolean handleCatchUpRecharge() {
        this.catchUp = false;
        int inc = this.regeneration * 75;
        int amount = inc > 0 ? (int) ((System.currentTimeMillis() - this.lastActive) / (long) inc) : 0;
        boolean changed = false;
        for (int i = 0; i < Math.min(amount, this.aspectsBase.visSize()); i++) {
            changed |= rechargeOneMissingAspect();
        }
        return changed;
    }

    private boolean rechargeOneMissingAspect() {
        AspectList missing = new AspectList();
        for (Aspect aspect : this.aspectsBase.getAspects()) {
            if (aspect != null && this.aspects.getAmount(aspect) < this.getNodeVisBase(aspect)) {
                missing.add(aspect, 1);
            }
        }
        if (missing.size() <= 0) return false;
        Aspect aspect = missing.getAspects()[this.world.rand.nextInt(missing.size())];
        this.addToContainer(aspect, 1);
        return true;
    }

    private void nodeChange() {
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }
}
