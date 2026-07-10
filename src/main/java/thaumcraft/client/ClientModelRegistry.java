package thaumcraft.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.client.renderers.block.ArcaneFurnaceBakedModel;
import thaumcraft.client.renderers.block.AlchemyFurnaceBakedModel;
import thaumcraft.client.renderers.item.CrystalPerspectiveModel;
import thaumcraft.client.renderers.item.ThaumometerPerspectiveModel;
import thaumcraft.client.renderers.item.TrunkSpawnerPerspectiveModel;
import thaumcraft.client.renderers.item.WandPerspectiveModel;
import thaumcraft.client.renderers.item.WoodenDevicePerspectiveModel;
import thaumcraft.common.Thaumcraft;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Side.CLIENT)
public final class ClientModelRegistry {

    static final ModelResourceLocation THAUMOMETER_MODEL =
            new ModelResourceLocation("thaumcraft:itemthaumometer_tesr", "inventory");
    static final ModelResourceLocation TRUNKSPAWNER_MODEL =
            new ModelResourceLocation("thaumcraft:trunkspawner_tesr", "inventory");
    static final ModelResourceLocation BLOCKCRYSTAL_MODEL =
            new ModelResourceLocation("thaumcraft:blockcrystal_tesr", "inventory");
    static final ModelResourceLocation WANDCASTING_MODEL =
            new ModelResourceLocation("thaumcraft:wandcasting_tesr", "inventory");
    static final ModelResourceLocation BLOCKWOODENDEVICE_BANNER_MODEL =
            new ModelResourceLocation("thaumcraft:blockwoodendevice_banner_tesr", "inventory");
    static final ResourceLocation FOCUS_PECH_DEPTH_SPRITE =
            new ResourceLocation("thaumcraft", "items/focus_pech_depth");
    static final ResourceLocation FROST_SHARD_SPRITE =
            new ResourceLocation("thaumcraft", "blocks/frostshard");
    static final ResourceLocation PIPE_VALVE_SPRITE =
            new ResourceLocation("thaumcraft", "blocks/pipe_valve");
    static final ResourceLocation ADVANCED_FURNACE_FLUXGOO_SPRITE =
            new ResourceLocation("thaumcraft", "blocks/fluxgoo");
    static final ResourceLocation ADVANCED_FURNACE_METALBASE_SPRITE =
            new ResourceLocation("thaumcraft", "blocks/metalbase");
    private static final ResourceLocation FROST_SHARD_OBJ =
            new ResourceLocation("thaumcraft", "textures/models/orb.obj");
    private static IBakedModel frostShardModel;
    private static final int[] ARCANE_FURNACE_TEXTURES = {
            0, 1, 2, 3, 4, 5, 6, 7,
            9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
            19, 20, 21, 22, 23, 25, 26
    };

    private ClientModelRegistry() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Thaumcraft.proxy.registerModelLocations();
    }

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(FOCUS_PECH_DEPTH_SPRITE);
        event.getMap().registerSprite(FROST_SHARD_SPRITE);
        event.getMap().registerSprite(PIPE_VALVE_SPRITE);
        event.getMap().registerSprite(ADVANCED_FURNACE_FLUXGOO_SPRITE);
        event.getMap().registerSprite(ADVANCED_FURNACE_METALBASE_SPRITE);
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/al_furnace_top_filled"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/al_furnace_front_on"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/lamp_grow_top_off"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/lamp_grow_side_off"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/lamp_fert_top_off"));
        event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/lamp_fert_side_off"));
        for (int texture : ARCANE_FURNACE_TEXTURES) {
            event.getMap().registerSprite(new ResourceLocation("thaumcraft", "blocks/furnace" + texture));
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        IBakedModel model = event.getModelRegistry().getObject(THAUMOMETER_MODEL);
        if (model != null) {
            event.getModelRegistry().putObject(THAUMOMETER_MODEL, new ThaumometerPerspectiveModel(model));
        }
        model = event.getModelRegistry().getObject(TRUNKSPAWNER_MODEL);
        if (model != null) {
            event.getModelRegistry().putObject(TRUNKSPAWNER_MODEL, new TrunkSpawnerPerspectiveModel(model));
        }
        model = event.getModelRegistry().getObject(BLOCKCRYSTAL_MODEL);
        if (model != null) {
            event.getModelRegistry().putObject(BLOCKCRYSTAL_MODEL, new CrystalPerspectiveModel(model));
        }
        model = event.getModelRegistry().getObject(WANDCASTING_MODEL);
        if (model != null) {
            event.getModelRegistry().putObject(WANDCASTING_MODEL, new WandPerspectiveModel(model));
        }
        model = event.getModelRegistry().getObject(BLOCKWOODENDEVICE_BANNER_MODEL);
        if (model != null) {
            event.getModelRegistry().putObject(BLOCKWOODENDEVICE_BANNER_MODEL, new WoodenDevicePerspectiveModel(model));
        }
        bakeFrostShardModel(event);
        replaceAlchemyFurnaceModel(event);
        replaceArcaneFurnaceModels(event);
    }

    public static IBakedModel getFrostShardModel() {
        return frostShardModel;
    }

    private static void bakeFrostShardModel(ModelBakeEvent event) {
        frostShardModel = null;
        try {
            IModel model = OBJLoader.INSTANCE.loadModel(FROST_SHARD_OBJ).retexture(ImmutableMap.of(
                    "#OBJModel.Default.Texture.Name", FROST_SHARD_SPRITE.toString()));
            frostShardModel = model.bake(
                    TRSRTransformation.identity(),
                    DefaultVertexFormats.ITEM,
                    location -> event.getModelManager().getTextureMap().getAtlasSprite(location.toString()));
        } catch (Exception e) {
            Thaumcraft.log.error("Unable to bake frost shard model {}", FROST_SHARD_OBJ, e);
        }
    }

    private static void replaceAlchemyFurnaceModel(ModelBakeEvent event) {
        ModelResourceLocation location = new ModelResourceLocation("thaumcraft:blockstonedevice", "type=0");
        IBakedModel delegate = event.getModelRegistry().getObject(location);
        if (delegate != null) {
            event.getModelRegistry().putObject(location, new AlchemyFurnaceBakedModel(delegate));
        }
    }

    private static void replaceArcaneFurnaceModels(ModelBakeEvent event) {
        IBakedModel fallback = null;
        for (int meta = 0; meta <= 10; meta++) {
            for (String facing : new String[]{"north", "east", "south", "west"}) {
                ModelResourceLocation location = new ModelResourceLocation("thaumcraft:blockarcanefurnace", "facing=" + facing + ",type=" + meta);
                IBakedModel model = event.getModelRegistry().getObject(location);
                if (model != null) {
                    fallback = model;
                    break;
                }
            }
            if (fallback != null) {
                break;
            }
        }
        if (fallback == null) {
            return;
        }
        for (int meta = 0; meta <= 10; meta++) {
            for (String facing : new String[]{"north", "east", "south", "west"}) {
                replaceArcaneFurnaceModel(event, "facing=" + facing + ",type=" + meta, meta, fallback);
                replaceArcaneFurnaceModel(event, "type=" + meta + ",facing=" + facing, meta, fallback);
            }
        }
    }

    private static void replaceArcaneFurnaceModel(ModelBakeEvent event, String variant, int meta, IBakedModel fallback) {
        ModelResourceLocation location = new ModelResourceLocation("thaumcraft:blockarcanefurnace", variant);
        IBakedModel delegate = event.getModelRegistry().getObject(location);
        if (delegate == null) {
            delegate = fallback;
        }
        event.getModelRegistry().putObject(location, new ArcaneFurnaceBakedModel(delegate, meta));
    }
}
