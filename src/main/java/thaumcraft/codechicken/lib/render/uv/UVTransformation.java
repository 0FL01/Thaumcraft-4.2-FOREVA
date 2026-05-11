package thaumcraft.codechicken.lib.render.uv;

import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.render.uv.UVTransformationList;
import thaumcraft.codechicken.lib.render.uv.UVTranslation;
import thaumcraft.codechicken.lib.vec.ITransformation;

public abstract class UVTransformation
extends ITransformation<UV, UVTransformation>
implements CCRenderState.IVertexOperation {
    public static final int operationIndex = CCRenderState.registerOperation();

    @Override
    public UVTransformation at(UV point) {
        return new UVTransformationList(new UVTranslation(-point.u, -point.v), this, new UVTranslation(point.u, point.v));
    }

    @Override
    public UVTransformationList with(UVTransformation t) {
        return new UVTransformationList(this, t);
    }

    @Override
    public boolean load() {
        return !this.isRedundant();
    }

    @Override
    public void operate() {
        this.apply(CCRenderState.vert.uv);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}

