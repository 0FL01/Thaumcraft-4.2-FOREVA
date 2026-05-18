package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEventHandler {
    public static boolean fogFiddled = false;
    public static float fogTarget = 0.0F;
    public static int fogDuration = 0;
    public static float prevVignetteBrightness = 0.0F;
    public static float targetBrightness = 1.0F;
    private static final ResourceLocation VIGNETTE_TEX =
            new ResourceLocation("thaumcraft", "textures/misc/vignette.png");
    public static int scanEntityId = -1;
    public static BlockPos scanPos = BlockPos.ORIGIN;
    public static long scanExpireAtMs = 0L;
    public static int scanRange = 0;

    public static void startScan(Entity entity, BlockPos pos, long expireAtMs, int range) {
        if (entity == null || pos == null) {
            return;
        }
        scanEntityId = entity.getEntityId();
        scanPos = pos.toImmutable();
        scanExpireAtMs = expireAtMs;
        scanRange = Math.max(0, range);
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
    public void blockHighlight(DrawBlockHighlightEvent event) {
    }

    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
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
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || event.getEntityLiving().getEntityId() != player.getEntityId()) {
            return;
        }
        if (scanExpireAtMs > 0L && System.currentTimeMillis() >= scanExpireAtMs) {
            scanExpireAtMs = 0L;
            scanEntityId = -1;
            scanRange = 0;
        }
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
