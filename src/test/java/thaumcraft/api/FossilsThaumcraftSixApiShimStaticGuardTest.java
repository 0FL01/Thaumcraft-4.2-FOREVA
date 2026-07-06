package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class FossilsThaumcraftSixApiShimStaticGuardTest {

    @Test
    public void aspectRegistryEventAndProxyMustMatchFossilsThaumcraftSixBridge() throws IOException {
        String event = readFile("src/main/java/thaumcraft/api/aspects/AspectRegistryEvent.java");
        String proxy = readFile("src/main/java/thaumcraft/api/aspects/AspectEventProxy.java");
        String aspect = readFile("src/main/java/thaumcraft/api/aspects/Aspect.java");

        assertTrue("Fossils expects the TC6 AspectRegistryEvent with a public register proxy field",
                event.contains("extends Event")
                        && event.contains("public AspectEventProxy register;"));
        assertTrue("Fossils calls AspectEventProxy.registerObjectTag(ItemStack, AspectList)",
                proxy.contains("public void registerObjectTag(ItemStack item, AspectList aspects)")
                        && proxy.contains("ThaumcraftApi.registerObjectTag(item, aspects);"));
        assertTrue("The TC6 proxy must continue to expose ore-dictionary and complex tag helpers",
                proxy.contains("public void registerObjectTag(String oreDict, AspectList aspects)")
                        && proxy.contains("public void registerComplexObjectTag(ItemStack item, AspectList aspects)")
                        && proxy.contains("public void registerComplexObjectTag(String oreDict, AspectList aspects)"));
        assertTrue("Fossils references TC6 aspect constants that must stay binary-compatible",
                aspect.contains("public static final Aspect ALCHEMY")
                        && aspect.contains("public static final Aspect AVERSION")
                        && aspect.contains("public static final Aspect PROTECT")
                        && aspect.contains("public static final Aspect DESIRE")
                        && aspect.contains("public static final Aspect FLUX = TAINT;"));
    }

    @Test
    public void thaumcraftApiMustExposeFossilsSeedRegistrationSurface() throws IOException {
        String api = readFile("src/main/java/thaumcraft/api/ThaumcraftApi.java");
        String crops = readFile("src/main/java/thaumcraft/common/lib/utils/CropUtils.java");

        assertTrue("Fossils calls the TC6 ThaumcraftApi.registerSeed(Block, ItemStack) method",
                api.contains("public static void registerSeed(Block crop, ItemStack seed)")
                        && api.contains("seedList.put(crop.getTranslationKey(), seed);")
                        && api.contains("CropUtils.addStandardCrop(crop, Short.MAX_VALUE);"));
        assertTrue("The TC6 getSeed lookup should remain paired with registerSeed",
                api.contains("public static ItemStack getSeed(Block crop)")
                        && api.contains("return seedList.get(crop.getTranslationKey());"));
        assertTrue("CropUtils needs a block overload because TC6 registerSeed receives the crop block directly",
                crops.contains("public static void addStandardCrop(Block block, int maxMeta)"));
    }

    @Test
    public void thaumcraftPostInitMustPublishAspectRegistryEventAfterCoreAspectTags() throws IOException {
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");
        String compat = readFile("src/main/java/thaumcraft/common/compat/ThaumcraftSixCompatibility.java");

        int coreTags = thaumcraft.indexOf("ConfigAspects.init();");
        int compatEvent = thaumcraft.indexOf("ThaumcraftSixCompatibility.postAspectRegistryEvent();");
        int research = thaumcraft.indexOf("ConfigResearch.init();");

        assertTrue("Thaumcraft.postInit must publish the TC6 aspect registry event after built-in tags and before research setup",
                coreTags >= 0 && compatEvent > coreTags && research > compatEvent);
        assertTrue("The published event must include the TC6 register proxy Fossils reads",
                compat.contains("AspectRegistryEvent aspectRegistryEvent = new AspectRegistryEvent();")
                        && compat.contains("aspectRegistryEvent.register = new AspectEventProxy();")
                        && compat.contains("MinecraftForge.EVENT_BUS.post(aspectRegistryEvent);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
