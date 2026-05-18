package thaumcraft.client.fx;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

public class WRVector3 {
    public float x;
    public float y;
    public float z;

    public WRVector3(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public WRVector3(TileEntity tile) {
        this.x = tile.getPos().getX() + 0.5f;
        this.y = tile.getPos().getY() + 0.5f;
        this.z = tile.getPos().getZ() + 0.5f;
    }

    public WRVector3(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public WRVector3 add(WRVector3 vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public WRVector3 sub(WRVector3 vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public WRVector3 scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }

    public WRVector3 scale(float scalex, float scaley, float scalez) {
        this.x *= scalex;
        this.y *= scaley;
        this.z *= scalez;
        return this;
    }

    public WRVector3 normalize() {
        float length = this.length();
        if (length == 0.0f) {
            return this;
        }
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public float lengthPow2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public WRVector3 copy() {
        return new WRVector3(this.x, this.y, this.z);
    }

    public static WRVector3 crossProduct(WRVector3 vec1, WRVector3 vec2) {
        return new WRVector3(
                vec1.y * vec2.z - vec1.z * vec2.y,
                vec1.z * vec2.x - vec1.x * vec2.z,
                vec1.x * vec2.y - vec1.y * vec2.x
        );
    }

    public static WRVector3 xCrossProduct(WRVector3 vec) {
        return new WRVector3(0.0, vec.z, -vec.y);
    }

    public static WRVector3 zCrossProduct(WRVector3 vec) {
        return new WRVector3(-vec.y, vec.x, 0.0);
    }

    public static float dotProduct(WRVector3 vec1, WRVector3 vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
    }

    public static float angle(WRVector3 vec1, WRVector3 vec2) {
        return WRVector3.anglePreNorm(vec1.copy().normalize(), vec2.copy().normalize());
    }

    public static float anglePreNorm(WRVector3 vec1, WRVector3 vec2) {
        float value = WRVector3.dotProduct(vec1, vec2);
        value = Math.max(-1.0f, Math.min(1.0f, value));
        return (float) Math.acos(value);
    }

    public WRVector3 rotate(float angle, WRVector3 axis) {
        return WRMat4.rotationMat(angle, axis).translate(this);
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.z + "]";
    }

    public Vec3d toVec3D() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public static WRVector3 getPerpendicular(WRVector3 vec) {
        if (vec.z == 0.0f) {
            return WRVector3.zCrossProduct(vec);
        }
        return WRVector3.xCrossProduct(vec);
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }
}
