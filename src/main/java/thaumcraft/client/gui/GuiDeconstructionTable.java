package thaumcraft.client.gui;

import java.io.IOException;
import java.util.Arrays;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.ContainerDeconstructionTable;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class GuiDeconstructionTable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_decontable.png");

    private final TileDeconstructionTable table;

    public GuiDeconstructionTable(InventoryPlayer playerInventory, TileDeconstructionTable table) {
        super(new ContainerDeconstructionTable(playerInventory, table));
        this.table = table;
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

        if (this.table.breaktime > 0) {
            int progress = this.table.getBreakTimeScaled(46);
            this.drawTexturedModalRect(this.guiLeft + 93, this.guiTop + 15 + 46 - progress,
                    176, 46 - progress, 9, progress);
        }

        Aspect aspect = this.table.aspect;
        if (aspect != null) {
            this.mc.getTextureManager().bindTexture(aspect.getImage());
            this.drawModalRectWithCustomSizedTexture(this.guiLeft + 64, this.guiTop + 48,
                    0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
            this.mc.getTextureManager().bindTexture(TEXTURE);
        }
        GlStateManager.disableBlend();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Aspect aspect = this.table.aspect;
        if (aspect != null && isMouseIn(mouseX, mouseY, 64, 48, 16, 16)) {
            this.drawHoveringText(Arrays.asList(aspect.getName(), aspect.getLocalizedDescription()), mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.table.aspect != null && isMouseIn(mouseX, mouseY, 64, 48, 16, 16)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
            this.playButtonAspect();
        }
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }

    private void playButtonAspect() {
        if (this.mc.player != null) {
            this.mc.player.playSound(TCSounds.HHOFF, 0.2F, 1.0F + this.mc.player.world.rand.nextFloat() * 0.1F);
        }
    }
}
