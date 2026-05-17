package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemCompassStoneStaticGuardTest {

    @Test
    public void compassStoneShouldKeepReferenceActiveVisibilityAndCleanupContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemCompassStone.java");

        assertTrue(source.contains("public static final HashMap<WorldCoordinates, Long> sinisterNodes = new HashMap<>();"));
        assertTrue(source.contains("private static boolean isSinisterVisible(World world, Entity entity)"));
        assertTrue(source.contains("long cutoff = System.currentTimeMillis() - 10000L;"));
        assertTrue(source.contains("if (entry.getValue() < cutoff)"));
        assertTrue(source.contains("it.remove();"));
        assertTrue(source.contains("coordinates.dim == dim"));
        assertTrue(source.contains("Math.cos((double) fov / 2.0D)"));
    }

    @Test
    public void compassStoneShouldKeepReferencePassiveUseAndCreativeEntryContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemCompassStone.java");

        assertTrue(source.contains("public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)"));
        assertTrue(source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue(source.contains("public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)"));
        assertTrue(source.contains("items.add(new ItemStack(this, 1, 0));"));
        assertTrue(source.contains("return EnumRarity.RARE;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
