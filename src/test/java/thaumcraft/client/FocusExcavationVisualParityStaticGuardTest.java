package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class FocusExcavationVisualParityStaticGuardTest {

    @Test
    public void excavationFocusShouldKeepReferenceWandColorAndChannelFx() throws IOException {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("FocusExcavation must keep the TC 4.2.3.5 dark green equipped-focus color",
                focus.contains("return 0x064006;"));
        assertTrue("FocusExcavation must keep separate client and server channel state",
                focus.contains("(player.world.isRemote ? \"R\" : \"S\") + player.getName()"));
        assertTrue("FocusExcavation must restore the original green type-2 continuous wand beam",
                focus.contains("player.world, player, tx, ty, tz, 2, 0x00FF66, false,")
                        && focus.contains("impact > 0 ? 2.0F : 0.0F")
                        && focus.contains("beam.get(key), impact"));
        assertTrue("FocusExcavation must publish client-side block damage progress and clear it when targeting stops",
                focus.contains("int progress = (int) (bc / hardness * 9.0F);")
                        && focus.contains("Thaumcraft.proxy.excavateFX(player.world, pos, player, progress);")
                        && focus.contains("Thaumcraft.proxy.excavateFX(player.world, previous, player, -1);"));
        assertTrue("The common/client proxy pair must route excavation damage to the vanilla crack overlay",
                commonProxy.contains("public void excavateFX(World world, BlockPos pos, EntityPlayer player, int progress)")
                        && clientProxy.contains("public void excavateFX(World world, BlockPos pos, EntityPlayer player, int progress)")
                        && clientProxy.contains("renderGlobal.sendBlockBreakProgress(player.getEntityId(), pos, progress)"));

        assertArrayEquals("Runtime excavation focus icon must remain identical to the TC4 source asset",
                Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/textures/items/focus_excavation.png")),
                Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/textures/items/focus_excavation.png")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
