package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemGolemBellContractsStaticGuardTest {

    @Test
    public void golemBellKeepsReferenceLinkAndSideResultContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemBell.java");

        assertTrue("ItemGolemBell must keep golem-home helper accessors backed by bell NBT keys",
                source.contains("public static int getGolemHomeFace(ItemStack stack)")
                        && source.contains("stack.getTagCompound().hasKey(\"golemhomeface\")")
                        && source.contains("public static BlockPos getGolemHomeCoords(ItemStack stack)")
                        && source.contains("stack.getTagCompound().hasKey(\"golemhomex\")"));
        assertTrue("ItemGolemBell must keep side-aware use-first result semantics",
                source.contains("return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;"));
        assertTrue("ItemGolemBell must keep creative copy-back behavior after golem link capture",
                source.contains("if (player.capabilities.isCreativeMode)")
                        && source.contains("player.setHeldItem(hand, stack.copy());"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
