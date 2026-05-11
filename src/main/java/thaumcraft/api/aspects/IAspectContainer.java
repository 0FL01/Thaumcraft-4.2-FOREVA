package thaumcraft.api.aspects;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IAspectContainer {
    public AspectList getAspects();

    public void setAspects(AspectList var1);

    public boolean doesContainerAccept(Aspect var1);

    public int addToContainer(Aspect var1, int var2);

    public boolean takeFromContainer(Aspect var1, int var2);

    @Deprecated
    public boolean takeFromContainer(AspectList var1);

    public boolean doesContainerContainAmount(Aspect var1, int var2);

    @Deprecated
    public boolean doesContainerContain(AspectList var1);

    public int containerContains(Aspect var1);
}

