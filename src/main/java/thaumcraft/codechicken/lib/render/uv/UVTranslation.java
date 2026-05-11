package thaumcraft.codechicken.lib.render.uv;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;

public class UVTranslation
extends UVTransformation {
    public double du;
    public double dv;

    public UVTranslation(double u, double v) {
        this.du = u;
        this.dv = v;
    }

    @Override
    public void apply(UV uv) {
        uv.u += this.du;
        uv.v += this.dv;
    }

    @Override
    public UVTransformation at(UV point) {
        return this;
    }

    @Override
    public UVTransformation inverse() {
        return new UVTranslation(-this.du, -this.dv);
    }

    @Override
    public UVTransformation merge(UVTransformation next) {
        if (next instanceof UVTranslation) {
            UVTranslation t = (UVTranslation)next;
            return new UVTranslation(this.du + t.du, this.dv + t.dv);
        }
        return null;
    }

    @Override
    public boolean isRedundant() {
        return MathHelper.between(-1.0E-5, this.du, 1.0E-5) && MathHelper.between(-1.0E-5, this.dv, 1.0E-5);
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UVTranslation(" + new BigDecimal(this.du, cont) + ", " + new BigDecimal(this.dv, cont) + ")";
    }
}

