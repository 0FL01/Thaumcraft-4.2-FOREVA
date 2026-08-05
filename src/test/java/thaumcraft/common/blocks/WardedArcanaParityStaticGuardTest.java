package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WardedArcanaParityStaticGuardTest {
    @Test
    public void pressurePlateReportsEachTc4Setting() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");

        assertTrue(source.contains("tc.pressureplate.everything"));
        assertTrue(source.contains("tc.pressureplate.except_owner"));
        assertTrue(source.contains("tc.pressureplate.owner_only"));
        assertTrue(source.contains("playerIn.sendMessage(new TextComponentTranslation(feedback));"));
    }

    @Test
    public void ironAndGoldKeysUseTheirOwnModels() throws IOException {
        String client = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String iron = read("src/main/resources/assets/thaumcraft/models/item/arcanedoorkey_iron.json");
        String gold = read("src/main/resources/assets/thaumcraft/models/item/arcanedoorkey.json");

        assertTrue(client.contains("if (item == ConfigItems.itemKey) {"));
        assertTrue(client.contains("new ResourceLocation(\"thaumcraft\", \"arcanedoorkey_iron\")"));
        assertTrue(client.contains("ModelLoader.setCustomModelResourceLocation(item, 0, iron);"));
        assertTrue(client.contains("for (int meta = 1; meta < 64; meta++)"));
        assertTrue(iron.contains("thaumcraft:items/keyiron"));
        assertTrue(gold.contains("thaumcraft:items/keygold"));
    }

    @Test
    public void doorAndPlateDecodeOnlyRealKeyAccessEntries() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockArcaneDoor.java");

        assertTrue(source.contains("Set<String> identities = collectIdentities(door);"));
        assertTrue(source.contains("for (String identity : collectIdentities(plate))"));
        assertTrue(source.contains("access.charAt(0) == '0' || access.charAt(0) == '1'"));
        assertTrue(source.contains("identities.add(access.substring(1));"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
