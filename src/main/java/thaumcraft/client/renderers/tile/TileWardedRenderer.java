package thaumcraft.client.renderers.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.foci.FocusWarding;
import thaumcraft.common.lib.utils.ConnectedTextureUtils;
import thaumcraft.common.tiles.TileWarded;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Side.CLIENT)
public class TileWardedRenderer extends TileEntitySpecialRenderer<TileWarded> {
    private static final float MIN = -0.5001F;
    private static final float MAX = 0.5001F;
    private static final float Y_MIN = -0.001F;
    private static final float Y_MAX = 1.001F;
    private static final ResourceLocation[] WARDED_GLASS = new ResourceLocation[47];
    private static final Map<CacheKey, ResourceLocation> ICON_CACHE = new HashMap<>();

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

        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean alphaTestEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.enableAlpha();
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
        } finally {
            GlStateManager.alphaFunc(alphaFunc, alphaRef);
            if (alphaTestEnabled) {
                GlStateManager.enableAlpha();
            } else {
                GlStateManager.disableAlpha();
            }
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (lightingEnabled) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderStoredFacade(TileWarded tile, double x, double y, double z) {
        IBlockState storedState = tile.getStoredState();
        if (storedState.getRenderType() != EnumBlockRenderType.MODEL) {
            return;
        }

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            IBlockAccess facadeWorld = new StoredBlockAccess(tile.getWorld(), tile.getPos(), storedState);
            IBlockState actualState = storedState.getActualState(facadeWorld, tile.getPos());
            actualState = actualState.getBlock().getExtendedState(actualState, facadeWorld, tile.getPos());
            Block block = actualState.getBlock();
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                if (!block.canRenderInLayer(actualState, layer)) {
                    continue;
                }
                ForgeHooksClient.setRenderLayer(layer);
                if (layer == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                            GL11.GL_ONE, GL11.GL_ZERO);
                } else {
                    GlStateManager.disableBlend();
                }
                renderFacadeLayer(dispatcher, tile, facadeWorld, actualState);
            }
        } finally {
            ForgeHooksClient.setRenderLayer(null);
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (lightingEnabled) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderFacadeLayer(BlockRendererDispatcher dispatcher, TileWarded tile,
                                   IBlockAccess facadeWorld, IBlockState state) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        try {
            buffer.setTranslation(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
            dispatcher.getBlockModelRenderer().renderModel(
                    facadeWorld,
                    dispatcher.getModelForState(state),
                    state,
                    tile.getPos(),
                    buffer,
                    false,
                    MathHelper.getPositionRandom(tile.getPos()));
        } finally {
            buffer.setTranslation(0.0D, 0.0D, 0.0D);
        }
        tessellator.draw();
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
            return !hasSameStoredBlock(tile, (TileWarded) neighbor);
        }
        return true;
    }

    private static boolean hasSameStoredBlock(TileWarded left, TileWarded right) {
        return left.block == right.block && (left.blockMd & 255) == (right.blockMd & 255);
    }

    private ResourceLocation getTextureOnSide(BlockPos pos, int side, int owner, long worldTime) {
        int dimension = getWorld().provider.getDimension();
        CacheKey key = new CacheKey(dimension, pos.getX(), pos.getY(), pos.getZ(), side, owner);
        ResourceLocation cached = ICON_CACHE.get(key);
        if (cached != null && (worldTime + side) % 10L != 0L) {
            return cached;
        }

        int textureIndex = ConnectedTextureUtils.getTextureIndex(pos, side,
                check -> isConnectedBlock(check.getX(), check.getY(), check.getZ(), owner));
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

    public static void invalidate(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return;
        }
        int dimension = world.provider.getDimension();
        ICON_CACHE.keySet().removeIf(key -> key.dimension == dimension
                && Math.abs(key.x - pos.getX()) <= 1
                && Math.abs(key.y - pos.getY()) <= 1
                && Math.abs(key.z - pos.getZ()) <= 1);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() != null && event.getWorld().isRemote) {
            ICON_CACHE.clear();
        }
    }

    private static final class StoredBlockAccess implements IBlockAccess {
        private final IBlockAccess delegate;
        private final BlockPos pos;
        private final IBlockState storedState;

        private StoredBlockAccess(IBlockAccess delegate, BlockPos pos, IBlockState storedState) {
            this.delegate = delegate;
            this.pos = pos;
            this.storedState = storedState;
        }

        @Override
        public TileEntity getTileEntity(BlockPos check) {
            return this.pos.equals(check) ? null : this.delegate.getTileEntity(check);
        }

        @Override
        public int getCombinedLight(BlockPos check, int lightValue) {
            return this.delegate.getCombinedLight(check, lightValue);
        }

        @Override
        public IBlockState getBlockState(BlockPos check) {
            return this.pos.equals(check) ? this.storedState : this.delegate.getBlockState(check);
        }

        @Override
        public boolean isAirBlock(BlockPos check) {
            return this.pos.equals(check) ? this.storedState.getBlock().isAir(this.storedState, this, check)
                    : this.delegate.isAirBlock(check);
        }

        @Override
        public Biome getBiome(BlockPos check) {
            return this.delegate.getBiome(check);
        }

        @Override
        public int getStrongPower(BlockPos check, EnumFacing direction) {
            return this.delegate.getStrongPower(check, direction);
        }

        @Override
        public WorldType getWorldType() {
            return this.delegate.getWorldType();
        }

        @Override
        public boolean isSideSolid(BlockPos check, EnumFacing side, boolean defaultValue) {
            return this.pos.equals(check) ? this.storedState.isSideSolid(this, check, side)
                    : this.delegate.isSideSolid(check, side, defaultValue);
        }
    }

    private static final class CacheKey {
        private final int dimension;
        private final int x;
        private final int y;
        private final int z;
        private final int side;
        private final int owner;

        private CacheKey(int dimension, int x, int y, int z, int side, int owner) {
            this.dimension = dimension;
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
            return dimension == cacheKey.dimension
                    && x == cacheKey.x
                    && y == cacheKey.y
                    && z == cacheKey.z
                    && side == cacheKey.side
                    && owner == cacheKey.owner;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dimension, x, y, z, side, owner);
        }
    }
}
