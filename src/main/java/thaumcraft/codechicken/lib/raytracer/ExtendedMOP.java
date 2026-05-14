package thaumcraft.codechicken.lib.raytracer;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ExtendedMOP
extends RayTraceResult
implements Comparable<ExtendedMOP> {
    public Object data;
    public double dist;

    public ExtendedMOP(Entity entity, Object data) {
        super(entity);
        this.setData(data);
    }

    public ExtendedMOP(int x, int y, int z, int side, Vec3d hit, Object data) {
        super(RayTraceResult.Type.BLOCK, hit, EnumFacing.byIndex(side), new BlockPos(x, y, z));
        this.setData(data);
    }

    public ExtendedMOP(RayTraceResult mop, Object data, double dist) {
        super(mop.typeOfHit, copyHitVec(mop), mop.sideHit, mop.getBlockPos());
        this.entityHit = mop.entityHit;
        this.subHit = mop.subHit;
        this.setData(data);
        this.dist = dist;
    }

    private static Vec3d copyHitVec(RayTraceResult mop) {
        return mop.hitVec == null ? null : new Vec3d(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z);
    }

    public void setData(Object data) {
        if (data instanceof Integer) {
            this.subHit = (Integer)data;
        }
        this.data = data;
    }

    public static <T> T getData(RayTraceResult mop) {
        if (mop instanceof ExtendedMOP) {
            return (T)((ExtendedMOP)mop).data;
        }
        return (T)Integer.valueOf(mop.subHit);
    }

    @Override
    public int compareTo(ExtendedMOP o) {
        return this.dist == o.dist ? 0 : (this.dist < o.dist ? -1 : 1);
    }
}
