package thaumcraft.client.renderers.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.entities.ModelGolem;
import thaumcraft.client.renderers.models.entities.ModelGolemAccessories;
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

    public RenderGolemBase(RenderManager renderManager) {
        super(renderManager, new ModelGolem(false), 0.25F);
        this.addLayer(new GolemAccessoriesLayer(this));
        this.addLayer(new GolemDamageLayer(this));
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
}
