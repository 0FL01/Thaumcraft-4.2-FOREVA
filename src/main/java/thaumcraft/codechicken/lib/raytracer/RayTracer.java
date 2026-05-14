package thaumcraft.codechicken.lib.raytracer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.codechicken.lib.math.MathHelper;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;

public class RayTracer {
    private Vector3 vec = new Vector3();
    private Vector3 vec2 = new Vector3();
    private Vector3 s_vec = new Vector3();
    private double s_dist;
    private int s_side;
    private IndexedCuboid6 c_cuboid;
    private static ThreadLocal<RayTracer> t_inst = new ThreadLocal();

    public static RayTracer instance() {
        RayTracer inst = t_inst.get();
        if (inst == null) {
            inst = new RayTracer();
            t_inst.set(inst);
        }
        return inst;
    }

    private void traceSide(int side, Vector3 start, Vector3 end, Cuboid6 cuboid) {
        this.vec.set(start);
        Vector3 hit = null;
        switch (side) {
            case 0: {
                hit = this.vec.XZintercept(end, cuboid.min.y);
                break;
            }
            case 1: {
                hit = this.vec.XZintercept(end, cuboid.max.y);
                break;
            }
            case 2: {
                hit = this.vec.XYintercept(end, cuboid.min.z);
                break;
            }
            case 3: {
                hit = this.vec.XYintercept(end, cuboid.max.z);
                break;
            }
            case 4: {
                hit = this.vec.YZintercept(end, cuboid.min.x);
                break;
            }
            case 5: {
                hit = this.vec.YZintercept(end, cuboid.max.x);
            }
        }
        if (hit == null) {
            return;
        }
        switch (side) {
            case 0: 
            case 1: {
                if (MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) && MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) break;
                return;
            }
            case 2: 
            case 3: {
                if (MathHelper.between(cuboid.min.x, hit.x, cuboid.max.x) && MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y)) break;
                return;
            }
            case 4: 
            case 5: {
                if (MathHelper.between(cuboid.min.y, hit.y, cuboid.max.y) && MathHelper.between(cuboid.min.z, hit.z, cuboid.max.z)) break;
                return;
            }
        }
        double dist = this.vec2.set(hit).subtract(start).magSquared();
        if (dist < this.s_dist) {
            this.s_side = side;
            this.s_dist = dist;
            this.s_vec.set(this.vec);
        }
    }

    public RayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid) {
        this.s_dist = Double.MAX_VALUE;
        this.s_side = -1;
        for (int i = 0; i < 6; ++i) {
            this.traceSide(i, start, end, cuboid);
        }
        if (this.s_side < 0) {
            return null;
        }
        RayTraceResult mop = new RayTraceResult(this.s_vec.toVec3D(), EnumFacing.byIndex(this.s_side));
        mop.typeOfHit = RayTraceResult.Type.MISS;
        return mop;
    }

    public RayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, BlockCoord pos) {
        RayTraceResult mop = this.rayTraceCuboid(start, end, cuboid);
        if (mop != null) {
            mop = RayTracer.withBlockPos(mop, pos);
        }
        return mop;
    }

    public RayTraceResult rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, Entity e) {
        RayTraceResult mop = this.rayTraceCuboid(start, end, cuboid);
        if (mop != null) {
            mop.typeOfHit = RayTraceResult.Type.ENTITY;
            mop.entityHit = e;
        }
        return mop;
    }

    public RayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids) {
        double c_dist = Double.MAX_VALUE;
        RayTraceResult c_hit = null;
        for (IndexedCuboid6 cuboid : cuboids) {
            RayTraceResult mop = this.rayTraceCuboid(start, end, cuboid);
            if (mop == null || !(this.s_dist < c_dist)) continue;
            mop = new ExtendedMOP(mop, cuboid.data, this.s_dist);
            c_dist = this.s_dist;
            c_hit = mop;
            this.c_cuboid = cuboid;
        }
        return c_hit;
    }

    public RayTraceResult rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockCoord pos, Block block) {
        RayTraceResult mop = this.rayTraceCuboids(start, end, cuboids);
        if (mop != null) {
            mop = RayTracer.withBlockPos(mop, pos);
            if (block != null) {
                this.c_cuboid.add(new Vector3(-pos.x, -pos.y, -pos.z)).setBlockBounds(block);
            }
        }
        return mop;
    }

    public void rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockCoord pos, Block block, List<ExtendedMOP> hitList) {
        for (IndexedCuboid6 cuboid : cuboids) {
            RayTraceResult mop = this.rayTraceCuboid(start, end, cuboid);
            if (mop == null) continue;
            ExtendedMOP emop = new ExtendedMOP(RayTracer.withBlockPos(mop, pos), cuboid.data, this.s_dist);
            hitList.add(emop);
        }
    }

    private static RayTraceResult withBlockPos(RayTraceResult mop, BlockCoord pos) {
        RayTraceResult blockMop = new RayTraceResult(RayTraceResult.Type.BLOCK, mop.hitVec, mop.sideHit, new BlockPos(pos.x, pos.y, pos.z));
        blockMop.entityHit = mop.entityHit;
        blockMop.subHit = mop.subHit;
        if (mop instanceof ExtendedMOP) {
            ExtendedMOP extended = (ExtendedMOP)mop;
            return new ExtendedMOP(blockMop, extended.data, extended.dist);
        }
        return blockMop;
    }

    public static RayTraceResult retraceBlock(World world, EntityPlayer player, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Vec3d headVec = RayTracer.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        double reach = RayTracer.getBlockReachDistance(player);
        Vec3d endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return block.collisionRayTrace(state, world, pos, headVec, endVec);
    }

    private static double getBlockReachDistance_server(EntityPlayerMP player) {
        return player.interactionManager.getBlockReachDistance();
    }

    @SideOnly(value=Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }

    public static RayTraceResult reTrace(World world, EntityPlayer player) {
        return RayTracer.reTrace(world, player, RayTracer.getBlockReachDistance(player));
    }

    public static RayTraceResult reTrace(World world, EntityPlayer player, double reach) {
        Vec3d headVec = RayTracer.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        Vec3d endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return world.rayTraceBlocks(headVec, endVec, true, false, true);
    }

    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {
        Vec3d v = new Vec3d(player.posX, player.posY, player.posZ);
        if (player.world.isRemote) {
            v = v.add(0.0, (double)player.getEyeHeight(), 0.0);
        } else {
            v = v.add(0.0, (double)player.getEyeHeight(), 0.0);
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                v = v.add(0.0, -0.08, 0.0);
            }
        }
        return v;
    }

    public static Vec3d getStartVec(EntityPlayer player) {
        return RayTracer.getCorrectedHeadVec(player);
    }

    public static double getBlockReachDistance(EntityPlayer player) {
        return player.world.isRemote ? RayTracer.getBlockReachDistance_client() : (player instanceof EntityPlayerMP ? RayTracer.getBlockReachDistance_server((EntityPlayerMP)player) : 5.0);
    }

    public static Vec3d getEndVec(EntityPlayer player) {
        Vec3d headVec = RayTracer.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0f);
        double reach = RayTracer.getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }
}
