package thaumcraft.client.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class GuiArcaneWorkbench extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_arcaneworkbench.png");
    private static final int GHOST_ALPHA = 168;
    private static final float GHOST_SHADE = 0.33F;
    private static final FloatBuffer BUILTIN_GHOST_COLOR = BufferUtils.createFloatBuffer(4);

    static {
        BUILTIN_GHOST_COLOR.put(GHOST_SHADE).put(GHOST_SHADE).put(GHOST_SHADE)
                .put(GHOST_ALPHA / 255.0F).flip();
    }

    private final TileArcaneWorkbench tileEntity;
    private final InventoryPlayer playerInventory;
    private final ContainerArcaneWorkbench workbenchContainer;
    private final int[][] aspectLocations = new int[][]{
            {72, 21}, {24, 43}, {24, 102}, {72, 124}, {120, 102}, {120, 43}
    };
    private final ArrayList<Aspect> primals = Aspect.getPrimalAspects();

    public GuiArcaneWorkbench(InventoryPlayer playerInventory, TileArcaneWorkbench tile) {
        this(new ContainerArcaneWorkbench(playerInventory, tile), playerInventory, tile);
    }

    private GuiArcaneWorkbench(ContainerArcaneWorkbench container, InventoryPlayer playerInventory, TileArcaneWorkbench tile) {
        super(container);
        this.workbenchContainer = container;
        this.tileEntity = tile;
        this.playerInventory = playerInventory;
        this.xSize = 190;
        this.ySize = 234;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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
                && !((ItemWandCasting) wandStack.getItem()).isStaff(wandStack)
                ? (ItemWandCasting) wandStack.getItem() : null;

        this.workbenchContainer.refreshResult();
        ItemStack result = this.workbenchContainer.getArcanePreviewResult();
        AspectList cost = this.workbenchContainer.getArcanePreviewCost();
        if (cost.size() > 0) {
            drawArcaneCostTags(cost, wandStack, wand);
        }
        if (wand != null && cost.size() > 0 && !wand.consumeAllVisCrafting(wandStack, this.playerInventory.player, cost, false)) {
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
            UtilsFX.drawTag(x, y, primal, drawAmount, 0, this.zLevel, 771, alpha, false);
        }
    }

    private void drawInsufficientVisResult(ItemStack result) {
        if (result.isEmpty()) return;

        RenderHelper.enableGUIStandardItemLighting();
        renderGhostItem(result, this.guiLeft + 160, this.guiTop + 64);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, result, this.guiLeft + 160, this.guiTop + 64, null);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.guiLeft + 168), (float) (this.guiTop + 46), 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        String text = "Insufficient vis";
        this.fontRenderer.drawString(text, -this.fontRenderer.getStringWidth(text) / 2, 0, 0xEE6E6E);
        GlStateManager.popMatrix();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderGhostItem(ItemStack stack, int x, int y) {
        IBakedModel model = this.itemRender.getItemModelWithOverrides(stack, null, this.playerInventory.player);
        if (model.isBuiltInRenderer()) {
            renderBuiltinGhost(stack, x, y);
            return;
        }

        GlStateManager.pushMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.translate(x + 8.0F, y + 8.0F, 100.0F + this.zLevel);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        for (EnumFacing facing : EnumFacing.values()) {
            renderGhostQuads(buffer, model.getQuads(null, facing, 0L), stack);
        }
        renderGhostQuads(buffer, model.getQuads(null, null, 0L), stack);
        tessellator.draw();

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private void renderGhostQuads(BufferBuilder buffer, List<BakedQuad> quads, ItemStack stack) {
        for (BakedQuad quad : quads) {
            int color = quad.hasTintIndex()
                    ? this.mc.getItemColors().colorMultiplier(stack, quad.getTintIndex())
                    : 0xFFFFFF;
            LightUtil.renderQuadColor(buffer, quad, ghostColor(color));
        }
    }

    private void renderBuiltinGhost(ItemStack stack, int x, int y) {
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL13.GL_CONSTANT);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_ALPHA, GL13.GL_CONSTANT);
        BUILTIN_GHOST_COLOR.position(0);
        GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, BUILTIN_GHOST_COLOR);
        try {
            this.itemRender.renderItemIntoGUI(stack, x, y);
        } finally {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            GlStateManager.setActiveTexture(activeTexture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    static int ghostColor(int color) {
        int red = Math.round(((color >> 16) & 0xFF) * GHOST_SHADE);
        int green = Math.round(((color >> 8) & 0xFF) * GHOST_SHADE);
        int blue = Math.round((color & 0xFF) * GHOST_SHADE);
        return GHOST_ALPHA << 24 | red << 16 | green << 8 | blue;
    }
}
