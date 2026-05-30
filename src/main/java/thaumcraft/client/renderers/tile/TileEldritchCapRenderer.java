package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelEldritchCap;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileEldritchAltar;

public class TileEldritchCapRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation CAP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap.png");
    private static final ResourceLocation CAP_TEXTURE_ALTAR =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap_altar.png");
    private static final ResourceLocation CAP_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap_2.png");
    private static final ModelEldritchCap MODEL = new ModelEldritchCap();
    private final ResourceLocation capTexture;

    public TileEldritchCapRenderer() {
        this(CAP_TEXTURE);
    }

    public TileEldritchCapRenderer(ResourceLocation capTexture) {
        this.capTexture = capTexture == null ? CAP_TEXTURE : capTexture;
    }

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        ResourceLocation texture = tile.getWorld() != null && tile.getWorld().provider.getDimension() == Config.dimensionOuterId
                ? CAP_TEXTURE_OUTER
                : capTexture;
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        if (tile.getWorld() != null) {
            int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
            int low = packedLight % 65536;
            int high = packedLight / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, low, high);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.disableCull();
        bindTexture(texture);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        MODEL.renderCap();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);

        if (tile instanceof TileEldritchAltar) {
            renderAltarEyes((TileEldritchAltar) tile, x, y, z);
        }
    }

    private static void renderAltarEyes(TileEldritchAltar altar, double x, double y, double z) {
        if (ConfigItems.itemEldritchObject == null) {
            return;
        }
        int eyes = Math.max(0, Math.min(4, altar.getEyes()));
        if (eyes == 0) {
            return;
        }
        ItemStack eye = new ItemStack(ConfigItems.itemEldritchObject, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        for (int i = 0; i < eyes; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.46D, 0.2D, 0.0D);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(18.0F, -1.0F, 0.0F, 0.0F);
            TileRenderHelper.renderEntityItem(altar, eye, 0.0F);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    public static ResourceLocation altarTexture() {
        return CAP_TEXTURE_ALTAR;
    }
}
