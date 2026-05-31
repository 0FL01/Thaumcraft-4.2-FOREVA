package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.ItemCompassStone;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

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
    private int wait = 0;
    private byte nodeLock = 0;
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
        this.count++;
        if (this.world.isRemote) {
            if (this.nodeType == NodeType.DARK && this.count % 50 == 0) {
                ItemCompassStone.sinisterNodes.put(new WorldCoordinates(this), System.currentTimeMillis());
            }
            return;
        }
        checkLock();
        if (this.regeneration < 0) {
            this.regeneration = getRegenerationInterval();
        }

        boolean changed = false;
        if (this.catchUp) {
            changed = handleCatchUpRecharge();
        }
        changed |= handleDischarge();
        if (this.wait > 0) {
            --this.wait;
        }
        if (this.regeneration > 0 && this.wait == 0 && this.count % this.regeneration == 0) {
            this.lastActive = System.currentTimeMillis();
            changed |= rechargeOneMissingAspect();
        }
        changed = handlePureNode(changed);

        if (changed) {
            nodeChange();
        }
    }

    private String generateId() {
        return this.world.provider.getDimension() + ":" + this.pos.getX() + ":" + this.pos.getY() + ":" + this.pos.getZ();
    }

    private int getRegenerationInterval() {
        int interval = 600;
        if (this.nodeModifier == NodeModifier.BRIGHT) {
            interval = 400;
        } else if (this.nodeModifier == NodeModifier.PALE) {
            interval = 900;
        } else if (this.nodeModifier == NodeModifier.FADING) {
            interval = 0;
        }
        if (interval > 0) {
            if (this.getLock() == 1) {
                interval *= 2;
            } else if (this.getLock() == 2) {
                interval *= 20;
            }
        }
        return interval;
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

    private boolean handlePureNode(boolean changed) {
        int dim = this.world.provider.getDimension();
        int dimBlacklist = ThaumcraftWorldGenerator.getDimBlacklist(dim);
        if (dim == -1 || dim == 1 || dimBlacklist == 0 || dimBlacklist == 2) {
            return changed;
        }
        if (this.getNodeType() != NodeType.PURE || this.count % 50 != 0) {
            return changed;
        }
        if (ThaumcraftWorldGenerator.biomeMagicalForest == null) {
            return changed;
        }

        int x = this.pos.getX() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
        int z = this.pos.getZ() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
        Biome biome = this.world.getBiome(new BlockPos(x, 0, z));
        int biomeId = Biome.getIdForBiome(biome);
        int biomeBlacklist = ThaumcraftWorldGenerator.getBiomeBlacklist(biomeId);
        if (biomeBlacklist == 0 || biomeBlacklist == 2) {
            return changed;
        }
        if (isSameBiome(biome, ThaumcraftWorldGenerator.biomeMagicalForest)) {
            return changed;
        }

        if (isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest);
        } else if (this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockMagicalLog) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest);
        }
        return changed;
    }

    private static boolean isSameBiome(Biome first, Biome second) {
        return first == second || first != null && second != null
                && Biome.getIdForBiome(first) == Biome.getIdForBiome(second);
    }

    private void nodeChange() {
        this.regeneration = -1;
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }

    public byte getLock() {
        return this.nodeLock;
    }

    private void checkLock() {
        if ((this.count <= 1 || this.count % 50 == 0)
                && this.pos.getY() > 0
                && this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockAiry) {
            byte oldLock = this.nodeLock;
            this.nodeLock = 0;
            if (!this.world.isAirBlock(this.pos.down())
                    && this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockStoneDevice) {
                int meta = this.world.getBlockState(this.pos.down()).getBlock()
                        .getMetaFromState(this.world.getBlockState(this.pos.down()));
                if (meta == 9) {
                    this.nodeLock = 1;
                } else if (meta == 10) {
                    this.nodeLock = 2;
                }
            }
            if (oldLock != this.nodeLock) {
                this.regeneration = -1;
            }
        }
    }

    private boolean handleDischarge() {
        if (this.world.getBlockState(this.pos).getBlock() != ConfigBlocks.blockAiry || this.getLock() == 1) {
            return false;
        }
        if (this.getNodeModifier() == NodeModifier.FADING) {
            return false;
        }

        boolean shiny = this.getNodeType() == NodeType.HUNGRY || this.getNodeModifier() == NodeModifier.BRIGHT;
        int interval = this.getNodeModifier() == null ? 2 : (shiny ? 1 : (this.getNodeModifier() == NodeModifier.PALE ? 3 : 2));
        if (this.count % interval != 0) {
            return false;
        }
        if (this.getNodeModifier() == NodeModifier.PALE && this.world.rand.nextBoolean()) {
            return false;
        }

        int x = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        int y = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        int z = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        TileEntity te = this.world.getTileEntity(this.pos.add(x, y, z));
        if (!(te instanceof INode) || this.world.getBlockState(this.pos.add(x, y, z)).getBlock() != ConfigBlocks.blockAiry) {
            return false;
        }
        if (te instanceof TileNode && ((TileNode) te).getLock() > 0) {
            return false;
        }

        INode node = (INode) te;
        int targetAverage = (node.getAspects().visSize() + node.getAspectsBase().visSize()) / 2;
        int thisAverage = (this.getAspects().visSize() + this.getAspectsBase().visSize()) / 2;
        if (targetAverage >= thisAverage || node.getAspects().size() <= 0) {
            return false;
        }

        Aspect aspect = node.getAspects().getAspects()[this.world.rand.nextInt(node.getAspects().size())];
        boolean updated = false;
        if (this.getAspects().getAmount(aspect) < this.getNodeVisBase(aspect) && node.takeFromContainer(aspect, 1)) {
            this.addToContainer(aspect, 1);
            updated = true;
        } else if (node.takeFromContainer(aspect, 1)) {
            int bound = 1 + (int) ((double) this.getNodeVisBase(aspect) / (shiny ? 1.5D : 1.0D));
            if (this.world.rand.nextInt(Math.max(1, bound)) == 0) {
                this.aspectsBase.add(aspect, 1);
                if (this.getNodeModifier() == NodeModifier.PALE && this.world.rand.nextInt(100) == 0) {
                    this.setNodeModifier(null);
                }
                if (this.world.rand.nextInt(3) == 0) {
                    node.setNodeVisBase(aspect, (short) (node.getNodeVisBase(aspect) - 1));
                }
            }
            updated = true;
        }

        if (!updated) {
            return false;
        }

        if (te instanceof TileNode) {
            TileNode targetNode = (TileNode) te;
            if (targetNode.regeneration < 0) {
                targetNode.regeneration = targetNode.getRegenerationInterval();
            }
            targetNode.wait = targetNode.regeneration / 2;
            te.markDirty();
            this.world.notifyBlockUpdate(te.getPos(), this.world.getBlockState(te.getPos()), this.world.getBlockState(te.getPos()), 3);
        }

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockZap(
                        (float) (this.pos.getX() + x) + 0.5F,
                        (float) (this.pos.getY() + y) + 0.5F,
                        (float) (this.pos.getZ() + z) + 0.5F,
                        (float) this.pos.getX() + 0.5F,
                        (float) this.pos.getY() + 0.5F,
                        (float) this.pos.getZ() + 0.5F),
                new NetworkRegistry.TargetPoint(
                        this.world.provider.getDimension(),
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        32.0));
        return true;
    }
}
