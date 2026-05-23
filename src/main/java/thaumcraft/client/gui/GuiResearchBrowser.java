package thaumcraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileNodeRenderer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class GuiResearchBrowser extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_research.png");
    private static final ResourceLocation UNKNOWN_ASPECT_TEXTURE = new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    private static int guiMapTop;
    private static int guiMapLeft;
    private static int guiMapBottom;
    private static int guiMapRight;

    public static int lastX = -5;
    public static int lastY = -6;
    public static HashMap<String, ArrayList<String>> completedResearch = new HashMap<>();
    public static ArrayList<String> highlightedItem = new ArrayList<>();

    private static String selectedCategory;

    protected int paneWidth = 256;
    protected int paneHeight = 230;
    protected int mouseX;
    protected int mouseY;
    protected double field_74117_m;
    protected double field_74115_n;
    protected double guiMapX;
    protected double guiMapY;
    protected double field_74124_q;
    protected double field_74123_r;
    private int isMouseButtonDown;
    private GuiButton button;
    private LinkedList<ResearchItem> research = new LinkedList<>();
    private FontRenderer galFontRenderer;
    private ResearchItem currentHighlight;
    private String player = "";
    long popuptime;
    String popupmessage = "";
    public boolean hasScribestuff;

    public GuiResearchBrowser() {
        int mapWidth = 141;
        int mapHeight = 141;
        this.guiMapX = this.field_74124_q = lastX * 24 - mapWidth / 2.0 - 12.0;
        this.field_74117_m = this.field_74124_q;
        this.guiMapY = this.field_74123_r = lastY * 24 - mapHeight / 2.0;
        this.field_74115_n = this.field_74123_r;
        this.updateResearch();
        Minecraft minecraft = Minecraft.getMinecraft();
        this.galFontRenderer = minecraft.fontRenderer;
        this.player = minecraft.player == null ? "" : minecraft.player.getName();
    }

    public GuiResearchBrowser(double x, double y) {
        this.guiMapX = this.field_74124_q = x;
        this.field_74117_m = this.field_74124_q;
        this.guiMapY = this.field_74123_r = y;
        this.field_74115_n = this.field_74123_r;
        this.updateResearch();
        Minecraft minecraft = Minecraft.getMinecraft();
        this.galFontRenderer = minecraft.fontRenderer;
        this.player = minecraft.player == null ? "" : minecraft.player.getName();
    }

    public static void syncClientKnowledgeCache(@Nullable EntityPlayer player) {
        if (player == null) {
            return;
        }
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) {
            completedResearch.remove(player.getName());
            return;
        }
        completedResearch.put(player.getName(), new ArrayList<>(knowledge.getResearchComplete()));
    }

    public static void clearClientKnowledgeCache(@Nullable EntityPlayer player) {
        if (player == null) {
            completedResearch.clear();
            return;
        }
        completedResearch.remove(player.getName());
    }

    public void updateResearch() {
        if (this.mc == null) {
            this.mc = Minecraft.getMinecraft();
        }
        this.research.clear();
        this.hasScribestuff = false;
        if (this.mc.player != null) {
            this.player = this.mc.player.getName();
            syncClientKnowledgeCache(this.mc.player);
        }
        if (selectedCategory == null || ResearchCategories.getResearchList(selectedCategory) == null) {
            Set<String> categories = ResearchCategories.researchCategories.keySet();
            if (!categories.isEmpty()) {
                selectedCategory = categories.iterator().next();
            }
        }
        ResearchCategoryList categoryList = ResearchCategories.getResearchList(selectedCategory);
        if (categoryList == null) {
            return;
        }
        Collection<ResearchItem> collection = categoryList.research.values();
        this.research.addAll(collection);
        if (this.mc.player != null
                && ResearchManager.consumeInkFromPlayer(this.mc.player, false)
                && InventoryUtils.isPlayerCarrying(this.mc.player, new ItemStack(Items.PAPER)) >= 0) {
            this.hasScribestuff = true;
        }
        guiMapTop = categoryList.minDisplayColumn * 24 - 85;
        guiMapLeft = categoryList.minDisplayRow * 24 - 112;
        guiMapBottom = categoryList.maxDisplayColumn * 24 - 112;
        guiMapRight = categoryList.maxDisplayRow * 24 - 61;
    }

    @Override
    public void onGuiClosed() {
        int mapWidth = 141;
        int mapHeight = 141;
        lastX = (int) ((this.guiMapX + mapWidth / 2.0 + 12.0) / 24.0);
        lastY = (int) ((this.guiMapY + mapHeight / 2.0) / 24.0);
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            highlightedItem.clear();
            this.mc.displayGuiScreen(null);
            return;
        }
        if (keyCode == 1) {
            highlightedItem.clear();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int left = (this.width - this.paneWidth) / 2;
        int top = (this.height - this.paneHeight) / 2;
        if (Mouse.isButtonDown(0)) {
            int mapLeft = left + 8;
            int mapTop = top + 17;
            if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1)
                    && mouseX >= mapLeft && mouseX < mapLeft + 224
                    && mouseY >= mapTop && mouseY < mapTop + 196) {
                if (this.isMouseButtonDown == 0) {
                    this.isMouseButtonDown = 1;
                } else {
                    this.guiMapX -= mouseX - this.mouseX;
                    this.guiMapY -= mouseY - this.mouseY;
                    this.field_74124_q = this.field_74117_m = this.guiMapX;
                    this.field_74123_r = this.field_74115_n = this.guiMapY;
                }
                this.mouseX = mouseX;
                this.mouseY = mouseY;
            }
            if (this.field_74124_q < guiMapTop) {
                this.field_74124_q = guiMapTop;
            }
            if (this.field_74123_r < guiMapLeft) {
                this.field_74123_r = guiMapLeft;
            }
            if (this.field_74124_q >= guiMapBottom) {
                this.field_74124_q = guiMapBottom - 1;
            }
            if (this.field_74123_r >= guiMapRight) {
                this.field_74123_r = guiMapRight - 1;
            }
        } else {
            this.isMouseButtonDown = 0;
        }
        this.drawDefaultBackground();
        this.genResearchBackground(mouseX, mouseY, partialTicks);
        if (this.popuptime > System.currentTimeMillis()) {
            int centerX = left + 128;
            int centerY = top + 128;
            int halfHeight = this.getWrappedHeight(this.fontRenderer, this.popupmessage, 150) / 2;
            this.drawGradientRect(centerX - 78, centerY - halfHeight - 3, centerX + 78, centerY + halfHeight + 3, -1073741824, -1073741824);
            this.fontRenderer.drawSplitString(this.popupmessage, centerX - 75, centerY - halfHeight, 150, -7302913);
        }
        Set<String> categories = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (String category : categories) {
            if (count == 9) {
                count = 0;
                swop = true;
            }
            if ("ELDRITCH".equals(category) && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")) {
                continue;
            }
            int hoverX = mouseX - (left - 24 + (swop ? 280 : 0));
            int hoverY = mouseY - (top + count * 24);
            if (hoverX >= 0 && hoverX < 24 && hoverY >= 0 && hoverY < 24) {
                this.fontRenderer.drawStringWithShadow(ResearchCategories.getCategoryName(category), mouseX, mouseY - 8, 0xFFFFFF);
            }
            ++count;
        }
    }

    @Override
    public void updateScreen() {
        this.field_74117_m = this.guiMapX;
        this.field_74115_n = this.guiMapY;
        double deltaX = this.field_74124_q - this.guiMapX;
        double deltaY = this.field_74123_r - this.guiMapY;
        if (deltaX * deltaX + deltaY * deltaY < 4.0) {
            this.guiMapX += deltaX;
            this.guiMapY += deltaY;
        } else {
            this.guiMapX += deltaX * 0.85;
            this.guiMapY += deltaY * 0.85;
        }
    }

    protected void genResearchBackground(int mouseX, int mouseY, float partialTicks) {
        long ticks50ms = System.nanoTime() / 50000000L;
        int mapX = MathHelper.floor(this.field_74117_m + (this.guiMapX - this.field_74117_m) * partialTicks);
        int mapY = MathHelper.floor(this.field_74115_n + (this.guiMapY - this.field_74115_n) * partialTicks);
        if (mapX < guiMapTop) {
            mapX = guiMapTop;
        }
        if (mapY < guiMapLeft) {
            mapY = guiMapLeft;
        }
        if (mapX >= guiMapBottom) {
            mapX = guiMapBottom - 1;
        }
        if (mapY >= guiMapRight) {
            mapY = guiMapRight - 1;
        }
        int left = (this.width - this.paneWidth) / 2;
        int top = (this.height - this.paneHeight) / 2;
        int contentLeft = left + 16;
        int contentTop = top + 17;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, -200.0F);
        GlStateManager.enableTexture2D();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 1.0F);
        ResearchCategoryList categoryList = ResearchCategories.getResearchList(selectedCategory);
        if (categoryList != null) {
            int backgroundU = (int) ((float) (mapX - guiMapTop) / (float) Math.max(1, Math.abs(guiMapTop - guiMapBottom)) * 288.0F);
            int backgroundV = (int) ((float) (mapY - guiMapLeft) / (float) Math.max(1, Math.abs(guiMapLeft - guiMapRight)) * 316.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(categoryList.background);
            this.drawTexturedModalRect(contentLeft / 2, contentTop / 2, backgroundU / 2, backgroundV / 2, 112, 98);
        }
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        ArrayList<String> completed = this.getCompletedResearch();
        if (completed != null) {
            for (ResearchItem item : this.research) {
                if (item.parents != null && item.parents.length > 0) {
                    for (String parentKey : item.parents) {
                        ResearchItem parent = parentKey == null ? null : ResearchCategories.getResearch(parentKey);
                        if (parent == null || !parent.category.equals(selectedCategory) || parent.isVirtual()) {
                            continue;
                        }
                        int x1 = item.displayColumn * 24 - mapX + 11 + contentLeft;
                        int y1 = item.displayRow * 24 - mapY + 11 + contentTop;
                        int x2 = parent.displayColumn * 24 - mapX + 11 + contentLeft;
                        int y2 = parent.displayRow * 24 - mapY + 11 + contentTop;
                        boolean itemComplete = completed.contains(item.key);
                        boolean parentComplete = completed.contains(parent.key);
                        if (itemComplete) {
                            this.drawLine(x1, y1, x2, y2, 0.1F, 0.1F, 0.1F, partialTicks, false);
                        } else if (!item.isLost() && !((item.isHidden() || item.isLost()) && !completed.contains("@" + item.key)) && !(item.isConcealed() && !this.canUnlockResearch(item))) {
                            if (parentComplete) {
                                this.drawLine(x1, y1, x2, y2, 0.0F, 1.0F, 0.0F, partialTicks, true);
                            } else if (!((parent.isHidden() || item.isLost()) && !completed.contains("@" + parent.key)) && !(parent.isConcealed() && !this.canUnlockResearch(parent))) {
                                this.drawLine(x1, y1, x2, y2, 0.0F, 0.0F, 1.0F, partialTicks, true);
                            }
                        }
                    }
                }
                if (item.siblings == null || item.siblings.length <= 0) {
                    continue;
                }
                for (String siblingKey : item.siblings) {
                    ResearchItem sibling = siblingKey == null ? null : ResearchCategories.getResearch(siblingKey);
                    if (sibling == null || !sibling.category.equals(selectedCategory) || sibling.isVirtual()) {
                        continue;
                    }
                    if (sibling.parents != null && Arrays.asList(sibling.parents).contains(item.key)) {
                        continue;
                    }
                    int x1 = item.displayColumn * 24 - mapX + 11 + contentLeft;
                    int y1 = item.displayRow * 24 - mapY + 11 + contentTop;
                    int x2 = sibling.displayColumn * 24 - mapX + 11 + contentLeft;
                    int y2 = sibling.displayRow * 24 - mapY + 11 + contentTop;
                    boolean itemComplete = completed.contains(item.key);
                    boolean siblingComplete = completed.contains(sibling.key);
                    if (itemComplete) {
                        this.drawLine(x1, y1, x2, y2, 0.1F, 0.1F, 0.2F, partialTicks, false);
                    } else if (!item.isLost() && !(item.isHidden() && !completed.contains("@" + item.key)) && !(item.isConcealed() && !this.canUnlockResearch(item))) {
                        if (siblingComplete) {
                            this.drawLine(x1, y1, x2, y2, 0.0F, 1.0F, 0.0F, partialTicks, true);
                        } else if (!(sibling.isHidden() && !completed.contains("@" + sibling.key)) && !(sibling.isConcealed() && !this.canUnlockResearch(sibling))) {
                            this.drawLine(x1, y1, x2, y2, 0.0F, 0.0F, 1.0F, partialTicks, true);
                        }
                    }
                }
            }
        }

        this.currentHighlight = null;
        RenderItem itemRenderer = this.itemRender;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        if (completed != null) {
            for (ResearchItem item : this.research) {
                int itemX = item.displayColumn * 24 - mapX;
                int itemY = item.displayRow * 24 - mapY;
                if (item.isVirtual() || itemX < -24 || itemY < -24 || itemX > 224 || itemY > 196) {
                    continue;
                }
                int drawX = contentLeft + itemX;
                int drawY = contentTop + itemY;
                if (completed.contains(item.key)) {
                    if (ThaumcraftApi.getWarp(item.key) > 0) {
                        this.drawForbidden(drawX + 11, drawY + 11);
                    }
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    if (!completed.contains("@" + item.key)
                            && (item.isLost() || (item.isHidden() && !completed.contains("@" + item.key)) || (item.isConcealed() && !this.canUnlockResearch(item)))) {
                        continue;
                    }
                    if (ThaumcraftApi.getWarp(item.key) > 0) {
                        this.drawForbidden(drawX + 11, drawY + 11);
                    }
                    if (this.canUnlockResearch(item)) {
                        float brightness = (float) Math.sin((Minecraft.getSystemTime() % 600L) / 600.0 * Math.PI * 2.0) * 0.25F + 0.75F;
                        GlStateManager.color(brightness, brightness, brightness, 1.0F);
                    } else {
                        GlStateManager.color(0.3F, 0.3F, 0.3F, 1.0F);
                    }
                }
                UtilsFX.bindTexture(GUI_TEXTURE);
                GlStateManager.enableCull();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if (item.isRound()) {
                    this.drawTexturedModalRect(drawX - 2, drawY - 2, 54, 230, 26, 26);
                } else if (item.isHidden()) {
                    if (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && item.isSecondary())) {
                        this.drawTexturedModalRect(drawX - 2, drawY - 2, 230, 230, 26, 26);
                    } else {
                        this.drawTexturedModalRect(drawX - 2, drawY - 2, 86, 230, 26, 26);
                    }
                } else if (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && item.isSecondary())) {
                    this.drawTexturedModalRect(drawX - 2, drawY - 2, 110, 230, 26, 26);
                } else {
                    this.drawTexturedModalRect(drawX - 2, drawY - 2, 0, 230, 26, 26);
                }
                if (item.isSpecial()) {
                    this.drawTexturedModalRect(drawX - 2, drawY - 2, 26, 230, 26, 26);
                }
                if (!this.canUnlockResearch(item)) {
                    GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
                }
                GlStateManager.disableBlend();
                if (highlightedItem.contains(item.key)) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(PARTICLE_TEXTURE);
                    int particleX = (int) (ticks50ms % 16L) * 16;
                    GlStateManager.translate(drawX - 5.0F, drawY - 5.0F, 0.0F);
                    UtilsFX.drawTexturedQuad(0, 0, particleX, 80, 16, 16, 0.0);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
                if (item.icon_item != null) {
                    ItemStack stack = InventoryUtils.cycleItemStack(item.icon_item);
                    if (stack != null && !stack.isEmpty()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        RenderHelper.enableGUIStandardItemLighting();
                        itemRenderer.renderItemAndEffectIntoGUI(stack, drawX + 3, drawY + 3);
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.popMatrix();
                    }
                } else if (item.icon_resource != null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    this.mc.getTextureManager().bindTexture(item.icon_resource);
                    if (!this.canUnlockResearch(item)) {
                        GlStateManager.color(0.2F, 0.2F, 0.2F, 1.0F);
                    }
                    UtilsFX.drawTexturedQuadFull(drawX + 3, drawY + 3, this.zLevel);
                    GlStateManager.popMatrix();
                }
                if (mouseX >= contentLeft && mouseY >= contentTop && mouseX < contentLeft + 224 && mouseY < contentTop + 196
                        && mouseX >= drawX && mouseX <= drawX + 22 && mouseY >= drawY && mouseY <= drawY + 22) {
                    this.currentHighlight = item;
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Set<String> categories = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (String category : categories) {
            ResearchCategoryList list = ResearchCategories.getResearchList(category);
            if ("ELDRITCH".equals(category) && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")) {
                continue;
            }
            GlStateManager.pushMatrix();
            if (count == 9) {
                count = 0;
                swop = true;
            }
            int offsetX = swop ? 264 : 0;
            int offsetU = selectedCategory.equals(category) ? 0 : 24;
            int iconInset = swop ? 14 : 0;
            if (!selectedCategory.equals(category)) {
                iconInset = swop ? 6 : 8;
            }
            UtilsFX.bindTexture(GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (swop) {
                this.drawTexturedModalRectReversed(left + offsetX - 8, top + count * 24, 176 + offsetU, 232, 24, 24);
            } else {
                this.drawTexturedModalRect(left - 24 + offsetX, top + count * 24, 152 + offsetU, 232, 24, 24);
            }
            if (highlightedItem.contains(category)) {
                GlStateManager.pushMatrix();
                this.mc.getTextureManager().bindTexture(PARTICLE_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int particleX = (int) (16L * (ticks50ms % 16L));
                UtilsFX.drawTexturedQuad(left - 27 + iconInset + offsetX, top - 4 + count * 24, particleX, 80, 16, 16, -90.0);
                GlStateManager.popMatrix();
            }
            GlStateManager.pushMatrix();
            this.mc.getTextureManager().bindTexture(list.icon);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            UtilsFX.drawTexturedQuadFull(left - 19 + iconInset + offsetX, top + 4 + count * 24, -80.0);
            GlStateManager.popMatrix();
            if (!selectedCategory.equals(category)) {
                UtilsFX.bindTexture(GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if (swop) {
                    this.drawTexturedModalRectReversed(left + offsetX - 8, top + count * 24, 224, 232, 24, 24);
                } else {
                    this.drawTexturedModalRect(left - 24 + offsetX, top + count * 24, 200, 232, 24, 24);
                }
            }
            GlStateManager.popMatrix();
            ++count;
        }

        UtilsFX.bindTexture(GUI_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(left, top, 0, 0, this.paneWidth, this.paneHeight);
        GlStateManager.popMatrix();
        this.zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (completed != null && this.currentHighlight != null) {
            this.drawCurrentHighlightTooltip(mouseX, mouseY, completed);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private void drawCurrentHighlightTooltip(int mouseX, int mouseY, ArrayList<String> completed) {
        String name = this.currentHighlight.getName();
        int tooltipX = mouseX + 6;
        int tooltipY = mouseY - 4;
        FontRenderer renderer = this.fontRenderer;
        if (!completed.contains(this.currentHighlight.key) && !this.canUnlockResearch(this.currentHighlight)) {
            renderer = this.galFontRenderer;
        }
        if (this.canUnlockResearch(this.currentHighlight)) {
            boolean secondary = !completed.contains(this.currentHighlight.key)
                    && this.currentHighlight.tags != null && this.currentHighlight.tags.size() > 0
                    && (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && this.currentHighlight.isSecondary()));
            boolean primary = !secondary && !completed.contains(this.currentHighlight.key);
            int tooltipWidth = Math.max(renderer.getStringWidth(name), renderer.getStringWidth(this.currentHighlight.getText()) / 2);
            int tooltipHeight = this.getWrappedHeight(renderer, name, tooltipWidth) + 5;
            if (primary) {
                tooltipHeight += 9;
                tooltipWidth = Math.max(tooltipWidth, renderer.getStringWidth(net.minecraft.client.resources.I18n.format("tc.research.shortprim")) / 2);
            }
            if (secondary) {
                tooltipHeight += 29;
                tooltipWidth = Math.max(tooltipWidth, renderer.getStringWidth(net.minecraft.client.resources.I18n.format("tc.research.short")) / 2);
            }
            int warp = Math.min(ThaumcraftApi.getWarp(this.currentHighlight.key), 5);
            String warpLine = net.minecraft.client.resources.I18n.format("tc.forbidden")
                    .replace("%n", net.minecraft.client.resources.I18n.format("tc.forbidden.level." + warp));
            if (ThaumcraftApi.getWarp(this.currentHighlight.key) > 0) {
                tooltipHeight += 9;
                tooltipWidth = Math.max(tooltipWidth, renderer.getStringWidth(warpLine) / 2);
            }
            this.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 6, -1073741824, -1073741824);
            GlStateManager.pushMatrix();
            GlStateManager.translate(tooltipX, tooltipY + tooltipHeight - 1, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.fontRenderer.drawString(this.currentHighlight.getText(), 0, 0, -7302913);
            GlStateManager.popMatrix();
            if (warp > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(tooltipX, tooltipY + tooltipHeight + 8, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                this.fontRenderer.drawString(warpLine, 0, 0, 0xFFFFFF);
                GlStateManager.popMatrix();
                tooltipHeight += 9;
            }
            if (primary) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(tooltipX, tooltipY + tooltipHeight + 8, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                int noteSlot = this.mc.player == null ? -1 : ResearchManager.getResearchSlot(this.mc.player, this.currentHighlight.key);
                if (noteSlot >= 0) {
                    this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("tc.research.hasnote"), 0, 0, 0xFFAA00);
                } else if (this.hasScribestuff) {
                    this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("tc.research.getprim"), 0, 0, 0x87D1AB);
                } else {
                    this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format("tc.research.shortprim"), 0, 0, 0xDC141C);
                }
                GlStateManager.popMatrix();
            } else if (secondary) {
                IPlayerKnowledge knowledge = this.getKnowledge();
                boolean enough = true;
                int count = 0;
                for (Aspect aspect : this.currentHighlight.tags.getAspectsSortedAmount()) {
                    if (knowledge != null && knowledge.hasDiscoveredAspect(aspect)) {
                        float alpha = 1.0F;
                        if (knowledge.getAspectPoolFor(aspect) < this.currentHighlight.tags.getAmount(aspect)) {
                            alpha = (float) Math.sin((Minecraft.getSystemTime() % 600L) / 600.0 * Math.PI * 2.0) * 0.25F + 0.75F;
                            enough = false;
                        }
                        GlStateManager.pushMatrix();
                        UtilsFX.drawTag(tooltipX + count * 16, tooltipY + tooltipHeight + 8, aspect, this.currentHighlight.tags.getAmount(aspect), 0, 0.0, GL11.GL_ONE_MINUS_SRC_ALPHA, alpha, false);
                        GlStateManager.popMatrix();
                    } else {
                        enough = false;
                        GlStateManager.pushMatrix();
                        UtilsFX.bindTexture(UNKNOWN_ASPECT_TEXTURE);
                        GlStateManager.color(0.5F, 0.5F, 0.5F, 0.5F);
                        GlStateManager.translate(tooltipX + count * 16, tooltipY + tooltipHeight + 8, 0.0F);
                        UtilsFX.drawTexturedQuadFull(0, 0, 0.0);
                        GlStateManager.popMatrix();
                    }
                    ++count;
                }
                GlStateManager.pushMatrix();
                GlStateManager.translate(tooltipX, tooltipY + tooltipHeight + 27, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                this.fontRenderer.drawString(net.minecraft.client.resources.I18n.format(enough ? "tc.research.purchase" : "tc.research.short"), 0, 0, enough ? 0x87D1AB : 0xDC141C);
                GlStateManager.popMatrix();
            }
        } else {
            GlStateManager.pushMatrix();
            String missing = net.minecraft.client.resources.I18n.format("tc.researchmissing");
            int tooltipWidth = Math.max(renderer.getStringWidth(name), renderer.getStringWidth(missing) / 2);
            int tooltipHeight = this.getWrappedHeight(renderer, missing, tooltipWidth * 2);
            this.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3, tooltipY + tooltipHeight + 10, -1073741824, -1073741824);
            GlStateManager.translate(tooltipX, tooltipY + 12, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.fontRenderer.drawSplitString(missing, 0, 0, tooltipWidth * 2, -9416624);
            GlStateManager.popMatrix();
        }
        renderer.drawString(name, tooltipX, tooltipY,
                this.canUnlockResearch(this.currentHighlight)
                        ? (this.currentHighlight.isSpecial() ? 0xFFFFFF80 : 0xFFFFFFFF)
                        : (this.currentHighlight.isSpecial() ? 0xFF808080 : 0xFF8080A0));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.popuptime = System.currentTimeMillis() - 1L;
        ArrayList<String> completed = this.getCompletedResearch();
        if (completed != null && this.currentHighlight != null && !completed.contains(this.currentHighlight.key) && this.canUnlockResearch(this.currentHighlight)) {
            this.updateResearch();
            boolean secondary = this.currentHighlight.tags != null && this.currentHighlight.tags.size() > 0
                    && (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && this.currentHighlight.isSecondary()));
            if (secondary) {
                boolean enough = true;
                IPlayerKnowledge knowledge = this.getKnowledge();
                for (Aspect aspect : this.currentHighlight.tags.getAspects()) {
                    if (knowledge != null && knowledge.getAspectPoolFor(aspect) >= this.currentHighlight.tags.getAmount(aspect)) {
                        continue;
                    }
                    enough = false;
                    break;
                }
                if (enough && this.mc.player != null) {
                    PacketHandler.INSTANCE.sendToServer(new PacketPlayerCompleteToServer(
                            this.currentHighlight.key,
                            this.mc.player.getName(),
                            this.mc.player.world.provider.getDimension(),
                            (byte) 0));
                }
            } else if (this.hasScribestuff && this.mc.player != null && ResearchManager.getResearchSlot(this.mc.player, this.currentHighlight.key) == -1) {
                PacketHandler.INSTANCE.sendToServer(new PacketPlayerCompleteToServer(
                        this.currentHighlight.key,
                        this.mc.player.getName(),
                        this.mc.player.world.provider.getDimension(),
                        (byte) 1));
                this.popuptime = System.currentTimeMillis() + 3000L;
                this.popupmessage = net.minecraft.client.resources.I18n.format("tc.research.popup", this.currentHighlight.getName());
            }
        } else if (completed != null && this.currentHighlight != null && completed.contains(this.currentHighlight.key)) {
            this.mc.displayGuiScreen(new GuiResearchRecipe(this.currentHighlight, 0, this.guiMapX, this.guiMapY));
        } else {
            int left = (this.width - this.paneWidth) / 2;
            int top = (this.height - this.paneHeight) / 2;
            Set<String> categories = ResearchCategories.researchCategories.keySet();
            int count = 0;
            boolean swop = false;
            for (String category : categories) {
                if ("ELDRITCH".equals(category) && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")) {
                    continue;
                }
                if (count == 9) {
                    count = 0;
                    swop = true;
                }
                int hoverX = mouseX - (left - 24 + (swop ? 280 : 0));
                int hoverY = mouseY - (top + count * 24);
                if (hoverX >= 0 && hoverX < 24 && hoverY >= 0 && hoverY < 24) {
                    selectedCategory = category;
                    this.updateResearch();
                    this.playButtonClick();
                    break;
                }
                ++count;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawTexturedModalRectReversed(int x, int y, int textureX, int textureY, int width, int height) {
        float du = 0.00390625F;
        float dv = 0.00390625F;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, this.zLevel).tex(textureX * du, (textureY + height) * dv).endVertex();
        buffer.pos(x + width, y + height, this.zLevel).tex((textureX - width) * du, (textureY + height) * dv).endVertex();
        buffer.pos(x + width, y, this.zLevel).tex((textureX - width) * du, textureY * dv).endVertex();
        buffer.pos(x, y, this.zLevel).tex(textureX * du, textureY * dv).endVertex();
        Tessellator.getInstance().draw();
    }

    private void playButtonClick() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.CAMERACLACK, SoundCategory.PLAYERS, 0.4F, 1.0F);
        }
    }

    private boolean canUnlockResearch(ResearchItem research) {
        ArrayList<String> completed = this.getCompletedResearch();
        if (completed == null) {
            return false;
        }
        if (research.parents != null && research.parents.length > 0) {
            for (String parentKey : research.parents) {
                ResearchItem parent = ResearchCategories.getResearch(parentKey);
                if (parent != null && !completed.contains(parent.key)) {
                    return false;
                }
            }
        }
        if (research.parentsHidden != null && research.parentsHidden.length > 0) {
            for (String parentKey : research.parentsHidden) {
                ResearchItem parent = ResearchCategories.getResearch(parentKey);
                if (parent != null && !completed.contains(parent.key)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawLine(int x1, int y1, int x2, int y2, float red, float green, float blue, float partialTicks, boolean wiggle) {
        float count = (this.mc.player == null ? 0.0F : this.mc.player.ticksExisted) + partialTicks;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        double dx = x1 - x2;
        double dy = y1 - y2;
        float dist = MathHelper.sqrt(dx * dx + dy * dy);
        int increments = Math.max(1, (int) (dist / 2.0F));
        float stepX = (float) (dx / increments);
        float stepY = (float) (dy / increments);
        if (Math.abs(dx) > Math.abs(dy)) {
            stepX *= 2.0F;
        } else {
            stepY *= 2.0F;
        }
        GL11.glLineWidth(3.0F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int index = 0; index <= increments; ++index) {
            float currentRed = red;
            float currentGreen = green;
            float currentBlue = blue;
            float offsetX = 0.0F;
            float offsetY = 0.0F;
            float alpha = 0.6F;
            if (wiggle) {
                float phase = index / (float) increments;
                offsetX = MathHelper.sin((count + index) / 7.0F) * 5.0F * (1.0F - phase);
                offsetY = MathHelper.sin((count + index) / 5.0F) * 5.0F * (1.0F - phase);
                currentRed *= 1.0F - phase;
                currentGreen *= 1.0F - phase;
                currentBlue *= 1.0F - phase;
                alpha *= phase;
            }
            buffer.pos(x1 - stepX * index + offsetX, y1 - stepY * index + offsetY, 0.0D)
                    .color(currentRed, currentGreen, currentBlue, alpha)
                    .endVertex();
            if (Math.abs(dx) > Math.abs(dy)) {
                stepX *= 1.0F - 1.0F / (increments * 3.0F / 2.0F);
            } else {
                stepY *= 1.0F - 1.0F / (increments * 3.0F / 2.0F);
            }
        }
        tessellator.draw();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawForbidden(double x, double y) {
        int tick = this.mc.player == null ? 0 : this.mc.player.ticksExisted;
        int frames = 32;
        int frame = frames - 1 - tick % frames;
        float u0 = frame / (float) frames;
        float u1 = (frame + 1) / (float) frames;
        float v0 = 5.0F / 8.0F;
        float v1 = 6.0F / 8.0F;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.bindTexture(TileNodeRenderer.NODES_TEXTURE);
        GlStateManager.color(0.266F, 0.0F, 0.333F, 0.66F);
        this.drawQuad(x - 8.0, y - 8.0, 16.0, 16.0, u0, v0, u1, v1, 0.0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawQuad(double x, double y, double width, double height, double u0, double v0, double u1, double v1, double z) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, z).tex(u0, v1).endVertex();
        buffer.pos(x + width, y + height, z).tex(u1, v1).endVertex();
        buffer.pos(x + width, y, z).tex(u1, v0).endVertex();
        buffer.pos(x, y, z).tex(u0, v0).endVertex();
        Tessellator.getInstance().draw();
    }

    @Nullable
    private IPlayerKnowledge getKnowledge() {
        if (this.mc == null || this.mc.player == null) {
            return null;
        }
        return this.mc.player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
    }

    @Nullable
    private ArrayList<String> getCompletedResearch() {
        if (this.mc != null && this.mc.player != null) {
            syncClientKnowledgeCache(this.mc.player);
            return completedResearch.get(this.mc.player.getName());
        }
        return completedResearch.get(this.player);
    }

    private int getWrappedHeight(FontRenderer renderer, String text, int width) {
        return renderer.listFormattedStringToWidth(text, width).size() * renderer.FONT_HEIGHT;
    }
}
