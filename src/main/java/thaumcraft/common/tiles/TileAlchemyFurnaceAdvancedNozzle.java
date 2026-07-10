package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileAlchemyFurnaceAdvancedNozzle extends TileThaumcraft
        implements ITickable, IAspectContainer, IEssentiaTransport {
    EnumFacing facing;
    public TileAlchemyFurnaceAdvanced furnace;

    @Override
    public void update() {
        this.resolveFurnace();
    }

    public TileAlchemyFurnaceAdvanced getFurnace() {
        return this.resolveFurnace();
    }

    private TileAlchemyFurnaceAdvanced resolveFurnace() {
        if (this.world == null || this.pos == null) {
            return this.furnace;
        }
        if (this.furnace != null && !this.furnace.isInvalid()
                && this.world.getTileEntity(this.furnace.getPos()) == this.furnace
                && this.pos.distanceSq(this.furnace.getPos()) == 1.0D) {
            return this.furnace;
        }
        this.furnace = null;
        this.facing = null;
        for (EnumFacing direction : EnumFacing.VALUES) {
            TileEntity tile = this.world.getTileEntity(this.pos.offset(direction));
            if (tile instanceof TileAlchemyFurnaceAdvanced) {
                this.facing = direction.getOpposite();
                this.furnace = (TileAlchemyFurnaceAdvanced) tile;
                break;
            }
        }
        return this.furnace;
    }

    @Override
    public AspectList getAspects() {
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        return source == null ? null : source.aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        if (source == null || source.aspects.getAmount(aspect) < amount) {
            return false;
        }
        source.aspects.remove(aspect, amount);
        source.vis = source.aspects.visSize();
        source.syncContents(false);
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        return source != null && source.aspects.getAmount(aspect) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        return source == null ? 0 : source.aspects.getAmount(aspect);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        this.resolveFurnace();
        return face == this.facing;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        this.resolveFurnace();
        return face == this.facing;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
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
    public Aspect getEssentiaType(EnumFacing face) {
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        if (source == null || source.aspects.size() == 0) {
            return null;
        }
        return source.aspects.getAspects()[0];
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        Aspect aspect = this.getEssentiaType(face);
        TileAlchemyFurnaceAdvanced source = this.resolveFurnace();
        return source == null || aspect == null ? 0 : source.aspects.getAmount(aspect);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }
}
