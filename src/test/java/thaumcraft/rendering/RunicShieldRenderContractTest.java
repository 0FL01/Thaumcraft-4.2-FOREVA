package thaumcraft.rendering;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RunicShieldRenderContractTest {
    private static final String SOURCE = "src/main/java/thaumcraft/client/fx/other/FXShieldRunes.java";
    private static final String MODEL = "src/main/resources/assets/thaumcraft/textures/models/hemis.obj";

    @Test
    public void shieldUsesTheAuthoredHemisphereModel() throws IOException {
        String source = read(SOURCE);
        assertTrue(source.contains("textures/models/hemis.obj")
                && source.contains("CCModel.parseObjModels(MODEL)")
                && source.contains("models.get(\"GeoSphere001\")")
                && source.contains("model.backfacedCopy()")
                && source.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 0.0F)")
                && source.contains("OpenGlHelper.lastBrightnessX")
                && source.contains("previousLightX, previousLightY")
                && source.contains("CCRenderState.startDrawing(GL11.GL_TRIANGLES")
                && source.contains("model.render(CCRenderState.normalAttrib)"));
        assertFalse(source.contains("addLitVertex(") || source.contains("GL11.GL_QUADS"));
    }

    @Test
    public void packagedHemisphereIsTheExactTc4Mesh() throws IOException {
        assertArrayEquals(Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/textures/models/hemis.obj")),
                Files.readAllBytes(Paths.get(MODEL)));
        String obj = read(MODEL);
        assertEquals(1, countLines(obj, "g GeoSphere001"));
        assertEquals(160, countLines(obj, "f "));
    }

    private static int countLines(String source, String prefix) {
        int count = 0;
        for (String line : source.split("\\R")) {
            if (line.startsWith(prefix)) count++;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
