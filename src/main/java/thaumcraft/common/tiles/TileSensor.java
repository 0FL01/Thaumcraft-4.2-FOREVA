package thaumcraft.common.tiles;

import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigBlocks;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class TileSensor extends TileThaumcraft implements ITickable {
    public byte note = 0;
    public byte tone = 0;
    public int redstoneSignal = 0;

    public static final WeakHashMap<WorldServer, ArrayList<Integer[]>> noteBlockEvents = new WeakHashMap<>();

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        this.note = compound.getByte("note");
        this.tone = compound.getByte("tone");
        if (this.note < 0) {
            this.note = 0;
        }
        if (this.note > 24) {
            this.note = 24;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        compound.setByte("note", this.note);
        compound.setByte("tone", this.tone);
    }

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }

        if (this.redstoneSignal > 0) {
            --this.redstoneSignal;
            if (this.redstoneSignal == 0) {
                if (this.world.isRemote) {
                    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
                } else {
                    notifySignalChange();
                }
            }
        }

        if (this.world.isRemote) return;

        if (!(this.world instanceof WorldServer)) {
            return;
        }
        ArrayList<Integer[]> events = noteBlockEvents.get((WorldServer) this.world);
        if (events == null) {
            return;
        }

        for (Integer[] data : events) {
            if (data == null || data.length < 5) {
                continue;
            }
            if (data[3] != this.tone || data[4] != this.note) {
                continue;
            }
            if (!(getDistanceSq(data[0] + 0.5, data[1] + 0.5, data[2] + 0.5) <= 4096.0)) {
                continue;
            }
            this.triggerNote(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), false);
            this.redstoneSignal = 10;
            notifySignalChange();
            break;
        }
    }

    private void notifySignalChange() {
        if (this.world == null || this.pos == null) {
            return;
        }
        this.world.notifyNeighborsOfStateChange(this.pos, ConfigBlocks.blockWoodenDevice, false);
        this.world.notifyNeighborsOfStateChange(this.pos.down(), ConfigBlocks.blockWoodenDevice, false);
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    public void updateTone() {
        if (this.world == null || this.pos == null) {
            return;
        }
        Material materialBelow = this.world.getBlockState(this.pos.down()).getMaterial();
        this.tone = (byte) getInstrument(materialBelow);
    }

    public void changePitch() {
        this.note = (byte) ((this.note + 1) % 25);
        this.markDirty();
    }

    public void triggerNote(net.minecraft.world.World world, int x, int y, int z, boolean sound) {
        if (world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial() == Material.AIR) {
            int instrument = -1;
            if (sound) {
                Material materialBelow = world.getBlockState(new BlockPos(x, y - 1, z)).getMaterial();
                instrument = getInstrument(materialBelow);
            }
            world.addBlockEvent(new BlockPos(x, y, z), ConfigBlocks.blockWoodenDevice, instrument, this.note);
        }
    }

    private static int getInstrument(Material material) {
        if (material == Material.ROCK) return 1;
        if (material == Material.SAND) return 2;
        if (material == Material.GLASS) return 3;
        if (material == Material.WOOD) return 4;
        return 0;
    }
}
