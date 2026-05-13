package thaumcraft.common.tiles;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.items.wands.foci.FocusPortableHole;

public class TileHole extends TileThaumcraft implements net.minecraft.util.ITickable {
    public Block oldblock = Blocks.AIR;
    public int oldmeta = 0;
    public NBTTagCompound tileEntityCompound = null;
    public short countdown = 0;
    public short countdownmax = 120;
    public byte count = 0;
    public byte direction = 0;

    public TileHole() {
    }

    public TileHole(Block block, int meta, short max, byte count, byte direction, @Nullable TileEntity tile) {
        this.setStoredBlock(block.getStateFromMeta(meta), tile, max, count, direction);
    }

    public void setStoredBlock(IBlockState state, @Nullable TileEntity tile, short max, byte count, byte direction) {
        this.oldblock = state.getBlock();
        this.oldmeta = this.oldblock.getMetaFromState(state);
        this.countdownmax = max;
        this.count = count;
        this.direction = direction;
        if (tile != null) {
            this.tileEntityCompound = tile.writeToNBT(new NBTTagCompound());
        }
        this.markDirty();
    }

    public IBlockState getStoredState() {
        Block block = this.oldblock != null ? this.oldblock : Blocks.AIR;
        try {
            return block.getStateFromMeta(this.oldmeta);
        } catch (RuntimeException ignored) {
            return block.getDefaultState();
        }
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) return;

        if (this.countdown == 0 && this.count > 1 && this.direction != -1) {
            this.createOpeningPlane();
            BlockPos next = this.pos.offset(net.minecraft.util.EnumFacing.byIndex(this.direction).getOpposite());
            if (!FocusPortableHole.createHole(this.world, next.getX(), next.getY(), next.getZ(),
                    this.direction, (byte) (this.count - 1), this.countdownmax)) {
                this.count = 0;
            }
        }

        this.countdown++;
        if (this.countdown >= this.countdownmax) {
            this.restoreBlock();
        }
    }

    private void createOpeningPlane() {
        switch (this.direction) {
            case 0:
            case 1:
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && z == 0) continue;
                        BlockPos p = this.pos.add(x, 0, z);
                        FocusPortableHole.createHole(this.world, p.getX(), p.getY(), p.getZ(), -1, (byte) 1, this.countdownmax);
                    }
                }
                break;
            case 2:
            case 3:
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x == 0 && y == 0) continue;
                        BlockPos p = this.pos.add(x, y, 0);
                        FocusPortableHole.createHole(this.world, p.getX(), p.getY(), p.getZ(), -1, (byte) 1, this.countdownmax);
                    }
                }
                break;
            case 4:
            case 5:
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (y == 0 && z == 0) continue;
                        BlockPos p = this.pos.add(0, y, z);
                        FocusPortableHole.createHole(this.world, p.getX(), p.getY(), p.getZ(), -1, (byte) 1, this.countdownmax);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void restoreBlock() {
        World world = this.world;
        BlockPos pos = this.pos;
        IBlockState restoreState = this.getStoredState();
        world.setBlockState(pos, restoreState, 3);
        if (this.tileEntityCompound != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                this.tileEntityCompound.setInteger("x", pos.getX());
                this.tileEntityCompound.setInteger("y", pos.getY());
                this.tileEntityCompound.setInteger("z", pos.getZ());
                tile.readFromNBT(this.tileEntityCompound);
            }
        }
        if (restoreState.getBlock() != Blocks.AIR) {
            world.scheduleUpdate(pos, restoreState.getBlock(), 2);
        }
        world.notifyNeighborsRespectDebug(pos, restoreState.getBlock(), false);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        String name = nbt.getString("oldblockName");
        if (!name.isEmpty()) {
            this.oldblock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        }
        if (this.oldblock == null || this.oldblock == Blocks.AIR) {
            this.oldblock = Block.getBlockById(nbt.getInteger("oldblock"));
        }
        if (this.oldblock == null) {
            this.oldblock = Blocks.AIR;
        }
        this.oldmeta = nbt.getInteger("oldmeta");
        if (nbt.hasKey("TileEntity")) {
            this.tileEntityCompound = nbt.getCompoundTag("TileEntity");
        }
        this.countdown = nbt.getShort("countdown");
        this.countdownmax = nbt.getShort("countdownmax");
        this.count = nbt.getByte("count");
        this.direction = nbt.getByte("direction");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("oldblock", Block.getIdFromBlock(this.oldblock));
        if (this.oldblock != null && this.oldblock.getRegistryName() != null) {
            nbt.setString("oldblockName", this.oldblock.getRegistryName().toString());
        }
        nbt.setInteger("oldmeta", this.oldmeta);
        if (this.tileEntityCompound != null) {
            nbt.setTag("TileEntity", this.tileEntityCompound);
        }
        nbt.setShort("countdown", this.countdown);
        nbt.setShort("countdownmax", this.countdownmax);
        nbt.setByte("count", this.count);
        nbt.setByte("direction", this.direction);
    }
}
