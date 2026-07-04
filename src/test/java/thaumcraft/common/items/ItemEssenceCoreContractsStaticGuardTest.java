package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemEssenceCoreContractsStaticGuardTest {

    @Test
    public void essencePhialKeepsReferenceFillEmptyAndClientResultContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemEssence.java");
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String filledModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemessence.json");
        String emptyModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemessence_empty.json");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemEssence must keep phial subtype and amount contracts",
                source.contains("PHIAL_AMOUNT = 8")
                        && source.contains("items.add(new ItemStack(this, 1, 0));")
                        && source.contains("new ItemStack(this, 1, 1)"));
        assertTrue("ItemEssence must keep fill/empty container paths",
                source.contains("fillPhialFromContainer(")
                        && source.contains("emptyPhialIntoJar(")
                        && source.contains("container.takeFromContainer(aspect, PHIAL_AMOUNT)")
                        && source.contains("jar.addToContainer(aspect, PHIAL_AMOUNT) == 0"));
        assertTrue("ItemEssence must keep client preview swing with PASS result contracts",
                source.contains("if (world.isRemote) {")
                        && source.contains("player.swingArm(hand);")
                        && source.contains("return EnumActionResult.PASS;"));
        assertTrue("ItemEssence tooltip/localization contracts must remain available",
                source.contains("new TextComponentTranslation(\"tc.aspect.unknown\")")
                        && lang.contains("item.thaumcraft.essence.0.name=Glass Phial")
                        && lang.contains("item.thaumcraft.essence.1.name=Phial of Essentia")
                        && lang.contains("tc.aspect.unknown=Unknown Aspect"));
        assertTrue("Filled phials should render like TC4: glass phial base plus animated colored essentia overlay",
                filledModel.contains("\"layer0\": \"thaumcraft:items/phial\"")
                        && filledModel.contains("\"layer1\": \"thaumcraft:items/essence\"")
                        && source.contains("if (stack.getItemDamage() == 0 || tintIndex == 0) return 0xFFFFFF;")
                        && source.contains("? aspects.getAspects()[0].getColor()"));
        assertTrue("Empty phials should not render the essentia overlay",
                emptyModel.contains("\"layer0\": \"thaumcraft:items/phial\"")
                        && !emptyModel.contains("layer1"));
        assertTrue("Client model registration should route meta 0 to empty phial and filled metas to layered phial",
                clientProxy.contains("if (item == ConfigItems.itemEssence)")
                        && clientProxy.contains("new ResourceLocation(\"thaumcraft\", \"itemessence_empty\")")
                        && clientProxy.contains("new ResourceLocation(\"thaumcraft\", \"itemessence\")")
                        && clientProxy.contains("ModelLoader.setCustomModelResourceLocation(item, 0, emptyPhialModel);")
                        && clientProxy.contains("for (int meta = 1; meta < 64; meta++)")
                        && clientProxy.contains("ModelLoader.setCustomModelResourceLocation(item, meta, filledPhialModel);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
