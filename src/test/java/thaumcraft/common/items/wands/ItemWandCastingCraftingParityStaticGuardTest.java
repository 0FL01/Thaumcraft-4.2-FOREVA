package thaumcraft.common.items.wands;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemWandCastingCraftingParityStaticGuardTest {
    @Test
    public void craftingVisShouldKeepTc4ModifierAndCreativeContracts() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java")), StandardCharsets.UTF_8);
        int consumeStart = source.indexOf("public boolean consumeAllVis(ItemStack stack");
        int consumeEnd = source.indexOf("public boolean consumeAllVisCrafting", consumeStart);
        String consume = source.substring(consumeStart, consumeEnd);

        assertTrue(source.contains("discount = cap.getSpecialCostModifier();"));
        assertFalse(source.contains("discount = Math.min(discount, cap.getSpecialCostModifier())"));
        assertFalse(consume.contains("player.capabilities.isCreativeMode"));
    }
}
