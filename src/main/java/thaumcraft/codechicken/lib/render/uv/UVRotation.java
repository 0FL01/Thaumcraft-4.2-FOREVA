package thaumcraft.codechicken.lib.render.uv;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;

public class UVRotation
extends UVTransformation {
    public double angle;

    public UVRotation(double angle) {
        this.angle = angle;
    }

    @Override
    public void apply(UV uv) {
        double c = MathHelper.cos(this.angle);
        double s = MathHelper.sin(this.angle);
        double u2 = c * uv.u + s * uv.v;
        uv.v = -s * uv.u + c * uv.v;
        uv.u = u2;
    }

    @Override
    public UVTransformation inverse() {
        return new UVRotation(-this.angle);
    }

    @Override
    public UVTransformation merge(UVTransformation next) {
        if (next instanceof UVRotation) {
            return new UVRotation(this.angle + ((UVRotation)next).angle);
        }
        return null;
    }

    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, this.angle, 1.0E-5);
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVRotation(" + new BigDecimal(this.angle, cont) + ")";
    }
}

