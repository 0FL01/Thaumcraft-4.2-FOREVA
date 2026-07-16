package thaumcraft.client.gui;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    private static final int START_X = 48;
    private static final int START_Y = 88;
    private static final int START_WIDTH = 96;
    private static final int START_HEIGHT = 8;
    private static final int OPTIONS_X = 48;
    private static final int OPTIONS_Y = 104;
    private static final int OPTION_SIZE = 16;

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
        this.renderHoveredToolTip(mouseX, mouseY);
        this.drawControlTooltip(mouseX, mouseY);
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
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        this.drawAppliedUpgrades();
        this.drawPossibleUpgrades();
        this.drawSelectedCost();
        this.drawProgress();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
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
            int x = this.guiLeft + OPTIONS_X + i * OPTION_SIZE;
            int y = this.guiTop + OPTIONS_Y;
            if (type.id == this.selected) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(TEXTURE);
                this.drawTexturedModalRect(x, y, 200, 0, OPTION_SIZE, OPTION_SIZE);
            }
            drawIcon(type.icon, x, y, OPTION_SIZE, OPTION_SIZE);
        }
    }

    private void drawSelectedCost() {
        if (this.selected < 0 || this.rank < 1) return;

        int xp = getRequiredExperience();
        boolean enoughExperience = hasEnoughExperience(xp);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        if (canStartUpgrade()) {
            this.drawTexturedModalRect(this.guiLeft + START_X, this.guiTop + START_Y,
                    8, 240, START_WIDTH, START_HEIGHT);
        }
        this.drawTexturedModalRect(this.guiLeft + 108, this.guiTop + 59, 200, 16, 16, 16);
        this.fontRenderer.drawString(String.valueOf(xp), this.guiLeft + 125, this.guiTop + 64,
                enoughExperience ? 10092429 : 16151160);

        Aspect[] aspects = this.selectedCost.getAspectsSorted();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft + 49.0F,
                this.guiTop + 68.0F - this.selectedCost.size() * 2.5F, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        int row = 0;
        for (Aspect aspect : aspects) {
            if (aspect == null) continue;
            this.fontRenderer.drawString(aspect.getName(), 0, row * 10, aspect.getColor());
            this.fontRenderer.drawString(VIS_FORMAT.format((float) this.selectedCost.getAmount(aspect) / 100.0F),
                    48, row * 10, aspect.getColor());
            ++row;
        }
        GlStateManager.popMatrix();
    }

    private void drawProgress() {
        if (this.table.size <= 0) return;
        int start = 0;
        this.mc.getTextureManager().bindTexture(TEXTURE);
        for (Aspect aspect : this.table.aspects.getAspectsSorted()) {
            if (aspect == null) continue;
            int amount = this.table.aspects.getAmount(aspect);
            if (amount <= 0) continue;
            int width = (int) ((float) amount / (float) this.table.size * START_WIDTH);
            int color = aspect.getColor();
            GlStateManager.color(((color >> 16) & 0xFF) / 255.0F,
                    ((color >> 8) & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F, 0.9F);
            this.drawTexturedModalRect(this.guiLeft + START_X + start, this.guiTop + START_Y,
                    112 + start, 240, width, START_HEIGHT);
            start += width;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
        if (canStartUpgrade() && isMouseIn(mouseX, mouseY, START_X, START_Y, START_WIDTH, START_HEIGHT)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, this.selected);
            this.playButtonClick();
            return;
        }
        if (this.table.size <= 0) {
            for (int i = 0; i < this.possibleUpgrades.size(); ++i) {
                if (!isMouseIn(mouseX, mouseY, OPTIONS_X + i * OPTION_SIZE, OPTIONS_Y,
                        OPTION_SIZE, OPTION_SIZE)) continue;
                int id = this.possibleUpgrades.get(i).id;
                this.selected = this.selected == id ? -1 : id;
                this.playButtonClick();
                return;
            }
        }
    }

    private void drawControlTooltip(int mouseX, int mouseY) {
        if (this.selected < 0 || this.rank < 1) return;
        if (isMouseIn(mouseX, mouseY, 48, 48, 36, 36)) {
            this.drawHoveringText(Collections.singletonList(I18n.translateToLocal("wandtable.text1")), mouseX, mouseY);
        } else if (isMouseIn(mouseX, mouseY, 108, 58, 36, 16)) {
            this.drawHoveringText(Collections.singletonList(I18n.translateToLocal("wandtable.text2")), mouseX, mouseY);
        } else if (canStartUpgrade() && isMouseIn(mouseX, mouseY,
                START_X, START_Y, START_WIDTH, START_HEIGHT)) {
            this.drawHoveringText(Collections.singletonList(I18n.translateToLocal("wandtable.text3")), mouseX, mouseY);
        }
    }

    private void drawUpgradeTooltip(int mouseX, int mouseY) {
        for (int i = 0; i < this.possibleUpgrades.size(); ++i) {
            if (!isMouseIn(mouseX, mouseY, OPTIONS_X + i * OPTION_SIZE, OPTIONS_Y,
                    OPTION_SIZE, OPTION_SIZE)) continue;
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

    private boolean canStartUpgrade() {
        if (this.table.size > 0 || this.rank < 1 || !isSelectedUpgradeAvailable()) return false;
        return hasEnoughExperience(getRequiredExperience());
    }

    private boolean isSelectedUpgradeAvailable() {
        for (FocusUpgradeType type : this.possibleUpgrades) {
            if (type.id == this.selected) return true;
        }
        return false;
    }

    private int getRequiredExperience() {
        return this.rank * TileFocalManipulator.XP_MULT;
    }

    private boolean hasEnoughExperience(int required) {
        return this.mc.player != null && (this.mc.player.capabilities.isCreativeMode
                || this.mc.player.experienceLevel >= required);
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
