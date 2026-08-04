package thaumcraft.client.renderers.block;

import net.minecraft.util.EnumFacing;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArcaneFurnaceNozzleGeometryTest {

    @Test
    public void nozzlePlanesMatchTc4ForEveryCoreDirection() {
        assertGeometry(EnumFacing.WEST, EnumFacing.EAST, 6.0F, 3.2F, 1.6F);
        assertGeometry(EnumFacing.EAST, EnumFacing.WEST, 10.0F, 12.8F, 14.4F);
        assertGeometry(EnumFacing.NORTH, EnumFacing.SOUTH, 6.0F, 3.2F, 1.6F);
        assertGeometry(EnumFacing.SOUTH, EnumFacing.NORTH, 10.0F, 12.8F, 14.4F);
    }

    private static void assertGeometry(EnumFacing coreFacing, EnumFacing outwardFace,
                                       float grateDepth, float grinDepth, float fireDepth) {
        List<ArcaneFurnaceBakedModel.NozzlePlane> planes =
                ArcaneFurnaceBakedModel.nozzleGeometry(coreFacing);

        assertEquals(3, planes.size());
        assertPlane(planes.get(0), 13, outwardFace, grateDepth, 16.0F);
        assertPlane(planes.get(1), 15, outwardFace, grinDepth, 16.0F);
        assertPlane(planes.get(2), ArcaneFurnaceBakedModel.NOZZLE_FIRE_TEXTURE,
                outwardFace, fireDepth, 24.0F);
    }

    private static void assertPlane(ArcaneFurnaceBakedModel.NozzlePlane plane, int textureIndex,
                                    EnumFacing outwardFace, float depth, float maxY) {
        assertEquals(textureIndex, plane.textureIndex);
        assertEquals(outwardFace, plane.outwardFace);
        assertEquals(depth, faceCoordinate(plane), 0.0001F);
        assertEquals(maxY, plane.maxY, 0.0001F);
    }

    private static float faceCoordinate(ArcaneFurnaceBakedModel.NozzlePlane plane) {
        switch (plane.outwardFace) {
            case EAST:
                return plane.maxX;
            case WEST:
                return plane.minX;
            case SOUTH:
                return plane.maxZ;
            case NORTH:
            default:
                return plane.minZ;
        }
    }
}
