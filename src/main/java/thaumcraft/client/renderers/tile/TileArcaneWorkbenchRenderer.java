package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class TileArcaneWorkbenchRenderer extends TileEntitySpecialRenderer<TileArcaneWorkbench> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/worktable.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();
    private final ModelWand wandModel = new ModelWand();

    @Override
    public void render(TileArcaneWorkbench tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();
        bindTexture(TABLE_TEXTURE);
        GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();

        ItemStack wand = tile.getStackInSlot(10);
        if (!wand.isEmpty() && wand.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wandItem = (ItemWandCasting) wand.getItem();
            boolean staff = wandItem.isStaff(wand);
            EntityPlayer player = Minecraft.getMinecraft().player;
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x + 0.65D, y + 1.0625D, z + 0.25D);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);

                // TC4 applied a fixed 0.1 entity bob, Forge's 0.5 entity-item scale, then
                // ItemWandRenderer's ENTITY offset (and another 0.9 scale for staffs).
                GlStateManager.translate(0.0F, staff ? 1.1F : 0.6F, 0.0F);
                float scale = staff ? 0.45F : 0.5F;
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderHelper.enableStandardItemLighting();
                wandModel.render(wand, partialTicks, player);
            } finally {
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableBlend();
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
            }
        }
    }
}
