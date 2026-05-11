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

public class Translation
extends Transformation {
    public Vector3 vec;

    public Translation(Vector3 vec) {
        this.vec = vec;
    }

    public Translation(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }

    @Override
    public void apply(Vector3 vec) {
        vec.add(this.vec);
    }

    @Override
    public void applyN(Vector3 normal) {
    }

    @Override
    public void apply(Matrix4 mat) {
        mat.translate(this.vec);
    }

    @Override
    public Transformation at(Vector3 point) {
        return this;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void glApply() {
        GL11.glTranslated((double)this.vec.x, (double)this.vec.y, (double)this.vec.z);
    }

    @Override
    public Transformation inverse() {
        return new Translation(-this.vec.x, -this.vec.y, -this.vec.z);
    }

    @Override
    public Transformation merge(Transformation next) {
        if (next instanceof Translation) {
            return new Translation(this.vec.copy().add(((Translation)next).vec));
        }
        return null;
    }

    @Override
    public boolean isRedundant() {
        return this.vec.equalsT(Vector3.zero);
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Translation(" + new BigDecimal(this.vec.x, cont) + ", " + new BigDecimal(this.vec.y, cont) + ", " + new BigDecimal(this.vec.z, cont) + ")";
    }
}

