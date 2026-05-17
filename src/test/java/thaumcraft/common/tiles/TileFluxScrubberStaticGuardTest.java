package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileFluxScrubberStaticGuardTest {

    @Test
    public void tileFluxScrubberShouldKeepServerFluxCleanupAndChargeFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileFluxScrubber.java");

        assertTrue(source.contains("public class TileFluxScrubber extends TileThaumcraft implements ITickable, IEssentiaTransport"));
        assertTrue(source.contains("public int essentia = 0;"));
        assertTrue(source.contains("public int charges = 0;"));
        assertTrue(source.contains("public int power = 0;"));
        assertTrue(source.contains("if (this.charges >= 4)"));
        assertTrue(source.contains("this.charges -= 4;"));
        assertTrue(source.contains("if (this.world.rand.nextInt(4) == 0)"));
        assertTrue(source.contains("this.power += VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.AIR, 10);"));
        assertTrue(source.contains("if (this.power >= 5)"));
        assertTrue(source.contains("this.checkFlux();"));
        assertTrue(source.contains("Collections.shuffle(this.checklist, this.world.rand);"));
        assertTrue(source.contains("for (int cc = 0; cc < 16 && this.checklist.size() > 0; ++cc)"));
        assertTrue(source.contains("this.world.setBlockState(target, state.getBlock().getStateFromMeta(lmd - 1), 3);"));
        assertTrue(source.contains("this.world.setBlockToAir(target);"));
        assertTrue(source.contains("PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockSparkle(x, y, z, 0xDD00FF),"));
        assertTrue(source.contains("++this.charges;"));
    }

    @Test
    public void tileFluxScrubberShouldKeepEssentiaTransportContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileFluxScrubber.java");

        assertTrue(source.contains("this.facing = EnumFacing.byIndex(nbt.getInteger(\"facing\"));"));
        assertTrue(source.contains("nbt.setInteger(\"facing\", this.facing.getIndex());"));
        assertTrue(source.contains("nbt.setInteger(\"charges\", this.charges);"));
        assertTrue(source.contains("nbt.setInteger(\"power\", this.power);"));
        assertTrue(source.contains("nbt.setInteger(\"essentia\", this.essentia);"));
        assertTrue(source.contains("return face == this.facing;"));
        assertTrue(source.contains("return Aspect.MAGIC;"));
        assertTrue(source.contains("int re = Math.min(this.essentia, amount);"));
        assertTrue(source.contains("this.essentia -= re;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
