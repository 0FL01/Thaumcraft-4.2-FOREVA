package thaumcraft.api.visnet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;

public class VisNetHandler {

    // Dimension -> (node position -> node reference)
    public static HashMap<Integer, HashMap<WorldCoordinates, WeakReference<TileVisNode>>> sources = new HashMap<>();

    static ArrayList<WorldCoordinates> cache = new ArrayList<>();
    private static HashMap<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>> nearbyNodes = new HashMap<>();

    // Tick counter for source reference cleanup.
    private static int tickCounter = 0;

    /**
     * World tick handler for stale source cleanup. Natural aura-node recharge is owned by TileNode.update().
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) return;
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter % 100 != 0) return;

        HashMap<WorldCoordinates, WeakReference<TileVisNode>> dimSources = sources.get(event.world.provider.getDimension());
        if (dimSources == null) return;
        cleanInvalidReferences(event.world);
    }

    private void cleanInvalidReferences(World world) {
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> dimSources = sources.get(world.provider.getDimension());
        if (dimSources == null) return;

        Iterator<WeakReference<TileVisNode>> it = dimSources.values().iterator();
        while (it.hasNext()) {
            if (!isNodeValid(it.next())) {
                it.remove();
            }
        }
    }

    public static int drainVis(World world, int x, int y, int z, Aspect aspect, int amount) {
        ArrayList<WeakReference<TileVisNode>> nodes;
        int drainedAmount = 0;
        WorldCoordinates drainer = new WorldCoordinates(x, y, z, world.provider.getDimension());
        if (!nearbyNodes.containsKey(drainer)) {
            VisNetHandler.calculateNearbyNodes(world, x, y, z);
        }
        if ((nodes = nearbyNodes.get(drainer)) != null && nodes.size() > 0) {
            for (WeakReference<TileVisNode> noderef : nodes) {
                TileVisNode node = noderef.get();
                if (node == null) continue;
                int a = node.consumeVis(aspect, amount);
                drainedAmount += a;
                amount -= a;
                if (a > 0) {
                    int color = Aspect.getPrimalAspects().indexOf(aspect);
                    if (color >= 0) {
                        VisNetHandler.generateVisEffect(world.provider.getDimension(), x, y, z,
                                node.getPos().getX(), node.getPos().getY(), node.getPos().getZ(), color);
                    }
                }
                if (amount <= 0) break;
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
            sourcelist = new HashMap<>();
        }
        sourcelist.put(vs.getLocation(), new WeakReference<>(vs));
        sources.put(world.provider.getDimension(), sourcelist);
        nearbyNodes.clear();
    }

    public static boolean isNodeValid(WeakReference<TileVisNode> node) {
        return node != null && node.get() != null && !node.get().isInvalid();
    }

    public static WeakReference<TileVisNode> addNode(World world, TileVisNode vn) {
        WeakReference<TileVisNode> ref = new WeakReference<>(vn);
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources.get(world.provider.getDimension());
        if (sourcelist == null) {
            sourcelist = new HashMap<>();
            return null;
        }
        ArrayList<Object[]> nearby = new ArrayList<>();
        for (WeakReference<TileVisNode> root : sourcelist.values()) {
            if (!VisNetHandler.isNodeValid(root)) continue;
            TileVisNode source = root.get();
            float r = VisNetHandler.inRange(world, vn.getLocation(), source.getLocation(), vn.getRange());
            if (r > 0.0f) {
                nearby.add(new Object[]{source, r - (float) (vn.getRange() * 2)});
            }
            nearby = VisNetHandler.findClosestNodes(vn, source, nearby);
            cache.clear();
        }
        float dist = Float.MAX_VALUE;
        TileVisNode closest = null;
        if (nearby.size() > 0) {
            for (Object[] o : nearby) {
                float d = (Float) o[1];
                TileVisNode candidate = (TileVisNode) o[0];
                if (d < dist
                        && (vn.getAttunement() == -1 || candidate.getAttunement() == -1
                        || vn.getAttunement() == candidate.getAttunement())
                        && VisNetHandler.canNodeBeSeen(vn, candidate)) {
                    dist = d;
                    closest = candidate;
                }
            }
        }
        if (closest != null) {
            closest.getChildren().add(ref);
            nearbyNodes.clear();
            return new WeakReference<>(closest);
        }
        return null;
    }

    public static ArrayList<Object[]> findClosestNodes(TileVisNode target, TileVisNode parent, ArrayList<Object[]> in) {
        if (cache.size() > 512 || cache.contains(new WorldCoordinates(parent))) {
            return in;
        }
        cache.add(new WorldCoordinates(parent));
        for (WeakReference<TileVisNode> childWR : parent.getChildren()) {
            TileVisNode child = childWR.get();
            if (child == null || child.equals(target) || child.equals(parent)) continue;
            float r2 = VisNetHandler.inRange(child.getWorld(), child.getLocation(), target.getLocation(), target.getRange());
            if (r2 > 0.0f) {
                in.add(new Object[]{child, r2});
            }
            in = VisNetHandler.findClosestNodes(target, child, in);
        }
        return in;
    }

    private static float inRange(World world, WorldCoordinates cc1, WorldCoordinates cc2, int range) {
        float distance = cc1.getDistanceSquaredToWorldCoordinates(cc2);
        return distance > (float) (range * range) ? -1.0f : distance;
    }

    private static void calculateNearbyNodes(World world, int x, int y, int z) {
        HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = sources.get(world.provider.getDimension());
        if (sourcelist == null) {
            sourcelist = new HashMap<>();
            return;
        }
        ArrayList<WeakReference<TileVisNode>> cn = new ArrayList<>();
        WorldCoordinates drainer = new WorldCoordinates(x, y, z, world.provider.getDimension());
        for (WeakReference<TileVisNode> root : sourcelist.values()) {
            if (!VisNetHandler.isNodeValid(root)) continue;
            TileVisNode source = root.get();
            float range = Float.MAX_VALUE;
            TileVisNode closest = null;
            float r = VisNetHandler.inRange(world, drainer, source.getLocation(), source.getRange());
            if (r > 0.0f) {
                range = r;
                closest = source;
            }
            ArrayList<WeakReference<TileVisNode>> children = new ArrayList<>();
            children = VisNetHandler.getAllChildren(source, children);
            for (WeakReference<TileVisNode> child : children) {
                TileVisNode n = child.get();
                if (n == null || n.equals(root)) continue;
                float r2 = VisNetHandler.inRange(n.getWorld(), n.getLocation(), drainer, n.getRange());
                if (r2 > 0.0f && r2 < range) {
                    range = r2;
                    closest = n;
                }
            }
            if (closest != null) {
                cn.add(new WeakReference<>(closest));
            }
        }
        nearbyNodes.put(drainer, cn);
    }

    private static ArrayList<WeakReference<TileVisNode>> getAllChildren(TileVisNode source, ArrayList<WeakReference<TileVisNode>> list) {
        for (WeakReference<TileVisNode> child : source.getChildren()) {
            TileVisNode n = child.get();
            if (n == null || n.getWorld() == null || !VisNetHandler.isChunkLoaded(n.getWorld(), n.getPos().getX(), n.getPos().getZ()))
                continue;
            list.add(child);
            list = VisNetHandler.getAllChildren(n, list);
        }
        return list;
    }

    public static boolean isChunkLoaded(World world, int x, int z) {
        return world.isBlockLoaded(new BlockPos(x, 0, z));
    }

    public static boolean canNodeBeSeen(TileVisNode source, TileVisNode target) {
        RayTraceResult mop = ThaumcraftApiHelper.rayTraceIgnoringSource(
                source.getWorld(),
                new Vec3d(source.getPos().getX() + 0.5, source.getPos().getY() + 0.5, source.getPos().getZ() + 0.5),
                new Vec3d(target.getPos().getX() + 0.5, target.getPos().getY() + 0.5, target.getPos().getZ() + 0.5),
                false, true, false);
        return mop == null || (mop.typeOfHit == RayTraceResult.Type.BLOCK
                && target.getPos().equals(mop.getBlockPos()));
    }
}
