package thaumcraft.codechicken.lib.render;

import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.codechicken.lib.colour.ColourRGBA;
import thaumcraft.codechicken.lib.lighting.LC;
import thaumcraft.codechicken.lib.lighting.LightMatrix;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderPipeline;
import thaumcraft.codechicken.lib.render.Vertex5;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class CCRenderState {
    private static int nextOperationIndex;
    private static ArrayList<VertexAttribute<?>> vertexAttributes;
    public static VertexAttribute<Vector3[]> normalAttrib;
    public static VertexAttribute<int[]> colourAttrib;
    public static VertexAttribute<int[]> sideAttrib;
    public static VertexAttribute<LC[]> lightCoordAttrib;
    public static IVertexSource model;
    public static int firstVertexIndex;
    public static int lastVertexIndex;
    public static int vertexIndex;
    public static CCRenderPipeline pipeline;
    public static int baseColour;
    public static int alphaOverride;
    public static boolean useNormals;
    public static LightMatrix lightMatrix;
    public static Vertex5 vert;
    public static boolean hasNormal;
    public static Vector3 normal;
    public static boolean hasColour;
    public static int colour;
    public static boolean hasBrightness;
    public static int brightness;
    public static int side;
    public static LC lc;
    private static VertexFormat currentVertexFormat;

    public static int registerOperation() {
        return nextOperationIndex++;
    }

    public static int operationCount() {
        return nextOperationIndex;
    }

    private static int registerVertexAttribute(VertexAttribute<?> attr) {
        vertexAttributes.add(attr);
        return vertexAttributes.size() - 1;
    }

    public static VertexAttribute<?> getAttribute(int index) {
        return vertexAttributes.get(index);
    }

    public static void arrayCopy(Object src, int srcPos, Object dst, int destPos, int length) {
        System.arraycopy(src, srcPos, dst, destPos, length);
        if (dst instanceof Copyable[]) {
            Object[] oa = (Object[])dst;
            Copyable[] c = (Copyable[])dst;
            for (int i = destPos; i < destPos + length; ++i) {
                if (c[i] == null) continue;
                oa[i] = c[i].copy();
            }
        }
    }

    public static <T> T copyOf(VertexAttribute<T> attr, T src, int length) {
        T dst = attr.newArray(length);
        CCRenderState.arrayCopy(src, 0, dst, 0, length);
        return dst;
    }

    public static void reset() {
        model = null;
        pipeline.reset();
        hasBrightness = false;
        hasColour = false;
        hasNormal = false;
        alphaOverride = -1;
        baseColour = -1;
        currentVertexFormat = null;
    }

    public static void setPipeline(IVertexOperation ... ops) {
        pipeline.setPipeline(ops);
    }

    public static void setPipeline(IVertexSource model, int start, int end, IVertexOperation ... ops) {
        pipeline.reset();
        CCRenderState.setModel(model, start, end);
        pipeline.setPipeline(ops);
    }

    public static void bindModel(IVertexSource model) {
        if (CCRenderState.model != model) {
            CCRenderState.model = model;
            pipeline.rebuild();
        }
    }

    public static void setModel(IVertexSource source) {
        CCRenderState.setModel(source, 0, source.getVertices().length);
    }

    public static void setModel(IVertexSource source, int start, int end) {
        CCRenderState.bindModel(source);
        firstVertexIndex = start;
        lastVertexIndex = end;
    }

    public static void render(IVertexOperation ... ops) {
        CCRenderState.setPipeline(ops);
        CCRenderState.render();
    }

    public static void render() {
        Vertex5[] verts = model.getVertices();
        for (vertexIndex = firstVertexIndex; vertexIndex < lastVertexIndex; ++vertexIndex) {
            model.prepareVertex();
            vert.set(verts[vertexIndex]);
            CCRenderState.runPipeline();
            CCRenderState.writeVert();
        }
    }

    public static void runPipeline() {
        pipeline.operate();
    }

    public static void writeVert() {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        VertexFormat format = currentVertexFormat != null ? currentVertexFormat : DefaultVertexFormats.BLOCK;
        for (int i = 0; i < format.getElementCount(); ++i) {
            VertexFormatElement element = format.getElement(i);
            switch (element.getUsage()) {
                case POSITION:
                    buffer.pos(CCRenderState.vert.vec.x, CCRenderState.vert.vec.y, CCRenderState.vert.vec.z);
                    break;
                case COLOR:
                    int rgba = hasColour ? colour : -1;
                    buffer.color(rgba >>> 24, rgba >> 16 & 0xFF, rgba >> 8 & 0xFF,
                            alphaOverride >= 0 ? alphaOverride : rgba & 0xFF);
                    break;
                case UV:
                    if (element.getIndex() == 0) {
                        buffer.tex(CCRenderState.vert.uv.u, CCRenderState.vert.uv.v);
                    } else if (element.getIndex() == 1) {
                        int packedBrightness = hasBrightness ? brightness : 0;
                        buffer.lightmap(packedBrightness >> 16 & 65535, packedBrightness & 65535);
                    }
                    break;
                case NORMAL:
                    if (hasNormal) {
                        buffer.normal((float)CCRenderState.normal.x, (float)CCRenderState.normal.y, (float)CCRenderState.normal.z);
                    } else {
                        buffer.normal(0.0F, 1.0F, 0.0F);
                    }
                    break;
                default:
                    break;
            }
        }
        buffer.endVertex();
    }

    public static void setNormal(double x, double y, double z) {
        hasNormal = true;
        normal.set(x, y, z);
    }

    public static void setNormal(Vector3 n) {
        hasNormal = true;
        normal.set(n);
    }

    public static void setColour(int c) {
        hasColour = true;
        colour = c;
    }

    public static void setBrightness(int b) {
        hasBrightness = true;
        brightness = b;
    }

    public static void setBrightness(IBlockAccess world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        CCRenderState.setBrightness(world.getCombinedLight(pos, state.getLightValue()));
    }

    public static void pullLightmap() {
        CCRenderState.setBrightness((int)OpenGlHelper.lastBrightnessY << 16 | (int)OpenGlHelper.lastBrightnessX);
    }

    public static void changeTexture(String texture) {
        CCRenderState.changeTexture(new ResourceLocation(texture));
    }

    public static void changeTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }

    public static void startDrawing() {
        CCRenderState.startDrawing(7);
    }

    private static void startDrawing(int mode) {
        CCRenderState.startDrawing(mode, DefaultVertexFormats.BLOCK);
    }

    public static void startDrawing(int mode, VertexFormat format) {
        currentVertexFormat = format;
        Tessellator.getInstance().getBuffer().begin(mode, format);
    }

    public static void draw() {
        Tessellator.getInstance().draw();
    }

    static /* synthetic */ int access$000(VertexAttribute x0) {
        return CCRenderState.registerVertexAttribute(x0);
    }

    static {
        vertexAttributes = new ArrayList();
        normalAttrib = new VertexAttribute<Vector3[]>(){
            private Vector3[] normalRef;

            @Override
            public Vector3[] newArray(int length) {
                return new Vector3[length];
            }

            @Override
            public boolean load() {
                this.normalRef = model.getAttributes(this);
                if (model.hasAttribute(this)) {
                    return this.normalRef != null;
                }
                if (model.hasAttribute(sideAttrib)) {
                    pipeline.addDependency(sideAttrib);
                    return true;
                }
                throw new IllegalStateException("Normals requested but neither normal or side attrutes are provided by the model");
            }

            @Override
            public void operate() {
                if (this.normalRef != null) {
                    CCRenderState.setNormal(this.normalRef[vertexIndex]);
                } else {
                    CCRenderState.setNormal(Rotation.axes[side]);
                }
            }
        };
        colourAttrib = new VertexAttribute<int[]>(){
            private int[] colourRef;

            @Override
            public int[] newArray(int length) {
                return new int[length];
            }

            @Override
            public boolean load() {
                this.colourRef = model.getAttributes(this);
                return this.colourRef != null || !model.hasAttribute(this);
            }

            @Override
            public void operate() {
                if (this.colourRef != null) {
                    CCRenderState.setColour(ColourRGBA.multiply(baseColour, this.colourRef[vertexIndex]));
                } else {
                    CCRenderState.setColour(baseColour);
                }
            }
        };
        sideAttrib = new VertexAttribute<int[]>(){
            private int[] sideRef;

            @Override
            public int[] newArray(int length) {
                return new int[length];
            }

            @Override
            public boolean load() {
                this.sideRef = model.getAttributes(this);
                if (model.hasAttribute(this)) {
                    return this.sideRef != null;
                }
                pipeline.addDependency(normalAttrib);
                return true;
            }

            @Override
            public void operate() {
                side = this.sideRef != null ? this.sideRef[vertexIndex] : CCModel.findSide(normal);
            }
        };
        lightCoordAttrib = new VertexAttribute<LC[]>(){
            private LC[] lcRef;
            private Vector3 vec = new Vector3();
            private Vector3 pos = new Vector3();

            @Override
            public LC[] newArray(int length) {
                return new LC[length];
            }

            @Override
            public boolean load() {
                this.lcRef = model.getAttributes(this);
                if (model.hasAttribute(this)) {
                    return this.lcRef != null;
                }
                this.pos.set(CCRenderState.lightMatrix.pos.x, CCRenderState.lightMatrix.pos.y, CCRenderState.lightMatrix.pos.z);
                pipeline.addDependency(sideAttrib);
                pipeline.addRequirement(Transformation.operationIndex);
                return true;
            }

            @Override
            public void operate() {
                if (this.lcRef != null) {
                    lc.set(this.lcRef[vertexIndex]);
                } else {
                    lc.compute(this.vec.set(CCRenderState.vert.vec).sub(this.pos), side);
                }
            }
        };
        pipeline = new CCRenderPipeline();
        lightMatrix = new LightMatrix();
        vert = new Vertex5();
        normal = new Vector3();
        lc = new LC();
    }

    public static interface IVertexSource {
        public Vertex5[] getVertices();

        public <T> T getAttributes(VertexAttribute<T> var1);

        public boolean hasAttribute(VertexAttribute<?> var1);

        public void prepareVertex();
    }

    public static abstract class VertexAttribute<T>
    implements IVertexOperation {
        public final int attributeIndex = CCRenderState.access$000(this);
        private final int operationIndex = CCRenderState.registerOperation();
        public boolean active = false;

        public abstract T newArray(int var1);

        @Override
        public int operationID() {
            return this.operationIndex;
        }
    }

    public static interface IVertexOperation {
        public boolean load();

        public void operate();

        public int operationID();
    }
}
