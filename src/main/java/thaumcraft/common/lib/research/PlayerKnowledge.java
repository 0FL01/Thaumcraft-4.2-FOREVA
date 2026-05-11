package thaumcraft.common.lib.research;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class PlayerKnowledge {

    public boolean hasDiscoveredAspect(String username, Aspect aspect) {
        return false;
    }

    public AspectList getAspectsDiscovered(String username) {
        return new AspectList();
    }
}
