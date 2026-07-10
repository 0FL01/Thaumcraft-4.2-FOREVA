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

        assertTrue("ContainerArcaneWorkbench should keep a player-local result slot and unified authoritative resolver",
                container.contains("new SlotCraftingArcaneWorkbench(playerInventory.player, this.tileEntity, this.craftResult, this, 0, 160, 64)")
                        && container.contains("new SlotLimitedByWand(this.tileEntity, 10, 160, 24)")
                        && container.contains("ArcaneWorkbenchRecipeResolver.resolve")
                        && container.contains("this.craftResult.setRecipeUsed(this.resolution.vanillaRecipe)")
                        && container.contains("fresh.vanillaRecipe.getRemainingItems(this.craftMatrix)")
                        && container.contains("ForgeHooks.defaultRecipeGetRemainingItems(this.craftMatrix)")
                        && container.contains("consumeAllVisCrafting(wandStack, player, fresh.cost, true)"));
        int onTake = slot.indexOf("public ItemStack onTake(EntityPlayer player, ItemStack stack)");
        assertTrue("SlotCraftingArcaneWorkbench should require server revalidation before firing crafting hooks",
                slot.contains("this.container.canTakeResult(playerIn, this.getStack())")
                        && slot.contains("this.container.prepareCraft(player, stack)")
                        && slot.indexOf("this.container.prepareCraft(player, stack)", onTake)
                        < slot.indexOf("this.onCrafting(stack)", onTake));
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
