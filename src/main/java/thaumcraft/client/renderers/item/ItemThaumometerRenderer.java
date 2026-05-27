package thaumcraft.client.renderers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.ScanResult;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.relics.ItemThaumometer;
import thaumcraft.common.lib.research.ScanManager;
import java.util.Map;

public class ItemThaumometerRenderer extends TileEntityItemStackRenderer {

    private static final ResourceLocation SCANNER_OBJ =
            new ResourceLocation("thaumcraft", "textures/models/scanner.obj");
    private static final ResourceLocation SCANNER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/scanner.png");
    private static final ResourceLocation SCANSCREEN_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/scanscreen.png");
    private static final float TC4_TO_TC6_VERTICAL_CENTER = -0.1F;
    private static final float TC4_TO_TC6_Y_ROTATION = -90.0F;
    private static final long DEBUG_LOG_INTERVAL_MS = 1500L;
    private static final float HUD_SCALE_MULTIPLIER = 1.875F;
    private static final ThreadLocal<ItemCameraTransforms.TransformType> CURRENT_TRANSFORM =
            ThreadLocal.withInitial(() -> ItemCameraTransforms.TransformType.NONE);
    private static long lastReadoutDebugLogMs = 0L;

    private final CCModel scannerModel = loadScannerModel();

    public static void setTransformType(ItemCameraTransforms.TransformType transformType) {
        CURRENT_TRANSFORM.set(transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || scannerModel == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        ItemCameraTransforms.TransformType transformType = CURRENT_TRANSFORM.get();

        GlStateManager.pushMatrix();
        try {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            // The held pose is intentionally left to the donor display transforms.
            // Only the scanner-mesh basis mismatch is adapted here; local first-person
            // arm/equipped-progress transforms were tried and rolled back after they
            // broke the visual placement in live testing.
            applyTc6ScannerBasis();
            renderScannerModel(mc);
            renderScannerDisplay(mc, stack, player, transformType);
        } finally {
            setTransformType(ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }
    }

    private void applyTc6ScannerBasis() {
        GlStateManager.translate(0.0F, TC4_TO_TC6_VERTICAL_CENTER, 0.0F);
        GlStateManager.rotate(TC4_TO_TC6_Y_ROTATION, 0.0F, 1.0F, 0.0F);
    }

    private void renderScannerModel(Minecraft mc) {
        mc.getTextureManager().bindTexture(SCANNER_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableCull();
        CCRenderState.reset();
        CCRenderState.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        scannerModel.render(CCRenderState.normalAttrib);
        CCRenderState.draw();
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderScannerDisplay(Minecraft mc, ItemStack stack, EntityPlayer player,
                                      ItemCameraTransforms.TransformType transformType) {
        mc.getTextureManager().bindTexture(SCANSCREEN_TEXTURE);
        int packed = getScannerGlow(player instanceof EntityPlayerSP ? (EntityPlayerSP) player : null, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, packed % 65536, packed / 65536);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.11F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float half = 1.25F;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-half, half, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(half, half, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(half, -half, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        buffer.pos(-half, -half, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 0.92F).endVertex();
        Tessellator.getInstance().draw();

        if (isFirstPerson(transformType) && player != null && mc.gameSettings.thirdPersonView == 0) {
            renderScanReadout(mc, stack, player);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderScanReadout(Minecraft mc, ItemStack stack, EntityPlayer player) {
        ScanResult scan = doScan(stack, player);
        if (scan == null) {
            return;
        }

        GlStateManager.pushMatrix();
        // The scanner screen plane is already aligned here, but its local axes are
        // inverted relative to the 1.12 font/tag draw direction. Flip the readout
        // inside the lens plane instead of touching the item pose again.
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        RenderHelper.enableStandardItemLighting();
        int packed = getScannerGlow(player instanceof EntityPlayerSP ? (EntityPlayerSP) player : null, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, packed % 65536, packed / 65536);
        FontRenderer font = mc.fontRenderer;
        AspectList aspects = null;
        String title = "?";
        ItemStack targetStack = ItemStack.EMPTY;

        if (scan.type == 1 && scan.id > 0) {
            Item item = Item.getItemById(scan.id);
            if (item != null) {
                targetStack = new ItemStack(item, 1, scan.meta);
            }
            if (ScanManager.hasBeenScanned(player, scan)) {
                aspects = ScanManager.getScanAspects(scan, player.world);
            }
        } else if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                targetStack = ((EntityItem) scan.entity).getItem();
            } else if (scan.entity != null) {
                title = scan.entity.getName();
            }
            if (ScanManager.hasBeenScanned(player, scan)) {
                aspects = ScanManager.getScanAspects(scan, player.world);
            }
        } else if (scan.type == 3 && scan.phenomena != null && scan.phenomena.startsWith("NODE") && ScanManager.hasBeenScanned(player, scan)) {
            TileEntity tile = getScannedNodeTile(player);
            if (tile instanceof INode) {
                INode node = (INode) tile;
                aspects = node.getAspects();
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                String nodeTitle = net.minecraft.client.resources.I18n.format("nodetype." + node.getNodeType().name() + ".name");
                if (node.getNodeModifier() != null) {
                    nodeTitle = nodeTitle + ", " + net.minecraft.client.resources.I18n.format("nodemod." + node.getNodeModifier().name() + ".name");
                }
                int nodeTitleWidth = font.getStringWidth(nodeTitle);
                GlStateManager.scale(0.004F * HUD_SCALE_MULTIPLIER, 0.004F * HUD_SCALE_MULTIPLIER, 0.004F * HUD_SCALE_MULTIPLIER);
                font.drawString(nodeTitle, -nodeTitleWidth / 2, -40, 15642134, false);
                title = "";
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        if (!targetStack.isEmpty()) {
            try {
                // Placed fluid blocks use bucket item as scan target; show block name instead
                if (scan.phenomena != null && scan.phenomena.equals("FLUID_WATER")) {
                    title = net.minecraft.client.resources.I18n.format("tile.water.name");
                } else if (scan.phenomena != null && scan.phenomena.equals("FLUID_LAVA")) {
                    title = net.minecraft.client.resources.I18n.format("tile.lava.name");
                } else {
                    title = targetStack.getDisplayName();
                }
            } catch (Exception ignored) {
            }
        }

        GlStateManager.translate(0.0F, 0.0F, -0.01F);
        if (aspects != null && aspects.size() > 0) {
            int posX = 0;
            int posY = 0;
            int remaining = aspects.size();
            int baseX = Math.min(5, remaining) * 8;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.0075F * HUD_SCALE_MULTIPLIER, 0.0075F * HUD_SCALE_MULTIPLIER, 0.0075F * HUD_SCALE_MULTIPLIER);
                int localPacked = getScannerGlow(player instanceof EntityPlayerSP ? (EntityPlayerSP) player : null, posX);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, localPacked % 65536, localPacked / 65536);
                UtilsFX.drawTag(-baseX + posX * 16, -8 + posY * 16, aspect, aspects.getAmount(aspect), 0, 0.01D, 1, 1.0F, false);
                GlStateManager.popMatrix();
                if (++posX >= 5 - posY) {
                    posX = 0;
                    remaining -= 5 - posY;
                    posY++;
                    baseX = Math.min(5 - posY, remaining) * 8;
                }
            }
        }

        if (title == null) {
            title = "?";
        }
        if (!title.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            int titleWidth = font.getStringWidth(title);
            float scale = 0.005F * HUD_SCALE_MULTIPLIER;
            if (titleWidth > 90) {
                scale -= 0.000025F * (titleWidth - 90);
            }
            GlStateManager.scale(scale, scale, scale);
            font.drawString(title, -titleWidth / 2, 0, 0xFFFFFF, false);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        logReadoutDebug(player, scan, title, aspects);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static TileEntity getScannedNodeTile(EntityPlayer player) {
        if (player == null || player.world == null) {
            return null;
        }
        return ItemThaumometer.findLookedAtNodeTile(player.world, player, 10.0D);
    }

    private static boolean isFirstPerson(ItemCameraTransforms.TransformType transformType) {
        return transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
    }

    private static int getScannerGlow(EntityPlayerSP player, int offset) {
        if (player == null || player.world == null) {
            return 200;
        }
        return (int) (190.0F + MathHelper.sin((float) (player.ticksExisted + offset - player.world.rand.nextInt(2))) * 10.0F + 10.0F);
    }

    private static ScanResult doScan(ItemStack stack, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || player == null || player.world == null) {
            return null;
        }
        if (stack.getItem() instanceof ItemThaumometer) {
            // This currently asks the gameplay item for the raw scan target so the HUD
            // and the use-to-scan path stay aligned. Any remaining readout gaps should
            // be fixed by tightening raw target selection, not by forking another
            // renderer-local scan implementation.
            return ((ItemThaumometer) stack.getItem()).findRawScanTarget(stack, player.world, player);
        }
        return null;
    }

    private static void logReadoutDebug(EntityPlayer player, ScanResult scan, String title, AspectList aspects) {
        long now = System.currentTimeMillis();
        if (now - lastReadoutDebugLogMs < DEBUG_LOG_INTERVAL_MS) {
            return;
        }
        lastReadoutDebugLogMs = now;

        boolean scanned = player != null && scan != null && ScanManager.hasBeenScanned(player, scan);
        Thaumcraft.log.info("[ThaumometerDebug] readout player={} type={} title={} scanned={} aspectCount={} phenomena={} entity={}",
                player == null ? "<null>" : player.getName(),
                scan == null ? -1 : scan.type,
                title == null ? "<null>" : title,
                scanned,
                aspects == null ? 0 : aspects.size(),
                scan == null || scan.phenomena == null ? "<none>" : scan.phenomena,
                scan == null || scan.entity == null ? "<none>" : scan.entity.getName() + "#" + scan.entity.getEntityId());
    }

    private static CCModel loadScannerModel() {
        Map<String, CCModel> models = CCModel.parseObjModels(SCANNER_OBJ);
        if (models == null || models.isEmpty()) {
            return null;
        }
        CCModel model = models.get("scanner");
        if (model != null) {
            return model;
        }
        return models.values().iterator().next();
    }
}
