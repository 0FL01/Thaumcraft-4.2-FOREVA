package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.lwjgl.opengl.GL11;
import thaumcraft.codechicken.lib.vec.Matrix4;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class Scale
extends Transformation {
    public Vector3 factor;

    public Scale(Vector3 factor) {
        this.factor = factor;
    }

    public Scale(double factor) {
        this(new Vector3(factor, factor, factor));
    }

    public Scale(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }

    @Override
    public void apply(Vector3 vec) {
        vec.multiply(this.factor);
    }

    @Override
    public void applyN(Vector3 normal) {
    }

    @Override
    public void apply(Matrix4 mat) {
        mat.scale(this.factor);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void glApply() {
        GL11.glScaled((double)this.factor.x, (double)this.factor.y, (double)this.factor.z);
    }

    @Override
    public Transformation inverse() {
        return new Scale(1.0 / this.factor.x, 1.0 / this.factor.y, 1.0 / this.factor.z);
    }

    @Override
    public Transformation merge(Transformation next) {
        if (next instanceof Scale) {
            return new Scale(this.factor.copy().multiply(((Scale)next).factor));
        }
        return null;
    }

    @Override
    public boolean isRedundant() {
        return this.factor.equalsT(Vector3.one);
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Scale(" + new BigDecimal(this.factor.x, cont) + ", " + new BigDecimal(this.factor.y, cont) + ", " + new BigDecimal(this.factor.z, cont) + ")";
    }
}

