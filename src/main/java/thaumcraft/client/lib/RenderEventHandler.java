package thaumcraft.client.lib;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.api.IGoggles;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.ScanResult;
import thaumcraft.client.renderers.tile.HoleRenderBatchCache;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.relics.ItemThaumometer;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.research.ScanManager;

@SideOnly(Side.CLIENT)
public class RenderEventHandler {
    private final REHNotifyHandler notifyHandler = new REHNotifyHandler();
    private final REHWandHandler wandHandler = new REHWandHandler();
    public static boolean resetShaders = false;
    private static int oldDisplayWidth = 0;
    private static int oldDisplayHeight = 0;
    public static HashMap<Integer, ShaderGroup> shaderGroups = new HashMap<Integer, ShaderGroup>();
    public static boolean fogFiddled = false;
    public static float fogTarget = 0.0F;
    public static int fogDuration = 0;
    public static float prevVignetteBrightness = 0.0F;
    public static float targetBrightness = 1.0F;
    public static float tagscale = 0.0F;

    private static final ResourceLocation VIGNETTE_TEX =
            new ResourceLocation("thaumcraft", "textures/misc/vignette.png");
    private static final ResourceLocation NODE_SCAN_TEX =
            new ResourceLocation("thaumcraft", "textures/misc/nodes.png");
    private static final ResourceLocation UNKNOWN_ASPECT_TEX =
            new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
    private static final int SCAN_GRID_RADIUS = 8;
    private static final int SCAN_GRID_SIZE = SCAN_GRID_RADIUS * 2 + 1;
    private static final int[][][] scannedBlocks = new int[SCAN_GRID_SIZE][SCAN_GRID_SIZE][SCAN_GRID_SIZE];

    public static int scanEntityId = -1;
    public static BlockPos scanPos = BlockPos.ORIGIN;
    public static long scanExpireAtMs = 0L;
    public static int scanRange = 0;

    public static void startScan(Entity entity, BlockPos pos, long expireAtMs, int range) {
        if (entity == null || pos == null || entity.world == null) {
            return;
        }

        clearScannedBlocks();
        scanEntityId = entity.getEntityId();
        scanPos = pos.toImmutable();
        scanExpireAtMs = expireAtMs;
        scanRange = MathHelper.clamp(range, 0, SCAN_GRID_RADIUS);

        World world = entity.world;
        for (int xx = -scanRange; xx <= scanRange; xx++) {
            for (int yy = -scanRange; yy <= scanRange; yy++) {
                for (int zz = -scanRange; zz <= scanRange; zz++) {
                    int value = classifyScannedBlock(world, scanPos.add(xx, yy, zz));
                    scannedBlocks[xx + SCAN_GRID_RADIUS][yy + SCAN_GRID_RADIUS][zz + SCAN_GRID_RADIUS] = value;
                }
            }
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PORTAL) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null) {
                ScaledResolution resolution = event.getResolution();
                renderVignette(targetBrightness, resolution.getScaledWidth(), resolution.getScaledHeight());
            }
        }
    }

    @SubscribeEvent
    public void renderNotifications(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null) {
            return;
        }
        long time = System.nanoTime() / 1000000L;
        this.notifyHandler.handleNotifications(mc, time, event.getResolution());
        this.wandHandler.handleCastingWandHud(mc, time, event);
        this.wandHandler.handleFociRadial(mc, time, event);
    }

    @SubscribeEvent
    public void renderShaders(RenderGameOverlayEvent.Pre event) {
        if (!Config.shaders || event.getType() != RenderGameOverlayEvent.ElementType.ALL || !OpenGlHelper.shadersSupported) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || shaderGroups.isEmpty()) {
            return;
        }
        this.updateShaderFrameBuffers(mc);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        for (ShaderGroup shaderGroup : shaderGroups.values()) {
            GL11.glPushMatrix();
            try {
                shaderGroup.render(event.getPartialTicks());
            } catch (Exception ignored) {
            }
            GL11.glPopMatrix();
        }
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        mc.getFramebuffer().bindFramebuffer(true);
    }

    private void updateShaderFrameBuffers(Minecraft mc) {
        if (resetShaders || mc.displayWidth != oldDisplayWidth || mc.displayHeight != oldDisplayHeight) {
            for (ShaderGroup shaderGroup : shaderGroups.values()) {
                shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            }
            oldDisplayWidth = mc.displayWidth;
            oldDisplayHeight = mc.displayHeight;
            resetShaders = false;
        }
    }

    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        if (!canShowGogglesPopups(player)) {
            return;
        }
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK || target.getBlockPos() == null) {
            return;
        }
        TileEntity tile = player.world.getTileEntity(target.getBlockPos());
        if (!(tile instanceof IAspectContainer) || tile instanceof INode) {
            return;
        }
        AspectList aspects = resolveAspectTags(tile);
        if (aspects == null || aspects.size() <= 0) {
            return;
        }

        BlockPos pos = target.getBlockPos();
        EnumFacing side = target.sideHit == null ? EnumFacing.UP : target.sideHit;
        boolean spaceAbove = player.world.isAirBlock(pos.up());
        double x = pos.getX() + 0.5D + side.getXOffset() * 0.55D;
        double y = pos.getY() + 0.5D + side.getYOffset() * 0.55D;
        double z = pos.getZ() + 0.5D + side.getZOffset() * 0.55D;
        if (spaceAbove) {
            y = Math.max(y, pos.getY() + 1.15D);
        }
        drawTagsOnContainer(x, y, z, aspects, 220, side, event.getPartialTicks());
    }

    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) {
            return;
        }

        // EldritchDiagnostics: disabled for release, re-enable for FPS profiling
        // EldritchDiagnostics.onFrame();
        HoleRenderBatchCache.nextFrame();

        if (tagscale > 0.0F) {
            tagscale = Math.max(0.0F, tagscale - 0.005F);
        }
        renderGogglesNodeTags(event.getPartialTicks(), player);

        long now = System.currentTimeMillis();
        if (scanExpireAtMs <= 0L || now >= scanExpireAtMs) {
            clearScanState();
            return;
        }
        if (scanEntityId >= 0 && scanEntityId != player.getEntityId()) {
            return;
        }
        renderScannedBlocks(event.getPartialTicks(), player, now);
    }

    @SubscribeEvent
    public void fogDensityEvent(EntityViewRenderEvent.RenderFogEvent event) {
        if (fogFiddled && fogTarget > 0.0F) {
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
            GL11.glFogf(GL11.GL_FOG_DENSITY, fogTarget);
        }
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == null || !event.getEntityLiving().world.isRemote) {
            return;
        }
        if (event.getEntityLiving() instanceof EntityMob && !event.getEntityLiving().isDead) {
            EntityMob mob = (EntityMob) event.getEntityLiving();
            IAttributeInstance mod = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);
            if (mod != null) {
                int type = (int) mod.getAttributeValue();
                if (type >= 0 && type < ChampionModifier.mods.length) {
                    ChampionModifier.mods[type].effect.showFX((EntityLivingBase) mob);
                }
            }
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || event.getEntityLiving().getEntityId() != player.getEntityId()) {
            return;
        }
        if (scanExpireAtMs > 0L && System.currentTimeMillis() >= scanExpireAtMs) {
            clearScanState();
        }
    }

    private static boolean canShowGogglesPopups(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack helmet = player.inventory.armorInventory.get(3);
        return !helmet.isEmpty()
                && helmet.getItem() instanceof IGoggles
                && ((IGoggles) helmet.getItem()).showIngamePopups(helmet, player);
    }

    private static void renderGogglesNodeTags(float partialTicks, EntityPlayer player) {
        if (!canShowGogglesPopups(player) || player.world == null) {
            return;
        }
        TileEntity tile = ItemThaumometer.findLookedAtNodeTile(player.world, player, 10.0D);
        if (!(tile instanceof INode)) {
            return;
        }
        AspectList aspects = resolveAspectTags(tile);
        if (aspects == null || aspects.size() <= 0) {
            return;
        }
        BlockPos pos = tile.getPos();
        drawTagsOnContainer(pos.getX() + 0.5D, pos.getY() + 1.15D, pos.getZ() + 0.5D,
                aspects, 220, EnumFacing.UP, partialTicks);
    }

    private static AspectList resolveAspectTags(TileEntity tile) {
        if (!(tile instanceof IAspectContainer)) {
            return null;
        }
        AspectList aspects = ((IAspectContainer) tile).getAspects();
        if ((aspects == null || aspects.size() <= 0) && tile instanceof INode) {
            aspects = ((INode) tile).getAspectsBase();
        }
        return aspects;
    }

    private static void drawTagsOnContainer(double x, double y, double z, AspectList aspects, int bright, EnumFacing side, float partialTicks) {
        if (aspects == null || aspects.size() <= 0) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null) {
            return;
        }
        RenderManager renderManager = mc.getRenderManager();
        IPlayerKnowledge knowledge = mc.player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        tagscale += (0.3F - tagscale) * 0.25F;

        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean alphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        int alphaFunction = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float alphaReference = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x - renderManager.viewerPosX, y - renderManager.viewerPosY, z - renderManager.viewerPosZ);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            float scale = 0.01875F * Math.max(0.35F, tagscale / 0.3F);
            GlStateManager.scale(-scale, -scale, scale);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();

            int posX = 0;
            int posY = 0;
            int rowSize = 5;
            int remaining = aspects.size();
            int baseX = Math.min(rowSize, remaining) * 8;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (aspect == null) {
                    continue;
                }
                int tagX = -baseX + posX * 16;
                int tagY = -8 + posY * 16;
                int amount = aspects.getAmount(aspect);
                if (knowledge == null || !knowledge.hasDiscoveredAspect(aspect)) {
                    drawUnknownAspectTag(tagX, tagY, aspect, amount, bright);
                } else {
                    UtilsFX.drawTag(tagX, tagY, aspect, amount, 0, 0.0D, bright, 1.0F, false);
                }
                if (++posX >= rowSize) {
                    posX = 0;
                    remaining -= rowSize;
                    posY++;
                    baseX = Math.min(rowSize, remaining) * 8;
                }
            }
        } finally {
            GlStateManager.depthMask(depthMask);
            if (depthEnabled) GlStateManager.enableDepth(); else GlStateManager.disableDepth();
            if (lightingEnabled) GlStateManager.enableLighting(); else GlStateManager.disableLighting();
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) GlStateManager.enableBlend(); else GlStateManager.disableBlend();
            GlStateManager.alphaFunc(alphaFunction, alphaReference);
            if (alphaEnabled) GlStateManager.enableAlpha(); else GlStateManager.disableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void drawUnknownAspectTag(int x, int y, Aspect aspect, int amount, int blend) {
        int color = aspect.getColor();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableLighting();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    blend == 1 ? GlStateManager.DestFactor.ONE : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            UtilsFX.bindTexture(UNKNOWN_ASPECT_TEX);
            GlStateManager.color(red, green, blue, 0.75F);
            UtilsFX.drawTexturedQuadFull(x, y, 0.0D);

            if (amount > 0) {
                GlStateManager.pushMatrix();
                try {
                    Minecraft mc = Minecraft.getMinecraft();
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    String amountText = Integer.toString(amount);
                    int width = mc.fontRenderer.getStringWidth(amountText);
                    if (blend > 1) {
                        mc.fontRenderer.drawString(amountText, 31 - width + x * 2, 32 - mc.fontRenderer.FONT_HEIGHT + y * 2, 0);
                        mc.fontRenderer.drawString(amountText, 33 - width + x * 2, 32 - mc.fontRenderer.FONT_HEIGHT + y * 2, 0);
                        mc.fontRenderer.drawString(amountText, 32 - width + x * 2, 31 - mc.fontRenderer.FONT_HEIGHT + y * 2, 0);
                        mc.fontRenderer.drawString(amountText, 32 - width + x * 2, 33 - mc.fontRenderer.FONT_HEIGHT + y * 2, 0);
                    }
                    mc.fontRenderer.drawString(amountText, 32 - width + x * 2,
                            32 - mc.fontRenderer.FONT_HEIGHT + y * 2, 0xFFFFFF);
                } finally {
                    GlStateManager.popMatrix();
                }
            }
        } finally {
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void renderScannedBlocks(float partialTicks, EntityPlayer player, long now) {
        long remaining = scanExpireAtMs - now;
        int frame = (int) ((now / 50L) % 32L);

        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        for (int xx = -SCAN_GRID_RADIUS; xx <= SCAN_GRID_RADIUS; xx++) {
            for (int yy = -SCAN_GRID_RADIUS; yy <= SCAN_GRID_RADIUS; yy++) {
                for (int zz = -SCAN_GRID_RADIUS; zz <= SCAN_GRID_RADIUS; zz++) {
                    int value = scannedBlocks[xx + SCAN_GRID_RADIUS][yy + SCAN_GRID_RADIUS][zz + SCAN_GRID_RADIUS];
                    if (value < 0 && value != -5 && value != -10) {
                        continue;
                    }

                    float alpha = computeScanAlpha(remaining, xx, yy, zz);
                    if (alpha <= 0.0F) {
                        continue;
                    }

                    double wx = scanPos.getX() + xx;
                    double wy = scanPos.getY() + yy;
                    double wz = scanPos.getZ() + zz;
                    if (value == -5) {
                        drawSpecialBlockOverlay(wx, wy, wz, partialTicks, 0x3CD4BC, alpha, player);
                    } else if (value == -10) {
                        drawSpecialBlockOverlay(wx, wy, wz, partialTicks, 0xFF55C1, alpha, player);
                    } else {
                        float size = Math.max(0.08F, value / 7.0F);
                        drawScannedNodePulse(wx + 0.5D, wy + 0.5D, wz + 0.5D, alpha, frame, size);
                    }
                }
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private static float computeScanAlpha(long remaining, int xx, int yy, int zz) {
        float alpha = 1.0F;
        if (remaining > 4750L) {
            alpha = 1.0F - (remaining - 4750L) / 5.0F;
        }
        if (remaining < 1500L) {
            alpha = remaining / 1500.0F;
        }
        float dist = 1.0F - (xx * xx + yy * yy + zz * zz) / 64.0F;
        alpha *= dist;
        return MathHelper.clamp(alpha, 0.0F, 1.0F);
    }

    private static void drawScannedNodePulse(double x, double y, double z, float alpha, int frame, float size) {
        drawFacingStrip(x, y, z, 0.20F * size, alpha, frame, 0xAAAA11);
        drawFacingStrip(x, y, z, 0.50F * size, alpha, frame, 0xAA1122);
    }

    private static void drawFacingStrip(double x, double y, double z, float half, float alpha, int frame, int rgb) {
        if (half <= 0.0F || alpha <= 0.0F) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager rm = mc.getRenderManager();
        float u0 = (frame % 32) / 32.0F;
        float u1 = u0 + 1.0F / 32.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x - rm.viewerPosX, y - rm.viewerPosY, z - rm.viewerPosZ);
        GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(rm.playerViewX, 1.0F, 0.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        mc.getTextureManager().bindTexture(NODE_SCAN_TEX);
        drawTexturedQuad(half, argb(alpha, rgb), u0, u1, 0.0F, 1.0F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawSpecialBlockOverlay(double x, double y, double z, float partialTicks, int color, float alpha, EntityPlayer player) {
        if (alpha <= 0.0F || player == null) {
            return;
        }
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D - px, y + 0.5D - py, z + 0.5D - pz);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableCull();
        for (EnumFacing face : EnumFacing.values()) {
            GlStateManager.pushMatrix();
            alignToFace(face);
            drawTexturedQuad(0.49F, argb(alpha, color), 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void alignToFace(EnumFacing face) {
        switch (face) {
            case DOWN:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case NORTH:
            default:
                break;
        }
        GlStateManager.translate(0.0F, 0.0F, -0.5F);
    }

    private static int classifyScannedBlock(World world, BlockPos pos) {
        if (world == null || pos == null || !world.isBlockLoaded(pos)) {
            return -1;
        }
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        if (block == Blocks.AIR || material == Material.AIR) {
            return -1;
        }
        if (material == Material.WATER) {
            return -5;
        }
        if (material == Material.LAVA) {
            return -10;
        }

        Item item = Item.getItemFromBlock(block);
        if (item == null || item == Item.getItemFromBlock(Blocks.AIR)) {
            return -1;
        }
        int meta = block.getMetaFromState(state);
        ItemStack probe = new ItemStack(item, 1, meta);
        if (!isOreDictionaryOre(probe)) {
            return -1;
        }
        try {
            ScanResult scan = new ScanResult((byte) 1, Item.getIdFromItem(item), meta, null, "");
            return ScanManager.getScanAspects(scan, world).visSize();
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static boolean isOreDictionaryOre(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        int[] oreIds = OreDictionary.getOreIDs(stack);
        if (oreIds == null || oreIds.length == 0) {
            return false;
        }
        for (int id : oreIds) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName != null && oreName.toUpperCase().contains("ORE")) {
                return true;
            }
        }
        return false;
    }

    private static void clearScannedBlocks() {
        for (int x = 0; x < SCAN_GRID_SIZE; x++) {
            for (int y = 0; y < SCAN_GRID_SIZE; y++) {
                for (int z = 0; z < SCAN_GRID_SIZE; z++) {
                    scannedBlocks[x][y][z] = -1;
                }
            }
        }
    }

    private static void clearScanState() {
        scanExpireAtMs = 0L;
        scanEntityId = -1;
        scanRange = 0;
        clearScannedBlocks();
    }

    private static int argb(float alpha, int rgb) {
        int a = MathHelper.clamp((int) (alpha * 255.0F), 0, 255);
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    private static void drawTexturedQuad(float half, int argb, float u0, float u1, float v0, float v1) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-half, -half, 0.0D).tex(u0, v1).color(r, g, b, a).endVertex();
        buffer.pos(half, -half, 0.0D).tex(u1, v1).color(r, g, b, a).endVertex();
        buffer.pos(half, half, 0.0D).tex(u1, v0).color(r, g, b, a).endVertex();
        buffer.pos(-half, half, 0.0D).tex(u0, v0).color(r, g, b, a).endVertex();
        tess.draw();
    }

    private void renderVignette(float brightness, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || width <= 0 || height <= 0) {
            return;
        }

        brightness = 1.0F - brightness;
        if (brightness < 0.0F) brightness = 0.0F;
        if (brightness > 1.0F) brightness = 1.0F;
        prevVignetteBrightness += (brightness - prevVignetteBrightness) * 0.01F;

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.ZERO,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);
        mc.getTextureManager().bindTexture(VIGNETTE_TEX);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0D, height, -90.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(width, height, -90.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(width, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
    }
}
