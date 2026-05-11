package thaumcraft.codechicken.lib.render.uv;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;
import thaumcraft.codechicken.lib.util.Copyable;

public class UV
implements Copyable<UV> {
    public double u;
    public double v;
    public int tex;

    public UV() {
    }

    public UV(double u, double v) {
        this(u, v, 0);
    }

    public UV(double u, double v, int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
    }

    public UV(UV uv) {
        this(uv.u, uv.v, uv.tex);
    }

    public UV set(double u, double v, int tex) {
        this.u = u;
        this.v = v;
        this.tex = tex;
        return this;
    }

    public UV set(double u, double v) {
        return this.set(u, v, this.tex);
    }

    public UV set(UV uv) {
        return this.set(uv.u, uv.v, uv.tex);
    }

    @Override
    public UV copy() {
        return new UV(this);
    }

    public UV add(UV uv) {
        this.u += uv.u;
        this.v += uv.v;
        return this;
    }

    public UV multiply(double d) {
        this.u *= d;
        this.v *= d;
        return this;
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "UV(" + new BigDecimal(this.u, cont) + ", " + new BigDecimal(this.v, cont) + ")";
    }

    public UV apply(UVTransformation t) {
        t.apply(this);
        return this;
    }

    public boolean equals(Object o) {
        if (!(o instanceof UV)) {
            return false;
        }
        UV uv = (UV)o;
        return this.u == uv.u && this.v == uv.v;
    }
}

