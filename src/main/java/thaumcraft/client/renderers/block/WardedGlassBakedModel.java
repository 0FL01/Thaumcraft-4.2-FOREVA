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
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;
import thaumcraft.common.blocks.BlockCosmeticOpaque;

public final class WardedGlassBakedModel implements IBakedModel {
    private static final int TEXTURE_COUNT = 47;
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private final IBakedModel delegate;
    private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    public WardedGlassBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return ImmutableList.of();
        }
        int textureIndex = state == null ? 0 : this.getTextureIndex(state, side);
        int key = side.getIndex() * TEXTURE_COUNT + textureIndex;
        return this.cache.computeIfAbsent(key, ignored -> ImmutableList.of(this.makeFace(side, textureIndex)));
    }

    private BakedQuad makeFace(EnumFacing face, int textureIndex) {
        BlockPartFace partFace = new BlockPartFace(null, -1, "", FULL_UV);
        return FACE_BAKERY.makeBakedQuad(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(16.0F, 16.0F, 16.0F),
                partFace,
                this.sprite(textureIndex),
                face,
                ModelRotation.X0_Y0,
                null,
                false,
                true);
    }

    private int getTextureIndex(IBlockState state, EnumFacing side) {
        IUnlistedProperty<Integer> property;
        switch (side) {
            case DOWN:
                property = BlockCosmeticOpaque.GLASS_DOWN;
                break;
            case UP:
                property = BlockCosmeticOpaque.GLASS_UP;
                break;
            case NORTH:
                property = BlockCosmeticOpaque.GLASS_NORTH;
                break;
            case SOUTH:
                property = BlockCosmeticOpaque.GLASS_SOUTH;
                break;
            case WEST:
                property = BlockCosmeticOpaque.GLASS_WEST;
                break;
            case EAST:
            default:
                property = BlockCosmeticOpaque.GLASS_EAST;
                break;
        }
        if (state instanceof IExtendedBlockState) {
            Integer value = ((IExtendedBlockState) state).getValue(property);
            if (value != null && value >= 0 && value < TEXTURE_COUNT) {
                return value;
            }
        }
        return 0;
    }

    private TextureAtlasSprite sprite(int index) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(
                "thaumcraft:blocks/warded_glass_" + (index + 1));
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.sprite(0);
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
        Pair<? extends IBakedModel, Matrix4f> delegatePerspective = this.delegate.handlePerspective(cameraTransformType);
        return Pair.of(this, delegatePerspective.getRight());
    }
}
