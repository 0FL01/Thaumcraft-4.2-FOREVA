package thaumcraft.client;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.client.renderers.item.CrystalPerspectiveModel;
import thaumcraft.client.renderers.item.ThaumometerPerspectiveModel;
import thaumcraft.client.renderers.item.TrunkSpawnerPerspectiveModel;
import thaumcraft.common.Thaumcraft;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Side.CLIENT)
public final class ClientModelRegistry {

    static final ModelResourceLocation THAUMOMETER_MODEL =
            new ModelResourceLocation("thaumcraft:itemthaumometer_tesr", "inventory");
    static final ModelResourceLocation TRUNKSPAWNER_MODEL =
            new ModelResourceLocation("thaumcraft:trunkspawner_tesr", "inventory");
    static final ModelResourceLocation BLOCKCRYSTAL_MODEL =
            new ModelResourceLocation("thaumcraft:blockcrystal_tesr", "inventory");

    private ClientModelRegistry() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Thaumcraft.proxy.registerModelLocations();
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
    }
}
