package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemKeyCoreContractsStaticGuardTest {

    @Test
    public void itemKeyKeepsReferenceClientPassAndServerConsumeUseFirstContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemKey.java");

        assertTrue("ItemKey must keep PASS on remote link/access mutation branches",
                source.contains("if (world.isRemote) return EnumActionResult.PASS;")
                        && source.contains("if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(\"location\"))")
                        && source.contains("if (sameLocation && !playerName.equals(owned.owner) && !owned.accessList.contains(exactAccess) && !owned.accessList.contains(goldAccess))"));
        assertTrue("ItemKey must keep remote PASS / server SUCCESS terminal use-first contract",
                source.contains("return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
