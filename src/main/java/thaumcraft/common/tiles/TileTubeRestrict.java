package thaumcraft.common.tiles;

import thaumcraft.api.aspects.Aspect;

public class TileTubeRestrict extends TileTube {
    @Override
    protected void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        super.calculateSuction(filter, true, directional);
    }
}
