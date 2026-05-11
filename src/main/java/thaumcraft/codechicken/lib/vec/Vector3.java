package thaumcraft.codechicken.lib.vec;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Translation;

public class Vector3
implements Copyable<Vector3> {
    public static Vector3 zero = new Vector3();
    public static Vector3 one = new Vector3(1.0, 1.0, 1.0);
    public static Vector3 center = new Vector3(0.5, 0.5, 0.5);
    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    public Vector3(double d, double d1, double d2) {
        this.x = d;
        this.y = d1;
        this.z = d2;
    }

    public Vector3(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3(double[] da) {
        this(da[0], da[1], da[2]);
    }

    public Vector3(Vec3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3(BlockCoord coord) {
        this.x = coord.x;
        this.y = coord.y;
        this.z = coord.z;
    }

    @Override
    public Vector3 copy() {
        return new Vector3(this);
    }

    public static Vector3 fromEntity(Entity e) {
        return new Vector3(e.posX, e.posY, e.posZ);
    }

    public static Vector3 fromEntityCenter(Entity e) {
        return new Vector3(e.posX, e.posY - e.getYOffset() + (double)(e.height / 2.0f), e.posZ);
    }

    public static Vector3 fromTileEntity(TileEntity e) {
        return new Vector3(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
    }

    public static Vector3 fromTileEntityCenter(TileEntity e) {
        return new Vector3((double)e.getPos().getX() + 0.5, (double)e.getPos().getY() + 0.5, (double)e.getPos().getZ() + 0.5);
    }

    public static Vector3 fromAxes(double[] da) {
        return new Vector3(da[2], da[0], da[1]);
    }

    public Vector3 set(double d, double d1, double d2) {
        this.x = d;
        this.y = d1;
        this.z = d2;
        return this;
    }

    public Vector3 set(Vector3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    public double getSide(int side) {
        switch (side) {
            case 0: 
            case 1: {
                return this.y;
            }
            case 2: 
            case 3: {
                return this.z;
            }
            case 4: 
            case 5: {
                return this.x;
            }
        }
        throw new IndexOutOfBoundsException("Switch Falloff");
    }

    public Vector3 setSide(int s, double v) {
        switch (s) {
            case 0: 
            case 1: {
                this.y = v;
                break;
            }
            case 2: 
            case 3: {
                this.z = v;
                break;
            }
            case 4: 
            case 5: {
                this.x = v;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Switch Falloff");
            }
        }
        return this;
    }

    public double dotProduct(Vector3 vec) {
        double d = vec.x * this.x + vec.y * this.y + vec.z * this.z;
        if (d > 1.0 && d < 1.00001) {
            d = 1.0;
        } else if (d < -1.0 && d > -1.00001) {
            d = -1.0;
        }
        return d;
    }

    public double dotProduct(double d, double d1, double d2) {
        return d * this.x + d1 * this.y + d2 * this.z;
    }

    public Vector3 crossProduct(Vector3 vec) {
        double d = this.y * vec.z - this.z * vec.y;
        double d1 = this.z * vec.x - this.x * vec.z;
        double d2 = this.x * vec.y - this.y * vec.x;
        this.x = d;
        this.y = d1;
        this.z = d2;
        return this;
    }

    public Vector3 add(double d, double d1, double d2) {
        this.x += d;
        this.y += d1;
        this.z += d2;
        return this;
    }

    public Vector3 add(Vector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public Vector3 add(double d) {
        return this.add(d, d, d);
    }

    public Vector3 sub(Vector3 vec) {
        return this.subtract(vec);
    }

    public Vector3 subtract(Vector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public Vector3 negate(Vector3 vec) {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3 multiply(double d) {
        this.x *= d;
        this.y *= d;
        this.z *= d;
        return this;
    }

    public Vector3 multiply(Vector3 f) {
        this.x *= f.x;
        this.y *= f.y;
        this.z *= f.z;
        return this;
    }

    public Vector3 multiply(double fx, double fy, double fz) {
        this.x *= fx;
        this.y *= fy;
        this.z *= fz;
        return this;
    }

    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double magSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3 normalize() {
        double d = this.mag();
        if (d != 0.0) {
            this.multiply(1.0 / d);
        }
        return this;
    }

    public String toString() {
        MathContext cont = new MathContext(4, RoundingMode.HALF_UP);
        return "Vector3(" + new BigDecimal(this.x, cont) + ", " + new BigDecimal(this.y, cont) + ", " + new BigDecimal(this.z, cont) + ")";
    }

    public Vector3 perpendicular() {
        if (this.z == 0.0) {
            return this.zCrossProduct();
        }
        return this.xCrossProduct();
    }

    public Vector3 xCrossProduct() {
        double d = this.z;
        double d1 = -this.y;
        this.x = 0.0;
        this.y = d;
        this.z = d1;
        return this;
    }

    public Vector3 zCrossProduct() {
        double d = this.y;
        double d1 = -this.x;
        this.x = d;
        this.y = d1;
        this.z = 0.0;
        return this;
    }

    public Vector3 yCrossProduct() {
        double d = -this.z;
        double d1 = this.x;
        this.x = d;
        this.y = 0.0;
        this.z = d1;
        return this;
    }

    public Vector3 rotate(double angle, Vector3 axis) {
        Quat.aroundAxis(axis.copy().normalize(), angle).rotate(this);
        return this;
    }

    public Vector3 rotate(Quat rotator) {
        rotator.rotate(this);
        return this;
    }

    public Vec3d toVec3D() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public double angle(Vector3 vec) {
        return Math.acos(this.copy().normalize().dotProduct(vec.copy().normalize()));
    }

    public boolean isZero() {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
    }

    public boolean isAxial() {
        return this.x == 0.0 ? this.y == 0.0 || this.z == 0.0 : this.y == 0.0 && this.z == 0.0;
    }

    @SideOnly(value=Side.CLIENT)
    public org.lwjgl.util.vector.Vector3f vector3f() {
        return new org.lwjgl.util.vector.Vector3f((float)this.x, (float)this.y, (float)this.z);
    }

    @SideOnly(value=Side.CLIENT)
    public org.lwjgl.util.vector.Vector4f vector4f() {
        return new org.lwjgl.util.vector.Vector4f((float)this.x, (float)this.y, (float)this.z, 1.0f);
    }

    @SideOnly(value=Side.CLIENT)
    public void glVertex() {
        net.minecraft.client.renderer.Tessellator.getInstance().getBuffer().pos(this.x, this.y, this.z).endVertex();
    }

    public Vector3 YZintercept(Vector3 end, double px) {
        double dx = end.x - this.x;
        double dy = end.y - this.y;
        double dz = end.z - this.z;
        if (dx == 0.0) {
            return null;
        }
        double d = (px - this.x) / dx;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x = px;
        this.y += d * dy;
        this.z += d * dz;
        return this;
    }

    public Vector3 XZintercept(Vector3 end, double py) {
        double dx = end.x - this.x;
        double dy = end.y - this.y;
        double dz = end.z - this.z;
        if (dy == 0.0) {
            return null;
        }
        double d = (py - this.y) / dy;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x += d * dx;
        this.y = py;
        this.z += d * dz;
        return this;
    }

    public Vector3 XYintercept(Vector3 end, double pz) {
        double dx = end.x - this.x;
        double dy = end.y - this.y;
        double dz = end.z - this.z;
        if (dz == 0.0) {
            return null;
        }
        double d = (pz - this.z) / dz;
        if (MathHelper.between(-1.0E-5, d, 1.0E-5)) {
            return this;
        }
        if (!MathHelper.between(0.0, d, 1.0)) {
            return null;
        }
        this.x += d * dx;
        this.y += d * dy;
        this.z = pz;
        return this;
    }

    public Vector3 negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Translation translation() {
        return new Translation(this);
    }

    public double scalarProject(Vector3 b) {
        double l = b.mag();
        return l == 0.0 ? 0.0 : this.dotProduct(b) / l;
    }

    public Vector3 project(Vector3 b) {
        double l = b.magSquared();
        if (l == 0.0) {
            this.set(0.0, 0.0, 0.0);
            return this;
        }
        double m = this.dotProduct(b) / l;
        this.set(b).multiply(m);
        return this;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Vector3)) {
            return false;
        }
        Vector3 v = (Vector3)o;
        return this.x == v.x && this.y == v.y && this.z == v.z;
    }

    public boolean equalsT(Vector3 v) {
        return MathHelper.between(this.x - 1.0E-5, v.x, this.x + 1.0E-5) && MathHelper.between(this.y - 1.0E-5, v.y, this.y + 1.0E-5) && MathHelper.between(this.z - 1.0E-5, v.z, this.z + 1.0E-5);
    }

    public Vector3 apply(Transformation t) {
        t.apply(this);
        return this;
    }

    public Vector3 $tilde() {
        return this.normalize();
    }

    public Vector3 unary_$tilde() {
        return this.normalize();
    }

    public Vector3 $plus(Vector3 v) {
        return this.add(v);
    }

    public Vector3 $minus(Vector3 v) {
        return this.subtract(v);
    }

    public Vector3 $times(double d) {
        return this.multiply(d);
    }

    public Vector3 $div(double d) {
        return this.multiply(1.0 / d);
    }

    public Vector3 $times(Vector3 v) {
        return this.crossProduct(v);
    }

    public double $dot$times(Vector3 v) {
        return this.dotProduct(v);
    }
}
