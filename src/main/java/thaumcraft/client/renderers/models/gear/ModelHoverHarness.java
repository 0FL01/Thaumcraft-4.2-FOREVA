package thaumcraft.client.renderers.models.gear;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import thaumcraft.client.ClientModelRegistry;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.render.Vertex5;
import thaumcraft.codechicken.lib.vec.Vector3;

public class ModelHoverHarness extends ModelBiped {
    private static final ResourceLocation HARNESS_MODEL =
            new ResourceLocation("thaumcraft", "textures/models/hoverharness.obj");
    private static final ResourceLocation HARNESS_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/hoverharness2.png");
    private static final double OBJ_UV_INSET = 0.0005D;

    private final CCModel engine;
    private final Map<Integer, Long> timingShock = new HashMap<>();

    public ModelHoverHarness() {
        super();
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.6F);

        Map<String, CCModel> models = CCModel.parseObjModels(HARNESS_MODEL);
        CCModel parsedEngine = models.get("Cylinder001");
        if (parsedEngine == null) {
            throw new IllegalStateException("Hover harness OBJ is missing Cylinder001");
        }
        this.engine = restoreObjFaceOrderAndInsetUvs(parsedEngine);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        boolean sneaking = entity != null && entity.isSneaking();
        GlStateManager.pushMatrix();
        try {
            if (sneaking) {
                GlStateManager.rotate(28.64789F, 1.0F, 0.0F, 0.0F);
            }
            this.bipedBody.render(scale);
        } finally {
            GlStateManager.popMatrix();
        }

        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableLighting();
            GlStateManager.scale(0.1F, 0.1F, 0.1F);
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            if (sneaking) {
                GlStateManager.rotate(28.64789F, 1.0F, 0.0F, 0.0F);
            }
            GlStateManager.translate(0.0F, 0.33F, -3.7F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(HARNESS_TEXTURE);
            renderModel(this.engine);
        } finally {
            if (lightingEnabled) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            GlStateManager.popMatrix();
        }

        if (isHoverActive(entity)) {
            EntityPlayer player = (EntityPlayer) entity;
            renderLightningRings(sneaking);
            spawnLightningBolt(player, sneaking);
        }
    }

    private static boolean isHoverActive(Entity entity) {
        if (!(entity instanceof EntityPlayer)
                || !GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)
                || GL11.glGetInteger(GL11.GL_MATRIX_MODE) != GL11.GL_MODELVIEW) {
            return false;
        }
        ItemStack harness = ((EntityPlayer) entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return !harness.isEmpty() && harness.hasTagCompound()
                && harness.getTagCompound().hasKey("hover")
                && harness.getTagCompound().getByte("hover") == 1;
    }

    private static void renderLightningRings(boolean sneaking) {
        Minecraft minecraft = Minecraft.getMinecraft();
        TextureAtlasSprite sprite = minecraft.getTextureMapBlocks()
                .getAtlasSprite(ClientModelRegistry.LIGHTNING_RING_SPRITE.toString());
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            if (sneaking) {
                GlStateManager.rotate(28.64789F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.075F, -0.05F);
            }
            GlStateManager.translate(0.0F, 0.2F, 0.55F);
            minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            GlStateManager.pushMatrix();
            try {
                renderRing(sprite, 2.5F, 1.0F, 1.0F, 1.0F, 1.0F);
            } finally {
                GlStateManager.popMatrix();
            }
            GlStateManager.pushMatrix();
            try {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 0.03F);
                renderRing(sprite, 1.5F, 1.0F, 0.5F, 1.0F, 1.0F);
            } finally {
                GlStateManager.popMatrix();
            }
        } finally {
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
            GlStateManager.popMatrix();
        }
    }

    private static void renderRing(TextureAtlasSprite sprite, float scale, float red, float green,
                                   float blue, float opacity) {
        GlStateManager.scale(scale, scale, scale);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        ringVertex(buffer, -0.5D, 0.5D, sprite.getMinU(), sprite.getMaxV(), red, green, blue, opacity);
        ringVertex(buffer, 0.5D, 0.5D, sprite.getMaxU(), sprite.getMaxV(), red, green, blue, opacity);
        ringVertex(buffer, 0.5D, -0.5D, sprite.getMaxU(), sprite.getMinV(), red, green, blue, opacity);
        ringVertex(buffer, -0.5D, -0.5D, sprite.getMinU(), sprite.getMinV(), red, green, blue, opacity);
        tessellator.draw();
    }

    private static void ringVertex(BufferBuilder buffer, double x, double y, double u, double v,
                                   float red, float green, float blue, float opacity) {
        buffer.pos(x, y, 0.0D).color(red, green, blue, opacity).tex(u, v).lightmap(230, 0)
                .endVertex();
    }

    private void spawnLightningBolt(EntityPlayer player, boolean sneaking) {
        long currentTime = System.currentTimeMillis();
        long nextShock = this.timingShock.getOrDefault(player.getEntityId(), 0L);
        if (nextShock >= currentTime) {
            return;
        }
        this.timingShock.put(player.getEntityId(), currentTime + 50L + player.world.rand.nextInt(50));

        float sneakOffset = sneaking ? 0.075F : 0.0F;
        Vec3d start = new Vec3d(player.posX, player.posY - 0.45F - sneakOffset, player.posZ);
        float yaw = player.renderYawOffset - 90.0F - player.world.rand.nextInt(180);
        float pitch = -80.0F + player.world.rand.nextInt(160);
        Vec3d end = start.add(Vec3d.fromPitchYaw(pitch, yaw).scale(6.0D));
        RayTraceResult hit = player.world.rayTraceBlocks(start, end, false, true, false);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        float sourceAngle = (player.renderYawOffset + 90.0F) / 180.0F * (float) Math.PI;
        double sourceX = player.posX - MathHelper.cos(sourceAngle) * 0.5F;
        double sourceY = player.posY - 0.45F - sneakOffset;
        double sourceZ = player.posZ - MathHelper.sin(sourceAngle) * 0.5F;
        FXLightningBolt bolt = new FXLightningBolt(player.world, sourceX, sourceY, sourceZ,
                hit.hitVec.x, hit.hitVec.y, hit.hitVec.z, player.world.rand.nextLong(), 1, 2.0F, 3);
        bolt.defaultFractal();
        bolt.setType(6);
        bolt.setWidth(0.015F);
        bolt.finalizeBolt();
    }

    private static void renderModel(CCModel model) {
        CCRenderState.reset();
        CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        model.render(CCRenderState.normalAttrib);
        CCRenderState.draw();
    }

    private static CCModel restoreObjFaceOrderAndInsetUvs(CCModel model) {
        CCModel corrected = model.backfacedCopy();
        Vector3[] normals = corrected.normals();
        if (normals != null) {
            for (Vector3 normal : normals) {
                if (normal != null) {
                    normal.negate();
                }
            }
        }

        Vertex5[] vertices = corrected.getVertices();
        for (int i = 0; i < vertices.length; i += 3) {
            double averageU = (vertices[i].uv.u + vertices[i + 1].uv.u + vertices[i + 2].uv.u) / 3.0D;
            double averageV = (vertices[i].uv.v + vertices[i + 1].uv.v + vertices[i + 2].uv.v) / 3.0D;
            for (int corner = i; corner < i + 3; ++corner) {
                vertices[corner].uv.u += insetTowardsAverage(vertices[corner].uv.u, averageU);
                vertices[corner].uv.v += insetTowardsAverage(vertices[corner].uv.v, averageV);
            }
        }
        return corrected;
    }

    private static double insetTowardsAverage(double coordinate, double average) {
        return coordinate > average ? -OBJ_UV_INSET : coordinate < average ? OBJ_UV_INSET : 0.0D;
    }
}
