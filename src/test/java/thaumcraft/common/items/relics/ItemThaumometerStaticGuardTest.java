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
                        && source.contains("completedClientSide || isNodeScan(current)")
                        && source.contains("PacketHandler.INSTANCE.sendToServer(new PacketScannedToServer(current, player, \"@\"))"));
        assertTrue("Thaumometer node scans should reach the server even when client-side node aspects are not synced yet",
                source.contains("private static boolean isNodeScan(ScanResult result)")
                        && source.contains("if (isNodeScan(result)) {")
                        && source.contains("this.showScanFeedback(world, player, result);")
                        && source.contains("return result;"));
        assertTrue("Thaumometer block scan must keep protected pick-block path with aspect-aware candidate ordering",
                source.contains("try {")
                        && source.contains("result = toTaggedItemScan(block.getPickBlock(state, hit, world, pos, player), world);")
                        && source.contains("} catch (Exception ignored)")
                        && source.contains("BlockUtils.createStackedBlock(block, dropMeta), world);")
                        && source.contains("if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)"));
        assertTrue("Thaumometer should keep the original permissive pointed-entity route and separate raw target selection from validated scan completion",
                source.contains("public ScanResult findRawScanTarget(ItemStack stack, World world, EntityPlayer player)")
                        && source.contains("EntityUtils.getPointedEntity(world, player, 0.5D, 10.0D, 0.0F, true)")
                        && source.contains("return new ScanResult((byte)2, 0, 0, pointed, \"\");")
                        && source.contains("return ScanManager.isValidScanTarget(player, result, \"@\") ? result : null;"));
        assertTrue("Thaumometer should scan aura nodes through a slightly expanded node-only ray box while still respecting first solid block occlusion",
                source.contains("public static TileEntity findLookedAtNodeTile(World world, EntityPlayer player, double range)")
                        && source.contains("world.rayTraceBlocks(eyes, end, false, true, false)")
                        && source.contains("eyes.distanceTo(blockHit.hitVec) + 0.25D")
                        && source.contains("TileEntity lookedAtNode = findLookedAtNodeTile(world, player, 10.0D);")
                        && source.contains("AxisAlignedBB scanBox = new AxisAlignedBB("));
        assertTrue("Thaumometer should restore client scan feedback through block runes for entity and block/node targets",
                source.contains("private void showScanFeedback(World world, EntityPlayer player, ScanResult result)")
                        && source.contains("Thaumcraft.proxy.blockRunes(")
                        && source.contains("entity.posX - 0.5D")
                        && source.contains("result.phenomena != null && result.phenomena.startsWith(\"NODE\")")
                        && source.contains("pos.getY() + 0.25D"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
