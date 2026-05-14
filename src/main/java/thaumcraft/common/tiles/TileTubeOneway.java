package thaumcraft.common.tiles;

import thaumcraft.api.aspects.Aspect;

public class TileTubeOneway extends TileTube {
    @Override
    protected void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        super.calculateSuction(filter, restrict, true);
    }

    @Override
    protected void equalizeWithNeighbours(boolean directional) {
        super.equalizeWithNeighbours(true);
    }
}
