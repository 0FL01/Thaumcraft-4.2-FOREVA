package thaumcraft.client.renderers.item;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** CPU-side counterpart of the renderer's use pose in the Rx(180)-corrected model basis. */
@SideOnly(Side.CLIENT)
public final class WandPoseMath {

    private WandPoseMath() {
    }

    public static Vec3d transformPoint(Vec3d point, Vec3d pivot, WandUsePose pose) {
        Vec3d transformed = point.subtract(pivot);

        // F*A*F^-1 keeps X angles and negates Z angles. GL post-multiplies calls, so points
        // observe the resulting rotations in reverse call order.
        transformed = rotateX(transformed, pose.waveRotateX);
        transformed = rotateZ(transformed, -pose.waveRotateZ);
        transformed = rotateX(transformed, pose.startupRotateX);
        transformed = rotateZ(transformed, -pose.contextRotateZ);
        transformed = rotateX(transformed, pose.contextRotateX);
        return transformed.add(pivot);
    }

    private static Vec3d rotateX(Vec3d vector, float degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Vec3d(
                vector.x,
                vector.y * cos - vector.z * sin,
                vector.y * sin + vector.z * cos);
    }

    private static Vec3d rotateZ(Vec3d vector, float degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Vec3d(
                vector.x * cos - vector.y * sin,
                vector.x * sin + vector.y * cos,
                vector.z);
    }
}
