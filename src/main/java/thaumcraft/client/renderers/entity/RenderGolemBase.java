package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entities.ModelGolem;
import thaumcraft.client.renderers.models.entities.ModelGolemAccessories;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class RenderGolemBase extends RenderLiving<EntityGolemBase> {

    private static final Map<EnumGolemType, ResourceLocation> GOLEM_TEXTURES = createTextureMap();
    private static final ResourceLocation GOLEM_DAMAGE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/golem_damage.png");
    private static final ResourceLocation GOLEM_DECORATION_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/golem_decoration.png");
    private static final ResourceLocation GOLEM_UPGRADE_EMPTY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/items/golem_upgrade_empty.png");

    public RenderGolemBase(RenderManager renderManager) {
        super(renderManager, new ModelGolem(false), 0.25F);
        this.addLayer(new GolemCoreLayer(this));
        this.addLayer(new GolemAccessoriesLayer(this));
        this.addLayer(new GolemDamageLayer(this));
        this.addLayer(new GolemHeldItemLayer(this));
    }

    @Override
    protected void applyRotations(EntityGolemBase entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
        float limbSwingAmount = entityLiving.prevLimbSwingAmount
                + (entityLiving.limbSwingAmount - entityLiving.prevLimbSwingAmount) * partialTicks;
        if ((double) limbSwingAmount >= 0.01D) {
            float wavePeriod = 13.0F;
            float swing = entityLiving.limbSwing - limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float wave = (Math.abs(swing % wavePeriod - wavePeriod * 0.5F) - wavePeriod * 0.25F) / (wavePeriod * 0.25F);
            GlStateManager.rotate(6.5F * wave, 0.0F, 0.0F, 1.0F);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGolemBase entity) {
        ResourceLocation texture = GOLEM_TEXTURES.get(entity.getGolemType());
        return texture != null ? texture : GOLEM_TEXTURES.get(EnumGolemType.STRAW);
    }

    private static Map<EnumGolemType, ResourceLocation> createTextureMap() {
        Map<EnumGolemType, ResourceLocation> textures = new EnumMap<>(EnumGolemType.class);
        textures.put(EnumGolemType.STRAW, new ResourceLocation("thaumcraft", "textures/models/golem_straw.png"));
        textures.put(EnumGolemType.WOOD, new ResourceLocation("thaumcraft", "textures/models/golem_wood.png"));
        textures.put(EnumGolemType.TALLOW, new ResourceLocation("thaumcraft", "textures/models/golem_tallow.png"));
        textures.put(EnumGolemType.CLAY, new ResourceLocation("thaumcraft", "textures/models/golem_clay.png"));
        textures.put(EnumGolemType.FLESH, new ResourceLocation("thaumcraft", "textures/models/golem_flesh.png"));
        textures.put(EnumGolemType.STONE, new ResourceLocation("thaumcraft", "textures/models/golem_stone.png"));
        textures.put(EnumGolemType.IRON, new ResourceLocation("thaumcraft", "textures/models/golem_iron.png"));
        textures.put(EnumGolemType.THAUMIUM, new ResourceLocation("thaumcraft", "textures/models/golem_thaumium.png"));
        return textures;
    }

    private static final class GolemCoreLayer implements LayerRenderer<EntityGolemBase> {
        private final RenderGolemBase renderer;
        private final ModelGolem model;

        private GolemCoreLayer(RenderGolemBase renderer) {
            this.renderer = renderer;
            this.model = (ModelGolem) renderer.getMainModel();
        }

        @Override
        public void doRenderLayer(EntityGolemBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.renderCore(entity);
            this.renderUpgrades(entity);
        }

        private void renderCore(EntityGolemBase entity) {
            int core = entity.getCore();
            if (core < 0) {
                return;
            }
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                    .getParticleIcon(ConfigItems.itemGolemCore, core);
            if (sprite == null) {
                return;
            }
            this.renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            beginIconRender();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
            this.model.golemBody.postRender(0.0625F);
            float z = entity.getGolemDecoration() != null && entity.getGolemDecoration().contains("P") ? -7.25F : -6.05F;
            renderBodySprite(0.0F, 4.0F, z, 0.4375F);
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            renderSprite(sprite, 0.0D);
            endIconRender();
            GlStateManager.popMatrix();
        }

        private void renderUpgrades(EntityGolemBase entity) {
            if (entity.upgrades == null || entity.upgrades.length == 0) {
                return;
            }
            float shift = 0.08F;
            GlStateManager.pushMatrix();
            beginIconRender();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
            this.model.golemBody.postRender(0.0625F);
            for (int slot = 0; slot < entity.upgrades.length; slot++) {
                int upgrade = entity.getUpgrade(slot);
                float x = (shift * ((float) slot - (float) (entity.upgrades.length - 1) / 2.0F)) / 0.025F;
                GlStateManager.pushMatrix();
                if (upgrade < 0) {
                    this.renderer.bindTexture(GOLEM_UPGRADE_EMPTY_TEXTURE);
                    renderBodySprite(x, 12.0F, -4.0F, 0.25F);
                    renderSprite(0.0D);
                } else {
                    TextureAtlasSprite sprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                            .getParticleIcon(ConfigItems.itemGolemUpgrade, upgrade);
                    if (sprite != null) {
                        this.renderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        renderBodySprite(x, 12.0F, -4.0F, 0.25F);
                        renderSprite(sprite, 0.0D);
                    }
                }
                GlStateManager.popMatrix();
            }
            endIconRender();
            GlStateManager.popMatrix();
        }

        private static void beginIconRender() {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
        }

        private static void endIconRender() {
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        private static void renderBodySprite(float x, float y, float z, float size) {
            GlStateManager.translate(x * 0.0625F, y * 0.0625F, z * 0.0625F);
            GlStateManager.scale(size, size, size);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        }

        private static void renderSprite(TextureAtlasSprite sprite, double z) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(0.0D, 0.0D, z).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
            buffer.pos(1.0D, 0.0D, z).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
            buffer.pos(1.0D, 1.0D, z).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
            buffer.pos(0.0D, 1.0D, z).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
            tessellator.draw();
        }

        private static void renderSprite(double z) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(0.0D, 0.0D, z).tex(0.0D, 1.0D).endVertex();
            buffer.pos(1.0D, 0.0D, z).tex(1.0D, 1.0D).endVertex();
            buffer.pos(1.0D, 1.0D, z).tex(1.0D, 0.0D).endVertex();
            buffer.pos(0.0D, 1.0D, z).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private static final class GolemAccessoriesLayer implements LayerRenderer<EntityGolemBase> {
        private final RenderGolemBase renderer;
        private final ModelGolemAccessories accessoriesModel = new ModelGolemAccessories(0.0F, 30.0F);

        private GolemAccessoriesLayer(RenderGolemBase renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(EntityGolemBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if ((entity.getGolemDecoration() == null || entity.getGolemDecoration().isEmpty()) && !entity.advanced) {
                return;
            }
            this.renderer.bindTexture(GOLEM_DECORATION_TEXTURE);
            this.accessoriesModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.accessoriesModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private static final class GolemDamageLayer implements LayerRenderer<EntityGolemBase> {
        private final RenderGolemBase renderer;
        private final ModelGolem damageModel = new ModelGolem(false);

        private GolemDamageLayer(RenderGolemBase renderer) {
            this.renderer = renderer;
            this.damageModel.pass = 2;
        }

        @Override
        public void doRenderLayer(EntityGolemBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.getHealthPercentage() >= 1.0F) {
                return;
            }
            this.renderer.bindTexture(GOLEM_DAMAGE_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F - entity.getHealthPercentage());
            this.damageModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.damageModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private static final class GolemHeldItemLayer implements LayerRenderer<EntityGolemBase> {
        private final RenderGolemBase renderer;

        private GolemHeldItemLayer(RenderGolemBase renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(EntityGolemBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.deathTime > 0) {
                return;
            }

            int core = entity.getCore();

            // Fisher golem: render fishing rod in right hand
            if (core == 11) {
                renderFishingRod(entity);
            }

            // Fluid golem: render bucket with fluid contents
            if (core == 5) {
                renderFluidBucket(entity);
            }

            // Carried items (skip fluid core 5 — it has its own rendering)
            ItemStack carried = entity.getCarriedForDisplay();
            if (core != 5 && !carried.isEmpty()) {
                renderCarriedItem(entity, carried);
            }
        }

        private void renderFishingRod(EntityGolemBase entity) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);

            ModelGolem model = (ModelGolem) this.renderer.getMainModel();
            model.golemRightArm.postRender(0.0625F);

            // Position at the hand area: bottom-center of the arm box
            GlStateManager.translate(-10.0F * 0.0625F, 20.0F * 0.0625F, 0.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(
                    entity,
                    new ItemStack(Items.FISHING_ROD),
                    ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    false);

            GlStateManager.popMatrix();
        }

        private void renderCarriedItem(EntityGolemBase entity, ItemStack stack) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);

            ItemCameraTransforms.TransformType transformType;
            if (stack.getItem() instanceof BlockJarItem) {
                GlStateManager.translate(0.0F, 2.5F, -1.0F);
                float jarScale = (1.0F + (float) Math.min(64, entity.getCarryLimit()) / 64.0F) * 2.25F;
                GlStateManager.scale(jarScale, jarScale, jarScale);
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                transformType = ItemCameraTransforms.TransformType.FIXED;
            } else if (stack.getItem() instanceof ItemBlock) {
                GlStateManager.translate(0.0F, 2.5F, -1.25F);
                // Blocks: center between the hands without extra item rotation
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                transformType = ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            } else {
                GlStateManager.translate(0.0F, 2.5F, -1.25F);
                GlStateManager.scale(1.25F, 1.25F, 1.25F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                transformType = ItemCameraTransforms.TransformType.FIXED;
            }

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(
                    entity,
                    stack,
                    transformType,
                    false);

            GlStateManager.popMatrix();
        }

        private void renderFluidBucket(EntityGolemBase entity) {
            if (entity.fluidCarried == null || entity.fluidCarried.amount <= 0) return;

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.4F, 0.4F, 0.4F);

            ModelGolem model = (ModelGolem) this.renderer.getMainModel();
            model.golemRightArm.postRender(0.0625F);
            GlStateManager.translate(-10.0F * 0.0625F, 20.0F * 0.0625F, 0.0F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

            // Try to render a filled bucket item if a bucket is registered for this fluid
            FluidStack fluidStack = entity.fluidCarried.copy();
            ItemStack bucketStack = FluidUtil.getFilledBucket(fluidStack);
            boolean useEmptyBucket = bucketStack.isEmpty();

            if (useEmptyBucket) {
                // No registered bucket item — render empty bucket + fluid overlay
                bucketStack = new ItemStack(Items.BUCKET);
            }

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(
                    entity, bucketStack,
                    ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false);

            // When a vanilla filled-bucket was used, the fluid is already shown in the model.
            // When falling back to empty bucket + overlay, draw the fluid sprite here.
            if (useEmptyBucket && fluidStack.getFluid() != null) {
                renderFluidFillIndicator(entity, fluidStack);
            }

            GlStateManager.popMatrix();
        }

        private void renderFluidFillIndicator(EntityGolemBase entity, FluidStack fluidStack) {
            Fluid fluid = fluidStack.getFluid();
            if (fluid == null) return;

            ResourceLocation stillLocation = fluid.getStill();
            if (stillLocation == null) return;

            TextureAtlasSprite fluidSprite = Minecraft.getMinecraft().getTextureMapBlocks()
                    .getAtlasSprite(stillLocation.toString());
            if (fluidSprite == null) return;

            float fillRatio = Math.min(1.0F, (float) fluidStack.amount / (float) entity.getFluidCarryLimit());
            if (fillRatio <= 0.0F) return;

            int color = fluid.getColor(fluidStack);
            float r = (float) ((color >> 16) & 0xFF) / 255.0F;
            float g = (float) ((color >> 8) & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();
            GlStateManager.color(r, g, b, 1.0F);

            // Position slightly forward and up to sit inside/atop the bucket opening
            GlStateManager.translate(0.0F, 0.25F, -0.35F);
            float quadSize = 0.35F;
            GlStateManager.scale(quadSize, quadSize * fillRatio, quadSize);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buf.pos(-1.0D, -1.0D, 0.0D).tex(fluidSprite.getMinU(), fluidSprite.getMaxV()).endVertex();
            buf.pos(1.0D, -1.0D, 0.0D).tex(fluidSprite.getMaxU(), fluidSprite.getMaxV()).endVertex();
            buf.pos(1.0D, 1.0D, 0.0D).tex(fluidSprite.getMaxU(), fluidSprite.getMinV()).endVertex();
            buf.pos(-1.0D, 1.0D, 0.0D).tex(fluidSprite.getMinU(), fluidSprite.getMinV()).endVertex();
            tessellator.draw();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
