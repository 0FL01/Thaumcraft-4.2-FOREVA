package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;
import thaumcraft.common.blocks.BlockCosmeticSolid;

public final class ObsidianTotemBakedModel implements IBakedModel {
    private static final int TEXTURE_TILE = 0;
    private static final int TEXTURE_BASE = 1;
    private static final int TEXTURE_SHADED = 2;
    private static final int TEXTURE_RUNE_START = 3;
    private static final int TEXTURE_COUNT = 7;
    private static final String[] TEXTURES = {
            "obsidiantile", "obsidiantotembase", "obsidiantotembaseshaded",
            "obsidiantotem1", "obsidiantotem2", "obsidiantotem3", "obsidiantotem4"
    };
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private final IBakedModel delegate;
    private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    public ObsidianTotemBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (!(state instanceof IExtendedBlockState)) {
            return this.delegate.getQuads(state, side, rand);
        }
        if (side == null) return ImmutableList.of();

        int texture = getTexture((IExtendedBlockState) state, side);
        int key = side.getIndex() * TEXTURE_COUNT + texture;
        return this.cache.computeIfAbsent(key, ignored -> ImmutableList.of(makeFace(side, texture)));
    }

    private static int getTexture(IExtendedBlockState state, EnumFacing side) {
        if (side == EnumFacing.UP || side == EnumFacing.DOWN) return TEXTURE_TILE;

        Integer style = state.getValue(BlockCosmeticSolid.TOTEM_STYLE);
        if (style == null || style == BlockCosmeticSolid.TOTEM_STYLE_BASE) return TEXTURE_BASE;
        if (style == BlockCosmeticSolid.TOTEM_STYLE_SHADED) return TEXTURE_SHADED;

        Integer variant = state.getValue(BlockCosmeticSolid.TOTEM_VARIANT);
        int rune = Math.abs(side.getIndex() + (variant == null ? 0 : variant)) % 4;
        return TEXTURE_RUNE_START + rune;
    }

    private static BakedQuad makeFace(EnumFacing face, int texture) {
        BlockPartFace partFace = new BlockPartFace(null, -1, "", FULL_UV);
        return FACE_BAKERY.makeBakedQuad(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(16.0F, 16.0F, 16.0F),
                partFace,
                sprite(texture),
                face,
                ModelRotation.X0_Y0,
                null,
                false,
                true);
    }

    private static TextureAtlasSprite sprite(int texture) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(
                "thaumcraft:blocks/" + TEXTURES[texture]);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.delegate.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.delegate.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.delegate.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.delegate.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.delegate.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.delegate.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> perspective = this.delegate.handlePerspective(cameraTransformType);
        return Pair.of(this, perspective.getRight());
    }
}
