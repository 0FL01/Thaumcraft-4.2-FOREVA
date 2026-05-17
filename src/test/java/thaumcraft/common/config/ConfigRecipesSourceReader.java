package thaumcraft.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public final class ConfigRecipesSourceReader {

    private static final Path CONFIG_RECIPES = Paths.get("src/main/java/thaumcraft/common/config/ConfigRecipes.java");
    private static final Path CONFIG_RECIPES_SLICES = Paths.get("src/main/java/thaumcraft/common/config/recipes");

    private ConfigRecipesSourceReader() {
    }

    public static String readMergedSource() throws IOException {
        StringBuilder source = new StringBuilder(readFile(CONFIG_RECIPES));
        try (Stream<Path> paths = Files.list(CONFIG_RECIPES_SLICES)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(path -> {
                        source.append('\n');
                        try {
                            source.append(readFile(path));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
        return source.toString();
    }

    private static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
