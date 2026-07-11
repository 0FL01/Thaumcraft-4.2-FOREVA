package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileWardedSyncStaticGuardTest {

    @Test
    public void clientUpdatesRefreshFacadeAndConnectedTextureCache() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileWarded.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(tile.contains("public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)")
                && tile.contains("public void handleUpdateTag(NBTTagCompound tag)")
                && tile.contains("this.world.markBlockRangeForRenderUpdate(this.pos, this.pos)")
                && tile.contains("Thaumcraft.proxy.refreshWardedBlockRender(this.world, this.pos)"));
        assertTrue(commonProxy.contains("public void refreshWardedBlockRender(World world, BlockPos pos)"));
        assertTrue(clientProxy.contains("TileWardedRenderer.invalidate(world, pos)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
