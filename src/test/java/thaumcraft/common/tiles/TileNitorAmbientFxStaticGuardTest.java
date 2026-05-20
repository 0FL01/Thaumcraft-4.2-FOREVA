package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileNitorAmbientFxStaticGuardTest {

    @Test
    public void tileNitorShouldKeepClientAmbientWispCadenceAndTranslucentPassContract() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileNitor.java");

        assertTrue("TileNitor should be a ticking tile again so the ambient nitor wisps can run client-side",
                source.contains("public class TileNitor extends TileEntity implements ITickable")
                        && source.contains("public void update()"));
        assertTrue("TileNitor update must remain client-only and use the reference dual-cadence wispFX3 path",
                source.contains("if (this.world == null || !this.world.isRemote)")
                        && source.contains("this.world.rand.nextInt(9 - Thaumcraft.proxy.particleCount(2)) == 0")
                        && source.contains("this.world.rand.nextInt(15 - Thaumcraft.proxy.particleCount(4)) == 0")
                        && source.contains("Thaumcraft.proxy.wispFX3(")
                        && source.contains("0.5f,")
                        && source.contains("0.25f,")
                        && source.contains("-0.025f")
                        && source.contains("-0.02f"));
        assertTrue("TileNitor should keep its translucent second-pass render contract",
                source.contains("public boolean shouldRenderInPass(int pass)")
                        && source.contains("return pass == 1;"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
