package thaumcraft.common.container;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ArcaneWorkbenchStage9bRuntimeSurfaceStaticGuardTest {

    @Test
    public void arcaneWorkbenchStage9bSurfaceShouldKeepServerClientContainerAndSlotContracts() throws IOException {
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String container = read("src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java");
        String slot = read("src/main/java/thaumcraft/common/container/SlotCraftingArcaneWorkbench.java");
        String matcher = read("src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java");

        assertTrue("CommonProxy should keep Arcane Workbench server GUI routing to ContainerArcaneWorkbench",
                commonProxy.contains("case GUI_ARCANE_WORKBENCH:")
                        && commonProxy.contains("tile instanceof TileArcaneWorkbench")
                        && commonProxy.contains("new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)"));
        assertTrue("ClientProxy should keep Arcane Workbench client GUI routing to GuiArcaneWorkbench behind the WorldClient guard",
                clientProxy.contains("if (!(world instanceof WorldClient)) {")
                        && clientProxy.contains("case GUI_ARCANE_WORKBENCH:")
                        && clientProxy.contains("new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)"));

        assertTrue("ContainerArcaneWorkbench should keep output slot, wand slot, vanilla-first output path, and arcane matcher probe",
                container.contains("new SlotCraftingArcaneWorkbench(playerInventory.player, this.tileEntity, this.tileEntity, 9, 160, 64)")
                        && container.contains("new SlotLimitedByWand(this.tileEntity, 10, 160, 24)")
                        && container.contains("CraftingManager.findMatchingResult(this.craftMatrix, this.tileEntity.getWorld())")
                        && container.contains("AspectList cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.tileEntity, this.playerInventory.player);")
                        && container.contains("wand.consumeAllVisCrafting(wandStack, this.playerInventory.player, cost, false)")
                        && container.contains("ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.playerInventory.player)")
                        && !container.contains("wand.isStaff(wandStack)"));
        assertTrue("SlotCraftingArcaneWorkbench should keep real vis consumption plus reference-shaped container-item remainder handling",
                slot.contains("AspectList cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.craftMatrix, this.thePlayer);")
                        && slot.contains("wand.consumeAllVisCrafting(wandStack, player, cost, true);")
                        && slot.contains("input.getItem().hasContainerItem(input)")
                        && slot.contains("ItemStack remainder = input.getItem().getContainerItem(input);")
                        && slot.contains("MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this.thePlayer, remainder, EnumHand.MAIN_HAND));")
                        && slot.contains("this.thePlayer.inventory.addItemStackToInventory(remainder.copy())")
                        && slot.contains("this.craftMatrix.setInventorySlotContents(i, remainder);")
                        && slot.contains("this.thePlayer.dropItem(remainder, false);"));
        assertTrue("ThaumcraftCraftingManager should keep public arcane matcher methods used by the workbench path",
                matcher.contains("public static ItemStack findMatchingArcaneRecipe(IInventory awb, EntityPlayer player)")
                        && matcher.contains("public static AspectList findMatchingArcaneRecipeAspects(IInventory awb, EntityPlayer player)")
                        && matcher.contains("for (Object recipe : ThaumcraftApi.getCraftingRecipes())")
                        && matcher.contains("if (!(recipe instanceof IArcaneRecipe)) continue;"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
