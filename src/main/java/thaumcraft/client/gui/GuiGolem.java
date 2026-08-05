package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.golems.ContainerGolem;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.ItemGolemCore;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.Utils;

public class GuiGolem extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guigolem.png");
    private static final int GOLEM_PREVIEW_X = 18;
    private static final int GOLEM_PREVIEW_Y = 45;
    private static final int BLURB_WIDTH = 55;

    private final EntityGolemBase golem;
    private int threat = -1;

    public GuiGolem(EntityPlayer player, EntityGolemBase golem) {
        super(new ContainerGolem(player.inventory, golem));
        this.golem = golem;
        if (this.golem.advanced && this.golem.world.rand.nextInt(4) == 0) {
            this.threat = this.golem.world.rand.nextInt(9);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawColorFilterName(mouseX, mouseY);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String blurbKey = this.threat >= 0 ? "golemthreat." + this.threat + ".text"
                : "golemblurb." + this.golem.getCore() + ".text";
        float blurbScale = this.fontRenderer.getUnicodeFlag() ? 0.875F : 0.5F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(40.0F, 11.0F, 0.0F);
        GlStateManager.scale(blurbScale, blurbScale, 1.0F);
        this.fontRenderer.drawSplitString(I18n.format(blurbKey), 0, 0,
                Math.max(1, (int) (BLURB_WIDTH / blurbScale)), 0xDDDDDD);
        GlStateManager.popMatrix();

        ContainerGolem container = getContainer();
        if (container.maxScroll > 0) {
            String page = (container.currentScroll + 1) + "/" + (container.maxScroll + 1);
            this.fontRenderer.drawString(page, 162, 70, 14540253);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.golem.isDead && this.mc.player != null) {
            this.mc.player.closeScreen();
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        drawInventoryPanels();
        drawCoreSpecificToggles();
        drawSortingToggles();

        GlStateManager.disableBlend();
        GuiInventory.drawEntityOnScreen(this.guiLeft + GOLEM_PREVIEW_X, this.guiTop + GOLEM_PREVIEW_Y, 30,
                (float) (this.guiLeft + GOLEM_PREVIEW_X) - mouseX,
                (float) (this.guiTop + GOLEM_PREVIEW_Y - 50) - mouseY, this.golem);
    }

    private void drawInventoryPanels() {
        if (this.golem.getCore() < 0 || !ItemGolemCore.hasInventory(this.golem.getCore()) || this.golem.inventory == null) {
            return;
        }
        int slots = this.golem.inventory.slotCount;
        int typeLoc = this.golem.getGolemType().ordinal() * 24;
        int visible = Math.min(6, Math.max(0, slots - getContainer().currentScroll * 6));
        for (int a = 0; a < visible; a++) {
            this.drawTexturedModalRect(this.guiLeft + 96 + a / 2 * 28, this.guiTop + 12 + a % 2 * 31, 184, typeLoc, 24, 24);
            if (this.golem.getUpgradeAmount(4) > 0) {
                this.drawTexturedModalRect(this.guiLeft + 96 + a / 2 * 28, this.guiTop + 4 + a % 2 * 31, 72, 168, 24, 12);
                short color = this.golem.getColors(a + getContainer().currentScroll * 6);
                if (color >= 0) {
                    int rgb = Utils.colors[color];
                    GlStateManager.color(((rgb >> 16) & 0xFF) / 255.0F,
                            ((rgb >> 8) & 0xFF) / 255.0F,
                            (rgb & 0xFF) / 255.0F, 1.0F);
                    this.drawTexturedModalRect(this.guiLeft + 105 + a / 2 * 28, this.guiTop + 7 + a % 2 * 31, 0, 176, 6, 6);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        if (slots > 6) {
            if (getContainer().currentScroll > 0) {
                this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 68, 0, 200, 24, 8);
            } else {
                this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 68, 0, 208, 24, 8);
            }
            if (getContainer().currentScroll < getContainer().maxScroll) {
                this.drawTexturedModalRect(this.guiLeft + 135, this.guiTop + 68, 24, 200, 24, 8);
            } else {
                this.drawTexturedModalRect(this.guiLeft + 135, this.guiTop + 68, 24, 208, 24, 8);
            }
        }
    }

    private void drawCoreSpecificToggles() {
        if (this.golem.getCore() == 4 && this.golem.getUpgradeAmount(4) > 0) {
            drawToggle(104, 5, this.golem.canAttackHostiles());
            drawToggle(104, 21, this.golem.canAttackAnimals());
            drawToggle(104, 37, this.golem.canAttackPlayers());
            drawToggle(104, 53, this.golem.canAttackCreepers());
            this.fontRenderer.drawString("Monsters", this.guiLeft + 122, this.guiTop + 6, 0xFFCCCC);
            this.fontRenderer.drawString("Animals", this.guiLeft + 122, this.guiTop + 22, 0xFFFFCC);
            this.fontRenderer.drawString("Players", this.guiLeft + 122, this.guiTop + 38, 0xCCCCFF);
            this.fontRenderer.drawString("Creepers", this.guiLeft + 122, this.guiTop + 54, 0xCCFFCC);
        }

        if (this.golem.getCore() == 0) {
            drawToggle(62, 54, !this.golem.getToggles()[0]);
            drawSmallCenteredText(this.golem.getToggles()[0] ? "Any amount" : "Precise amount", 66, 48, 0xFDFDFD);
        }

        if (this.golem.getCore() == 8) {
            drawToggle(42, 40, !this.golem.getToggles()[0]);
            drawToggle(42, 50, !this.golem.getToggles()[1]);
            drawToggle(42, 60, !this.golem.getToggles()[2]);
            drawSmallText(this.golem.getToggles()[0] ? "Empty space" : "Block", 53, 42, 0xFDFDFD);
            drawSmallText(this.golem.getToggles()[1] ? "Left click" : "Right click", 53, 52, 0xFDFDFD);
            drawSmallText(this.golem.getToggles()[2] ? "Sneaking" : "Not sneaking", 53, 62, 0xFDFDFD);
        }
    }

    private void drawSortingToggles() {
        if (this.golem.getUpgradeAmount(5) <= 0 || !ItemGolemCore.canSort(this.golem.getCore())) return;
        int shiftX = this.golem.getCore() == 10 ? 66 : 180;
        int shiftY = this.golem.getCore() == 10 ? 12 : 0;
        drawToggle(shiftX, 24 + shiftY, this.golem.checkOreDict());
        drawToggle(shiftX, 34 + shiftY, this.golem.ignoreDamage());
        drawToggle(shiftX, 44 + shiftY, this.golem.ignoreNBT());
        drawSmallText("Use Ore dictionary", shiftX + 10, 26 + shiftY,
                this.golem.checkOreDict() ? 0xFDFDFD : 0x666666);
        drawSmallText("Ignore item damage", shiftX + 10, 36 + shiftY,
                this.golem.ignoreDamage() ? 0xFDFDFD : 0x666666);
        drawSmallText("Ignore NBT values", shiftX + 10, 46 + shiftY,
                this.golem.ignoreNBT() ? 0xFDFDFD : 0x666666);
    }

    private void drawSmallText(String text, int x, int y, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft + x, this.guiTop + y, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        UtilsFX.drawCompactString(this.fontRenderer, text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void drawSmallCenteredText(String text, int x, int y, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft + x, this.guiTop + y, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        UtilsFX.drawCompactCenteredString(this.fontRenderer, text, 0.0F, 0, color);
        GlStateManager.popMatrix();
    }

    private void drawToggle(int x, int y, boolean enabled) {
        this.drawTexturedModalRect(this.guiLeft + x, this.guiTop + y, 8, 168, 8, 8);
        if (enabled) {
            this.drawTexturedModalRect(this.guiLeft + x, this.guiTop + y, 8, 176, 8, 8);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (clickInventoryColorControls(mouseX, mouseY)) return;
        if (clickInventoryScrollControls(mouseX, mouseY)) return;
        if (clickCoreToggleControls(mouseX, mouseY)) return;
        clickSortToggleControls(mouseX, mouseY);
    }

    private boolean clickInventoryColorControls(int mouseX, int mouseY) {
        if (this.golem.getCore() < 0 || !ItemGolemCore.hasInventory(this.golem.getCore()) || this.golem.inventory == null) {
            return false;
        }
        if (this.golem.getUpgradeAmount(4) <= 0) return false;

        int slots = this.golem.inventory.slotCount;
        int visible = Math.min(6, Math.max(0, slots - getContainer().currentScroll * 6));
        for (int a = 0; a < visible; a++) {
            int slotIndex = a + getContainer().currentScroll * 6;
            if (isMouseIn(mouseX, mouseY, 96 + a / 2 * 28, 4 + a % 2 * 31, 8, 12)) {
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, slotIndex);
                return true;
            }
            if (isMouseIn(mouseX, mouseY, 112 + a / 2 * 28, 4 + a % 2 * 31, 8, 12)) {
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, slotIndex + slots);
                return true;
            }
        }
        return false;
    }

    private boolean clickInventoryScrollControls(int mouseX, int mouseY) {
        if (this.golem.inventory == null || this.golem.inventory.slotCount <= 6) return false;
        if (isMouseIn(mouseX, mouseY, 111, 68, 24, 8) && getContainer().currentScroll > 0) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 66);
            getContainer().currentScroll--;
            getContainer().refreshInventory(this.mc.player.inventory);
            return true;
        }
        if (isMouseIn(mouseX, mouseY, 135, 68, 24, 8) && getContainer().currentScroll < getContainer().maxScroll) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 67);
            getContainer().currentScroll++;
            getContainer().refreshInventory(this.mc.player.inventory);
            return true;
        }
        return false;
    }

    private boolean clickCoreToggleControls(int mouseX, int mouseY) {
        if (this.golem.getCore() == 4 && this.golem.getUpgradeAmount(4) > 0) {
            if (isMouseIn(mouseX, mouseY, 104, 5, 8, 8)) return sendButton(51);
            if (isMouseIn(mouseX, mouseY, 104, 21, 8, 8)) return sendButton(52);
            if (isMouseIn(mouseX, mouseY, 104, 37, 8, 8)) return sendButton(53);
            if (isMouseIn(mouseX, mouseY, 104, 53, 8, 8)) return sendButton(54);
        }
        if (this.golem.getCore() == 0 && isMouseIn(mouseX, mouseY, 62, 54, 8, 8)) {
            return sendButton(50);
        }
        if (this.golem.getCore() == 8) {
            if (isMouseIn(mouseX, mouseY, 42, 40, 8, 8)) return sendButton(50);
            if (isMouseIn(mouseX, mouseY, 42, 50, 8, 8)) return sendButton(51);
            if (isMouseIn(mouseX, mouseY, 42, 60, 8, 8)) return sendButton(52);
        }
        return false;
    }

    private boolean clickSortToggleControls(int mouseX, int mouseY) {
        if (this.golem.getUpgradeAmount(5) <= 0 || !ItemGolemCore.canSort(this.golem.getCore())) return false;
        int shiftX = this.golem.getCore() == 10 ? 66 : 180;
        int shiftY = this.golem.getCore() == 10 ? 12 : 0;
        for (int i = 0; i < 3; i++) {
            if (isMouseIn(mouseX, mouseY, shiftX, 24 + i * 10 + shiftY, 64, 8)) {
                return sendButton(55 + i);
            }
        }
        return false;
    }

    private boolean sendButton(int id) {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, id);
        return true;
    }

    private ContainerGolem getContainer() {
        return (ContainerGolem) this.inventorySlots;
    }

    private void drawColorFilterName(int mouseX, int mouseY) {
        if (this.golem.getCore() < 0 || !ItemGolemCore.hasInventory(this.golem.getCore())
                || this.golem.inventory == null || this.golem.getUpgradeAmount(4) <= 0) return;

        int slots = this.golem.inventory.slotCount;
        int visible = Math.min(6, Math.max(0, slots - getContainer().currentScroll * 6));
        for (int a = 0; a < visible; a++) {
            if (!isMouseIn(mouseX, mouseY, 96 + a / 2 * 28, 4 + a % 2 * 31, 24, 12)) continue;
            short color = this.golem.getColors(a + getContainer().currentScroll * 6);
            String text = color >= 0 ? Utils.colorNames[color] : "Any color";
            this.fontRenderer.drawString(text, this.guiLeft + 133 - this.fontRenderer.getStringWidth(text) / 2,
                    this.guiTop - 6, 0xFDFDFD);
            return;
        }
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }
}
