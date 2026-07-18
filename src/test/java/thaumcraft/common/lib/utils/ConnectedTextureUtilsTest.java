package thaumcraft.common.lib.utils;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConnectedTextureUtilsTest {
    private static final BlockPos ORIGIN = BlockPos.ORIGIN;

    @Test
    public void tc4LookupShouldRetainRepresentativeFramesAndBounds() {
        assertEquals(0, ConnectedTextureUtils.getTextureIndex(0));
        assertEquals(6, ConnectedTextureUtils.getTextureIndex(2));
        assertEquals(46, ConnectedTextureUtils.getTextureIndex(90));
        assertEquals(11, ConnectedTextureUtils.getTextureIndex(255));
        assertEquals(0, ConnectedTextureUtils.getTextureIndex(-1));
        assertEquals(0, ConnectedTextureUtils.getTextureIndex(256));

        int max = 0;
        for (int mask = 0; mask < 256; mask++) {
            int texture = ConnectedTextureUtils.getTextureIndex(mask);
            assertTrue(texture >= 0 && texture < 47);
            max = Math.max(max, texture);
        }
        assertEquals(46, max);
    }

    @Test
    public void everyFaceShouldMapItsEightPlaneNeighborsToTheSameTc4Mask() {
        for (int side = 0; side < 6; side++) {
            BlockPos[] neighbors = neighbors(side);
            for (int mask = 0; mask < 256; mask++) {
                Set<BlockPos> connected = new HashSet<>();
                for (int bit = 0; bit < neighbors.length; bit++) {
                    if ((mask & 1 << bit) != 0) {
                        connected.add(neighbors[bit]);
                    }
                }
                assertEquals(ConnectedTextureUtils.getTextureIndex(mask),
                        ConnectedTextureUtils.getTextureIndex(ORIGIN, side, connected::contains));
            }
        }
    }

    private static BlockPos[] neighbors(int side) {
        if (side == 0 || side == 1) {
            return new BlockPos[]{
                    ORIGIN.add(-1, 0, -1), ORIGIN.add(0, 0, -1), ORIGIN.add(1, 0, -1),
                    ORIGIN.add(-1, 0, 0), ORIGIN.add(1, 0, 0),
                    ORIGIN.add(-1, 0, 1), ORIGIN.add(0, 0, 1), ORIGIN.add(1, 0, 1)
            };
        }
        if (side == 2 || side == 3) {
            int left = side == 2 ? 1 : -1;
            int right = -left;
            return new BlockPos[]{
                    ORIGIN.add(left, 1, 0), ORIGIN.add(0, 1, 0), ORIGIN.add(right, 1, 0),
                    ORIGIN.add(left, 0, 0), ORIGIN.add(right, 0, 0),
                    ORIGIN.add(left, -1, 0), ORIGIN.add(0, -1, 0), ORIGIN.add(right, -1, 0)
            };
        }
        int left = side == 5 ? 1 : -1;
        int right = -left;
        return new BlockPos[]{
                ORIGIN.add(0, 1, left), ORIGIN.add(0, 1, 0), ORIGIN.add(0, 1, right),
                ORIGIN.add(0, 0, left), ORIGIN.add(0, 0, right),
                ORIGIN.add(0, -1, left), ORIGIN.add(0, -1, 0), ORIGIN.add(0, -1, right)
        };
    }
}
