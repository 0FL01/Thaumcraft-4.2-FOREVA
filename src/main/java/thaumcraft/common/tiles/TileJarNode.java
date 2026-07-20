package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileJarNode
extends TileJar
implements IAspectContainer,
 INode,
 IWandable {
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
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player,
                                int x, int y, int z, int side, int md) {
        BlockPos nodePos = new BlockPos(x, y, z);
        if (!world.isRemote) {
            this.drop = false;
            world.setBlockState(nodePos,
                    ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 0), 3);
            TileEntity tile = world.getTileEntity(nodePos);
            if (tile instanceof TileNode) {
                TileNode node = (TileNode) tile;
                node.setAspects(this.getAspects());
                node.setNodeModifier(this.getNodeModifier());
                node.setNodeType(this.getNodeType());
                node.setId(this.getId());
                node.markDirty();
                world.notifyBlockUpdate(nodePos, world.getBlockState(nodePos), world.getBlockState(nodePos), 3);
            }
            world.playEvent(2001, nodePos, Block.getStateId(Blocks.GLASS.getDefaultState()));
            world.playSound(null, nodePos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS,
                    1.0F, 0.9F + world.rand.nextFloat() * 0.2F);
        } else {
            player.swingArm(ItemWandCasting.getHandHoldingWand(player, wandstack));
        }
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
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
