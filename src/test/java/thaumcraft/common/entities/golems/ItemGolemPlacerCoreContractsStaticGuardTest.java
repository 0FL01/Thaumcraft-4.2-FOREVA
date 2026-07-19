package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemGolemPlacerCoreContractsStaticGuardTest {

    @Test
    public void golemPlacerKeepsReferenceUseAndTooltipContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemPlacer.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemGolemPlacer must keep sneak-bypass contract",
                source.contains("public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)"));
        assertTrue("ItemGolemPlacer must keep core/advanced/upgrades/markers/deco tooltip contracts",
                source.contains("I18n.translateToLocal(\"item.ItemGolemCore.name\")")
                        && source.contains("I18n.translateToLocal(\"tc.adv\")")
                        && source.contains("I18n.translateToLocal(\"item.ItemGolemUpgrade.\" + b + \".name\")")
                        && source.contains("I18n.translateToLocal(\"tc.markedloc\")")
                        && source.contains("I18n.translateToLocal(\"item.ItemGolemDecoration.6.name\")"));
        assertTrue("ItemGolemPlacer tooltip labels must keep their original English localization",
                lang.contains("tc.adv=Advanced")
                        && lang.contains("tc.markedloc=marked locations")
                        && lang.contains("item.ItemGolemCore.name="));
        for (int core = 0; core <= 11; core++) {
            assertTrue("Missing golem core localization " + core,
                    lang.contains("item.ItemGolemCore." + core + ".name="));
        }
        for (int upgrade = 0; upgrade <= 5; upgrade++) {
            assertTrue("Missing golem upgrade localization " + upgrade,
                    lang.contains("item.ItemGolemUpgrade." + upgrade + ".name="));
        }
        for (int decoration = 0; decoration <= 6; decoration++) {
            assertTrue("Missing golem decoration localization " + decoration,
                    lang.contains("item.ItemGolemDecoration." + decoration + ".name="));
        }
        assertTrue("ItemGolemPlacer must keep server-side success consume semantics for use-first path",
                source.contains("if (world.isRemote || player.isSneaking()) {")
                        && source.contains("return EnumActionResult.PASS;")
                         && source.contains("return EnumActionResult.SUCCESS;"));
    }

    @Test
    public void golemPlacerModelsShowTheStoredCoreOverlay() throws IOException {
        String placerSource = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemPlacer.java");
        String bellSource = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemBell.java");

        assertTrue("Bell pickup must store the golem core on the placer",
                bellSource.contains("tag.setByte(\"core\", golem.getCore());"));
        assertTrue("ItemGolemPlacer must expose the stored core to item model overrides",
                placerSource.contains("new ResourceLocation(\"thaumcraft\", \"core\")")
                        && placerSource.contains("stack.hasTagCompound() && stack.getTagCompound().hasKey(\"core\")"));

        String[] materials = {"straw", "wood", "tallow", "clay", "flesh", "stone", "iron", "thaumium"};
        for (String material : materials) {
            String modelName = "itemgolemplacer_" + material;
            String baseModel = readFile("src/main/resources/assets/thaumcraft/models/item/" + modelName + ".json");
            String coreModel = readFile("src/main/resources/assets/thaumcraft/models/item/" + modelName + "_core.json");

            assertTrue(modelName + " must select its core-overlay model from the NBT property",
                    baseModel.contains("\"thaumcraft:core\": 1.0")
                            && baseModel.contains("\"model\": \"thaumcraft:item/" + modelName + "_core\""));
            assertTrue(modelName + " core model must retain its material and use the original heart overlay",
                    coreModel.contains("\"layer0\": \"thaumcraft:items/golem_" + material + "\"")
                            && coreModel.contains("\"layer1\": \"thaumcraft:items/golem_over_core\""));
        }
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
