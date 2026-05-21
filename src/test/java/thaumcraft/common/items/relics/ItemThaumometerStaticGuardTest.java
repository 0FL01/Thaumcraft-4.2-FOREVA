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
                source.contains("this.startScan = doActiveScan(stack, world, player, true);")
                        && source.contains("player.setActiveHand(hand);"));
        assertTrue("Thaumometer active scanning should validate aspect prerequisites once per attempt and keep later use-ticks notification-free",
                source.contains("private ScanResult doActiveScan(ItemStack stack, World world, EntityPlayer player, boolean notifyInvalid)")
                        && source.contains("if (notifyInvalid) {")
                        && source.contains("ScanManager.notifyInvalidScan(aspects, player);")
                        && source.contains("ScanResult current = doActiveScan(stack, world, player, false);"));
        assertTrue("Thaumometer must keep client completion and packet send path",
                source.contains("if (this.startScan != null && current != null && current.equals(this.startScan))")
                        && 
                source.contains("if (count <= 5)")
                        && source.contains("player.stopActiveHand();")
                        && source.contains("ScanManager.completeScan(player, current, \"@\")")
                        && source.contains("PacketHandler.INSTANCE.sendToServer(new PacketScannedToServer(current, player, \"@\"))"));
        assertTrue("Thaumometer block scan must keep protected pick-block path with fallback stack creation",
                source.contains("try {")
                        && source.contains("target = block.getPickBlock(state, hit, world, pos, player);")
                        && source.contains("} catch (Exception ignored)")
                        && source.contains("target = BlockUtils.createStackedBlock(block, meta);"));
        assertTrue("Thaumometer should keep the original permissive pointed-entity route and separate raw target selection from validated scan completion",
                source.contains("public ScanResult findRawScanTarget(ItemStack stack, World world, EntityPlayer player)")
                        && source.contains("EntityUtils.getPointedEntity(world, player, 0.5D, 10.0D, 0.0F, true)")
                        && source.contains("return new ScanResult((byte)2, 0, 0, pointed, \"\");")
                        && source.contains("return ScanManager.isValidScanTarget(player, result, \"@\") ? result : null;"));
        assertTrue("Thaumometer should restore client scan feedback through block runes for entity and block/node targets",
                source.contains("private void showScanFeedback(World world, EntityPlayer player, ScanResult result)")
                        && source.contains("Thaumcraft.proxy.blockRunes(")
                        && source.contains("entity.posX - 0.5D")
                        && source.contains("pos.getY() + 0.25D"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
