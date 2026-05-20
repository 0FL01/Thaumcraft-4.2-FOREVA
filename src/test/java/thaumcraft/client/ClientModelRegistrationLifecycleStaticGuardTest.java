package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientModelRegistrationLifecycleStaticGuardTest {

    @Test
    public void modelLocationsShouldBeRegisteredFromClientModelRegistryEvent() throws IOException {
        String registry = readFile("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        String proxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String commonProxy = readFile("src/main/java/thaumcraft/common/CommonProxy.java");
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertTrue("ClientModelRegistry must subscribe on the client side and route ModelRegistryEvent through Thaumcraft.proxy.registerModelLocations",
                registry.contains("@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Side.CLIENT)")
                        && registry.contains("public static void registerModels(ModelRegistryEvent event)")
                        && registry.contains("Thaumcraft.proxy.registerModelLocations();"));
        assertTrue("CommonProxy must expose a model-registration hook for the client event bridge",
                commonProxy.contains("public void registerModelLocations()"));
        assertTrue("ClientProxy must register item and block model locations through the dedicated hook",
                proxy.contains("public void registerModelLocations()")
                        && proxy.contains("setupItemRenderers();")
                        && proxy.contains("setupBlockRenderers();"));
        assertTrue("Thaumcraft init must still keep display-information bootstrap separate from the model registry event hook",
                thaumcraft.contains("proxy.registerDisplayInformation();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
