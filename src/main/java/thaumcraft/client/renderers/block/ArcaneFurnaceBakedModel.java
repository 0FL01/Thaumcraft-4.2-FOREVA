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
import org.apache.commons.lang3.tuple.Pair;
import thaumcraft.common.blocks.BlockArcaneFurnace;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArcaneFurnaceBakedModel implements IBakedModel {
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private final IBakedModel delegate;
    private final int fallbackType;
    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    public ArcaneFurnaceBakedModel(IBakedModel delegate, int fallbackType) {
        this.delegate = delegate;
        this.fallbackType = Math.max(0, Math.min(10, fallbackType));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) {
            return ImmutableList.of();
        }
        int type = this.getType(state);
        int level = this.getExtendedInt(state, BlockArcaneFurnace.RENDER_LEVEL, 0);
        int nozzleSide = this.getExtendedInt(state, BlockArcaneFurnace.NOZZLE_SIDE, -1);
        EnumFacing facing = this.getFacing(state);
        String key = type + ":" + level + ":" + nozzleSide + ":" + facing.getIndex();
        return this.cache.computeIfAbsent(key, ignored -> this.buildQuads(type, level, nozzleSide, facing));
    }

    private List<BakedQuad> buildQuads(int type, int level, int nozzleSide, EnumFacing facing) {
        List<BakedQuad> quads = new ArrayList<>();
        if (type == 0) {
            TextureAtlasSprite lava = this.sprite("minecraft:blocks/lava_still");
            this.addTexturedCube(quads, 0, 0, 0, 16, 16, 16,
                    lava, lava, lava, lava, lava, lava, ModelRotation.X0_Y0, true);
        } else if (type == 10) {
            this.addNozzleQuads(quads, facing);
        } else {
            this.addTexturedCube(quads, 0, 0, 0, 16, 16, 16,
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.DOWN)),
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.UP)),
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.NORTH)),
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.SOUTH)),
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.WEST)),
                    this.sprite(this.textureForSide(type, level, nozzleSide, EnumFacing.EAST)),
                    ModelRotation.X0_Y0, true);
        }
        return ImmutableList.copyOf(quads);
    }

    private void addNozzleQuads(List<BakedQuad> quads, EnumFacing facing) {
        ModelRotation rotation = this.nozzleRotation(facing);
        this.addFace(quads, 0, 0, 12, 16, 16, 13, EnumFacing.SOUTH, this.sprite(13), rotation, true);
        this.addFace(quads, 0, 0, 13, 16, 16, 14, EnumFacing.SOUTH, this.sprite(15), rotation, true);
        this.addFace(quads, 0, 0, 14, 16, 16, 15, EnumFacing.SOUTH, this.sprite("minecraft:blocks/fire_layer_0"), rotation, false);
    }

    private int textureForSide(int meta, int level, int nozzleSide, EnumFacing face) {
        int side = face.getIndex();
        int nozzleOffset = nozzleSide == side ? 3 : 0;
        switch (face) {
            case DOWN:
            case UP:
                if (face == EnumFacing.UP && level == 18) {
                    switch (meta) {
                        case 2:
                            return 16;
                        case 4:
                            return 17;
                        case 6:
                            return 26;
                        case 8:
                            return 25;
                        default:
                            break;
                    }
                }
                if (nozzleOffset == 3) {
                    return 6;
                }
                if (meta == 5) {
                    return 10;
                }
                int index = (meta - 1) % 3 + ((meta - 1) / 3) * 9;
                return index >= 0 ? index : 7;
            case NORTH:
                switch (meta) {
                    case 1:
                        return 2 + level + nozzleOffset;
                    case 2:
                        return 1 + level + nozzleOffset;
                    case 3:
                        return level + nozzleOffset;
                    default:
                        return level != 9 ? 7 : 6;
                }
            case SOUTH:
                switch (meta) {
                    case 7:
                        return level + nozzleOffset;
                    case 8:
                        return 1 + level + nozzleOffset;
                    case 9:
                        return 2 + level + nozzleOffset;
                    default:
                        return level != 9 ? 7 : 6;
                }
            case WEST:
                switch (meta) {
                    case 1:
                        return level + nozzleOffset;
                    case 4:
                        return 1 + level + nozzleOffset;
                    case 7:
                        return 2 + level + nozzleOffset;
                    default:
                        return level != 9 ? 7 : 6;
                }
            case EAST:
                switch (meta) {
                    case 3:
                        return 2 + level + nozzleOffset;
                    case 6:
                        return 1 + level + nozzleOffset;
                    case 9:
                        return level + nozzleOffset;
                    default:
                        return level != 9 ? 7 : 6;
                }
            default:
                return nozzleOffset == 0 ? 7 : 6;
        }
    }

    private void addTexturedCube(List<BakedQuad> quads, float x1, float y1, float z1, float x2, float y2, float z2,
                                 TextureAtlasSprite down, TextureAtlasSprite up, TextureAtlasSprite north,
                                 TextureAtlasSprite south, TextureAtlasSprite west, TextureAtlasSprite east,
                                 ModelRotation rotation, boolean shade) {
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.DOWN, down, rotation, shade);
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.UP, up, rotation, shade);
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.NORTH, north, rotation, shade);
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.SOUTH, south, rotation, shade);
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.WEST, west, rotation, shade);
        this.addFace(quads, x1, y1, z1, x2, y2, z2, EnumFacing.EAST, east, rotation, shade);
    }

    private void addFace(List<BakedQuad> quads, float x1, float y1, float z1, float x2, float y2, float z2,
                         EnumFacing face, TextureAtlasSprite sprite, ModelRotation rotation, boolean shade) {
        BlockPartFace partFace = new BlockPartFace(null, -1, "", FULL_UV);
        quads.add(FACE_BAKERY.makeBakedQuad(
                new Vector3f(x1, y1, z1),
                new Vector3f(x2, y2, z2),
                partFace,
                sprite,
                face,
                rotation,
                null,
                false,
                shade));
    }

    private ModelRotation nozzleRotation(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return ModelRotation.X0_Y180;
            case EAST:
                return ModelRotation.X0_Y90;
            case WEST:
                return ModelRotation.X0_Y270;
            case SOUTH:
            default:
                return ModelRotation.X0_Y0;
        }
    }

    private TextureAtlasSprite sprite(int index) {
        return this.sprite("thaumcraft:blocks/furnace" + index);
    }

    private TextureAtlasSprite sprite(String name) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
    }

    private int getType(@Nullable IBlockState state) {
        if (state != null && state.getPropertyKeys().contains(BlockArcaneFurnace.TYPE)) {
            return state.getValue(BlockArcaneFurnace.TYPE);
        }
        return this.fallbackType;
    }

    private EnumFacing getFacing(@Nullable IBlockState state) {
        if (state != null && state.getPropertyKeys().contains(BlockArcaneFurnace.FACING)) {
            return state.getValue(BlockArcaneFurnace.FACING);
        }
        return EnumFacing.SOUTH;
    }

    private int getExtendedInt(@Nullable IBlockState state, net.minecraftforge.common.property.IUnlistedProperty<Integer> property, int fallback) {
        if (state instanceof IExtendedBlockState) {
            Integer value = ((IExtendedBlockState) state).getValue(property);
            if (value != null) {
                return value;
            }
        }
        return fallback;
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
