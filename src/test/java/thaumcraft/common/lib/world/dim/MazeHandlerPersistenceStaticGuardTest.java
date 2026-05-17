package thaumcraft.common.lib.world.dim;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class MazeHandlerPersistenceStaticGuardTest {

    @Test
    public void labyrinthPersistenceUsesScopedStreamsAndFallbackWritePath() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java");

        assertTrue("Maze load path must scope primary labyrinth stream with try-with-resources",
                source.contains("try (FileInputStream stream = new FileInputStream(file1))"));
        assertTrue("Maze load path must scope backup labyrinth stream with try-with-resources",
                source.contains("try (FileInputStream stream = new FileInputStream(file2))"));
        assertTrue("Maze save path must scope temporary write stream with try-with-resources",
                source.contains("try (FileOutputStream stream = new FileOutputStream(fileNew))"));
        assertTrue("Maze save path must keep direct-write fallback when rename fails",
                source.contains("if (!fileNew.renameTo(fileCur))")
                        && source.contains("new FileOutputStream(fileCur)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
