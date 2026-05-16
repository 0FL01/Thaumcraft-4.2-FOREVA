package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class RenderGolemBase extends RenderBiped<EntityGolemBase> {

    private static final Map<EnumGolemType, ResourceLocation> GOLEM_TEXTURES = createTextureMap();

    public RenderGolemBase(RenderManager renderManager) {
        super(renderManager, new ModelBiped(), 0.7F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGolemBase entity) {
        ResourceLocation texture = GOLEM_TEXTURES.get(entity.getGolemType());
        if (texture == null) {
            return GOLEM_TEXTURES.get(EnumGolemType.STRAW);
        }
        return texture;
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
}
