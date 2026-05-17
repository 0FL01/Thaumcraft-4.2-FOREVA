package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemHoverHarnessCoreContractsStaticGuardTest {

    @Test
    public void hoverHarnessKeepsReferenceCoreContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/armor/ItemHoverHarness.java");

        assertTrue("ItemHoverHarness must keep epic rarity, iron repair and vis-discount contracts",
                source.contains("return EnumRarity.EPIC;")
                        && source.contains("repair.getItem() == Items.IRON_INGOT")
                        && source.contains("return aspect == Aspect.AIR ? 5 : 2;"));
        assertTrue("ItemHoverHarness must keep hover tick gate and handler dispatch",
                source.contains("if (!player.capabilities.isCreativeMode)")
                        && source.contains("Hover.handleHoverArmor(player, stack);"));
        assertTrue("ItemHoverHarness must keep server-side GUI open with floored coordinates",
                source.contains("player.openGui(Thaumcraft.instance, CommonProxy.GUI_HOVER_HARNESS, world,")
                        && source.contains("MathHelper.floor(player.posX)")
                        && source.contains("MathHelper.floor(player.posY)")
                        && source.contains("MathHelper.floor(player.posZ)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
