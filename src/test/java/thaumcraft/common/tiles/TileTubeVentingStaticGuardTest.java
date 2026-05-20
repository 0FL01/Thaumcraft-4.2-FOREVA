package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileTubeVentingStaticGuardTest {

    @Test
    public void tileTubeShouldKeepReferenceShapedVentCadenceAndClientFeedback() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileTube.java");

        assertTrue(source.contains("protected int venting = 0;"));
        assertTrue(source.contains("protected int ventColor = 0;"));
        assertTrue(source.contains("if (this.venting > 0) {"));
        assertTrue(source.contains("this.count = this.world.rand.nextInt(10);"));
        assertTrue(source.contains("if (!this.world.isRemote) {"));
        assertTrue(source.contains("if (this.venting <= 0) {"));
        assertTrue(source.contains("this.checkVenting();"));
        assertTrue(source.contains("Thaumcraft.proxy.drawVentParticles("));
        assertTrue(source.contains("int c = -1;"));
        assertTrue(source.contains("c = Config.aspectOrder.indexOf(this.suctionType);"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 1, c);"));
        assertTrue(source.contains("this.venting = 40;"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 0, 0);"));
        assertTrue(source.contains("TCSounds.CREAK"));
        assertTrue(source.contains("SoundEvents.BLOCK_FIRE_EXTINGUISH"));
        assertTrue(source.contains("this.venting = 50;"));
        assertTrue(source.contains("this.ventColor = type == -1 || type >= Config.aspectOrder.size() ? 0xAAAAAA : Config.aspectOrder.get(type).getColor();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
