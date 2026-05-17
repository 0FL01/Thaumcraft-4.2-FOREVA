package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.TileThaumatorium;

public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer<TileThaumatorium> {

    @Override
    public void render(TileThaumatorium tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || tile.recipeHash == null || tile.recipeHash.isEmpty()) {
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
        double ix = x + 0.5D + facing.getXOffset() * 0.50D;
        double iy = y + 1.30D;
        double iz = z + 0.5D + facing.getZOffset() * 0.50D;

        GlStateManager.pushMatrix();
        GlStateManager.translate(ix, iy, iz);
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        TileRenderHelper.renderFloatingItem(output.copy(), TileRenderHelper.ticks(tile, partialTicks), 0.0F, 0.75F);
        GlStateManager.popMatrix();
    }
}
