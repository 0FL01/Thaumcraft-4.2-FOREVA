package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyEntityRendererRegistrationStaticGuardTest {

    @Test
    public void entityRendererBootstrapStaysWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String noopRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderNoop.java");

        assertTrue("ClientProxy must keep setupEntityRenderers entry-point",
                source.contains("private void setupEntityRenderers()"));
        assertTrue("ClientProxy must keep non-noop RenderEntityItem registrations for item-like entities",
                source.contains("registerEntityRenderer(EntitySpecialItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityPermanentItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityFollowingItem.class, manager -> new RenderEntityItem(manager, renderItem), registered);")
                        && source.contains("registerEntityRenderer(EntityItemGrate.class, manager -> new RenderEntityItem(manager, renderItem), registered);"));
        assertTrue("ClientProxy must iterate ConfigEntities.ENTITIES for renderer registration coverage",
                source.contains("for (net.minecraftforge.fml.common.registry.EntityEntry entry : ConfigEntities.ENTITIES)"));
        assertTrue("ClientProxy must keep fallback RenderNoop registrations for remaining entities",
                source.contains("if (registered.contains(entityClass))")
                        && source.contains("RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNoop::new);"));
        assertTrue("RenderNoop must stay as a side-safe doRender baseline",
                noopRenderer.contains("extends Render<T>")
                        && noopRenderer.contains("public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
