package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.ITransformation;
import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.TransformationList;
import thaumcraft.codechicken.lib.vec.Translation;
import thaumcraft.codechicken.lib.vec.Vector3;

public abstract class Transformation
extends ITransformation<Vector3, Transformation>
implements CCRenderState.IVertexOperation {
    public static final int operationIndex = CCRenderState.registerOperation();

    public abstract void applyN(Vector3 var1);

    public abstract void apply(Matrix4 var1);

    @Override
    public Transformation at(Vector3 point) {
        return new TransformationList(new Translation(-point.x, -point.y, -point.z), this, point.translation());
    }

    @Override
    public TransformationList with(Transformation t) {
        return new TransformationList(this, t);
    }

    @SideOnly(value=Side.CLIENT)
    public abstract void glApply();

    @Override
    public boolean load() {
        CCRenderState.pipeline.addRequirement(CCRenderState.normalAttrib.operationID());
        return !this.isRedundant();
    }

    @Override
    public void operate() {
        this.apply(CCRenderState.vert.vec);
        if (CCRenderState.normalAttrib.active) {
            this.applyN(CCRenderState.normal);
        }
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
