package thaumcraft.client.gui;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileFocalManipulator;

public class GuiFocalManipulator extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_wandtable.png");

    // Vis is stored internally as centi-vis (100 = 1.0 Vis); display values must be
    // divided by 100, matching ItemWandCasting / ItemAmuletVis / ItemFocusBasic.
    private static final DecimalFormat VIS_FORMAT = new DecimalFormat("#######.#");

    private final TileFocalManipulator table;
    private final List<FocusUpgradeType> possibleUpgrades = new ArrayList<FocusUpgradeType>();
    private final AspectList selectedCost = new AspectList();
    private int selected = -1;
    private int rank = -1;

    public GuiFocalManipulator(InventoryPlayer playerInventory, TileFocalManipulator table) {
        super(new ContainerFocalManipulator(playerInventory, table));
        this.table = table;
        this.xSize = 192;
        this.ySize = 233;
        if (table.size > 0) {
            this.selected = table.upgrade;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawUpgradeTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.gatherInfo();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        this.drawAppliedUpgrades();
        this.drawPossibleUpgrades();
        this.drawSelectedCost();
        this.drawProgress();
    }

    private void gatherInfo() {
        this.possibleUpgrades.clear();
        this.selectedCost.aspects.clear();
        this.rank = -1;

        ItemStack focusStack = this.table.getStackInSlot(0);
        if (focusStack.isEmpty() || !(focusStack.getItem() instanceof ItemFocusBasic)) {
            this.selected = -1;
            return;
        }

        ItemFocusBasic focus = (ItemFocusBasic) focusStack.getItem();
        this.rank = this.table.size > 0 && this.table.rank > 0 ? this.table.rank : getNextRank(focus, focusStack);
        if (this.rank < 1) {
            this.selected = -1;
            return;
        }

        FocusUpgradeType[] allowed = focus.getPossibleUpgradesByRank(focusStack, this.rank);
        if (allowed != null) {
            for (FocusUpgradeType type : allowed) {
                if (type != null && focus.canApplyUpgrade(focusStack, this.mc.player, type, this.rank)) {
                    this.possibleUpgrades.add(type);
                }
            }
        }

        if (this.table.size > 0) {
            this.selected = this.table.upgrade;
            this.selectedCost.add(this.table.aspects);
            return;
        }

        FocusUpgradeType selectedType = getSelectedType();
        if (selectedType != null && selectedType.aspects != null) {
            int amount = TileFocalManipulator.VIS_MULT;
            for (int i = 1; i < this.rank; ++i) {
                amount *= 2;
            }
            for (Aspect aspect : selectedType.aspects.getAspects()) {
                addPrimalCost(aspect, selectedType.aspects.getAmount(aspect) * amount);
            }
        }
    }

    private int getNextRank(ItemFocusBasic focus, ItemStack focusStack) {
        short[] applied = focus.getAppliedUpgrades(focusStack);
        int nextRank = 1;
        while (nextRank <= 5 && applied[nextRank - 1] != -1) {
            ++nextRank;
        }
        return nextRank > 5 ? -1 : nextRank;
    }

    private void addPrimalCost(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return;
        if (aspect.isPrimal() || aspect.getComponents() == null) {
            this.selectedCost.add(aspect, amount);
            return;
        }
        for (Aspect component : aspect.getComponents()) {
            addPrimalCost(component, amount);
        }
    }

    private void drawAppliedUpgrades() {
        ItemStack focusStack = this.table.getStackInSlot(0);
        if (focusStack.isEmpty() || !(focusStack.getItem() instanceof ItemFocusBasic)) return;

        short[] applied = ((ItemFocusBasic) focusStack.getItem()).getAppliedUpgrades(focusStack);
        for (int i = 0; i < applied.length; ++i) {
            if (applied[i] < 0 || applied[i] >= FocusUpgradeType.types.length) continue;
            FocusUpgradeType type = FocusUpgradeType.types[applied[i]];
            if (type == null) continue;
            drawIcon(type.icon, this.guiLeft + 56 + i * 16, this.guiTop + 32, 16, 16);
        }
    }

    private void drawPossibleUpgrades() {
        for (int i = 0; i < this.possibleUpgrades.size(); ++i) {
            FocusUpgradeType type = this.possibleUpgrades.get(i);
            int x = this.guiLeft + 48 + i * 16;
            int y = this.guiTop + 104;
            if (type.id == this.selected) {
                drawRect(x - 1, y - 1, x + 17, y + 17, 0x66FFFFFF);
            }
            drawIcon(type.icon, x, y, 16, 16);
        }
    }

    private void drawSelectedCost() {
        if (this.selected < 0 || this.rank < 1) return;

        String rankText = "Rank " + this.rank;
        this.fontRenderer.drawString(rankText, this.guiLeft + 48, this.guiTop + 48, 0xFFFFFF);
        this.fontRenderer.drawString(I18n.translateToLocal("wandtable.text2") + ": " + this.rank * TileFocalManipulator.XP_MULT,
                this.guiLeft + 108, this.guiTop + 58, 0xFFFFFF);

        Aspect[] aspects = this.selectedCost.getAspectsSorted();
        for (int i = 0; i < aspects.length && i < 6; ++i) {
            Aspect aspect = aspects[i];
            drawIcon(aspect.getImage(), this.guiLeft + 48 + i * 16, this.guiTop + 68, 16, 16);
            this.fontRenderer.drawString(VIS_FORMAT.format((float) this.selectedCost.getAmount(aspect) / 100.0F),
                    this.guiLeft + 50 + i * 16, this.guiTop + 84, 0xFFFFFF);
        }
    }

    private void drawProgress() {
        if (this.table.size <= 0) return;
        int remaining = this.table.aspects.visSize();
        int width = Math.max(0, Math.min(72, 72 - remaining * 72 / Math.max(1, this.table.size)));
        drawRect(this.guiLeft + 60, this.guiTop + 124, this.guiLeft + 132, this.guiTop + 130, 0x66000000);
        drawRect(this.guiLeft + 60, this.guiTop + 124, this.guiLeft + 60 + width, this.guiTop + 130, 0xAA8D62E9);
        this.fontRenderer.drawString(I18n.translateToLocal("wandtable.text1") + ": " + VIS_FORMAT.format((float) remaining / 100.0F),
                this.guiLeft + 48, this.guiTop + 132, 0xFFFFFF);
    }

    private void drawIcon(ResourceLocation texture, int x, int y, int width, int height) {
        if (texture == null) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (int i = 0; i < this.possibleUpgrades.size(); ++i) {
            if (isMouseIn(mouseX, mouseY, 48 + i * 16, 104, 16, 16)) {
                this.selected = this.possibleUpgrades.get(i).id;
                this.playButtonClick();
                return;
            }
        }
        if (this.selected >= 0 && this.table.size <= 0 && isMouseIn(mouseX, mouseY, 108, 58, 36, 16)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, this.selected);
            this.playButtonClick();
        }
    }

    private void drawUpgradeTooltip(int mouseX, int mouseY) {
        for (int i = 0; i < this.possibleUpgrades.size(); ++i) {
            if (!isMouseIn(mouseX, mouseY, 48 + i * 16, 104, 16, 16)) continue;
            FocusUpgradeType type = this.possibleUpgrades.get(i);
            List<String> tooltip = new ArrayList<String>();
            tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.UNDERLINE + type.getLocalizedName());
            tooltip.add(type.getLocalizedText());
            this.drawHoveringText(tooltip, mouseX, mouseY);
            return;
        }
    }

    private FocusUpgradeType getSelectedType() {
        if (this.selected < 0 || this.selected >= FocusUpgradeType.types.length) return null;
        return FocusUpgradeType.types[this.selected];
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }

    private void playButtonClick() {
        if (this.mc.player != null) {
            this.mc.player.playSound(TCSounds.CAMERACLACK, 0.4F, 1.0F);
        }
    }
}
