package thaumcraft.client.fx;

public class WRMat4 {
    private float[] mat;

    public WRMat4() {
        this.loadIdentity();
    }

    public WRMat4 loadIdentity() {
        this.mat = new float[16];
        this.mat[15] = 1.0f;
        this.mat[10] = 1.0f;
        this.mat[5] = 1.0f;
        this.mat[0] = 1.0f;
        return this;
    }

    public WRVector3 translate(WRVector3 vec) {
        float x = vec.x * this.mat[0] + vec.y * this.mat[1] + vec.z * this.mat[2] + this.mat[3];
        float y = vec.x * this.mat[4] + vec.y * this.mat[5] + vec.z * this.mat[6] + this.mat[7];
        float z = vec.x * this.mat[8] + vec.y * this.mat[9] + vec.z * this.mat[10] + this.mat[11];
        vec.x = x;
        vec.y = y;
        vec.z = z;
        return vec;
    }

    public static WRMat4 rotationMat(double angle, WRVector3 axis) {
        WRVector3 normalized = axis.copy().normalize();
        float x = normalized.x;
        float y = normalized.y;
        float z = normalized.z;
        float cos = (float) Math.cos(angle * 0.0174532925D);
        float oneMinusCos = 1.0f - cos;
        float sin = (float) Math.sin(angle * 0.0174532925D);

        WRMat4 rotmat = new WRMat4();
        rotmat.mat[0] = x * x * oneMinusCos + cos;
        rotmat.mat[1] = y * x * oneMinusCos + z * sin;
        rotmat.mat[2] = x * z * oneMinusCos - y * sin;
        rotmat.mat[4] = x * y * oneMinusCos - z * sin;
        rotmat.mat[5] = y * y * oneMinusCos + cos;
        rotmat.mat[6] = y * z * oneMinusCos + x * sin;
        rotmat.mat[8] = x * z * oneMinusCos + y * sin;
        rotmat.mat[9] = y * z * oneMinusCos - x * sin;
        rotmat.mat[10] = z * z * oneMinusCos + cos;
        rotmat.mat[15] = 1.0f;
        return rotmat;
    }
}
