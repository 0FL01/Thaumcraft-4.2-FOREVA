package thaumcraft.common.lib.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import thaumcraft.common.config.Config;

public class EventHandlerNetwork {
    @SubscribeEvent
    public void clientLogsOut(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        restoreClientConfig();
    }

    static void restoreClientConfig() {
        Config.allowCheatSheet = Config.CallowCheatSheet;
        Config.wardedStone = Config.CwardedStone;
        Config.allowMirrors = Config.CallowMirrors;
        Config.hardNode = Config.ChardNode;
        Config.wuss = Config.Cwuss;
        Config.researchDifficulty = Config.CresearchDifficulty;
        Config.aspectTotalCap = Config.CaspectTotalCap;
    }
}
