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

        assertTrue("ItemGolemPlacer must keep sneak-bypass contract",
                source.contains("public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)"));
        assertTrue("ItemGolemPlacer must keep core/advanced/upgrades/markers/deco tooltip contracts",
                source.contains("I18n.translateToLocal(\"item.ItemGolemCore.name\")")
                        && source.contains("I18n.translateToLocal(\"tc.adv\")")
                        && source.contains("I18n.translateToLocal(\"item.ItemGolemUpgrade.\" + b + \".name\")")
                        && source.contains("I18n.translateToLocal(\"tc.markedloc\")")
                        && source.contains("I18n.translateToLocal(\"item.ItemGolemDecoration.6.name\")"));
        assertTrue("ItemGolemPlacer must keep server-side success consume semantics for use-first path",
                source.contains("if (world.isRemote || player.isSneaking()) {")
                        && source.contains("return EnumActionResult.PASS;")
                        && source.contains("return EnumActionResult.SUCCESS;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
