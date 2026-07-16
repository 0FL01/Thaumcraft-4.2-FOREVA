package thaumcraft.common.tiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TileFocalManipulatorSoundStaticGuardTest {

    @Test
    public void focalManipulatorShouldKeepOriginalCraftLifecycleSounds() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/TileFocalManipulator.java");
        String update = between(tile, "public void update()", "public boolean startCraft(");
        String start = between(tile, "public boolean startCraft(", "private static AspectList reduceToPrimals(");

        String startSound = "this.world.playSound(null, this.pos, TCSounds.CRAFTSTART, SoundCategory.BLOCKS, 0.25F, 1.0F);";
        assertTrue("successful craft must sync before playing its start sound",
                start.indexOf(startSound) > start.indexOf("this.markDirtyAndSync();"));
        assertTrue("rejected crafts must return before the success sound",
                start.lastIndexOf("return false;") < start.indexOf(startSound));
        assertTrue("successful craft must play its sound before returning",
                start.indexOf("return true;") > start.indexOf(startSound));

        int applyUpgrade = update.indexOf("focus.applyUpgrade(");
        int upgraded = update.indexOf("upgraded = true;", applyUpgrade);
        int successSound = update.indexOf("this.world.playSound(null, this.pos, TCSounds.WAND, SoundCategory.BLOCKS, 1.0F, 1.0F);", upgraded);
        int failureSound = update.indexOf("this.world.playSound(null, this.pos, TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 0.33F, 1.0F);", upgraded);
        assertTrue("completion sound must follow a successfully applied upgrade",
                applyUpgrade >= 0 && upgraded > applyUpgrade && successSound > upgraded);
        assertTrue("an interrupted craft must use the failure branch",
                update.indexOf("if (upgraded)", upgraded) > upgraded && failureSound > successSound);

        assertEquals(1, occurrences(tile, "TCSounds.CRAFTSTART"));
        assertEquals(1, occurrences(tile, "TCSounds.WAND"));
        assertEquals(1, occurrences(tile, "TCSounds.CRAFTFAIL"));
    }

    @Test
    public void rejectedStartShouldRemainOwnedByTheContainer() throws IOException {
        String container = read("src/main/java/thaumcraft/common/container/ContainerFocalManipulator.java");

        assertTrue(container.contains("!this.table.startCraft(id, playerIn)"));
        assertTrue(container.contains("TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 0.33F, 1.0F"));
        assertEquals(0, occurrences(container, "TCSounds.CRAFTSTART"));
        assertEquals(0, occurrences(container, "TCSounds.WAND"));
    }

    private static String between(String source, String start, String end) {
        int from = source.indexOf(start);
        int to = source.indexOf(end, from);
        return source.substring(from, to);
    }

    private static int occurrences(String source, String value) {
        int count = 0;
        for (int at = 0; (at = source.indexOf(value, at)) >= 0; at += value.length()) {
            ++count;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
