package thaumcraft.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class GuiArcaneWorkbench extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_arcaneworkbench.png");
    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer playerInventory;
    private final int[][] aspectLocations = new int[][]{
            {72, 21}, {24, 43}, {24, 102}, {72, 124}, {120, 102}, {120, 43}
    };
    private final ArrayList<Aspect> primals = Aspect.getPrimalAspects();

    public GuiArcaneWorkbench(InventoryPlayer playerInventory, TileArcaneWorkbench tile) {
        super(new ContainerArcaneWorkbench(playerInventory, tile));
        this.tileEntity = tile;
        this.playerInventory = playerInventory;
        this.xSize = 190;
        this.ySize = 234;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.disableBlend();

        ItemStack wandStack = this.tileEntity.getStackInSlot(10);
        ItemWandCasting wand = !wandStack.isEmpty() && wandStack.getItem() instanceof ItemWandCasting
                ? (ItemWandCasting) wandStack.getItem() : null;

        ItemStack result = ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.playerInventory.player);
        AspectList cost = result.isEmpty() ? null : ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.tileEntity, this.playerInventory.player);
        if (cost != null) {
            drawArcaneCostTags(cost, wandStack, wand);
        }
        if (wand != null && cost != null && !wand.consumeAllVisCrafting(wandStack, this.playerInventory.player, cost, false)) {
            drawInsufficientVisResult(result);
        }
    }

    private void drawArcaneCostTags(AspectList cost, ItemStack wandStack, ItemWandCasting wand) {
        for (int i = 0; i < this.primals.size() && i < this.aspectLocations.length; i++) {
            Aspect primal = this.primals.get(i);
            int amount = cost.getAmount(primal);
            if (amount <= 0 || primal == null) continue;

            float drawAmount = amount;
            float alpha = 0.5F + (MathHelper.sin((float) (this.playerInventory.player.ticksExisted + i * 10) / 2.0F) * 0.2F - 0.2F);
            if (wand != null) {
                drawAmount *= wand.getConsumptionModifier(wandStack, this.playerInventory.player, primal, true);
                if (drawAmount * 100.0F <= wand.getVis(wandStack, primal)) {
                    alpha = 1.0F;
                }
            }

            int x = this.guiLeft + this.aspectLocations[i][0] - 8;
            int y = this.guiTop + this.aspectLocations[i][1] - 8;
            this.mc.getTextureManager().bindTexture(primal.getImage());
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16.0F, 16.0F);

            String label = formatAspectCost(drawAmount);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (x + 18), (float) (y + 10), 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            this.fontRenderer.drawStringWithShadow(label, 0, 0, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawInsufficientVisResult(ItemStack result) {
        if (result.isEmpty()) return;

        GlStateManager.pushMatrix();
        GlStateManager.color(0.33F, 0.33F, 0.33F, 0.66F);
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemAndEffectIntoGUI(result, this.guiLeft + 160, this.guiTop + 64);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, this.guiLeft + 160, this.guiTop + 64, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.guiLeft + 168), (float) (this.guiTop + 46), 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        String text = "Insufficient vis";
        this.fontRenderer.drawString(text, -this.fontRenderer.getStringWidth(text) / 2, 0, 0xEE6E6E);
        GlStateManager.popMatrix();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static String formatAspectCost(float amount) {
        if (Math.abs(amount - Math.round(amount)) < 0.05F) {
            return Integer.toString(Math.round(amount));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", amount);
    }
}
