package thaumcraft.common.items.relics;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemResonatorStaticGuardTest {

    @Test
    public void resonatorKeepsReferenceFaceRetraceAndUntypedFallback() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/relics/ItemResonator.java");

        assertTrue("ItemResonator must keep RayTracer face retrace for sub-hit diagnostics",
                source.contains("RayTracer.retraceBlock(world, player, pos.getX(), pos.getY(), pos.getZ())")
                        && source.contains("hit.subHit >= 0 && hit.subHit < 6")
                        && source.contains("face = EnumFacing.byIndex(hit.subHit);"));
        assertTrue("ItemResonator must keep tc.resonator3 untyped suction fallback",
                source.contains("new TextComponentTranslation(\"tc.resonator3\").getFormattedText()")
                        && source.contains("new TextComponentTranslation(\"tc.resonator2\", transport.getSuctionAmount(face), suctionName)"));
        assertTrue("ItemResonator client-side use path must swing and return PASS (reference-shaped super fallback)",
                source.contains("if (world.isRemote) {")
                        && source.contains("player.swingArm(hand);")
                        && source.contains("return EnumActionResult.PASS;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
