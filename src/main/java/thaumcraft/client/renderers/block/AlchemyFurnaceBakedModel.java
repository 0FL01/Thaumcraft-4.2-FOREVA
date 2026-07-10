package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableList;
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
import thaumcraft.common.blocks.BlockStoneDevice;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AlchemyFurnaceBakedModel implements IBakedModel {
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private final IBakedModel delegate;
    private final Map<String, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    public AlchemyFurnaceBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) {
            return ImmutableList.of();
        }
        boolean item = state == null;
        boolean filled = this.getExtendedBoolean(state, BlockStoneDevice.FILLED);
        boolean burning = this.getExtendedBoolean(state, BlockStoneDevice.BURNING);
        String key = item + ":" + filled + ":" + burning;
        return this.cache.computeIfAbsent(key, ignored -> this.buildQuads(item, filled, burning));
    }

    private List<BakedQuad> buildQuads(boolean item, boolean filled, boolean burning) {
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
        TextureAtlasSprite top = this.sprite(item || !filled
                ? "thaumcraft:blocks/al_furnace_top"
                : "thaumcraft:blocks/al_furnace_top_filled");
        TextureAtlasSprite lateral = this.sprite(!item && burning
                ? "thaumcraft:blocks/al_furnace_front_on"
                : "thaumcraft:blocks/al_furnace_front_off");

        this.addFace(quads, EnumFacing.DOWN, this.sprite(item
                ? "thaumcraft:blocks/al_furnace_top"
                : "thaumcraft:blocks/al_furnace_side"));
        this.addFace(quads, EnumFacing.UP, top);
        this.addFace(quads, EnumFacing.NORTH, lateral);
        this.addFace(quads, EnumFacing.SOUTH, lateral);
        this.addFace(quads, EnumFacing.WEST, lateral);
        this.addFace(quads, EnumFacing.EAST, lateral);
        return quads.build();
    }

    private void addFace(ImmutableList.Builder<BakedQuad> quads, EnumFacing face, TextureAtlasSprite sprite) {
        BlockPartFace partFace = new BlockPartFace(null, -1, "", FULL_UV);
        quads.add(FACE_BAKERY.makeBakedQuad(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(16.0F, 16.0F, 16.0F),
                partFace,
                sprite,
                face,
                ModelRotation.X0_Y0,
                null,
                false,
                true));
    }

    private boolean getExtendedBoolean(@Nullable IBlockState state, IUnlistedProperty<Boolean> property) {
        if (state instanceof IExtendedBlockState) {
            Boolean value = ((IExtendedBlockState) state).getValue(property);
            return value != null && value;
        }
        return false;
    }

    private TextureAtlasSprite sprite(String name) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
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
        return this.sprite("thaumcraft:blocks/al_furnace_side");
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
