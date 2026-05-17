package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;

public class TileNodeConverter extends TileThaumcraft implements ITickable {
    public int count = -1;
    public int status = 0;

    @Override
    public void update() {
        if (this.world == null || this.pos == null) {
            return;
        }

        if (this.count == -1) {
            this.checkStatus();
        }

        TileEntity tile;
        if (this.status == 1 && this.world != null && !this.world.isRemote && this.count >= 1000
                && (tile = this.world.getTileEntity(this.pos.down())) instanceof TileNode) {
            AspectList base = ((TileNode) tile).getAspectsBase();
            NodeType type = ((TileNode) tile).getNodeType();
            NodeModifier mod = ((TileNode) tile).getNodeModifier();

            this.world.setBlockState(this.pos.down(),
                    ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 5), 3);
            TileEntity tileNew = this.world.getTileEntity(this.pos.down());
            if (tileNew instanceof TileNodeEnergized) {
                ((TileNodeEnergized) tileNew).setNodeModifier(mod);
                ((TileNodeEnergized) tileNew).setNodeType(type);
                ((TileNodeEnergized) tileNew).setAspects(base.copy());
                ((TileNodeEnergized) tileNew).setupNode();
            }
            this.checkStatus();
            this.world.addBlockEvent(this.pos, this.getBlockType(), 10, 10);
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.markDirty();
        }

        if (this.status == 2 && this.world != null && !this.world.isRemote && this.count <= 50
                && (tile = this.world.getTileEntity(this.pos.down())) instanceof TileNodeEnergized) {
            AspectList base = ((TileNodeEnergized) tile).getAuraBase();
            NodeType type = ((TileNodeEnergized) tile).getNodeType();
            NodeModifier mod = ((TileNodeEnergized) tile).getNodeModifier();

            this.world.setBlockState(this.pos.down(),
                    ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 0), 3);
            TileEntity tileNew = this.world.getTileEntity(this.pos.down());
            if (tileNew instanceof TileNode) {
                TileNode node = (TileNode) tileNew;
                node.setNodeModifier(mod);
                node.setNodeType(type);
                node.setAspects(base.copy());
                Aspect[] aspects = node.getAspects().getAspects();
                for (Aspect a : aspects) {
                    node.takeFromContainer(a, node.getAspects().getAmount(a));
                }
            }
            this.world.addBlockEvent(this.pos, this.getBlockType(), 10, 10);
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.markDirty();
            this.status = 0;
        }

        if (this.status == 0 || this.world == null || !this.world.isBlockPowered(this.pos)) {
            if (this.count > 0) {
                --this.count;
                if (this.count > 50 && this.world != null && this.world.isRemote) {
                    this.spawnConverterBolts();
                }
            }
        } else if (this.count < 1000) {
            ++this.count;
            if (!this.world.isRemote) {
                TileEntity tileNew = this.world.getTileEntity(this.pos.down());
                if (tileNew instanceof TileNode) {
                    TileNode node = (TileNode) tileNew;
                    AspectList al = node.getAspects();
                    Aspect[] aspects = al.getAspects();
                    if (aspects.length > 0) {
                        node.takeFromContainer(aspects[this.world.rand.nextInt(aspects.length)], 1);
                        if (this.count % 5 == 0 || node.getAspects().visSize() == 0) {
                            this.world.notifyBlockUpdate(this.pos.down(), this.world.getBlockState(this.pos.down()),
                                    this.world.getBlockState(this.pos.down()), 3);
                        }
                    }
                }
            }
            if (this.count > 50 && this.world.isRemote) {
                this.spawnConverterBolts();
            }
        }

        if (this.count > 1000) {
            this.count = 1000;
        }
    }

    private void spawnConverterBolts() {
        Thaumcraft.proxy.bolt(this.world,
                this.pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                this.pos.getY() + 0.5F,
                this.pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                this.pos.getX() + 0.5F,
                this.pos.getY() - 0.5F,
                this.pos.getZ() + 0.5F,
                0x66CCFF, 4);

        if (this.world.rand.nextBoolean() && this.hasStabilizer()) {
            Thaumcraft.proxy.bolt(this.world,
                    this.pos.getX() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                    this.pos.getY() - 1.5F,
                    this.pos.getZ() + 0.25F + this.world.rand.nextFloat() * 0.5F,
                    this.pos.getX() + 0.5F,
                    this.pos.getY() - 0.5F,
                    this.pos.getZ() + 0.5F,
                    0x66CCFF, 4);
        }
    }

    private boolean hasStabilizer() {
        if (this.world == null || this.pos == null) {
            return false;
        }
        BlockPos below = this.pos.down(2);
        TileEntity te = this.world.getTileEntity(below);
        return !this.world.isAirBlock(below) && te instanceof TileNodeStabilizer;
    }

    public void checkStatus() {
        if (this.world == null || this.pos == null) {
            return;
        }
        if (this.count == -1) {
            this.count = 0;
        }
        if (!(this.status != 2 || this.count <= 50 || this.hasStabilizer()
                && this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockAiry
                && this.world.getBlockState(this.pos.down()).getValue(BlockAiry.TYPE) == 5)) {
            BlockAiry.explodify(this.getWorld(), this.pos.getX(), this.pos.getY() - 1, this.pos.getZ());
            this.status = 0;
            this.count = 50;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        } else if (this.world.isBlockPowered(this.pos)
                && this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockAiry
                && this.world.getBlockState(this.pos.down()).getValue(BlockAiry.TYPE) == 0
                && this.hasStabilizer()) {
            this.status = 1;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        } else if (this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockAiry
                && this.world.getBlockState(this.pos.down()).getValue(BlockAiry.TYPE) == 5) {
            this.status = 2;
            this.count = 1000;
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        } else {
            this.status = 0;
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.status = nbt.getInteger("status");
        this.count = nbt.getInteger("count");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setInteger("status", this.status);
        nbt.setInteger("count", this.count);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 10 && type == 10) {
            if (this.world != null && this.world.isRemote) {
                Thaumcraft.proxy.burst(this.world, this.pos.getX() + 0.5D, this.pos.getY() - 0.5D, this.pos.getZ() + 0.5D, 1.0F);
                this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() - 0.5D, this.pos.getZ() + 0.5D,
                        TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }
}
