package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemThaumometerStaticGuardTest {

    @Test
    public void thaumometerKeepsUseDurationAndClientScanCompletionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java");

        assertTrue("Thaumometer must keep reference use duration and none-use action contracts",
                source.contains("public int getMaxItemUseDuration(ItemStack stack)")
                        && source.contains("return 25;")
                        && source.contains("public EnumAction getItemUseAction(ItemStack stack)")
                        && source.contains("return EnumAction.NONE;"));
        assertTrue("Thaumometer must keep start-scan capture on right-click and active-hand use flow",
                source.contains("this.startScan = doScan(stack, world, player);")
                        && source.contains("player.setActiveHand(hand);"));
        assertTrue("Thaumometer must keep client completion and packet send path",
                source.contains("if (this.startScan != null && current != null && current.equals(this.startScan))")
                        && 
                source.contains("if (count <= 5)")
                        && source.contains("ScanManager.completeScan(player, current, \"@\")")
                        && source.contains("PacketHandler.INSTANCE.sendToServer(new PacketScannedToServer(current, player, \"@\"))"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
