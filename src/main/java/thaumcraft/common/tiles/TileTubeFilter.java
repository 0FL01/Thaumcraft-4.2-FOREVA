package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

public class TileTubeFilter extends TileTube implements IAspectContainer {
    public Aspect aspectFilter = null;

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.aspectFilter = Aspect.getAspect(nbt.getString("AspectFilter"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        if (this.aspectFilter != null) nbt.setString("AspectFilter", this.aspectFilter.getTag());
    }

    @Override
    protected void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        super.calculateSuction(this.aspectFilter, restrict, directional);
    }

    @Override
    public AspectList getAspects() { return this.aspectFilter == null ? null : new AspectList().add(this.aspectFilter, -1); }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect tag) { return false; }

    @Override
    public int addToContainer(Aspect tag, int amount) { return amount; }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) { return false; }

    @Override
    public boolean takeFromContainer(AspectList ot) { return false; }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) { return false; }

    @Override
    public boolean doesContainerContain(AspectList ot) { return false; }

    @Override
    public int containerContains(Aspect tag) { return 0; }
}
