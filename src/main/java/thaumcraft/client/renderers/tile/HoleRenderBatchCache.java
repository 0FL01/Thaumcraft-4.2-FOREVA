package thaumcraft.client.renderers.tile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileHole;

public final class HoleRenderBatchCache {
    private static final int MAX_GROUP_BLOCKS = 8192;

    private static final Map<BlockPos, HoleRenderGroup> GROUP_BY_MEMBER = new HashMap<BlockPos, HoleRenderGroup>();
    private static World cachedWorld;
    private static int cachedDimension = Integer.MIN_VALUE;
    private static long cachedWorldTime = Long.MIN_VALUE;
    private static int frameId;

    private HoleRenderBatchCache() {}

    public static void nextFrame() {
        frameId++;
        if (frameId == Integer.MAX_VALUE) {
            frameId = 0;
            GROUP_BY_MEMBER.clear();
        }
    }

    static HoleRenderGroup getGroup(TileHole tile) {
        if (tile == null || tile.getWorld() == null || tile.getPos() == null) {
            return null;
        }

        World world = tile.getWorld();
        refreshWorldCache(world);

        HoleRenderGroup group = GROUP_BY_MEMBER.get(tile.getPos());
        if (group != null) {
            return group;
        }

        return buildGroup(world, tile.getPos());
    }

    private static void refreshWorldCache(World world) {
        int dimension = world.provider == null ? 0 : world.provider.getDimension();
        long worldTime = world.getTotalWorldTime();
        if (world != cachedWorld || dimension != cachedDimension || worldTime != cachedWorldTime) {
            GROUP_BY_MEMBER.clear();
            cachedWorld = world;
            cachedDimension = dimension;
            cachedWorldTime = worldTime;
        }
    }

    private static HoleRenderGroup buildGroup(World world, BlockPos start) {
        if (!isHoleBlock(world, start)) {
            return null;
        }

        Set<BlockPos> members = new HashSet<BlockPos>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        BlockPos startImmutable = start.toImmutable();
        members.add(startImmutable);
        queue.add(startImmutable);

        while (!queue.isEmpty()) {
            if (members.size() > MAX_GROUP_BLOCKS) {
                return null;
            }
            BlockPos current = queue.removeFirst();
            for (EnumFacing face : EnumFacing.VALUES) {
                BlockPos next = current.offset(face);
                if (members.contains(next) || !isHoleBlock(world, next)) {
                    continue;
                }
                BlockPos nextImmutable = next.toImmutable();
                members.add(nextImmutable);
                queue.add(nextImmutable);
            }
        }

        HoleRenderGroup group = buildRenderGroup(world, members);
        for (BlockPos member : members) {
            GROUP_BY_MEMBER.put(member, group);
        }
        return group;
    }

    private static HoleRenderGroup buildRenderGroup(World world, Set<BlockPos> members) {
        Map<FacePlaneKey, Set<Long>> cellsByPlane = new HashMap<FacePlaneKey, Set<Long>>();
        int unitFaces = 0;

        for (BlockPos pos : members) {
            for (EnumFacing face : EnumFacing.VALUES) {
                if (!shouldRenderFace(world, pos.offset(face))) {
                    continue;
                }
                FacePlaneKey key = FacePlaneKey.from(face, pos);
                Set<Long> cells = cellsByPlane.get(key);
                if (cells == null) {
                    cells = new HashSet<Long>();
                    cellsByPlane.put(key, cells);
                }
                cells.add(pack(cellA(face, pos), cellB(face, pos)));
                unitFaces++;
            }
        }

        List<MergedFaceRect> rects = new ArrayList<MergedFaceRect>();
        for (Map.Entry<FacePlaneKey, Set<Long>> entry : cellsByPlane.entrySet()) {
            rects.addAll(mergeCells(entry.getKey(), entry.getValue()));
        }
        Collections.sort(rects, new Comparator<MergedFaceRect>() {
            @Override
            public int compare(MergedFaceRect left, MergedFaceRect right) {
                int c = Integer.compare(left.face.ordinal(), right.face.ordinal());
                if (c != 0) return c;
                c = Integer.compare(left.baseX, right.baseX);
                if (c != 0) return c;
                c = Integer.compare(left.baseY, right.baseY);
                if (c != 0) return c;
                c = Integer.compare(left.baseZ, right.baseZ);
                if (c != 0) return c;
                c = Integer.compare(left.sizeA, right.sizeA);
                if (c != 0) return c;
                return Integer.compare(left.sizeB, right.sizeB);
            }
        });

        return new HoleRenderGroup(rects, members.size(), unitFaces);
    }

    private static List<MergedFaceRect> mergeCells(FacePlaneKey key, Set<Long> input) {
        Set<Long> remaining = new HashSet<Long>(input);
        List<Long> sorted = new ArrayList<Long>(input);
        Collections.sort(sorted, new Comparator<Long>() {
            @Override
            public int compare(Long left, Long right) {
                int c = Integer.compare(unpackB(left.longValue()), unpackB(right.longValue()));
                if (c != 0) return c;
                return Integer.compare(unpackA(left.longValue()), unpackA(right.longValue()));
            }
        });

        List<MergedFaceRect> rects = new ArrayList<MergedFaceRect>();
        for (Long packed : sorted) {
            long value = packed.longValue();
            if (!remaining.contains(value)) {
                continue;
            }

            int startA = unpackA(value);
            int startB = unpackB(value);
            int width = 1;
            while (remaining.contains(pack(startA + width, startB))) {
                width++;
            }

            int height = 1;
            boolean canGrow = true;
            while (canGrow) {
                int nextB = startB + height;
                for (int da = 0; da < width; da++) {
                    if (!remaining.contains(pack(startA + da, nextB))) {
                        canGrow = false;
                        break;
                    }
                }
                if (canGrow) {
                    height++;
                }
            }

            for (int db = 0; db < height; db++) {
                for (int da = 0; da < width; da++) {
                    remaining.remove(pack(startA + da, startB + db));
                }
            }
            rects.add(createRect(key.face, key.plane, startA, startB, width, height));
        }
        return rects;
    }

    private static MergedFaceRect createRect(EnumFacing face, int plane, int startA, int startB, int width, int height) {
        switch (face) {
            case UP:
            case DOWN:
                return new MergedFaceRect(face, startA, plane, startB, width, height);
            case NORTH:
            case SOUTH:
                return new MergedFaceRect(face, startA, startB, plane, width, height);
            case WEST:
            case EAST:
            default:
                return new MergedFaceRect(face, plane, startB, startA, width, height);
        }
    }

    private static boolean isHoleBlock(World world, BlockPos pos) {
        return world != null
                && pos != null
                && world.isBlockLoaded(pos)
                && world.getBlockState(pos).getBlock() == ConfigBlocks.blockHole;
    }

    private static boolean shouldRenderFace(World world, BlockPos adjacent) {
        if (world == null || adjacent == null || !world.isBlockLoaded(adjacent)) {
            return false;
        }
        IBlockState adjacentState = world.getBlockState(adjacent);
        return adjacentState.isOpaqueCube() && adjacentState.getBlock() != ConfigBlocks.blockHole;
    }

    private static int cellA(EnumFacing face, BlockPos pos) {
        switch (face) {
            case WEST:
            case EAST:
                return pos.getZ();
            default:
                return pos.getX();
        }
    }

    private static int cellB(EnumFacing face, BlockPos pos) {
        switch (face) {
            case UP:
            case DOWN:
                return pos.getZ();
            default:
                return pos.getY();
        }
    }

    private static long pack(int a, int b) {
        return ((long) a << 32) ^ (b & 0xFFFFFFFFL);
    }

    private static int unpackA(long packed) {
        return (int) (packed >> 32);
    }

    private static int unpackB(long packed) {
        return (int) packed;
    }

    static final class HoleRenderGroup {
        final List<MergedFaceRect> rects;
        final int blockCount;
        final int unitFaceCount;
        private int renderedFrame = Integer.MIN_VALUE;

        private HoleRenderGroup(List<MergedFaceRect> rects, int blockCount, int unitFaceCount) {
            this.rects = rects;
            this.blockCount = blockCount;
            this.unitFaceCount = unitFaceCount;
        }

        boolean markRenderedThisFrame() {
            if (this.renderedFrame == frameId) {
                return false;
            }
            this.renderedFrame = frameId;
            return true;
        }
    }

    static final class MergedFaceRect {
        final EnumFacing face;
        final int baseX;
        final int baseY;
        final int baseZ;
        final int sizeA;
        final int sizeB;

        private MergedFaceRect(EnumFacing face, int baseX, int baseY, int baseZ, int sizeA, int sizeB) {
            this.face = face;
            this.baseX = baseX;
            this.baseY = baseY;
            this.baseZ = baseZ;
            this.sizeA = sizeA;
            this.sizeB = sizeB;
        }
    }

    private static final class FacePlaneKey {
        private final EnumFacing face;
        private final int plane;

        private FacePlaneKey(EnumFacing face, int plane) {
            this.face = face;
            this.plane = plane;
        }

        private static FacePlaneKey from(EnumFacing face, BlockPos pos) {
            int plane;
            switch (face) {
                case UP:
                case DOWN:
                    plane = pos.getY();
                    break;
                case NORTH:
                case SOUTH:
                    plane = pos.getZ();
                    break;
                case WEST:
                case EAST:
                default:
                    plane = pos.getX();
                    break;
            }
            return new FacePlaneKey(face, plane);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FacePlaneKey)) {
                return false;
            }
            FacePlaneKey other = (FacePlaneKey) obj;
            return this.face == other.face && this.plane == other.plane;
        }

        @Override
        public int hashCode() {
            return 31 * this.face.ordinal() + this.plane;
        }
    }
}
