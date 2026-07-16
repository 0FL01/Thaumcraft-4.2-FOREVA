package thaumcraft.common.tiles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;

import java.util.ArrayList;
import java.util.Collections;

public class TileFluxScrubber extends TileThaumcraft implements ITickable, IEssentiaTransport {
    public int essentia = 0;
    public int charges = 0;
    public int power = 0;
    public EnumFacing facing = EnumFacing.byIndex(0);
    public int count = 0;
    public final ArrayList<BlockCoordinates> checklist = new ArrayList<>();

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.count == 0) {
            this.count = this.world.rand.nextInt(1000);
        }
        if (!this.world.isRemote) {
            if (this.charges >= 4) {
                this.charges -= 4;
                if (this.world.rand.nextInt(4) == 0) {
                    ++this.essentia;
                    if (this.essentia > 4) {
                        this.essentia = 4;
                    }
                    this.markDirty();
                }
            }
            if (this.power < 5) {
                this.power += VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.AIR, 10);
            }
            if (this.power >= 5) {
                this.checkFlux();
            }
        }
    }

    boolean isFlux(int x, int y, int z) {
        Material mat = this.world.getBlockState(new BlockPos(x, y, z)).getMaterial();
        return mat == Config.fluxGoomaterial;
    }

    private void checkFlux() {
        int distance = 16;
        if (this.checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int c = -distance; c <= distance; ++c) {
                    for (int b = -distance; b <= distance; ++b) {
                        this.checklist.add(new BlockCoordinates(this.pos.getX() + a, this.pos.getY() + c, this.pos.getZ() + b));
                    }
                }
            }
            Collections.shuffle(this.checklist, this.world.rand);
        }
        int x = 0;
        int y = 0;
        int z = 0;
        for (int cc = 0; cc < 16 && this.checklist.size() > 0; ++cc) {
            x = this.checklist.get(0).x;
            y = this.checklist.get(0).y;
            z = this.checklist.get(0).z;
            this.checklist.remove(0);
            BlockPos target = new BlockPos(x, y, z);
            if (this.world.isAirBlock(target) || !this.isFlux(x, y, z) || !(this.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < distance * distance)) {
                continue;
            }
            this.power -= 5;
            IBlockState state = this.world.getBlockState(target);
            int lmd = state.getBlock().getMetaFromState(state);
            if (lmd > 0) {
                this.world.setBlockState(target, state.getBlock().getStateFromMeta(lmd - 1), 3);
            } else {
                this.world.setBlockToAir(target);
            }
            this.sendFluxCleanupEffect(target);
            ++this.charges;
            this.markDirty();
            return;
        }
    }

    void sendFluxCleanupEffect(BlockPos target) {
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockSparkle(target.getX(), target.getY(), target.getZ(), 0xDD00FF),
                new NetworkRegistry.TargetPoint(this.world.provider.getDimension(),
                        target.getX(), target.getY(), target.getZ(), 32.0));
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getInteger("facing"));
        if (this.facing == null) {
            this.facing = EnumFacing.byIndex(0);
        }
        this.charges = nbt.getInteger("charges");
        this.power = nbt.getInteger("power");
        this.essentia = nbt.getInteger("essentia");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("facing", this.facing.getIndex());
        nbt.setInteger("charges", this.charges);
        nbt.setInteger("power", this.power);
        nbt.setInteger("essentia", this.essentia);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == this.facing;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == this.facing;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return Aspect.MAGIC;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.essentia;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing loc) {
        int re = Math.min(this.essentia, amount);
        this.essentia -= re;
        this.markDirty();
        return re;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }
}
