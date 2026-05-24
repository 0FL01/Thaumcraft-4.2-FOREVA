package thaumcraft.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer;
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.tiles.TileResearchTable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GuiResearchTable extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guiresearchtable2.png");
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    private static boolean RESEARCHER_1;
    private static boolean RESEARCHER_2;
    private static boolean RESEARCHDUPE;

    private final int HEX_SIZE = 9;
    private float xSize_lo;
    private float ySize_lo;
    private long butcount1 = 0L;
    private long butcount2 = 0L;
    private int page = 0;
    private int lastPage = 0;
    private int isMouseButtonDown = 0;
    private TileResearchTable tileEntity;
    private FontRenderer galFontRenderer;
    private String username;
    private EntityPlayer player;
    public Aspect select1 = null;
    public Aspect select2 = null;
    private AspectList aspectlist = new AspectList();
    private HashMap<String, Rune> runes = new HashMap<>();
    private float popupScale = 0.05f;
    private Aspect draggedAspect;
    public ResearchNoteData note = null;
    long lastRuneCheck = 0L;
    private HashMap<String, HexUtils.Hex[]> lines = new HashMap<>();
    private ArrayList<String> checked = new ArrayList<>();
    private ArrayList<String> highlight = new ArrayList<>();

    public GuiResearchTable(EntityPlayer player, TileResearchTable tile) {
        super(new ContainerResearchTable(player.inventory, tile));
        this.tileEntity = tile;
        this.xSize = 255;
        this.ySize = 255;
        this.username = player.getName();
        this.player = player;
        RESEARCHER_1 = ResearchManager.isResearchComplete(player.getName(), "RESEARCHER1");
        RESEARCHER_2 = ResearchManager.isResearchComplete(player.getName(), "RESEARCHER2");
        RESEARCHDUPE = ResearchManager.isResearchComplete(player.getName(), "RESEARCHDUPE");
        int count = 0;
        for (Aspect aspect : Aspect.aspects.values()) {
            this.aspectlist.add(aspect, count);
            ++count;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (this.galFontRenderer == null) {
            this.galFontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRenderer;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Notifications rendered by RenderEventHandler via event bus
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.xSize_lo = (float) mouseX;
        this.ySize_lo = (float) mouseY;
        int gx = (this.width - this.xSize) / 2;
        int gy = (this.height - this.ySize) / 2;

        if (this.note != null && RESEARCHDUPE && this.note.isComplete()) {
            int var7 = mouseX - (gx + 37);
            int var8 = mouseY - (gy + 5);
            if (var7 >= 0 && var8 >= 0 && var7 < 24 && var8 < 24) {
                RenderHelper.enableStandardItemLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                ResearchItem rr = ResearchCategories.getResearch(this.note.key);
                String ss = I18n.translateToLocal("tc.research.copy");
                GlStateManager.enableBlend();
                UtilsFX.bindTexture(GUI_TEXTURE);
                this.drawTexturedModalRect(gx + 100, gy + 21, 184, 224, 48, 16);
                AspectList al = rr.tags.copy();
                for (Aspect aspect : al.getAspects()) {
                    al.add(aspect, this.note.copies);
                }
                int count = 0;
                for (Aspect aspect : al.getAspectsSorted()) {
                    UtilsFX.drawTag(gx + 100 + 48 + count * 16, gy + 21, aspect, (float) al.getAmount(aspect), 0, this.zLevel, 771, 1.0f, false);
                    ++count;
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.fontRenderer.drawString(ss, gx + 100, gy + 12, -1);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (Mouse.isButtonDown(0)) {
            int sx = gx + 10;
            int sy = gy + 40;
            if (this.isMouseButtonDown == 0 && mouseX >= sx && mouseX < sx + 80 && mouseY >= sy && mouseY < sy + 80) {
                Aspect aspect = this.getClickedAspect(mouseX, mouseY, gx, gy, false);
                if (aspect != null) {
                    this.playButtonAspect();
                    this.isMouseButtonDown = 1;
                    this.draggedAspect = aspect;
                }
            } else if (this.isMouseButtonDown == 1 && this.draggedAspect != null) {
                GlStateManager.enableBlend();
                this.drawOrb(mouseX - 8, mouseY - 8, this.draggedAspect.getColor());
                GlStateManager.disableBlend();
            }
        } else {
            if (this.isMouseButtonDown == 1 && this.draggedAspect != null) {
                int mouseX2 = mouseX - (gx + 169);
                int mouseY2 = mouseY - (gy + 83);
                HexUtils.Hex hp = new HexUtils.Pixel(mouseX2, mouseY2).toHex(9);
                if (this.note != null && this.note.hexEntries.containsKey(hp.toString()) && this.note.hexEntries.get(hp.toString()).type == 0) {
                    this.playButtonCombine();
                    this.playButtonWrite();
                    PacketHandler.INSTANCE.sendToServer(new PacketAspectPlaceToServer(this.player, (byte) hp.q, (byte) hp.r,
                            this.tileEntity.getPos().getX(), this.tileEntity.getPos().getY(), this.tileEntity.getPos().getZ(),
                            this.draggedAspect));
                    this.draggedAspect = null;
                }
                if (this.draggedAspect != null) {
                    Aspect aspect;
                    boolean skip = false;
                    int mx2 = mouseX - (gx + 20);
                    int my2 = mouseY - (gy + 146);
                    if (mx2 >= -16 && my2 >= -16 && mx2 < 16 && my2 < 16) {
                        this.playButtonAspect();
                        this.select1 = this.draggedAspect;
                        skip = true;
                    }
                    mx2 = mouseX - (gx + 79);
                    my2 = mouseY - (gy + 146);
                    if (!skip && mx2 >= -16 && my2 >= -16 && mx2 < 16 && my2 < 16) {
                        this.playButtonAspect();
                        this.select2 = this.draggedAspect;
                        skip = true;
                    }
                    if (!skip && (aspect = this.getClickedAspect(mouseX, mouseY, gx, gy, false)) == this.draggedAspect) {
                        if (this.select1 == null) {
                            this.select1 = this.draggedAspect;
                        } else if (this.select2 == null) {
                            this.select2 = this.draggedAspect;
                        }
                    }
                }
            }
            this.isMouseButtonDown = 0;
            this.draggedAspect = null;
        }

        this.drawAspectText(guiLeft + 10, guiTop + 40, mouseX, mouseY);

        if (this.note != null && (this.tileEntity.getStackInSlot(0).isEmpty() || this.tileEntity.getStackInSlot(0).getItemDamage() == this.tileEntity.getStackInSlot(0).getMaxDamage())) {
            int sx = Math.max(this.fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.0")),
                    this.fontRenderer.getStringWidth(I18n.translateToLocal("tile.researchtable.noink.1"))) / 2;
            this.drawHoveringText(Arrays.asList(
                    I18n.translateToLocal("tile.researchtable.noink.0"),
                    I18n.translateToLocal("tile.researchtable.noink.1")),
                    gx + 157 - sx, gy + 84);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.tileEntity.isInvalid() && this.mc.player != null) {
            this.mc.player.closeScreen();
            return;
        }
        if (this.galFontRenderer == null) {
            this.galFontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRenderer;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        UtilsFX.bindTexture(GUI_TEXTURE);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, 255, 167);
        this.drawTexturedModalRect(guiLeft + 40, guiTop + 167, 0, 166, 184, 88);
        if (this.page < this.lastPage) {
            this.drawTexturedModalRect(guiLeft + 51, guiTop + 121, 208, 208, 24, 8);
        }
        if (this.page > 0) {
            this.drawTexturedModalRect(guiLeft + 27, guiTop + 121, 184, 208, 24, 8);
        }
        if (this.butcount2 < System.nanoTime() && this.select1 != null && this.select2 != null) {
            this.drawTexturedModalRect(guiLeft + 35, guiTop + 139, 184, 184, 32, 16);
            this.drawOrb(guiLeft + 43, guiTop + 139);
        } else if (this.butcount2 >= System.nanoTime() && this.select1 != null && this.select2 != null) {
            this.drawTexturedModalRect(guiLeft + 35, guiTop + 139, 184, 184, 32, 16);
            this.drawTexturedModalRect(guiLeft + 35, guiTop + 139, 184, 168, 32, 16);
        }
        if (RESEARCHDUPE && this.note != null && this.note.isComplete()) {
            this.drawTexturedModalRect(guiLeft + 37, guiTop + 5, 232, 200, 24, 24);
        }
        this.drawAspects(guiLeft + 10, guiTop + 40);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        this.drawResearchData(guiLeft, guiTop, mouseX, mouseY);
    }

    private void drawAspects(int x, int y) {
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
        if (knowledge == null) return;
        AspectList aspects = knowledge.getAspectsDiscovered();
        if (aspects != null) {
            int count = aspects.size();
            this.lastPage = (count - 20) / 5;
            count = 0;
            int drawn = 0;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (++count - 1 < this.page * 5 || drawn >= 25) continue;
                boolean faded = aspects.getAmount(aspect) <= 0 && this.tileEntity.bonusAspects.getAmount(aspect) <= 0;
                int xx = drawn / 5 * 16;
                int yy = drawn % 5 * 16;
                UtilsFX.drawTag(x + xx, y + yy, aspect, (float) aspects.getAmount(aspect),
                        this.tileEntity.bonusAspects.getAmount(aspect), this.zLevel, 771, faded ? 0.33f : 1.0f, false);
                ++drawn;
            }
        }
        if (this.select1 != null && knowledge.getAspectPoolFor(this.select1) <= 0 && this.tileEntity.bonusAspects.getAmount(this.select1) <= 0) {
            this.select1 = null;
        }
        if (this.select2 != null && knowledge.getAspectPoolFor(this.select2) <= 0 && this.tileEntity.bonusAspects.getAmount(this.select2) <= 0) {
            this.select2 = null;
        }
        if (this.select1 != null) {
            UtilsFX.drawTag(x + 3, y + 99, this.select1, 0.0f, 0, this.zLevel, 771, 1.0f, false);
        }
        if (this.select2 != null) {
            UtilsFX.drawTag(x + 61, y + 99, this.select2, 0.0f, 0, this.zLevel, 771, 1.0f, false);
        }
    }

    private void drawAspectText(int x, int y, int mx, int my) {
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
        if (knowledge == null) return;
        AspectList aspects = knowledge.getAspectsDiscovered();
        if (aspects != null) {
            int count = 0;
            int drawn = 0;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (++count - 1 < this.page * 5 || drawn >= 25) continue;
                int xx = drawn / 5 * 16;
                int yy = drawn % 5 * 16;
                int var7 = mx - (x + xx);
                int var8 = my - (y + yy);
                if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                    this.drawHoveringText(Arrays.asList(aspect.getName(), aspect.getLocalizedDescription()), mx, my - 8);
                    if (RESEARCHER_1 && !aspect.isPrimal()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        UtilsFX.bindTexture("textures/aspects/_back.png");
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(mx + 6, my + 6, 0.0);
                        GlStateManager.scale(1.25, 1.25, 0.0);
                        UtilsFX.drawTexturedQuadFull(0, 0, 0.0);
                        GlStateManager.popMatrix();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(mx + 24, my + 6, 0.0);
                        GlStateManager.scale(1.25, 1.25, 0.0);
                        UtilsFX.drawTexturedQuadFull(0, 0, 0.0);
                        GlStateManager.popMatrix();
                        UtilsFX.drawTag(mx + 26, my + 8, aspect.getComponents()[1], 0.0f, 0, 0.0, 771, 1.0f, false);
                        UtilsFX.drawTag(mx + 8, my + 8, aspect.getComponents()[0], 0.0f, 0, 0.0, 771, 1.0f, false);
                        GlStateManager.disableBlend();
                        GlStateManager.popMatrix();
                    }
                    return;
                }
                ++drawn;
            }
        }
        if (this.select1 != null) {
            int var7 = mx - (x + 3);
            int var8 = my - (y + 99);
            if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                this.drawHoveringText(Arrays.asList(this.select1.getName(), this.select1.getLocalizedDescription()), mx, my - 8);
                return;
            }
        }
        if (this.select2 != null) {
            int var7 = mx - (x + 61);
            int var8 = my - (y + 99);
            if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                this.drawHoveringText(Arrays.asList(this.select2.getName(), this.select2.getLocalizedDescription()), mx, my - 8);
                return;
            }
        }
    }

    private void drawResearchData(int x, int y, int mx, int my) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        this.drawSheet(x, y, mx, my);
        GlStateManager.popMatrix();
    }

    private void drawHex(HexUtils.Hex hex, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        UtilsFX.bindTexture("textures/gui/hex1.png");
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
        HexUtils.Pixel pix = hex.toPixel(9);
        GlStateManager.translate((double) x + pix.x, (double) y + pix.y, 0.0);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-8.0, 8.0, (double) this.zLevel).tex(0.0, 1.0).endVertex();
        buffer.pos(8.0, 8.0, (double) this.zLevel).tex(1.0, 1.0).endVertex();
        buffer.pos(8.0, -8.0, (double) this.zLevel).tex(1.0, 0.0).endVertex();
        buffer.pos(-8.0, -8.0, (double) this.zLevel).tex(0.0, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawHexHighlight(HexUtils.Hex hex, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        UtilsFX.bindTexture("textures/gui/hex2.png");
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        HexUtils.Pixel pix = hex.toPixel(9);
        GlStateManager.translate((double) x + pix.x, (double) y + pix.y, 0.0);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-8.0, 8.0, (double) this.zLevel).tex(0.0, 1.0).endVertex();
        buffer.pos(8.0, 8.0, (double) this.zLevel).tex(1.0, 1.0).endVertex();
        buffer.pos(8.0, -8.0, (double) this.zLevel).tex(1.0, 0.0).endVertex();
        buffer.pos(-8.0, -8.0, (double) this.zLevel).tex(0.0, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawLine(double x, double y, double x2, double y2) {
        int count = this.mc.player.ticksExisted;
        float alpha = 0.3f + MathHelper.sin((float) ((double) count + x)) * 0.3f + 0.3f;
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.pushMatrix();
        GL11.glLineWidth(3.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x, y, 0.0).color(0.0f, 0.6f, 0.8f, alpha).endVertex();
        buffer.pos(x2, y2, 0.0).color(0.0f, 0.6f, 0.8f, alpha).endVertex();
        tessellator.draw();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(32826); // GL_RESCALE_NORMAL
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawSheet(int x, int y, int mx, int my) {
        this.note = ResearchManager.getData(this.tileEntity.getStackInSlot(1));
        if (this.note == null || this.note.key == null || this.note.key.length() == 0) {
            this.runes.clear();
            return;
        }
        UtilsFX.bindTexture("textures/misc/parchment3.png");
        this.drawTexturedModalRect(x + 94, y + 8, 0, 0, 150, 150);

        long time = System.currentTimeMillis();
        if (this.lastRuneCheck < time) {
            this.lastRuneCheck = time + 250L;
            int k = this.mc.world.rand.nextInt(120) - 60;
            int l = this.mc.world.rand.nextInt(120) - 60;
            HexUtils.Hex hp = new HexUtils.Pixel(k, l).toHex(9);
            if (!this.runes.containsKey(hp.toString()) && !this.note.hexes.containsKey(hp.toString())) {
                this.runes.put(hp.toString(), new Rune(hp.q, hp.r, time,
                        this.lastRuneCheck + 15000L + (long) this.mc.world.rand.nextInt(10000),
                        this.mc.world.rand.nextInt(16)));
            }
        }

        if (this.runes.size() > 0) {
            Rune[] rns = this.runes.values().toArray(new Rune[0]);
            for (int a = 0; a < rns.length; ++a) {
                Rune rune = rns[a];
                if (rune.decay < time) {
                    this.runes.remove(rune.q + ":" + rune.r);
                    continue;
                }
                HexUtils.Pixel pix = new HexUtils.Hex(rune.q, rune.r).toPixel(9);
                float progress = (float) (time - rune.start) / (float) (rune.decay - rune.start);
                float alpha = 0.5f;
                if (progress < 0.25f) {
                    alpha = progress * 2.0f;
                } else if (progress > 0.5f) {
                    alpha = 1.0f - progress;
                }
                this.drawRune((double) (x + 169) + pix.x, (double) (y + 83) + pix.y, rune.rune, alpha * 0.66f);
            }
        }

        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);

        int mouseX = mx - (x + 169);
        int mouseY = my - (y + 83);
        HexUtils.Hex hp = new HexUtils.Pixel(mouseX, mouseY).toHex(9);
        this.lines.clear();
        this.checked.clear();
        this.highlight.clear();
        if (knowledge != null) {
            for (HexUtils.Hex hex : this.note.hexes.values()) {
                if (this.note.hexEntries.get(hex.toString()).type != 1
                        || !knowledge.hasDiscoveredAspect(this.note.hexEntries.get(hex.toString()).aspect))
                    continue;
                this.checkConnections(hex);
            }
        }
        for (HexUtils.Hex[] con : this.lines.values()) {
            HexUtils.Pixel p1 = con[0].toPixel(9);
            HexUtils.Pixel p2 = con[1].toPixel(9);
            this.drawLine((double) (x + 169) + p1.x, (double) (y + 83) + p1.y,
                    (double) (x + 169) + p2.x, (double) (y + 83) + p2.y);
        }

        UtilsFX.bindTexture("textures/gui/hex1.png");
        GlStateManager.pushMatrix();
        if (!this.note.isComplete()) {
            for (HexUtils.Hex hex : this.note.hexes.values()) {
                if (this.note.hexEntries.get(hex.toString()).type != 1) {
                    if (hex.equals(hp)) {
                        this.drawHexHighlight(hex, x + 169, y + 83);
                    }
                    this.drawHex(hex, x + 169, y + 83);
                    continue;
                }
                this.drawOrb((double) (x + 161) + hex.toPixel(9).x, (double) (y + 75) + hex.toPixel(9).y);
            }
        }
        for (HexUtils.Hex hex : this.note.hexes.values()) {
            HexUtils.Pixel pix;
            if (this.note.hexEntries.get(hex.toString()).aspect != null
                    && (knowledge == null || !knowledge.hasDiscoveredAspect(this.note.hexEntries.get(hex.toString()).aspect))) {
                pix = hex.toPixel(9);
                UtilsFX.bindTexture("textures/aspects/_unknown.png");
                GlStateManager.pushMatrix();
                GlStateManager.color(0.0f, 0.0f, 0.0f, 0.5f);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.translate((double) (x + 161) + pix.x, (double) (y + 75) + pix.y, 0.0);
                UtilsFX.drawTexturedQuadFull(0, 0, this.zLevel);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                continue;
            }
            if (this.note.hexEntries.get(hex.toString()).type == 1 || this.highlight.contains(hex.toString())) {
                pix = hex.toPixel(9);
                UtilsFX.drawTag((double) (x + 161) + pix.x, (double) (y + 75) + pix.y,
                        this.note.hexEntries.get(hex.toString()).aspect, 0.0f, 0,
                        (double) this.zLevel, 771, 1.0f, false);
                continue;
            }
            if (this.note.hexEntries.get(hex.toString()).type != 2) continue;
            pix = hex.toPixel(9);
            UtilsFX.drawTag((double) (x + 161) + pix.x, (double) (y + 75) + pix.y,
                    this.note.hexEntries.get(hex.toString()).aspect, 0.0f, 0,
                    (double) this.zLevel, 771, 0.66f, true);
        }
        GlStateManager.popMatrix();
    }

    private void checkConnections(HexUtils.Hex hex) {
        if (this.note == null) return;
        this.checked.add(hex.toString());
        for (int a = 0; a < 6; ++a) {
            HexUtils.Hex target = hex.getNeighbour(a);
            if (this.checked.contains(target.toString())
                    || !this.note.hexEntries.containsKey(target.toString())
                    || this.note.hexEntries.get(target.toString()).type < 1)
                continue;
            Aspect aspect1 = this.note.hexEntries.get(hex.toString()).aspect;
            Aspect aspect2 = this.note.hexEntries.get(target.toString()).aspect;

            IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
            if (knowledge == null) continue;
            if (!knowledge.hasDiscoveredAspect(aspect1) || !knowledge.hasDiscoveredAspect(aspect2)) continue;

            boolean validConnection;
            if (aspect1.isPrimal()) {
                validConnection = !aspect2.isPrimal()
                        && (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1);
            } else if (aspect2.isPrimal()) {
                validConnection = (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2);
            } else {
                validConnection = (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2)
                        || (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1);
            }
            if (!validConnection) continue;

            String k1 = hex.toString() + ":" + target.toString();
            String k2 = target.toString() + ":" + hex.toString();
            if (!this.lines.containsKey(k1) && !this.lines.containsKey(k2)) {
                this.lines.put(k1, new HexUtils.Hex[]{hex, target});
                this.highlight.add(target.toString());
            }
            this.checkConnections(target);
        }
    }

    private void drawRune(double x, double y, int rune, float alpha) {
        GlStateManager.pushMatrix();
        UtilsFX.bindTexture("textures/misc/script.png");
        GlStateManager.color(0.0f, 0.0f, 0.0f, alpha);
        GlStateManager.translate(x, y, 0.0);
        if (rune < 16) {
            GlStateManager.rotate(90.0f, 0.0f, 0.0f, -1.0f);
        }
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float var8 = 0.0625f * (float) rune;
        float var9 = var8 + 0.0625f;
        float var10 = 0.0f;
        float var11 = 1.0f;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-5.0, 5.0, (double) this.zLevel).tex((double) var9, (double) var11).endVertex();
        buffer.pos(5.0, 5.0, (double) this.zLevel).tex((double) var9, (double) var10).endVertex();
        buffer.pos(5.0, -5.0, (double) this.zLevel).tex((double) var8, (double) var10).endVertex();
        buffer.pos(-5.0, -5.0, (double) this.zLevel).tex((double) var8, (double) var11).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.butcount1 > System.nanoTime() || this.butcount2 > System.nanoTime()) {
            return;
        }
        int gx = (this.width - this.xSize) / 2;
        int gy = (this.height - this.ySize) / 2;

        // Combine button
        int var7 = mouseX - (gx + 35);
        int var8 = mouseY - (gy + 139);
        if (var7 >= 0 && var8 >= 0 && var7 < 32 && var8 < 16
                && this.butcount2 < System.nanoTime()
                && this.select1 != null && this.select2 != null) {
            this.butcount2 = System.nanoTime() + 200000000L;
            this.playButtonClick();
            this.playButtonCombine();

            IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
            boolean useBonus1 = knowledge != null
                    && this.tileEntity.bonusAspects.getAmount(this.select1) > 0;
            boolean useBonus2 = knowledge != null
                    && this.tileEntity.bonusAspects.getAmount(this.select2) > 0;

            PacketHandler.INSTANCE.sendToServer(new PacketAspectCombinationToServer(
                    this.player, this.tileEntity.getPos().getX(), this.tileEntity.getPos().getY(), this.tileEntity.getPos().getZ(),
                    this.select1, this.select2, useBonus1, useBonus2, true));
            return;
        }

        // Previous page
        var7 = mouseX - (gx + 27);
        var8 = mouseY - (gy + 121);
        if (this.page > 0 && var7 >= 0 && var8 >= 0 && var7 < 24 && var8 < 8) {
            --this.page;
            this.playButtonScroll();
            return;
        }

        // Next page
        var7 = mouseX - (gx + 51);
        var8 = mouseY - (gy + 121);
        if (this.page < this.lastPage && var7 >= 0 && var8 >= 0 && var7 < 24 && var8 < 8) {
            ++this.page;
            this.playButtonScroll();
            return;
        }

        // Clear select1
        if (this.select1 != null) {
            var7 = mouseX - (gx + 11);
            var8 = mouseY - (gy + 137);
            if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                this.select1 = null;
                this.playButtonAspect();
                return;
            }
        }

        // Clear select2
        if (this.select2 != null) {
            var7 = mouseX - (gx + 71);
            var8 = mouseY - (gy + 137);
            if (var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                this.select2 = null;
                this.playButtonAspect();
                return;
            }
        }

        // Hex click or duplicate
        if (this.note != null) {
            this.checkClickedHex(mouseX, mouseY, gx, gy);
            if (RESEARCHDUPE && this.note.isComplete()) {
                var7 = mouseX - (gx + 37);
                var8 = mouseY - (gy + 5);
                if (var7 >= 0 && var8 >= 0 && var7 < 24 && var8 < 24) {
                    this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 5);
                    this.playButtonClick();
                    return;
                }
            }
        }

        // RESEARCHER_2: right-click combine from aspect grid
        if (this.isCtrlKeyDown() && RESEARCHER_2) {
            Aspect aspect = this.getClickedAspect(mouseX, mouseY, gx, gy, true);
            if (aspect != null && !aspect.isPrimal()) {
                IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
                if (knowledge != null) {
                    AspectList aspects = knowledge.getAspectsDiscovered();
                    if (aspects != null
                            && (aspects.getAmount(aspect.getComponents()[0]) > 0 || this.tileEntity.bonusAspects.getAmount(aspect.getComponents()[0]) > 0)
                            && (aspects.getAmount(aspect.getComponents()[1]) > 0 || this.tileEntity.bonusAspects.getAmount(aspect.getComponents()[1]) > 0)) {
                        this.draggedAspect = null;
                        this.playButtonCombine();
                        boolean useBonus1 = this.tileEntity.bonusAspects.getAmount(aspect.getComponents()[0]) > 0;
                        boolean useBonus2 = this.tileEntity.bonusAspects.getAmount(aspect.getComponents()[1]) > 0;
                        PacketHandler.INSTANCE.sendToServer(new PacketAspectCombinationToServer(
                                this.player, this.tileEntity.getPos().getX(), this.tileEntity.getPos().getY(), this.tileEntity.getPos().getZ(),
                                aspect.getComponents()[0], aspect.getComponents()[1], useBonus1, useBonus2, true));
                    }
                }
            }
        }
    }

    private void checkClickedHex(int mx, int my, int gx, int gy) {
        int mouseX = mx - (gx + 169);
        int mouseY = my - (gy + 83);
        HexUtils.Hex hp = new HexUtils.Pixel(mouseX, mouseY).toHex(9);
        if (this.note.hexes.containsKey(hp.toString())
                && this.note.hexEntries.get(hp.toString()).type == 2) {
            this.playButtonCombine();
            this.playButtonErase();
            PacketHandler.INSTANCE.sendToServer(new PacketAspectPlaceToServer(
                    this.player, (byte) hp.q, (byte) hp.r,
                    this.tileEntity.getPos().getX(), this.tileEntity.getPos().getY(), this.tileEntity.getPos().getZ(),
                    null));
        }
    }

    private Aspect getClickedAspect(int mx, int my, int gx, int gy, boolean ignoreZero) {
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(this.player);
        if (knowledge == null) return null;
        AspectList aspects = knowledge.getAspectsDiscovered();
        if (aspects != null) {
            int count = 0;
            int drawn = 0;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (++count - 1 < this.page * 5 || drawn >= 25) continue;
                int xx = drawn / 5 * 16;
                int yy = drawn % 5 * 16;
                int var7 = mx - (gx + xx + 10);
                int var8 = my - (gy + yy + 40);
                if ((ignoreZero
                        || aspects.getAmount(aspect) > 0
                        || this.tileEntity.bonusAspects.getAmount(aspect) > 0)
                        && var7 >= 0 && var8 >= 0 && var7 < 16 && var8 < 16) {
                    return aspect;
                }
                ++drawn;
            }
        }
        return null;
    }

    // ---- Sound effects ----

    private void playButtonClick() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.CAMERACLACK, SoundCategory.PLAYERS, 0.4f, 1.0f);
        }
    }

    private void playButtonAspect() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.HHOFF, SoundCategory.PLAYERS, 0.2f,
                    1.0f + this.mc.world.rand.nextFloat() * 0.1f);
        }
    }

    private void playButtonCombine() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.HHON, SoundCategory.PLAYERS, 0.3f, 1.0f);
        }
    }

    private void playButtonWrite() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.WRITE, SoundCategory.PLAYERS, 0.2f, 1.0f);
        }
    }

    private void playButtonErase() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.ERASE, SoundCategory.PLAYERS, 0.2f,
                    1.0f + this.mc.world.rand.nextFloat() * 0.1f);
        }
    }

    private void playButtonScroll() {
        if (this.mc.player != null) {
            this.mc.player.world.playSound(this.mc.player, this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ,
                    TCSounds.KEY, SoundCategory.PLAYERS, 0.3f, 1.0f);
        }
    }

    // ---- Orbs ----

    private void drawOrb(double x, double y) {
        int count = this.mc.player.ticksExisted;
        float red = 0.7f + MathHelper.sin((float) ((double) count + x) / 10.0f) * 0.15f + 0.15f;
        float green = 0.7f + MathHelper.sin((float) ((double) count + x + y) / 11.0f) * 0.15f + 0.15f;
        float blue = 0.7f + MathHelper.sin((float) ((double) count + y) / 12.0f) * 0.15f + 0.15f;
        Color c = new Color(red, green, blue);
        this.drawOrb(x, y, c.getRGB());
    }

    private void drawOrb(double x, double y, int color) {
        int count = this.mc.player.ticksExisted;
        Color c = new Color(color);
        float red = (float) c.getRed() / 255.0f;
        float green = (float) c.getGreen() / 255.0f;
        float blue = (float) c.getBlue() / 255.0f;
        if (Config.colorBlind) {
            red /= 1.8f;
            green /= 1.8f;
            blue /= 1.8f;
        }
        GlStateManager.pushMatrix();
        UtilsFX.bindTexture(PARTICLE_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x, y, 0.0);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        int part = count % 8;
        float var8 = 0.5f + (float) part / 8.0f;
        float var9 = var8 + 0.0624375f;
        float var10 = 0.5f;
        float var11 = var10 + 0.0624375f;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(0.0, 16.0, (double) this.zLevel).tex((double) var9, (double) var11).color(red, green, blue, 1.0f).endVertex();
        buffer.pos(16.0, 16.0, (double) this.zLevel).tex((double) var9, (double) var10).color(red, green, blue, 1.0f).endVertex();
        buffer.pos(16.0, 0.0, (double) this.zLevel).tex((double) var8, (double) var10).color(red, green, blue, 1.0f).endVertex();
        buffer.pos(0.0, 0.0, (double) this.zLevel).tex((double) var8, (double) var11).color(red, green, blue, 1.0f).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    class Coord2D {
        int x;
        int y;

        Coord2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Rune {
        int q;
        int r;
        long start;
        long decay;
        int rune;

        public Rune(int q, int r, long start, long decay, int rune) {
            this.q = q;
            this.r = r;
            this.start = start;
            this.decay = decay;
            this.rune = rune;
        }
    }
}
