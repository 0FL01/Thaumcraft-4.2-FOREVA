package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerWorldCraftingReturnStaticGuardTest {

    @Test
    public void craftedSpecialReturnHooksStayWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java");

        assertTrue("Crafted alumentum path must keep essence-slot return hook",
                source.contains("event.crafting.getItem() == ConfigItems.itemResource")
                        && source.contains("event.crafting.getItemDamage() == 13")
                        && source.contains("stack.getItem() instanceof ItemEssence")
                        && source.contains("stack.grow(1);")
                        && source.contains("event.craftMatrix.setInventorySlotContents(slot, stack)"));
        assertTrue("Crafted bellows path must keep center-slot return hook for blockMetalDevice meta 3",
                source.contains("event.crafting.getItem() == Item.getItemFromBlock(ConfigBlocks.blockMetalDevice)")
                        && source.contains("event.crafting.getItemDamage() == 3")
                        && source.contains("event.craftMatrix.getStackInSlot(4)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
