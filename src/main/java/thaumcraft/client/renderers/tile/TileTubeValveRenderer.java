package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelTubeValve;
import thaumcraft.common.tiles.TileTubeValve;

public class TileTubeValveRenderer extends TileEntitySpecialRenderer<TileTubeValve> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");
    private static final float MODEL_SCALE = 0.0625F;
    private static final float VALVE_THICKNESS = 0.1F;

    private final ModelTubeValve model = new ModelTubeValve();

    @Override
    public void render(TileTubeValve tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.VALVE, null, x, y, z);

        bindTexture(VALVE_TEXTURE);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            orientByFace(tile.facing);
            GlStateManager.rotate(-tile.rotation * 1.5F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, -(tile.rotation / 360.0F) * 0.12D, 0.0D);
            model.render(MODEL_SCALE);
            renderValveOverlay();
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderValveOverlay() {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(-0.25F, -0.25F, -0.25F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                    .getAtlasSprite("thaumcraft:blocks/pipe_valve");
            ExtrudedSpriteRenderHelper.render(sprite, VALVE_THICKNESS);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void orientByFace(EnumFacing face) {
        if (face.getYOffset() == 0) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else {
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, face.getYOffset(), 0.0F, 0.0F);
        }
        GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset());
    }
}
