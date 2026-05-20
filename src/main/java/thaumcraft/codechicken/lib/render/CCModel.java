package thaumcraft.codechicken.lib.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.codechicken.lib.lighting.LC;
import thaumcraft.codechicken.lib.lighting.LightModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.render.Vertex5;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;
import thaumcraft.codechicken.lib.render.uv.UVTranslation;
import thaumcraft.codechicken.lib.util.Copyable;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.RedundantTransformation;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Transformation;
import thaumcraft.codechicken.lib.vec.TransformationList;
import thaumcraft.codechicken.lib.vec.Vector3;

public class CCModel
implements CCRenderState.IVertexSource,
Copyable<CCModel> {
    public final int vertexMode;
    public final int vp;
    public Vertex5[] verts;
    public ArrayList<Object> attributes = new ArrayList();
    private static final Pattern vertPattern = Pattern.compile("v(?: ([\\d\\.+-]+))+");
    private static final Pattern uvwPattern = Pattern.compile("vt(?: ([\\d\\.+-]+))+");
    private static final Pattern normalPattern = Pattern.compile("vn(?: ([\\d\\.+-]+))+");
    private static final Pattern polyPattern = Pattern.compile("f(?: ((?:\\d*)(?:/\\d*)?(?:/\\d*)?))+");
    public static final Matcher vertMatcher = vertPattern.matcher("");
    public static final Matcher uvwMatcher = uvwPattern.matcher("");
    public static final Matcher normalMatcher = normalPattern.matcher("");
    public static final Matcher polyMatcher = polyPattern.matcher("");

    protected CCModel(int vertexMode) {
        if (vertexMode != 7 && vertexMode != 4) {
            throw new IllegalArgumentException("Models must be GL_QUADS or GL_TRIANGLES");
        }
        this.vertexMode = vertexMode;
        this.vp = vertexMode == 7 ? 4 : 3;
    }

    public Vector3[] normals() {
        return this.getAttributes(CCRenderState.normalAttrib);
    }

    @Override
    public Vertex5[] getVertices() {
        return this.verts;
    }

    @Override
    public <T> T getAttributes(CCRenderState.VertexAttribute<T> attr) {
        if (attr.attributeIndex < this.attributes.size()) {
            return (T)this.attributes.get(attr.attributeIndex);
        }
        return null;
    }

    @Override
    public boolean hasAttribute(CCRenderState.VertexAttribute<?> attrib) {
        return attrib.attributeIndex < this.attributes.size() && this.attributes.get(attrib.attributeIndex) != null;
    }

    @Override
    public void prepareVertex() {
    }

    public <T> T getOrAllocate(CCRenderState.VertexAttribute<T> attrib) {
        T array = this.getAttributes(attrib);
        if (array == null) {
            while (this.attributes.size() <= attrib.attributeIndex) {
                this.attributes.add(null);
            }
            array = attrib.newArray(this.verts.length);
            this.attributes.set(attrib.attributeIndex, array);
        }
        return array;
    }

    public CCModel generateBox(int i, double x1, double y1, double z1, double w, double h, double d, double tx, double ty, double tw, double th, double f) {
        double x2 = x1 + w;
        double y2 = y1 + h;
        double z2 = z1 + d;
        x1 /= f;
        x2 /= f;
        y1 /= f;
        y2 /= f;
        z1 /= f;
        double u1 = (tx + d + w) / tw;
        double v1 = (ty + d) / th;
        double u2 = (tx + d * 2.0 + w) / tw;
        double v2 = ty / th;
        this.verts[i++] = new Vertex5(x1, y1, z2 /= f, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
        this.verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = ty / th;
        this.verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x1, y2, z2, u1, v2);
        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z1, u2, v2);
        u1 = (tx + d * 2.0 + w * 2.0) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2.0 + w) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
        this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = tx / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
        this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        this.verts[i++] = new Vertex5(x1, y1, z2, u2, v2);
        u1 = (tx + d * 2.0 + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = (ty + d + h) / th;
        this.verts[i++] = new Vertex5(x2, y1, z2, u1, v2);
        this.verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        this.verts[i++] = new Vertex5(x2, y2, z2, u1, v1);
        return this;
    }

    public CCModel generateBlock(int i, Cuboid6 bounds) {
        return this.generateBlock(i, bounds, 0);
    }

    public CCModel generateBlock(int i, Cuboid6 bounds, int mask) {
        return this.generateBlock(i, bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z, mask);
    }

    public CCModel generateBlock(int i, double x1, double y1, double z1, double x2, double y2, double z2) {
        return this.generateBlock(i, x1, y1, z1, x2, y2, z2, 0);
    }

    public CCModel generateBlock(int i, double x1, double y1, double z1, double x2, double y2, double z2, int mask) {
        double v2;
        double u2;
        double v1;
        double u1;
        if ((mask & 1) == 0) {
            u1 = x1;
            v1 = z1;
            u2 = x2;
            v2 = z2;
            this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        }
        if ((mask & 2) == 0) {
            u1 = x1 + 2.0;
            v1 = z1;
            u2 = x2 + 2.0;
            v2 = z2;
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
            this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x1, y2, z2, u1, v2);
        }
        if ((mask & 4) == 0) {
            u1 = 1.0 - x1 + 4.0;
            v1 = 1.0 - y2;
            u2 = 1.0 - x2 + 4.0;
            v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        }
        if ((mask & 8) == 0) {
            u1 = x1 + 6.0;
            v1 = 1.0 - y2;
            u2 = x2 + 6.0;
            v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
            this.verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        }
        if ((mask & 0x10) == 0) {
            u1 = z1 + 8.0;
            v1 = 1.0 - y2;
            u2 = z2 + 8.0;
            v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x1, y1, z2, u2, v2);
            this.verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        }
        if ((mask & 0x20) == 0) {
            u1 = 1.0 - z1 + 10.0;
            v1 = 1.0 - y2;
            u2 = 1.0 - z2 + 10.0;
            v2 = 1.0 - y1;
            this.verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
            this.verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
            this.verts[i++] = new Vertex5(x2, y2, z2, u2, v1);
            this.verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        }
        return this;
    }

    public CCModel computeNormals() {
        return this.computeNormals(0, this.verts.length);
    }

    public CCModel computeNormals(int start, int length) {
        if (length % this.vp != 0 || start % this.vp != 0) {
            throw new IllegalArgumentException("Cannot generate normals across polygons");
        }
        Vector3[] normals = this.getOrAllocate(CCRenderState.normalAttrib);
        for (int k = 0; k < length; k += this.vp) {
            int i = k + start;
            Vector3 diff1 = this.verts[i + 1].vec.copy().subtract(this.verts[i].vec);
            Vector3 diff2 = this.verts[i + this.vp - 1].vec.copy().subtract(this.verts[i].vec);
            normals[i] = diff1.crossProduct(diff2).normalize();
            for (int d = 1; d < this.vp; ++d) {
                normals[i + d] = normals[i].copy();
            }
        }
        return this;
    }

    public CCModel computeLighting(LightModel light) {
        Vector3[] normals = this.normals();
        int[] colours = this.getAttributes(CCRenderState.colourAttrib);
        if (colours == null) {
            this.setColour(-1);
            colours = this.getAttributes(CCRenderState.colourAttrib);
        }
        for (int k = 0; k < this.verts.length; ++k) {
            colours[k] = light.apply(colours[k], normals[k]);
        }
        return this;
    }

    public CCModel setColour(int c) {
        int[] colours = this.getOrAllocate(CCRenderState.colourAttrib);
        Arrays.fill(colours, c);
        return this;
    }

    public CCModel computeLightCoords() {
        LC[] lcs = this.getOrAllocate(CCRenderState.lightCoordAttrib);
        Vector3[] normals = this.normals();
        for (int i = 0; i < this.verts.length; ++i) {
            lcs[i] = new LC().compute(this.verts[i].vec, normals[i]);
        }
        return this;
    }

    public CCModel smoothNormals() {
        ArrayList<PositionNormalEntry> map = new ArrayList<PositionNormalEntry>();
        Vector3[] normals = this.normals();
        block0: for (int k = 0; k < this.verts.length; ++k) {
            Vector3 vec = this.verts[k].vec;
            for (PositionNormalEntry e : map) {
                if (!e.positionEqual(vec)) continue;
                e.addNormal(normals[k]);
                continue block0;
            }
            map.add(new PositionNormalEntry(vec).addNormal(normals[k]));
        }
        for (PositionNormalEntry e : map) {
            if (e.normals.size() <= 1) continue;
            Vector3 new_n = new Vector3();
            for (Vector3 n : e.normals) {
                new_n.add(n);
            }
            new_n.normalize();
            for (Vector3 n : e.normals) {
                n.set(new_n);
            }
        }
        return this;
    }

    public CCModel apply(Transformation t) {
        for (int k = 0; k < this.verts.length; ++k) {
            this.verts[k].apply(t);
        }
        Vector3[] normals = this.normals();
        if (normals != null) {
            for (int k = 0; k < normals.length; ++k) {
                t.applyN(normals[k]);
            }
        }
        return this;
    }

    public CCModel apply(UVTransformation uvt) {
        for (int k = 0; k < this.verts.length; ++k) {
            this.verts[k].apply(uvt);
        }
        return this;
    }

    public CCModel expand(int extraVerts) {
        int newLen = this.verts.length + extraVerts;
        this.verts = Arrays.copyOf(this.verts, newLen);
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.get(i) == null) continue;
            this.attributes.set(i, CCRenderState.copyOf((CCRenderState.VertexAttribute)CCRenderState.getAttribute(i), this.attributes.get(i), newLen));
        }
        return this;
    }

    public void render() {
        this.render(0, this.verts.length, new CCRenderState.IVertexOperation[0]);
    }

    public void render(double x, double y, double z, double u, double v) {
        this.render(new Vector3(x, y, z).translation(), new UVTranslation(u, v));
    }

    public void render(double x, double y, double z, UVTransformation u) {
        this.render(new Vector3(x, y, z).translation(), u);
    }

    public void render(Transformation t, double u, double v) {
        this.render(t, new UVTranslation(u, v));
    }

    public void render(CCRenderState.IVertexOperation ... ops) {
        this.render(0, this.verts.length, ops);
    }

    public void render(int start, int end, CCRenderState.IVertexOperation ... ops) {
        CCRenderState.setPipeline(this, start, end, ops);
        CCRenderState.render();
    }

    public static CCModel quadModel(int numVerts) {
        return CCModel.newModel(7, numVerts);
    }

    public static CCModel triModel(int numVerts) {
        return CCModel.newModel(4, numVerts);
    }

    public static CCModel newModel(int vertexMode, int numVerts) {
        CCModel model = CCModel.newModel(vertexMode);
        model.verts = new Vertex5[numVerts];
        return model;
    }

    public static CCModel newModel(int vertexMode) {
        return new CCModel(vertexMode);
    }

    public static double[] parseDoubles(String s, String token) {
        String[] as = s.split(token);
        double[] values = new double[as.length];
        for (int i = 0; i < as.length; ++i) {
            values[i] = Double.parseDouble(as[i]);
        }
        return values;
    }

    public static void illegalAssert(boolean b, String err) {
        if (!b) {
            throw new IllegalArgumentException(err);
        }
    }

    public static void assertMatch(Matcher m, String s) {
        m.reset(s);
        CCModel.illegalAssert(m.matches(), "Malformed line: " + s);
    }

    public static Map<String, CCModel> parseObjModels(InputStream input, int vertexMode, Transformation coordSystem) throws IOException {
        String line;
        if (coordSystem == null) {
            coordSystem = new RedundantTransformation();
        }
        int vp = vertexMode == 7 ? 4 : 3;
        HashMap<String, CCModel> modelMap = new HashMap<String, CCModel>();
        ArrayList<Vector3> verts = new ArrayList<Vector3>();
        ArrayList<Vector3> uvs = new ArrayList<Vector3>();
        ArrayList<Vector3> normals = new ArrayList<Vector3>();
        ArrayList<int[]> polys = new ArrayList<int[]>();
        String modelName = "unnamed";
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while ((line = reader.readLine()) != null) {
            double[] values;
            if ((line = line.replaceAll("\\s+", " ").trim()).startsWith("#") || line.length() == 0) continue;
            if (line.startsWith("v ")) {
                CCModel.assertMatch(vertMatcher, line);
                values = CCModel.parseDoubles(line.substring(2), " ");
                CCModel.illegalAssert(values.length >= 3, "Vertices must have x, y and z components");
                Vector3 vert = new Vector3(values[0], values[1], values[2]);
                coordSystem.apply(vert);
                verts.add(vert);
                continue;
            }
            if (line.startsWith("vt ")) {
                CCModel.assertMatch(uvwMatcher, line);
                values = CCModel.parseDoubles(line.substring(3), " ");
                CCModel.illegalAssert(values.length >= 2, "Tex Coords must have u, and v components");
                uvs.add(new Vector3(values[0], 1.0 - values[1], 0.0));
                continue;
            }
            if (line.startsWith("vn ")) {
                CCModel.assertMatch(normalMatcher, line);
                values = CCModel.parseDoubles(line.substring(3), " ");
                CCModel.illegalAssert(values.length >= 3, "Normals must have x, y and z components");
                Vector3 norm = new Vector3(values[0], values[1], values[2]).normalize();
                coordSystem.applyN(norm);
                normals.add(norm);
                continue;
            }
            if (line.startsWith("f ")) {
                CCModel.assertMatch(polyMatcher, line);
                String[] av = line.substring(2).split(" ");
                CCModel.illegalAssert(av.length >= 3, "Polygons must have at least 3 vertices");
                int[][] polyVerts = new int[av.length][3];
                for (int i = 0; i < av.length; ++i) {
                    String[] as = av[i].split("/");
                    for (int p = 0; p < as.length; ++p) {
                        if (as[p].length() <= 0) continue;
                        polyVerts[i][p] = Integer.parseInt(as[p]);
                    }
                }
                if (vp == 3) {
                    CCModel.triangulate(polys, polyVerts);
                } else {
                    CCModel.quadulate(polys, polyVerts);
                }
            }
            if (!line.startsWith("g ")) continue;
            if (!polys.isEmpty()) {
                modelMap.put(modelName, CCModel.createModel(verts, uvs, normals, vertexMode, polys));
                polys.clear();
            }
            modelName = line.substring(2);
        }
        if (!polys.isEmpty()) {
            modelMap.put(modelName, CCModel.createModel(verts, uvs, normals, vertexMode, polys));
        }
        return modelMap;
    }

    public static void triangulate(List<int[]> polys, int[][] polyVerts) {
        for (int i = 2; i < polyVerts.length; ++i) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[i]);
            polys.add(polyVerts[i - 1]);
        }
    }

    public static void quadulate(List<int[]> polys, int[][] polyVerts) {
        if (polyVerts.length == 4) {
            polys.add(polyVerts[0]);
            polys.add(polyVerts[3]);
            polys.add(polyVerts[2]);
            polys.add(polyVerts[1]);
        } else {
            for (int i = 2; i < polyVerts.length; ++i) {
                polys.add(polyVerts[0]);
                polys.add(polyVerts[i]);
                polys.add(polyVerts[i - 1]);
                polys.add(polyVerts[i - 1]);
            }
        }
    }

    public static Map<String, CCModel> parseObjModels(ResourceLocation res) {
        return CCModel.parseObjModels(res, 4, null);
    }

    public static Map<String, CCModel> parseObjModels(ResourceLocation res, Transformation coordSystem) {
        try {
            return CCModel.parseObjModels(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream(), 4, coordSystem);
        }
        catch (IOException e) {
            throw new RuntimeException("failed to load model: " + res, e);
        }
    }

    public static Map<String, CCModel> parseObjModels(ResourceLocation res, int vertexMode, Transformation coordSystem) {
        try {
            return CCModel.parseObjModels(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream(), vertexMode, coordSystem);
        }
        catch (Exception e) {
            throw new RuntimeException("failed to load model: " + res, e);
        }
    }

    public static CCModel createModel(List<Vector3> verts, List<Vector3> uvs, List<Vector3> normals, int vertexMode, List<int[]> polys) {
        int vp;
        int n = vp = vertexMode == 7 ? 4 : 3;
        if (polys.size() < vp || polys.size() % vp != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for model: " + polys.size());
        }
        boolean hasNormals = polys.get(0)[2] > 0;
        CCModel model = CCModel.newModel(vertexMode, polys.size());
        if (hasNormals) {
            model.getOrAllocate(CCRenderState.normalAttrib);
        }
        for (int i = 0; i < polys.size(); ++i) {
            int[] ai = polys.get(i);
            Vector3 vert = verts.get(ai[0] - 1).copy();
            Vector3 uv = ai[1] <= 0 ? new Vector3() : uvs.get(ai[1] - 1).copy();
            if (ai[2] > 0 != hasNormals) {
                throw new IllegalArgumentException("Normals are an all or nothing deal here.");
            }
            model.verts[i] = new Vertex5(vert, uv.x, uv.y);
            if (!hasNormals) continue;
            model.normals()[i] = normals.get(ai[2] - 1).copy();
        }
        return model;
    }

    private static <T> int addIndex(List<T> list, T elem) {
        int i = list.indexOf(elem) + 1;
        if (i == 0) {
            list.add(elem);
            i = list.size();
        }
        return i;
    }

    private static String clean(double d) {
        return d == (double)((int)d) ? Integer.toString((int)d) : Double.toString(d);
    }

    public static void exportObj(Map<String, CCModel> models, PrintWriter p) {
        ArrayList verts = new ArrayList();
        ArrayList uvs = new ArrayList();
        ArrayList normals = new ArrayList();
        ArrayList<int[]> polys = new ArrayList<int[]>();
        for (Map.Entry<String, CCModel> e : models.entrySet()) {
            int[] ia;
            int i;
            p.println("g " + e.getKey());
            CCModel m = e.getValue();
            int vStart = verts.size();
            int uStart = uvs.size();
            int nStart = normals.size();
            boolean hasNormals = m.normals() != null;
            polys.clear();
            for (i = 0; i < m.verts.length; ++i) {
                ia = new int[hasNormals ? 3 : 2];
                ia[0] = CCModel.addIndex(verts, m.verts[i].vec);
                ia[1] = CCModel.addIndex(uvs, m.verts[i].uv);
                if (hasNormals) {
                    ia[2] = CCModel.addIndex(normals, m.normals()[i]);
                }
                polys.add(ia);
            }
            if (vStart < verts.size()) {
                p.println();
                for (i = vStart; i < verts.size(); ++i) {
                    Vector3 v = (Vector3)verts.get(i);
                    p.format("v %s %s %s\n", CCModel.clean(v.x), CCModel.clean(v.y), CCModel.clean(v.z));
                }
            }
            if (uStart < uvs.size()) {
                p.println();
                for (i = uStart; i < uvs.size(); ++i) {
                    UV uv = (UV)uvs.get(i);
                    p.format("vt %s %s\n", CCModel.clean(uv.u), CCModel.clean(uv.v));
                }
            }
            if (nStart < normals.size()) {
                p.println();
                for (i = nStart; i < normals.size(); ++i) {
                    Vector3 n = (Vector3)normals.get(i);
                    p.format("vn %s %s %s\n", CCModel.clean(n.x), CCModel.clean(n.y), CCModel.clean(n.z));
                }
            }
            p.println();
            for (i = 0; i < polys.size(); ++i) {
                if (i % m.vp == 0) {
                    p.format("f", new Object[0]);
                }
                ia = (int[])polys.get(i);
                if (hasNormals) {
                    p.format(" %d/%d/%d", ia[0], ia[1], ia[2]);
                } else {
                    p.format(" %d/%d", ia[0], ia[1]);
                }
                if (i % m.vp != m.vp - 1) continue;
                p.println();
            }
        }
    }

    public CCModel shrinkUVs(double d) {
        for (int k = 0; k < this.verts.length; k += this.vp) {
            int i;
            UV uv = new UV();
            for (i = 0; i < this.vp; ++i) {
                uv.add(this.verts[k + i].uv);
            }
            uv.multiply(1.0 / (double)this.vp);
            for (i = 0; i < this.vp; ++i) {
                Vertex5 vert = this.verts[k + i];
                vert.uv.u = vert.uv.u + (vert.uv.u < uv.u ? d : -d);
                vert.uv.v = vert.uv.v + (vert.uv.v < uv.v ? d : -d);
            }
        }
        return this;
    }

    public CCModel sidedCopy(int side1, int side2, Vector3 point) {
        CCModel model = CCModel.newModel(this.vertexMode, this.verts.length);
        CCModel.copy(this, 0, model, 0, model.verts.length);
        return model.apply(new TransformationList((Transformation)Rotation.sideRotations[side1].inverse(), Rotation.sideRotations[side2]).at(point));
    }

    public static void copy(CCModel src, int srcpos, CCModel dst, int destpos, int length) {
        for (int k = 0; k < length; ++k) {
            dst.verts[destpos + k] = src.verts[srcpos + k].copy();
        }
        for (int i = 0; i < src.attributes.size(); ++i) {
            if (src.attributes.get(i) == null) continue;
            CCRenderState.arrayCopy(src.attributes.get(i), srcpos, dst.getOrAllocate(CCRenderState.getAttribute(i)), destpos, length);
        }
    }

    public static void generateSidedModels(CCModel[] models, int side, Vector3 point) {
        for (int s = 0; s < 6; ++s) {
            if (s == side) continue;
            models[s] = models[side].sidedCopy(side, s, point);
        }
    }

    public static void generateSidedModelsH(CCModel[] models, int side, Vector3 point) {
        for (int s = 2; s < 6; ++s) {
            if (s == side) continue;
            models[s] = models[side].sidedCopy(side, s, point);
        }
    }

    public CCModel backfacedCopy() {
        return CCModel.generateBackface(this, 0, this.copy(), 0, this.verts.length);
    }

    public static CCModel generateBackface(CCModel src, int srcpos, CCModel dst, int destpos, int length) {
        int vp = src.vp;
        if (srcpos % vp != 0 || destpos % vp != 0 || length % vp != 0) {
            throw new IllegalArgumentException("Vertices do not align with polygons");
        }
        int[][] o = new int[][]{{0, 0}, {1, vp - 1}, {2, vp - 2}, {3, vp - 3}};
        for (int i = 0; i < length; ++i) {
            int b = i / vp * vp;
            int d = i % vp;
            int di = destpos + b + o[d][1];
            int si = srcpos + b + o[d][0];
            dst.verts[di] = src.verts[si].copy();
            for (int a = 0; a < src.attributes.size(); ++a) {
                if (src.attributes.get(a) == null) continue;
                CCRenderState.arrayCopy(src.attributes.get(a), si, dst.getOrAllocate(CCRenderState.getAttribute(a)), di, 1);
            }
            if (dst.normals() == null || dst.normals()[di] == null) continue;
            dst.normals()[di].negate();
        }
        return dst;
    }

    public CCModel generateSidedParts(int side, Vector3 point) {
        if (this.verts.length % (6 * this.vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        int length = this.verts.length / 6;
        for (int s = 0; s < 6; ++s) {
            if (s == side) continue;
            this.generateSidedPart(side, s, point, length * side, length * s, length);
        }
        return this;
    }

    public CCModel generateSidedPartsH(int side, Vector3 point) {
        if (this.verts.length % (4 * this.vp) != 0) {
            throw new IllegalArgumentException("Invalid number of vertices for sided part generation");
        }
        int length = this.verts.length / 4;
        for (int s = 2; s < 6; ++s) {
            if (s == side) continue;
            this.generateSidedPart(side, s, point, length * (side - 2), length * (s - 2), length);
        }
        return this;
    }

    public CCModel generateSidedPart(int side1, int side2, Vector3 point, int srcpos, int destpos, int length) {
        return this.apply(new TransformationList((Transformation)Rotation.sideRotations[side1].inverse(), Rotation.sideRotations[side2]).at(point), srcpos, destpos, length);
    }

    public CCModel apply(Transformation t, int srcpos, int destpos, int length) {
        for (int k = 0; k < length; ++k) {
            this.verts[destpos + k] = this.verts[srcpos + k].copy();
            this.verts[destpos + k].vec.apply(t);
        }
        Vector3[] normals = this.normals();
        if (normals != null) {
            for (int k = 0; k < length; ++k) {
                normals[destpos + k] = normals[srcpos + k].copy();
                t.applyN(normals[destpos + k]);
            }
        }
        return this;
    }

    public static CCModel combine(Collection<CCModel> models) {
        if (models.isEmpty()) {
            return null;
        }
        int numVerts = 0;
        int vertexMode = -1;
        for (CCModel model : models) {
            if (vertexMode == -1) {
                vertexMode = model.vertexMode;
            }
            if (vertexMode != model.vertexMode) {
                throw new IllegalArgumentException("Cannot combine models with different vertex modes");
            }
            numVerts += model.verts.length;
        }
        CCModel c_model = CCModel.newModel(vertexMode, numVerts);
        int i = 0;
        for (CCModel model : models) {
            CCModel.copy(model, 0, c_model, i, model.verts.length);
            i += model.verts.length;
        }
        return c_model;
    }

    public CCModel twoFacedCopy() {
        CCModel model = CCModel.newModel(this.vertexMode, this.verts.length * 2);
        CCModel.copy(this, 0, model, 0, this.verts.length);
        return CCModel.generateBackface(model, 0, model, this.verts.length, this.verts.length);
    }

    @Override
    public CCModel copy() {
        CCModel model = CCModel.newModel(this.vertexMode, this.verts.length);
        CCModel.copy(this, 0, model, 0, this.verts.length);
        return model;
    }

    public Vector3 collapse() {
        Vector3 v = new Vector3();
        for (Vertex5 vert : this.verts) {
            v.add(vert.vec);
        }
        v.multiply(1.0 / (double)this.verts.length);
        return v;
    }

    public CCModel zOffset(Cuboid6 offsets) {
        block8: for (int k = 0; k < this.verts.length; ++k) {
            Vertex5 vert = this.verts[k];
            Vector3 normal = this.normals()[k];
            switch (CCModel.findSide(normal)) {
                case 0: {
                    vert.vec.y += offsets.min.y;
                    continue block8;
                }
                case 1: {
                    vert.vec.y += offsets.max.y;
                    continue block8;
                }
                case 2: {
                    vert.vec.z += offsets.min.z;
                    continue block8;
                }
                case 3: {
                    vert.vec.z += offsets.max.z;
                    continue block8;
                }
                case 4: {
                    vert.vec.x += offsets.min.x;
                    continue block8;
                }
                case 5: {
                    vert.vec.x += offsets.max.x;
                }
            }
        }
        return this;
    }

    public static int findSide(Vector3 normal) {
        if (normal.y <= -0.99) {
            return 0;
        }
        if (normal.y >= 0.99) {
            return 1;
        }
        if (normal.z <= -0.99) {
            return 2;
        }
        if (normal.z >= 0.99) {
            return 3;
        }
        if (normal.x <= -0.99) {
            return 4;
        }
        if (normal.x >= 0.99) {
            return 5;
        }
        return -1;
    }

    public Cuboid6 bounds() {
        Vector3 vec1 = this.verts[0].vec;
        Cuboid6 c = new Cuboid6(vec1.copy(), vec1.copy());
        for (int i = 1; i < this.verts.length; ++i) {
            c.enclose(this.verts[i].vec);
        }
        return c;
    }

    private static class PositionNormalEntry {
        public Vector3 pos;
        public LinkedList<Vector3> normals = new LinkedList();

        public PositionNormalEntry(Vector3 position) {
            this.pos = position;
        }

        public boolean positionEqual(Vector3 v) {
            return this.pos.x == v.x && this.pos.y == v.y && this.pos.z == v.z;
        }

        public PositionNormalEntry addNormal(Vector3 normal) {
            this.normals.add(normal);
            return this;
        }
    }
}
