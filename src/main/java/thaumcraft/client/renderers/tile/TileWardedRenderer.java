package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.foci.FocusWarding;
import thaumcraft.common.tiles.TileWarded;

public class TileWardedRenderer extends TileEntitySpecialRenderer<TileWarded> {
    private static final float EXPAND = 0.001F;

    @Override
    public void render(TileWarded tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || !isWardingWandHeld()) {
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        IBlockState storedState = tile.getStoredState();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getBlockModelShapes().getTexture(storedState);
        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        float ticks = player.ticksExisted + partialTicks;
        boolean owner = tile.owner == player.getName().hashCode();
        float r = (float) (Math.sin(ticks / 8.0F + tile.getPos().getX()) * 0.2F + 0.8F);
        float g = (float) (Math.sin(ticks / 10.0F + tile.getPos().getY()) * 0.2F + (owner ? 0.7F : 0.28F));
        float b = (float) (Math.sin(ticks / 12.0F + tile.getPos().getZ()) * 0.2F + 0.28F);
        float a = owner ? 0.50F : 0.25F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (EnumFacing face : EnumFacing.VALUES) {
            if (shouldRenderFace(tile.getPos(), face, tile.owner)) {
                addFace(buf, face, sprite, r, g, b, a);
            }
        }
        tess.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private boolean isWardingWandHeld() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return false;
        }
        return isWardingWand(player.getHeldItemMainhand()) || isWardingWand(player.getHeldItemOffhand());
    }

    private static boolean isWardingWand(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemWandCasting)) {
            return false;
        }
        ItemFocusBasic focus = ((ItemWandCasting) stack.getItem()).getFocus(stack);
        return focus instanceof FocusWarding;
    }

    private boolean shouldRenderFace(BlockPos pos, EnumFacing face, int ownerHash) {
        BlockPos n = pos.offset(face);
        IBlockState state = getWorld().getBlockState(n);
        if (state.getBlock() != ConfigBlocks.blockWarded) {
            return true;
        }
        if (getWorld().getTileEntity(n) instanceof TileWarded) {
            return ((TileWarded) getWorld().getTileEntity(n)).owner != ownerHash;
        }
        return true;
    }

    private static void addFace(BufferBuilder buf, EnumFacing face, TextureAtlasSprite sprite,
                                float r, float g, float b, float a) {
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        float min = -EXPAND;
        float max = 1.0F + EXPAND;

        switch (face) {
            case UP:
                v(buf, min, max, max, u1, v1, r, g, b, a);
                v(buf, min, max, min, u1, v0, r, g, b, a);
                v(buf, max, max, min, u0, v0, r, g, b, a);
                v(buf, max, max, max, u0, v1, r, g, b, a);
                break;
            case DOWN:
                v(buf, min, min, min, u1, v1, r, g, b, a);
                v(buf, min, min, max, u1, v0, r, g, b, a);
                v(buf, max, min, max, u0, v0, r, g, b, a);
                v(buf, max, min, min, u0, v1, r, g, b, a);
                break;
            case NORTH:
                v(buf, min, max, min, u1, v1, r, g, b, a);
                v(buf, min, min, min, u1, v0, r, g, b, a);
                v(buf, max, min, min, u0, v0, r, g, b, a);
                v(buf, max, max, min, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, min, min, max, u1, v1, r, g, b, a);
                v(buf, min, max, max, u1, v0, r, g, b, a);
                v(buf, max, max, max, u0, v0, r, g, b, a);
                v(buf, max, min, max, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, min, max, min, u1, v1, r, g, b, a);
                v(buf, min, max, max, u1, v0, r, g, b, a);
                v(buf, min, min, max, u0, v0, r, g, b, a);
                v(buf, min, min, min, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, max, min, min, u1, v1, r, g, b, a);
                v(buf, max, min, max, u1, v0, r, g, b, a);
                v(buf, max, max, max, u0, v0, r, g, b, a);
                v(buf, max, max, min, u0, v1, r, g, b, a);
                break;
            default:
                break;
        }
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
