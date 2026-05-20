package thaumcraft.common.container;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ContainerArcaneWorkbenchArcaneFlowStaticGuardTest {

    @Test
    public void containerArcaneWorkbenchShouldRetainArcaneOutputComputationFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java");

        assertTrue("Container should route arcane aspect cost probe through ThaumcraftCraftingManager",
                source.contains("AspectList cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.tileEntity, this.playerInventory.player);"));
        assertTrue("Container should probe wand vis consumption with simulate=false before setting arcane output",
                source.contains("wand.consumeAllVisCrafting(wandStack, this.playerInventory.player, cost, false)"));
        assertTrue("Container should set output slot from ThaumcraftCraftingManager.findMatchingArcaneRecipe",
                source.contains("ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.playerInventory.player)"));
        assertTrue("Container should keep a detached preview craft matrix so recomputing slot 9 does not recurse back through InventoryCrafting callbacks",
                source.contains("new InventoryCrafting(new ContainerDummy(), 3, 3)")
                        && source.contains("private static final class ContainerDummy extends Container"));
        assertTrue("Container should not add an extra staff-only preview gate beyond the reference wand-slot contract",
                !source.contains("wand.isStaff(wandStack)"));
    }

    @Test
    public void containerArcaneWorkbenchShouldRetainWorkbenchWandSlotAndTransferContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java");

        assertTrue("Container should keep dedicated wand slot index 10 via SlotLimitedByWand",
                source.contains("new SlotLimitedByWand(this.tileEntity, 10, 160, 24)"));
        assertTrue("Shift-click routing should preserve wand merge target slot [1,2) and output-to-player flow [11,47)",
                source.contains("if (index == 0)")
                        && source.contains("this.mergeItemStack(stack, 11, 47, true)")
                        && source.contains("this.mergeItemStack(stack, 1, 2, false)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
