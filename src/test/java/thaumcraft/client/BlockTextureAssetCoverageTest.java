package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BlockTextureAssetCoverageTest {

    @Test
    public void everyReferenceBlockTextureAssetIsPresentInPortResources() throws IOException {
        Path sourceRoot = Paths.get("thaumcraft_src/assets/thaumcraft/textures/blocks");
        Path portRoot = Paths.get("src/main/resources/assets/thaumcraft/textures/blocks");
        List<String> missing = new ArrayList<>();

        Files.walk(sourceRoot)
                .filter(Files::isRegularFile)
                .forEach(source -> {
                    Path relative = sourceRoot.relativize(source);
                    if (!Files.exists(portRoot.resolve(relative))) {
                        missing.add(relative.toString());
                    }
                });

        assertTrue("Missing thaumcraft block texture assets from original corpus: " + missing, missing.isEmpty());
    }
}
