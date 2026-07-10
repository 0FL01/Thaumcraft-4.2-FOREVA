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

    @Test
    public void creativeTabShouldIncludeSilverwoodThaumiumSceptre() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("setRod(sceptre, WandRod.rods.get(\"silverwood\"))"));
        assertTrue(source.contains("setCap(sceptre, WandCap.caps.get(\"thaumium\"))"));
        assertTrue(source.contains("sceptre.getTagCompound().setByte(\"sceptre\", (byte) 1)"));
        assertTrue(source.contains("setVis(sceptre, aspect, getMaxVis(sceptre))"));
        assertTrue(source.contains("items.add(sceptre)"));
    }
}
