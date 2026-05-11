package thaumcraft.api.visnet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;

public class VisNetHandler {
    public static HashMap<Integer, HashMap<WorldCoordinates, WeakReference<TileVisNode>>> sources = new HashMap();
    static ArrayList<WorldCoordinates> cache = new ArrayList();
    private static HashMap<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>> nearbyNodes = new HashMap();

    public static int drainVis(World world, int x, int y, int z, Aspect aspect, int amount) {
        ArrayList<WeakReference<TileVisNode>> nodes;
        int drainedAmount = 0;
        WorldCoordinates drainer = new WorldCoordinates(x, y, z, world.provider.getDimension());
        if (!nearbyNodes.containsKey(drainer)) {
            VisNetHandler.calculateNearbyNodes(world, x, y, z);
        }
        if ((nodes = nearbyNodes.get(drainer)) != null && nodes.size() > 0) {
            for (WeakReference<TileVisNode> noderef : nodes) {
                TileVisNode node = (TileVisNode)((Object)noderef.get());
                if (node == null) continue;
                int a = node.consumeVis(aspect, amount);
                drainedAmount += a;
                amount -= a;
                if (a > 0) {
                    int color = Aspect.getPrimalAspects().indexOf(aspect);
                    VisNetHandler.generateVisEffect(world.provider.getDimension(), x, y, z, node.getPos().getX(), node.getPos().getY(), node.getPos().getZ(), color);
                }
                if (amount > 0) continue;
                break;
            }
        }
        return drainedAmount;
    }

    public static void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
        ThaumcraftApi.internalMethods.generateVisEffect(dim, x, y, z, x2, y2, z2, color);
    }

    public static void addSource(World world, TileVisNode vs) {
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources.get(world.provider.getDimension());
        if (sourcelist == null) {
            sourcelist = new HashMap();
        }
        sourcelist.put(vs.getLocation(), new WeakReference<TileVisNode>(vs));
        sources.put(world.provider.getDimension(), sourcelist);
        nearbyNodes.clear();
    }

    public static boolean isNodeValid(WeakReference<TileVisNode> node) {
        return node != null && node.get() != null && !((TileVisNode)((Object)node.get())).isInvalid();
    }

    public static WeakReference<TileVisNode> addNode(World world, TileVisNode vn) {
        WeakReference<TileVisNode> ref = new WeakReference<TileVisNode>(vn);
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources.get(world.provider.getDimension());
        if (sourcelist == null) {
            sourcelist = new HashMap();
            return null;
        }
        ArrayList<Object[]> nearby = new ArrayList<Object[]>();
        for (WeakReference<TileVisNode> root : sourcelist.values()) {
            if (!VisNetHandler.isNodeValid(root)) continue;
            TileVisNode source = (TileVisNode)((Object)root.get());
            float r = VisNetHandler.inRange(world, vn.getLocation(), source.getLocation(), vn.getRange());
            if (r > 0.0f) {
                nearby.add(new Object[]{source, Float.valueOf(r - (float)(vn.getRange() * 2))});
            }
            nearby = VisNetHandler.findClosestNodes(vn, source, nearby);
            cache.clear();
        }
        float dist = Float.MAX_VALUE;
        TileVisNode closest = null;
        if (nearby.size() > 0) {
            for (Object[] o : nearby) {
                if (!(((Float)o[1]).floatValue() < dist) || vn.getAttunement() != -1 && ((TileVisNode)((Object)o[0])).getAttunement() != -1 && vn.getAttunement() != ((TileVisNode)((Object)o[0])).getAttunement() || !VisNetHandler.canNodeBeSeen(vn, (TileVisNode)((Object)o[0]))) continue;
                dist = ((Float)o[1]).floatValue();
                closest = (TileVisNode)((Object)o[0]);
            }
        }
        if (closest != null) {
            closest.getChildren().add(ref);
            nearbyNodes.clear();
            return new WeakReference<TileVisNode>(closest);
        }
        return null;
    }

    public static ArrayList<Object[]> findClosestNodes(TileVisNode target, TileVisNode parent, ArrayList<Object[]> in) {
        if (cache.size() > 512 || cache.contains(new WorldCoordinates(parent))) {
            return in;
        }
        cache.add(new WorldCoordinates(parent));
        for (WeakReference<TileVisNode> childWR : parent.getChildren()) {
            TileVisNode child = (TileVisNode)((Object)childWR.get());
            if (child == null || ((Object)((Object)child)).equals((Object)target) || ((Object)((Object)child)).equals((Object)parent)) continue;
            float r2 = VisNetHandler.inRange(child.getWorld(), child.getLocation(), target.getLocation(), target.getRange());
            if (r2 > 0.0f) {
                in.add(new Object[]{child, Float.valueOf(r2)});
            }
            in = VisNetHandler.findClosestNodes(target, child, in);
        }
        return in;
    }

    private static float inRange(World world, WorldCoordinates cc1, WorldCoordinates cc2, int range) {
        float distance = cc1.getDistanceSquaredToWorldCoordinates(cc2);
        return distance > (float)(range * range) ? -1.0f : distance;
    }

    private static void calculateNearbyNodes(World world, int x, int y, int z) {
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources.get(world.provider.getDimension());
        if (sourcelist == null) {
            sourcelist = new HashMap();
            return;
        }
        ArrayList<WeakReference<TileVisNode>> cn = new ArrayList<WeakReference<TileVisNode>>();
        WorldCoordinates drainer = new WorldCoordinates(x, y, z, world.provider.getDimension());
        ArrayList nearby = new ArrayList();
        for (WeakReference<TileVisNode> root : sourcelist.values()) {
            if (!VisNetHandler.isNodeValid(root)) continue;
            TileVisNode source = (TileVisNode)((Object)root.get());
            TileVisNode closest = null;
            float range = Float.MAX_VALUE;
            float r = VisNetHandler.inRange(world, drainer, source.getLocation(), source.getRange());
            if (r > 0.0f) {
                range = r;
                closest = source;
            }
            ArrayList<WeakReference<TileVisNode>> children = new ArrayList<WeakReference<TileVisNode>>();
            children = VisNetHandler.getAllChildren(source, children);
            for (WeakReference<TileVisNode> child : children) {
                float r2;
                TileVisNode n = (TileVisNode)((Object)child.get());
                if (n == null || ((Object)((Object)n)).equals(root) || !((r2 = VisNetHandler.inRange(n.getWorld(), n.getLocation(), drainer, n.getRange())) > 0.0f) || !(r2 < range)) continue;
                range = r2;
                closest = n;
            }
            if (closest == null) continue;
            cn.add(new WeakReference<TileVisNode>(closest));
        }
        nearbyNodes.put(drainer, cn);
    }

    private static ArrayList<WeakReference<TileVisNode>> getAllChildren(TileVisNode source, ArrayList<WeakReference<TileVisNode>> list) {
        for (WeakReference<TileVisNode> child : source.getChildren()) {
            TileVisNode n = (TileVisNode)((Object)child.get());
            if (n == null || n.getWorld() == null || !VisNetHandler.isChunkLoaded(n.getWorld(), n.getPos().getX(), n.getPos().getZ())) continue;
            list.add(child);
            list = VisNetHandler.getAllChildren(n, list);
        }
        return list;
    }

    public static boolean isChunkLoaded(World world, int x, int z) {
        return world.isBlockLoaded(new BlockPos(x, 0, z));
    }

    public static boolean canNodeBeSeen(TileVisNode source, TileVisNode target) {
        RayTraceResult mop = ThaumcraftApiHelper.rayTraceIgnoringSource(source.getWorld(), new Vec3d((double)((double)source.getPos().getX() + 0.5), (double)((double)source.getPos().getY() + 0.5), (double)((double)source.getPos().getZ() + 0.5)), new Vec3d((double)((double)target.getPos().getX() + 0.5), (double)((double)target.getPos().getY() + 0.5), (double)((double)target.getPos().getZ() + 0.5)), false, true, false);
        return mop == null || mop.typeOfHit == RayTraceResult.Type.BLOCK && mop.hitVec.x == target.getPos().getX() && mop.hitVec.y == target.getPos().getY() && mop.hitVec.z == target.getPos().getZ();
    }
}
