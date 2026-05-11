package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class LC
implements Copyable<LC> {
    public int side;
    public float fa;
    public float fb;
    public float fc;
    public float fd;

    public LC() {
        this(0, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public LC(int s, float a, float b, float c, float d) {
        this.side = s;
        this.fa = a;
        this.fb = b;
        this.fc = c;
        this.fd = d;
    }

    public LC set(int s, float a, float b, float c, float d) {
        this.side = s;
        this.fa = a;
        this.fb = b;
        this.fc = c;
        this.fd = d;
        return this;
    }

    public LC set(LC lc) {
        return this.set(lc.side, lc.fa, lc.fb, lc.fc, lc.fd);
    }

    public LC compute(Vector3 vec, Vector3 normal) {
        int side = CCModel.findSide(normal);
        if (side < 0) {
            return this.set(12, 1.0f, 0.0f, 0.0f, 0.0f);
        }
        return this.compute(vec, side);
    }

    public LC compute(Vector3 vec, int side) {
        boolean offset = false;
        switch (side) {
            case 0: {
                offset = vec.y <= 0.0;
                break;
            }
            case 1: {
                offset = vec.y >= 1.0;
                break;
            }
            case 2: {
                offset = vec.z <= 0.0;
                break;
            }
            case 3: {
                offset = vec.z >= 1.0;
                break;
            }
            case 4: {
                offset = vec.x <= 0.0;
                break;
            }
            case 5: {
                boolean bl = offset = vec.x >= 1.0;
            }
        }
        if (!offset) {
            side += 6;
        }
        return this.computeO(vec, side);
    }

    public LC computeO(Vector3 vec, int side) {
        Vector3 v1 = Rotation.axes[((side & 0xE) + 3) % 6];
        Vector3 v2 = Rotation.axes[((side & 0xE) + 5) % 6];
        float d1 = (float)vec.scalarProject(v1);
        float d2 = 1.0f - d1;
        float d3 = (float)vec.scalarProject(v2);
        float d4 = 1.0f - d3;
        return this.set(side, d2 * d4, d2 * d3, d1 * d4, d1 * d3);
    }

    @Override
    public LC copy() {
        return new LC(this.side, this.fa, this.fb, this.fc, this.fd);
    }
}

