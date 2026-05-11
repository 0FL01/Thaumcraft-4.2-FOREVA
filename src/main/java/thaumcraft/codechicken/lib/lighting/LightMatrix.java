package thaumcraft.codechicken.lib.lighting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.codechicken.lib.colour.ColourRGBA;
import thaumcraft.codechicken.lib.lighting.LC;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.BlockCoord;

public class LightMatrix
implements CCRenderState.IVertexOperation {
    public static final int operationIndex = CCRenderState.registerOperation();
    public int computed = 0;
    public float[][] ao = new float[13][4];
    public int[][] brightness = new int[13][4];
    public IBlockAccess access;
    public BlockCoord pos = new BlockCoord();
    private int sampled = 0;
    private float[] aSamples = new float[27];
    private int[] bSamples = new int[27];
    public static final int[][] ssamplem = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8}, {18, 19, 20, 21, 22, 23, 24, 25, 26}, {0, 9, 18, 1, 10, 19, 2, 11, 20}, {6, 15, 24, 7, 16, 25, 8, 17, 26}, {0, 3, 6, 9, 12, 15, 18, 21, 24}, {2, 5, 8, 11, 14, 17, 20, 23, 26}, {9, 10, 11, 12, 13, 14, 15, 16, 17}, {9, 10, 11, 12, 13, 14, 15, 16, 17}, {3, 12, 21, 4, 13, 22, 5, 14, 23}, {3, 12, 21, 4, 13, 22, 5, 14, 23}, {1, 4, 7, 10, 13, 16, 19, 22, 25}, {1, 4, 7, 10, 13, 16, 19, 22, 25}, {13, 13, 13, 13, 13, 13, 13, 13, 13}};
    public static final int[][] qsamplem = new int[][]{{0, 1, 3, 4}, {5, 1, 2, 4}, {6, 7, 3, 4}, {5, 7, 8, 4}};
    public static final float[] sideao = new float[]{0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f, 0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f, 1.0f};

    public void locate(IBlockAccess a, int x, int y, int z) {
        this.access = a;
        this.pos.set(x, y, z);
        this.computed = 0;
        this.sampled = 0;
    }

    public void sample(int i) {
        if ((this.sampled & 1 << i) == 0) {
            int x = this.pos.x + i % 3 - 1;
            int y = this.pos.y + i / 9 - 1;
            int z = this.pos.z + i / 3 % 3 - 1;
            BlockPos bp = new BlockPos(x, y, z);
            IBlockState state = this.access.getBlockState(bp);
            Block b = state.getBlock();
            this.bSamples[i] = this.access.getCombinedLight(bp, state.getLightValue());
            this.aSamples[i] = state.getAmbientOcclusionLightValue();
        }
    }

    public int[] brightness(int side) {
        this.sideSample(side);
        return this.brightness[side];
    }

    public float[] ao(int side) {
        this.sideSample(side);
        return this.ao[side];
    }

    public void sideSample(int side) {
        if ((this.computed & 1 << side) == 0) {
            int[] ssample = ssamplem[side];
            for (int q = 0; q < 4; ++q) {
                int[] qsample = qsamplem[q];
                if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
                    this.interp(side, q, ssample[qsample[0]], ssample[qsample[1]], ssample[qsample[2]], ssample[qsample[3]]);
                    continue;
                }
                this.interp(side, q, ssample[4], ssample[4], ssample[4], ssample[4]);
            }
            this.computed |= 1 << side;
        }
    }

    private void interp(int s, int q, int a, int b, int c, int d) {
        this.sample(a);
        this.sample(b);
        this.sample(c);
        this.sample(d);
        this.ao[s][q] = LightMatrix.interpAO(this.aSamples[a], this.aSamples[b], this.aSamples[c], this.aSamples[d]) * sideao[s];
        this.brightness[s][q] = LightMatrix.interpBrightness(this.bSamples[a], this.bSamples[b], this.bSamples[c], this.bSamples[d]);
    }

    public static float interpAO(float a, float b, float c, float d) {
        return (a + b + c + d) / 4.0f;
    }

    public static int interpBrightness(int a, int b, int c, int d) {
        if (a == 0) {
            a = d;
        }
        if (b == 0) {
            b = d;
        }
        if (c == 0) {
            c = d;
        }
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    @Override
    public boolean load() {
        CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
        CCRenderState.pipeline.addDependency(CCRenderState.lightCoordAttrib);
        return true;
    }

    @Override
    public void operate() {
        LC lc = CCRenderState.lc;
        float[] a = this.ao(lc.side);
        float f = a[0] * lc.fa + a[1] * lc.fb + a[2] * lc.fc + a[3] * lc.fd;
        int[] b = this.brightness(lc.side);
        CCRenderState.setColour(ColourRGBA.multiplyC(CCRenderState.colour, f));
        CCRenderState.setBrightness((int)((float)b[0] * lc.fa + (float)b[1] * lc.fb + (float)b[2] * lc.fc + (float)b[3] * lc.fd) & 0xFF00FF);
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}
