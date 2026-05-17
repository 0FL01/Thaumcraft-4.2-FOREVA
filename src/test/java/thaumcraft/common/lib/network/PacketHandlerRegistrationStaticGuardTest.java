package thaumcraft.common.lib.network;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacketHandlerRegistrationStaticGuardTest {

    private static final Pattern REGISTER_LINE = Pattern.compile(
            "register\\(([^,]+)\\.class,\\s*idx\\+\\+,\\s*Side\\.(CLIENT|SERVER)\\);");

    @Test
    public void packetHandlerShouldKeepReferencePacketCountAndChannel() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/PacketHandler.java");

        assertTrue("PacketHandler should keep lowercase thaumcraft channel id",
                source.contains("public static final String CHANNEL = \"thaumcraft\";"));
        assertTrue("PacketHandler should keep reference packet count contract",
                source.contains("public static final int REFERENCE_PACKET_COUNT = 39;"));
        assertTrue("PacketHandler should keep discriminator mismatch guard",
                source.contains("if (idx != REFERENCE_PACKET_COUNT) {"));
    }

    @Test
    public void packetHandlerShouldKeepReferenceRegistrationOrder() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/PacketHandler.java");

        List<String> actual = new ArrayList<>();
        Matcher matcher = REGISTER_LINE.matcher(source);
        while (matcher.find()) {
            actual.add(matcher.group(1).trim() + ":" + matcher.group(2));
        }

        List<String> expected = Arrays.asList(
                "PacketBiomeChange:CLIENT",
                "PacketConfig:CLIENT",
                "PacketMiscEvent:CLIENT",
                "PacketSyncWipe:CLIENT",
                "PacketSyncAspects:CLIENT",
                "PacketSyncResearch:CLIENT",
                "PacketSyncScannedItems:CLIENT",
                "PacketSyncScannedEntities:CLIENT",
                "PacketSyncScannedPhenomena:CLIENT",
                "PacketResearchComplete:CLIENT",
                "PacketAspectPool:CLIENT",
                "PacketAspectDiscovery:CLIENT",
                "PacketScannedToServer:SERVER",
                "PacketAspectCombinationToServer:SERVER",
                "PacketPlayerCompleteToServer:SERVER",
                "PacketAspectPlaceToServer:SERVER",
                "PacketRunicCharge:CLIENT",
                "PacketBoreDig:CLIENT",
                "PacketNote:CLIENT",
                "PacketSyncWarp:CLIENT",
                "PacketWarpMessage:CLIENT",
                "PacketNote:SERVER",
                "PacketItemKeyToServer:SERVER",
                "PacketFocusChangeToServer:SERVER",
                "PacketFlyToServer:SERVER",
                "PacketFXBlockBubble:CLIENT",
                "PacketFXBlockDig:CLIENT",
                "PacketFXBlockSparkle:CLIENT",
                "PacketFXBlockArc:CLIENT",
                "PacketFXBlockZap:CLIENT",
                "PacketFXEssentiaSource:CLIENT",
                "PacketFXInfusionSource:CLIENT",
                "PacketFXShield:CLIENT",
                "PacketFXSonic:CLIENT",
                "PacketFXWispZap:CLIENT",
                "PacketFXZap:CLIENT",
                "PacketFXVisDrain:CLIENT",
                "PacketFXBeamPulse:CLIENT",
                "PacketFXBeamPulseGolemBoss:CLIENT");

        assertEquals("PacketHandler registration sequence must stay reference-aligned",
                expected, actual);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
