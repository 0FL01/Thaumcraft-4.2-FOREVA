package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.common.lib.research.ResearchManager;

public class TileNodeEnergized extends TileVisNode implements IAspectContainer {
    private AspectList auraBase = new AspectList()
            .add(Aspect.AIR, 20)
            .add(Aspect.FIRE, 20)
            .add(Aspect.EARTH, 20)
            .add(Aspect.WATER, 20)
            .add(Aspect.ORDER, 20)
            .add(Aspect.ENTROPY, 20);
    AspectList visBase = new AspectList();
    AspectList vis = new AspectList();
    private NodeType nodeType = NodeType.NORMAL;
    private NodeModifier nodeModifier = null;
    String id = "blank";

    @Override
    public void update() {
        super.update();
        if (this.world == null || this.world.isRemote) {
            return;
        }
        if (this.getNodeType() == NodeType.UNSTABLE && this.world.rand.nextInt(500) == 1) {
            this.visBase = new AspectList();
        }
        if (this.visBase.size() == 0 && this.getAuraBase().size() > 0) {
            this.setupNode();
        }
        this.vis = this.visBase.copy();
    }

    public void setupNode() {
        this.visBase = new AspectList();
        AspectList temp = ResearchManager.reduceToPrimals(this.getAuraBase(), true);
        for (Aspect aspect : temp.getAspects()) {
            int amt = temp.getAmount(aspect);
            if (this.getNodeModifier() == NodeModifier.BRIGHT) {
                amt = (int) (amt * 1.2F);
            }
            if (this.getNodeModifier() == NodeModifier.PALE) {
                amt = (int) (amt * 0.8F);
            }
            if (this.getNodeModifier() == NodeModifier.FADING) {
                amt = (int) (amt * 0.5F);
            }
            amt = MathHelper.floor(MathHelper.sqrt(amt));
            if (this.getNodeType() == NodeType.UNSTABLE) {
                amt += this.world.rand.nextInt(5) - 2;
            }
            if (amt < 1) continue;
            this.visBase.merge(aspect, amt);
        }
        this.markDirty();
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.id = nbt.getString("nodeId");
        this.setNodeType(NodeType.values()[nbt.getByte("type")]);
        byte mod = nbt.getByte("modifier");
        if (mod >= 0) {
            this.setNodeModifier(NodeModifier.values()[mod]);
        } else {
            this.setNodeModifier(null);
        }
        this.visBase.aspects.clear();
        NBTTagList tlist = nbt.getTagList("AEB", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            this.visBase.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
        this.getAuraBase().readFromNBT(nbt);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setString("nodeId", this.id);
        nbt.setByte("type", (byte) this.getNodeType().ordinal());
        nbt.setByte("modifier", this.getNodeModifier() == null ? (byte) -1 : (byte) this.getNodeModifier().ordinal());
        NBTTagList tlist = new NBTTagList();
        nbt.setTag("AEB", tlist);
        for (Aspect aspect : this.visBase.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound f = new NBTTagCompound();
            f.setString("key", aspect.getTag());
            f.setInteger("amount", this.visBase.getAmount(aspect));
            tlist.appendTag(f);
        }
        this.getAuraBase().writeToNBT(nbt);
    }

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

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    @Override
    public int consumeVis(Aspect aspect, int amount) {
        int drain = Math.min(this.vis.getAmount(aspect), amount);
        if (drain > 0) {
            this.vis.reduce(aspect, drain);
        }
        return drain;
    }

    public AspectList getAuraBase() {
        return this.auraBase;
    }

    @Override
    public AspectList getAspects() {
        return this.visBase;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.auraBase = aspects;
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    @Override
    public byte getAttunement() {
        return -1;
    }
}
