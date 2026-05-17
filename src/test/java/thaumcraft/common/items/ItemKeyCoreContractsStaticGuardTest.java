package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemKeyCoreContractsStaticGuardTest {

    @Test
    public void itemKeyKeepsReferenceDoorAndWoodenDeviceContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemKey.java");

        assertTrue("ItemKey must keep arcane door and wooden-device use-first routing",
                source.contains("if (block == ConfigBlocks.blockArcaneDoor)")
                        && source.contains("int fullMeta = block.getMetaFromState(state);")
                        && source.contains("if ((fullMeta & 8) != 0)")
                        && source.contains("} else if (block == ConfigBlocks.blockWoodenDevice)"));
        assertTrue("ItemKey must keep PASS on remote link/access mutation branches",
                source.contains("if (world.isRemote) return EnumActionResult.PASS;")
                        && source.contains("if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(\"location\"))")
                        && source.contains("if (sameLocation && !playerName.equals(owned.owner) && !owned.accessList.contains(exactAccess) && !owned.accessList.contains(goldAccess))"));
        assertTrue("ItemKey must keep type-specific localization for link/grant feedback",
                source.contains("sendKeyMessage(player, type == 0 ? \"tc.key1\" : \"tc.key2\");")
                        && source.contains("type == 0 ? \"tc.key3\" : \"tc.key5\"")
                        && source.contains("type == 0 ? \"tc.key4\" : \"tc.key6\""));
        assertTrue("ItemKey must keep linked arcane-door half access propagation",
                source.contains("if (type == 0 && linkedPos != null)")
                        && source.contains("TileEntity linkedTile = world.getTileEntity(linkedPos);")
                        && source.contains("linkedOwned.accessList.add(exactAccess);"));
        assertTrue("ItemKey must keep remote PASS / server SUCCESS terminal use-first contract",
                source.contains("return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
