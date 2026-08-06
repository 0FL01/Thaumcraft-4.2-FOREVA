package thaumcraft.common.lib.network;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import thaumcraft.common.config.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventHandlerNetworkContractTest {
    private boolean allowCheatSheet;
    private boolean wardedStone;
    private boolean allowMirrors;
    private boolean hardNode;
    private boolean wuss;
    private int researchDifficulty;
    private int aspectTotalCap;
    private boolean clientAllowCheatSheet;
    private boolean clientWardedStone;
    private boolean clientAllowMirrors;
    private boolean clientHardNode;
    private boolean clientWuss;
    private int clientResearchDifficulty;
    private int clientAspectTotalCap;

    @Before
    public void rememberWorkingConfig() {
        this.allowCheatSheet = Config.allowCheatSheet;
        this.wardedStone = Config.wardedStone;
        this.allowMirrors = Config.allowMirrors;
        this.hardNode = Config.hardNode;
        this.wuss = Config.wuss;
        this.researchDifficulty = Config.researchDifficulty;
        this.aspectTotalCap = Config.aspectTotalCap;
        this.clientAllowCheatSheet = Config.CallowCheatSheet;
        this.clientWardedStone = Config.CwardedStone;
        this.clientAllowMirrors = Config.CallowMirrors;
        this.clientHardNode = Config.ChardNode;
        this.clientWuss = Config.Cwuss;
        this.clientResearchDifficulty = Config.CresearchDifficulty;
        this.clientAspectTotalCap = Config.CaspectTotalCap;
    }

    @After
    public void restoreWorkingConfig() {
        Config.allowCheatSheet = this.allowCheatSheet;
        Config.wardedStone = this.wardedStone;
        Config.allowMirrors = this.allowMirrors;
        Config.hardNode = this.hardNode;
        Config.wuss = this.wuss;
        Config.researchDifficulty = this.researchDifficulty;
        Config.aspectTotalCap = this.aspectTotalCap;
        Config.CallowCheatSheet = this.clientAllowCheatSheet;
        Config.CwardedStone = this.clientWardedStone;
        Config.CallowMirrors = this.clientAllowMirrors;
        Config.ChardNode = this.clientHardNode;
        Config.Cwuss = this.clientWuss;
        Config.CresearchDifficulty = this.clientResearchDifficulty;
        Config.CaspectTotalCap = this.clientAspectTotalCap;
    }

    @Test
    public void disconnectRestoresEveryClientLocalConfigCopy() {
        Config.CallowCheatSheet = false;
        Config.CwardedStone = true;
        Config.CallowMirrors = false;
        Config.ChardNode = true;
        Config.Cwuss = false;
        Config.CresearchDifficulty = -19;
        Config.CaspectTotalCap = 876;
        Config.allowCheatSheet = true;
        Config.wardedStone = false;
        Config.allowMirrors = true;
        Config.hardNode = false;
        Config.wuss = true;
        Config.researchDifficulty = 1;
        Config.aspectTotalCap = 2;

        EventHandlerNetwork.restoreClientConfig();

        assertFalse(Config.allowCheatSheet);
        assertTrue(Config.wardedStone);
        assertFalse(Config.allowMirrors);
        assertTrue(Config.hardNode);
        assertFalse(Config.wuss);
        assertEquals(-19, Config.researchDifficulty);
        assertEquals(876, Config.aspectTotalCap);
    }

    @Test
    public void lifecycleUsesExistingLoginSyncAndClientFmlBus() throws IOException {
        String entityHandler = read("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String networkHandler = read("src/main/java/thaumcraft/common/lib/network/EventHandlerNetwork.java");

        assertTrue(entityHandler.contains("PacketHandler.INSTANCE.sendTo(new PacketConfig(), (EntityPlayerMP) player);"));
        assertTrue(clientProxy.contains("FMLCommonHandler.instance().bus().register(new EventHandlerNetwork());"));
        assertTrue(networkHandler.contains("FMLNetworkEvent.ClientDisconnectionFromServerEvent"));
        assertTrue(networkHandler.contains("restoreClientConfig();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
