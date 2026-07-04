package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class ClientFxReferenceClassSurfaceStaticGuardTest {

    @Test
    public void clientFxClassSurfaceMatchesReferenceSetWithKnownAllowlist() throws IOException {
        Set<String> expected = new HashSet<>(Arrays.asList(
                "thaumcraft/client/fx/ParticleEngine.java",
                "thaumcraft/client/fx/WRMat4.java",
                "thaumcraft/client/fx/WRVector3.java",
                "thaumcraft/client/fx/beams/FXArc.java",
                "thaumcraft/client/fx/beams/FXBeam.java",
                "thaumcraft/client/fx/beams/FXBeamBore.java",
                "thaumcraft/client/fx/beams/FXBeamGolemBoss.java",
                "thaumcraft/client/fx/beams/FXBeamPower.java",
                "thaumcraft/client/fx/beams/FXBeamWand.java",
                "thaumcraft/client/fx/bolt/FXLightningBolt.java",
                "thaumcraft/client/fx/bolt/FXLightningBoltCommon.java",
                "thaumcraft/client/fx/other/FXBlockWard.java",
                "thaumcraft/client/fx/other/FXShieldRunes.java",
                "thaumcraft/client/fx/other/FXSonic.java",
                "thaumcraft/client/fx/particles/FXBlockRunes.java",
                "thaumcraft/client/fx/particles/FXBoreParticles.java",
                "thaumcraft/client/fx/particles/FXBoreSparkle.java",
                "thaumcraft/client/fx/particles/FXBreaking.java",
                "thaumcraft/client/fx/particles/FXBubble.java",
                "thaumcraft/client/fx/particles/FXBubbleAlt.java",
                "thaumcraft/client/fx/particles/FXBurst.java",
                "thaumcraft/client/fx/particles/FXDrop.java",
                "thaumcraft/client/fx/particles/FXEssentiaTrail.java",
                "thaumcraft/client/fx/particles/FXGeneric.java",
                "thaumcraft/client/fx/particles/FXScorch.java",
                "thaumcraft/client/fx/particles/FXSlimyBubble.java",
                "thaumcraft/client/fx/particles/FXSmokeSpiral.java",
                "thaumcraft/client/fx/particles/FXSmokeTrail.java",
                "thaumcraft/client/fx/particles/FXSpark.java",
                "thaumcraft/client/fx/particles/FXSparkle.java",
                "thaumcraft/client/fx/particles/FXSparkleTrail.java",
                "thaumcraft/client/fx/particles/FXSwarm.java",
                "thaumcraft/client/fx/particles/FXVent.java",
                "thaumcraft/client/fx/particles/FXVisSparkle.java",
                "thaumcraft/client/fx/particles/FXWisp.java",
                "thaumcraft/client/fx/particles/FXWispArcing.java",
                "thaumcraft/client/fx/particles/FXWispEG.java"
        ));

        Set<String> allowlistedExtra = new HashSet<>(Arrays.asList(
                "thaumcraft/client/fx/ITCParticle.java",
                "thaumcraft/client/fx/particles/FXSmokeDrift.java"
        ));

        Path sourceRoot = Paths.get("src/main/java");
        Path root = sourceRoot.resolve("thaumcraft/client/fx");
        Set<String> current;
        try (Stream<Path> stream = Files.walk(root)) {
            current = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(path -> sourceRoot.relativize(path).toString().replace('\\', '/'))
                    .collect(Collectors.toSet());
        }

        Set<String> missing = new HashSet<>(expected);
        missing.removeAll(current);
        assertTrue("Missing reference FX classes: " + missing, missing.isEmpty());

        Set<String> unexpected = new HashSet<>(current);
        unexpected.removeAll(expected);
        unexpected.removeAll(allowlistedExtra);
        assertTrue("Unexpected extra FX classes outside allowlist: " + unexpected, unexpected.isEmpty());
    }
}
