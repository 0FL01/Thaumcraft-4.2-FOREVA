package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.renderers.models.ModelThaumatorium;
import thaumcraft.common.tiles.TileThaumatorium;

public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer<TileThaumatorium> {

    private static final ResourceLocation THAUMATORIUM_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/thaumatorium.png");
    private final ModelThaumatorium model = new ModelThaumatorium();

    @Override
    public void render(TileThaumatorium tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        renderThaumatoriumModel(tile, x, y, z);
        if (tile.getWorld() == null) {
            return;
        }
        renderOutputItem(tile, x, y, z, partialTicks);
    }

    private void renderThaumatoriumModel(TileThaumatorium tile, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        rotateByFacing(tile.facing);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(THAUMATORIUM_TEXTURE);
        model.renderAll();
        GlStateManager.popMatrix();
    }

    private void renderOutputItem(TileThaumatorium tile, double x, double y, double z, float partialTicks) {
        if (tile.recipeHash == null || tile.recipeHash.isEmpty()) {
            return;
        }
        int index = (int) (tile.getWorld().getTotalWorldTime() / 40L % tile.recipeHash.size());
        CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(tile.recipeHash.get(index));
        if (recipe == null) {
            return;
        }

        ItemStack output = recipe.getRecipeOutput();
        if (output == null || output.isEmpty()) {
            return;
        }

        EnumFacing facing = tile.facing == null ? EnumFacing.NORTH : tile.facing;
        double ix = x + 0.5D + facing.getXOffset() / 1.99D;
        double iy = y + 1.325D;
        double iz = z + 0.5D + facing.getZOffset() / 1.99D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(ix, iy, iz);
        rotateItemByFacing(facing);
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        TileRenderHelper.renderEntityItem(tile, output, 0.0F);
        GlStateManager.popMatrix();
    }

    private static void rotateByFacing(EnumFacing facing) {
        if (facing == EnumFacing.EAST) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        } else if (facing == EnumFacing.SOUTH) {
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (facing == EnumFacing.NORTH) {
            GlStateManager.rotate(270.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    private static void rotateItemByFacing(EnumFacing facing) {
        if (facing == EnumFacing.EAST) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.WEST) {
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else if (facing == EnumFacing.NORTH) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }
    }
}
