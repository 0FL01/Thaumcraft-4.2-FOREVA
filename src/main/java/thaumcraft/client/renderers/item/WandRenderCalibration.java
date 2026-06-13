package thaumcraft.client.renderers.item;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * External calibration layer for {@link ItemWandRenderer}.
 *
 * <p>Transforms for the wand/staff/sceptre TEISR are resolved from a JSON document and cached.
 * If the JSON is missing or malformed, the {@link #buildDefaultCalibration()} Java defaults are
 * used. Those defaults reproduce the pre-calibration hardcoded constants exactly, so absence of
 * the JSON is a no-op visually.
 *
 * <p>Application order is fixed and documented in {@link ItemWandRenderer#applyBasePose}:
 * <pre>
 *   translate(preTranslate) -> translate(translate)
 *   -> [if first person] scale(firstPersonScale)
 *   -> scale(scale) -> scale(scaleMultiplier)
 *   -> rotateX -> rotateY -> rotateZ
 *   -> translate(postTranslate)   // already includes postTranslateAdd
 *   -> rotate(finalRotate)
 * </pre>
 *
 * <p>Hot reload: edit the JSON on disk and trigger a resource reload (F3+T). The reload listener
 * in ClientProxy calls {@link #reload()} which re-reads both the config file and the asset.
 *
 * <p>Kind priority matches existing logic: staff &gt; sceptre &gt; wand.
 */
@SideOnly(Side.CLIENT)
public final class WandRenderCalibration {

    private static final Logger LOGGER = LogManager.getLogger("Thaumcraft");

    /**
     * Asset location of the bundled calibration document. This is the source of truth for the
     * shipped defaults; a config-file copy may override it for live tweaking.
     */
    static final ResourceLocation ASSET =
            new ResourceLocation("thaumcraft", "render_calibration/wand_casting.json");

    /** Editable override path. Hot-reloaded via F3+T. */
    static final File CONFIG_FILE =
            new File("config/thaumcraft/render_calibration/wand_casting.json");

    static final String KIND_WAND = "wand";
    static final String KIND_STAFF = "staff";
    static final String KIND_SCEPTRE = "sceptre";

    /** Resolved calibration: kind -> (TransformType -> Transform). */
    private static volatile Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> resolved;

    private static final String DEBUG_PROPERTY = "thaumcraft.debugWandRender";

    private WandRenderCalibration() {
    }

    // ------------------------------------------------------------------ Transform holder

    /** Fully resolved transform for one (kind, TransformType). All values are in GL units. */
    public static final class Transform {
        public float preTranslateX, preTranslateY, preTranslateZ;
        public float translateX, translateY, translateZ;
        public float firstPersonScaleX = 1f, firstPersonScaleY = 1f, firstPersonScaleZ = 1f;
        public float scaleX = 1f, scaleY = 1f, scaleZ = 1f;
        public float scaleMulX = 1f, scaleMulY = 1f, scaleMulZ = 1f;
        public float rotateX, rotateY, rotateZ;
        public float postTranslateX, postTranslateY, postTranslateZ;
        public float finalRotateX, finalRotateY, finalRotateZ;
        public boolean mirrorFromRightHand;

        @Override
        public String toString() {
            return "t=[" + f(translateX) + "," + f(translateY) + "," + f(translateZ) + "]"
                    + " r=[" + f(rotateX) + "," + f(rotateY) + "," + f(rotateZ) + "]"
                    + " s=[" + f(scaleX) + "," + f(scaleY) + "," + f(scaleZ) + "]"
                    + " sm=[" + f(scaleMulX) + "," + f(scaleMulY) + "," + f(scaleMulZ) + "]"
                    + " post=[" + f(postTranslateX) + "," + f(postTranslateY) + "," + f(postTranslateZ) + "]"
                    + " pre=[" + f(preTranslateX) + "," + f(preTranslateY) + "," + f(preTranslateZ) + "]"
                    + " final=[" + f(finalRotateX) + "," + f(finalRotateY) + "," + f(finalRotateZ) + "]";
        }

        private static String f(float v) {
            if (v == (long) v) return Long.toString((long) v);
            return Float.toString(v);
        }
    }

    // ------------------------------------------------------------------ Access

    /**
     * @return resolved transform for the given kind and context. Never null. Kind falls back to
     *         "wand" if unknown; context falls back to NONE if unknown.
     */
    public static Transform get(String kind, ItemCameraTransforms.TransformType type) {
        Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> map = resolved;
        if (map == null) {
            synchronized (WandRenderCalibration.class) {
                map = resolved;
                if (map == null) {
                    map = loadCalibration();
                    resolved = map;
                }
            }
        }
        EnumMap<ItemCameraTransforms.TransformType, Transform> perKind = map.get(kind);
        if (perKind == null) {
            perKind = map.get(KIND_WAND);
        }
        if (perKind == null) {
            perKind = map.get(KIND_WAND); // guaranteed by defaults
        }
        Transform t = perKind.get(type);
        if (t == null) {
            t = perKind.get(ItemCameraTransforms.TransformType.NONE);
        }
        return t;
    }

    public static boolean isDebug() {
        return Boolean.getBoolean(DEBUG_PROPERTY);
    }

    // ------------------------------------------------------------------ Reload

    /** Drops the cache so the next {@link #get} re-reads config + asset. Thread-safe. */
    public static void reload() {
        synchronized (WandRenderCalibration.class) {
            try {
                resolved = loadCalibration();
                LOGGER.info("[TC4F/WandRender] calibration reloaded ({} kinds)", resolved.size());
            } catch (Throwable t) {
                LOGGER.error("[TC4F/WandRender] calibration reload failed, keeping previous cache", t);
            }
        }
    }

    /** Force a fresh load right now (used by resource reload listener). */
    public static void onResourceManagerReload(@SuppressWarnings("unused") IResourceManager manager) {
        reload();
    }

    // ------------------------------------------------------------------ Loading

    private static Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> loadCalibration() {
        // 1) editable config file on disk (highest priority for live tweaking)
        String json = readConfigFile();
        if (json == null) {
            // 2) bundled asset
            json = readAsset();
        }
        if (json == null) {
            LOGGER.info("[TC4F/WandRender] no wand_casting.json found; using built-in Java defaults");
            return buildDefaultCalibration();
        }
        try {
            return parseCalibration(json);
        } catch (Throwable t) {
            LOGGER.error("[TC4F/WandRender] failed to parse wand_casting.json; falling back to Java defaults", t);
            return buildDefaultCalibration();
        }
    }

    @Nullable
    private static String readConfigFile() {
        try {
            if (!CONFIG_FILE.isFile()) return null;
            try (Reader r = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                return readFully(r);
            }
        } catch (Throwable t) {
            LOGGER.warn("[TC4F/WandRender] could not read {}: {}", CONFIG_FILE, t.toString());
            return null;
        }
    }

    @Nullable
    private static String readAsset() {
        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(ASSET);
            try (Reader r = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
                return readFully(r);
            }
        } catch (Throwable t) {
            return null;
        }
    }

    private static String readFully(Reader r) throws java.io.IOException {
        StringWriter sw = new StringWriter();
        char[] buf = new char[2048];
        int n;
        while ((n = r.read(buf)) != -1) {
            sw.write(buf, 0, n);
        }
        return sw.toString();
    }

    // ------------------------------------------------------------------ Parsing

    private static Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> parseCalibration(String json) {
        JsonObject root = new JsonParser().parse(json).getAsJsonObject();

        // common.finalRotate -> applied to every resolved transform
        float[] finalRotate = {180f, 0f, 0f};
        if (root.has("common") && root.get("common").isJsonObject()) {
            JsonObject common = root.getAsJsonObject("common");
            float[] fr = readFloatArray(common, "finalRotate");
            if (fr != null) finalRotate = fr;
        }

        JsonObject kinds = root.has("kinds") ? root.getAsJsonObject("kinds") : new JsonObject();

        Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> out = new HashMap<>();

        // Resolve wand first (base), then staff/sceptre which may inherit.
        resolveKind(KIND_WAND, kinds.getAsJsonObject(KIND_WAND), finalRotate, null, out);
        resolveKind(KIND_STAFF, kinds.getAsJsonObject(KIND_STAFF), finalRotate, out.get(KIND_WAND), out);
        resolveKind(KIND_SCEPTRE, kinds.getAsJsonObject(KIND_SCEPTRE), finalRotate, out.get(KIND_WAND), out);

        return out;
    }

    private static void resolveKind(String kind, @Nullable JsonObject kindDef, float[] finalRotate,
                                    @Nullable EnumMap<ItemCameraTransforms.TransformType, Transform> inherited,
                                    Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> out) {
        EnumMap<ItemCameraTransforms.TransformType, Transform> perKind = new EnumMap<>(ItemCameraTransforms.TransformType.class);

        float[] preTranslate = readFloatArray(kindDef, "preTranslate");
        if (preTranslate == null && inherited != null) {
            // inherit kind-level preTranslate from parent via a sentinel context lookup
            Transform parentNone = inherited.get(ItemCameraTransforms.TransformType.NONE);
            if (parentNone != null) {
                preTranslate = new float[]{parentNone.preTranslateX, parentNone.preTranslateY, parentNone.preTranslateZ};
            }
        }
        if (preTranslate == null) preTranslate = new float[]{0f, 0f, 0f};

        JsonObject contexts = (kindDef != null && kindDef.has("contexts") && kindDef.get("contexts").isJsonObject())
                ? kindDef.getAsJsonObject("contexts") : new JsonObject();

        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            String key = type.name(); // GUI, GROUND, FIXED, FIRST_PERSON_RIGHT_HAND, ...
            Transform base;
            if (inherited != null && inherited.containsKey(type)) {
                base = copy(inherited.get(type));
            } else {
                base = new Transform();
            }
            // stamp kind-level preTranslate (overrides inherited unless context overrides below)
            base.preTranslateX = preTranslate[0];
            base.preTranslateY = preTranslate[1];
            base.preTranslateZ = preTranslate[2];
            // common final rotate
            base.finalRotateX = finalRotate[0];
            base.finalRotateY = finalRotate[1];
            base.finalRotateZ = finalRotate[2];

            if (contexts.has(key) && contexts.get(key).isJsonObject()) {
                applyContext(base, contexts.getAsJsonObject(key));
            }
            perKind.put(type, base);
        }
        out.put(kind, perKind);
    }

    private static void applyContext(Transform t, JsonObject ctx) {
        float[] tr = readFloatArray(ctx, "translate");
        if (tr != null) { t.translateX = tr[0]; t.translateY = tr[1]; t.translateZ = tr[2]; }

        float[] ro = readFloatArray(ctx, "rotate");
        if (ro != null) { t.rotateX = ro[0]; t.rotateY = ro[1]; t.rotateZ = ro[2]; }

        float[] sc = readFloatArray(ctx, "scale");
        if (sc != null) { t.scaleX = sc[0]; t.scaleY = sc[1]; t.scaleZ = sc[2]; }

        // scaleMultiplier multiplies the (possibly inherited) scale
        float[] sm = readFloatArray(ctx, "scaleMultiplier");
        if (sm != null) { t.scaleMulX = sm[0]; t.scaleMulY = sm[1]; t.scaleMulZ = sm[2]; }

        float[] fps = readFloatArray(ctx, "firstPersonScale");
        if (fps != null) { t.firstPersonScaleX = fps[0]; t.firstPersonScaleY = fps[1]; t.firstPersonScaleZ = fps[2]; }

        float[] post = readFloatArray(ctx, "postTranslate");
        if (post != null) { t.postTranslateX = post[0]; t.postTranslateY = post[1]; t.postTranslateZ = post[2]; }

        // postTranslateAdd adds on top of (possibly inherited) postTranslate
        float[] postAdd = readFloatArray(ctx, "postTranslateAdd");
        if (postAdd != null) { t.postTranslateX += postAdd[0]; t.postTranslateY += postAdd[1]; t.postTranslateZ += postAdd[2]; }

        float[] fr = readFloatArray(ctx, "finalRotate");
        if (fr != null) { t.finalRotateX = fr[0]; t.finalRotateY = fr[1]; t.finalRotateZ = fr[2]; }

        if (ctx.has("mirrorFromRightHand") && ctx.get("mirrorFromRightHand").isJsonPrimitive()) {
            t.mirrorFromRightHand = ctx.get("mirrorFromRightHand").getAsBoolean();
        }
    }

    private static Transform copy(Transform s) {
        Transform d = new Transform();
        d.preTranslateX = s.preTranslateX; d.preTranslateY = s.preTranslateY; d.preTranslateZ = s.preTranslateZ;
        d.translateX = s.translateX; d.translateY = s.translateY; d.translateZ = s.translateZ;
        d.firstPersonScaleX = s.firstPersonScaleX; d.firstPersonScaleY = s.firstPersonScaleY; d.firstPersonScaleZ = s.firstPersonScaleZ;
        d.scaleX = s.scaleX; d.scaleY = s.scaleY; d.scaleZ = s.scaleZ;
        d.scaleMulX = s.scaleMulX; d.scaleMulY = s.scaleMulY; d.scaleMulZ = s.scaleMulZ;
        d.rotateX = s.rotateX; d.rotateY = s.rotateY; d.rotateZ = s.rotateZ;
        d.postTranslateX = s.postTranslateX; d.postTranslateY = s.postTranslateY; d.postTranslateZ = s.postTranslateZ;
        d.finalRotateX = s.finalRotateX; d.finalRotateY = s.finalRotateY; d.finalRotateZ = s.finalRotateZ;
        d.mirrorFromRightHand = s.mirrorFromRightHand;
        return d;
    }

    @Nullable
    private static float[] readFloatArray(@Nullable JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonArray()) return null;
        JsonElement el = obj.get(key);
        try {
            com.google.gson.JsonArray arr = el.getAsJsonArray();
            float[] out = new float[Math.max(3, arr.size())];
            for (int i = 0; i < out.length; i++) out[i] = i < arr.size() ? (float) arr.get(i).getAsDouble() : 0f;
            if (arr.size() == 1) { out[1] = out[0]; out[2] = out[0]; }
            return out;
        } catch (Throwable t) {
            return null;
        }
    }

    // ------------------------------------------------------------------ Java defaults

    /**
     * Built-in defaults. These reproduce the constants that were hardcoded in
     * {@code ItemWandRenderer} before calibration was introduced, byte-for-byte, so a missing or
     * malformed JSON changes nothing visually.
     *
     * <p>Constants mirrored here (original code references):
     * <ul>
     *   <li>common final rotate: 180 about X</li>
     *   <li>wand GUI: translate(0.5,0.5,0) scale(0.6) rotate(20,-45,45) translate(0,0.6,0)</li>
     *   <li>wand GROUND/FIXED: translate(0,1,0)</li>
     *   <li>wand hand: translate(0.5,1.0,0.5) [fp: scale(1,1.1,1)] scale(0.5)</li>
     *   <li>wand NONE: translate(0.5,1.5,0.5)</li>
     *   <li>staff preTranslate(0,0.5,0); staff GUI scaleMul(0.8) postAdd(-0.7,0.6,0);
     *       staff GROUND/FIXED translate(0,1.5,0) scale(0.9)</li>
     *   <li>sceptre: identical to wand (separate kind so it can diverge later)</li>
     * </ul>
     */
    static Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> buildDefaultCalibration() {
        Map<String, EnumMap<ItemCameraTransforms.TransformType, Transform>> out = new HashMap<>();

        float[] finalRotate = {180f, 0f, 0f};

        // ---- wand ----
        EnumMap<ItemCameraTransforms.TransformType, Transform> wand = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        putContext(wand, ItemCameraTransforms.TransformType.GUI,
                0,0,0,  0.5f,0.5f,0f,
                1f,1.1f,1f,  0.6f,0.6f,0.6f,  1f,1f,1f,
                20f,-45f,45f,  0f,0.6f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.GROUND,
                0,0,0,  0f,1f,0f,  1f,1.1f,1f,  1f,1f,1f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.FIXED,
                0,0,0,  0f,1f,0f,  1f,1.1f,1f,  1f,1f,1f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
                0,0,0,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                0,0,0,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                0,0,0,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                0,0,0,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(wand, ItemCameraTransforms.TransformType.NONE,
                0,0,0,  0.5f,1.5f,0.5f,  1f,1.1f,1f,  1f,1f,1f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        out.put(KIND_WAND, wand);

        // ---- staff (inherits wand, overrides the staff-specific offsets) ----
        EnumMap<ItemCameraTransforms.TransformType, Transform> staff = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        // preTranslate [0,0.5,0] applied to all staff contexts
        putContext(staff, ItemCameraTransforms.TransformType.GUI,
                0f,0.5f,0f,  0.5f,0.5f,0f,
                1f,1.1f,1f,  0.6f,0.6f,0.6f,  0.8f,0.8f,0.8f,
                20f,-45f,45f,  -0.7f,1.2f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.GROUND,
                0f,0.5f,0f,  0f,1.5f,0f,  1f,1.1f,1f,  0.9f,0.9f,0.9f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.FIXED,
                0f,0.5f,0f,  0f,1.5f,0f,  1f,1.1f,1f,  0.9f,0.9f,0.9f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
                0f,0.5f,0f,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                0f,0.5f,0f,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                0f,0.5f,0f,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                0f,0.5f,0f,  0.5f,1f,0.5f,  1f,1.1f,1f,  0.5f,0.5f,0.5f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        putContext(staff, ItemCameraTransforms.TransformType.NONE,
                0f,0.5f,0f,  0.5f,1.5f,0.5f,  1f,1.1f,1f,  1f,1f,1f,  1f,1f,1f,
                0f,0f,0f,  0f,0f,0f,  finalRotate, false);
        out.put(KIND_STAFF, staff);

        // ---- sceptre (its own kind, identical to wand for now) ----
        EnumMap<ItemCameraTransforms.TransformType, Transform> sceptre = new EnumMap<>(ItemCameraTransforms.TransformType.class);
        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            sceptre.put(type, copy(wand.get(type)));
        }
        out.put(KIND_SCEPTRE, sceptre);

        return out;
    }

    private static void putContext(EnumMap<ItemCameraTransforms.TransformType, Transform> map,
                                   ItemCameraTransforms.TransformType type,
                                   float preX, float preY, float preZ,
                                   float tx, float ty, float tz,
                                   float fpsx, float fpsy, float fpsz,
                                   float sx, float sy, float sz,
                                   float smx, float smy, float smz,
                                   float rx, float ry, float rz,
                                   float postX, float postY, float postZ,
                                   float[] finalRotate, boolean mirror) {
        Transform t = new Transform();
        t.preTranslateX = preX; t.preTranslateY = preY; t.preTranslateZ = preZ;
        t.translateX = tx; t.translateY = ty; t.translateZ = tz;
        t.firstPersonScaleX = fpsx; t.firstPersonScaleY = fpsy; t.firstPersonScaleZ = fpsz;
        t.scaleX = sx; t.scaleY = sy; t.scaleZ = sz;
        t.scaleMulX = smx; t.scaleMulY = smy; t.scaleMulZ = smz;
        t.rotateX = rx; t.rotateY = ry; t.rotateZ = rz;
        t.postTranslateX = postX; t.postTranslateY = postY; t.postTranslateZ = postZ;
        t.finalRotateX = finalRotate[0]; t.finalRotateY = finalRotate[1]; t.finalRotateZ = finalRotate[2];
        t.mirrorFromRightHand = mirror;
        map.put(type, t);
    }

    // ------------------------------------------------------------------ Debug dump

    /** Writes a compact JSON snapshot of a resolved render context (debug only). */
    static void dumpTransform(File dir, String kind, ItemCameraTransforms.TransformType type, Transform t,
                              @Nullable String rod, @Nullable String cap, boolean hasFocus, @Nullable String focusItem) {
        try {
            if (!dir.exists() && !dir.mkdirs()) return;
            File f = new File(dir, (kind + "_" + type.name() + ".json").toLowerCase());
            JsonObject calibration = new JsonObject();
            calibration.add("translate", arr(t.translateX, t.translateY, t.translateZ));
            calibration.add("rotate", arr(t.rotateX, t.rotateY, t.rotateZ));
            calibration.add("scale", arr(t.scaleX, t.scaleY, t.scaleZ));
            calibration.add("scaleMultiplier", arr(t.scaleMulX, t.scaleMulY, t.scaleMulZ));
            calibration.add("postTranslate", arr(t.postTranslateX, t.postTranslateY, t.postTranslateZ));
            calibration.add("preTranslate", arr(t.preTranslateX, t.preTranslateY, t.preTranslateZ));
            calibration.add("firstPersonScale", arr(t.firstPersonScaleX, t.firstPersonScaleY, t.firstPersonScaleZ));
            calibration.add("finalRotate", arr(t.finalRotateX, t.finalRotateY, t.finalRotateZ));

            JsonObject root = new JsonObject();
            root.addProperty("kind", kind);
            root.addProperty("transformType", type.name());
            if (rod != null) root.addProperty("rod", rod);
            if (cap != null) root.addProperty("cap", cap);
            root.addProperty("hasFocus", hasFocus);
            if (focusItem != null) root.addProperty("focusItem", focusItem);
            root.add("calibration", calibration);

            try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(f.toPath()), StandardCharsets.UTF_8)) {
                new Gson().toJson(root, w);
            }
        } catch (Throwable ignored) {
            // dump is best-effort; never fail rendering because of it
        }
    }

    private static com.google.gson.JsonArray arr(float a, float b, float c) {
        com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
        arr.add(a); arr.add(b); arr.add(c);
        return arr;
    }
}
