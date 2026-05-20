package thaumcraft.common.tiles;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

import java.util.ArrayList;
import java.util.List;

public class TileArcaneLampFertility extends TileThaumcraft implements ITickable, IEssentiaTransport {
    public EnumFacing facing = EnumFacing.byIndex(0);
    public int charges = 0;
    int count = 0;
    int drawDelay = 0;

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.world.isRemote) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        if (this.charges < 4 && this.drawEssentia()) {
            ++this.charges;
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
        if (this.charges > 1 && ++this.count % 300 == 0) {
            this.updateAnimals();
        }
    }

    private void updateAnimals() {
        List<EntityAnimal> animals = this.world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(this.pos).grow(7.0D));
        for (EntityAnimal animal : animals) {
            if (animal.getGrowingAge() != 0 || animal.isInLove()) {
                continue;
            }
            ArrayList<EntityAnimal> sameClass = new ArrayList<>();
            for (EntityAnimal other : animals) {
                if (other.getClass().equals(animal.getClass())) {
                    sameClass.add(other);
                }
            }
            if (sameClass.size() > 7) {
                continue;
            }
            EntityAnimal mate = null;
            for (EntityAnimal other : sameClass) {
                if (other.getGrowingAge() != 0 || other.isInLove()) {
                    continue;
                }
                if (mate == null) {
                    mate = other;
                    continue;
                }
                this.charges -= 2;
                other.setInLove(null);
                mate.setInLove(null);
                return;
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getInteger("orientation"));
        if (this.facing == null) {
            this.facing = EnumFacing.byIndex(0);
        }
        this.charges = nbt.getInteger("charges");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.facing.getIndex());
        nbt.setInteger("charges", this.charges);
    }

    boolean drawEssentia() {
        if (++this.drawDelay % 5 != 0) {
            return false;
        }
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facing);
        if (te instanceof IEssentiaTransport) {
            IEssentiaTransport ic = (IEssentiaTransport) te;
            if (!ic.canOutputTo(this.facing.getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(this.facing.getOpposite()) < this.getSuctionAmount(this.facing)
                    && ic.takeEssentia(Aspect.LIFE, 1, this.facing.getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == this.facing;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == this.facing;
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
        return Aspect.LIFE;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return face == this.facing ? 128 - this.charges * 10 : 0;
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
    public int takeEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }
}
