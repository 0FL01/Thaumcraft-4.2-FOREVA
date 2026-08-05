package thaumcraft.client.renderers.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.common.items.wands.ItemWandCasting;

/** Preserves vanilla EntityItem rendering while exposing that context to the wand TEISR. */
public class RenderWandEntityItem extends RenderEntityItem {

    public RenderWandEntityItem(RenderManager renderManager) {
        super(renderManager, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemWandCasting)) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        ItemWandRenderer.beginEntityItemRender();
        try {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        } finally {
            ItemWandRenderer.endEntityItemRender();
        }
    }
}
