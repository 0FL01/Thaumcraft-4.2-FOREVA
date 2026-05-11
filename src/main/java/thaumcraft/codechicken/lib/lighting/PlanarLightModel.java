package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.colour.ColourRGBA;
import thaumcraft.codechicken.lib.lighting.LightModel;
import thaumcraft.codechicken.lib.render.CCRenderState;

public class PlanarLightModel
implements CCRenderState.IVertexOperation {
    public static PlanarLightModel standardLightModel = LightModel.standardLightModel.reducePlanar();
    public int[] colours;

    public PlanarLightModel(int[] colours) {
        this.colours = colours;
    }

    @Override
    public boolean load() {
        CCRenderState.pipeline.addDependency(CCRenderState.sideAttrib);
        CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
        return true;
    }

    @Override
    public void operate() {
        CCRenderState.setColour(ColourRGBA.multiply(CCRenderState.colour, this.colours[CCRenderState.side]));
    }

    @Override
    public int operationID() {
        return LightModel.operationIndex;
    }
}

