package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
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
import thaumcraft.common.blocks.BlockEldritch;

public final class EldritchCrustBakedModel implements IBakedModel {
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private final IBakedModel delegate;
    private final List<List<BakedQuad>> faceQuads;

    public EldritchCrustBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
        ImmutableList.Builder<List<BakedQuad>> builder = ImmutableList.builder();
        for (int mask = 0; mask < 64; mask++) {
            List<BakedQuad> shape = this.buildQuads(mask);
            for (EnumFacing facing : EnumFacing.values()) {
                builder.add(ImmutableList.of(shape.get(facing.getIndex())));
            }
        }
        this.faceQuads = builder.build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return ImmutableList.of();
        }
        int index = this.getNeighborMask(state) * EnumFacing.values().length + side.getIndex();
        return this.faceQuads.get(index);
    }

    private int getNeighborMask(@Nullable IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            Integer mask = ((IExtendedBlockState) state).getValue(BlockEldritch.CRUST_NEIGHBOR_MASK);
            if (mask != null) {
                return mask;
            }
        }
        return 0;
    }

    private List<BakedQuad> buildQuads(int mask) {
        float minX = this.hasNeighbor(mask, EnumFacing.WEST) ? 0.0F : 2.0F;
        float minY = this.hasNeighbor(mask, EnumFacing.DOWN) ? 0.0F : 2.0F;
        float minZ = this.hasNeighbor(mask, EnumFacing.NORTH) ? 0.0F : 2.0F;
        float maxX = this.hasNeighbor(mask, EnumFacing.EAST) ? 16.0F : 14.0F;
        float maxY = this.hasNeighbor(mask, EnumFacing.UP) ? 16.0F : 14.0F;
        float maxZ = this.hasNeighbor(mask, EnumFacing.SOUTH) ? 16.0F : 14.0F;
        Vector3f from = new Vector3f(minX, minY, minZ);
        Vector3f to = new Vector3f(maxX, maxY, maxZ);

        Map<EnumFacing, BlockPartFace> faces = new EnumMap<>(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.values()) {
            faces.put(facing, new BlockPartFace(null, -1, "", new BlockFaceUV(null, 0)));
        }
        BlockPart part = new BlockPart(from, to, faces, null, true);

        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        TextureAtlasSprite sprite = this.delegate.getParticleTexture();
        for (EnumFacing facing : EnumFacing.values()) {
            quads.add(FACE_BAKERY.makeBakedQuad(
                    from,
                    to,
                    part.mapFaces.get(facing),
                    sprite,
                    facing,
                    ModelRotation.X0_Y0,
                    null,
                    false,
                    true));
        }
        return quads.build();
    }

    private boolean hasNeighbor(int mask, EnumFacing facing) {
        return (mask & 1 << facing.getIndex()) != 0;
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
