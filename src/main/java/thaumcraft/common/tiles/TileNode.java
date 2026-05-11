package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
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
    public long fuel = 0;
    public boolean balanced = false;

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.readFromNBT(nbttagcompound);
        this.id = nbttagcompound.getString("nodeId");
        byte type = nbttagcompound.getByte("type");
        byte mod = nbttagcompound.getByte("modifier");
        this.nodeType = NodeType.values()[type];
        this.nodeModifier = mod >= 0 ? NodeModifier.values()[mod] : null;
        this.fuel = nbttagcompound.getLong("fuel");
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.writeToNBT(nbttagcompound);
        nbttagcompound.setString("nodeId", this.id);
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
    public boolean takeFromContainer(AspectList ot) { return false; }
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
    public void setNodeModifier(NodeModifier mod) { this.nodeModifier = mod; }
    public int getNodeVisBase(Aspect aspect) { return this.aspectsBase.getAmount(aspect); }
    public void setNodeVisBase(Aspect aspect, short visBase) {
        this.aspectsBase.merge(aspect, visBase);
    }
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    @Override
    public void update() {
        // Tick logic for vis regeneration will be added in Phase 4.6
    }
}
