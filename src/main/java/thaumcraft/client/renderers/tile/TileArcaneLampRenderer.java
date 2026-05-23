package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelBoreBase;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampFertility;
import thaumcraft.common.tiles.TileArcaneLampGrowth;

public class TileArcaneLampRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bore.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBoreBase model = new ModelBoreBase();

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EnumFacing facing = facingFor(tile);
        renderLampShell(tile, x, y, z);
        bindTexture(BORE_TEXTURE);

        // Lamp nozzle at local block orientation.
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        orientNozzleByFace(facing);
        model.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();

        // Connector nozzle when there is a bore base in front of the lamp.
        if (tile.getWorld().getTileEntity(tile.getPos().offset(facing)) instanceof TileArcaneBoreBase) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    x + 0.5D + facing.getXOffset(),
                    y + facing.getYOffset(),
                    z + 0.5D + facing.getZOffset());
            orientNozzleByFace(facing.getOpposite());
            model.renderNozzle(MODEL_SCALE);
            GlStateManager.popMatrix();
        }
    }

    private void renderLampShell(TileEntity tile, double x, double y, double z) {
        TextureAtlasSprite top = topSprite(tile);
        TextureAtlasSprite side = sideSprite(tile);
        if (top == null || side == null) {
            return;
        }

        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                packedLight & 0xFFFF, (packedLight >> 16) & 0xFFFF);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        TileRenderHelper.drawTexturedCuboid(buf,
                4.0F / 16.0F, 2.0F / 16.0F, 4.0F / 16.0F,
                12.0F / 16.0F, 14.0F / 16.0F, 12.0F / 16.0F,
                top, top, side, side, side, side, 0xFFFFFFFF);
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static TextureAtlasSprite topSprite(TileEntity tile) {
        String path = "thaumcraft:blocks/lamp_top";
        if (tile instanceof TileArcaneLampGrowth) {
            path = ((TileArcaneLampGrowth) tile).charges > 0
                    ? "thaumcraft:blocks/lamp_grow_top"
                    : "thaumcraft:blocks/lamp_grow_top_off";
        } else if (tile instanceof TileArcaneLampFertility) {
            path = ((TileArcaneLampFertility) tile).charges > 0
                    ? "thaumcraft:blocks/lamp_fert_top"
                    : "thaumcraft:blocks/lamp_fert_top_off";
        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(path);
    }

    private static TextureAtlasSprite sideSprite(TileEntity tile) {
        String path = "thaumcraft:blocks/lamp_side";
        if (tile instanceof TileArcaneLampGrowth) {
            path = ((TileArcaneLampGrowth) tile).charges > 0
                    ? "thaumcraft:blocks/lamp_grow_side"
                    : "thaumcraft:blocks/lamp_grow_side_off";
        } else if (tile instanceof TileArcaneLampFertility) {
            path = ((TileArcaneLampFertility) tile).charges > 0
                    ? "thaumcraft:blocks/lamp_fert_side"
                    : "thaumcraft:blocks/lamp_fert_side_off";
        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(path);
    }

    private static EnumFacing facingFor(TileEntity tile) {
        if (tile instanceof TileArcaneLamp) {
            return ((TileArcaneLamp) tile).facing;
        }
        if (tile instanceof TileArcaneLampGrowth) {
            return ((TileArcaneLampGrowth) tile).facing;
        }
        if (tile instanceof TileArcaneLampFertility) {
            return ((TileArcaneLampFertility) tile).facing;
        }
        return EnumFacing.DOWN;
    }

    private static void orientNozzleByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
                break;
            case UP:
                GlStateManager.translate(0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case NORTH:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
    }
}
