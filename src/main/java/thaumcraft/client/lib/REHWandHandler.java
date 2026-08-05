package thaumcraft.client.lib;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemFocusPouch;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.items.wands.foci.FocusTrade;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.utils.ConnectedTextureUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public class REHWandHandler {

    static float radialHudScale = 0.0F;
    private static final DecimalFormat VIS_FORMAT = new DecimalFormat("#######.##");
    private static final DecimalFormat COOLDOWN_FORMAT = new DecimalFormat("0.0");

    private final TreeMap<String, Integer> foci = new TreeMap<>();
    private final HashMap<String, ItemStack> fociItem = new HashMap<>();
    private final HashMap<String, Boolean> fociHover = new HashMap<>();
    private final HashMap<String, Float> fociScale = new HashMap<>();
    private long lastTime = 0L;
    private boolean lastState = false;
    private boolean prevMouseButton = false;
    private final int[][] oldVis = new int[9][6];
    private final boolean[] oldVisValid = new boolean[9];
    private long nextVisSnapshot = 0L;
    private static final String ARCHITECT_ARROW_TEXTURE = "textures/misc/architect_arrows.png";
    private final ArchitectRefreshLimiter architectRefreshLimiter = new ArchitectRefreshLimiter();
    private List<BlockCoordinates> architectBlocks = Collections.emptyList();
    private Set<BlockPos> architectBlockSet = Collections.emptySet();
    private EntityPlayer architectPlayer;
    private Object architectWorld;
    private ItemStack architectWandStack;
    private ItemStack architectFocusStack = ItemStack.EMPTY;
    private BlockPos architectTarget;
    private int architectSide = -1;
    private int architectAreaX;
    private int architectAreaY;
    private int architectAreaZ;
    private int architectAreaDim;

    public void handleCastingWandHud(Minecraft mc, long time, RenderGameOverlayEvent event) {
        if (mc.player == null || !mc.inGameHasFocus || mc.isGamePaused()) {
            return;
        }
        ItemStack held = mc.player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemWandCasting)) {
            return;
        }

        ItemWandCasting wand = (ItemWandCasting) held.getItem();
        AspectList vis = wand.getAllVis(held);
        List<Aspect> primals = Aspect.getPrimalAspects();
        int slot = MathHelper.clamp(mc.player.inventory.currentItem, 0, oldVis.length - 1);
        if (!oldVisValid[slot]) {
            snapshotVis(slot, vis, primals);
        }

        ScaledResolution resolution = event.getResolution();
        int hudY = Config.dialBottom ? resolution.getScaledHeight() - 32 : 0;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0.0F, hudY, 0.0F);

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            UtilsFX.bindTexture("textures/gui/hud.png");
            UtilsFX.drawTexturedQuad(0, 0, 0, 0, 64, 64, -90.0D);
            GlStateManager.popMatrix();

            GlStateManager.translate(16.0F, 16.0F, 0.0F);

            ItemFocusBasic focus = wand.getFocus(held);
            ItemStack focusStack = wand.getFocusItem(held);
            AspectList focusCost = focus == null || focusStack.isEmpty() ? null : focus.getVisCost(focusStack);
            int maxVis = Math.max(1, ItemWandCasting.getMaxVis(held));

            for (int i = 0; i < primals.size() && i < 6; i++) {
                Aspect aspect = primals.get(i);
                int amount = vis.getAmount(aspect);
                GlStateManager.pushMatrix();
                if (!Config.dialBottom) {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }
                GlStateManager.rotate(-15.0F + i * 24.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, -32.0F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 1.0F);

                int fill = MathHelper.clamp((int)(30.0F * amount / maxVis), 0, 30);
                int color = aspect.getColor();
                GlStateManager.color((color >> 16 & 255) / 255.0F,
                        (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 0.8F);
                UtilsFX.bindTexture("textures/gui/hud.png");
                UtilsFX.drawTexturedQuad(-4, 35 - fill, 104, 0, 8, fill, -89.0D);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                UtilsFX.drawTexturedQuad(-8, -3, 72, 0, 16, 42, -88.0D);

                boolean usedByFocus = focusCost != null && focusCost.getAmount(aspect) > 0;
                if (usedByFocus) {
                    UtilsFX.drawTexturedQuad(-4, -8, 136, 0, 8, 8, -87.0D);
                }
                int previous = oldVis[slot][i];
                if (previous != amount) {
                    int arrowU = previous > amount ? 128 : 120;
                    UtilsFX.drawTexturedQuad(-4, usedByFocus ? -16 : -8, arrowU, 0, 8, 8, -86.0D);
                }

                if (mc.player.isSneaking()) {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                    String stored = Integer.toString(amount / 100);
                    mc.fontRenderer.drawString(stored, -32, -4, 0xFFFFFF);
                    if (usedByFocus) {
                        float cost = focusCost.getAmount(aspect)
                                * ItemWandCasting.getConsumptionModifier(held, mc.player, aspect, false) / 100.0F;
                        mc.fontRenderer.drawString(VIS_FORMAT.format(cost), 8, -4, 0xFFFFFF);
                    }
                }
                GlStateManager.popMatrix();
            }

            renderCenterItem(mc, held, wand, focus, focusStack);
        } finally {
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (time >= nextVisSnapshot) {
            snapshotVis(slot, vis, primals);
            nextVisSnapshot = time + 1000L;
        }
    }

    private void renderCenterItem(Minecraft mc, ItemStack held, ItemWandCasting wand,
                                  ItemFocusBasic focus, ItemStack focusStack) {
        ItemStack display = focusStack;
        int count = -1;
        if (focus instanceof FocusTrade) {
            ItemStack picked = ((FocusTrade) focus).getPickedBlock(held);
            if (!picked.isEmpty()) {
                display = picked;
                count = countMatchingItems(mc.player, picked);
            }
        }

        GlStateManager.pushMatrix();
        if (!display.isEmpty()) {
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(display, -8, -8);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }
        if (count >= 0) {
            String text = Integer.toString(count);
            int width = mc.fontRenderer.getStringWidth(text);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -mc.fontRenderer.FONT_HEIGHT, 500.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawString(text, 16 - width, 24, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
        float cooldown = WandManager.getCooldown(mc.player);
        if (cooldown > 0.0F) {
            String text = COOLDOWN_FORMAT.format(cooldown) + "s";
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 150.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawString(text, -mc.fontRenderer.getStringWidth(text) / 2, -4, 0xFFFFFF);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private static int countMatchingItems(EntityPlayer player, ItemStack picked) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && ItemStack.areItemsEqual(stack, picked)
                    && ItemStack.areItemStackTagsEqual(stack, picked)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void snapshotVis(int slot, AspectList vis, List<Aspect> primals) {
        for (int i = 0; i < primals.size() && i < 6; i++) {
            oldVis[slot][i] = vis.getAmount(primals.get(i));
        }
        oldVisValid[slot] = true;
    }

    public boolean handleArchitectOverlay(ItemStack stack, DrawBlockHighlightEvent event, int playerTicks,
                                          RayTraceResult target) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IArchitect) || target.getBlockPos() == null
                || target.sideHit == null) {
            return false;
        }

        EntityPlayer player = event.getPlayer();
        IArchitect architect = (IArchitect) stack.getItem();
        ItemStack focusStack = ItemStack.EMPTY;
        int areaX = 0;
        int areaY = 0;
        int areaZ = 0;
        int areaDim = 0;
        if (stack.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) stack.getItem();
            ItemFocusBasic focus = wand.getFocus(stack);
            focusStack = wand.getFocusItem(stack);
            if (focus != null && !focusStack.isEmpty()) {
                int max = focus.getMaxAreaSize(focusStack);
                areaX = WandManager.getAreaX(stack, max);
                areaY = WandManager.getAreaY(stack, max);
                areaZ = WandManager.getAreaZ(stack, max);
                areaDim = WandManager.getAreaDim(stack);
            }
        }

        BlockPos targetPos = target.getBlockPos().toImmutable();
        int side = target.sideHit.getIndex();
        boolean contextChanged = player != this.architectPlayer
                || player.world != this.architectWorld
                || stack != this.architectWandStack
                || !ItemStack.areItemStacksEqual(focusStack, this.architectFocusStack)
                || !targetPos.equals(this.architectTarget)
                || side != this.architectSide
                || areaX != this.architectAreaX
                || areaY != this.architectAreaY
                || areaZ != this.architectAreaZ
                || areaDim != this.architectAreaDim;
        if (contextChanged) {
            this.architectPlayer = player;
            this.architectWorld = player.world;
            this.architectWandStack = stack;
            this.architectFocusStack = focusStack.copy();
            this.architectTarget = targetPos;
            this.architectSide = side;
            this.architectAreaX = areaX;
            this.architectAreaY = areaY;
            this.architectAreaZ = areaZ;
            this.architectAreaDim = areaDim;
            this.architectBlocks = Collections.emptyList();
            this.architectBlockSet = Collections.emptySet();
        }

        if (this.architectRefreshLimiter.shouldRefresh(playerTicks)) {
            ArrayList<BlockCoordinates> blocks = architect.getArchitectBlocks(stack, player.world,
                    targetPos.getX(), targetPos.getY(), targetPos.getZ(), side, player);
            if (blocks == null || blocks.isEmpty()) {
                this.architectBlocks = Collections.emptyList();
                this.architectBlockSet = Collections.emptySet();
            } else {
                this.architectBlocks = new ArrayList<>(blocks);
                Set<BlockPos> positions = new HashSet<>(blocks.size());
                for (BlockCoordinates coordinates : blocks) {
                    positions.add(new BlockPos(coordinates.x, coordinates.y, coordinates.z));
                }
                this.architectBlockSet = positions;
            }
        }

        if (this.architectBlocks.isEmpty()) {
            return false;
        }
        drawArchitectAxis(targetPos, event.getPartialTicks(), player,
                architect.showAxis(stack, player.world, player, side, IArchitect.EnumAxis.X),
                architect.showAxis(stack, player.world, player, side, IArchitect.EnumAxis.Y),
                architect.showAxis(stack, player.world, player, side, IArchitect.EnumAxis.Z));
        drawArchitectBlocks(playerTicks, event.getPartialTicks(), player);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }

    private void drawArchitectBlocks(int ticks, float partialTicks, EntityPlayer player) {
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(-playerX, -playerY, -playerZ);
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (BlockPos pos : this.architectBlockSet) {
                float red = MathHelper.sin(ticks / 2.0F + pos.getX()) * 0.2F + 0.3F;
                float green = MathHelper.sin(ticks / 3.0F + pos.getY()) * 0.2F + 0.3F;
                float blue = MathHelper.sin(ticks / 4.0F + pos.getZ()) * 0.2F + 0.8F;
                for (EnumFacing face : EnumFacing.VALUES) {
                    if (!isArchitectFaceExterior(this.architectBlockSet, pos, face)) {
                        continue;
                    }
                    int texture = ConnectedTextureUtils.getTextureIndex(pos, face.getIndex(),
                            this.architectBlockSet::contains);
                    TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(
                            "thaumcraft:blocks/warded_glass_" + (texture + 1));
                    addArchitectFace(buffer, pos, face, sprite, red, green, blue);
                }
            }
            Tessellator.getInstance().draw();
        } finally {
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
        }
    }

    static boolean isArchitectFaceExterior(Set<BlockPos> blocks, BlockPos pos, EnumFacing face) {
        return !blocks.contains(pos.offset(face));
    }

    private static void addArchitectFace(BufferBuilder buffer, BlockPos pos, EnumFacing face,
                                         TextureAtlasSprite sprite, float red, float green, float blue) {
        double minX = pos.getX() - 0.001D;
        double minY = pos.getY() - 0.001D;
        double minZ = pos.getZ() - 0.001D;
        double maxX = pos.getX() + 1.001D;
        double maxY = pos.getY() + 1.001D;
        double maxZ = pos.getZ() + 1.001D;
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        switch (face) {
            case DOWN:
                addArchitectVertex(buffer, minX, minY, maxZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, minY, maxZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, minY, minZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, minX, minY, minZ, minU, minV, red, green, blue);
                break;
            case UP:
                addArchitectVertex(buffer, minX, maxY, minZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, minZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, maxZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, minX, maxY, maxZ, minU, minV, red, green, blue);
                break;
            case NORTH:
                addArchitectVertex(buffer, maxX, minY, minZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, minX, minY, minZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, minX, maxY, minZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, minZ, minU, minV, red, green, blue);
                break;
            case SOUTH:
                addArchitectVertex(buffer, minX, minY, maxZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, minY, maxZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, maxZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, minX, maxY, maxZ, minU, minV, red, green, blue);
                break;
            case WEST:
                addArchitectVertex(buffer, minX, minY, minZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, minX, minY, maxZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, minX, maxY, maxZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, minX, maxY, minZ, minU, minV, red, green, blue);
                break;
            case EAST:
                addArchitectVertex(buffer, maxX, minY, maxZ, minU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, minY, minZ, maxU, maxV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, minZ, maxU, minV, red, green, blue);
                addArchitectVertex(buffer, maxX, maxY, maxZ, minU, minV, red, green, blue);
                break;
        }
    }

    private static void addArchitectVertex(BufferBuilder buffer, double x, double y, double z, float u, float v,
                                           float red, float green, float blue) {
        buffer.pos(x, y, z).tex(u, v).color(red, green, blue, 0.2F).endVertex();
    }

    private static void drawArchitectAxis(BlockPos pos, float partialTicks, EntityPlayer player,
                                          boolean showX, boolean showY, boolean showZ) {
        if (!showX && !showY && !showZ) {
            return;
        }
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        float red = MathHelper.sin(player.ticksExisted / 4.0F + pos.getX()) * 0.2F + 0.3F;
        float green = MathHelper.sin(player.ticksExisted / 3.0F + pos.getY()) * 0.2F + 0.3F;
        float blue = MathHelper.sin(player.ticksExisted / 2.0F + pos.getZ()) * 0.2F + 0.8F;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.translate(pos.getX() + 0.5D - playerX, pos.getY() + 0.5D - playerY,
                    pos.getZ() + 0.5D - playerZ);
            UtilsFX.bindTexture(ARCHITECT_ARROW_TEXTURE);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            if (showX) {
                drawArchitectArrow(red, green, blue);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                drawArchitectArrow(red, green, blue);
                GlStateManager.popMatrix();
            }
            if (showZ) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                drawArchitectArrow(red, green, blue);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                drawArchitectArrow(red, green, blue);
                GlStateManager.popMatrix();
            }
            if (showY) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                drawArchitectArrow(red, green, blue);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                drawArchitectArrow(red, green, blue);
                GlStateManager.popMatrix();
            }
        } finally {
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void drawArchitectArrow(float red, float green, float blue) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 1.0D).color(red, green, blue, 0.33F).endVertex();
        buffer.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 1.0D).color(red, green, blue, 0.33F).endVertex();
        buffer.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 0.0D).color(red, green, blue, 0.33F).endVertex();
        buffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 0.0D).color(red, green, blue, 0.33F).endVertex();
        Tessellator.getInstance().draw();
    }

    static final class ArchitectRefreshLimiter {
        static final int REFRESH_TICKS = 5;
        private int lastRefreshTick = Integer.MIN_VALUE;

        boolean shouldRefresh(int playerTicks) {
            if (this.lastRefreshTick == Integer.MIN_VALUE || playerTicks < this.lastRefreshTick
                    || playerTicks - this.lastRefreshTick >= REFRESH_TICKS) {
                this.lastRefreshTick = playerTicks;
                return true;
            }
            return false;
        }
    }

    public void handleFociRadial(Minecraft mc, long time, RenderGameOverlayEvent event) {
        if (!KeyHandler.radialActive && radialHudScale <= 0.0F) {
            return;
        }

        if (KeyHandler.radialActive) {
            if (mc.currentScreen != null) {
                KeyHandler.radialActive = false;
                KeyHandler.radialLock = true;
                mc.displayGuiScreen(null);
                mc.setIngameFocus();
                return;
            }

            if (radialHudScale == 0.0F) {
                foci.clear();
                fociItem.clear();
                fociHover.clear();
                fociScale.clear();

                EntityPlayer player = mc.player;
                int pouchCount = 0;

                // Scan Baubles slots for focus pouches
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
                if (baubles != null) {
                    for (int slot = 0; slot < baubles.getSlots(); slot++) {
                        ItemStack stack = baubles.getStackInSlot(slot);
                        if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) {
                            ++pouchCount;
                            ItemStack[] inv = ((ItemFocusPouch) stack.getItem()).getInventory(stack);
                            for (int q = 0; q < inv.length; q++) {
                                ItemStack focus = inv[q];
                                if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
                                    String key = ((ItemFocusBasic) focus.getItem()).getSortingHelper(focus);
                                    foci.put(key, q + pouchCount * 1000);
                                    fociItem.put(key, focus.copy());
                                    fociScale.put(key, 1.0F);
                                    fociHover.put(key, false);
                                }
                            }
                        }
                    }
                }

                // Scan player inventory
                for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
                    ItemStack stack = player.inventory.mainInventory.get(slot);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusBasic) {
                        String key = ((ItemFocusBasic) stack.getItem()).getSortingHelper(stack);
                        foci.put(key, slot);
                        fociItem.put(key, stack.copy());
                        fociScale.put(key, 1.0F);
                        fociHover.put(key, false);
                    }
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) {
                        ++pouchCount;
                        ItemStack[] inv = ((ItemFocusPouch) stack.getItem()).getInventory(stack);
                        for (int q = 0; q < inv.length; q++) {
                            ItemStack focus = inv[q];
                            if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
                                String key = ((ItemFocusBasic) focus.getItem()).getSortingHelper(focus);
                                foci.put(key, q + pouchCount * 1000);
                                fociItem.put(key, focus.copy());
                                fociScale.put(key, 1.0F);
                                fociHover.put(key, false);
                            }
                        }
                    }
                }

                ItemStack offhand = player.getHeldItemOffhand();
                if (!offhand.isEmpty() && offhand.getItem() instanceof ItemFocusPouch) {
                    ++pouchCount;
                    ItemStack[] inv = ((ItemFocusPouch) offhand.getItem()).getInventory(offhand);
                    for (int q = 0; q < inv.length; q++) {
                        ItemStack focus = inv[q];
                        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
                            String key = ((ItemFocusBasic) focus.getItem()).getSortingHelper(focus);
                            foci.put(key, q + pouchCount * 1000);
                            fociItem.put(key, focus.copy());
                            fociScale.put(key, 1.0F);
                            fociHover.put(key, false);
                        }
                    }
                }

                // Grab mouse so we can track position
                if (!foci.isEmpty() && mc.inGameHasFocus) {
                    mc.inGameHasFocus = false;
                    mc.mouseHelper.ungrabMouseCursor();
                }
            }
        } else if (mc.currentScreen == null && lastState) {
            // Key released: re-grab mouse
            if (Display.isActive() && !mc.inGameHasFocus) {
                mc.inGameHasFocus = true;
                mc.mouseHelper.grabMouseCursor();
            }
            lastState = false;
        }

        ScaledResolution resolution = event.getResolution();
        double sw = resolution.getScaledWidth_double();
        double sh = resolution.getScaledHeight_double();

        // Lock cursor within radial circle while selector is open
        if (KeyHandler.radialActive && radialHudScale > 0.0F && !foci.isEmpty()) {
            float radius = 16.0F + fociItem.size() * 2.5F;
            float clampRadius = radius * radialHudScale + 15.0F;
            float centerX = (float)(sw / 2.0);
            float centerY = (float)(sh / 2.0);

            int rawX = Mouse.getX();
            int rawY = Mouse.getY();
            double mx = (double) rawX * sw / (double) mc.displayWidth;
            double my = sh - (double) rawY * sh / (double) mc.displayHeight - 1.0;

            double dx = mx - centerX;
            double dy = my - centerY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > clampRadius && dist > 0.0) {
                double clampedX = centerX + dx / dist * clampRadius;
                double clampedY = centerY + dy / dist * clampRadius;
                int newRawX = (int)(clampedX * mc.displayWidth / sw);
                int newRawY = (int)((sh - clampedY - 1.0) * mc.displayHeight / sh);
                Mouse.setCursorPosition(newRawX, newRawY);
            }
        }

        renderFocusRadialHUD(sw, sh, time, event.getPartialTicks());

        // Animate scales and handle selection
        if (time > lastTime) {
            for (String key : fociHover.keySet()) {
                if (fociHover.get(key)) {
                    // Hovered: send selection if key released
                    if (!KeyHandler.radialActive && !KeyHandler.radialLock) {
                        PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(mc.player, key));
                        KeyHandler.radialLock = true;
                    }
                    // Animate scale up
                    if (fociScale.get(key) < 1.3F) {
                        fociScale.put(key, fociScale.get(key) + 0.025F);
                    }
                } else {
                    // Animate scale down
                    if (fociScale.get(key) > 1.0F) {
                        fociScale.put(key, fociScale.get(key) - 0.025F);
                    }
                }
            }

            // Animate overall HUD scale
            if (!KeyHandler.radialActive) {
                radialHudScale -= 0.05F;
            } else if (radialHudScale < 1.0F) {
                radialHudScale += 0.05F;
            }
            if (radialHudScale > 1.0F) radialHudScale = 1.0F;
            if (radialHudScale < 0.0F) {
                radialHudScale = 0.0F;
                KeyHandler.radialLock = false;
            }

            lastTime = time + 5L;
            lastState = KeyHandler.radialActive;
        }
    }

    private void renderFocusRadialHUD(double sw, double sh, long time, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem ri = mc.getRenderItem();

        if (mc.player == null) return;
        ItemStack held = mc.player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemWandCasting)) return;

        ItemWandCasting wand = (ItemWandCasting) held.getItem();
        ItemFocusBasic focus = wand.getFocus(held);

        // Mouse position in scaled coordinates (use current pos, not event pos)
        int rawX = Mouse.getX();
        int rawY = Mouse.getY();
        int mouseX = (int) ((double) rawX * sw / (double) mc.displayWidth);
        int mouseY = (int) (sh - (double) rawY * sh / (double) mc.displayHeight - 1.0);

        // Click detection with debounce
        boolean mouseDown = Mouse.isButtonDown(0);
        boolean justClicked = mouseDown && !prevMouseButton;
        prevMouseButton = mouseDown;

        if (fociItem.isEmpty()) return;

        // Save and set orthographic projection
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, sw, sh, 0.0, 1000.0, 3000.0);

        // Save and set modelview
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(sw / 2.0), (float)(sh / 2.0), 0.0F);

        ItemStack tooltipStack = null;
        float radius = 16.0F + (float) fociItem.size() * 2.5F;

        // Draw spinning radial background 1
        UtilsFX.bindTexture("textures/misc/radial.png");
        GlStateManager.pushMatrix();
        GlStateManager.rotate(partialTicks + (float)(mc.player.ticksExisted % 720) / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.renderQuadCenteredFromTexture(radius * 2.75F * radialHudScale, 0.5F, 0.5F, 0.5F, 200, GL11.GL_ONE_MINUS_SRC_ALPHA, 0.5F);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();

        // Draw spinning radial background 2 (counter-rotating)
        UtilsFX.bindTexture("textures/misc/radial2.png");
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-(partialTicks + (float)(mc.player.ticksExisted % 720) / 2.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.renderQuadCenteredFromTexture(radius * 2.55F * radialHudScale, 0.5F, 0.5F, 0.5F, 200, GL11.GL_ONE_MINUS_SRC_ALPHA, 0.5F);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();

        // Draw current focus in center
        if (focus != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            ItemStack centerItem = wand.getFocusItem(held).copy();
            centerItem.setTagCompound(null);
            ri.renderItemAndEffectIntoGUI(centerItem, -8, -8);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();

            // Check if mouse hovers center (current focus)
            int mx = (int)((double) mouseX - sw / 2.0);
            int my = (int)((double) mouseY - sh / 2.0);
            if (mx >= -10 && mx <= 10 && my >= -10 && my <= 10) {
                tooltipStack = wand.getFocusItem(held);
            }
        }

        // Scale for radial items
        GlStateManager.scale(radialHudScale, radialHudScale, radialHudScale);
        float currentRot = -90.0F * radialHudScale;
        float pieSlice = 360.0F / (float) fociItem.size();

        // Draw each focus item in a circle
        String key = foci.firstKey();
        for (int a = 0; a < fociItem.size(); a++) {
            double xx = MathHelper.cos(currentRot / 180.0F * (float) Math.PI) * radius;
            double yy = MathHelper.sin(currentRot / 180.0F * (float) Math.PI) * radius;
            currentRot += pieSlice;

            GlStateManager.pushMatrix();
            GlStateManager.translate((float)xx, (float)yy, 100.0F);
            float scale = fociScale.get(key);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();
            ItemStack item = fociItem.get(key).copy();
            item.setTagCompound(null);
            ri.renderItemAndEffectIntoGUI(item, -8, -8);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();

            // Hover detection
            if (!KeyHandler.radialLock && KeyHandler.radialActive) {
                int mx = (int)((double) mouseX - sw / 2.0 - xx);
                int my = (int)((double) mouseY - sh / 2.0 - yy);
                if (mx >= -10 && mx <= 10 && my >= -10 && my <= 10) {
                    fociHover.put(key, true);
                    tooltipStack = fociItem.get(key);
                    if (justClicked) {
                        KeyHandler.radialActive = false;
                        KeyHandler.radialLock = true;
                        PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(mc.player, key));
                        break;
                    }
                } else {
                    fociHover.put(key, false);
                }
            }

            key = foci.higherKey(key);
        }

        GlStateManager.popMatrix();

        // Draw tooltip for hovered focus
        if (tooltipStack != null) {
            UtilsFX.drawCustomTooltip(mc.currentScreen, ri, mc.fontRenderer,
                    tooltipStack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), -4, 20, 11);
        }

        // Restore modelview
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        // Restore projection
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }
}
