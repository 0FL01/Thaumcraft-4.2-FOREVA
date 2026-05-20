package thaumcraft.client.renderers.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.foci.FocusWarding;
import thaumcraft.common.tiles.TileWarded;

public class TileWardedRenderer extends TileEntitySpecialRenderer<TileWarded> {
    private static final float MIN = -0.5001F;
    private static final float MAX = 0.5001F;
    private static final float Y_MIN = -0.001F;
    private static final float Y_MAX = 1.001F;
    private static final ResourceLocation[] WARDED_GLASS = new ResourceLocation[47];
    private static final Map<CacheKey, ResourceLocation> ICON_CACHE = new HashMap<>();
    private static final int[] CONNECTED_TEXTURE_REF_BY_ID = new int[]{
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            4, 4, 5, 5, 4, 4, 5, 5, 17, 17, 22, 26, 17, 17, 22, 26, 16, 16, 20, 20, 16, 16, 28, 28, 21, 21, 46, 42, 21, 21, 43, 38,
            4, 4, 5, 5, 4, 4, 5, 5, 9, 9, 30, 12, 9, 9, 30, 12, 16, 16, 20, 20, 16, 16, 28, 28, 25, 25, 45, 37, 25, 25, 40, 32,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            4, 4, 5, 5, 4, 4, 5, 5, 17, 17, 22, 26, 17, 17, 22, 26, 7, 7, 24, 24, 7, 7, 10, 10, 29, 29, 44, 41, 29, 29, 39, 33,
            4, 4, 5, 5, 4, 4, 5, 5, 9, 9, 30, 12, 9, 9, 30, 12, 7, 7, 24, 24, 7, 7, 10, 10, 8, 8, 36, 35, 8, 8, 34, 11
    };

    static {
        for (int i = 0; i < WARDED_GLASS.length; i++) {
            WARDED_GLASS[i] = new ResourceLocation("thaumcraft", "textures/blocks/warded_glass_" + (i + 1) + ".png");
        }
    }

    @Override
    public void render(TileWarded tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        renderStoredFacade(tile, x, y, z);
        if (!isWardingWandHeld()) {
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        float ticks = player.ticksExisted + partialTicks;
        boolean owner = tile.owner == player.getName().hashCode();
        float r = (float) (Math.sin(ticks / 8.0F + tile.getPos().getX()) * 0.2F + 0.8F);
        float g = (float) (Math.sin(ticks / 10.0F + tile.getPos().getY()) * 0.2F + (owner ? 0.7F : 0.28F));
        float b = (float) (Math.sin(ticks / 12.0F + tile.getPos().getZ()) * 0.2F + 0.28F);
        float a = owner ? 0.50F : 0.25F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        Tessellator tess = Tessellator.getInstance();
        for (EnumFacing face : EnumFacing.VALUES) {
            if (shouldRenderFace(tile, face)) {
                ResourceLocation texture = getTextureOnSide(
                        tile.getPos(),
                        face.getOpposite().getIndex(),
                        tile.owner,
                        tile.getWorld().getTotalWorldTime());
                bindTexture(texture);

                BufferBuilder buf = tess.getBuffer();
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                addFace(buf, face, r, g, b, a);
                tess.draw();
            }
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderStoredFacade(TileWarded tile, double x, double y, double z) {
        IBlockState storedState = tile.getStoredState();
        if (storedState.getRenderType() != EnumBlockRenderType.MODEL) {
            return;
        }

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        dispatcher.getBlockModelRenderer().renderModel(
                tile.getWorld(),
                dispatcher.getModelForState(storedState),
                storedState,
                tile.getPos(),
                buffer,
                false,
                MathHelper.getPositionRandom(tile.getPos()));
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();

        GlStateManager.enableLighting();
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

    private boolean shouldRenderFace(TileWarded tile, EnumFacing face) {
        BlockPos n = tile.getPos().offset(face);
        IBlockState state = tile.getWorld().getBlockState(n);
        if (state.getBlock() != ConfigBlocks.blockWarded) {
            return true;
        }
        TileEntity neighbor = tile.getWorld().getTileEntity(n);
        if (neighbor instanceof TileWarded) {
            return ((TileWarded) neighbor).blockMd != tile.blockMd;
        }
        return true;
    }

    private ResourceLocation getTextureOnSide(BlockPos pos, int side, int owner, long worldTime) {
        CacheKey key = new CacheKey(pos.getX(), pos.getY(), pos.getZ(), side, owner);
        ResourceLocation cached = ICON_CACHE.get(key);
        if (cached != null && (worldTime + side) % 10L != 0L) {
            return cached;
        }

        boolean[] bitMatrix = new boolean[8];
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (side == 0 || side == 1) {
            bitMatrix[0] = isConnectedBlock(x - 1, y, z - 1, owner);
            bitMatrix[1] = isConnectedBlock(x, y, z - 1, owner);
            bitMatrix[2] = isConnectedBlock(x + 1, y, z - 1, owner);
            bitMatrix[3] = isConnectedBlock(x - 1, y, z, owner);
            bitMatrix[4] = isConnectedBlock(x + 1, y, z, owner);
            bitMatrix[5] = isConnectedBlock(x - 1, y, z + 1, owner);
            bitMatrix[6] = isConnectedBlock(x, y, z + 1, owner);
            bitMatrix[7] = isConnectedBlock(x + 1, y, z + 1, owner);
        } else if (side == 2 || side == 3) {
            bitMatrix[0] = isConnectedBlock(x + (side == 2 ? 1 : -1), y + 1, z, owner);
            bitMatrix[1] = isConnectedBlock(x, y + 1, z, owner);
            bitMatrix[2] = isConnectedBlock(x + (side == 3 ? 1 : -1), y + 1, z, owner);
            bitMatrix[3] = isConnectedBlock(x + (side == 2 ? 1 : -1), y, z, owner);
            bitMatrix[4] = isConnectedBlock(x + (side == 3 ? 1 : -1), y, z, owner);
            bitMatrix[5] = isConnectedBlock(x + (side == 2 ? 1 : -1), y - 1, z, owner);
            bitMatrix[6] = isConnectedBlock(x, y - 1, z, owner);
            bitMatrix[7] = isConnectedBlock(x + (side == 3 ? 1 : -1), y - 1, z, owner);
        } else if (side == 4 || side == 5) {
            bitMatrix[0] = isConnectedBlock(x, y + 1, z + (side == 5 ? 1 : -1), owner);
            bitMatrix[1] = isConnectedBlock(x, y + 1, z, owner);
            bitMatrix[2] = isConnectedBlock(x, y + 1, z + (side == 4 ? 1 : -1), owner);
            bitMatrix[3] = isConnectedBlock(x, y, z + (side == 5 ? 1 : -1), owner);
            bitMatrix[4] = isConnectedBlock(x, y, z + (side == 4 ? 1 : -1), owner);
            bitMatrix[5] = isConnectedBlock(x, y - 1, z + (side == 5 ? 1 : -1), owner);
            bitMatrix[6] = isConnectedBlock(x, y - 1, z, owner);
            bitMatrix[7] = isConnectedBlock(x, y - 1, z + (side == 4 ? 1 : -1), owner);
        }

        int idBuilder = 0;
        for (int i = 0; i < bitMatrix.length; i++) {
            if (bitMatrix[i]) {
                idBuilder += 1 << i;
            }
        }
        if (idBuilder < 0 || idBuilder >= CONNECTED_TEXTURE_REF_BY_ID.length) {
            ICON_CACHE.put(key, WARDED_GLASS[0]);
            return WARDED_GLASS[0];
        }

        int textureIndex = CONNECTED_TEXTURE_REF_BY_ID[idBuilder];
        if (textureIndex < 0 || textureIndex >= WARDED_GLASS.length) {
            ICON_CACHE.put(key, WARDED_GLASS[0]);
            return WARDED_GLASS[0];
        }
        ResourceLocation resolved = WARDED_GLASS[textureIndex];
        ICON_CACHE.put(key, resolved);
        return resolved;
    }

    private boolean isConnectedBlock(int x, int y, int z, int owner) {
        BlockPos check = new BlockPos(x, y, z);
        if (getWorld().getBlockState(check).getBlock() != ConfigBlocks.blockWarded) {
            return false;
        }
        TileEntity tile = getWorld().getTileEntity(check);
        return tile instanceof TileWarded && ((TileWarded) tile).owner == owner;
    }

    private static void addFace(BufferBuilder buf, EnumFacing face, float r, float g, float b, float a) {
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        switch (face) {
            case UP:
                v(buf, MIN, Y_MAX, MAX, u1, v1, r, g, b, a);
                v(buf, MIN, Y_MAX, MIN, u1, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MIN, u0, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MAX, u0, v1, r, g, b, a);
                break;
            case DOWN:
                v(buf, MIN, Y_MIN, MIN, u1, v1, r, g, b, a);
                v(buf, MIN, Y_MIN, MAX, u1, v0, r, g, b, a);
                v(buf, MAX, Y_MIN, MAX, u0, v0, r, g, b, a);
                v(buf, MAX, Y_MIN, MIN, u0, v1, r, g, b, a);
                break;
            case NORTH:
                v(buf, MIN, Y_MAX, MIN, u1, v1, r, g, b, a);
                v(buf, MIN, Y_MIN, MIN, u1, v0, r, g, b, a);
                v(buf, MAX, Y_MIN, MIN, u0, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MIN, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, MIN, Y_MIN, MAX, u1, v1, r, g, b, a);
                v(buf, MIN, Y_MAX, MAX, u1, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MAX, u0, v0, r, g, b, a);
                v(buf, MAX, Y_MIN, MAX, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, MIN, Y_MAX, MIN, u1, v1, r, g, b, a);
                v(buf, MIN, Y_MAX, MAX, u1, v0, r, g, b, a);
                v(buf, MIN, Y_MIN, MAX, u0, v0, r, g, b, a);
                v(buf, MIN, Y_MIN, MIN, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, MAX, Y_MIN, MIN, u1, v1, r, g, b, a);
                v(buf, MAX, Y_MIN, MAX, u1, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MAX, u0, v0, r, g, b, a);
                v(buf, MAX, Y_MAX, MIN, u0, v1, r, g, b, a);
                break;
            default:
                break;
        }
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }

    private static final class CacheKey {
        private final int x;
        private final int y;
        private final int z;
        private final int side;
        private final int owner;

        private CacheKey(int x, int y, int z, int side, int owner) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
            this.owner = owner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }
            CacheKey cacheKey = (CacheKey) o;
            return x == cacheKey.x
                    && y == cacheKey.y
                    && z == cacheKey.z
                    && side == cacheKey.side
                    && owner == cacheKey.owner;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, side, owner);
        }
    }
}
