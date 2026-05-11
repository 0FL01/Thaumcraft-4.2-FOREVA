package thaumcraft.api.visnet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;

public abstract class TileVisNode extends TileThaumcraft implements ITickable {

    WeakReference<TileVisNode> parent = null;
    ArrayList<WeakReference<TileVisNode>> children = new ArrayList<>();
    protected int nodeCounter = 0;
    private boolean nodeRegged = false;
    public boolean nodeRefresh = false;

    // NBT saved data
    protected int vis = 0;
    protected int maxVis = 0;
    protected byte attunement = -1;

    public WorldCoordinates getLocation() {
        return new WorldCoordinates(this);
    }

    public abstract int getRange();

    public abstract boolean isSource();

    public int consumeVis(Aspect aspect, int vis) {
        if (VisNetHandler.isNodeValid(this.getParent())) {
            int out = this.getParent().get().consumeVis(aspect, vis);
            if (out > 0) {
                this.triggerConsumeEffect(aspect);
            }
            return out;
        }
        return 0;
    }

    public void removeThisNode() {
        for (WeakReference<TileVisNode> n : this.getChildren()) {
            if (n != null && n.get() != null) {
                n.get().removeThisNode();
            }
        }
        this.children = new ArrayList<>();
        if (VisNetHandler.isNodeValid(this.getParent())) {
            this.getParent().get().nodeRefresh = true;
        }
        this.setParent(null);
        this.parentChanged();
        if (this.isSource()) {
            HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist =
                    VisNetHandler.sources.get(this.world.provider.getDimension());
            if (sourcelist == null) {
                sourcelist = new HashMap<>();
            }
            sourcelist.remove(this.getLocation());
            VisNetHandler.sources.put(this.world.provider.getDimension(), sourcelist);
        }
        this.world.notifyBlockUpdate(this.getPos(), this.world.getBlockState(this.getPos()),
                this.world.getBlockState(this.getPos()), 3);
    }

    @Override
    public void invalidate() {
        this.removeThisNode();
        super.invalidate();
    }

    public void triggerConsumeEffect(Aspect aspect) {
    }

    public WeakReference<TileVisNode> getParent() {
        return this.parent;
    }

    public WeakReference<TileVisNode> getRootSource() {
        return VisNetHandler.isNodeValid(this.getParent())
                ? this.getParent().get().getRootSource()
                : (this.isSource() ? new WeakReference<>(this) : null);
    }

    public void setParent(WeakReference<TileVisNode> parent) {
        this.parent = parent;
    }

    public ArrayList<WeakReference<TileVisNode>> getChildren() {
        return this.children;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && (this.nodeCounter++ % 40 == 0 || this.nodeRefresh)) {
            if (!this.nodeRefresh && this.children.size() > 0) {
                for (WeakReference<TileVisNode> n : this.children) {
                    if (n != null && n.get() != null
                            && VisNetHandler.canNodeBeSeen(this, n.get())) continue;
                    this.nodeRefresh = true;
                    break;
                }
            }
            if (this.nodeRefresh) {
                for (WeakReference<TileVisNode> n : this.children) {
                    if (n.get() == null) continue;
                    n.get().nodeRefresh = true;
                }
                this.children.clear();
                this.parent = null;
            }
            if (this.isSource() && !this.nodeRegged) {
                VisNetHandler.addSource(this.getWorld(), this);
                this.nodeRegged = true;
            } else if (!this.isSource() && !VisNetHandler.isNodeValid(this.getParent())) {
                this.setParent(VisNetHandler.addNode(this.getWorld(), this));
                this.nodeRefresh = true;
            }
            if (this.nodeRefresh) {
                this.world.notifyBlockUpdate(this.getPos(), this.world.getBlockState(this.getPos()),
                        this.world.getBlockState(this.getPos()), 3);
                this.parentChanged();
            }
            this.nodeRefresh = false;
        }
    }

    public void parentChanged() {
    }

    public byte getAttunement() {
        return this.attunement;
    }

    public void setAttunement(byte attunement) {
        this.attunement = attunement;
    }

    public int getVis() {
        return vis;
    }

    public void setVis(int vis) {
        this.vis = Math.max(0, Math.min(vis, getMaxVis()));
    }

    public int getMaxVis() {
        return maxVis > 0 ? maxVis : 100;
    }

    public void setMaxVis(int maxVis) {
        this.maxVis = maxVis;
    }

    // ---- NBT ----

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.vis = nbt.getInteger("vis");
        this.maxVis = nbt.getInteger("maxVis");
        this.attunement = nbt.getByte("attunement");
        this.nodeRegged = false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("vis", vis);
        nbt.setInteger("maxVis", maxVis);
        nbt.setByte("attunement", attunement);
        return nbt;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }
}
