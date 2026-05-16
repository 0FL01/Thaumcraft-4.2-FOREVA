package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;

import javax.annotation.Nullable;

public class RenderTravelingTrunk extends RenderLiving<EntityTravelingTrunk> {

    private static final ResourceLocation TRUNK_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunk.png");
    private static final ResourceLocation TRUNK_ANGRY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/trunkangry.png");

    public RenderTravelingTrunk(RenderManager renderManager) {
        super(renderManager, new ModelPig(), 0.6F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityTravelingTrunk entity) {
        return entity.getAnger() > 0 ? TRUNK_ANGRY_TEXTURE : TRUNK_TEXTURE;
    }
}
