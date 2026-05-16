package thaumcraft.common.items.equipment;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemElementalAxeStaticGuardTest {

    @Test
    public void elementalAxeKeepsMagnetAndBubbleContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/equipment/ItemElementalAxe.java");

        assertTrue("ItemElementalAxe must expose right-click active-use contract",
                source.contains("player.setActiveHand(hand)")
                        && source.contains("getMaxItemUseDuration(ItemStack stack)")
                        && source.contains("return 72000;"));
        assertTrue("ItemElementalAxe must keep thaumium-ingot repair contract",
                source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemElementalAxe must keep item-magnet pull update contract",
                source.contains("EntityUtils.getEntitiesInRange(")
                        && source.contains("itemEntity.motionX -=")
                        && source.contains("clamp(itemEntity.motionX, -0.35D, 0.35D)")
                        && source.contains("Thaumcraft.proxy.crucibleBubble("));
        assertTrue("ItemElementalAxe must keep wood-log chain-break FX packet contract",
                source.contains("!player.isSneaking() && Utils.isWoodLog(world, pos)")
                        && source.contains("BlockUtils.breakFurthestBlock(world, pos, player)")
                        && source.contains("new PacketFXBlockBubble(")
                        && source.contains("PacketHandler.INSTANCE.sendToAllAround(")
                        && source.contains("new NetworkRegistry.TargetPoint(")
                        && source.contains("TCSounds.BUBBLE")
                        && source.contains("stack.damageItem(1, player);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
