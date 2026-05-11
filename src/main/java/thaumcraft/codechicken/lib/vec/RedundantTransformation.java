package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class RedundantTransformation
extends Transformation {
    @Override
    public void apply(Vector3 vec) {
    }

    @Override
    public void apply(Matrix4 mat) {
    }

    @Override
    public void applyN(Vector3 normal) {
    }

    @Override
    public Transformation at(Vector3 point) {
        return this;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void glApply() {
    }

    @Override
    public Transformation inverse() {
        return this;
    }

    @Override
    public Transformation merge(Transformation next) {
        return next;
    }

    @Override
    public boolean isRedundant() {
        return true;
    }

    public String toString() {
        return "Nothing()";
    }
}

