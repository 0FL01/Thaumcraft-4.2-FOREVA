package thaumcraft.common.tiles;

import net.minecraft.util.EnumFacing;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileJarFillable;

public class TileJarFillableVoid
extends TileJarFillable {
    int count = 0;

    @Override
    public int addToContainer(Aspect tt, int am) {
        boolean up = this.amount < this.maxAmount;
        if (am == 0) {
            return am;
        }
        if (tt == this.aspect || this.amount == 0) {
            this.aspect = tt;
            this.amount += am;
            am = 0;
            if (this.amount > this.maxAmount) {
                this.amount = this.maxAmount;
            }
        }
        if (up) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            this.markDirty();
        }
        return am;
    }

    @Override
    public int getMinimumSuction() {
        return this.aspectFilter != null ? 48 : 32;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (this.aspectFilter != null && this.amount < this.maxAmount) {
            return 48;
        }
        return 32;
    }

    @Override
    public void update() {
        if (!world.isRemote && ++this.count % 5 == 0) {
            this.fillJar();
        }
    }
}
