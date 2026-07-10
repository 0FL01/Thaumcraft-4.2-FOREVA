package thaumcraft.client.lib;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemFocusPouch;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.items.wands.foci.FocusTrade;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
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
