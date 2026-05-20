package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CCModelNullOperationRenderStaticGuardTest {

    @Test
    public void ccModelAndPipelineShouldRejectNullVertexOpsOnNoArgRenderPath() throws IOException {
        String model = readFile("src/main/java/thaumcraft/codechicken/lib/render/CCModel.java");
        String pipeline = readFile("src/main/java/thaumcraft/codechicken/lib/render/CCRenderPipeline.java");

        assertTrue("CCModel.render() must use an empty vertex-op array instead of null placeholders",
                model.contains("this.render(0, this.verts.length, new CCRenderState.IVertexOperation[0]);"));
        assertTrue("CCRenderPipeline must ignore null vertex operations when building the pipeline",
                pipeline.contains("if (ops == null)")
                        && pipeline.contains("if (ops[i] != null)")
                        && pipeline.contains("if (op == null)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
