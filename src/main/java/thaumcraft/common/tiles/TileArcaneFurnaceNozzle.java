package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileArcaneFurnaceNozzle extends TileThaumcraft implements ITickable, IEssentiaTransport {
    private EnumFacing facing = EnumFacing.UP;
    private TileArcaneFurnace furnace = null;
    private int drawDelay = 0;

    @Override
    public void update() {
        if (this.world == null || this.pos == null) {
            return;
        }
        if (this.furnace == null) {
            this.findFurnace();
        }
        if (!this.world.isRemote) {
            try {
                if (this.furnace != null && this.furnace.speedyTime < 60 && this.drawEssentia()) {
                    this.furnace.speedyTime += 600;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void findFurnace() {
        this.facing = null;
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity tile = this.world.getTileEntity(this.pos.offset(dir));
            if (tile instanceof TileArcaneFurnace) {
                this.facing = dir.getOpposite();
                this.furnace = (TileArcaneFurnace) tile;
                break;
            }
        }
    }

    private boolean drawEssentia() {
        if (this.facing == null || ++this.drawDelay % 5 != 0) {
            return false;
        }
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(
                this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facing);
        if (!(te instanceof IEssentiaTransport)) {
            return false;
        }
        IEssentiaTransport transport = (IEssentiaTransport) te;
        EnumFacing remote = this.facing.getOpposite();
        return transport.canOutputTo(remote)
                && transport.getSuctionAmount(remote) < this.getSuctionAmount(this.facing)
                && transport.takeEssentia(Aspect.FIRE, 1, remote) == 1;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return this.facing != null;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return this.facing != null;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
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
        return Aspect.FIRE;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        try {
            if (this.furnace != null && this.furnace.speedyTime < 40) {
                return 128;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }
}
