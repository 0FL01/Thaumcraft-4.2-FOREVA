package thaumcraft.codechicken.lib.colour;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.util.Copyable;

public abstract class Colour
implements Copyable<Colour> {
    public byte r;
    public byte g;
    public byte b;
    public byte a;

    public Colour(int r, int g, int b, int a) {
        this.r = (byte)r;
        this.g = (byte)g;
        this.b = (byte)b;
        this.a = (byte)a;
    }

    public Colour(Colour colour) {
        this.r = colour.r;
        this.g = colour.g;
        this.b = colour.b;
        this.a = colour.a;
    }

    @SideOnly(value=Side.CLIENT)
    public void glColour() {
        GL11.glColor4ub((byte)this.r, (byte)this.g, (byte)this.b, (byte)this.a);
    }

    @SideOnly(value=Side.CLIENT)
    public void glColour(int a) {
        GL11.glColor4ub((byte)this.r, (byte)this.g, (byte)this.b, (byte)((byte)a));
    }

    public abstract int pack();

    public String toString() {
        return this.getClass().getSimpleName() + "[0x" + Integer.toHexString(this.pack()).toUpperCase() + "]";
    }

    public Colour add(Colour colour2) {
        this.a = (byte)(this.a + colour2.a);
        this.r = (byte)(this.r + colour2.r);
        this.g = (byte)(this.g + colour2.g);
        this.b = (byte)(this.b + colour2.b);
        return this;
    }

    public Colour sub(Colour colour2) {
        int ia = (this.a & 0xFF) - (colour2.a & 0xFF);
        int ir = (this.r & 0xFF) - (colour2.r & 0xFF);
        int ig = (this.g & 0xFF) - (colour2.g & 0xFF);
        int ib = (this.b & 0xFF) - (colour2.b & 0xFF);
        this.a = (byte)(ia < 0 ? 0 : ia);
        this.r = (byte)(ir < 0 ? 0 : ir);
        this.g = (byte)(ig < 0 ? 0 : ig);
        this.b = (byte)(ib < 0 ? 0 : ib);
        return this;
    }

    public Colour invert() {
        this.a = (byte)(255 - (this.a & 0xFF));
        this.r = (byte)(255 - (this.r & 0xFF));
        this.g = (byte)(255 - (this.g & 0xFF));
        this.b = (byte)(255 - (this.b & 0xFF));
        return this;
    }

    public Colour multiply(Colour colour2) {
        this.a = (byte)((double)(this.a & 0xFF) * ((double)(colour2.a & 0xFF) / 255.0));
        this.r = (byte)((double)(this.r & 0xFF) * ((double)(colour2.r & 0xFF) / 255.0));
        this.g = (byte)((double)(this.g & 0xFF) * ((double)(colour2.g & 0xFF) / 255.0));
        this.b = (byte)((double)(this.b & 0xFF) * ((double)(colour2.b & 0xFF) / 255.0));
        return this;
    }

    public Colour scale(double d) {
        this.a = (byte)((double)(this.a & 0xFF) * d);
        this.r = (byte)((double)(this.r & 0xFF) * d);
        this.g = (byte)((double)(this.g & 0xFF) * d);
        this.b = (byte)((double)(this.b & 0xFF) * d);
        return this;
    }

    public Colour interpolate(Colour colour2, double d) {
        return this.add(colour2.copy().sub(this).scale(d));
    }

    public Colour multiplyC(double d) {
        this.r = (byte)MathHelper.clip((double)(this.r & 0xFF) * d, 0.0, 255.0);
        this.g = (byte)MathHelper.clip((double)(this.g & 0xFF) * d, 0.0, 255.0);
        this.b = (byte)MathHelper.clip((double)(this.b & 0xFF) * d, 0.0, 255.0);
        return this;
    }

    @Override
    public abstract Colour copy();

    public int rgb() {
        return (this.r & 0xFF) << 16 | (this.g & 0xFF) << 8 | this.b & 0xFF;
    }

    public int argb() {
        return (this.a & 0xFF) << 24 | (this.r & 0xFF) << 16 | (this.g & 0xFF) << 8 | this.b & 0xFF;
    }

    public int rgba() {
        return (this.r & 0xFF) << 24 | (this.g & 0xFF) << 16 | (this.b & 0xFF) << 8 | this.a & 0xFF;
    }

    public Colour set(Colour colour) {
        this.r = colour.r;
        this.g = colour.g;
        this.b = colour.b;
        this.a = colour.a;
        return this;
    }

    public boolean equals(Colour colour) {
        return colour != null && this.rgba() == colour.rgba();
    }
}

