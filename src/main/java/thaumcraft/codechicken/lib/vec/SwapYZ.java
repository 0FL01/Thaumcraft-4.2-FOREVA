package thaumcraft.codechicken.lib.vec;

import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.VariableTransformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class SwapYZ
extends VariableTransformation {
    public SwapYZ() {
        super(new Matrix4(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0));
    }

    @Override
    public void apply(Vector3 vec) {
        double vz = vec.z;
        vec.z = vec.y;
        vec.y = vz;
    }

    @Override
    public Transformation inverse() {
        return this;
    }
}

