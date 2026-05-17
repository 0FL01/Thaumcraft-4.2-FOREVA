package thaumcraft.common.items.baubles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemAmuletVisCoreContractsStaticGuardTest {

    @Test
    public void itemAmuletVisKeepsReferenceTooltipAndRelayDrainContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/baubles/ItemAmuletVis.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemAmuletVis must keep vis-relay proximity cache and relay-drain hooks",
                source.contains("WeakReference<TileVisRelay> relayRef = TileVisRelay.nearbyPlayers.get(player.getEntityId());")
                        && source.contains("int drained = relay.consumeVis(aspect, amount);")
                        && source.contains("relay.triggerConsumeEffect(aspect);"));
        assertTrue("ItemAmuletVis must keep tooltip capacity and vis-amount formatting hooks",
                source.contains("I18n.translateToLocal(\"item.capacity.text\")")
                        && source.contains("VIS_FORMAT.format((float) stack.getTagCompound().getInteger(aspect.getTag()) / 100.0F)")
                        && source.contains("I18n.translateToLocal(\"item.ItemAmuletVis.text\")"));
        assertTrue("ItemAmuletVis tooltip localization keys must exist in en_us.lang",
                lang.contains("item.capacity.text=Capacity")
                        && lang.contains("item.ItemAmuletVis.text=Slowly recharges held wands"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
