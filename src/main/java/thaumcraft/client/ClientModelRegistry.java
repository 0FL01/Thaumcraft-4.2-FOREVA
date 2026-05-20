package thaumcraft.client;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.Thaumcraft;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID, value = Side.CLIENT)
public final class ClientModelRegistry {

    private ClientModelRegistry() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Thaumcraft.proxy.registerModelLocations();
    }
}
