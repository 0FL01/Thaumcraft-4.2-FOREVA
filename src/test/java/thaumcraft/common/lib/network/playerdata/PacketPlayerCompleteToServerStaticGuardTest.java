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
                source.contains("if (!ResearchManager.doesPlayerHaveRequisites(player.getName(), this.key))"));
        assertTrue("Missing secondary/type guard in PacketPlayerCompleteToServer",
                source.contains("if (this.type == 0 && research.isSecondary())"));
        assertTrue("Missing primary/type guard in PacketPlayerCompleteToServer",
                source.contains("} else if (this.type == 1 && !research.isSecondary())"));
        assertTrue("Missing primary note-creation path in PacketPlayerCompleteToServer",
                source.contains("ResearchManager.createResearchNoteForPlayer(player.world, player, this.key)"));
        assertTrue("Missing secondary completion path in PacketPlayerCompleteToServer",
                source.contains("consumeResearchCost(player, research) && completeResearch(player, research)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
