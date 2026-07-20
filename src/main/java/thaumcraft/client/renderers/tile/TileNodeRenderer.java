package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.relics.ItemThaumometer;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileNode;
import org.lwjgl.opengl.GL11;

public class TileNodeRenderer extends TileEntitySpecialRenderer<TileEntity> {

    public static final ResourceLocation NODES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/nodes.png");
    private static final AspectList DEFAULT_NODE_ASPECTS = new AspectList()
            .add(Aspect.AURA, 40)
            .add(Aspect.MAGIC, 25)
            .add(Aspect.AIR, 25);
    private static final int FRAMES = 32;

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (!(tile instanceof INode)) {
            return;
        }
        INode node = (INode) tile;

        // Separate render-space (camera-relative) from world-space coordinates.
        // x/y/z from TESR are: blockPos - cameraPos.
        // render-space: use for actual drawing (GlStateManager.translate).
        // world-space: use for viewer.getDistance() and isVisibleTo().
        double renderX = x + 0.5D;
        double renderY = y + 0.5D;
        double renderZ = z + 0.5D;

        BlockPos pos = tile.getPos();
        double worldX = pos.getX() + 0.5D;
        double worldY = pos.getY() + 0.5D;
        double worldZ = pos.getZ() + 0.5D;

        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        float size = 1.0F;
        double viewDistance = 64.0D;
        boolean visible = false;
        boolean depthIgnore = false;

        if (tile instanceof TileJarNode) {
            visible = true;
            size = 0.7F;
        } else if (viewer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) viewer;
            ItemStack helmet = player.inventory.armorInventory.get(3);
            if (!helmet.isEmpty() && helmet.getItem() instanceof IRevealer
                    && ((IRevealer) helmet.getItem()).showNodes(helmet, viewer)) {
                visible = true;
                depthIgnore = true;
            } else {
                if (isHoldingThaumometer(player)
                        && isVisibleTo(0.44F, viewer, worldX, worldY, worldZ, 48.0F)) {
                    visible = true;
                    depthIgnore = true;
                    viewDistance = 48.0D;
                }
            }
        }

        AspectList renderAspects = node.getAspects();
        if ((renderAspects == null || renderAspects.size() <= 0) && node.getAspectsBase() != null && node.getAspectsBase().size() > 0) {
            renderAspects = node.getAspectsBase();
        }
        if ((renderAspects == null || renderAspects.size() <= 0) && visible) {
            renderAspects = DEFAULT_NODE_ASPECTS;
        }

        renderNodeSeeded(viewer, viewDistance, visible, depthIgnore, size,
                renderX, renderY, renderZ,
                worldX, worldY, worldZ,
                partialTicks,
                renderAspects, node.getNodeType(), node.getNodeModifier(), pos.getX());

        if (tile instanceof TileNode) {
            renderDrainBeam((TileNode) tile, partialTicks);
        }
    }

    private static void renderDrainBeam(TileNode node, float partialTicks) {
        if (node.drainEntity == null || node.drainCollision == null) {
            return;
        }

        Entity entity = node.drainEntity;

        RayTraceResult hit = node.drainCollision;
        BlockPos hitPos = hit.getBlockPos() == null ? node.getPos() : hit.getBlockPos();
        float beamAge = node.drainBeamAge + partialTicks;
        float wobble = MathHelper.sin(beamAge / 10.0F) * 10.0F;

        Vec3d offset = new Vec3d(-0.1D, -0.1D, 0.5D);
        float pitch = -(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks) * (float) Math.PI / 180.0F;
        float yaw = -(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks) * (float) Math.PI / 180.0F;
        offset = offset.rotatePitch(pitch);
        offset = offset.rotateYaw(yaw);
        offset = offset.rotateYaw(-wobble * 0.01F);
        offset = offset.rotatePitch(-wobble * 0.015F);

        double sourceWorldX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks + offset.x;
        double sourceWorldY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks + offset.y
                + (entity == Minecraft.getMinecraft().player ? 0.0D : entity.getEyeHeight());
        double sourceWorldZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks + offset.z;
        double targetWorldX = hitPos.getX() + 0.5D;
        double targetWorldY = hitPos.getY() + 0.5D;
        double targetWorldZ = hitPos.getZ() + 0.5D;
        int color = node.color == null ? node.drainColor : node.color.getRGB();

        GlStateManager.pushMatrix();
        UtilsFX.drawFloatyLine(sourceWorldX, sourceWorldY, sourceWorldZ,
                targetWorldX, targetWorldY, targetWorldZ,
                partialTicks, color, "textures/misc/wispy.png", -0.02F, Math.min(beamAge, 10.0F) / 10.0F);
        GlStateManager.popMatrix();
    }

    public static void renderNodeAt(INode node, double x, double y, double z, float partialTicks, float size) {
        renderNodeAt(node, x, y, z, partialTicks, size, false);
    }

    public static void renderNodeAt(INode node, double x, double y, double z, float partialTicks, float size,
                                    boolean depthIgnore) {
        if (node == null) return;
        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        double worldX = x;
        double worldY = y;
        double worldZ = z;
        if (node instanceof TileEntity) {
            TileEntity tile = (TileEntity) node;
            if (tile.getWorld() == null) {
                viewer = null;
            } else {
                BlockPos pos = tile.getPos();
                worldX = pos.getX() + 0.5D;
                worldY = pos.getY() + 0.5D;
                worldZ = pos.getZ() + 0.5D;
            }
        }
        int seed = node.getId() == null ? 0 : node.getId().hashCode();
        renderNodeSeeded(viewer, 64.0D, true, depthIgnore, size,
                x, y, z,
                worldX, worldY, worldZ,
                partialTicks, node.getAspects(), node.getNodeType(), node.getNodeModifier(), seed);
    }

    public static void renderNodeAt(AspectList aspectsList,
                                    String nodeId,
                                    NodeType nodeType,
                                    NodeModifier nodeModifier,
                                    double x, double y, double z,
                                    float partialTicks,
                                    float size) {
        EntityLivingBase viewer = Minecraft.getMinecraft().player;
        int seed = nodeId == null ? 0 : nodeId.hashCode();
        renderNode(viewer, 64.0D, true, false, size, x, y, z, partialTicks, aspectsList, nodeType, nodeModifier, seed);
    }

    public static void renderNode(EntityLivingBase viewer,
                                  double viewDistance,
                                  boolean visible,
                                  boolean depthIgnore,
                                  float size,
                                  double x, double y, double z,
                                  float partialTicks,
                                  AspectList aspects,
                                  NodeType type,
                                  NodeModifier modifier) {
        renderNodeSeeded(viewer, viewDistance, visible, depthIgnore, size, x, y, z, partialTicks, aspects, type, modifier, 0);
    }

    public static void renderNode(EntityLivingBase viewer,
                                  double viewDistance,
                                  boolean visible,
                                  boolean depthIgnore,
                                  float size,
                                  double x, double y, double z,
                                  float partialTicks,
                                  AspectList aspects,
                                  NodeType type,
                                  NodeModifier modifier,
                                  int seed) {
        renderNodeSeeded(viewer, viewDistance, visible, depthIgnore, size, x, y, z, partialTicks, aspects, type, modifier, seed);
    }

    /**
     * Backward-compatible overload: uses the same coordinates for both render and world-space.
     * Called from public static renderNode() / renderNodeAt() wrappers.
     */
    private static void renderNodeSeeded(EntityLivingBase viewer,
                                         double viewDistance,
                                         boolean visible,
                                         boolean depthIgnore,
                                         float size,
                                         double x, double y, double z,
                                         float partialTicks,
                                         AspectList aspects,
                                         NodeType type,
                                         NodeModifier modifier,
                                         int seed) {
        renderNodeSeeded(viewer, viewDistance, visible, depthIgnore, size,
                x, y, z,
                x, y, z,
                partialTicks, aspects, type, modifier, seed);
    }

    /**
     * Core render implementation with separated render-space and world-space coordinates.
     * <p>
     * renderX/Y/Z — camera-relative coordinates, used for actual drawing (renderFacingStrip).
     * worldX/Y/Z — absolute world coordinates, used for distance/visibility checks.
     */
    static void renderNodeSeeded(EntityLivingBase viewer,
                                 double viewDistance,
                                 boolean visible,
                                 boolean depthIgnore,
                                 float size,
                                 double renderX, double renderY, double renderZ,
                                 double worldX, double worldY, double worldZ,
                                 float partialTicks,
                                 AspectList aspects,
                                 NodeType type,
                                 NodeModifier modifier,
                                 int seed) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(NODES_TEXTURE);
        long nano = System.nanoTime();
        int frame = (int) ((nano / 40000000L + seed) % FRAMES);

        if (aspects != null && aspects.size() > 0 && visible) {
            float alpha = 1.0F;
            if (viewer != null) {
                double distance = viewer.getDistance(worldX, worldY, worldZ);
                if (distance > viewDistance) return;
                alpha = (float) ((viewDistance - distance) / viewDistance);
            }

            if (modifier == NodeModifier.BRIGHT) alpha *= 1.5F;
            else if (modifier == NodeModifier.PALE) alpha *= 0.66F;
            else if (modifier == NodeModifier.FADING) alpha *= (float) Math.sin(viewerTicks(viewer, partialTicks) / 3.0F) * 0.25F + 0.33F;

            NodeLightState lightState = enableNodeLighting();
            GlStateManager.pushMatrix();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);
            GlStateManager.depthMask(false);
            if (depthIgnore) GlStateManager.disableDepth();
            GlStateManager.disableCull();

            long time = nano / 5000000L;
            float bscale = 0.25F;
            int count = 0;
            float average = 0.0F;
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect == null) continue;
                float aspectAlpha = alpha;
                if (aspect.getBlend() == GL11.GL_ONE_MINUS_SRC_ALPHA) {
                    aspectAlpha *= 1.5F;
                }
                average += aspects.getAmount(aspect);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, aspect.getBlend());
                float scale = (float) Math.sin(viewerTicks(viewer, partialTicks) / (14.0F - count)) * bscale + bscale * 2.0F;
                scale = 0.2F + scale * (aspects.getAmount(aspect) / 50.0F);
                float angle = (float) (time % (5000L + 500L * count)) / (5000.0F + 500.0F * count) * ((float) Math.PI * 2.0F);
                renderFacingStrip(renderX, renderY, renderZ, angle, scale * size, aspectAlpha / Math.max(1.0F, aspects.size() / 2.0F),
                        FRAMES, 0, frame, aspect.getColor());
                GlStateManager.disableBlend();
                count++;
            }

            GlStateManager.enableBlend();
            int strip = 1;
            float angle = 0.0F;
            float centerScale = 0.1F + (average / aspects.size()) / 150.0F;
            centerScale *= size;
            if (type != null) {
                switch (type) {
                    case NORMAL:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                        break;
                    case UNSTABLE:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                        strip = 6;
                        break;
                    case DARK:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        strip = 2;
                        break;
                    case TAINTED:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        strip = 5;
                        break;
                    case PURE:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                        strip = 4;
                        break;
                    case HUNGRY:
                        centerScale *= 0.75F;
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                        strip = 3;
                        break;
                    default:
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                        break;
                }
            }
            renderFacingStrip(renderX, renderY, renderZ, angle, centerScale, alpha, FRAMES, strip, frame, 0xFFFFFF);
            GlStateManager.disableBlend();

            GlStateManager.enableCull();
            if (depthIgnore) GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.popMatrix();
            restoreNodeLighting(lightState);
            return;
        }

        NodeLightState lightState = enableNodeLighting();
        GlStateManager.pushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.depthMask(false);
        if (depthIgnore) GlStateManager.disableDepth();
        renderFacingStrip(renderX, renderY, renderZ, 0.0F, 0.5F, 0.1F, FRAMES, 1, frame, 0xFFFFFF);
        if (depthIgnore) GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
        restoreNodeLighting(lightState);
    }

    private static boolean isHoldingThaumometer(EntityPlayer player) {
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        return (!main.isEmpty() && main.getItem() instanceof ItemThaumometer)
                || (!off.isEmpty() && off.getItem() instanceof ItemThaumometer);
    }

    private static NodeLightState enableNodeLighting() {
        NodeLightState state = new NodeLightState(OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY,
                GL11.glIsEnabled(GL11.GL_LIGHTING));
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);
        return state;
    }

    private static void restoreNodeLighting(NodeLightState state) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, state.lightX, state.lightY);
        if (state.lightingEnabled) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }
    }

    private static final class NodeLightState {
        private final float lightX;
        private final float lightY;
        private final boolean lightingEnabled;

        private NodeLightState(float lightX, float lightY, boolean lightingEnabled) {
            this.lightX = lightX;
            this.lightY = lightY;
            this.lightingEnabled = lightingEnabled;
        }
    }

    private static void renderFacingStrip(double x, double y, double z,
                                          float angle, float scale, float alpha,
                                          int frames, int strip, int frame, int color) {
        float clampedAlpha = Math.max(0.0F, Math.min(1.0F, alpha));
        int argb = ((int) (clampedAlpha * 255.0F) << 24) | (color & 0x00FFFFFF);
        float u0 = frame / (float) frames;
        float u1 = (frame + 1) / (float) frames;
        float v0 = strip / (float) frames;
        float v1 = (strip + 1) / (float) frames;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        TileRenderHelper.orientBillboardToPlayer();
        GlStateManager.rotate((float) Math.toDegrees(angle), 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(scale, argb, u0, u1, v0, v1);
        GlStateManager.popMatrix();
    }

    private static float viewerTicks(EntityLivingBase viewer, float partialTicks) {
        if (viewer != null) {
            return viewer.ticksExisted + partialTicks;
        }
        EntityPlayer local = Minecraft.getMinecraft().player;
        return local == null ? partialTicks : local.ticksExisted + partialTicks;
    }

    private static boolean isVisibleTo(float fov, EntityLivingBase entity, double x, double y, double z, float range) {
        if (entity == null) return false;
        Vec3d eyes = new Vec3d(entity.posX, entity.getEntityBoundingBox().minY + entity.getEyeHeight(), entity.posZ);
        Vec3d target = new Vec3d(x, y, z);
        Vec3d toTarget = target.subtract(eyes);
        double distance = toTarget.length();
        if (distance <= 0.0D || distance > range) return false;
        Vec3d look = entity.getLook(1.0F).normalize();
        return toTarget.normalize().dotProduct(look) > Math.cos(fov / 2.0F);
    }
}
