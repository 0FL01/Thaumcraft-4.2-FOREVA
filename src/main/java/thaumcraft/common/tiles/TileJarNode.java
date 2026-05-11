package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileJar;

public class TileJarNode
extends TileJar
implements IAspectContainer,
INode {
    private AspectList aspects = new AspectList();
    private AspectList aspectsBase = new AspectList();
    private NodeType nodeType = NodeType.NORMAL;
    private NodeModifier nodeModifier = null;
    private String id = "";
    public long animate = 0L;
    public boolean drop = true;

    @Override
    public boolean shouldRefresh(net.minecraft.world.World worldIn, net.minecraft.util.math.BlockPos pos, net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.readFromNBT(nbttagcompound);
        this.id = nbttagcompound.getString("nodeId");
        AspectList al = new AspectList();
        NBTTagList tlist = nbttagcompound.getTagList("AspectsBase", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
        short oldBase = nbttagcompound.getShort("nodeVisBase");
        this.aspectsBase = new AspectList();
        if (oldBase > 0 && al.size() == 0) {
            for (Aspect a : this.aspects.getAspects()) {
                this.aspectsBase.merge(a, oldBase);
            }
        } else {
            this.aspectsBase = al.copy();
        }
        this.setNodeType(NodeType.values()[nbttagcompound.getByte("type")]);
        byte mod = nbttagcompound.getByte("modifier");
        if (mod >= 0) {
            this.setNodeModifier(NodeModifier.values()[mod]);
        } else {
            this.setNodeModifier(null);
        }
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.writeToNBT(nbttagcompound);
        nbttagcompound.setString("nodeId", this.id);
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("AspectsBase", tlist);
        for (Aspect aspect : this.aspectsBase.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound f = new NBTTagCompound();
            f.setString("key", aspect.getTag());
            f.setInteger("amount", this.aspectsBase.getAmount(aspect));
            tlist.appendTag(f);
        }
        nbttagcompound.setByte("type", (byte)this.getNodeType().ordinal());
        nbttagcompound.setByte("modifier", this.getNodeModifier() == null ? (byte)-1 : (byte)this.getNodeModifier().ordinal());
    }

    public AspectList getAspects() {
        return this.aspects;
    }

    public AspectList getAspectsBase() {
        return this.aspectsBase;
    }

    public void setAspects(AspectList aspects) {
        this.aspects = aspects.copy();
        this.aspectsBase = aspects.copy();
    }

    public int addToContainer(Aspect tt, int am) {
        int out = 0;
        if (this.aspects.getAmount(tt) + am > this.aspectsBase.getAmount(tt)) {
            out = this.aspects.getAmount(tt) + am - this.aspectsBase.getAmount(tt);
        }
        this.aspects.add(tt, am - out);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        this.markDirty();
        return out;
    }

    public boolean takeFromContainer(Aspect tt, int am) {
        if (this.aspects.getAmount(tt) >= am) {
            this.aspects.remove(tt, am);
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
        return this.aspects.getAmount(tag) >= amt;
    }

    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (this.aspects.getAmount(tt) >= ot.getAmount(tt)) continue;
            return false;
        }
        return true;
    }

    public int containerContains(Aspect tag) {
        return this.aspects.getAmount(tag);
    }

    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    // INode implementation
    public NodeType getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void setNodeModifier(NodeModifier nodeModifier) {
        this.nodeModifier = nodeModifier;
    }

    public NodeModifier getNodeModifier() {
        return this.nodeModifier;
    }

    public int getNodeVisBase(Aspect aspect) {
        return this.aspectsBase.getAmount(aspect);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNodeVisBase(Aspect aspect, short nodeVisBase) {
        if (this.aspectsBase.getAmount(aspect) < nodeVisBase) {
            this.aspectsBase.merge(aspect, nodeVisBase);
        } else {
            this.aspectsBase.reduce(aspect, this.aspectsBase.getAmount(aspect) - nodeVisBase);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 9) {
            if (world.isRemote) {
                for (int yy = -1; yy < 3; ++yy) {
                    for (int xx = -1; xx < 2; ++xx) {
                        for (int zz = -1; zz < 2; ++zz) {
                            Thaumcraft.proxy.blockSparkle(world, pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz, -9999, 5);
                        }
                    }
                }
                this.animate = System.currentTimeMillis() + 1000L;
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }
}
