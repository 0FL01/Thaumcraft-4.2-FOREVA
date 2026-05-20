package thaumcraft.common.lib.network.playerdata;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PacketPlayerCompleteToServerStaticGuardTest {

    @Test
    public void packetHandlerKeepsPrerequisiteAndTypeGatingGuards() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java");

        assertTrue("Missing prerequisites guard in PacketPlayerCompleteToServer",
                source.contains("if (!ResearchManager.doesPlayerHaveRequisites(player, key))"));
        assertTrue("Missing secondary/type guard in PacketPlayerCompleteToServer",
                source.contains("if (type == 0 && research.isSecondary())"));
        assertTrue("Missing primary/type guard in PacketPlayerCompleteToServer",
                source.contains("if (type == 1 && !research.isSecondary())"));
        assertTrue("Missing primary note-creation path in PacketPlayerCompleteToServer",
                source.contains("ResearchManager.createResearchNoteForPlayer(player.world, player, key)"));
        assertTrue("Missing secondary completion path in PacketPlayerCompleteToServer",
                source.contains("consumeResearchCost(player, research) && completeResearch(player, research)"));
        assertTrue("Missing dimension/username packet authority guard in PacketPlayerCompleteToServer",
                source.contains("if (player.world.provider.getDimension() != dim) return false;")
                        && source.contains("if (username != null && !username.isEmpty() && !player.getName().equals(username)) return false;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
