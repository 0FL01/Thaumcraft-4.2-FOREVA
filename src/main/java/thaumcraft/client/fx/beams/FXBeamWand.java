package thaumcraft.client.fx.beams;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.items.wands.ItemWandCasting;

@SideOnly(Side.CLIENT)
public class FXBeamWand extends FXBeam {
    /** Enable with -Dthaumcraft.debugWandEffectOrigin=true; disabled for normal launches. */
    private static final boolean DEBUG_EFFECT_ORIGIN =
            Boolean.getBoolean("thaumcraft.debugWandEffectOrigin");
    private static final double DEBUG_AXIS_LENGTH = 0.25D;
    private static final double DEBUG_MARKER_RADIUS = 0.025D;

    private final EntityPlayer player;
    private final double sourceYOffset;
    public int impact;

    public FXBeamWand(World world, EntityPlayer player, double tx, double ty, double tz,
                      float red, float green, float blue, int age, boolean flicker, int density) {
        this(world, player, tx, ty, tz, red, green, blue, age, flicker, density,
                InitialSource.create(player));
    }

    private FXBeamWand(World world, EntityPlayer player, double tx, double ty, double tz,
                       float red, float green, float blue, int age, boolean flicker, int density,
                       InitialSource source) {
        super(world,
                source.position.x,
                source.position.y,
                source.position.z,
                tx, ty, tz,
                red, green, blue,
                age, flicker, density);
        this.player = player;
        this.sourceYOffset = source.yOffset;
        this.impact = 0;
    }

    public void updateBeam(double tx, double ty, double tz) {
        if (this.player != null) {
            Vec3d src = sourcePos(this.player, 1.0F, this.sourceYOffset);
            super.updateBeam(
                    src.x,
                    src.y,
                    src.z,
                    tx, ty, tz);
        } else {
            super.updateBeam(this.posX, this.posY, this.posZ, tx, ty, tz);
        }
    }

    @Override
    public void onUpdate() {
        if (this.player == null
                || !this.player.isEntityAlive()
                || !this.player.isHandActive()
                || !(this.player.getActiveItemStack().getItem() instanceof ItemWandCasting)) {
            this.setExpired();
            return;
        }
        Vec3d src = sourcePos(this.player, 1.0F, this.sourceYOffset);
        this.setPosition(src.x, src.y, src.z);
        if (this.impact > 0) {
            --this.impact;
        }
        super.onUpdate();
    }

    @Override
    protected void renderImpact(float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.impact <= 0) {
            return;
        }
        super.renderImpact(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public void renderParticle(BufferBuilder ignored, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        super.renderParticle(ignored, entityIn, partialTicks,
                rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        if (DEBUG_EFFECT_ORIGIN && this.player != null) {
            renderDebugFrame(partialTicks);
        }
    }

    private static Vec3d sourcePos(EntityPlayer player, float partialTicks, double yOffset) {
        return WandEffectOrigin.resolve(player, partialTicks, yOffset);
    }

    private void renderDebugFrame(float partialTicks) {
        WandEffectOrigin.DebugFrame frame =
                WandEffectOrigin.debugFrame(this.player, partialTicks, this.sourceYOffset);
        Vec3d origin = cameraRelative(frame.origin);
        Vec3d target = cameraRelative(new Vec3d(
                this.ptX + (this.tX - this.ptX) * partialTicks,
                this.ptY + (this.tY - this.ptY) * partialTicks,
                this.ptZ + (this.tZ - this.ptZ) * partialTicks));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GL11.glLineWidth(2.0F);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        addDebugLine(buffer, origin, origin.add(frame.right.scale(DEBUG_AXIS_LENGTH)), 255, 64, 64);
        addDebugLine(buffer, origin, origin.add(frame.up.scale(DEBUG_AXIS_LENGTH)), 64, 255, 64);
        addDebugLine(buffer, origin, origin.add(frame.forward.scale(DEBUG_AXIS_LENGTH)), 64, 128, 255);
        addDebugMarker(buffer, origin, 255, 255, 255);
        addDebugMarker(buffer, target, 255, 224, 32);
        tessellator.draw();

        GL11.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private static Vec3d cameraRelative(Vec3d point) {
        return point.subtract(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
    }

    private static void addDebugMarker(BufferBuilder buffer, Vec3d point, int red, int green, int blue) {
        addDebugLine(buffer, point.add(-DEBUG_MARKER_RADIUS, 0.0D, 0.0D),
                point.add(DEBUG_MARKER_RADIUS, 0.0D, 0.0D), red, green, blue);
        addDebugLine(buffer, point.add(0.0D, -DEBUG_MARKER_RADIUS, 0.0D),
                point.add(0.0D, DEBUG_MARKER_RADIUS, 0.0D), red, green, blue);
        addDebugLine(buffer, point.add(0.0D, 0.0D, -DEBUG_MARKER_RADIUS),
                point.add(0.0D, 0.0D, DEBUG_MARKER_RADIUS), red, green, blue);
    }

    private static void addDebugLine(BufferBuilder buffer, Vec3d from, Vec3d to,
                                     int red, int green, int blue) {
        buffer.pos(from.x, from.y, from.z).color(red, green, blue, 255).endVertex();
        buffer.pos(to.x, to.y, to.z).color(red, green, blue, 255).endVertex();
    }

    private static final class InitialSource {
        private final double yOffset;
        private final Vec3d position;

        private InitialSource(double yOffset, Vec3d position) {
            this.yOffset = yOffset;
            this.position = position;
        }

        private static InitialSource create(EntityPlayer player) {
            double yOffset = WandEffectOrigin.sourceYOffset(player);
            return new InitialSource(yOffset, sourcePos(player, 1.0F, yOffset));
        }
    }
}
