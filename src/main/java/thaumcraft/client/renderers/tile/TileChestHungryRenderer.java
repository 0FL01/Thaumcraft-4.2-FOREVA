package thaumcraft.client.renderers.tile;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.blocks.BlockChestHungry;
import thaumcraft.common.tiles.TileChestHungry;

public class TileChestHungryRenderer extends TileEntitySpecialRenderer<TileChestHungry> {
    private static final ResourceLocation CHEST_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/chesthungry.png");

    private final ModelChest model = new ModelChest();

    @Override
    public void render(TileChestHungry tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockChestHungry.FACING);
        float yaw = facing == null ? 0.0F : facing.getHorizontalAngle();
        float lid = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;
        lid = 1.0F - lid;
        lid = 1.0F - lid * lid * lid;

        bindTexture(CHEST_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x, y + 1.0D, z + 1.0D);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        this.model.chestLid.rotateAngleX = -(lid * ((float) Math.PI / 2.0F));
        this.model.chestKnob.rotateAngleX = this.model.chestLid.rotateAngleX;
        this.model.chestLid.render(0.0625F);
        this.model.chestKnob.render(0.0625F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
