package thaumcraft.common.lib.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;

public class EssentiaHandler {
    private static final int DELAY = 5000;
    private static final Map<SourceKey, ArrayList<SourceKey>> SOURCES = new HashMap<SourceKey, ArrayList<SourceKey>>();
    private static final Map<SourceKey, Long> SOURCES_DELAY = new HashMap<SourceKey, Long>();
    public static final Map<String, EssentiaSourceFX> sourceFX = new HashMap<String, EssentiaSourceFX>();

    public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range) {
        return drainEssentia(tile, aspect, direction, range, false);
    }

    public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror) {
        if (tile == null || tile.getWorld() == null || aspect == null || range <= 0) return false;
        SourceKey tileLoc = new SourceKey(tile);
        if (!SOURCES.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            if (!SOURCES.containsKey(tileLoc)) return false;
        }

        ArrayList<SourceKey> sources = SOURCES.get(tileLoc);
        Iterator<SourceKey> iterator = sources.iterator();
        while (iterator.hasNext()) {
            SourceKey source = iterator.next();
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (!(sourceTile instanceof IAspectSource)) {
                iterator.remove();
                continue;
            }
            IAspectSource aspectSource = (IAspectSource) sourceTile;
            if (!aspectSource.takeFromContainer(aspect, 1)) continue;
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXEssentiaSource(
                            tile.getPos().getX(),
                            tile.getPos().getY(),
                            tile.getPos().getZ(),
                            (byte) (tile.getPos().getX() - source.pos.getX()),
                            (byte) (tile.getPos().getY() - source.pos.getY()),
                            (byte) (tile.getPos().getZ() - source.pos.getZ()),
                            aspect.getColor()),
                    new NetworkRegistry.TargetPoint(
                            tile.getWorld().provider.getDimension(),
                            tile.getPos().getX(),
                            tile.getPos().getY(),
                            tile.getPos().getZ(),
                            32.0));
            return true;
        }

        SOURCES.remove(tileLoc);
        SOURCES_DELAY.put(tileLoc, System.currentTimeMillis() + DELAY);
        return false;
    }

    public static boolean findEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range) {
        if (tile == null || tile.getWorld() == null || aspect == null || range <= 0) return false;
        SourceKey tileLoc = new SourceKey(tile);
        if (!SOURCES.containsKey(tileLoc)) {
            getSources(tile.getWorld(), tileLoc, direction, range);
            if (!SOURCES.containsKey(tileLoc)) return false;
        }

        ArrayList<SourceKey> sources = SOURCES.get(tileLoc);
        Iterator<SourceKey> iterator = sources.iterator();
        while (iterator.hasNext()) {
            SourceKey source = iterator.next();
            TileEntity sourceTile = tile.getWorld().getTileEntity(source.pos);
            if (!(sourceTile instanceof IAspectSource)) {
                iterator.remove();
                continue;
            }
            if (((IAspectSource) sourceTile).doesContainerContainAmount(aspect, 1)) return true;
        }

        SOURCES.remove(tileLoc);
        SOURCES_DELAY.put(tileLoc, System.currentTimeMillis() + DELAY);
        return false;
    }

    private static void getSources(World world, SourceKey tileLoc, EnumFacing direction, int range) {
        Long delay = SOURCES_DELAY.get(tileLoc);
        if (delay != null) {
            if (delay > System.currentTimeMillis()) return;
            SOURCES_DELAY.remove(tileLoc);
        }

        ArrayList<SourceKey> sourceList = new ArrayList<SourceKey>();
        List<BlockPos> scan = buildScan(tileLoc.pos, direction, range);
        for (BlockPos pos : scan) {
            if (pos.equals(tileLoc.pos)) continue;
            if (!world.isBlockLoaded(pos)) continue;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof IAspectSource) {
                sourceList.add(new SourceKey(pos, world.provider.getDimension()));
            }
        }

        if (sourceList.isEmpty()) {
            SOURCES_DELAY.put(tileLoc, System.currentTimeMillis() + DELAY);
        } else {
            SOURCES.put(tileLoc, sourceList);
        }
    }

    private static List<BlockPos> buildScan(BlockPos origin, EnumFacing direction, int range) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        if (direction == null) {
            for (int x = -range; x <= range; ++x) {
                for (int y = -range; y <= range; ++y) {
                    for (int z = -range; z <= range; ++z) {
                        out.add(origin.add(x, y, z));
                    }
                }
            }
            return out;
        }

        for (int a = -range; a <= range; ++a) {
            for (int b = -range; b <= range; ++b) {
                for (int c = 0; c < range; ++c) {
                    if (direction.getYOffset() != 0) {
                        out.add(origin.add(a, c * direction.getYOffset(), b));
                    } else if (direction.getXOffset() != 0) {
                        out.add(origin.add(c * direction.getXOffset(), a, b));
                    } else {
                        out.add(origin.add(a, b, c * direction.getZOffset()));
                    }
                }
            }
        }
        return out;
    }

    public static void refreshSources(TileEntity tile) {
        if (tile != null) SOURCES.remove(new SourceKey(tile));
    }

    private static final class SourceKey {
        private final BlockPos pos;
        private final int dim;

        private SourceKey(TileEntity tile) {
            this(tile.getPos(), tile.getWorld().provider.getDimension());
        }

        private SourceKey(BlockPos pos, int dim) {
            this.pos = pos.toImmutable();
            this.dim = dim;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof SourceKey)) return false;
            SourceKey key = (SourceKey) other;
            return this.dim == key.dim && this.pos.equals(key.pos);
        }

        @Override
        public int hashCode() {
            return 31 * this.pos.hashCode() + this.dim;
        }
    }

    public static final class EssentiaSourceFX {
        public final BlockPos start;
        public final BlockPos end;
        public int ticks;
        public final int color;

        public EssentiaSourceFX(BlockPos start, BlockPos end, int ticks, int color) {
            this.start = start.toImmutable();
            this.end = end.toImmutable();
            this.ticks = ticks;
            this.color = color;
        }
    }
}
