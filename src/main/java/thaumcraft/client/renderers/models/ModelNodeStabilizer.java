package thaumcraft.client.renderers.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ModelNodeStabilizer {
    private static final float[][] VERTICES = {
            {-0.5000F, -0.5000F, 0.0000F},
            {-0.5000F, 0.5000F, 0.0000F},
            {0.5000F, 0.5000F, 0.0000F},
            {0.5000F, -0.5000F, 0.0000F},
            {-0.2500F, -0.2500F, 0.7500F},
            {0.2500F, -0.2500F, 0.7500F},
            {0.2500F, 0.2500F, 0.7500F},
            {-0.2500F, 0.2500F, 0.7500F},
            {0.5000F, -0.5000F, 0.2500F},
            {-0.5000F, -0.5000F, 0.2500F},
            {0.5000F, 0.5000F, 0.2500F},
            {-0.5000F, 0.5000F, 0.2500F},
            {0.2500F, -0.2500F, 0.5000F},
            {-0.2500F, -0.2500F, 0.5000F},
            {0.2500F, 0.2500F, 0.5000F},
            {-0.2500F, 0.2500F, 0.5000F},
            {-0.1000F, -0.1500F, 0.1250F},
            {-0.1000F, 0.1500F, 0.1250F},
            {0.1000F, 0.1500F, 0.1250F},
            {0.1000F, -0.1500F, 0.1250F},
            {-0.1000F, -0.1500F, 0.6250F},
            {0.1000F, -0.1500F, 0.6250F},
            {0.1000F, 0.1500F, 0.6250F},
            {-0.1000F, 0.1500F, 0.6250F},
    };

    private static final float[][] UVS = {
            {0.0000F, 0.5000F},
            {0.0000F, 0.0000F},
            {0.5000F, 0.0000F},
            {0.5000F, 0.5000F},
            {0.0000F, 0.7500F},
            {0.2500F, 0.7500F},
            {0.2500F, 1.0000F},
            {0.0000F, 1.0000F},
            {1.0000F, 0.5000F},
            {1.0000F, 0.6250F},
            {0.5000F, 0.6250F},
            {0.8750F, 0.8750F},
            {0.6250F, 0.8750F},
            {0.8750F, 1.0000F},
            {0.6250F, 1.0000F},
            {0.9375F, 0.4688F},
            {0.7500F, 0.4688F},
            {0.7500F, 0.3457F},
            {0.9375F, 0.3457F},
            {0.5950F, 0.0625F},
            {0.7175F, 0.0625F},
            {0.7170F, 0.3130F},
            {0.5950F, 0.3130F},
            {0.7500F, 0.0625F},
            {0.9375F, 0.0625F},
            {0.9375F, 0.3125F},
            {0.7500F, 0.3125F},
    };

    private static final float[][] NORMALS = {
            {0.0000F, 0.0000F, -1.0000F},
            {0.0000F, 0.0000F, 1.0000F},
            {0.0000F, -1.0000F, 0.0000F},
            {1.0000F, 0.0000F, 0.0000F},
            {0.0000F, 1.0000F, 0.0000F},
            {-1.0000F, 0.0000F, 0.0000F},
            {0.0000F, -0.7071F, 0.7071F},
            {0.7071F, 0.0000F, 0.7071F},
            {0.0000F, 0.7071F, 0.7071F},
            {-0.7071F, 0.0000F, 0.7071F},
            {-0.0000F, 0.0000F, -1.0000F},
            {0.0000F, 0.0000F, 1.0000F},
            {0.0000F, -1.0000F, 0.0000F},
            {1.0000F, 0.0000F, -0.0000F},
            {0.0000F, 1.0000F, 0.0000F},
            {-1.0000F, 0.0000F, 0.0000F},
    };

    private static final int[][] LOCK_TRIANGLES = {
            {1, 1, 1, 2, 2, 1, 3, 3, 1},
            {3, 3, 1, 4, 4, 1, 1, 1, 1},
            {5, 5, 2, 6, 6, 2, 7, 7, 2},
            {7, 7, 2, 8, 8, 2, 5, 5, 2},
            {1, 4, 3, 4, 9, 3, 9, 10, 3},
            {9, 10, 3, 10, 11, 3, 1, 4, 3},
            {4, 4, 4, 3, 9, 4, 11, 10, 4},
            {11, 10, 4, 9, 11, 4, 4, 4, 4},
            {3, 4, 5, 2, 9, 5, 12, 10, 5},
            {12, 10, 5, 11, 11, 5, 3, 4, 5},
            {2, 4, 6, 1, 9, 6, 10, 10, 6},
            {10, 10, 6, 12, 11, 6, 2, 4, 6},
            {10, 11, 7, 9, 10, 7, 13, 12, 7},
            {13, 12, 7, 14, 13, 7, 10, 11, 7},
            {9, 11, 8, 11, 10, 8, 15, 12, 8},
            {15, 12, 8, 13, 13, 8, 9, 11, 8},
            {11, 11, 9, 12, 10, 9, 16, 12, 9},
            {16, 12, 9, 15, 13, 9, 11, 11, 9},
            {12, 11, 10, 10, 10, 10, 14, 12, 10},
            {14, 12, 10, 16, 13, 10, 12, 11, 10},
            {14, 13, 3, 13, 12, 3, 6, 14, 3},
            {6, 14, 3, 5, 15, 3, 14, 13, 3},
            {13, 13, 4, 15, 12, 4, 7, 14, 4},
            {7, 14, 4, 6, 15, 4, 13, 13, 4},
            {15, 13, 5, 16, 12, 5, 8, 14, 5},
            {8, 14, 5, 7, 15, 5, 15, 13, 5},
            {16, 13, 6, 14, 12, 6, 5, 14, 6},
            {5, 14, 6, 8, 15, 6, 16, 13, 6},
    };

    private static final int[][] PISTON_TRIANGLES = {
            {17, 16, 11, 18, 17, 11, 19, 18, 11},
            {19, 18, 11, 20, 19, 11, 17, 16, 11},
            {21, 17, 12, 22, 18, 12, 23, 19, 12},
            {23, 19, 12, 24, 16, 12, 21, 17, 12},
            {17, 20, 13, 20, 21, 13, 22, 22, 13},
            {22, 22, 13, 21, 23, 13, 17, 20, 13},
            {20, 24, 14, 19, 25, 14, 23, 26, 14},
            {23, 26, 14, 22, 27, 14, 20, 24, 14},
            {19, 20, 15, 18, 21, 15, 24, 22, 15},
            {24, 22, 15, 23, 23, 15, 19, 20, 15},
            {18, 24, 16, 17, 25, 16, 21, 26, 16},
            {21, 26, 16, 24, 27, 16, 18, 24, 16},
    };

    // Wavefront node_stabilizer.obj groups from the original 1.7.10 renderer asset.

    public void renderLock(float scale) {
        renderGroup(LOCK_TRIANGLES);
    }

    public void renderPiston(float scale) {
        renderGroup(PISTON_TRIANGLES);
    }

    private static void renderGroup(int[][] triangles) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int[] triangle : triangles) {
            addVertex(buf, triangle, 0);
            addVertex(buf, triangle, 3);
            addVertex(buf, triangle, 6);
        }
        tess.draw();
    }

    private static void addVertex(BufferBuilder buf, int[] triangle, int offset) {
        float[] pos = VERTICES[triangle[offset] - 1];
        float[] uv = UVS[triangle[offset + 1] - 1];
        float[] normal = NORMALS[triangle[offset + 2] - 1];
        buf.pos(pos[0], pos[1], pos[2]).tex(uv[0], 1.0F - uv[1]).normal(normal[0], normal[1], normal[2]).endVertex();
    }
}
