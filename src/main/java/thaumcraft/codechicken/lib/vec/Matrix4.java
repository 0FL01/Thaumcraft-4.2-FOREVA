package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import org.lwjgl.opengl.GL11;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.IrreversibleTransformationException;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class Matrix4
extends Transformation
implements Copyable<Matrix4> {
    private static DoubleBuffer glBuf = ByteBuffer.allocateDirect(128).order(ByteOrder.nativeOrder()).asDoubleBuffer();
    public double m00;
    public double m01;
    public double m02;
    public double m03;
    public double m10;
    public double m11;
    public double m12;
    public double m13;
    public double m20;
    public double m21;
    public double m22;
    public double m23;
    public double m30;
    public double m31;
    public double m32;
    public double m33;

    public Matrix4() {
        this.m33 = 1.0;
        this.m22 = 1.0;
        this.m11 = 1.0;
        this.m00 = 1.0;
    }

    public Matrix4(double d00, double d01, double d02, double d03, double d10, double d11, double d12, double d13, double d20, double d21, double d22, double d23, double d30, double d31, double d32, double d33) {
        this.m00 = d00;
        this.m01 = d01;
        this.m02 = d02;
        this.m03 = d03;
        this.m10 = d10;
        this.m11 = d11;
        this.m12 = d12;
        this.m13 = d13;
        this.m20 = d20;
        this.m21 = d21;
        this.m22 = d22;
        this.m23 = d23;
        this.m30 = d30;
        this.m31 = d31;
        this.m32 = d32;
        this.m33 = d33;
    }

    public Matrix4(Matrix4 mat) {
        this.set(mat);
    }

    public Matrix4 setIdentity() {
        this.m33 = 1.0;
        this.m22 = 1.0;
        this.m11 = 1.0;
        this.m00 = 1.0;
        this.m32 = 0.0;
        this.m31 = 0.0;
        this.m30 = 0.0;
        this.m23 = 0.0;
        this.m21 = 0.0;
        this.m20 = 0.0;
        this.m13 = 0.0;
        this.m12 = 0.0;
        this.m10 = 0.0;
        this.m03 = 0.0;
        this.m02 = 0.0;
        this.m01 = 0.0;
        return this;
    }

    public Matrix4 translate(Vector3 vec) {
        this.m03 += this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z;
        this.m13 += this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z;
        this.m23 += this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z;
        this.m33 += this.m30 * vec.x + this.m31 * vec.y + this.m32 * vec.z;
        return this;
    }

    public Matrix4 scale(Vector3 vec) {
        this.m00 *= vec.x;
        this.m10 *= vec.x;
        this.m20 *= vec.x;
        this.m30 *= vec.x;
        this.m01 *= vec.y;
        this.m11 *= vec.y;
        this.m21 *= vec.y;
        this.m31 *= vec.y;
        this.m02 *= vec.z;
        this.m12 *= vec.z;
        this.m22 *= vec.z;
        this.m32 *= vec.z;
        return this;
    }

    public Matrix4 rotate(double angle, Vector3 axis) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double mc = 1.0 - c;
        double xy = axis.x * axis.y;
        double yz = axis.y * axis.z;
        double xz = axis.x * axis.z;
        double xs = axis.x * s;
        double ys = axis.y * s;
        double zs = axis.z * s;
        double f00 = axis.x * axis.x * mc + c;
        double f10 = xy * mc + zs;
        double f20 = xz * mc - ys;
        double f01 = xy * mc - zs;
        double f11 = axis.y * axis.y * mc + c;
        double f21 = yz * mc + xs;
        double f02 = xz * mc + ys;
        double f12 = yz * mc - xs;
        double f22 = axis.z * axis.z * mc + c;
        double t00 = this.m00 * f00 + this.m01 * f10 + this.m02 * f20;
        double t10 = this.m10 * f00 + this.m11 * f10 + this.m12 * f20;
        double t20 = this.m20 * f00 + this.m21 * f10 + this.m22 * f20;
        double t30 = this.m30 * f00 + this.m31 * f10 + this.m32 * f20;
        double t01 = this.m00 * f01 + this.m01 * f11 + this.m02 * f21;
        double t11 = this.m10 * f01 + this.m11 * f11 + this.m12 * f21;
        double t21 = this.m20 * f01 + this.m21 * f11 + this.m22 * f21;
        double t31 = this.m30 * f01 + this.m31 * f11 + this.m32 * f21;
        this.m02 = this.m00 * f02 + this.m01 * f12 + this.m02 * f22;
        this.m12 = this.m10 * f02 + this.m11 * f12 + this.m12 * f22;
        this.m22 = this.m20 * f02 + this.m21 * f12 + this.m22 * f22;
        this.m32 = this.m30 * f02 + this.m31 * f12 + this.m32 * f22;
        this.m00 = t00;
        this.m10 = t10;
        this.m20 = t20;
        this.m30 = t30;
        this.m01 = t01;
        this.m11 = t11;
        this.m21 = t21;
        this.m31 = t31;
        return this;
    }

    public Matrix4 rotate(Rotation rotation) {
        rotation.apply(this);
        return this;
    }

    public Matrix4 leftMultiply(Matrix4 mat) {
        double n00 = this.m00 * mat.m00 + this.m10 * mat.m01 + this.m20 * mat.m02 + this.m30 * mat.m03;
        double n01 = this.m01 * mat.m00 + this.m11 * mat.m01 + this.m21 * mat.m02 + this.m31 * mat.m03;
        double n02 = this.m02 * mat.m00 + this.m12 * mat.m01 + this.m22 * mat.m02 + this.m32 * mat.m03;
        double n03 = this.m03 * mat.m00 + this.m13 * mat.m01 + this.m23 * mat.m02 + this.m33 * mat.m03;
        double n10 = this.m00 * mat.m10 + this.m10 * mat.m11 + this.m20 * mat.m12 + this.m30 * mat.m13;
        double n11 = this.m01 * mat.m10 + this.m11 * mat.m11 + this.m21 * mat.m12 + this.m31 * mat.m13;
        double n12 = this.m02 * mat.m10 + this.m12 * mat.m11 + this.m22 * mat.m12 + this.m32 * mat.m13;
        double n13 = this.m03 * mat.m10 + this.m13 * mat.m11 + this.m23 * mat.m12 + this.m33 * mat.m13;
        double n20 = this.m00 * mat.m20 + this.m10 * mat.m21 + this.m20 * mat.m22 + this.m30 * mat.m23;
        double n21 = this.m01 * mat.m20 + this.m11 * mat.m21 + this.m21 * mat.m22 + this.m31 * mat.m23;
        double n22 = this.m02 * mat.m20 + this.m12 * mat.m21 + this.m22 * mat.m22 + this.m32 * mat.m23;
        double n23 = this.m03 * mat.m20 + this.m13 * mat.m21 + this.m23 * mat.m22 + this.m33 * mat.m23;
        double n30 = this.m00 * mat.m30 + this.m10 * mat.m31 + this.m20 * mat.m32 + this.m30 * mat.m33;
        double n31 = this.m01 * mat.m30 + this.m11 * mat.m31 + this.m21 * mat.m32 + this.m31 * mat.m33;
        double n32 = this.m02 * mat.m30 + this.m12 * mat.m31 + this.m22 * mat.m32 + this.m32 * mat.m33;
        double n33 = this.m03 * mat.m30 + this.m13 * mat.m31 + this.m23 * mat.m32 + this.m33 * mat.m33;
        this.m00 = n00;
        this.m01 = n01;
        this.m02 = n02;
        this.m03 = n03;
        this.m10 = n10;
        this.m11 = n11;
        this.m12 = n12;
        this.m13 = n13;
        this.m20 = n20;
        this.m21 = n21;
        this.m22 = n22;
        this.m23 = n23;
        this.m30 = n30;
        this.m31 = n31;
        this.m32 = n32;
        this.m33 = n33;
        return this;
    }

    public Matrix4 multiply(Matrix4 mat) {
        double n00 = this.m00 * mat.m00 + this.m01 * mat.m10 + this.m02 * mat.m20 + this.m03 * mat.m30;
        double n01 = this.m00 * mat.m01 + this.m01 * mat.m11 + this.m02 * mat.m21 + this.m03 * mat.m31;
        double n02 = this.m00 * mat.m02 + this.m01 * mat.m12 + this.m02 * mat.m22 + this.m03 * mat.m32;
        double n03 = this.m00 * mat.m03 + this.m01 * mat.m13 + this.m02 * mat.m23 + this.m03 * mat.m33;
        double n10 = this.m10 * mat.m00 + this.m11 * mat.m10 + this.m12 * mat.m20 + this.m13 * mat.m30;
        double n11 = this.m10 * mat.m01 + this.m11 * mat.m11 + this.m12 * mat.m21 + this.m13 * mat.m31;
        double n12 = this.m10 * mat.m02 + this.m11 * mat.m12 + this.m12 * mat.m22 + this.m13 * mat.m32;
        double n13 = this.m10 * mat.m03 + this.m11 * mat.m13 + this.m12 * mat.m23 + this.m13 * mat.m33;
        double n20 = this.m20 * mat.m00 + this.m21 * mat.m10 + this.m22 * mat.m20 + this.m23 * mat.m30;
        double n21 = this.m20 * mat.m01 + this.m21 * mat.m11 + this.m22 * mat.m21 + this.m23 * mat.m31;
        double n22 = this.m20 * mat.m02 + this.m21 * mat.m12 + this.m22 * mat.m22 + this.m23 * mat.m32;
        double n23 = this.m20 * mat.m03 + this.m21 * mat.m13 + this.m22 * mat.m23 + this.m23 * mat.m33;
        double n30 = this.m30 * mat.m00 + this.m31 * mat.m10 + this.m32 * mat.m20 + this.m33 * mat.m30;
        double n31 = this.m30 * mat.m01 + this.m31 * mat.m11 + this.m32 * mat.m21 + this.m33 * mat.m31;
        double n32 = this.m30 * mat.m02 + this.m31 * mat.m12 + this.m32 * mat.m22 + this.m33 * mat.m32;
        double n33 = this.m30 * mat.m03 + this.m31 * mat.m13 + this.m32 * mat.m23 + this.m33 * mat.m33;
        this.m00 = n00;
        this.m01 = n01;
        this.m02 = n02;
        this.m03 = n03;
        this.m10 = n10;
        this.m11 = n11;
        this.m12 = n12;
        this.m13 = n13;
        this.m20 = n20;
        this.m21 = n21;
        this.m22 = n22;
        this.m23 = n23;
        this.m30 = n30;
        this.m31 = n31;
        this.m32 = n32;
        this.m33 = n33;
        return this;
    }

    public Matrix4 transpose() {
        double n00 = this.m00;
        double n10 = this.m01;
        double n20 = this.m02;
        double n30 = this.m03;
        double n01 = this.m10;
        double n11 = this.m11;
        double n21 = this.m12;
        double n31 = this.m13;
        double n02 = this.m20;
        double n12 = this.m21;
        double n22 = this.m22;
        double n32 = this.m23;
        double n03 = this.m30;
        double n13 = this.m31;
        double n23 = this.m32;
        double n33 = this.m33;
        this.m00 = n00;
        this.m01 = n01;
        this.m02 = n02;
        this.m03 = n03;
        this.m10 = n10;
        this.m11 = n11;
        this.m12 = n12;
        this.m13 = n13;
        this.m20 = n20;
        this.m21 = n21;
        this.m22 = n22;
        this.m23 = n23;
        this.m30 = n30;
        this.m31 = n31;
        this.m32 = n32;
        this.m33 = n33;
        return this;
    }

    @Override
    public Matrix4 copy() {
        return new Matrix4(this);
    }

    public Matrix4 set(Matrix4 mat) {
        this.m00 = mat.m00;
        this.m01 = mat.m01;
        this.m02 = mat.m02;
        this.m03 = mat.m03;
        this.m10 = mat.m10;
        this.m11 = mat.m11;
        this.m12 = mat.m12;
        this.m13 = mat.m13;
        this.m20 = mat.m20;
        this.m21 = mat.m21;
        this.m22 = mat.m22;
        this.m23 = mat.m23;
        this.m30 = mat.m30;
        this.m31 = mat.m31;
        this.m32 = mat.m32;
        this.m33 = mat.m33;
        return this;
    }

    @Override
    public void apply(Matrix4 mat) {
        mat.multiply(this);
    }

    private void mult3x3(Vector3 vec) {
        double x = this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z;
        double y = this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z;
        double z = this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z;
        vec.x = x;
        vec.y = y;
        vec.z = z;
    }

    @Override
    public void apply(Vector3 vec) {
        this.mult3x3(vec);
        vec.add(this.m03, this.m13, this.m23);
    }

    @Override
    public void applyN(Vector3 vec) {
        this.mult3x3(vec);
        vec.normalize();
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "[" + new BigDecimal(this.m00, cont) + "," + new BigDecimal(this.m01, cont) + "," + new BigDecimal(this.m02, cont) + "," + new BigDecimal(this.m03, cont) + "]\n" + "[" + new BigDecimal(this.m10, cont) + "," + new BigDecimal(this.m11, cont) + "," + new BigDecimal(this.m12, cont) + "," + new BigDecimal(this.m13, cont) + "]\n" + "[" + new BigDecimal(this.m20, cont) + "," + new BigDecimal(this.m21, cont) + "," + new BigDecimal(this.m22, cont) + "," + new BigDecimal(this.m23, cont) + "]\n" + "[" + new BigDecimal(this.m30, cont) + "," + new BigDecimal(this.m31, cont) + "," + new BigDecimal(this.m32, cont) + "," + new BigDecimal(this.m33, cont) + "]";
    }

    public Matrix4 apply(Transformation t) {
        t.apply(this);
        return this;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void glApply() {
        glBuf.put(this.m00).put(this.m10).put(this.m20).put(this.m30).put(this.m01).put(this.m11).put(this.m21).put(this.m31).put(this.m02).put(this.m12).put(this.m22).put(this.m32).put(this.m03).put(this.m13).put(this.m23).put(this.m33);
        glBuf.flip();
        GL11.glMultMatrix((DoubleBuffer)glBuf);
    }

    @Override
    public Transformation inverse() {
        throw new IrreversibleTransformationException(this);
    }
}

