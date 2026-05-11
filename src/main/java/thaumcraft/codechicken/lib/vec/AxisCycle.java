package thaumcraft.codechicken.lib.vec;

import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.RedundantTransformation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.VariableTransformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class AxisCycle {
    public static Transformation[] cycles = new Transformation[]{new RedundantTransformation(), new VariableTransformation(new Matrix4(0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)){

        @Override
        public void apply(Vector3 vec) {
            double d2;
            double d0 = vec.x;
            double d1 = vec.y;
            vec.x = d2 = vec.z;
            vec.y = d0;
            vec.z = d1;
        }

        @Override
        public Transformation inverse() {
            return cycles[2];
        }
    }, new VariableTransformation(new Matrix4(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)){

        @Override
        public void apply(Vector3 vec) {
            double d0 = vec.x;
            double d1 = vec.y;
            double d2 = vec.z;
            vec.x = d1;
            vec.y = d2;
            vec.z = d0;
        }

        @Override
        public Transformation inverse() {
            return cycles[1];
        }
    }};
}

