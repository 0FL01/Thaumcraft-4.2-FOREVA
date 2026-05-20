package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderFollowingItem extends Render<EntityItem> {

    private static final ResourceLocation DEFAULT_ITEM_TEXTURE = TextureMap.LOCATION_BLOCKS_TEXTURE;

    private final RenderEntityItem itemRenderer;

    public RenderFollowingItem(RenderManager renderManager) {
        super(renderManager);
        this.itemRenderer = new RenderEntityItem(renderManager, Minecraft.getMinecraft().getRenderItem());
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.getItem().isEmpty()) {
            this.itemRenderer.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return DEFAULT_ITEM_TEXTURE;
    }
}
