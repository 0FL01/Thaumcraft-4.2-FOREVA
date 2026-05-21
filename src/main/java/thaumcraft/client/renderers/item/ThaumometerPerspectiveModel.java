package thaumcraft.client.renderers.item;

import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

public final class ThaumometerPerspectiveModel implements IBakedModel {

    private final IBakedModel delegate;

    public ThaumometerPerspectiveModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return delegate.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return delegate.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return delegate.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return delegate.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return delegate.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return delegate.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return delegate.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        ItemThaumometerRenderer.setTransformType(cameraTransformType);
        // Keep donor first-person display matrices intact here.
        // A previous attempt replaced them with an identity matrix and reimplemented
        // the hand/equipped-progress transforms locally, but live testing showed that
        // route regressed the held pose more severely than it helped the HUD. Until
        // the full TC4 first-person scanner surface is ported end-to-end, the baked
        // model remains responsible for item placement in every render context.
        Pair<? extends IBakedModel, Matrix4f> delegatePerspective = delegate.handlePerspective(cameraTransformType);
        return Pair.of(this, delegatePerspective.getRight());
    }
}
