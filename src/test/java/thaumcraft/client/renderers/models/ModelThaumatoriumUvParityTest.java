package thaumcraft.client.renderers.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelThaumatoriumUvParityTest {

    private static final float EPSILON = 0.000001F;

    @Test
    public void rawObjUvsUseTc4WavefrontVFlipAndPerFaceInset() {
        float averageU = (0.2344F + 0.0156F + 0.0156F) / 3.0F;
        float averageV = ((1.0F - 0.9063F) + (1.0F - 0.9063F)
                + (1.0F - 0.8750F)) / 3.0F;

        assertEquals(0.2339F, ModelThaumatorium.legacyTextureU(0.2344F, averageU), EPSILON);
        assertEquals(0.0161F, ModelThaumatorium.legacyTextureU(0.0156F, averageU), EPSILON);
        assertEquals(0.0942F, ModelThaumatorium.legacyTextureV(0.9063F, averageV), EPSILON);
        assertEquals(0.1245F, ModelThaumatorium.legacyTextureV(0.8750F, averageV), EPSILON);
    }
}
