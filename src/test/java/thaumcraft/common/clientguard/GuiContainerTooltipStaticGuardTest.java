package thaumcraft.common.clientguard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GuiContainerTooltipStaticGuardTest {

    private static final Path GUI_DIRECTORY = Paths.get("src/main/java/thaumcraft/client/gui");

    @Test
    public void everyContainerScreenShouldRenderHoveredItemTooltipAfterContainer() throws IOException {
        List<Path> sources = new ArrayList<Path>();
        try (Stream<Path> paths = Files.list(GUI_DIRECTORY)) {
            paths.filter(path -> path.getFileName().toString().endsWith(".java"))
                    .forEach(sources::add);
        }
        Collections.sort(sources);

        int containerScreens = 0;
        for (Path path : sources) {
            String source = read(path);
            if (!source.contains("extends GuiContainer")) continue;
            ++containerScreens;
            assertRendersHoveredTooltip(path, source);
        }

        assertEquals("guard must audit every direct GuiContainer screen", 15, containerScreens);
    }

    @Test
    public void arcaneBoreShouldKeepOriginalStatusLabelSpacing() throws IOException {
        String source = read(GUI_DIRECTORY.resolve("GuiArcaneBore.java"));
        assertTrue(source.contains("\"Width: \" +"));
        assertTrue(source.contains("\"Fortune \" +"));
    }

    private static void assertRendersHoveredTooltip(Path path, String source) {
        String drawSignature = "public void drawScreen(int mouseX, int mouseY, float partialTicks)";
        String drawContainerCall = "super.drawScreen(mouseX, mouseY, partialTicks);";
        String drawTooltipCall = "this.renderHoveredToolTip(mouseX, mouseY);";
        int drawScreen = source.indexOf(drawSignature);
        int drawContainer = source.indexOf(drawContainerCall, drawScreen);
        int drawTooltip = source.indexOf(drawTooltipCall, drawContainer);

        assertTrue(path + " must override drawScreen", drawScreen >= 0);
        assertTrue(path + " must render the container before its tooltip", drawContainer > drawScreen);
        assertTrue(path + " must render the hovered item tooltip after the container", drawTooltip > drawContainer);
        assertEquals(path + " must render the hovered item tooltip exactly once",
                1, countOccurrences(source, drawTooltipCall));
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            ++count;
            index += needle.length();
        }
        return count;
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
